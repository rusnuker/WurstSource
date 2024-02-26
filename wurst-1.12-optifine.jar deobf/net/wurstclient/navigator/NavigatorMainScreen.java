// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.navigator;

import net.wurstclient.clickgui.ClickGui;
import java.awt.Rectangle;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import net.wurstclient.util.MiscUtils;
import net.wurstclient.files.ConfigFiles;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.FontRenderer;
import net.wurstclient.font.Fonts;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.features.Feature;
import java.util.ArrayList;

public class NavigatorMainScreen extends NavigatorScreen
{
    private static final ArrayList<Feature> navigatorDisplayList;
    private GuiTextField searchBar;
    private String tooltip;
    private int hoveredFeature;
    private boolean hoveringArrow;
    private int clickTimer;
    private boolean expanding;
    
    static {
        navigatorDisplayList = new ArrayList<Feature>();
    }
    
    public NavigatorMainScreen() {
        this.hoveredFeature = -1;
        this.clickTimer = -1;
        this.expanding = false;
        this.hasBackground = false;
        this.nonScrollableArea = 0;
        (this.searchBar = new GuiTextField(0, Fonts.segoe22, 0, 32, 200, 20)).setEnableBackgroundDrawing(false);
        this.searchBar.setMaxStringLength(128);
        this.searchBar.setFocused(true);
        final Navigator navigator = WurstClient.INSTANCE.navigator;
        navigator.copyNavigatorList(NavigatorMainScreen.navigatorDisplayList);
    }
    
    @Override
    protected void onResize() {
        this.searchBar.xPosition = this.middleX - 100;
        this.setContentHeight(NavigatorMainScreen.navigatorDisplayList.size() / 3 * 20);
    }
    
    @Override
    protected void onKeyPress(final char typedChar, final int keyCode) {
        if (keyCode == 1 && this.clickTimer == -1) {
            this.mc.openScreen(null);
        }
        if (this.clickTimer == -1) {
            final String oldText = this.searchBar.getText();
            this.searchBar.textboxKeyTyped(typedChar, keyCode);
            String newText = this.searchBar.getText();
            final Navigator navigator = WurstClient.INSTANCE.navigator;
            if (newText.isEmpty()) {
                navigator.copyNavigatorList(NavigatorMainScreen.navigatorDisplayList);
            }
            else if (!newText.equals(oldText)) {
                newText = newText.toLowerCase().trim();
                navigator.getSearchResults(NavigatorMainScreen.navigatorDisplayList, newText);
            }
            this.setContentHeight(NavigatorMainScreen.navigatorDisplayList.size() / 3 * 20);
        }
    }
    
    @Override
    protected void onMouseClick(final int x, final int y, final int button) {
        if (this.clickTimer == -1 && this.hoveredFeature != -1) {
            if ((button == 0 && (Keyboard.isKeyDown(42) || this.hoveringArrow)) || button == 2) {
                this.expanding = true;
            }
            else if (button == 0) {
                final Feature feature = NavigatorMainScreen.navigatorDisplayList.get(this.hoveredFeature);
                if (feature.getPrimaryAction().isEmpty()) {
                    this.expanding = true;
                }
                else {
                    feature.doPrimaryAction();
                    final WurstClient wurst = WurstClient.INSTANCE;
                    wurst.navigator.addPreference(feature.getName());
                    ConfigFiles.NAVIGATOR.save();
                }
            }
            else if (button == 1) {
                final Feature feature = NavigatorMainScreen.navigatorDisplayList.get(this.hoveredFeature);
                if (feature.getHelpPage().isEmpty()) {
                    return;
                }
                MiscUtils.openLink("https://www.wurstclient.net/wiki/" + feature.getHelpPage() + "/");
                final WurstClient wurst = WurstClient.INSTANCE;
                wurst.navigator.addPreference(feature.getName());
                ConfigFiles.NAVIGATOR.save();
            }
        }
    }
    
    @Override
    protected void onMouseDrag(final int x, final int y, final int button, final long timeDragged) {
    }
    
