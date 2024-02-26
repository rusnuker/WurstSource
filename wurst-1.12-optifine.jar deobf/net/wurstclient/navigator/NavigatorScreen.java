// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.navigator;

import net.wurstclient.WurstClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;
import java.awt.Rectangle;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;

public abstract class NavigatorScreen extends GuiScreen
{
    protected int scroll;
    private int scrollKnobPosition;
    private boolean scrolling;
    private int maxScroll;
    protected boolean scrollbarLocked;
    protected int middleX;
    protected boolean hasBackground;
    protected int nonScrollableArea;
    private boolean showScrollbar;
    
    public NavigatorScreen() {
        this.scroll = 0;
        this.scrollKnobPosition = 2;
        this.hasBackground = true;
        this.nonScrollableArea = 26;
    }
    
    @Override
    public final void initGui() {
        this.middleX = this.width / 2;
        this.onResize();
    }
    
    public final void keyTyped(final char typedChar, final int keyCode) throws IOException {
        this.onKeyPress(typedChar, keyCode);
    }
    
    public final void mouseClicked(final int x, final int y, final int button) throws IOException {
        super.mouseClicked(x, y, button);
        if (new Rectangle(this.width / 2 + 170, 60, 12, this.height - 103).contains(x, y)) {
            this.scrolling = true;
        }
        this.onMouseClick(x, y, button);
    }
    
    public final void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        if (this.scrolling && !this.scrollbarLocked && clickedMouseButton == 0) {
            if (this.maxScroll == 0) {
                this.scroll = 0;
            }
            else {
                this.scroll = (int)((mouseY - 72) * (float)this.maxScroll / (this.height - 131));
            }
            if (this.scroll > 0) {
                this.scroll = 0;
            }
            else if (this.scroll < this.maxScroll) {
                this.scroll = this.maxScroll;
            }
        }
        this.onMouseDrag(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }
    
    public final void mouseReleased(final int x, final int y, final int button) {
        super.mouseReleased(x, y, button);
        this.scrolling = false;
        this.onMouseRelease(x, y, button);
    }
    
    @Override
    public final void updateScreen() {
        this.onUpdate();
        if (!this.scrollbarLocked) {
            this.scroll += Mouse.getDWheel() / 10;
            if (this.scroll > 0) {
                this.scroll = 0;
            }
            else if (this.scroll < this.maxScroll) {
                this.scroll = this.maxScroll;
            }
            if (this.maxScroll == 0) {
                this.scrollKnobPosition = 0;
            }
            else {
                this.scrollKnobPosition = (int)((this.height - 131) * this.scroll / (float)this.maxScroll);
            }
            this.scrollKnobPosition += 2;
        }
    }
    
    @Override
    public final void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glShadeModel(7425);
        final int bgx1 = this.middleX - 154;
        final int bgx2 = this.middleX + 154;
        final int bgy1 = 60;
        final int bgy2 = this.height - 43;
        if (this.hasBackground) {
            this.drawBackgroundBox(bgx1, bgy1, bgx2, bgy2);
        }
        if (this.showScrollbar) {
            int x1 = bgx2 + 16;
            int x2 = x1 + 12;
            int y1 = bgy1;
            int y2 = bgy2;
            this.drawBackgroundBox(x1, y1, x2, y2);
            x1 += 2;
            x2 -= 2;
            y1 += this.scrollKnobPosition;
            y2 = y1 + 24;
            this.drawForegroundBox(x1, y1, x2, y2);
            ++x1;
            --x2;
            y1 += 8;
            y2 -= 15;
            for (int i = 0; i < 3; ++i) {
                this.drawDownShadow(x1, y1, x2, y2);
                y1 += 4;
                y2 += 4;
            }
        }
        this.onRender(mouseX, mouseY, partialTicks);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    
    protected abstract void onResize();
    
    protected abstract void onKeyPress(final char p0, final int p1);
    
    protected abstract void onMouseClick(final int p0, final int p1, final int p2);
    
    protected abstract void onMouseDrag(final int p0, final int p1, final int p2, final long p3);
    
    protected abstract void onMouseRelease(final int p0, final int p1, final int p2);
    
    protected abstract void onUpdate();
    
    protected abstract void onRender(final int p0, final int p1, final float p2);
    
    @Override
    public final boolean doesGuiPauseGame() {
        return false;
    }
    
    protected final void setContentHeight(final int contentHeight) {
        this.maxScroll = this.height - contentHeight - this.nonScrollableArea - 120;
        if (this.maxScroll > 0) {
            this.maxScroll = 0;
        }
        this.showScrollbar = (this.maxScroll != 0);
    }
    
