// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.clickgui.components;

import net.wurstclient.clickgui.ClickGui;
import org.lwjgl.opengl.GL11;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.clickgui.screens.EditItemListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.wurstclient.font.Fonts;
import net.wurstclient.settings.ItemListSetting;
import net.wurstclient.clickgui.Component;

public final class ItemListEditButton extends Component
{
    private final ItemListSetting setting;
    private int buttonWidth;
    
    public ItemListEditButton(final ItemListSetting setting) {
        this.setting = setting;
        final FontRenderer fr = Fonts.segoe18;
        this.buttonWidth = fr.getStringWidth("Edit...");
        this.setWidth(this.getDefaultWidth());
        this.setHeight(this.getDefaultHeight());
    }
    
    @Override
    public void handleMouseClick(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton != 0) {
            return;
        }
        if (mouseX < this.getX() + this.getWidth() - this.buttonWidth - 4) {
            return;
        }
        Minecraft.getMinecraft().openScreen(new EditItemListScreen(Minecraft.getMinecraft().currentScreen, this.setting));
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        final ClickGui gui = WurstClient.INSTANCE.getGui();
        final float[] bgColor = gui.getBgColor();
        final float[] acColor = gui.getAcColor();
        final float opacity = gui.getOpacity();
        final int x1 = this.getX();
        final int x2 = x1 + this.getWidth();
        final int x3 = x2 - this.buttonWidth - 4;
        final int y1 = this.getY();
        final int y2 = y1 + this.getHeight();
        final int scroll = this.getParent().isScrollingEnabled() ? this.getParent().getScrollOffset() : 0;
        final boolean hovering = mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2 && mouseY >= -scroll && mouseY < this.getParent().getHeight() - 13 - scroll;
        final boolean hText = hovering && mouseX < x3;
        final boolean hBox = hovering && mouseX >= x3;
        if (hText) {
            gui.setTooltip(this.setting.getDescription());
        }
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], opacity);
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x1, y2);
        GL11.glVertex2i(x3, y2);
        GL11.glVertex2i(x3, y1);
        GL11.glEnd();
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], hBox ? (opacity * 1.5f) : opacity);
        GL11.glBegin(7);
        GL11.glVertex2i(x3, y1);
        GL11.glVertex2i(x3, y2);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x2, y1);
        GL11.glEnd();
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
        GL11.glBegin(2);
        GL11.glVertex2i(x3, y1);
        GL11.glVertex2i(x3, y2);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x2, y1);
        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        final FontRenderer fr = Fonts.segoe18;
        final String text = String.valueOf(this.setting.getName()) + ": " + this.setting.getItemNames().size();
        fr.drawString(text, x1, y1 - 1, 15790320);
        fr.drawString("Edit...", x3 + 2, y1 - 1, 15790320);
        GL11.glDisable(3553);
    }
    
    @Override
    public int getDefaultWidth() {
        final FontRenderer fr = Fonts.segoe18;
        final String text = String.valueOf(this.setting.getName()) + ": " + this.setting.getItemNames().size();
        return fr.getStringWidth(text) + this.buttonWidth + 6;
    }
    
    @Override
    public int getDefaultHeight() {
        return 11;
    }
}
