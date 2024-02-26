// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.ai;

import java.util.Map;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import java.util.List;
import java.util.Collections;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.util.EnumFacing;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.WurstClient;

public class PathFinder
{
    private final WurstClient wurst;
    private final boolean invulnerable;
    private final boolean creativeFlying;
    protected final boolean flying;
    private final boolean immuneToFallDamage;
    private final boolean noWaterSlowdown;
    private final boolean jesus;
    private final boolean spider;
    protected boolean fallingAllowed;
    protected boolean divingAllowed;
    private final PathPos start;
    protected PathPos current;
    private final BlockPos goal;
    private final HashMap<PathPos, Float> costMap;
    protected final HashMap<PathPos, PathPos> prevPosMap;
    private final PathQueue queue;
    protected int thinkSpeed;
    protected int thinkTime;
    private int iterations;
    protected boolean done;
    protected boolean failed;
    private final ArrayList<PathPos> path;
    
    public PathFinder(final BlockPos goal) {
        this.wurst = WurstClient.INSTANCE;
        this.invulnerable = WMinecraft.getPlayer().abilities.creativeMode;
        this.creativeFlying = WMinecraft.getPlayer().abilities.isFlying;
        this.flying = (this.creativeFlying || this.wurst.hax.flightMod.isActive());
        this.immuneToFallDamage = (this.invulnerable || this.wurst.hax.noFallMod.isActive());
        this.noWaterSlowdown = this.wurst.hax.antiWaterPushHack.isPreventingSlowdown();
        this.jesus = this.wurst.hax.jesusMod.isActive();
        this.spider = this.wurst.hax.spiderMod.isActive();
        this.fallingAllowed = true;
        this.divingAllowed = true;
        this.costMap = new HashMap<PathPos, Float>();
        this.prevPosMap = new HashMap<PathPos, PathPos>();
        this.queue = new PathQueue();
        this.thinkSpeed = 1024;
        this.thinkTime = 200;
        this.path = new ArrayList<PathPos>();
        if (WMinecraft.getPlayer().onGround) {
            this.start = new PathPos(new BlockPos(WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY + 0.5, WMinecraft.getPlayer().posZ));
        }
        else {
            this.start = new PathPos(new BlockPos(WMinecraft.getPlayer()));
        }
        this.goal = goal;
        this.costMap.put(this.start, 0.0f);
        this.queue.add(this.start, this.getHeuristic(this.start));
    }
    
    public PathFinder(final PathFinder pathFinder) {
        this(pathFinder.goal);
        this.thinkSpeed = pathFinder.thinkSpeed;
        this.thinkTime = pathFinder.thinkTime;
    }
    
    public void think() {
        if (this.done) {
            throw new IllegalStateException("Path was already found!");
        }
        int i;
        for (i = 0; i < this.thinkSpeed && !this.checkFailed(); ++i) {
            this.current = this.queue.poll();
            if (this.checkDone()) {
                return;
            }
            for (final PathPos next : this.getNeighbors(this.current)) {
                final float newCost = this.costMap.get(this.current) + this.getCost(this.current, next);
                if (this.costMap.containsKey(next) && this.costMap.get(next) <= newCost) {
                    continue;
                }
                this.costMap.put(next, newCost);
                this.prevPosMap.put(next, this.current);
                this.queue.add(next, newCost + this.getHeuristic(next));
            }
        }
        this.iterations += i;
    }
    
    protected boolean checkDone() {
        return this.done = this.goal.equals(this.current);
    }
    
    private boolean checkFailed() {
        return this.failed = (this.queue.isEmpty() || this.iterations >= this.thinkSpeed * this.thinkTime);
    }
    
