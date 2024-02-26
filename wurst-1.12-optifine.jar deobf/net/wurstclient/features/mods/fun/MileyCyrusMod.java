// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.fun;

import net.minecraft.client.settings.GameSettings;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "miley cyrus", "twerk" })
@Bypasses
public final class MileyCyrusMod extends Hack implements UpdateListener
{
    private int timer;
    private final SliderSetting twerkSpeed;
    
    public MileyCyrusMod() {
        super("MileyCyrus", "Makes you twerk.");
        this.twerkSpeed = new SliderSetting("Twerk speed", 5.0, 1.0, 10.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.setCategory(Category.FUN);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.twerkSpeed);
    }
    
    @Override
    public void onEnable() {
        this.timer = 0;
        MileyCyrusMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        MileyCyrusMod.EVENTS.remove(UpdateListener.class, this);
        MileyCyrusMod.MC.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(MileyCyrusMod.MC.gameSettings.keyBindSneak);
    }
    
    @Override
    public void onUpdate() {
        ++this.timer;
        if (this.timer < 10 - this.twerkSpeed.getValueI()) {
            return;
        }
        MileyCyrusMod.MC.gameSettings.keyBindSneak.pressed = !MileyCyrusMod.MC.gameSettings.keyBindSneak.pressed;
        this.timer = -1;
    }
}
