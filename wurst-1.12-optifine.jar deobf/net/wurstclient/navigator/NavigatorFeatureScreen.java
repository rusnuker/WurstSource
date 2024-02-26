// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.navigator;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.FontRenderer;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WSoundEvents;
import net.wurstclient.keybinds.KeybindList;
import net.wurstclient.keybinds.NavigatorRemoveKeybindScreen;
import net.wurstclient.features.Hack;
import java.util.TreeMap;
import java.util.HashMap;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.keybinds.NavigatorNewKeybindScreen;
import net.wurstclient.font.Fonts;
import java.awt.Rectangle;
import java.io.IOException;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.util.MiscUtils;
import net.wurstclient.WurstClient;
import net.wurstclient.clickgui.Component;
import java.util.Iterator;
import net.wurstclient.settings.Setting;
import net.wurstclient.clickgui.Window;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.wurstclient.features.Feature;

public class NavigatorFeatureScreen extends NavigatorScreen
{
    private Feature feature;
    private NavigatorMainScreen parent;
    private ButtonData activeButton;
    private GuiButton primaryButton;
    private int sliding;
    private String text;
    private ArrayList<ButtonData> buttonDatas;
    private ArrayList<SliderSetting> sliders;
    private ArrayList<CheckboxSetting> checkboxes;
    private Window window;
    
    public NavigatorFeatureScreen(final Feature feature, final NavigatorMainScreen parent) {
        this.sliding = -1;
        this.buttonDatas = new ArrayList<ButtonData>();
        this.sliders = new ArrayList<SliderSetting>();
        this.checkboxes = new ArrayList<CheckboxSetting>();
        this.window = new Window("");
        this.feature = feature;
        this.parent = parent;
        this.hasBackground = false;
        for (final Setting setting : feature.getSettings()) {
            final Component c = setting.getComponent();
            if (c != null) {
                this.window.add(c);
            }
        }
        this.window.pack();
        this.window.setWidth(308);
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        final WurstClient wurst = WurstClient.INSTANCE;
        switch (button.id) {
            case 0: {
                this.feature.doPrimaryAction();
                this.primaryButton.displayString = this.feature.getPrimaryAction();
                break;
            }
            case 1: {
                MiscUtils.openLink("https://www.wurstclient.net/wiki/" + this.feature.getHelpPage() + "/");
                break;
            }
        }
        wurst.navigator.addPreference(this.feature.getName());
        ConfigFiles.NAVIGATOR.save();
    }
    
