// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.Hack;

@Bypasses
public final class NoWeatherMod extends Hack
{
    private final CheckboxSetting disableRain;
    private final CheckboxSetting changeTime;
    private final SliderSetting time;
    private final CheckboxSetting changeMoonPhase;
    private final SliderSetting moonPhase;
    
    public NoWeatherMod() {
        super("NoWeather", "Allows you to alter the client-side weather, time and moon phase.");
        this.disableRain = new CheckboxSetting("Disable Rain", true);
        this.changeTime = new CheckboxSetting("Change World Time", false) {
            @Override
            public void update() {
                NoWeatherMod.this.time.setDisabled(!this.isChecked());
            }
        };
        this.time = new SliderSetting("Time", 6000.0, 0.0, 23900.0, 100.0, SliderSetting.ValueDisplay.INTEGER);
        this.changeMoonPhase = new CheckboxSetting("Change Moon Phase", false) {
            @Override
            public void update() {
                NoWeatherMod.this.moonPhase.setDisabled(!this.isChecked());
            }
        };
        this.moonPhase = new SliderSetting("Moon Phase", 0.0, 0.0, 7.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.setCategory(Category.RENDER);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.disableRain);
        this.addSetting(this.changeTime);
        this.addSetting(this.time);
        this.addSetting(this.changeMoonPhase);
        this.addSetting(this.moonPhase);
    }
    
    public boolean isRainDisabled() {
        return this.isActive() && this.disableRain.isChecked();
    }
    
    public boolean isTimeChanged() {
        return this.isActive() && this.changeTime.isChecked();
    }
    
    public long getChangedTime() {
        return this.time.getValueI();
    }
    
    public boolean isMoonPhaseChanged() {
        return this.isActive() && this.changeMoonPhase.isChecked();
    }
    
    public int getChangedMoonPhase() {
        return this.moonPhase.getValueI();
    }
}
