// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util;

import net.minecraft.util.math.Box;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.util.math.Vec3d;

public class RotationUtils
{
    private static boolean fakeRotation;
    private static float serverYaw;
    private static float serverPitch;
    
    public static Vec3d getEyesPos() {
        return new Vec3d(WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY + WMinecraft.getPlayer().getEyeHeight(), WMinecraft.getPlayer().posZ);
    }
    
    public static Vec3d getClientLookVec() {
        final float f = WMath.cos(-WMinecraft.getPlayer().rotationYaw * 0.017453292f - 3.1415927f);
        final float f2 = WMath.sin(-WMinecraft.getPlayer().rotationYaw * 0.017453292f - 3.1415927f);
        final float f3 = -WMath.cos(-WMinecraft.getPlayer().rotationPitch * 0.017453292f);
        final float f4 = WMath.sin(-WMinecraft.getPlayer().rotationPitch * 0.017453292f);
        return new Vec3d(f2 * f3, f4, f * f3);
    }
    
    public static Vec3d getServerLookVec() {
        final float f = WMath.cos(-RotationUtils.serverYaw * 0.017453292f - 3.1415927f);
        final float f2 = WMath.sin(-RotationUtils.serverYaw * 0.017453292f - 3.1415927f);
        final float f3 = -WMath.cos(-RotationUtils.serverPitch * 0.017453292f);
        final float f4 = WMath.sin(-RotationUtils.serverPitch * 0.017453292f);
        return new Vec3d(f2 * f3, f4, f * f3);
    }
    
    private static float[] getNeededRotations(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { WMath.wrapDegrees(yaw), WMath.wrapDegrees(pitch) };
    }
    
    private static float[] getNeededRotations2(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { WMinecraft.getPlayer().rotationYaw + WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw), WMinecraft.getPlayer().rotationPitch + WMath.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch) };
    }
    
    public static double getAngleToLastReportedLookVec(final Vec3d vec) {
        final float[] needed = getNeededRotations(vec);
        final EntityPlayerSP player = WMinecraft.getPlayer();
        final float lastReportedYaw = WMath.wrapDegrees(player.lastReportedYaw);
        final float lastReportedPitch = WMath.wrapDegrees(player.lastReportedPitch);
        final float diffYaw = lastReportedYaw - needed[0];
        final float diffPitch = lastReportedPitch - needed[1];
        return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
    }
    
    public static float limitAngleChange(final float current, final float intended, final float maxChange) {
        float change = WMath.wrapDegrees(intended - current);
        change = WMath.clamp(change, -maxChange, maxChange);
        return WMath.wrapDegrees(current + change);
    }
    
    public static boolean faceVectorPacket(final Vec3d vec) {
        RotationUtils.fakeRotation = true;
        final float[] rotations = getNeededRotations(vec);
        RotationUtils.serverYaw = limitAngleChange(RotationUtils.serverYaw, rotations[0], 30.0f);
        RotationUtils.serverPitch = rotations[1];
        return Math.abs(RotationUtils.serverYaw - rotations[0]) < 1.0f;
    }
    
    public static void faceVectorPacketInstant(final Vec3d vec) {
        final float[] rotations = getNeededRotations2(vec);
        WConnection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], WMinecraft.getPlayer().onGround));
    }
    
    public static boolean faceVectorClient(final Vec3d vec) {
        final float[] rotations = getNeededRotations(vec);
        final float oldYaw = WMinecraft.getPlayer().prevRotationYaw;
        final float oldPitch = WMinecraft.getPlayer().prevRotationPitch;
        WMinecraft.getPlayer().rotationYaw = limitAngleChange(oldYaw, rotations[0], 30.0f);
        WMinecraft.getPlayer().rotationPitch = rotations[1];
        return Math.abs(oldYaw - rotations[0]) + Math.abs(oldPitch - rotations[1]) < 1.0f;
    }
    
    public static boolean faceEntityClient(final Entity entity) {
        final Vec3d eyesPos = getEyesPos();
        final Vec3d lookVec = getServerLookVec();
        final Box bb = entity.boundingBox;
        return faceVectorClient(bb.getCenter()) || bb.calculateIntercept(eyesPos, eyesPos.add(lookVec.scale(6.0))) != null;
    }
    
    public static boolean faceEntityPacket(final Entity entity) {
        final Vec3d eyesPos = getEyesPos();
        final Vec3d lookVec = getServerLookVec();
        final Box bb = entity.boundingBox;
        return faceVectorPacket(bb.getCenter()) || bb.calculateIntercept(eyesPos, eyesPos.add(lookVec.scale(6.0))) != null;
    }
    
    public static boolean faceVectorForWalking(final Vec3d vec) {
        final float[] rotations = getNeededRotations(vec);
        final float oldYaw = WMinecraft.getPlayer().prevRotationYaw;
        WMinecraft.getPlayer().rotationYaw = limitAngleChange(oldYaw, rotations[0], 30.0f);
        WMinecraft.getPlayer().rotationPitch = 0.0f;
        return Math.abs(oldYaw - rotations[0]) < 1.0f;
    }
    
    public static float getAngleToClientRotation(final Vec3d vec) {
        final float[] needed = getNeededRotations(vec);
        final float diffYaw = WMath.wrapDegrees(WMinecraft.getPlayer().rotationYaw) - needed[0];
        final float diffPitch = WMath.wrapDegrees(WMinecraft.getPlayer().rotationPitch) - needed[1];
        final float angle = (float)Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
        return angle;
    }
    
    public static float getHorizontalAngleToClientRotation(final Vec3d vec) {
        final float[] needed = getNeededRotations(vec);
        final float angle = WMath.wrapDegrees(WMinecraft.getPlayer().rotationYaw) - needed[0];
        return angle;
    }
    
    public static float getAngleToServerRotation(final Vec3d vec) {
        final float[] needed = getNeededRotations(vec);
        final float diffYaw = RotationUtils.serverYaw - needed[0];
        final float diffPitch = RotationUtils.serverPitch - needed[1];
        final float angle = (float)Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
        return angle;
    }
    
    public static void updateServerRotation() {
        if (RotationUtils.fakeRotation) {
            RotationUtils.fakeRotation = false;
            return;
        }
        RotationUtils.serverYaw = limitAngleChange(RotationUtils.serverYaw, WMinecraft.getPlayer().rotationYaw, 30.0f);
        RotationUtils.serverPitch = WMinecraft.getPlayer().rotationPitch;
    }
    
    public static float getServerYaw() {
        return RotationUtils.serverYaw;
    }
    
    public static float getServerPitch() {
        return RotationUtils.serverPitch;
    }
}