    @Override
    protected void onResize() {
        this.buttonDatas.clear();
        final String primaryAction = this.feature.getPrimaryAction();
        final boolean hasPrimaryAction = !primaryAction.isEmpty();
        final boolean hasHelp = !this.feature.getHelpPage().isEmpty();
        if (hasPrimaryAction) {
            this.primaryButton = new GuiButton(0, this.width / 2 - 151, this.height - 65, hasHelp ? 149 : 302, 18, primaryAction);
            this.buttonList.add(this.primaryButton);
        }
        if (hasHelp) {
            this.buttonList.add(new GuiButton(1, this.width / 2 + (hasPrimaryAction ? 2 : -151), this.height - 65, hasPrimaryAction ? 149 : 302, 20, "Help"));
        }
        this.text = "Type: " + this.feature.getType();
        if (this.feature.getCategory() != null) {
            this.text = String.valueOf(this.text) + ", Category: " + this.feature.getCategory().getName();
        }
        final String description = this.feature.getDescription();
        if (!description.isEmpty()) {
            this.text = String.valueOf(this.text) + "\n\nDescription:\n" + description;
        }
        final Rectangle area = new Rectangle(this.middleX - 154, 60, 308, this.height - 103);
        final ArrayList<Setting> settings = this.feature.getSettings();
        if (!settings.isEmpty()) {
            this.text = String.valueOf(this.text) + "\n\nSettings:";
            this.window.setY(Fonts.segoe15.getStringHeight(this.text) + 2);
            this.sliders.clear();
            this.checkboxes.clear();
            for (int i = 0; i < Math.ceil(this.window.getInnerHeight() / 9.0); ++i) {
                this.text = String.valueOf(this.text) + "\n";
            }
            for (final Setting setting : settings) {
                if (setting.getComponent() == null) {
                    setting.addToFeatureScreen(this);
                }
            }
        }
        final ArrayList<PossibleKeybind> possibleKeybinds = this.feature.getPossibleKeybinds();
        if (!possibleKeybinds.isEmpty()) {
            this.text = String.valueOf(this.text) + "\n\nKeybinds:";
            final ButtonData addKeybindButton = new ButtonData(this, area.x + area.width - 16, area.y + Fonts.segoe15.getStringHeight(this.text) - 7, 12, 8, "+", 65280) {
                @Override
                public void press() {
                    NavigatorFeatureScreen.this.mc.openScreen(new NavigatorNewKeybindScreen(possibleKeybinds, NavigatorFeatureScreen.this));
                }
            };
            this.buttonDatas.add(addKeybindButton);
            final HashMap<String, String> possibleKeybindsMap = new HashMap<String, String>();
            for (final PossibleKeybind possibleKeybind : possibleKeybinds) {
                possibleKeybindsMap.put(possibleKeybind.getCommand(), possibleKeybind.getDescription());
            }
            final TreeMap<String, PossibleKeybind> existingKeybinds = new TreeMap<String, PossibleKeybind>();
            boolean noKeybindsSet = true;
            for (int j = 0; j < WurstClient.INSTANCE.getKeybinds().size(); ++j) {
                final KeybindList.Keybind keybind = WurstClient.INSTANCE.getKeybinds().get(j);
                String commands = keybind.getCommands();
                commands = commands.replace(";", "§").replace("§§", ";");
                String[] split;
                for (int length = (split = commands.split("§")).length, k = 0; k < length; ++k) {
                    String command = split[k];
                    command = command.trim();
                    final String keybindDescription = possibleKeybindsMap.get(command);
                    if (keybindDescription != null) {
                        if (noKeybindsSet) {
                            noKeybindsSet = false;
                        }
                        this.text = String.valueOf(this.text) + "\n" + keybind.getKey() + ": " + keybindDescription;
                        existingKeybinds.put(keybind.getKey(), new PossibleKeybind(command, keybindDescription));
                    }
                    else if (this.feature instanceof Hack && command.equalsIgnoreCase(this.feature.getName())) {
                        if (noKeybindsSet) {
                            noKeybindsSet = false;
                        }
                        this.text = String.valueOf(this.text) + "\n" + keybind.getKey() + ": " + "Toggle " + this.feature.getName();
                        existingKeybinds.put(keybind.getKey(), new PossibleKeybind(command, "Toggle " + this.feature.getName()));
                    }
                }
            }
            if (noKeybindsSet) {
                this.text = String.valueOf(this.text) + "\nNone";
            }
            else {
                this.buttonDatas.add(new ButtonData(this, addKeybindButton.x, addKeybindButton.y, addKeybindButton.width, addKeybindButton.height, "-", 16711680) {
                    @Override
                    public void press() {
                        NavigatorFeatureScreen.this.mc.openScreen(new NavigatorRemoveKeybindScreen(existingKeybinds, NavigatorFeatureScreen.this));
                    }
                });
                final ButtonData buttonData = addKeybindButton;
                buttonData.x -= 16;
            }
        }
        final Feature[] seeAlso = this.feature.getSeeAlso();
        if (seeAlso.length != 0) {
            this.text = String.valueOf(this.text) + "\n\nSee also:";
            Feature[] array;
            for (int length2 = (array = seeAlso).length, l = 0; l < length2; ++l) {
                final Feature seeAlsoFeature = array[l];
                final int y = 60 + this.getTextHeight() + 2;
                final String name = seeAlsoFeature.getName();
                this.text = String.valueOf(this.text) + "\n- " + name;
                this.buttonDatas.add(new ButtonData(this, this.middleX - 148, y, Fonts.segoe15.getStringWidth(name) + 1, 8, "", 4210752) {
                    @Override
                    public void press() {
                        NavigatorFeatureScreen.this.mc.openScreen(new NavigatorFeatureScreen(seeAlsoFeature, NavigatorFeatureScreen.this.parent));
                    }
                });
            }
        }
        this.setContentHeight(Fonts.segoe15.getStringHeight(this.text));
    }
    
    @Override
    protected void onKeyPress(final char typedChar, final int keyCode) {
        if (keyCode == 1) {
            this.parent.setExpanding(false);
            this.mc.openScreen(this.parent);
        }
    }
    
