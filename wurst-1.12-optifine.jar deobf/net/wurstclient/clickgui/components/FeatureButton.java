// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.clickgui.components;

import net.minecraft.client.gui.FontRenderer;
import net.wurstclient.font.Fonts;
import org.lwjgl.opengl.GL11;
import net.wurstclient.clickgui.ClickGui;
import java.util.Iterator;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import net.wurstclient.settings.Setting;
import net.wurstclient.clickgui.Window;
import net.wurstclient.features.Feature;
import net.wurstclient.clickgui.Component;

public final class FeatureButton extends Component
{
    private final Feature feature;
    private Window settingsWindow;
    
    public FeatureButton(final Feature feature) {
        this.feature = feature;
        this.setWidth(this.getDefaultWidth());
        this.setHeight(this.getDefaultHeight());
    }
    
    @Override
    public void handleMouseClick(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton != 0) {
            return;
        }
        if (this.feature.getSettings().isEmpty() || mouseX <= this.getX() + this.getWidth() - 12) {
            this.feature.doPrimaryAction();
            return;
        }
        if (this.settingsWindow != null && !this.settingsWindow.isClosing()) {
            this.settingsWindow.close();
            this.settingsWindow = null;
            return;
        }
        this.settingsWindow = new Window(String.valueOf(this.feature.getName()) + " Settings");
        for (final Setting setting : this.feature.getSettings()) {
            this.settingsWindow.add(setting.getComponent());
        }
        this.settingsWindow.setClosable(true);
        this.settingsWindow.setMinimizable(false);
        this.settingsWindow.pack();
        final int scroll = this.getParent().isScrollingEnabled() ? this.getParent().getScrollOffset() : 0;
        int x = this.getParent().getX() + this.getParent().getWidth() + 5;
        int y = this.getParent().getY() + 12 + this.getY() + scroll;
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        if (x + this.settingsWindow.getWidth() > sr.getScaledWidth()) {
            x = this.getParent().getX() - this.settingsWindow.getWidth() - 5;
        }
        if (y + this.settingsWindow.getHeight() > sr.getScaledHeight()) {
            y -= this.settingsWindow.getHeight() - 14;
        }
        this.settingsWindow.setX(x);
        this.settingsWindow.setY(y);
        final ClickGui gui = WurstClient.INSTANCE.getGui();
        gui.addWindow(this.settingsWindow);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        final ClickGui gui = WurstClient.INSTANCE.getGui();
        final float[] bgColor = gui.getBgColor();
        final float[] acColor = gui.getAcColor();
        final float opacity = gui.getOpacity();
        final boolean settings = !this.feature.getSettings().isEmpty();
        final int x1 = this.getX();
        final int x2 = x1 + this.getWidth();
        final int x3 = settings ? (x2 - 11) : x2;
        final int y1 = this.getY();
        final int y2 = y1 + this.getHeight();
        final int scroll = this.getParent().isScrollingEnabled() ? this.getParent().getScrollOffset() : 0;
        final boolean hovering = mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2 && mouseY >= -scroll && mouseY < this.getParent().getHeight() - 13 - scroll;
        final boolean hHack = hovering && mouseX < x3;
        final boolean hSettings = hovering && mouseX >= x3;
        String tooltip = this.feature.getDescription();
        if (this.feature.isBlocked()) {
            if (tooltip == null) {
                tooltip = "";
            }
            else {
                tooltip = String.valueOf(tooltip) + "\n\n";
            }
            tooltip = String.valueOf(tooltip) + "Your current YesCheat+ profile is blocking this feature.";
        }
        if (hHack) {
            gui.setTooltip(tooltip);
        }
        if (this.feature.isEnabled()) {
            if (this.feature.isBlocked()) {
                GL11.glColor4f(1.0f, 0.0f, 0.0f, hHack ? (opacity * 1.5f) : opacity);
            }
            else {
                GL11.glColor4f(0.0f, 1.0f, 0.0f, hHack ? (opacity * 1.5f) : opacity);
            }
        }
        else {
            GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], hHack ? (opacity * 1.5f) : opacity);
        }
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x1, y2);
        GL11.glVertex2i(x3, y2);
        GL11.glVertex2i(x3, y1);
        if (settings) {
            GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], hSettings ? (opacity * 1.5f) : opacity);
            GL11.glVertex2i(x3, y1);
            GL11.glVertex2i(x3, y2);
            GL11.glVertex2i(x2, y2);
            GL11.glVertex2i(x2, y1);
        }
        GL11.glEnd();
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
        GL11.glBegin(2);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x1, y2);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x2, y1);
        GL11.glEnd();
        if (settings) {
            GL11.glBegin(1);
            GL11.glVertex2i(x3, y1);
            GL11.glVertex2i(x3, y2);
            GL11.glEnd();
            final double xa1 = x3 + 1;
            final double xa2 = (x3 + x2) / 2.0;
            final double xa3 = x2 - 1;
            double ya1;
            double ya2;
            if (this.settingsWindow != null && !this.settingsWindow.isClosing()) {
                ya1 = y2 - 3.5;
                ya2 = y1 + 3;
                GL11.glColor4f(hSettings ? 1.0f : 0.85f, 0.0f, 0.0f, 1.0f);
            }
            else {
                ya1 = y1 + 3.5;
                ya2 = y2 - 3;
                GL11.glColor4f(0.0f, hSettings ? 1.0f : 0.85f, 0.0f, 1.0f);
            }
            GL11.glBegin(4);
            GL11.glVertex2d(xa1, ya1);
            GL11.glVertex2d(xa3, ya1);
            GL11.glVertex2d(xa2, ya2);
            GL11.glEnd();
            GL11.glColor4f(0.0625f, 0.0625f, 0.0625f, 0.5f);
            GL11.glBegin(2);
            GL11.glVertex2d(xa1, ya1);
            GL11.glVertex2d(xa3, ya1);
            GL11.glVertex2d(xa2, ya2);
            GL11.glEnd();
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        final FontRenderer fr = Fonts.segoe18;
        final int fx = x1 + ((settings ? (this.getWidth() - 11) : this.getWidth()) - fr.getStringWidth(this.feature.getName())) / 2;
        final int fy = y1 - 1;
        fr.drawString(this.feature.getName(), fx, fy, 15790320);
        GL11.glDisable(3553);
    }
    
    @Override
    public int getDefaultWidth() {
        int width = Fonts.segoe18.getStringWidth(this.feature.getName()) + 2;
        if (!this.feature.getSettings().isEmpty()) {
            width += 11;
        }
        return width;
    }
    
    @Override
    public int getDefaultHeight() {
        return 11;
    }
}
