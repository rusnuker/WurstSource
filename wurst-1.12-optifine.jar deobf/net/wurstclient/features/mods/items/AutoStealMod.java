// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import net.wurstclient.features.special_features.YesCheatSpf;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "ChestStealer", "auto steal", "chest stealer" })
@Bypasses
public final class AutoStealMod extends Hack
{
    private final SliderSetting delay;
    private final CheckboxSetting buttons;
    
    public AutoStealMod() {
        super("AutoSteal", "Automatically steals everything from all chests you open.");
        this.delay = new SliderSetting("Delay", "Delay between moving stacks of items.\nShould be at least 70ms for NoCheat+ servers.", 100.0, 0.0, 500.0, 10.0, v -> String.valueOf((int)v) + "ms");
        this.buttons = new CheckboxSetting("Steal/Store buttons", true);
        this.setCategory(Category.ITEMS);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.buttons);
        this.addSetting(this.delay);
    }
    
    public boolean areButtonsVisible() {
        return this.buttons.isChecked();
    }
    
    public long getDelay() {
        return this.delay.getValueI();
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            case OFF:
            case MINEPLEX: {
                this.delay.resetUsableMin();
                break;
            }
            case ANTICHEAT:
            case OLDER_NCP:
            case LATEST_NCP: {
                this.delay.setUsableMin(70.0);
                break;
            }
            case GHOST_MODE: {
                this.delay.setUsableMin(200.0);
                break;
            }
        }
    }
}
