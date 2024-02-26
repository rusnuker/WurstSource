// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.compatibility.WItem;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockFarmland;
import java.util.Map;
import net.minecraft.init.Items;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockMelon;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockCrops;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import net.wurstclient.util.RotationUtils;
import net.wurstclient.util.RenderUtils;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import java.util.Set;
import java.util.ArrayDeque;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto farm", "AutoHarvest", "auto harvest" })
@Bypasses(ghostMode = false)
public final class AutoFarmMod extends Hack implements UpdateListener, RenderListener
{
    private final SliderSetting range;
    private final HashMap<BlockPos, Item> plants;
    private final ArrayDeque<Set<BlockPos>> prevBlocks;
    private BlockPos currentBlock;
    private float progress;
    private float prevProgress;
    private int displayList;
    private int box;
    private int node;
    
    public AutoFarmMod() {
        super("AutoFarm", "Harvests and re-plants crops automatically.\nWorks with wheat, carrots, potatoes, beetroots,\npumpkins, melons, cacti, sugar canes and\nnether warts.");
        this.range = new SliderSetting("Range", 5.0, 1.0, 6.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.plants = new HashMap<BlockPos, Item>();
        this.prevBlocks = new ArrayDeque<Set<BlockPos>>();
        this.setCategory(Category.BLOCKS);
        this.addSetting(this.range);
    }
    
    @Override
    public void onEnable() {
        this.plants.clear();
        this.displayList = GL11.glGenLists(1);
        this.box = GL11.glGenLists(1);
        this.node = GL11.glGenLists(1);
        GL11.glNewList(this.box, 4864);
        final Box box = new Box(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);
        RenderUtils.drawOutlinedBox(box);
        GL11.glEndList();
        GL11.glNewList(this.node, 4864);
        final Box node = new Box(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
        GL11.glBegin(1);
        RenderUtils.drawNode(node);
        GL11.glEnd();
        GL11.glEndList();
        AutoFarmMod.EVENTS.add(UpdateListener.class, this);
        AutoFarmMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoFarmMod.EVENTS.remove(UpdateListener.class, this);
        AutoFarmMod.EVENTS.remove(RenderListener.class, this);
        if (this.currentBlock != null) {
            AutoFarmMod.MC.playerController.isHittingBlock = true;
            AutoFarmMod.MC.playerController.resetBlockRemoving();
            this.currentBlock = null;
        }
        this.prevBlocks.clear();
        GL11.glDeleteLists(this.displayList, 1);
        GL11.glDeleteLists(this.box, 1);
        GL11.glDeleteLists(this.node, 1);
    }
    
    @Override
    public void onUpdate() {
        this.currentBlock = null;
        final Vec3d eyesVec = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
        final BlockPos eyesBlock = new BlockPos(RotationUtils.getEyesPos());
        final double rangeSq = Math.pow(this.range.getValue(), 2.0);
        final int blockRange = (int)Math.ceil(this.range.getValue());
        BlockPos pos = null;
        final List<BlockPos> blocks = this.getBlockStream(eyesBlock, blockRange).filter(pos -> vec3d.squareDistanceTo(new Vec3d(pos)) <= n).filter(pos -> WBlock.canBeClicked(pos)).collect((Collector<? super BlockPos, ?, List<BlockPos>>)Collectors.toList());
        this.registerPlants(blocks);
        List<BlockPos> blocksToHarvest = new ArrayList<BlockPos>();
        List<BlockPos> blocksToReplant = new ArrayList<BlockPos>();
        if (!AutoFarmMod.WURST.hax.freecamMod.isActive()) {
            blocksToHarvest = blocks.parallelStream().filter((Predicate<? super Object>)this::shouldBeHarvested).sorted(Comparator.comparingDouble(pos -> vec3d2.squareDistanceTo(new Vec3d(pos)))).collect((Collector<? super Object, ?, List<BlockPos>>)Collectors.toList());
            blocksToReplant = this.getBlockStream(eyesBlock, blockRange).filter(pos -> vec3d3.squareDistanceTo(new Vec3d(pos)) <= n2).filter(pos -> WBlock.getMaterial(pos).isReplaceable()).filter(pos -> this.plants.containsKey(pos)).filter(this::canBeReplanted).sorted(Comparator.comparingDouble(pos -> vec3d4.squareDistanceTo(new Vec3d(pos)))).collect((Collector<? super BlockPos, ?, List<BlockPos>>)Collectors.toList());
        }
        while (!blocksToReplant.isEmpty()) {
            pos = blocksToReplant.get(0);
            final Item neededItem = this.plants.get(pos);
            if (this.tryToReplant(pos, neededItem)) {
                break;
            }
            blocksToReplant.removeIf(p -> this.plants.get(p) == item);
        }
        if (blocksToReplant.isEmpty()) {
            this.harvest(blocksToHarvest);
        }
        this.updateDisplayList(blocksToHarvest, blocksToReplant);
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
        GL11.glTranslated(-AutoFarmMod.MC.getRenderManager().renderPosX, -AutoFarmMod.MC.getRenderManager().renderPosY, -AutoFarmMod.MC.getRenderManager().renderPosZ);
        GL11.glCallList(this.displayList);
        if (this.currentBlock != null) {
            GL11.glPushMatrix();
            final Box box = new Box(BlockPos.ORIGIN);
            final float p = this.prevProgress + (this.progress - this.prevProgress) * partialTicks;
            final float red = p * 2.0f;
            final float green = 2.0f - red;
            GL11.glTranslated((double)this.currentBlock.getX(), (double)this.currentBlock.getY(), (double)this.currentBlock.getZ());
            if (p < 1.0f) {
                GL11.glTranslated(0.5, 0.5, 0.5);
                GL11.glScaled((double)p, (double)p, (double)p);
                GL11.glTranslated(-0.5, -0.5, -0.5);
            }
            GL11.glColor4f(red, green, 0.0f, 0.25f);
            RenderUtils.drawSolidBox(box);
            GL11.glColor4f(red, green, 0.0f, 0.5f);
            RenderUtils.drawOutlinedBox(box);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    private Stream<BlockPos> getBlockStream(final BlockPos center, final int range) {
        return StreamSupport.stream(BlockPos.getAllInBox(center.add(range, range, range), center.add(-range, -range, -range)).spliterator(), true);
    }
    
    private boolean shouldBeHarvested(final BlockPos pos) {
        final Block block = WBlock.getBlock(pos);
        final IBlockState state = WBlock.getState(pos);
        if (block instanceof BlockCrops) {
            return ((BlockCrops)block).isMaxAge(state);
        }
        if (block == Blocks.PUMPKIN || block instanceof BlockMelon) {
            return true;
        }
        if (block instanceof BlockReed) {
            return WBlock.getBlock(pos.down()) instanceof BlockReed && !(WBlock.getBlock(pos.down(2)) instanceof BlockReed);
        }
        if (block instanceof BlockCactus) {
            return WBlock.getBlock(pos.down()) instanceof BlockCactus && !(WBlock.getBlock(pos.down(2)) instanceof BlockCactus);
        }
        return block instanceof BlockNetherWart && WBlock.getIntegerProperty(state, BlockNetherWart.AGE) >= 3;
    }
    
    private void registerPlants(final List<BlockPos> blocks) {
        final HashMap<Block, Item> seeds = new HashMap<Block, Item>();
        seeds.put(Blocks.WHEAT, Items.WHEAT_SEEDS);
        seeds.put(Blocks.CARROTS, Items.CARROT);
        seeds.put(Blocks.POTATOES, Items.POTATO);
        seeds.put(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
        seeds.put(Blocks.PUMPKIN_STEM, Items.PUMPKIN_SEEDS);
        seeds.put(Blocks.MELON_STEM, Items.MELON_SEEDS);
        seeds.put(Blocks.NETHER_WART, Items.NETHER_WART);
        this.plants.putAll(blocks.parallelStream().filter(pos -> hashMap.containsKey(WBlock.getBlock(pos))).collect(Collectors.toMap(pos -> pos, pos -> hashMap2.get(WBlock.getBlock(pos)))));
    }
    
    private boolean canBeReplanted(final BlockPos pos) {
        final Item item = this.plants.get(pos);
        if (item == Items.WHEAT_SEEDS || item == Items.CARROT || item == Items.POTATO || item == Items.BEETROOT_SEEDS || item == Items.PUMPKIN_SEEDS || item == Items.MELON_SEEDS) {
            return WBlock.getBlock(pos.down()) instanceof BlockFarmland;
        }
        return item == Items.NETHER_WART && WBlock.getBlock(pos.down()) instanceof BlockSoulSand;
    }
    
    private boolean tryToReplant(final BlockPos pos, final Item neededItem) {
        final EntityPlayerSP player = AutoFarmMod.MC.player;
        final ItemStack heldItem = player.getHeldItemMainhand();
        if (!WItem.isNullOrEmpty(heldItem) && heldItem.getItem() == neededItem) {
            BlockUtils.placeBlockSimple(pos);
            return true;
        }
        for (int slot = 0; slot < 36; ++slot) {
            if (slot != player.inventory.selectedSlot) {
                final ItemStack stack = player.inventory.getInvStack(slot);
                if (!WItem.isNullOrEmpty(stack)) {
                    if (stack.getItem() == neededItem) {
                        if (slot < 9) {
                            player.inventory.selectedSlot = slot;
                        }
                        else if (player.inventory.getFirstEmptyStack() < 9) {
                            WPlayerController.windowClick_QUICK_MOVE(slot);
                        }
                        else if (player.inventory.getFirstEmptyStack() != -1) {
                            WPlayerController.windowClick_QUICK_MOVE(player.inventory.selectedSlot + 36);
                            WPlayerController.windowClick_QUICK_MOVE(slot);
                        }
                        else {
                            WPlayerController.windowClick_PICKUP(player.inventory.selectedSlot + 36);
                            WPlayerController.windowClick_PICKUP(slot);
                            WPlayerController.windowClick_PICKUP(player.inventory.selectedSlot + 36);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void harvest(final List<BlockPos> blocksToHarvest) {
        if (AutoFarmMod.MC.player.abilities.creativeMode) {
            Stream<BlockPos> stream3 = blocksToHarvest.parallelStream();
            for (final Set<BlockPos> set : this.prevBlocks) {
                final BlockPos pos;
                stream3 = stream3.filter(pos -> !set2.contains(pos));
            }
            final List<BlockPos> blocksToHarvest2 = stream3.collect((Collector<? super BlockPos, ?, List<BlockPos>>)Collectors.toList());
            this.prevBlocks.addLast(new HashSet<BlockPos>(blocksToHarvest2));
            while (this.prevBlocks.size() > 5) {
                this.prevBlocks.removeFirst();
            }
            if (!blocksToHarvest2.isEmpty()) {
                this.currentBlock = blocksToHarvest2.get(0);
            }
            AutoFarmMod.MC.playerController.resetBlockRemoving();
            this.progress = 1.0f;
            this.prevProgress = 1.0f;
            BlockUtils.breakBlocksPacketSpam(blocksToHarvest2);
            return;
        }
        final Iterator<BlockPos> iterator2 = blocksToHarvest.iterator();
        while (iterator2.hasNext()) {
            final BlockPos pos = iterator2.next();
            if (BlockUtils.breakBlockSimple(pos)) {
                this.currentBlock = pos;
                break;
            }
        }
        if (this.currentBlock == null) {
            AutoFarmMod.MC.playerController.resetBlockRemoving();
        }
        if (this.currentBlock != null && WBlock.getHardness(this.currentBlock) < 1.0f) {
            this.prevProgress = this.progress;
            this.progress = AutoFarmMod.MC.playerController.curBlockDamageMP;
            if (this.progress < this.prevProgress) {
                this.prevProgress = this.progress;
            }
        }
        else {
            this.progress = 1.0f;
            this.prevProgress = 1.0f;
        }
    }
    
    private void updateDisplayList(final List<BlockPos> blocksToHarvest, final List<BlockPos> blocksToReplant) {
        GL11.glNewList(this.displayList, 4864);
        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
        for (final BlockPos pos : blocksToHarvest) {
            GL11.glPushMatrix();
            GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
            GL11.glCallList(this.box);
            GL11.glPopMatrix();
        }
        GL11.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        for (final BlockPos pos : this.plants.keySet()) {
            GL11.glPushMatrix();
            GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
            GL11.glCallList(this.node);
            GL11.glPopMatrix();
        }
        GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
        for (final BlockPos pos : blocksToReplant) {
            GL11.glPushMatrix();
            GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
            GL11.glCallList(this.box);
            GL11.glPopMatrix();
        }
        GL11.glEndList();
    }
}