    protected final void drawQuads(final int x1, final int y1, final int x2, final int y2) {
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x2, y1);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x1, y2);
        GL11.glEnd();
    }
    
    protected final void drawBoxShadow(final int x1, final int y1, final int x2, final int y2) {
        final float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
        double xi1 = x1 - 0.1;
        double xi2 = x2 + 0.1;
        double yi1 = y1 - 0.1;
        double yi2 = y2 + 0.1;
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
        GL11.glBegin(2);
        GL11.glVertex2d(xi1, yi1);
        GL11.glVertex2d(xi2, yi1);
        GL11.glVertex2d(xi2, yi2);
        GL11.glVertex2d(xi1, yi2);
        GL11.glEnd();
        xi1 -= 0.9;
        xi2 += 0.9;
        yi1 -= 0.9;
        yi2 += 0.9;
        GL11.glBegin(9);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75f);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x2, (double)y1);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glVertex2d(xi2, yi1);
        GL11.glVertex2d(xi1, yi1);
        GL11.glVertex2d(xi1, yi2);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75f);
        GL11.glVertex2d((double)x1, (double)y2);
        GL11.glEnd();
        GL11.glBegin(9);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)y1);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glVertex2d(xi2, yi1);
        GL11.glVertex2d(xi2, yi2);
        GL11.glVertex2d(xi1, yi2);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75f);
        GL11.glVertex2d((double)x1, (double)y2);
        GL11.glEnd();
    }
    
    protected final void drawInvertedBoxShadow(final int x1, final int y1, final int x2, final int y2) {
        final float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
        double xi1 = x1 + 0.1;
        double xi2 = x2 - 0.1;
        double yi1 = y1 + 0.1;
        double yi2 = y2 - 0.1;
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
        GL11.glBegin(2);
        GL11.glVertex2d(xi1, yi1);
        GL11.glVertex2d(xi2, yi1);
        GL11.glVertex2d(xi2, yi2);
        GL11.glVertex2d(xi1, yi2);
        GL11.glEnd();
        xi1 += 0.9;
        xi2 -= 0.9;
        yi1 += 0.9;
        yi2 -= 0.9;
        GL11.glBegin(9);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75f);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x2, (double)y1);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glVertex2d(xi2, yi1);
        GL11.glVertex2d(xi1, yi1);
        GL11.glVertex2d(xi1, yi2);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75f);
        GL11.glVertex2d((double)x1, (double)y2);
        GL11.glEnd();
        GL11.glBegin(9);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)y1);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glVertex2d(xi2, yi1);
        GL11.glVertex2d(xi2, yi2);
        GL11.glVertex2d(xi1, yi2);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75f);
        GL11.glVertex2d((double)x1, (double)y2);
        GL11.glEnd();
    }
    
    protected final void drawDownShadow(final int x1, final int y1, final int x2, final int y2) {
        final float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
        final double yi1 = y1 + 0.1;
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
        GL11.glBegin(1);
        GL11.glVertex2d((double)x1, yi1);
        GL11.glVertex2d((double)x2, yi1);
        GL11.glEnd();
        GL11.glBegin(9);
        GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75f);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x2, y1);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x1, y2);
        GL11.glEnd();
    }
    
    protected final void drawBox(final int x1, final int y1, final int x2, final int y2) {
        this.drawQuads(x1, y1, x2, y2);
        this.drawBoxShadow(x1, y1, x2, y2);
    }
    
    protected final void drawEngravedBox(final int x1, final int y1, final int x2, final int y2) {
        this.drawQuads(x1, y1, x2, y2);
        this.drawInvertedBoxShadow(x1, y1, x2, y2);
    }
    
    protected final void setColorToBackground() {
        final float[] bgColor = WurstClient.INSTANCE.getGui().getBgColor();
        final float opacity = WurstClient.INSTANCE.getGui().getOpacity();
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], opacity);
    }
    
    protected final void setColorToForeground() {
        final float[] bgColor = WurstClient.INSTANCE.getGui().getBgColor();
        final float opacity = WurstClient.INSTANCE.getGui().getOpacity();
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], opacity);
    }
    
    protected final void drawBackgroundBox(final int x1, final int y1, final int x2, final int y2) {
        this.setColorToBackground();
        this.drawBox(x1, y1, x2, y2);
    }
    
    protected final void drawForegroundBox(final int x1, final int y1, final int x2, final int y2) {
        this.setColorToForeground();
        this.drawBox(x1, y1, x2, y2);
    }
}
