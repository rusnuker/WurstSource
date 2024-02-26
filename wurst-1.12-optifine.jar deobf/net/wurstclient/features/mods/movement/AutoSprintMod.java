// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto sprint" })
@Bypasses
public final class AutoSprintMod extends Hack implements UpdateListener
{
    public AutoSprintMod() {
        super("AutoSprint", "Makes you sprint automatically.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        AutoSprintMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoSprintMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (AutoSprintMod.MC.player.isCollidedHorizontally || AutoSprintMod.MC.player.isSneaking()) {
            return;
        }
        if (AutoSprintMod.MC.player.moveForward > 0.0f) {
            AutoSprintMod.MC.player.setSprinting(true);
        }
    }
}
