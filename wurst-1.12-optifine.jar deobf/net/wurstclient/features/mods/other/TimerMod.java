// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.wurstclient.features.special_features.YesCheatSpf;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false)
public final class TimerMod extends Hack
{
    private final SliderSetting speed;
    
    public TimerMod() {
        super("Timer", "Changes the speed of almost everything.");
        this.speed = new SliderSetting("Speed", 2.0, 0.1, 20.0, 0.1, SliderSetting.ValueDisplay.DECIMAL);
        this.setCategory(Category.OTHER);
    }
    
    @Override
    public String getRenderName() {
        return String.valueOf(this.getName()) + " [" + this.speed.getValueString() + "]";
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.speed);
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            case OLDER_NCP:
            case LATEST_NCP: {
                this.speed.setUsableMax(1.0);
                break;
            }
            default: {
                this.speed.resetUsableMax();
                break;
            }
        }
    }
    
    public float getTimerSpeed() {
        return this.isActive() ? this.speed.getValueF() : 1.0f;
    }
}