    @Override
    protected void onMouseClick(final int x, final int y, final int button) {
        if (WurstClient.INSTANCE.getGui().handleNavigatorPopupClick(x - this.middleX + 154, y - 60 - this.scroll + 13, button)) {
            return;
        }
        final Rectangle area = new Rectangle(this.width / 2 - 154, 60, 308, this.height - 103);
        if (!area.contains(x, y)) {
            return;
        }
        if (this.activeButton != null) {
            WSoundEvents.playButtonClick();
            this.activeButton.press();
            WurstClient.INSTANCE.navigator.addPreference(this.feature.getName());
            ConfigFiles.NAVIGATOR.save();
            return;
        }
        area.height = 12;
        for (int i = 0; i < this.sliders.size(); ++i) {
            area.y = this.sliders.get(i).getY() + this.scroll;
            if (area.contains(x, y)) {
                this.sliding = i;
                return;
            }
        }
        for (final CheckboxSetting checkbox : this.checkboxes) {
            area.y = checkbox.getY() + this.scroll;
            if (area.contains(x, y)) {
                checkbox.toggle();
                final WurstClient wurst = WurstClient.INSTANCE;
                wurst.navigator.addPreference(this.feature.getName());
                ConfigFiles.NAVIGATOR.save();
                return;
            }
        }
        WurstClient.INSTANCE.getGui().handleNavigatorMouseClick(x - this.middleX + 154, y - 60 - this.scroll - this.window.getY(), button, this.window);
    }
    
    @Override
    protected void onMouseDrag(final int x, final int y, final int button, final long timeDragged) {
        if (button != 0) {
            return;
        }
        if (this.sliding != -1) {
            final float mousePercentage = WMath.clamp((x - (this.middleX - 150)) / 298.0f, 0.0f, 1.0f);
            final SliderSetting slider = this.sliders.get(this.sliding);
            slider.setValue(slider.getMinimum() + slider.getRange() * mousePercentage);
        }
    }
    
    @Override
    protected void onMouseRelease(final int x, final int y, final int button) {
        if (this.sliding != -1) {
            final WurstClient wurst = WurstClient.INSTANCE;
            this.sliding = -1;
            wurst.navigator.addPreference(this.feature.getName());
            ConfigFiles.NAVIGATOR.save();
        }
    }
    
    @Override
    protected void onUpdate() {
    }
    
