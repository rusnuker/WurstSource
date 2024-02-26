// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.util.MathUtils;
import org.lwjgl.input.Mouse;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.Minecraft;
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@SearchTags({ "telescope", "optifine" })
public final class ZoomSpf extends OtherFeature
{
    private final SliderSetting level;
    private final CheckboxSetting scroll;
    private Float currentLevel;
    private Float defaultMouseSensitivity;
    
    public ZoomSpf() {
        super("Zoom", "Allows you to zoom in.\nBy default, the zoom is activated by pressing the §lV§r key.\nGo to Wurst Options -> Zoom to change this keybind.");
        this.level = new SliderSetting("Zoom level", 3.0, 1.0, 50.0, 0.1, v -> String.valueOf(SliderSetting.ValueDisplay.DECIMAL.getValueString(v)) + "x");
        this.scroll = new CheckboxSetting("Use mouse wheel", "If enabled, you can use the mouse wheel\nwhile zooming to zoom in even further.", true);
        this.addSetting(this.level);
        this.addSetting(this.scroll);
    }
    
    public float changeFovBasedOnZoom(final float fov) {
        final GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        this.onMouseScroll();
        if (this.currentLevel == null) {
            this.currentLevel = this.level.getValueF();
        }
        if (!ZoomSpf.WURST.getZoomKey().pressed) {
            this.currentLevel = this.level.getValueF();
            if (this.defaultMouseSensitivity != null) {
                gameSettings.mouseSensitivity = this.defaultMouseSensitivity;
                this.defaultMouseSensitivity = null;
            }
            return fov;
        }
        if (this.defaultMouseSensitivity == null) {
            this.defaultMouseSensitivity = gameSettings.mouseSensitivity;
        }
        gameSettings.mouseSensitivity = this.defaultMouseSensitivity * (fov / this.currentLevel / fov);
        return fov / this.currentLevel;
    }
    
    private void onMouseScroll() {
        if (!ZoomSpf.WURST.getZoomKey().pressed || !this.scroll.isChecked()) {
            return;
        }
        if (this.currentLevel == null) {
            this.currentLevel = this.level.getValueF();
        }
        final int amount = Mouse.getDWheel();
        if (amount > 0) {
            this.currentLevel *= 1.1f;
        }
        else if (amount < 0) {
            this.currentLevel *= 0.9f;
        }
        this.currentLevel = MathUtils.clamp(this.currentLevel, (float)this.level.getMinimum(), (float)this.level.getMaximum());
    }
    
    public SliderSetting getLevelSetting() {
        return this.level;
    }
    
    public CheckboxSetting getScrollSetting() {
        return this.scroll;
    }
}
