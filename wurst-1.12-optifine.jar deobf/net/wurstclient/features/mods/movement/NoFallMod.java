// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "no fall" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class NoFallMod extends Hack implements UpdateListener
{
    public NoFallMod() {
        super("NoFall", "Protects you from fall damage.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        NoFallMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        NoFallMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (NoFallMod.MC.player.fallDistance > 2.0f) {
            WConnection.sendPacket(new CPacketPlayer(true));
        }
    }
}