    @Override
    protected void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawCenteredString(Fonts.segoe22, this.feature.getName(), this.middleX, 32, 16777215);
        GL11.glDisable(3553);
        final int bgx1 = this.middleX - 154;
        final int bgx2 = this.middleX + 154;
        final int bgy1 = 60;
        final int bgy2 = this.height - 43;
        this.setColorToBackground();
        this.drawQuads(bgx1, bgy1, bgx2, Math.max(bgy1, Math.min(bgy2 - (this.buttonList.isEmpty() ? 0 : 24), bgy1 + this.scroll + this.window.getY())));
        this.drawQuads(bgx1, Math.max(bgy1, Math.min(bgy2 - (this.buttonList.isEmpty() ? 0 : 24), bgy1 + this.scroll + this.window.getY() + this.window.getInnerHeight())), bgx2, bgy2);
        this.drawBoxShadow(bgx1, bgy1, bgx2, bgy2);
        RenderUtils.scissorBox(bgx1, bgy1, bgx2, bgy2 - (this.buttonList.isEmpty() ? 0 : 24));
        GL11.glEnable(3089);
        WurstClient.INSTANCE.getGui().setTooltip(null);
        this.window.validate();
        final int windowY = bgy1 + this.scroll + this.window.getY();
        GL11.glPushMatrix();
        GL11.glTranslated((double)bgx1, (double)windowY, 0.0);
        GL11.glDisable(3553);
        final int x1 = 0;
        final int y1 = -13;
        final int x2 = x1 + this.window.getWidth();
        final int y2 = y1 + this.window.getHeight();
        final int y3 = y1 + 13;
        final int x3 = x1 + 2;
        final int x4 = x2 - 2;
        this.setColorToBackground();
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y3);
        GL11.glVertex2i(x1, y2);
        GL11.glVertex2i(x3, y2);
        GL11.glVertex2i(x3, y3);
        GL11.glVertex2i(x4, y3);
        GL11.glVertex2i(x4, y2);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x2, y3);
        GL11.glEnd();
        this.setColorToBackground();
        GL11.glBegin(7);
        final int xc1 = 2;
        final int xc2 = x4 - x1;
        for (int i = 0; i < this.window.countChildren(); ++i) {
            final int yc1 = this.window.getChild(i).getY();
            final int yc2 = yc1 - 2;
            GL11.glVertex2i(xc1, yc2);
            GL11.glVertex2i(xc1, yc1);
            GL11.glVertex2i(xc2, yc1);
            GL11.glVertex2i(xc2, yc2);
        }
        int yc3;
        if (this.window.countChildren() == 0) {
            yc3 = 0;
        }
        else {
            final Component lastChild = this.window.getChild(this.window.countChildren() - 1);
            yc3 = lastChild.getY() + lastChild.getHeight();
        }
        final int yc4 = yc3 + 2;
        GL11.glVertex2i(xc1, yc4);
        GL11.glVertex2i(xc1, yc3);
        GL11.glVertex2i(xc2, yc3);
        GL11.glVertex2i(xc2, yc4);
        GL11.glEnd();
        for (int j = 0; j < this.window.countChildren(); ++j) {
            this.window.getChild(j).render(mouseX - bgx1, mouseY - windowY, partialTicks);
        }
        GL11.glPopMatrix();
        for (final SliderSetting slider : this.sliders) {
            int x5 = bgx1 + 2;
            int x6 = bgx2 - 2;
            int y4 = slider.getY() + this.scroll + 4;
            int y5 = y4 + 4;
            this.setColorToForeground();
            this.drawEngravedBox(x5, y4, x6, y5);
            final int width = x6 - x5;
            final boolean renderAsDisabled = slider.isDisabled() || slider.isLocked();
            if (!renderAsDisabled && slider.isLimited()) {
                GL11.glColor4f(0.75f, 0.125f, 0.125f, 0.25f);
                final double ratio = width / slider.getRange();
                this.drawQuads(x5, y4, (int)(x5 + ratio * (slider.getUsableMin() - slider.getMinimum())), y5);
                this.drawQuads((int)(x6 + ratio * (slider.getUsableMax() - slider.getMaximum())), y4, x6, y5);
            }
            final float percentage = slider.getPercentage();
            x5 = bgx1 + (int)((width - 6) * percentage) + 1;
            x6 = x5 + 8;
            y4 -= 2;
            y5 += 2;
            if (renderAsDisabled) {
                GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.75f);
            }
            else {
                final float factor = 2.0f * percentage;
                GL11.glColor4f(factor, 2.0f - factor, 0.0f, 0.75f);
            }
            this.drawBox(x5, y4, x6, y5);
            final String value = slider.getValueString();
            x5 = bgx2 - Fonts.segoe15.getStringWidth(value) - 2;
            y4 -= 12;
            this.drawString(Fonts.segoe15, value, x5, y4, renderAsDisabled ? 11184810 : 16777215);
            GL11.glDisable(3553);
        }
        this.activeButton = null;
        for (final ButtonData buttonData : this.buttonDatas) {
            final int x5 = buttonData.x;
            final int x6 = x5 + buttonData.width;
            final int y4 = buttonData.y + this.scroll;
            final int y5 = y4 + buttonData.height;
            float alpha;
            if (buttonData.isLocked()) {
                alpha = 0.25f;
            }
            else if (mouseX >= x5 && mouseX <= x6 && mouseY >= y4 && mouseY <= y5) {
                alpha = 0.75f;
                this.activeButton = buttonData;
            }
            else {
                alpha = 0.375f;
            }
            final float[] rgb = buttonData.color.getColorComponents(null);
            GL11.glColor4f(rgb[0], rgb[1], rgb[2], alpha);
            this.drawBox(x5, y4, x6, y5);
            this.drawCenteredString(Fonts.segoe15, buttonData.buttonText, (x5 + x6) / 2, y4 + (buttonData.height - 10) / 2 - 1, buttonData.isLocked() ? 11184810 : buttonData.textColor);
            GL11.glDisable(3553);
        }
        for (final CheckboxSetting checkbox : this.checkboxes) {
            int x5 = bgx1 + 2;
            final int x6 = x5 + 10;
            int y4 = checkbox.getY() + this.scroll + 2;
            final int y5 = y4 + 10;
            final boolean hovering = !checkbox.isLocked() && mouseX >= x5 && mouseX <= bgx2 - 2 && mouseY >= y4 && mouseY <= y5;
            if (hovering) {
                GL11.glColor4f(0.375f, 0.375f, 0.375f, 0.25f);
            }
            else {
                GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.25f);
            }
            this.drawBox(x5, y4, x6, y5);
            if (checkbox.isChecked()) {
                GL11.glColor4f(0.0f, 1.0f, 0.0f, hovering ? 0.75f : 0.375f);
                GL11.glBegin(7);
                GL11.glVertex2i(x5 + 3, y4 + 5);
                GL11.glVertex2i(x5 + 4, y4 + 6);
                GL11.glVertex2i(x5 + 4, y4 + 8);
                GL11.glVertex2i(x5 + 2, y4 + 6);
                GL11.glVertex2i(x5 + 7, y4 + 2);
                GL11.glVertex2i(x5 + 8, y4 + 3);
                GL11.glVertex2i(x5 + 4, y4 + 8);
                GL11.glVertex2i(x5 + 4, y4 + 6);
                GL11.glEnd();
                GL11.glColor4f(0.125f, 0.125f, 0.125f, hovering ? 0.75f : 0.375f);
                GL11.glBegin(2);
                GL11.glVertex2i(x5 + 3, y4 + 5);
                GL11.glVertex2i(x5 + 4, y4 + 6);
                GL11.glVertex2i(x5 + 7, y4 + 2);
                GL11.glVertex2i(x5 + 8, y4 + 3);
                GL11.glVertex2i(x5 + 4, y4 + 8);
                GL11.glVertex2i(x5 + 2, y4 + 6);
                GL11.glEnd();
            }
            x5 += 12;
            --y4;
            this.drawString(Fonts.segoe15, checkbox.getName(), x5, y4, checkbox.isLocked() ? 11184810 : 16777215);
            GL11.glDisable(3553);
        }
        this.drawString(Fonts.segoe15, this.text, bgx1 + 2, bgy1 + this.scroll, 16777215);
        GL11.glDisable(3089);
        GL11.glPushMatrix();
        GL11.glTranslated((double)bgx1, (double)(bgy1 + this.scroll - 13), 0.0);
        GL11.glDisable(3553);
        WurstClient.INSTANCE.getGui().renderPopupsAndTooltip(mouseX - bgx1, mouseY - bgy1 - this.scroll + 13);
        GL11.glPopMatrix();
        for (final GuiButton button : this.buttonList) {
            final int x5 = button.xPosition;
            final int x6 = x5 + button.getButtonWidth();
            final int y4 = button.yPosition;
            final int y5 = y4 + 18;
            final boolean hovering = mouseX >= x5 && mouseX <= x6 && mouseY >= y4 && mouseY <= y5;
            if (this.feature.isEnabled() && button.id == 0) {
                if (this.feature.isBlocked()) {
                    GL11.glColor4f(hovering ? 1.0f : 0.875f, 0.0f, 0.0f, 0.25f);
                }
                else {
                    GL11.glColor4f(0.0f, hovering ? 1.0f : 0.875f, 0.0f, 0.25f);
                }
            }
            else if (hovering) {
                GL11.glColor4f(0.375f, 0.375f, 0.375f, 0.25f);
            }
            else {
                GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.25f);
            }
            GL11.glDisable(3553);
            this.drawBox(x5, y4, x6, y5);
            this.drawCenteredString(Fonts.segoe18, button.displayString, (x5 + x6) / 2, y4 + 2, 16777215);
        }
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    
    @Override
    public void onGuiClosed() {
        this.window.close();
        WurstClient.INSTANCE.getGui().handleMouseClick(Integer.MIN_VALUE, Integer.MIN_VALUE, 0);
    }
    
    public Feature getFeature() {
        return this.feature;
    }
    
    public int getMiddleX() {
        return this.middleX;
    }
    
    public void addText(final String text) {
        this.text = String.valueOf(this.text) + text;
    }
    
    public int getTextHeight() {
        return Fonts.segoe15.getStringHeight(this.text);
    }
    
    public void addButton(final ButtonData button) {
        this.buttonDatas.add(button);
    }
    
    public void addSlider(final SliderSetting slider) {
        this.sliders.add(slider);
    }
    
    public void addCheckbox(final CheckboxSetting checkbox) {
        this.checkboxes.add(checkbox);
    }
    
    public abstract class ButtonData extends Rectangle
    {
        public String buttonText;
        public Color color;
        public int textColor;
        
        public ButtonData(final int x, final int y, final int width, final int height, final String buttonText, final int color) {
            super(x, y, width, height);
            this.textColor = 16777215;
            this.buttonText = buttonText;
            this.color = new Color(color);
        }
        
        public abstract void press();
        
        public boolean isLocked() {
            return false;
        }
    }
}
