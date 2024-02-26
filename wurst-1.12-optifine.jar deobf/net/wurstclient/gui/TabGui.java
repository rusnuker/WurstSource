// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui;

import net.wurstclient.clickgui.ClickGui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.wurstclient.font.Fonts;
import java.util.Iterator;
import java.util.Collection;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import java.util.LinkedHashMap;
import net.wurstclient.WurstClient;
import net.wurstclient.features.special_features.TabGuiSpf;
import java.util.ArrayList;
import net.wurstclient.events.KeyPressListener;

public final class TabGui implements KeyPressListener
{
    private final ArrayList<Tab> tabs;
    private final TabGuiSpf tabGuiSpf;
    private int width;
    private int height;
    private int selected;
    private boolean tabOpened;
    
    public TabGui() {
        this.tabs = new ArrayList<Tab>();
        this.tabGuiSpf = WurstClient.INSTANCE.special.tabGuiSpf;
        WurstClient.INSTANCE.events.add(KeyPressListener.class, this);
        final LinkedHashMap<Category, Tab> tabMap = new LinkedHashMap<Category, Tab>();
        Category[] values;
        for (int length = (values = Category.values()).length, i = 0; i < length; ++i) {
            final Category category = values[i];
            tabMap.put(category, new Tab(category.getName()));
        }
        final ArrayList<Feature> features = new ArrayList<Feature>();
        features.addAll(WurstClient.INSTANCE.hax.getAll());
        features.addAll(WurstClient.INSTANCE.commands.getAllCommands());
        features.addAll(WurstClient.INSTANCE.special.getAllFeatures());
        for (final Feature feature : features) {
            if (feature.getCategory() != null) {
                tabMap.get(feature.getCategory()).add(feature);
            }
        }
        this.tabs.addAll(tabMap.values());
        this.tabs.forEach(tab -> tab.updateSize());
        this.updateSize();
    }
    
    private void updateSize() {
        this.width = 64;
        for (final Tab tab : this.tabs) {
            final int tabWidth = Fonts.segoe18.getStringWidth(tab.name) + 10;
            if (tabWidth > this.width) {
                this.width = tabWidth;
            }
        }
        this.height = this.tabs.size() * 10;
    }
    
    @Override
    public void onKeyPress(final KeyPressEvent event) {
        if (this.tabGuiSpf.isHidden()) {
            return;
        }
        if (this.tabOpened) {
            switch (event.getKeyCode()) {
                case 203: {
                    this.tabOpened = false;
                    break;
                }
                default: {
                    this.tabs.get(this.selected).onKeyPress(event.getKeyCode());
                    break;
                }
            }
        }
        else {
            switch (event.getKeyCode()) {
                case 208: {
                    if (this.selected < this.tabs.size() - 1) {
                        ++this.selected;
                        break;
                    }
                    this.selected = 0;
                    break;
                }
                case 200: {
                    if (this.selected > 0) {
                        --this.selected;
                        break;
                    }
                    this.selected = this.tabs.size() - 1;
                    break;
                }
                case 205: {
                    this.tabOpened = true;
                    break;
                }
            }
        }
    }
    
    public void render(final float partialTicks) {
        if (this.tabGuiSpf.isHidden()) {
            return;
        }
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final int x = 2;
        final int y = 23;
        GL11.glTranslatef((float)x, (float)y, 0.0f);
        this.drawBox(0, 0, this.width, this.height);
        final int factor = sr.getScaleFactor();
        GL11.glScissor(x * factor, (sr.getScaledHeight() - this.height - y) * factor, this.width * factor, this.height * factor);
        GL11.glEnable(3089);
        int textY = -2;
        for (int i = 0; i < this.tabs.size(); ++i) {
            String tabName = this.tabs.get(i).name;
            if (i == this.selected) {
                tabName = String.valueOf(this.tabOpened ? "<" : ">") + tabName;
            }
            Fonts.segoe18.drawString(tabName, 2, textY, -1);
            textY += 10;
        }
        GL11.glDisable(3089);
        if (this.tabOpened) {
            GL11.glPushMatrix();
            GL11.glDisable(3553);
            final Tab tab = this.tabs.get(this.selected);
            final int tabX = x + this.width + 2;
            final int tabY = y;
            GL11.glTranslatef((float)(this.width + 2), 0.0f, 0.0f);
            this.drawBox(0, 0, tab.width, tab.height);
            GL11.glScissor(tabX * factor, (sr.getScaledHeight() - tab.height - tabY) * factor, tab.width * factor, tab.height * factor);
            GL11.glEnable(3089);
            int tabTextY = -2;
            for (int j = 0; j < tab.features.size(); ++j) {
                final Feature feature = tab.features.get(j);
                String fName = feature.getName();
                if (feature.isEnabled()) {
                    if (feature.isBlocked()) {
                        fName = "§c" + fName + "§r";
                    }
                    else {
                        fName = "§a" + fName + "§r";
                    }
                }
                if (j == tab.selected) {
                    fName = ">" + fName;
                }
                Fonts.segoe18.drawString(fName, 2, tabTextY, -1);
                tabTextY += 10;
            }
            GL11.glDisable(3089);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glEnable(2884);
    }
    
    private void drawBox(final int x1, final int y1, final int x2, final int y2) {
        final ClickGui gui = WurstClient.INSTANCE.getGui();
        final float[] bgColor = gui.getBgColor();
        final float[] acColor = gui.getAcColor();
        final float opacity = gui.getOpacity();
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], opacity);
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x2, y1);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x1, y2);
        GL11.glEnd();
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
    
    private static final class Tab
    {
        private final String name;
        private final ArrayList<Feature> features;
        private int width;
        private int height;
        private int selected;
        
        public Tab(final String name) {
            this.features = new ArrayList<Feature>();
            this.name = name;
        }
        
        public void updateSize() {
            this.width = 64;
            for (final Feature feature : this.features) {
                final int fWidth = Fonts.segoe18.getStringWidth(feature.getName()) + 10;
                if (fWidth > this.width) {
                    this.width = fWidth;
                }
            }
            this.height = this.features.size() * 10;
        }
        
        public void onKeyPress(final int keyCode) {
            switch (keyCode) {
                case 208: {
                    if (this.selected < this.features.size() - 1) {
                        ++this.selected;
                        break;
                    }
                    this.selected = 0;
                    break;
                }
                case 200: {
                    if (this.selected > 0) {
                        --this.selected;
                        break;
                    }
                    this.selected = this.features.size() - 1;
                    break;
                }
                case 28: {
                    this.features.get(this.selected).doPrimaryAction();
                    break;
                }
            }
        }
        
        public void add(final Feature feature) {
            this.features.add(feature);
        }
    }
}