    @Override
    protected void onMouseRelease(final int x, final int y, final int button) {
    }
    
    @Override
    protected void onUpdate() {
        this.searchBar.updateCursorCounter();
        if (this.expanding) {
            if (this.clickTimer < 4) {
                ++this.clickTimer;
            }
            else {
                final Feature feature = NavigatorMainScreen.navigatorDisplayList.get(this.hoveredFeature);
                this.mc.openScreen(new NavigatorFeatureScreen(feature, this));
            }
        }
        else if (!this.expanding && this.clickTimer > -1) {
            --this.clickTimer;
        }
        this.scrollbarLocked = (this.clickTimer != -1);
    }
    
    @Override
    protected void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        final ClickGui gui = WurstClient.INSTANCE.getGui();
        final float[] bgColor = gui.getBgColor();
        final float[] acColor = gui.getAcColor();
        final float opacity = gui.getOpacity();
        final boolean clickTimerNotRunning = this.clickTimer == -1;
        this.tooltip = null;
        if (clickTimerNotRunning) {
            Fonts.segoe22.drawString("Search: ", this.middleX - 150, 32, 16777215);
            this.searchBar.drawTextBox();
            GL11.glDisable(3553);
        }
        final int x = this.middleX - 50;
        if (clickTimerNotRunning) {
            this.hoveredFeature = -1;
        }
        RenderUtils.scissorBox(0, 59, this.width, this.height - 42);
        GL11.glEnable(3089);
        for (int i = Math.max(-this.scroll * 3 / 20 - 3, 0); i < NavigatorMainScreen.navigatorDisplayList.size(); ++i) {
            final int y = 60 + i / 3 * 20 + this.scroll;
            if (y >= 40) {
                if (y > this.height - 40) {
                    break;
                }
                int xi = 0;
                switch (i % 3) {
                    case 0: {
                        xi = x - 104;
                        break;
                    }
                    case 1: {
                        xi = x;
                        break;
                    }
                    case 2: {
                        xi = x + 104;
                        break;
                    }
                }
                final Feature feature = NavigatorMainScreen.navigatorDisplayList.get(i);
                final Rectangle area = new Rectangle(xi, y, 100, 16);
                if (!clickTimerNotRunning) {
                    if (i == this.hoveredFeature) {
                        float factor;
                        if (this.expanding) {
                            if (this.clickTimer == 4) {
                                factor = 1.0f;
                            }
                            else {
                                factor = (this.clickTimer + partialTicks) / 4.0f;
                            }
                        }
                        else if (this.clickTimer == 0) {
                            factor = 0.0f;
                        }
                        else {
                            factor = (this.clickTimer - partialTicks) / 4.0f;
                        }
                        final float antiFactor = 1.0f - factor;
                        area.x = (int)(area.x * antiFactor + (this.middleX - 154) * factor);
                        area.y = (int)(area.y * antiFactor + 60.0f * factor);
                        area.width = (int)(area.width * antiFactor + 308.0f * factor);
                        area.height = (int)(area.height * antiFactor + (this.height - 103) * factor);
                        this.drawBackgroundBox(area.x, area.y, area.x + area.width, area.y + area.height);
                    }
                }
                else {
                    final boolean hovering = area.contains(mouseX, mouseY);
                    if (hovering) {
                        this.hoveredFeature = i;
                    }
                    if (feature.isEnabled()) {
                        if (feature.isBlocked()) {
                            GL11.glColor4f(1.0f, 0.0f, 0.0f, hovering ? (opacity * 1.5f) : opacity);
                        }
                        else {
                            GL11.glColor4f(0.0f, 1.0f, 0.0f, hovering ? (opacity * 1.5f) : opacity);
                        }
                    }
                    else {
                        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], hovering ? (opacity * 1.5f) : opacity);
                    }
                    String tt = feature.getDescription();
                    if (feature.isBlocked()) {
                        if (tt == null) {
                            tt = "";
                        }
                        else {
                            tt = String.valueOf(tt) + "\n\n";
                        }
                        tt = String.valueOf(tt) + "Your current YesCheat+ profile is blocking this feature.";
                    }
                    if (hovering) {
                        this.tooltip = tt;
                    }
                    this.drawBox(area.x, area.y, area.x + area.width, area.y + area.height);
                    final int bx1 = area.x + area.width - area.height;
                    final int by1 = area.y + 2;
                    final int by2 = by1 + area.height - 4;
                    GL11.glBegin(1);
                    GL11.glVertex2i(bx1, by1);
                    GL11.glVertex2i(bx1, by2);
                    GL11.glEnd();
                    if (hovering) {
                        this.hoveringArrow = (mouseX >= bx1);
                    }
                    final double oneThrird = area.height / 3.0;
                    final double twoThrirds = area.height * 2.0 / 3.0;
                    final double ax1 = bx1 + oneThrird - 2.0;
                    final double ax2 = bx1 + twoThrirds + 2.0;
                    final double ax3 = bx1 + area.height / 2.0;
                    final double ay1 = area.y + oneThrird;
                    final double ay2 = area.y + twoThrirds;
                    GL11.glColor4f(0.0f, hovering ? 1.0f : 0.85f, 0.0f, 1.0f);
                    GL11.glBegin(4);
                    GL11.glVertex2d(ax1, ay1);
                    GL11.glVertex2d(ax2, ay1);
                    GL11.glVertex2d(ax3, ay2);
                    GL11.glEnd();
                    GL11.glLineWidth(1.0f);
                    GL11.glColor4f(0.0625f, 0.0625f, 0.0625f, 0.5f);
                    GL11.glBegin(2);
                    GL11.glVertex2d(ax1, ay1);
                    GL11.glVertex2d(ax2, ay1);
                    GL11.glVertex2d(ax3, ay2);
                    GL11.glEnd();
                    if (clickTimerNotRunning) {
                        final String buttonText = feature.getName();
                        Fonts.segoe15.drawString(buttonText, area.x + 4, area.y + 2, 16777215);
                        GL11.glDisable(3553);
                    }
                }
            }
        }
        GL11.glDisable(3089);
        if (this.tooltip != null) {
            final String[] lines = this.tooltip.split("\n");
            final FontRenderer fr = Fonts.segoe15;
            int tw = 0;
            final int th = lines.length * fr.FONT_HEIGHT;
            String[] array;
            for (int length = (array = lines).length, k = 0; k < length; ++k) {
                final String line = array[k];
                final int lw = fr.getStringWidth(line);
                if (lw > tw) {
                    tw = lw;
                }
            }
            final int sw = this.mc.currentScreen.width;
            final int sh = this.mc.currentScreen.height;
            final int xt1 = (mouseX + tw + 11 <= sw) ? (mouseX + 8) : (mouseX - tw - 8);
            final int xt2 = xt1 + tw + 3;
            final int yt1 = (mouseY + th - 2 <= sh) ? (mouseY - 4) : (mouseY - th - 4);
            final int yt2 = yt1 + th + 2;
            GL11.glDisable(3553);
            GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], 0.75f);
            GL11.glBegin(7);
            GL11.glVertex2i(xt1, yt1);
            GL11.glVertex2i(xt1, yt2);
            GL11.glVertex2i(xt2, yt2);
            GL11.glVertex2i(xt2, yt1);
            GL11.glEnd();
            GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
            GL11.glBegin(2);
            GL11.glVertex2i(xt1, yt1);
            GL11.glVertex2i(xt1, yt2);
            GL11.glVertex2i(xt2, yt2);
            GL11.glVertex2i(xt2, yt1);
            GL11.glEnd();
            GL11.glEnable(3553);
            for (int j = 0; j < lines.length; ++j) {
                fr.drawString(lines[j], xt1 + 2, yt1 - 1 + j * fr.FONT_HEIGHT, 16777215);
            }
        }
    }
    
    public void setExpanding(final boolean expanding) {
        this.expanding = expanding;
    }
}
