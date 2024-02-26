// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "no slowdown", "no slow down" })
@Bypasses(ghostMode = false)
public final class NoSlowdownMod extends Hack
{
    private final CheckboxSetting soulSand;
    private final CheckboxSetting items;
    
    public NoSlowdownMod() {
        super("NoSlowdown", "Cancels slowness effects caused by water, soul sand and using items.");
        this.soulSand = new CheckboxSetting("Block soul sand slowness", true);
        this.items = new CheckboxSetting("Block item slowness", true);
        this.setCategory(Category.MOVEMENT);
        this.addSetting(this.soulSand);
        this.addSetting(this.items);
    }
    
    public boolean blockSoulSandSlowness() {
        return this.isActive() && this.soulSand.isChecked();
    }
    
    public boolean blockItemSlowness() {
        return this.isActive() && this.items.isChecked();
    }
}
