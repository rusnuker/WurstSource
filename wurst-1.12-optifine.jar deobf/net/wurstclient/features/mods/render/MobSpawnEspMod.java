// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.minecraft.world.EnumSkyBlock;
import net.minecraft.block.BlockLiquid;
import net.wurstclient.compatibility.WBlock;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import net.wurstclient.util.RenderUtils;
import net.minecraft.network.Packet;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.client.multiplayer.WorldClient;
import java.util.ConcurrentModificationException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Comparator;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.util.RotationUtils;
import net.wurstclient.compatibility.WMinecraft;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import java.util.Collection;
import java.util.ArrayList;
import net.wurstclient.util.MinPriorityThreadFactory;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.concurrent.ExecutorService;
import net.minecraft.world.chunk.Chunk;
import java.util.HashMap;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.PacketInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "mob spawn esp", "LightLevelESP", "light level esp", "LightLevelOverlay", "light level overlay" })
public final class MobSpawnEspMod extends Hack implements UpdateListener, PacketInputListener, RenderListener
{
    private final EnumSetting<DrawDistance> drawDistance;
    private final SliderSetting loadingSpeed;
    private final HashMap<Chunk, ChunkScanner> scanners;
    private ExecutorService pool;
    
    public MobSpawnEspMod() {
        super("MobSpawnESP", "Highlights areas where mobs can spawn.\n" + ChatFormatting.YELLOW + "yellow" + ChatFormatting.RESET + " - mobs can spawn at night\n" + ChatFormatting.RED + "red" + ChatFormatting.RESET + " - mobs can always spawn");
        this.drawDistance = new EnumSetting<DrawDistance>("Draw distance", DrawDistance.values(), DrawDistance.D9);
        this.loadingSpeed = new SliderSetting("Loading speed", 1.0, 1.0, 5.0, 1.0, v -> String.valueOf((int)v) + "x");
        this.scanners = new HashMap<Chunk, ChunkScanner>();
        this.setCategory(Category.RENDER);
        this.addSetting(this.drawDistance);
        this.addSetting(this.loadingSpeed);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { MobSpawnEspMod.WURST.hax.playerEspMod, MobSpawnEspMod.WURST.hax.itemEspMod };
    }
    
    @Override
    public void onEnable() {
        this.pool = MinPriorityThreadFactory.newFixedThreadPool();
        MobSpawnEspMod.EVENTS.add(UpdateListener.class, this);
        MobSpawnEspMod.EVENTS.add(PacketInputListener.class, this);
        MobSpawnEspMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        MobSpawnEspMod.EVENTS.remove(UpdateListener.class, this);
        MobSpawnEspMod.EVENTS.remove(PacketInputListener.class, this);
        MobSpawnEspMod.EVENTS.remove(RenderListener.class, this);
        for (final ChunkScanner scanner : new ArrayList(this.scanners.values())) {
            if (scanner.displayList != 0) {
                GL11.glDeleteLists(scanner.displayList, 1);
            }
            this.scanners.remove(scanner.chunk);
        }
        this.pool.shutdownNow();
    }
    
    @Override
    public void onUpdate() {
        final WorldClient world = WMinecraft.getWorld();
        final BlockPos eyesBlock = new BlockPos(RotationUtils.getEyesPos());
        final int chunkX = eyesBlock.getX() >> 4;
        final int chunkZ = eyesBlock.getZ() >> 4;
        final int chunkRange = this.drawDistance.getSelected().chunkRange;
        final ArrayList<Chunk> chunks = new ArrayList<Chunk>();
        for (int x = chunkX - chunkRange; x <= chunkX + chunkRange; ++x) {
            for (int z = chunkZ - chunkRange; z <= chunkZ + chunkRange; ++z) {
                chunks.add(world.getChunkFromChunkCoords(x, z));
            }
        }
        for (final Chunk chunk : chunks) {
            if (this.scanners.containsKey(chunk)) {
                continue;
            }
            final ChunkScanner scanner = new ChunkScanner(chunk);
            this.scanners.put(chunk, scanner);
            scanner.future = this.pool.submit(() -> chunkScanner.scan());
        }
        for (final ChunkScanner scanner2 : new ArrayList(this.scanners.values())) {
            if (Math.abs(scanner2.chunk.xPosition - chunkX) <= chunkRange && Math.abs(scanner2.chunk.zPosition - chunkZ) <= chunkRange) {
                continue;
            }
            if (!scanner2.doneCompiling) {
                continue;
            }
            if (scanner2.displayList != 0) {
                GL11.glDeleteLists(scanner2.displayList, 1);
            }
            if (scanner2.future != null) {
                scanner2.future.cancel(true);
            }
            this.scanners.remove(scanner2.chunk);
        }
        final Comparator<ChunkScanner> c = Comparator.comparingInt(s -> Math.abs(s.chunk.xPosition - n) + Math.abs(s.chunk.zPosition - n2));
        final List<ChunkScanner> sortedScanners = this.scanners.values().stream().filter(s -> s.doneScanning).filter(s -> !s.doneCompiling).sorted(c).limit(this.loadingSpeed.getValueI()).collect((Collector<? super ChunkScanner, ?, List<ChunkScanner>>)Collectors.toList());
        final Iterator<ChunkScanner> iterator3 = sortedScanners.iterator();
        while (iterator3.hasNext()) {
            final ChunkScanner scanner = iterator3.next();
            if (scanner.displayList == 0) {
                ChunkScanner.access$5(scanner, GL11.glGenLists(1));
            }
            try {
                scanner.compileDisplayList();
            }
            catch (final ConcurrentModificationException e) {
                System.out.println("WARNING! ChunkScanner.compileDisplayList(); failed with the following exception:");
                e.printStackTrace();
                GL11.glDeleteLists(scanner.displayList, 1);
                ChunkScanner.access$5(scanner, 0);
            }
        }
    }
    
