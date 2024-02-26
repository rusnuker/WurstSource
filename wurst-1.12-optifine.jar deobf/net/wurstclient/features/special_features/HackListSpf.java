// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@SearchTags({ "hack list", "ArrayList", "array list", "ModList", "mod list", "CheatList", "cheat list" })
public final class HackListSpf extends OtherFeature
{
    private final ModeSetting mode;
    private final ModeSetting position;
    private final CheckboxSetting animations;
    
    public HackListSpf() {
        super("HackList", "Shows a list of active mods on the screen.\nThe §lLeft§r position should only be used while TabGui is disabled.");
        this.mode = new ModeSetting("Mode", "§lAuto§r mode renders the whole list if it fits onto the screen.\n§lCount§r mode only renders the number of active hacks.\n§lHidden§r mode renders nothing.", new String[] { "Auto", "Count", "Hidden" }, 0) {
            @Override
            public void update() {
                if (this.getSelected() == 0) {
                    HackListSpf.this.animations.unlock();
                }
                else {
                    HackListSpf.this.animations.lock(() -> false);
                }
            }
        };
        this.position = new ModeSetting("Position", new String[] { "Left", "Right" }, 0);
        this.animations = new CheckboxSetting("Animations", true);
        this.addSetting(this.mode);
        this.addSetting(this.position);
        this.addSetting(this.animations);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { HackListSpf.WURST.special.tabGuiSpf };
    }
    
    public boolean isCountMode() {
        return this.mode.getSelected() == 1;
    }
    
    public boolean isPositionRight() {
        return this.position.getSelected() == 1;
    }
    
    public boolean isHidden() {
        return this.mode.getSelected() == 2;
    }
    
    public boolean isAnimations() {
        return this.animations.isChecked();
    }
}