    private ArrayList<PathPos> getNeighbors(final PathPos pos) {
        final ArrayList<PathPos> neighbors = new ArrayList<PathPos>();
        if (Math.abs(this.start.getX() - pos.getX()) > 256 || Math.abs(this.start.getZ() - pos.getZ()) > 256) {
            return neighbors;
        }
        final BlockPos north = pos.north();
        final BlockPos east = pos.east();
        final BlockPos south = pos.south();
        final BlockPos west = pos.west();
        final BlockPos northEast = north.east();
        final BlockPos southEast = south.east();
        final BlockPos southWest = south.west();
        final BlockPos northWest = north.west();
        final BlockPos up = pos.up();
        final BlockPos down = pos.down();
        final boolean flying = this.canFlyAt(pos);
        final boolean onGround = this.canBeSolid(down);
        if (flying || onGround || pos.isJumping() || this.canMoveSidewaysInMidairAt(pos) || this.canClimbUpAt(pos.down())) {
            if (this.checkHorizontalMovement(pos, north)) {
                neighbors.add(new PathPos(north));
            }
            if (this.checkHorizontalMovement(pos, east)) {
                neighbors.add(new PathPos(east));
            }
            if (this.checkHorizontalMovement(pos, south)) {
                neighbors.add(new PathPos(south));
            }
            if (this.checkHorizontalMovement(pos, west)) {
                neighbors.add(new PathPos(west));
            }
            if (this.checkDiagonalMovement(pos, EnumFacing.NORTH, EnumFacing.EAST)) {
                neighbors.add(new PathPos(northEast));
            }
            if (this.checkDiagonalMovement(pos, EnumFacing.SOUTH, EnumFacing.EAST)) {
                neighbors.add(new PathPos(southEast));
            }
            if (this.checkDiagonalMovement(pos, EnumFacing.SOUTH, EnumFacing.WEST)) {
                neighbors.add(new PathPos(southWest));
            }
            if (this.checkDiagonalMovement(pos, EnumFacing.NORTH, EnumFacing.WEST)) {
                neighbors.add(new PathPos(northWest));
            }
        }
        if (pos.getY() < 256 && this.canGoThrough(up.up()) && (flying || onGround || this.canClimbUpAt(pos)) && (flying || this.canClimbUpAt(pos) || this.goal.equals(up) || this.canSafelyStandOn(north) || this.canSafelyStandOn(east) || this.canSafelyStandOn(south) || this.canSafelyStandOn(west)) && (this.divingAllowed || WBlock.getMaterial(up.up()) != Material.WATER)) {
            neighbors.add(new PathPos(up, onGround));
        }
        if (pos.getY() > 0 && this.canGoThrough(down) && this.canGoAbove(down.down()) && (flying || this.canFallBelow(pos)) && (this.divingAllowed || WBlock.getMaterial(pos) != Material.WATER)) {
            neighbors.add(new PathPos(down));
        }
        return neighbors;
    }
    
    private boolean checkHorizontalMovement(final BlockPos current, final BlockPos next) {
        return this.isPassable(next) && (this.canFlyAt(current) || this.canGoThrough(next.down()) || this.canSafelyStandOn(next.down()));
    }
    
    private boolean checkDiagonalMovement(final BlockPos current, final EnumFacing direction1, final EnumFacing direction2) {
        final BlockPos horizontal1 = current.offset(direction1);
        final BlockPos horizontal2 = current.offset(direction2);
        final BlockPos next = horizontal1.offset(direction2);
        return this.isPassable(horizontal1) && this.isPassable(horizontal2) && this.checkHorizontalMovement(current, next);
    }
    
    protected boolean isPassable(final BlockPos pos) {
        return this.canGoThrough(pos) && this.canGoThrough(pos.up()) && this.canGoAbove(pos.down()) && (this.divingAllowed || WBlock.getMaterial(pos.up()) != Material.WATER);
    }
    
    protected boolean canBeSolid(final BlockPos pos) {
        final Material material = WBlock.getMaterial(pos);
        final Block block = WBlock.getBlock(pos);
        return (material.blocksMovement() && !(block instanceof BlockSign)) || block instanceof BlockLadder || (this.jesus && (material == Material.WATER || material == Material.LAVA));
    }
    
    private boolean canGoThrough(final BlockPos pos) {
        if (!WMinecraft.getWorld().isBlockLoaded(pos, false)) {
            return false;
        }
        final Material material = WBlock.getMaterial(pos);
        final Block block = WBlock.getBlock(pos);
        return (!material.blocksMovement() || block instanceof BlockSign) && !(block instanceof BlockTripWire) && !(block instanceof BlockPressurePlate) && (this.invulnerable || (material != Material.LAVA && material != Material.FIRE));
    }
    
    private boolean canGoAbove(final BlockPos pos) {
        final Block block = WBlock.getBlock(pos);
        return !(block instanceof BlockFence) && !(block instanceof BlockWall) && !(block instanceof BlockFenceGate);
    }
    
    private boolean canSafelyStandOn(final BlockPos pos) {
        final Material material = WBlock.getMaterial(pos);
        return this.canBeSolid(pos) && (this.invulnerable || (material != Material.CACTUS && material != Material.LAVA));
    }
    
