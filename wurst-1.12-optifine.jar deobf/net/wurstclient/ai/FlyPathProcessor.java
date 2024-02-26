// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.ai;

import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.util.RotationUtils;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMinecraft;
import java.util.ArrayList;

public class FlyPathProcessor extends PathProcessor
{
    private final boolean creativeFlying;
    
    public FlyPathProcessor(final ArrayList<PathPos> path, final boolean creativeFlying) {
        super(path);
        this.creativeFlying = creativeFlying;
    }
    
    @Override
    public void process() {
        final BlockPos pos = new BlockPos(WMinecraft.getPlayer());
        final Vec3d posVec = WMinecraft.getPlayer().getPositionVector();
        final BlockPos nextPos = this.path.get(this.index);
        final int posIndex = this.path.indexOf(pos);
        final Box nextBox = new Box(nextPos.getX() + 0.3, nextPos.getY(), nextPos.getZ() + 0.3, nextPos.getX() + 0.7, nextPos.getY() + 0.2, nextPos.getZ() + 0.7);
        if (posIndex == -1) {
            ++this.ticksOffPath;
        }
        else {
            this.ticksOffPath = 0;
        }
        if (posIndex > this.index || (posVec.xCoord >= nextBox.minX && posVec.xCoord <= nextBox.maxX && posVec.yCoord >= nextBox.minY && posVec.yCoord <= nextBox.maxY && posVec.zCoord >= nextBox.minZ && posVec.zCoord <= nextBox.maxZ)) {
            if (posIndex > this.index) {
                this.index = posIndex + 1;
            }
            else {
                ++this.index;
            }
            if (this.creativeFlying) {
                final EntityPlayerSP player = WMinecraft.getPlayer();
                player.motionX /= Math.max(Math.abs(WMinecraft.getPlayer().motionX) * 50.0, 1.0);
                final EntityPlayerSP player2 = WMinecraft.getPlayer();
                player2.motionY /= Math.max(Math.abs(WMinecraft.getPlayer().motionY) * 50.0, 1.0);
                final EntityPlayerSP player3 = WMinecraft.getPlayer();
                player3.motionZ /= Math.max(Math.abs(WMinecraft.getPlayer().motionZ) * 50.0, 1.0);
            }
            if (this.index >= this.path.size()) {
                this.done = true;
            }
            return;
        }
        lockControls();
        WMinecraft.getPlayer().abilities.isFlying = this.creativeFlying;
        final boolean x = posVec.xCoord < nextBox.minX || posVec.xCoord > nextBox.maxX;
        final boolean y = posVec.yCoord < nextBox.minY || posVec.yCoord > nextBox.maxY;
        final boolean z = posVec.zCoord < nextBox.minZ || posVec.zCoord > nextBox.maxZ;
        final boolean horizontal = x || z;
        if (horizontal) {
            this.facePosition(nextPos);
            if (Math.abs(WMath.wrapDegrees(RotationUtils.getHorizontalAngleToClientRotation(new Vec3d(nextPos).addVector(0.5, 0.5, 0.5)))) > 1.0f) {
                return;
            }
        }
        final Vec3i offset = nextPos.subtract(pos);
        while (this.index < this.path.size() - 1 && this.path.get(this.index).add(offset).equals(this.path.get(this.index + 1))) {
            ++this.index;
        }
        if (this.creativeFlying) {
            if (!x) {
                final EntityPlayerSP player4 = WMinecraft.getPlayer();
                player4.motionX /= Math.max(Math.abs(WMinecraft.getPlayer().motionX) * 50.0, 1.0);
            }
            if (!y) {
                final EntityPlayerSP player5 = WMinecraft.getPlayer();
                player5.motionY /= Math.max(Math.abs(WMinecraft.getPlayer().motionY) * 50.0, 1.0);
            }
            if (!z) {
                final EntityPlayerSP player6 = WMinecraft.getPlayer();
                player6.motionZ /= Math.max(Math.abs(WMinecraft.getPlayer().motionZ) * 50.0, 1.0);
            }
        }
        if (horizontal) {
            if (!this.creativeFlying && WMinecraft.getPlayer().getDistance(nextPos.getX() + 0.5, pos.getY() + 0.1, nextPos.getZ() + 0.5) <= FlyPathProcessor.wurst.hax.flightMod.speed.getValueF()) {
                WMinecraft.getPlayer().setPosition(nextPos.getX() + 0.5, pos.getY() + 0.1, nextPos.getZ() + 0.5);
                return;
            }
            FlyPathProcessor.mc.gameSettings.keyBindForward.pressed = true;
            if (WMinecraft.getPlayer().isCollidedHorizontally) {
                if (posVec.yCoord > nextBox.maxY) {
                    FlyPathProcessor.mc.gameSettings.keyBindSneak.pressed = true;
                }
                else if (posVec.yCoord < nextBox.minY) {
                    FlyPathProcessor.mc.gameSettings.keyBindJump.pressed = true;
                }
            }
        }
        else if (y) {
            if (!this.creativeFlying && WMinecraft.getPlayer().getDistance(pos.getX() + 0.5, nextPos.getY() + 0.1, pos.getZ() + 0.5) <= FlyPathProcessor.wurst.hax.flightMod.speed.getValueF()) {
                WMinecraft.getPlayer().setPosition(pos.getX() + 0.5, nextPos.getY() + 0.1, pos.getZ() + 0.5);
                return;
            }
            if (posVec.yCoord < nextBox.minY) {
                FlyPathProcessor.mc.gameSettings.keyBindJump.pressed = true;
            }
            else {
                FlyPathProcessor.mc.gameSettings.keyBindSneak.pressed = true;
            }
            if (WMinecraft.getPlayer().isCollidedVertically) {
                FlyPathProcessor.mc.gameSettings.keyBindSneak.pressed = false;
                FlyPathProcessor.mc.gameSettings.keyBindForward.pressed = true;
            }
        }
    }
}
