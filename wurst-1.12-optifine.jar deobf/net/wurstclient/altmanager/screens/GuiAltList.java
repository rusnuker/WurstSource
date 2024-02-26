// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager.screens;

import net.wurstclient.altmanager.AltRenderer;
import net.wurstclient.compatibility.WMath;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.Minecraft;
import net.wurstclient.altmanager.Alt;
import java.util.List;
import net.minecraft.client.gui.GuiSlot;

public final class GuiAltList extends GuiSlot
{
    private final AltManagerScreen prevScreen;
    private final List<Alt> list;
    private int selected;
    
    public GuiAltList(final Minecraft mc, final AltManagerScreen prevScreen, final List<Alt> list) {
        super(mc, prevScreen.width, prevScreen.height, 36, prevScreen.height - 56, 30);
        this.selected = -1;
        this.prevScreen = prevScreen;
        this.list = list;
    }
    
    @Override
    protected boolean isSelected(final int id) {
        return this.selected == id;
    }
    
    protected int getSelectedSlot() {
        return this.selected;
    }
    
    public void setSelectedSlot(final int selectedSlot) {
        this.selected = selectedSlot;
    }
    
    protected Alt getSelectedAlt() {
        if (this.selected < 0 || this.selected >= this.list.size()) {
            return null;
        }
        return this.list.get(this.selected);
    }
    
    @Override
    protected int getSize() {
        return this.list.size();
    }
    
    @Override
    protected void elementClicked(final int index, final boolean doubleClick, final int var3, final int var4) {
        if (index >= 0 && index < this.list.size()) {
            this.selected = index;
        }
        if (doubleClick) {
            this.prevScreen.actionPerformed(new GuiButton(0, 0, 0, null));
        }
    }
    
    @Override
    protected void drawBackground() {
    }
    
    @Override
    protected void drawSlot(final int id, final int x, final int y, final int var4, final int var5, final int var6, final float partialTicks) {
        final Alt alt = this.list.get(id);
        if (this.mc.getSession().getUsername().equals(alt.getName())) {
            GL11.glDisable(3553);
            GL11.glDisable(2884);
            GL11.glEnable(3042);
            final float opacity = 0.3f - Math.abs(WMath.sin(Minecraft.getSystemTime() % 10000L / 10000.0f * 3.1415927f * 2.0f) * 0.15f);
            GL11.glColor4f(0.0f, 1.0f, 0.0f, opacity);
            GL11.glBegin(7);
            GL11.glVertex2d((double)(x - 2), (double)(y - 2));
            GL11.glVertex2d((double)(x - 2 + 220), (double)(y - 2));
            GL11.glVertex2d((double)(x - 2 + 220), (double)(y - 2 + 30));
            GL11.glVertex2d((double)(x - 2), (double)(y - 2 + 30));
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glEnable(2884);
            GL11.glDisable(3042);
        }
        AltRenderer.drawAltFace(alt.getNameOrEmail(), x + 1, y + 1, 24, 24, this.isSelected(id));
        this.mc.fontRendererObj.drawString("Name: " + alt.getNameOrEmail(), x + 31, y + 3, 10526880);
        String tags = alt.isCracked() ? "§8cracked" : "§2premium";
        if (alt.isStarred()) {
            tags = String.valueOf(tags) + "§r, §estarred";
        }
        if (alt.isUnchecked()) {
            tags = String.valueOf(tags) + "§r, §cunchecked";
        }
        this.mc.fontRendererObj.drawString(tags, x + 31, y + 15, 10526880);
    }
}