    private boolean canFallBelow(final PathPos pos) {
        final BlockPos down2 = pos.down(2);
        if (this.fallingAllowed && this.canGoThrough(down2)) {
            return true;
        }
        if (!this.canSafelyStandOn(down2)) {
            return false;
        }
        if (this.immuneToFallDamage && this.fallingAllowed) {
            return true;
        }
        if (WBlock.getBlock(down2) instanceof BlockSlime && this.fallingAllowed) {
            return true;
        }
        BlockPos prevPos = pos;
        for (int i = 0; i <= (this.fallingAllowed ? 3 : 1); ++i) {
            if (prevPos == null) {
                return true;
            }
            if (!pos.up(i).equals(prevPos)) {
                return true;
            }
            final Block prevBlock = WBlock.getBlock(prevPos);
            if (prevBlock instanceof BlockLiquid || prevBlock instanceof BlockLadder || prevBlock instanceof BlockVine || prevBlock instanceof BlockWeb) {
                return true;
            }
            prevPos = this.prevPosMap.get(prevPos);
        }
        return false;
    }
    
    private boolean canFlyAt(final BlockPos pos) {
        return this.flying || (!this.noWaterSlowdown && WBlock.getMaterial(pos) == Material.WATER);
    }
    
    private boolean canClimbUpAt(final BlockPos pos) {
        final Block block = WBlock.getBlock(pos);
        if (!this.spider && !(block instanceof BlockLadder) && !(block instanceof BlockVine)) {
            return false;
        }
        final BlockPos up = pos.up();
        return this.canBeSolid(pos.north()) || this.canBeSolid(pos.east()) || this.canBeSolid(pos.south()) || this.canBeSolid(pos.west()) || this.canBeSolid(up.north()) || this.canBeSolid(up.east()) || this.canBeSolid(up.south()) || this.canBeSolid(up.west());
    }
    
    private boolean canMoveSidewaysInMidairAt(final BlockPos pos) {
        final Block blockFeet = WBlock.getBlock(pos);
        if (blockFeet instanceof BlockLiquid || blockFeet instanceof BlockLadder || blockFeet instanceof BlockVine || blockFeet instanceof BlockWeb) {
            return true;
        }
        final Block blockHead = WBlock.getBlock(pos.up());
        return blockHead instanceof BlockLiquid || blockHead instanceof BlockWeb;
    }
    
    private float getCost(final BlockPos current, final BlockPos next) {
        final float[] costs = { 0.5f, 0.5f };
        final BlockPos[] positions = { current, next };
        for (int i = 0; i < positions.length; ++i) {
            final Material material = WBlock.getMaterial(positions[i]);
            if (material == Material.WATER && !this.noWaterSlowdown) {
                final float[] array = costs;
                final int n = i;
                array[n] *= 1.3164438f;
            }
            else if (material == Material.LAVA) {
                final float[] array2 = costs;
                final int n2 = i;
                array2[n2] *= 4.5395155f;
            }
            if (!this.canFlyAt(positions[i]) && WBlock.getBlock(positions[i].down()) instanceof BlockSoulSand) {
                final float[] array3 = costs;
                final int n3 = i;
                array3[n3] *= 2.5f;
            }
        }
        float cost = costs[0] + costs[1];
        if (current.getX() != next.getX() && current.getZ() != next.getZ()) {
            cost *= 1.4142135f;
        }
        return cost;
    }
    
    private float getHeuristic(final BlockPos pos) {
        final float dx = (float)Math.abs(pos.getX() - this.goal.getX());
        final float dy = (float)Math.abs(pos.getY() - this.goal.getY());
        final float dz = (float)Math.abs(pos.getZ() - this.goal.getZ());
        return 1.001f * (dx + dy + dz - 0.58578646f * Math.min(dx, dz));
    }
    
    public PathPos getCurrentPos() {
        return this.current;
    }
    
    public BlockPos getGoal() {
        return this.goal;
    }
    
    public int countProcessedBlocks() {
        return this.prevPosMap.keySet().size();
    }
    
    public int getQueueSize() {
        return this.queue.size();
    }
    
    public float getCost(final BlockPos pos) {
        return this.costMap.get(pos);
    }
    
    public boolean isDone() {
        return this.done;
    }
    
    public boolean isFailed() {
        return this.failed;
    }
    
