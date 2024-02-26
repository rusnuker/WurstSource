// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.fun;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.Category;
import java.util.Random;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "Retarded" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class DerpMod extends Hack implements UpdateListener
{
    private final Random random;
    
    public DerpMod() {
        super("Derp", "Randomly moves your head around.");
        this.random = new Random();
        this.setCategory(Category.FUN);
    }
    
    @Override
    public void onEnable() {
        DerpMod.WURST.getHax().headlessMod.setEnabled(false);
        DerpMod.WURST.getHax().headRollMod.setEnabled(false);
        DerpMod.WURST.getHax().tiredMod.setEnabled(false);
        DerpMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        DerpMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        final float yaw = DerpMod.MC.player.rotationYaw + this.random.nextFloat() * 360.0f - 180.0f;
        final float pitch = this.random.nextFloat() * 180.0f - 90.0f;
        WConnection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, DerpMod.MC.player.onGround));
    }
}
