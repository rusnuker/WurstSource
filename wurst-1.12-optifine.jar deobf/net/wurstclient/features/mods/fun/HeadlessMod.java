// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.fun;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class HeadlessMod extends Hack implements UpdateListener
{
    public HeadlessMod() {
        super("Headless", "Makes it look like you lost your head.");
        this.setCategory(Category.FUN);
    }
    
    @Override
    public void onEnable() {
        HeadlessMod.WURST.getHax().derpMod.setEnabled(false);
        HeadlessMod.WURST.getHax().headRollMod.setEnabled(false);
        HeadlessMod.WURST.getHax().tiredMod.setEnabled(false);
        HeadlessMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        HeadlessMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        WConnection.sendPacket(new CPacketPlayer.Rotation(HeadlessMod.MC.player.rotationYaw, 180.0f, HeadlessMod.MC.player.onGround));
    }
}
