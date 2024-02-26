// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.WurstClient;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.PacketOutputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "WaterWalking", "water walking" })
@Bypasses(ghostMode = false)
public final class JesusMod extends Hack implements UpdateListener, PacketOutputListener
{
    private int tickTimer;
    private int packetTimer;
    
    public JesusMod() {
        super("Jesus", "Allows you to walk on water.\nThe real Jesus used this hack ~2000 years ago.");
        this.tickTimer = 10;
        this.packetTimer = 0;
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        WurstClient.INSTANCE.events.add(UpdateListener.class, this);
        WurstClient.INSTANCE.events.add(PacketOutputListener.class, this);
    }
    
    @Override
    public void onDisable() {
        WurstClient.INSTANCE.events.remove(UpdateListener.class, this);
        WurstClient.INSTANCE.events.remove(PacketOutputListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (JesusMod.MC.gameSettings.keyBindSneak.pressed) {
            return;
        }
        if (JesusMod.MC.player.isInWater()) {
            JesusMod.MC.player.motionY = 0.11;
            this.tickTimer = 0;
            return;
        }
        if (this.tickTimer == 0) {
            JesusMod.MC.player.motionY = 0.3;
        }
        else if (this.tickTimer == 1) {
            JesusMod.MC.player.motionY = 0.0;
        }
        ++this.tickTimer;
    }
    
    @Override
    public void onSentPacket(final PacketOutputEvent event) {
        if (!(event.getPacket() instanceof CPacketPlayer)) {
            return;
        }
        final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
        if (!(packet instanceof CPacketPlayer.Position) && !(packet instanceof CPacketPlayer.PositionRotation)) {
            return;
        }
        if (JesusMod.MC.player.isInWater()) {
            return;
        }
        if (JesusMod.MC.player.fallDistance > 3.0f) {
            return;
        }
        if (!this.isOverLiquid()) {
            return;
        }
        if (JesusMod.MC.player.movementInput == null) {
            event.cancel();
            return;
        }
        ++this.packetTimer;
        if (this.packetTimer < 4) {
            return;
        }
        event.cancel();
        final double x = packet.getX(0.0);
        double y = packet.getY(0.0);
        final double z = packet.getZ(0.0);
        if (JesusMod.MC.player.ticksExisted % 2 == 0) {
            y -= 0.05;
        }
        else {
            y += 0.05;
        }
        Packet newPacket;
        if (packet instanceof CPacketPlayer.Position) {
            newPacket = new CPacketPlayer.Position(x, y, z, true);
        }
        else {
            newPacket = new CPacketPlayer.PositionRotation(x, y, z, packet.getYaw(0.0f), packet.getPitch(0.0f), true);
        }
        WConnection.sendPacketBypass(newPacket);
    }
    
    public boolean isOverLiquid() {
        boolean foundLiquid = false;
        boolean foundSolid = false;
        for (final Box bb : WMinecraft.getWorld().getCollisionBoxes(JesusMod.MC.player, JesusMod.MC.player.boundingBox.offset(0.0, -0.5, 0.0))) {
            final BlockPos pos = new BlockPos(bb.getCenter());
            final Material material = WBlock.getMaterial(pos);
            if (material == Material.WATER || material == Material.LAVA) {
                foundLiquid = true;
            }
            else {
                if (material == Material.AIR) {
                    continue;
                }
                foundSolid = true;
            }
        }
        return foundLiquid && !foundSolid;
    }
    
    public boolean shouldBeSolid() {
        return this.isActive() && JesusMod.MC.player != null && JesusMod.MC.player.fallDistance <= 3.0f && !JesusMod.MC.gameSettings.keyBindSneak.pressed && !JesusMod.MC.player.isInWater();
    }
}
