// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.clickgui.components;

import net.minecraft.client.gui.FontRenderer;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.font.Fonts;
import org.lwjgl.opengl.GL11;
import net.wurstclient.WurstClient;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.clickgui.screens.EditSliderScreen;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.clickgui.Component;

public final class Slider extends Component
{
    private final SliderSetting setting;
    private boolean dragging;
    
    public Slider(final SliderSetting setting) {
        this.setting = setting;
        this.setWidth(this.getDefaultWidth());
        this.setHeight(this.getDefaultHeight());
    }
    
    @Override
    public void handleMouseClick(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseY < this.getY() + 11) {
            return;
        }
        if (mouseButton == 0) {
            if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) {
                Minecraft.getMinecraft().openScreen(new EditSliderScreen(Minecraft.getMinecraft().currentScreen, this.setting));
            }
            else {
                this.dragging = true;
            }
        }
        else if (mouseButton == 1) {
            this.setting.setValue(this.setting.getDefaultValue());
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        if (this.dragging) {
            if (Mouse.isButtonDown(0)) {
                final double mousePercentage = (mouseX - (this.getX() + 2)) / (double)(this.getWidth() - 4);
                final double value = this.setting.getMinimum() + (this.setting.getMaximum() - this.setting.getMinimum()) * mousePercentage;
                this.setting.setValue(value);
            }
            else {
                this.dragging = false;
            }
        }
        final ClickGui gui = WurstClient.INSTANCE.getGui();
        final float[] bgColor = gui.getBgColor();
        final float[] acColor = gui.getAcColor();
        final float opacity = gui.getOpacity();
        final int x1 = this.getX();
        final int x2 = x1 + this.getWidth();
        final int x3 = x1 + 2;
        final int x4 = x2 - 2;
        final int y1 = this.getY();
        final int y2 = y1 + this.getHeight();
        final int y3 = y1 + 11;
        final int y4 = y3 + 4;
        final int y5 = y2 - 4;
        final int scroll = this.getParent().isScrollingEnabled() ? this.getParent().getScrollOffset() : 0;
        boolean hovering = mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2 && mouseY >= -scroll && mouseY < this.getParent().getHeight() - 13 - scroll;
        boolean hSlider = (hovering && mouseY >= y3) || this.dragging;
        final boolean renderAsDisabled = this.setting.isDisabled() || this.setting.isLocked();
        String tooltip = this.setting.getDescription();
        if (renderAsDisabled) {
            if (tooltip == null) {
                tooltip = "";
            }
            else {
                tooltip = String.valueOf(tooltip) + "\n\n";
            }
        }
        if (this.setting.isLocked()) {
            tooltip = String.valueOf(tooltip) + "This slider is locked to " + this.setting.getValueString() + ".";
        }
        else if (this.setting.isDisabled()) {
            tooltip = String.valueOf(tooltip) + "This slider is disabled.";
        }
        if (hovering && mouseY < y3) {
            gui.setTooltip(tooltip);
        }
        else if (hSlider && !this.dragging) {
            gui.setTooltip("§e[ctrl]§r+§e[left-click]§r for precise input");
        }
        if (renderAsDisabled) {
            hovering = false;
            hSlider = false;
        }
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], opacity);
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x1, y4);
        GL11.glVertex2i(x2, y4);
        GL11.glVertex2i(x2, y1);
        GL11.glVertex2i(x1, y5);
        GL11.glVertex2i(x1, y2);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x2, y5);
        GL11.glVertex2i(x1, y4);
        GL11.glVertex2i(x1, y5);
        GL11.glVertex2i(x3, y5);
        GL11.glVertex2i(x3, y4);
        GL11.glVertex2i(x4, y4);
        GL11.glVertex2i(x4, y5);
        GL11.glVertex2i(x2, y5);
        GL11.glVertex2i(x2, y4);
        GL11.glEnd();
        double xl1 = x3;
        double xl2 = x4;
        if (!renderAsDisabled && this.setting.isLimited()) {
            final double ratio = (x4 - x3) / this.setting.getRange();
            xl1 += ratio * (this.setting.getUsableMin() - this.setting.getMinimum());
            xl2 += ratio * (this.setting.getUsableMax() - this.setting.getMaximum());
        }
        GL11.glColor4f(1.0f, 0.0f, 0.0f, hSlider ? (opacity * 1.5f) : opacity);
        GL11.glBegin(7);
        GL11.glVertex2d((double)x3, (double)y4);
        GL11.glVertex2d((double)x3, (double)y5);
        GL11.glVertex2d(xl1, (double)y5);
        GL11.glVertex2d(xl1, (double)y4);
        GL11.glVertex2d(xl2, (double)y4);
        GL11.glVertex2d(xl2, (double)y5);
        GL11.glVertex2d((double)x4, (double)y5);
        GL11.glVertex2d((double)x4, (double)y4);
        GL11.glEnd();
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], hSlider ? (opacity * 1.5f) : opacity);
        GL11.glBegin(7);
        GL11.glVertex2d(xl1, (double)y4);
        GL11.glVertex2d(xl1, (double)y5);
        GL11.glVertex2d(xl2, (double)y5);
        GL11.glVertex2d(xl2, (double)y4);
        GL11.glEnd();
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
        GL11.glBegin(2);
        GL11.glVertex2i(x3, y4);
        GL11.glVertex2i(x3, y5);
        GL11.glVertex2i(x4, y5);
        GL11.glVertex2i(x4, y4);
        GL11.glEnd();
        final double percentage = (this.setting.getValue() - this.setting.getMinimum()) / (this.setting.getMaximum() - this.setting.getMinimum());
        final double xk1 = x1 + (x2 - x1 - 8) * percentage;
        final double xk2 = xk1 + 8.0;
        final double yk1 = y3 + 1.5;
        final double yk2 = y2 - 1.5;
        if (renderAsDisabled) {
            GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.75f);
        }
        else {
            final float f = (float)(2.0 * percentage);
            GL11.glColor4f(f, 2.0f - f, 0.0f, hSlider ? 1.0f : 0.75f);
        }
        GL11.glBegin(7);
        GL11.glVertex2d(xk1, yk1);
        GL11.glVertex2d(xk1, yk2);
        GL11.glVertex2d(xk2, yk2);
        GL11.glVertex2d(xk2, yk1);
        GL11.glEnd();
        GL11.glColor4f(0.0625f, 0.0625f, 0.0625f, 0.5f);
        GL11.glBegin(2);
        GL11.glVertex2d(xk1, yk1);
        GL11.glVertex2d(xk1, yk2);
        GL11.glVertex2d(xk2, yk2);
        GL11.glVertex2d(xk2, yk1);
        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        final FontRenderer fr = Fonts.segoe18;
        fr.drawString(this.setting.getName(), x1, y1 - 1, renderAsDisabled ? 11184810 : 15790320);
        fr.drawString(this.setting.getValueString(), x2 - fr.getStringWidth(this.setting.getValueString()), y1 - 1, renderAsDisabled ? 11184810 : 15790320);
        GL11.glDisable(3553);
    }
    
    @Override
    public int getDefaultWidth() {
        final FontRenderer fr = Fonts.segoe18;
        return fr.getStringWidth(this.setting.getName()) + fr.getStringWidth(this.setting.getValueString()) + 6;
    }
    
    @Override
    public int getDefaultHeight() {
        return 22;
    }
}
