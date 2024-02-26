// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.util.BlockUtils;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import net.wurstclient.util.ChatUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import net.minecraft.util.math.Vec3i;
import java.util.Collection;
import java.util.Iterator;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.util.RenderUtils;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.block.Block;
import net.wurstclient.util.RotationUtils;
import net.minecraft.network.Packet;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.wurstclient.compatibility.WMinecraft;
import org.lwjgl.opengl.GL11;
import net.wurstclient.util.MinPriorityThreadFactory;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import java.util.Collections;
import java.text.DecimalFormat;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import java.util.HashSet;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ExecutorService;
import java.util.Set;
import net.minecraft.world.chunk.Chunk;
import java.util.HashMap;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.BlockSetting;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.PacketInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

public final class SearchMod extends Hack implements UpdateListener, PacketInputListener, RenderListener
{
    private final BlockSetting block;
    private final EnumSetting<Area> area;
    private final SliderSetting limit;
    private int prevLimit;
    private boolean notify;
    private final HashMap<Chunk, ChunkSearcher> searchers;
    private final Set<Chunk> chunksToUpdate;
    private ExecutorService pool1;
    private ForkJoinPool pool2;
    private ForkJoinTask<HashSet<BlockPos>> getMatchingBlocksTask;
    private ForkJoinTask<ArrayList<int[]>> compileVerticesTask;
    private int displayList;
    private boolean displayListUpToDate;
    
    public SearchMod() {
        super("Search", "Helps you to find specific blocks by\nhighlighting them in rainbow color.");
        this.block = new BlockSetting("Block", "The type of block to search for.", "minecraft:diamond_ore");
        this.area = new EnumSetting<Area>("Area", "The area around the player to search in.\nHigher values require a faster computer.", Area.values(), Area.D11);
        this.limit = new SliderSetting("Limit", "The maximum number of blocks to display.\nHigher values require a faster computer.", 4.0, 3.0, 6.0, 1.0, v -> new DecimalFormat("##,###,###").format(Math.pow(10.0, v)));
        this.searchers = new HashMap<Chunk, ChunkSearcher>();
        this.chunksToUpdate = Collections.synchronizedSet(new HashSet<Chunk>());
        this.setCategory(Category.RENDER);
        this.addSetting(this.block);
        this.addSetting(this.area);
        this.addSetting(this.limit);
    }
    
    @Override
    public String getRenderName() {
        return String.valueOf(this.getName()) + " [" + this.block.getBlockName().replace("minecraft:", "") + "]";
    }
    
    @Override
    public void onEnable() {
        this.prevLimit = this.limit.getValueI();
        this.notify = true;
        this.pool1 = MinPriorityThreadFactory.newFixedThreadPool();
        this.pool2 = new ForkJoinPool();
        this.displayList = GL11.glGenLists(1);
        this.displayListUpToDate = false;
        SearchMod.EVENTS.add(UpdateListener.class, this);
        SearchMod.EVENTS.add(PacketInputListener.class, this);
        SearchMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        SearchMod.EVENTS.remove(UpdateListener.class, this);
        SearchMod.EVENTS.remove(PacketInputListener.class, this);
        SearchMod.EVENTS.remove(RenderListener.class, this);
        this.stopPool2Tasks();
        this.pool1.shutdownNow();
        this.pool2.shutdownNow();
        GL11.glDeleteLists(this.displayList, 1);
        this.chunksToUpdate.clear();
    }
    
    @Override
    public void onReceivedPacket(final PacketInputEvent event) {
        final EntityPlayerSP player = SearchMod.MC.player;
        final WorldClient world = WMinecraft.getWorld();
        if (player == null || world == null) {
            return;
        }
        final Packet packet = event.getPacket();
        Chunk chunk;
        if (packet instanceof SPacketBlockChange) {
            final SPacketBlockChange change = (SPacketBlockChange)packet;
            final BlockPos pos = change.getBlockPosition();
            chunk = world.getChunkFromBlockCoords(pos);
        }
        else if (packet instanceof SPacketMultiBlockChange) {
            final SPacketMultiBlockChange change2 = (SPacketMultiBlockChange)packet;
            final SPacketMultiBlockChange.BlockUpdateData[] changedBlocks = change2.getChangedBlocks();
            if (changedBlocks.length == 0) {
                return;
            }
            final BlockPos pos2 = changedBlocks[0].getPos();
            chunk = world.getChunkFromBlockCoords(pos2);
        }
        else {
            if (!(packet instanceof SPacketChunkData)) {
                return;
            }
            final SPacketChunkData chunkData = (SPacketChunkData)packet;
            chunk = world.getChunkFromChunkCoords(chunkData.getChunkX(), chunkData.getChunkZ());
        }
        this.chunksToUpdate.add(chunk);
    }
    
