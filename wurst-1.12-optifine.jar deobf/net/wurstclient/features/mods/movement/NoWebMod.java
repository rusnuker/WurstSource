// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "no web" })
@Bypasses(ghostMode = false)
public final class NoWebMod extends Hack implements UpdateListener
{
    public NoWebMod() {
        super("NoWeb", "Prevents you from getting slowed down in webs.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        NoWebMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        NoWebMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        NoWebMod.MC.player.isInWeb = false;
    }
}
