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
public final class TiredMod extends Hack implements UpdateListener
{
    public TiredMod() {
        super("Tired", "Makes it look like you're about to fall asleep.");
        this.setCategory(Category.FUN);
    }
    
    @Override
    public void onEnable() {
        TiredMod.WURST.getHax().derpMod.setEnabled(false);
        TiredMod.WURST.getHax().headlessMod.setEnabled(false);
        TiredMod.WURST.getHax().headRollMod.setEnabled(false);
        TiredMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        TiredMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        WConnection.sendPacket(new CPacketPlayer.Rotation(TiredMod.MC.player.rotationYaw, (float)(TiredMod.MC.player.ticksExisted % 100), TiredMod.MC.player.onGround));
    }
}