    @Override
    public void onReceivedPacket(final PacketInputEvent event) {
        final EntityPlayerSP player = MobSpawnEspMod.MC.player;
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
        final ArrayList<Chunk> chunks = new ArrayList<Chunk>();
        for (int x = chunk.xPosition - 1; x <= chunk.xPosition + 1; ++x) {
            for (int z = chunk.zPosition - 1; z <= chunk.zPosition + 1; ++z) {
                chunks.add(world.getChunkFromChunkCoords(x, z));
            }
        }
        for (final Chunk chunk2 : chunks) {
            final ChunkScanner scanner = this.scanners.get(chunk2);
            if (scanner == null) {
                return;
            }
            scanner.reset();
            scanner.future = this.pool.submit(() -> chunkScanner.scan());
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
        GL11.glPushMatrix();
        RenderUtils.applyRenderOffset();
        for (final ChunkScanner scanner : new ArrayList(this.scanners.values())) {
            if (scanner.displayList == 0) {
                continue;
            }
            GL11.glCallList(scanner.displayList);
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    private enum DrawDistance
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
        D25("D25", 11, "25x25 chunks", 12);
        
        private final String name;
        private final int chunkRange;
        
        private DrawDistance(final String name2, final int ordinal, final String name, final int chunkRange) {
            this.name = name;
            this.chunkRange = chunkRange;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    private class ChunkScanner
    {
        public Future future;
        private final Chunk chunk;
        private final Set<BlockPos> red;
        private final Set<BlockPos> yellow;
        private int displayList;
        private boolean doneScanning;
        private boolean doneCompiling;
        
        public ChunkScanner(final Chunk chunk) {
            this.red = new HashSet<BlockPos>();
            this.yellow = new HashSet<BlockPos>();
            this.chunk = chunk;
        }
        
        private void scan() {
            final int minX = this.chunk.xPosition << 4;
            final int minY = 0;
            final int minZ = this.chunk.zPosition << 4;
            final int maxX = (this.chunk.xPosition << 4) + 15;
            final int maxY = 255;
            final int maxZ = (this.chunk.zPosition << 4) + 15;
            final WorldClient world = WMinecraft.getWorld();
            final ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
            BlockPos pos = null;
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        pos = new BlockPos(x, y, z);
                        if (!WBlock.getMaterial(pos).blocksMovement()) {
                            if (!(WBlock.getBlock(pos) instanceof BlockLiquid)) {
                                if (WBlock.isFullyOpaque(pos.down())) {
                                    blocks.add(pos);
                                }
                            }
                        }
                    }
                }
            }
            if (Thread.interrupted()) {
                return;
            }
            this.red.addAll(blocks.stream().filter(pos -> worldClient.getLightFor(EnumSkyBlock.BLOCK, pos) < 8).filter(pos -> worldClient2.getLightFor(EnumSkyBlock.SKY, pos) < 8).collect((Collector<? super Object, ?, Collection<? extends BlockPos>>)Collectors.toList()));
            if (Thread.interrupted()) {
                return;
            }
            this.yellow.addAll(blocks.stream().filter(pos -> !this.red.contains(pos)).filter(pos -> worldClient3.getLightFor(EnumSkyBlock.BLOCK, pos) < 8).collect((Collector<? super Object, ?, Collection<? extends BlockPos>>)Collectors.toList()));
            this.doneScanning = true;
        }
        
        private void compileDisplayList() {
            GL11.glNewList(this.displayList, 4864);
            try {
                GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
                GL11.glBegin(1);
                new ArrayList(this.red).forEach(pos -> {
                    GL11.glVertex3d((double)pos.getX(), pos.getY() + 0.01, (double)pos.getZ());
                    GL11.glVertex3d((double)(pos.getX() + 1), pos.getY() + 0.01, (double)(pos.getZ() + 1));
                    GL11.glVertex3d((double)(pos.getX() + 1), pos.getY() + 0.01, (double)pos.getZ());
                    GL11.glVertex3d((double)pos.getX(), pos.getY() + 0.01, (double)(pos.getZ() + 1));
                    return;
                });
                GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
                new ArrayList(this.yellow).forEach(pos -> {
                    GL11.glVertex3d((double)pos.getX(), pos.getY() + 0.01, (double)pos.getZ());
                    GL11.glVertex3d((double)(pos.getX() + 1), pos.getY() + 0.01, (double)(pos.getZ() + 1));
                    GL11.glVertex3d((double)(pos.getX() + 1), pos.getY() + 0.01, (double)pos.getZ());
                    GL11.glVertex3d((double)pos.getX(), pos.getY() + 0.01, (double)(pos.getZ() + 1));
                    return;
                });
                GL11.glEnd();
            }
            finally {
                GL11.glEndList();
            }
            GL11.glEndList();
            this.doneCompiling = true;
        }
        
        private void reset() {
            if (this.future != null) {
                this.future.cancel(true);
            }
            this.red.clear();
            this.yellow.clear();
            this.doneScanning = false;
            this.doneCompiling = false;
        }
        
        static /* synthetic */ void access$5(final ChunkScanner chunkScanner, final int displayList) {
            chunkScanner.displayList = displayList;
        }
    }
}
