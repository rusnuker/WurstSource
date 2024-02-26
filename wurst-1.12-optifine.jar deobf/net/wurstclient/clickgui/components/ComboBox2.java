// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.clickgui.components;

import org.lwjgl.opengl.GL11;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.clickgui.Popup;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.FontRenderer;
import java.util.Arrays;
import net.wurstclient.font.Fonts;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.clickgui.Component;

public final class ComboBox2 extends Component
{
    private final ModeSetting setting;
    private final int popupWidth;
    private ComboBoxPopup popup;
    
    public ComboBox2(final ModeSetting setting) {
        this.setting = setting;
        final FontRenderer fr = Fonts.segoe18;
        this.popupWidth = Arrays.stream(setting.getModes()).mapToInt(m -> fontRenderer.getStringWidth(m)).max().getAsInt();
        this.setWidth(this.getDefaultWidth());
        this.setHeight(this.getDefaultHeight());
    }
    
    @Override
    public void handleMouseClick(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseX < this.getX() + this.getWidth() - this.popupWidth - 15) {
            return;
        }
        if (mouseButton == 0) {
            if (this.popup != null && !this.popup.isClosing()) {
                this.popup.close();
                this.popup = null;
                return;
            }
            this.popup = new ComboBoxPopup(this);
            final ClickGui gui = WurstClient.INSTANCE.getGui();
            gui.addPopup(this.popup);
        }
        else if (mouseButton == 1 && (this.popup == null || this.popup.isClosing())) {
            this.setting.setSelected(this.setting.getDefaultSelected());
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        final ClickGui gui = WurstClient.INSTANCE.getGui();
        final float[] bgColor = gui.getBgColor();
        final float[] acColor = gui.getAcColor();
        final float opacity = gui.getOpacity();
        final int x1 = this.getX();
        final int x2 = x1 + this.getWidth();
        final int x3 = x2 - 11;
        final int x4 = x3 - this.popupWidth - 4;
        final int y1 = this.getY();
        final int y2 = y1 + this.getHeight();
        final int scroll = this.getParent().isScrollingEnabled() ? this.getParent().getScrollOffset() : 0;
        final boolean hovering = mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2 && mouseY >= -scroll && mouseY < this.getParent().getHeight() - 13 - scroll;
        final boolean hText = hovering && mouseX < x4;
        final boolean hBox = hovering && mouseX >= x4;
        if (hText) {
            gui.setTooltip(this.setting.getDescription());
        }
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], opacity);
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x1, y2);
        GL11.glVertex2i(x4, y2);
        GL11.glVertex2i(x4, y1);
        GL11.glEnd();
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], hBox ? (opacity * 1.5f) : opacity);
        GL11.glBegin(7);
        GL11.glVertex2i(x4, y1);
        GL11.glVertex2i(x4, y2);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x2, y1);
        GL11.glEnd();
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
        GL11.glBegin(2);
        GL11.glVertex2i(x4, y1);
        GL11.glVertex2i(x4, y2);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x2, y1);
        GL11.glEnd();
        GL11.glBegin(1);
        GL11.glVertex2i(x3, y1);
        GL11.glVertex2i(x3, y2);
        GL11.glEnd();
        final double xa1 = x3 + 1;
        final double xa2 = (x3 + x2) / 2.0;
        final double xa3 = x2 - 1;
        double ya1;
        double ya2;
        if (this.popup != null && !this.popup.isClosing()) {
            ya1 = y2 - 3.5;
            ya2 = y1 + 3;
            GL11.glColor4f(hBox ? 1.0f : 0.85f, 0.0f, 0.0f, 1.0f);
        }
        else {
            ya1 = y1 + 3.5;
            ya2 = y2 - 3;
            GL11.glColor4f(0.0f, hBox ? 1.0f : 0.85f, 0.0f, 1.0f);
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
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        final FontRenderer fr = Fonts.segoe18;
        fr.drawString(this.setting.getName(), x1, y1 - 1, 15790320);
        fr.drawString(this.setting.getSelectedMode(), x4 + 2, y1 - 1, 15790320);
        GL11.glDisable(3553);
    }
    
    @Override
    public int getDefaultWidth() {
        return Fonts.segoe18.getStringWidth(this.setting.getName()) + this.popupWidth + 17;
    }
    
    @Override
    public int getDefaultHeight() {
        return 11;
    }
    
    private static class ComboBoxPopup extends Popup
    {
        public ComboBoxPopup(final ComboBox2 owner) {
            super(owner);
            this.setWidth(this.getDefaultWidth());
            this.setHeight(this.getDefaultHeight());
            this.setX(owner.getWidth() - this.getWidth());
            this.setY(owner.getHeight());
        }
        
        @Override
        public void handleMouseClick(final int mouseX, final int mouseY, final int mouseButton) {
            if (mouseButton != 0) {
                return;
            }
            final String[] values = ((ComboBox2)this.getOwner()).setting.getModes();
            int yi1 = this.getY() - 11;
            String[] array;
            for (int length = (array = values).length, i = 0; i < length; ++i) {
                final String value = array[i];
                if (!value.equalsIgnoreCase(((ComboBox2)this.getOwner()).setting.getSelectedMode())) {
                    yi1 += 11;
                    final int yi2 = yi1 + 11;
                    if (mouseY >= yi1) {
                        if (mouseY < yi2) {
                            ((ComboBox2)this.getOwner()).setting.setSelected(((ComboBox2)this.getOwner()).setting.indexOf(value));
                            this.close();
                            break;
                        }
                    }
                }
            }
        }
        
        @Override
        public void render(final int mouseX, final int mouseY) {
            final ClickGui gui = WurstClient.INSTANCE.getGui();
            final float[] bgColor = gui.getBgColor();
            final float[] acColor = gui.getAcColor();
            final float opacity = gui.getOpacity();
            final int x1 = this.getX();
            final int x2 = x1 + this.getWidth();
            final int y1 = this.getY();
            final int y2 = y1 + this.getHeight();
            final boolean hovering = mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2;
            if (hovering) {
                gui.setTooltip(null);
            }
            GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
            GL11.glBegin(2);
            GL11.glVertex2i(x1, y1);
            GL11.glVertex2i(x1, y2);
            GL11.glVertex2i(x2, y2);
            GL11.glVertex2i(x2, y1);
            GL11.glEnd();
            final String[] values = ((ComboBox2)this.getOwner()).setting.getModes();
            int yi1 = y1 - 11;
            String[] array;
            for (int length = (array = values).length, i = 0; i < length; ++i) {
                final String value = array[i];
                if (!value.equalsIgnoreCase(((ComboBox2)this.getOwner()).setting.getSelectedMode())) {
                    yi1 += 11;
                    final int yi2 = yi1 + 11;
                    final boolean hValue = hovering && mouseY >= yi1 && mouseY < yi2;
                    GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], hValue ? (opacity * 1.5f) : opacity);
                    GL11.glBegin(7);
                    GL11.glVertex2i(x1, yi1);
                    GL11.glVertex2i(x1, yi2);
                    GL11.glVertex2i(x2, yi2);
                    GL11.glVertex2i(x2, yi1);
                    GL11.glEnd();
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glEnable(3553);
                    final FontRenderer fr = Fonts.segoe18;
                    fr.drawString(value.toString(), x1 + 2, yi1 - 1, 15790320);
                    GL11.glDisable(3553);
                }
            }
        }
        
        @Override
        public int getDefaultWidth() {
            return ((ComboBox2)this.getOwner()).popupWidth + 15;
        }
        
        @Override
        public int getDefaultHeight() {
            return (((ComboBox2)this.getOwner()).setting.getModes().length - 1) * 11;
        }
    }
}
