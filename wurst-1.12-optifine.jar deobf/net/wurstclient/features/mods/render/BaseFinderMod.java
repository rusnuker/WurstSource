// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.util.ChatUtils;
import net.wurstclient.compatibility.WBlock;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import net.wurstclient.Category;
import java.util.Arrays;
import net.minecraft.init.Blocks;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import java.util.HashSet;
import net.minecraft.block.Block;
import java.util.List;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "base finder", "factions" })
@Bypasses
public final class BaseFinderMod extends Hack implements UpdateListener, RenderListener
{
    private static final List<Block> NATURAL_BLOCKS;
    private final HashSet<BlockPos> matchingBlocks;
    private final ArrayList<int[]> vertices;
    private int messageTimer;
    private int counter;
    
    static {
        NATURAL_BLOCKS = Arrays.asList(Blocks.AIR, Blocks.STONE, Blocks.DIRT, Blocks.GRASS, Blocks.GRAVEL, Blocks.SAND, Blocks.CLAY, Blocks.SANDSTONE, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.LOG, Blocks.LOG2, Blocks.LEAVES, Blocks.LEAVES2, Blocks.DEADBUSH, Blocks.IRON_ORE, Blocks.COAL_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.BEDROCK, Blocks.MOB_SPAWNER, Blocks.MOSSY_COBBLESTONE, Blocks.TALLGRASS, Blocks.YELLOW_FLOWER, Blocks.RED_FLOWER, Blocks.WEB, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.SNOW_LAYER, Blocks.VINE, Blocks.WATERLILY, Blocks.DOUBLE_PLANT, Blocks.HARDENED_CLAY, Blocks.RED_SANDSTONE, Blocks.ICE, Blocks.QUARTZ_ORE, Blocks.OBSIDIAN, Blocks.MONSTER_EGG, Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK);
    }
    
    public BaseFinderMod() {
        super("BaseFinder", "Finds player bases by searching for man-made blocks.\nThe blocks that it finds will be highlighted in red.\nGood for finding faction bases.");
        this.matchingBlocks = new HashSet<BlockPos>();
        this.vertices = new ArrayList<int[]>();
        this.messageTimer = 0;
        this.setCategory(Category.RENDER);
    }
    
    @Override
    public String getRenderName() {
        String name = String.valueOf(this.getName()) + " [";
        if (this.counter >= 10000) {
            name = String.valueOf(name) + "10000+ blocks";
        }
        else if (this.counter == 1) {
            name = String.valueOf(name) + "1 block";
        }
        else if (this.counter == 0) {
            name = String.valueOf(name) + "nothing";
        }
        else {
            name = String.valueOf(name) + this.counter + " blocks";
        }
        name = String.valueOf(name) + " found]";
        return name;
    }
    
    @Override
    public void onEnable() {
        this.messageTimer = 0;
        BaseFinderMod.EVENTS.add(UpdateListener.class, this);
        BaseFinderMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        BaseFinderMod.EVENTS.remove(UpdateListener.class, this);
        BaseFinderMod.EVENTS.remove(RenderListener.class, this);
    }
    
    @Override
    public void onRender(final float partialTicks) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GL11.glDisable(2929);
        GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.15f);
        GL11.glPushMatrix();
        GL11.glTranslated(-BaseFinderMod.MC.getRenderManager().renderPosX, -BaseFinderMod.MC.getRenderManager().renderPosY, -BaseFinderMod.MC.getRenderManager().renderPosZ);
        GL11.glBegin(7);
        for (final int[] vertex : this.vertices) {
            GL11.glVertex3d((double)vertex[0], (double)vertex[1], (double)vertex[2]);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    
    @Override
    public void onUpdate() {
        final int modulo = BaseFinderMod.MC.player.ticksExisted % 64;
        if (modulo == 0) {
            this.matchingBlocks.clear();
        }
        final int startY = 255 - modulo * 4;
        final int endY = startY - 4;
        final BlockPos playerPos = new BlockPos(BaseFinderMod.MC.player.posX, 0.0, BaseFinderMod.MC.player.posZ);
    Label_0171:
        for (int y = startY; y > endY; --y) {
            for (int x = 64; x > -64; --x) {
                for (int z = 64; z > -64; --z) {
                    if (this.matchingBlocks.size() >= 10000) {
                        break Label_0171;
                    }
                    final BlockPos pos = playerPos.add(x, y, z);
                    if (!BaseFinderMod.NATURAL_BLOCKS.contains(WBlock.getBlock(pos))) {
                        this.matchingBlocks.add(pos);
                    }
                }
            }
        }
        if (modulo != 63) {
            return;
        }
        if (this.matchingBlocks.size() < 10000) {
            --this.messageTimer;
        }
        else {
            if (this.messageTimer <= 0) {
                ChatUtils.warning("BaseFinder found §lA LOT§r of blocks.");
                ChatUtils.message("To prevent lag, it will only show the first 10000 blocks.");
            }
            this.messageTimer = 3;
        }
        this.counter = this.matchingBlocks.size();
        this.vertices.clear();
        for (final BlockPos pos2 : this.matchingBlocks) {
            if (!this.matchingBlocks.contains(pos2.down())) {
                this.addVertex(pos2, 0, 0, 0);
                this.addVertex(pos2, 1, 0, 0);
                this.addVertex(pos2, 1, 0, 1);
                this.addVertex(pos2, 0, 0, 1);
            }
            if (!this.matchingBlocks.contains(pos2.up())) {
                this.addVertex(pos2, 0, 1, 0);
                this.addVertex(pos2, 0, 1, 1);
                this.addVertex(pos2, 1, 1, 1);
                this.addVertex(pos2, 1, 1, 0);
            }
            if (!this.matchingBlocks.contains(pos2.north())) {
                this.addVertex(pos2, 0, 0, 0);
                this.addVertex(pos2, 0, 1, 0);
                this.addVertex(pos2, 1, 1, 0);
                this.addVertex(pos2, 1, 0, 0);
            }
            if (!this.matchingBlocks.contains(pos2.east())) {
                this.addVertex(pos2, 1, 0, 0);
                this.addVertex(pos2, 1, 1, 0);
                this.addVertex(pos2, 1, 1, 1);
                this.addVertex(pos2, 1, 0, 1);
            }
            if (!this.matchingBlocks.contains(pos2.south())) {
                this.addVertex(pos2, 0, 0, 1);
                this.addVertex(pos2, 1, 0, 1);
                this.addVertex(pos2, 1, 1, 1);
                this.addVertex(pos2, 0, 1, 1);
            }
            if (!this.matchingBlocks.contains(pos2.west())) {
                this.addVertex(pos2, 0, 0, 0);
                this.addVertex(pos2, 0, 0, 1);
                this.addVertex(pos2, 0, 1, 1);
                this.addVertex(pos2, 0, 1, 0);
            }
        }
    }
    
    private void addVertex(final BlockPos pos, final int x, final int y, final int z) {
        this.vertices.add(new int[] { pos.getX() + x, pos.getY() + y, pos.getZ() + z });
    }
}
