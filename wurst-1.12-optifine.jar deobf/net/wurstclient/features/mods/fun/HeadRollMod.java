// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.fun;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "head roll" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class HeadRollMod extends Hack implements UpdateListener
{
    public HeadRollMod() {
        super("HeadRoll", "Makes you nod all the time.");
        this.setCategory(Category.FUN);
    }
    
    @Override
    public void onEnable() {
        HeadRollMod.WURST.getHax().derpMod.setEnabled(false);
        HeadRollMod.WURST.getHax().headlessMod.setEnabled(false);
        HeadRollMod.WURST.getHax().tiredMod.setEnabled(false);
        HeadRollMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        HeadRollMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        final float timer = HeadRollMod.MC.player.ticksExisted % 20 / 10.0f;
        final float pitch = WMath.sin(timer * 3.1415927f) * 90.0f;
        WConnection.sendPacket(new CPacketPlayer.Rotation(HeadRollMod.MC.player.rotationYaw, pitch, HeadRollMod.MC.player.onGround));
    }
}