    @Override
    public void onUpdate() {
        final Block currentBlock = this.block.getBlock();
        final BlockPos eyesPos = new BlockPos(RotationUtils.getEyesPos());
        final ChunkPos center = this.getPlayerChunkPos(eyesPos);
        final int range = this.area.getSelected().chunkRange;
        final int dimensionId = SearchMod.MC.player.dimension;
        this.addSearchersInRange(center, range, currentBlock, dimensionId);
        this.removeSearchersOutOfRange(center, range);
        this.replaceSearchersWithDifferences(currentBlock, dimensionId);
        this.replaceSearchersWithChunkUpdate(currentBlock, dimensionId);
        if (!this.areAllChunkSearchersDone()) {
            return;
        }
        this.checkIfLimitChanged();
        if (this.getMatchingBlocksTask == null) {
            this.startGetMatchingBlocksTask(eyesPos);
        }
        if (!this.getMatchingBlocksTask.isDone()) {
            return;
        }
        if (this.compileVerticesTask == null) {
            this.startCompileVerticesTask();
        }
        if (!this.compileVerticesTask.isDone()) {
            return;
        }
        if (!this.displayListUpToDate) {
            this.setDisplayListFromTask();
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        RenderUtils.applyRenderOffset();
        final float x = System.currentTimeMillis() % 2000L / 1000.0f;
        final float red = 0.5f + 0.5f * WMath.sin(x * 3.1415927f);
        final float green = 0.5f + 0.5f * WMath.sin((x + 1.3333334f) * 3.1415927f);
        final float blue = 0.5f + 0.5f * WMath.sin((x + 2.6666667f) * 3.1415927f);
        GL11.glColor4f(red, green, blue, 0.5f);
        GL11.glBegin(7);
        GL11.glCallList(this.displayList);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    private ChunkPos getPlayerChunkPos(final BlockPos eyesPos) {
        final int chunkX = eyesPos.getX() >> 4;
        final int chunkZ = eyesPos.getZ() >> 4;
        return WMinecraft.getWorld().getChunkFromChunkCoords(chunkX, chunkZ).getChunkCoordIntPair();
    }
    
    private void addSearchersInRange(final ChunkPos center, final int chunkRange, final Block block, final int dimensionId) {
        final ArrayList<Chunk> chunksInRange = this.getChunksInRange(center, chunkRange);
        for (final Chunk chunk : chunksInRange) {
            if (this.searchers.containsKey(chunk)) {
                continue;
            }
            this.addSearcher(chunk, block, dimensionId);
        }
    }
    
    private ArrayList<Chunk> getChunksInRange(final ChunkPos center, final int chunkRange) {
        final ArrayList<Chunk> chunksInRange = new ArrayList<Chunk>();
        for (int x = center.chunkXPos - chunkRange; x <= center.chunkXPos + chunkRange; ++x) {
            for (int z = center.chunkZPos - chunkRange; z <= center.chunkZPos + chunkRange; ++z) {
                chunksInRange.add(WMinecraft.getWorld().getChunkFromChunkCoords(x, z));
            }
        }
        return chunksInRange;
    }
    
    private void removeSearchersOutOfRange(final ChunkPos center, final int chunkRange) {
        for (final ChunkSearcher searcher : new ArrayList(this.searchers.values())) {
            if (Math.abs(searcher.chunk.getChunkCoordIntPair().chunkXPos - center.chunkXPos) <= chunkRange && Math.abs(searcher.chunk.getChunkCoordIntPair().chunkZPos - center.chunkZPos) <= chunkRange) {
                continue;
            }
            this.removeSearcher(searcher);
        }
    }
    
    private void replaceSearchersWithDifferences(final Block currentBlock, final int dimensionId) {
        for (final ChunkSearcher oldSearcher : new ArrayList(this.searchers.values())) {
            if (currentBlock.equals(oldSearcher.block) && dimensionId == oldSearcher.dimensionId) {
                continue;
            }
            this.removeSearcher(oldSearcher);
            this.addSearcher(oldSearcher.chunk, currentBlock, dimensionId);
        }
    }
    
    private void replaceSearchersWithChunkUpdate(final Block currentBlock, final int dimensionId) {
        synchronized (this.chunksToUpdate) {
            if (this.chunksToUpdate.isEmpty()) {
                monitorexit(this.chunksToUpdate);
                return;
            }
            final Iterator<Chunk> itr = this.chunksToUpdate.iterator();
            while (itr.hasNext()) {
                final Chunk chunk = itr.next();
                final ChunkSearcher oldSearcher = this.searchers.get(chunk);
                if (oldSearcher == null) {
                    continue;
                }
                this.removeSearcher(oldSearcher);
                this.addSearcher(chunk, currentBlock, dimensionId);
                itr.remove();
            }
            monitorexit(this.chunksToUpdate);
        }
    }
    
    private void addSearcher(final Chunk chunk, final Block block, final int dimensionId) {
        this.stopPool2Tasks();
        final ChunkSearcher searcher = new ChunkSearcher(chunk, block, dimensionId);
        this.searchers.put(chunk, searcher);
        searcher.startSearching(this.pool1);
    }
    
    private void removeSearcher(final ChunkSearcher searcher) {
        this.stopPool2Tasks();
        this.searchers.remove(searcher.chunk);
        searcher.cancelSearching();
    }
    
    private void stopPool2Tasks() {
        if (this.getMatchingBlocksTask != null) {
            this.getMatchingBlocksTask.cancel(true);
            this.getMatchingBlocksTask = null;
        }
        if (this.compileVerticesTask != null) {
            this.compileVerticesTask.cancel(true);
            this.compileVerticesTask = null;
        }
        this.displayListUpToDate = false;
    }
    
    private boolean areAllChunkSearchersDone() {
        for (final ChunkSearcher searcher : this.searchers.values()) {
            if (searcher.status != ChunkSearcher.Status.DONE) {
                return false;
            }
        }
        return true;
    }
    
    private void checkIfLimitChanged() {
        if (this.limit.getValueI() != this.prevLimit) {
            this.stopPool2Tasks();
            this.notify = true;
            this.prevLimit = this.limit.getValueI();
        }
    }
    
    private void startGetMatchingBlocksTask(final BlockPos eyesPos) {
        final int maxBlocks = (int)Math.pow(10.0, this.limit.getValueI());
        final Callable<HashSet<BlockPos>> task = (Callable<HashSet<BlockPos>>)(() -> this.searchers.values().parallelStream().flatMap(searcher -> searcher.matchingBlocks.stream()).sorted(Comparator.comparingInt(pos -> blockPos.getManhattanDistance(pos))).limit(n).collect((Collector<? super Object, ?, HashSet<?>>)Collectors.toCollection(() -> new HashSet())));
        this.getMatchingBlocksTask = this.pool2.submit(task);
    }
    
    private HashSet<BlockPos> getMatchingBlocksFromTask() {
        HashSet<BlockPos> matchingBlocks = new HashSet<BlockPos>();
        try {
            matchingBlocks = this.getMatchingBlocksTask.get();
        }
        catch (final InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        final int maxBlocks = (int)Math.pow(10.0, this.limit.getValueI());
        if (matchingBlocks.size() < maxBlocks) {
            this.notify = true;
        }
        else if (this.notify) {
            ChatUtils.warning("Search found §lA LOT§r of blocks! To prevent lag, it will only show the closest §6" + this.limit.getValueString() + "§r results.");
            this.notify = false;
        }
        return matchingBlocks;
    }
    
    private void startCompileVerticesTask() {
        final HashSet<BlockPos> matchingBlocks = this.getMatchingBlocksFromTask();
        final Callable<ArrayList<int[]>> task = (Callable<ArrayList<int[]>>)(() -> set.parallelStream().flatMap(pos -> this.getVertices(pos, matchingBlocks2).stream()).collect(Collectors.toCollection(() -> new ArrayList())));
        this.compileVerticesTask = this.pool2.submit(task);
    }
    
    private ArrayList<int[]> getVertices(final BlockPos pos, final HashSet<BlockPos> matchingBlocks) {
        final ArrayList<int[]> vertices = new ArrayList<int[]>();
        if (!matchingBlocks.contains(pos.down())) {
            vertices.add(this.getVertex(pos, 0, 0, 0));
            vertices.add(this.getVertex(pos, 1, 0, 0));
            vertices.add(this.getVertex(pos, 1, 0, 1));
            vertices.add(this.getVertex(pos, 0, 0, 1));
        }
        if (!matchingBlocks.contains(pos.up())) {
            vertices.add(this.getVertex(pos, 0, 1, 0));
            vertices.add(this.getVertex(pos, 0, 1, 1));
            vertices.add(this.getVertex(pos, 1, 1, 1));
            vertices.add(this.getVertex(pos, 1, 1, 0));
        }
        if (!matchingBlocks.contains(pos.north())) {
            vertices.add(this.getVertex(pos, 0, 0, 0));
            vertices.add(this.getVertex(pos, 0, 1, 0));
            vertices.add(this.getVertex(pos, 1, 1, 0));
            vertices.add(this.getVertex(pos, 1, 0, 0));
        }
        if (!matchingBlocks.contains(pos.east())) {
            vertices.add(this.getVertex(pos, 1, 0, 0));
            vertices.add(this.getVertex(pos, 1, 1, 0));
            vertices.add(this.getVertex(pos, 1, 1, 1));
            vertices.add(this.getVertex(pos, 1, 0, 1));
        }
        if (!matchingBlocks.contains(pos.south())) {
            vertices.add(this.getVertex(pos, 0, 0, 1));
            vertices.add(this.getVertex(pos, 1, 0, 1));
            vertices.add(this.getVertex(pos, 1, 1, 1));
            vertices.add(this.getVertex(pos, 0, 1, 1));
        }
        if (!matchingBlocks.contains(pos.west())) {
            vertices.add(this.getVertex(pos, 0, 0, 0));
            vertices.add(this.getVertex(pos, 0, 0, 1));
            vertices.add(this.getVertex(pos, 0, 1, 1));
            vertices.add(this.getVertex(pos, 0, 1, 0));
        }
        return vertices;
    }
    
    private int[] getVertex(final BlockPos pos, final int x, final int y, final int z) {
        return new int[] { pos.getX() + x, pos.getY() + y, pos.getZ() + z };
    }
    
    private void setDisplayListFromTask() {
        ArrayList<int[]> vertices;
        try {
            vertices = this.compileVerticesTask.get();
        }
        catch (final InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        GL11.glNewList(this.displayList, 4864);
        for (final int[] vertex : vertices) {
            GL11.glVertex3d((double)vertex[0], (double)vertex[1], (double)vertex[2]);
        }
        GL11.glEndList();
        this.displayListUpToDate = true;
    }
    
    private enum Area
    {
        D3("D3", 0, "3x3 chunks", 1), 
        D5("D5", 1, "5x5 chunks", 2), 
        D7("D7", 2, "7x7 chunks", 3), 
        D9("D9", 3, "9x9 chunks", 4), 
        D11("D11", 4, "11x11 chunks", 5), 
        D13("D13", 5, "13x13 chunks", 6), 
        D15("D15", 6, "15x15 chunks", 7), 
        D17("D17", 7, "17x17 chunks", 8), 
        D19("D19", 8, "19x19 chunks", 9), 
        D21("D21", 9, "21x21 chunks", 10), 
        D23("D23", 10, "23x23 chunks", 11), 
        D25("D25", 11, "25x25 chunks", 12), 
        D27("D27", 12, "27x27 chunks", 13), 
        D29("D29", 13, "29x29 chunks", 14), 
        D31("D31", 14, "31x31 chunks", 15), 
        D33("D33", 15, "33x33 chunks", 16);
        
        private final String name;
        private final int chunkRange;
        
        private Area(final String name2, final int ordinal, final String name, final int chunkRange) {
            this.name = name;
            this.chunkRange = chunkRange;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    private static class ChunkSearcher
    {
        private final Chunk chunk;
        private final Block block;
        private final int dimensionId;
        private final ArrayList<BlockPos> matchingBlocks;
        private Status status;
        private Future future;
        
        public ChunkSearcher(final Chunk chunk, final Block block, final int dimensionId) {
            this.matchingBlocks = new ArrayList<BlockPos>();
            this.status = Status.IDLE;
            this.chunk = chunk;
            this.block = block;
            this.dimensionId = dimensionId;
        }
        
        public void startSearching(final ExecutorService pool) {
            if (this.status != Status.IDLE) {
                throw new IllegalStateException();
            }
            this.status = Status.SEARCHING;
            this.future = pool.submit(() -> this.searchNow());
        }
        
        private void searchNow() {
            if (this.status == Status.IDLE || this.status == Status.DONE || !this.matchingBlocks.isEmpty()) {
                throw new IllegalStateException();
            }
            final ChunkPos chunkPos = this.chunk.getChunkCoordIntPair();
            final int minX = chunkPos.getXStart();
            final int minY = 0;
            final int minZ = chunkPos.getZStart();
            final int maxX = chunkPos.getXEnd();
            final int maxY = 255;
            final int maxZ = chunkPos.getZEnd();
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        if (this.status == Status.INTERRUPTED || Thread.interrupted()) {
                            return;
                        }
                        final BlockPos pos = new BlockPos(x, y, z);
                        final Block block = BlockUtils.getBlock(pos);
                        if (this.block.equals(block)) {
                            this.matchingBlocks.add(pos);
                        }
                    }
                }
            }
            this.status = Status.DONE;
        }
        
        public void cancelSearching() {
            new Thread(() -> this.cancelNow(), "ChunkSearcher-canceller").start();
        }
        
        private void cancelNow() {
            if (this.future != null) {
                try {
                    this.status = Status.INTERRUPTED;
                    this.future.get();
                }
                catch (final InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            this.matchingBlocks.clear();
            this.status = Status.IDLE;
        }
        
        private enum Status
        {
            IDLE("IDLE", 0), 
            SEARCHING("SEARCHING", 1), 
            INTERRUPTED("INTERRUPTED", 2), 
            DONE("DONE", 3);
            
            private Status(final String name, final int ordinal) {
            }
        }
    }
}
