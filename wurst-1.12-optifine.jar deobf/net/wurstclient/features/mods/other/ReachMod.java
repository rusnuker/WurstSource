// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.wurstclient.Category;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "range" })
@DontSaveState
public final class ReachMod extends Hack
{
    public ReachMod() {
        super("Reach", "Allows you to reach further.");
        this.setCategory(Category.OTHER);
    }
    
    @Override
    public void onEnable() {
        ReachMod.MC.playerController.setOverrideReach(true);
    }
    
    @Override
    public void onDisable() {
        ReachMod.MC.playerController.setOverrideReach(false);
    }
}
