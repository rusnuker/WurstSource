// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.settings.Setting;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "high jump" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false, antiCheat = false, mineplex = false)
public final class HighJumpMod extends Hack
{
    private final SliderSetting height;
    
    public HighJumpMod() {
        super("HighJump", "Allows you to jump higher.\n\n§c§lWARNING:§r You will take fall damage if you don't use NoFall.");
        this.height = new SliderSetting("Height", 6.0, 1.0, 100.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { HighJumpMod.WURST.hax.noFallMod };
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.height);
    }
    
    public double getAdditionalJumpMotion() {
        return this.isActive() ? (this.height.getValue() * 0.1) : 0.0;
    }
}
