// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "Phaze" })
@Bypasses(ghostMode = false, latestNCP = false, antiCheat = false, mineplex = false)
public final class PhaseMod extends Hack implements UpdateListener
{
    public PhaseMod() {
        super("Phase", "Exploits a bug in older versions of NoCheat+ that allows you to glitch through blocks.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        PhaseMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        PhaseMod.EVENTS.remove(UpdateListener.class, this);
        PhaseMod.MC.player.noClip = false;
    }
    
    @Override
    public void onUpdate() {
        PhaseMod.MC.player.noClip = true;
        PhaseMod.MC.player.fallDistance = 0.0f;
        PhaseMod.MC.player.onGround = true;
    }
}
