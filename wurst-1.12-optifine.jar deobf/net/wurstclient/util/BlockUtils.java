// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util;

import net.minecraft.block.material.Material;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayDeque;
import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.Minecraft;

public final class BlockUtils
{
    private static final Minecraft mc;
    
    static {
        mc = Minecraft.getMinecraft();
    }
    
    public static IBlockState getState(final BlockPos pos) {
        return WBlock.getState(pos);
    }
    
    public static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }
    
    public static String getName(final BlockPos pos) {
        return getName(getBlock(pos));
    }
    
    public static String getName(final Block block) {
        return WBlock.getName(block);
    }
    
    public static Block getBlockFromName(final String name) {
        try {
            return Block.getBlockFromName(name);
        }
        catch (final Exception e) {
            return Blocks.AIR;
        }
    }
    
    public static boolean placeBlockLegit(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final BlockPos neighbor = pos.offset(side);
            if (WBlock.canBeClicked(neighbor)) {
                final Vec3d dirVec = new Vec3d(side.getDirectionVec());
                final Vec3d hitVec = posVec.add(dirVec.scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= 18.0625) {
                    if (distanceSqPosVec <= eyesPos.squareDistanceTo(posVec.add(dirVec))) {
                        if (WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                            RotationUtils.faceVectorPacketInstant(hitVec);
                            WPlayerController.processRightClickBlock(neighbor, side.getOpposite(), hitVec);
                            WPlayer.swingArmClient();
                            BlockUtils.mc.rightClickDelayTimer = 4;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean placeBlockScaffold(final BlockPos pos) {
        final Vec3d eyesPos = new Vec3d(WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY + WMinecraft.getPlayer().getEyeHeight(), WMinecraft.getPlayer().posZ);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (eyesPos.squareDistanceTo(new Vec3d(pos).addVector(0.5, 0.5, 0.5)) < eyesPos.squareDistanceTo(new Vec3d(neighbor).addVector(0.5, 0.5, 0.5))) {
                if (WBlock.canBeClicked(neighbor)) {
                    final Vec3d hitVec = new Vec3d(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                    if (eyesPos.squareDistanceTo(hitVec) <= 18.0625) {
                        RotationUtils.faceVectorPacketInstant(hitVec);
                        WPlayerController.processRightClickBlock(neighbor, side2, hitVec);
                        WPlayer.swingArmClient();
                        BlockUtils.mc.rightClickDelayTimer = 4;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static void placeBlockSimple(final BlockPos pos) {
        EnumFacing side = null;
        final EnumFacing[] sides = EnumFacing.values();
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        final Vec3d[] hitVecs = new Vec3d[sides.length];
        for (int i = 0; i < sides.length; ++i) {
            hitVecs[i] = posVec.add(new Vec3d(sides[i].getDirectionVec()).scale(0.5));
        }
        for (int i = 0; i < sides.length; ++i) {
            if (WBlock.canBeClicked(pos.offset(sides[i]))) {
                if (WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVecs[i], false, true, false) == null) {
                    side = sides[i];
                    break;
                }
            }
        }
        if (side == null) {
            for (int i = 0; i < sides.length; ++i) {
                if (WBlock.canBeClicked(pos.offset(sides[i]))) {
                    if (distanceSqPosVec <= eyesPos.squareDistanceTo(hitVecs[i])) {
                        side = sides[i];
                        break;
                    }
                }
            }
        }
        if (side == null) {
            return;
        }
        final Vec3d hitVec = hitVecs[side.ordinal()];
        RotationUtils.faceVectorPacket(hitVec);
        if (RotationUtils.getAngleToLastReportedLookVec(hitVec) > 1.0) {
            return;
        }
        if (BlockUtils.mc.rightClickDelayTimer > 0) {
            return;
        }
        WPlayerController.processRightClickBlock(pos.offset(side), side.getOpposite(), hitVec);
        WPlayer.swingArmPacket();
        BlockUtils.mc.rightClickDelayTimer = 4;
    }
    
    public static boolean placeBlockSimple_old(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final BlockPos neighbor = pos.offset(side);
            if (WBlock.canBeClicked(neighbor)) {
                final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                    WPlayerController.processRightClickBlock(neighbor, side.getOpposite(), hitVec);
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean prepareToBreakBlockLegit(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            final double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (distanceSqHitVec <= 18.0625) {
                if (distanceSqHitVec < distanceSqPosVec) {
                    if (WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                        return RotationUtils.faceVectorPacket(hitVec) || true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean breakBlockLegit(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            final double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (distanceSqHitVec <= 18.0625) {
                if (distanceSqHitVec < distanceSqPosVec) {
                    if (WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                        if (!BlockUtils.mc.playerController.onPlayerDamageBlock(pos, side)) {
                            return false;
                        }
                        WPlayer.swingArmPacket();
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean breakBlockExtraLegit(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            final double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (distanceSqHitVec <= 18.0625) {
                if (distanceSqHitVec < distanceSqPosVec) {
                    if (WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                        if (!RotationUtils.faceVectorClient(hitVec)) {
                            return true;
                        }
                        if (BlockUtils.mc.gameSettings.keyBindAttack.pressed && !BlockUtils.mc.playerController.getIsHittingBlock()) {
                            BlockUtils.mc.gameSettings.keyBindAttack.pressed = false;
                            return true;
                        }
                        return BlockUtils.mc.gameSettings.keyBindAttack.pressed = true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean breakBlockSimple(final BlockPos pos) {
        EnumFacing side = null;
        final EnumFacing[] sides = EnumFacing.values();
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d relCenter = WBlock.getBoundingBox(pos).offset(-pos.getX(), -pos.getY(), -pos.getZ()).getCenter();
        final Vec3d center = new Vec3d(pos).add(relCenter);
        final Vec3d[] hitVecs = new Vec3d[sides.length];
        for (int i = 0; i < sides.length; ++i) {
            final Vec3i dirVec = sides[i].getDirectionVec();
            final Vec3d relHitVec = new Vec3d(relCenter.xCoord * dirVec.getX(), relCenter.yCoord * dirVec.getY(), relCenter.zCoord * dirVec.getZ());
            hitVecs[i] = center.add(relHitVec);
        }
        for (int i = 0; i < sides.length; ++i) {
            if (WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVecs[i], false, true, false) == null) {
                side = sides[i];
                break;
            }
        }
        if (side == null) {
            final double distanceSqToCenter = eyesPos.squareDistanceTo(center);
            for (int j = 0; j < sides.length; ++j) {
                if (eyesPos.squareDistanceTo(hitVecs[j]) < distanceSqToCenter) {
                    side = sides[j];
                    break;
                }
            }
        }
        if (side == null) {
            throw new RuntimeException("How could none of the sides be facing towards the player?!");
        }
        RotationUtils.faceVectorPacket(hitVecs[side.ordinal()]);
        if (!BlockUtils.mc.playerController.onPlayerDamageBlock(pos, side)) {
            return false;
        }
        WPlayer.swingArmPacket();
        return true;
    }
    
    public static boolean breakBlockSimple_old(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            final double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (distanceSqHitVec <= 36.0) {
                if (distanceSqHitVec < distanceSqPosVec) {
                    RotationUtils.faceVectorPacket(hitVec);
                    if (!BlockUtils.mc.playerController.onPlayerDamageBlock(pos, side)) {
                        return false;
                    }
                    WPlayer.swingArmPacket();
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void breakBlockPacketSpam(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            if (eyesPos.squareDistanceTo(hitVec) < distanceSqPosVec) {
                WConnection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
                WConnection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
                return;
            }
        }
    }
    
    public static void breakBlocksPacketSpam(final Iterable<BlockPos> blocks) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        for (final BlockPos pos : blocks) {
            final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
            final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
            EnumFacing[] values;
            for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
                final EnumFacing side = values[i];
                final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) < distanceSqPosVec) {
                    WConnection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
                    WConnection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
                    break;
                }
            }
        }
    }
    
    public static boolean rightClickBlockLegit(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            final double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (distanceSqHitVec <= 18.0625) {
                if (distanceSqHitVec < distanceSqPosVec) {
                    if (WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                        if (!RotationUtils.faceVectorPacket(hitVec)) {
                            return true;
                        }
                        WPlayerController.processRightClickBlock(pos, side, hitVec);
                        WPlayer.swingArmClient();
                        BlockUtils.mc.rightClickDelayTimer = 4;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean rightClickBlockSimple(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            final double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (distanceSqHitVec <= 36.0) {
                if (distanceSqHitVec < distanceSqPosVec) {
                    WPlayerController.processRightClickBlock(pos, side, hitVec);
                    return true;
                }
            }
        }
        return false;
    }
    
    public static Iterable<BlockPos> getValidBlocksByDistance(final double range, final boolean ignoreVisibility, final BlockValidator validator) {
        final Vec3d eyesPos = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
        final double rangeSq = Math.pow(range + 0.5, 2.0);
        final BlockPos startPos = new BlockPos(RotationUtils.getEyesPos());
        return () -> new AbstractIterator<BlockPos>(blockPos) {
            private ArrayDeque<BlockPos> queue;
            private HashSet<BlockPos> visited;
            private final /* synthetic */ Vec3d val$eyesPos;
            private final /* synthetic */ double val$rangeSq;
            private final /* synthetic */ boolean val$ignoreVisibility;
            private final /* synthetic */ BlockValidator val$validator;
            
            {
                this.queue = new ArrayDeque<BlockPos>(Arrays.asList(blockPos));
                this.visited = new HashSet<BlockPos>();
            }
            
            protected BlockPos computeNext() {
                while (!this.queue.isEmpty()) {
                    final BlockPos current = this.queue.pop();
                    if (this.val$eyesPos.squareDistanceTo(new Vec3d(current)) > this.val$rangeSq) {
                        continue;
                    }
                    final boolean canBeClicked = WBlock.canBeClicked(current);
                    if (this.val$ignoreVisibility || !canBeClicked) {
                        EnumFacing[] values;
                        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
                            final EnumFacing facing = values[i];
                            final BlockPos next = current.offset(facing);
                            if (!this.visited.contains(next)) {
                                this.queue.add(next);
                                this.visited.add(next);
                            }
                        }
                    }
                    if (canBeClicked && this.val$validator.isValid(current)) {
                        return current;
                    }
                }
                return (BlockPos)this.endOfData();
            }
        };
    }
    
    public static Iterable<BlockPos> getValidBlocksByDistanceReversed(final double range, final boolean ignoreVisibility, final BlockValidator validator) {
        final ArrayDeque<BlockPos> validBlocks = new ArrayDeque<BlockPos>();
        getValidBlocksByDistance(range, ignoreVisibility, validator).forEach(p -> arrayDeque.push(p));
        return validBlocks;
    }
    
    public static Iterable<BlockPos> getValidBlocks(final double range, final BlockValidator validator) {
        final Vec3d eyesPos = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
        final double rangeSq = Math.pow(range + 0.5, 2.0);
        return getValidBlocks((int)Math.ceil(range), pos -> vec3d.squareDistanceTo(new Vec3d(pos)) <= n && blockValidator.isValid(pos));
    }
    
    public static Iterable<BlockPos> getValidBlocks(final int blockRange, final BlockValidator validator) {
        final BlockPos playerPos = new BlockPos(RotationUtils.getEyesPos());
        final BlockPos min = playerPos.add(-blockRange, -blockRange, -blockRange);
        final BlockPos max = playerPos.add(blockRange, blockRange, blockRange);
        return () -> new AbstractIterator<BlockPos>() {
            private BlockPos last;
            private final /* synthetic */ BlockPos val$min;
            private final /* synthetic */ BlockPos val$max;
            private final /* synthetic */ BlockValidator val$validator;
            
            private BlockPos computeNextUnchecked() {
                if (this.last == null) {
                    return this.last = this.val$min;
                }
                int x = this.last.getX();
                int y = this.last.getY();
                int z = this.last.getZ();
                if (z < this.val$max.getZ()) {
                    ++z;
                }
                else if (x < this.val$max.getX()) {
                    z = this.val$min.getZ();
                    ++x;
                }
                else {
                    if (y >= this.val$max.getY()) {
                        return null;
                    }
                    z = this.val$min.getZ();
                    x = this.val$min.getX();
                    ++y;
                }
                return this.last = new BlockPos(x, y, z);
            }
            
            protected BlockPos computeNext() {
                BlockPos pos;
                while ((pos = this.computeNextUnchecked()) != null) {
                    if (WBlock.getMaterial(pos) == Material.AIR) {
                        continue;
                    }
                    if (!this.val$validator.isValid(pos)) {
                        continue;
                    }
                    return pos;
                }
                return (BlockPos)this.endOfData();
            }
        };
    }
    
    public interface BlockValidator
    {
        boolean isValid(final BlockPos p0);
    }
}