    public ArrayList<PathPos> formatPath() {
        if (!this.done && !this.failed) {
            throw new IllegalStateException("No path found!");
        }
        if (!this.path.isEmpty()) {
            throw new IllegalStateException("Path was already formatted!");
        }
        PathPos pos;
        if (!this.failed) {
            pos = this.current;
        }
        else {
            pos = this.start;
            for (final PathPos next : this.prevPosMap.keySet()) {
                if (this.getHeuristic(next) < this.getHeuristic(pos) && (this.canFlyAt(next) || this.canBeSolid(next.down()))) {
                    pos = next;
                }
            }
        }
        while (pos != null) {
            this.path.add(pos);
            pos = this.prevPosMap.get(pos);
        }
        Collections.reverse(this.path);
        return this.path;
    }
    
    public void renderPath(final boolean debugMode, final boolean depthTest) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        if (!depthTest) {
            GL11.glDisable(2929);
        }
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glTranslated(-Minecraft.getMinecraft().getRenderManager().renderPosX, -Minecraft.getMinecraft().getRenderManager().renderPosY, -Minecraft.getMinecraft().getRenderManager().renderPosZ);
        GL11.glTranslated(0.5, 0.5, 0.5);
        if (debugMode) {
            int renderedThings = 0;
            GL11.glLineWidth(2.0f);
            GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.75f);
            PathPos[] array;
            for (int length = (array = this.queue.toArray()).length, j = 0; j < length; ++j) {
                final PathPos element = array[j];
                if (renderedThings >= 5000) {
                    break;
                }
                PathRenderer.renderNode(element);
                ++renderedThings;
            }
            GL11.glLineWidth(2.0f);
            for (final Map.Entry<PathPos, PathPos> entry : this.prevPosMap.entrySet()) {
                if (renderedThings >= 5000) {
                    break;
                }
                if (entry.getKey().isJumping()) {
                    GL11.glColor4f(1.0f, 0.0f, 1.0f, 0.75f);
                }
                else {
                    GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.75f);
                }
                PathRenderer.renderArrow(entry.getValue(), entry.getKey());
                ++renderedThings;
            }
        }
        if (debugMode) {
            GL11.glLineWidth(4.0f);
            GL11.glColor4f(0.0f, 0.0f, 1.0f, 0.75f);
        }
        else {
            GL11.glLineWidth(2.0f);
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.75f);
        }
        for (int i = 0; i < this.path.size() - 1; ++i) {
            PathRenderer.renderArrow(this.path.get(i), this.path.get(i + 1));
        }
        GL11.glPopMatrix();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
    }
    
    public boolean isPathStillValid(final int index) {
        if (this.path.isEmpty()) {
            throw new IllegalStateException("Path is not formatted!");
        }
        if (this.invulnerable != WMinecraft.getPlayer().abilities.creativeMode || this.flying != (this.creativeFlying || this.wurst.hax.flightMod.isActive()) || this.immuneToFallDamage != (this.invulnerable || this.wurst.hax.noFallMod.isActive()) || this.noWaterSlowdown != this.wurst.hax.antiWaterPushHack.isPreventingSlowdown() || this.jesus != this.wurst.hax.jesusMod.isActive() || this.spider != this.wurst.hax.spiderMod.isActive()) {
            return false;
        }
        if (index == 0) {
            final PathPos pos = this.path.get(0);
            if (!this.isPassable(pos) || (!this.canFlyAt(pos) && !this.canGoThrough(pos.down()) && !this.canSafelyStandOn(pos.down()))) {
                return false;
            }
        }
        for (int i = Math.max(1, index); i < this.path.size(); ++i) {
            if (!this.getNeighbors(this.path.get(i - 1)).contains(this.path.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public PathProcessor getProcessor() {
        if (this.flying) {
            return new FlyPathProcessor(this.path, this.creativeFlying);
        }
        return new WalkPathProcessor(this.path);
    }
    
    public void setThinkSpeed(final int thinkSpeed) {
        this.thinkSpeed = thinkSpeed;
    }
    
    public void setThinkTime(final int thinkTime) {
        this.thinkTime = thinkTime;
    }
    
    public void setFallingAllowed(final boolean fallingAllowed) {
        this.fallingAllowed = fallingAllowed;
    }
    
    public void setDivingAllowed(final boolean divingAllowed) {
        this.divingAllowed = divingAllowed;
    }
}
