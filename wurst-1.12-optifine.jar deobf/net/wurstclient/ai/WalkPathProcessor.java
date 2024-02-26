// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockLadder;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.util.RotationUtils;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMinecraft;
import java.util.ArrayList;

public class WalkPathProcessor extends PathProcessor
{
    public WalkPathProcessor(final ArrayList<PathPos> path) {
        super(path);
    }
    
    @Override
    public void process() {
        BlockPos pos;
        if (WMinecraft.getPlayer().onGround) {
            pos = new BlockPos(WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY + 0.5, WMinecraft.getPlayer().posZ);
        }
        else {
            pos = new BlockPos(WMinecraft.getPlayer());
        }
        final PathPos nextPos = this.path.get(this.index);
        final int posIndex = this.path.indexOf(pos);
        if (posIndex == -1) {
            ++this.ticksOffPath;
        }
        else {
            this.ticksOffPath = 0;
        }
        if (pos.equals(nextPos)) {
            ++this.index;
            if (this.index >= this.path.size()) {
                this.done = true;
            }
            return;
        }
        if (posIndex > this.index) {
            this.index = posIndex + 1;
            if (this.index >= this.path.size()) {
                this.done = true;
            }
            return;
        }
        lockControls();
        WMinecraft.getPlayer().abilities.isFlying = false;
        this.facePosition(nextPos);
        if (WMath.wrapDegrees(Math.abs(RotationUtils.getHorizontalAngleToClientRotation(new Vec3d(nextPos).addVector(0.5, 0.5, 0.5)))) > 90.0f) {
            return;
        }
        if (WalkPathProcessor.wurst.hax.jesusMod.isActive()) {
            if (WMinecraft.getPlayer().posY < nextPos.getY() && (WMinecraft.getPlayer().isInWater() || WMinecraft.getPlayer().isInLava())) {
                return;
            }
            if (WMinecraft.getPlayer().posY - nextPos.getY() > 0.5 && (WMinecraft.getPlayer().isInWater() || WMinecraft.getPlayer().isInLava() || WalkPathProcessor.wurst.hax.jesusMod.isOverLiquid())) {
                WalkPathProcessor.mc.gameSettings.keyBindSneak.pressed = true;
            }
        }
        if (pos.getX() != nextPos.getX() || pos.getZ() != nextPos.getZ()) {
            WalkPathProcessor.mc.gameSettings.keyBindForward.pressed = true;
            if ((this.index > 0 && this.path.get(this.index - 1).isJumping()) || pos.getY() < nextPos.getY()) {
                WalkPathProcessor.mc.gameSettings.keyBindJump.pressed = true;
            }
        }
        else if (pos.getY() != nextPos.getY()) {
            if (pos.getY() < nextPos.getY()) {
                final Block block = WBlock.getBlock(pos);
                if (block instanceof BlockLadder || block instanceof BlockVine) {
                    RotationUtils.faceVectorForWalking(WBlock.getBoundingBox(pos).getCenter());
                    WalkPathProcessor.mc.gameSettings.keyBindForward.pressed = true;
                }
                else {
                    if (this.index < this.path.size() - 1 && !nextPos.up().equals(this.path.get(this.index + 1))) {
                        ++this.index;
                    }
                    WalkPathProcessor.mc.gameSettings.keyBindJump.pressed = true;
                }
            }
            else {
                while (this.index < this.path.size() - 1 && this.path.get(this.index).down().equals(this.path.get(this.index + 1))) {
                    ++this.index;
                }
                if (WMinecraft.getPlayer().onGround) {
                    WalkPathProcessor.mc.gameSettings.keyBindForward.pressed = true;
                }
            }
        }
    }
}
