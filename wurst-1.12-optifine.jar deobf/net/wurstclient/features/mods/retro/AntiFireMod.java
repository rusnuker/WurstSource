// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.retro;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.RetroMod;

@SearchTags({ "AntiBurn", "NoFire", "FireExtinguisher", "anti fire", "anti burn", "no fire", "fire extinguisher" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class AntiFireMod extends RetroMod implements UpdateListener
{
    public AntiFireMod() {
        super("AntiFire", "Blocks damage from catching on fire.\nDoes NOT block damage from standing inside of fire.\nRequires a full hunger bar.");
        this.setCategory(Category.RETRO);
    }
    
    @Override
    public void onEnable() {
        AntiFireMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AntiFireMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (AntiFireMod.MC.player.abilities.creativeMode) {
            return;
        }
        if (!AntiFireMod.MC.player.onGround) {
            return;
        }
        if (!AntiFireMod.MC.player.isBurning()) {
            return;
        }
        for (int i = 0; i < 100; ++i) {
            WConnection.sendPacket(new CPacketPlayer());
        }
    }
}
