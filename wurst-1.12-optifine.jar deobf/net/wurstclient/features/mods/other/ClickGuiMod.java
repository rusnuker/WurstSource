// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.clickgui.screens.ClickGuiScreen;
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.DontBlock;
import net.wurstclient.features.Hack;

@DontBlock
@DontSaveState
@SearchTags({ "click gui", "WindowGUI", "window gui", "HackMenu", "hack menu" })
public final class ClickGuiMod extends Hack
{
    private final SliderSetting opacity;
    private final SliderSetting bgRed;
    private final SliderSetting bgGreen;
    private final SliderSetting bgBlue;
    private final SliderSetting acRed;
    private final SliderSetting acGreen;
    private final SliderSetting acBlue;
    
    public ClickGuiMod() {
        super("ClickGUI", "Window-based ClickGUI.");
        this.opacity = new SliderSetting("Opacity", 0.5, 0.15, 0.85, 0.01, SliderSetting.ValueDisplay.PERCENTAGE);
        this.bgRed = new SliderSetting("BG red", "Background red", 64.0, 0.0, 255.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.bgGreen = new SliderSetting("BG green", "Background green", 64.0, 0.0, 255.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.bgBlue = new SliderSetting("BG blue", "Background blue", 64.0, 0.0, 255.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.acRed = new SliderSetting("AC red", "Accent red", 16.0, 0.0, 255.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.acGreen = new SliderSetting("AC green", "Accent green", 16.0, 0.0, 255.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.acBlue = new SliderSetting("AC blue", "Accent blue", 16.0, 0.0, 255.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.addSetting(this.opacity);
        this.addSetting(this.bgRed);
        this.addSetting(this.bgGreen);
        this.addSetting(this.bgBlue);
        this.addSetting(this.acRed);
        this.addSetting(this.acGreen);
        this.addSetting(this.acBlue);
    }
    
    @Override
    public void onEnable() {
        ClickGuiMod.MC.openScreen(new ClickGuiScreen(ClickGuiMod.WURST.getGui()));
        this.setEnabled(false);
    }
    
    public float getOpacity() {
        return this.opacity.getValueF();
    }
    
    public float[] getBgColor() {
        return new float[] { this.bgRed.getValueI() / 255.0f, this.bgGreen.getValueI() / 255.0f, this.bgBlue.getValueI() / 255.0f };
    }
    
    public float[] getAcColor() {
        return new float[] { this.acRed.getValueI() / 255.0f, this.acGreen.getValueI() / 255.0f, this.acBlue.getValueI() / 255.0f };
    }
}
