// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.util.EntityFakePlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.ArrayDeque;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.PacketOutputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses
@DontSaveState
public final class BlinkMod extends Hack implements UpdateListener, PacketOutputListener
{
    private final SliderSetting limit;
    private final ArrayDeque<CPacketPlayer> packets;
    private EntityFakePlayer fakePlayer;
    
    public BlinkMod() {
        super("Blink", "Suspends all motion updates while enabled.");
        this.limit = new SliderSetting("Limit", "Automatically restarts Blink once\nthe given number of packets\nhave been suspended.\n\n0 = no limit", 0.0, 0.0, 500.0, 1.0, v -> {
            String string;
            if (v == 0.0) {
                string = "disabled";
            }
            else {
                string = new StringBuilder(String.valueOf((int)v)).toString();
            }
            return string;
        });
        this.packets = new ArrayDeque<CPacketPlayer>();
        this.setCategory(Category.MOVEMENT);
        this.addSetting(this.limit);
    }
    
    @Override
    public String getRenderName() {
        if (this.limit.getValueI() == 0) {
            return String.valueOf(this.getName()) + " [" + this.packets.size() + "]";
        }
        return String.valueOf(this.getName()) + " [" + this.packets.size() + "/" + this.limit.getValueI() + "]";
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { BlinkMod.WURST.commands.blinkCmd };
    }
    
    @Override
    public void onEnable() {
        this.fakePlayer = new EntityFakePlayer();
        BlinkMod.EVENTS.add(UpdateListener.class, this);
        BlinkMod.EVENTS.add(PacketOutputListener.class, this);
    }
    
    @Override
    public void onDisable() {
        BlinkMod.EVENTS.remove(UpdateListener.class, this);
        BlinkMod.EVENTS.remove(PacketOutputListener.class, this);
        this.fakePlayer.despawn();
        this.packets.forEach(p -> WConnection.sendPacket(p));
        this.packets.clear();
    }
    
    @Override
    public void onUpdate() {
        if (this.limit.getValueI() == 0) {
            return;
        }
        if (this.packets.size() >= this.limit.getValueI()) {
            this.setEnabled(false);
            this.setEnabled(true);
        }
    }
    
    @Override
    public void onSentPacket(final PacketOutputEvent event) {
        if (!(event.getPacket() instanceof CPacketPlayer)) {
            return;
        }
        event.cancel();
        final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
        final CPacketPlayer prevPacket = this.packets.peekLast();
        if (prevPacket != null && packet.isOnGround() == prevPacket.isOnGround() && packet.getYaw(-1.0f) == prevPacket.getYaw(-1.0f) && packet.getPitch(-1.0f) == prevPacket.getPitch(-1.0f) && packet.getX(-1.0) == prevPacket.getX(-1.0) && packet.getY(-1.0) == prevPacket.getY(-1.0) && packet.getZ(-1.0) == prevPacket.getZ(-1.0)) {
            return;
        }
        this.packets.addLast(packet);
    }
    
    public void cancel() {
        this.packets.clear();
        this.fakePlayer.resetPlayerPosition();
        this.setEnabled(false);
    }
}
