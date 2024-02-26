// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui.options.zoom;

import net.minecraft.client.settings.KeyBinding;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.gui.options.GuiPressAKey;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.special_features.ZoomSpf;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;
import net.wurstclient.WurstClient;
import net.wurstclient.gui.options.GuiPressAKeyCallback;
import net.minecraft.client.gui.GuiScreen;

public class GuiZoomManager extends GuiScreen implements GuiPressAKeyCallback
{
    private GuiScreen prevScreen;
    
    public GuiZoomManager(final GuiScreen par1GuiScreen) {
        this.prevScreen = par1GuiScreen;
    }
    
    @Override
    public void initGui() {
        final ZoomSpf zoom = WurstClient.INSTANCE.special.zoomSpf;
        final CheckboxSetting scroll = zoom.getScrollSetting();
        final String zoomKeyName = Keyboard.getKeyName(WurstClient.INSTANCE.getZoomKey().getKeyCode());
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 144 - 16, 200, 20, "Back"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 79, this.height / 4 + 24 - 16, 158, 20, "Zoom Key: " + zoomKeyName));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 79, this.height / 4 + 72 - 16, 50, 20, "More"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 25, this.height / 4 + 72 - 16, 50, 20, "Less"));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 29, this.height / 4 + 72 - 16, 50, 20, "Default"));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 79, this.height / 4 + 96 - 16, 158, 20, "Use Mouse Wheel: " + (scroll.isChecked() ? "ON" : "OFF")));
        WurstClient.INSTANCE.analytics.trackPageView("/options/keybind-manager", "Keybind Manager");
    }
    
    @Override
    public void updateScreen() {
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) {
        final ZoomSpf zoom = WurstClient.INSTANCE.special.zoomSpf;
        final SliderSetting level = zoom.getLevelSetting();
        final CheckboxSetting scroll = zoom.getScrollSetting();
        if (button.enabled) {
            switch (button.id) {
                case 0: {
                    this.mc.openScreen(this.prevScreen);
                    break;
                }
                case 1: {
                    this.mc.openScreen(new GuiPressAKey(this));
                    break;
                }
                case 2: {
                    level.increaseValue();
                    break;
                }
                case 3: {
                    level.decreaseValue();
                    break;
                }
                case 4: {
                    level.setValue(level.getDefaultValue());
                    break;
                }
                case 5: {
                    scroll.setChecked(!scroll.isChecked());
                    this.buttonList.get(5).displayString = "Use Mouse Wheel: " + (scroll.isChecked() ? "ON" : "OFF");
                    break;
                }
            }
        }
    }
    
    @Override
    protected void keyTyped(final char par1, final int par2) {
    }
    
    @Override
    public void drawScreen(final int par1, final int par2, final float par3) {
        final ZoomSpf zoom = WurstClient.INSTANCE.special.zoomSpf;
        final SliderSetting level = zoom.getLevelSetting();
        this.drawBackground(0);
        this.drawCenteredString(this.fontRendererObj, "Zoom Manager", this.width / 2, 40, 16777215);
        this.drawString(this.fontRendererObj, "Zoom Level: " + level.getValueString() + " x normal", this.width / 2 - 75, this.height / 4 + 44, 13421772);
        super.drawScreen(par1, par2, par3);
    }
    
    @Override
    public void setKey(final String key) {
        WurstClient.INSTANCE.getZoomKey().setKeyCode(Keyboard.getKeyIndex(key));
        this.mc.gameSettings.saveOptions();
        KeyBinding.resetKeyBindingArrayAndHash();
        this.buttonList.get(1).displayString = "Zoom Key: " + key;
    }
}
