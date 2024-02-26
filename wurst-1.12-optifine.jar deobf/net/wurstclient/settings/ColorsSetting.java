// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.settings;

import com.google.gson.JsonObject;
import net.wurstclient.util.json.JsonUtils;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.wurstclient.clickgui.Component;
import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;
import java.awt.Color;
import net.wurstclient.navigator.NavigatorFeatureScreen;

public class ColorsSetting extends Setting implements ColorsLock
{
    private boolean[] selected;
    private ColorsLock lock;
    
    public ColorsSetting(final String name, final boolean[] selected) {
        super(name, null);
        if (selected.length != 16) {
            throw new IllegalArgumentException("Length of 'selected' must be 16 but was " + selected.length + " instead.");
        }
        this.selected = selected;
    }
    
    @Override
    public final void addToFeatureScreen(final NavigatorFeatureScreen featureScreen) {
        featureScreen.addText("\n" + this.getName() + ":\n\n\n\n\n\n\n");
        int x = featureScreen.getMiddleX() - 104;
        int y = 60 + featureScreen.getTextHeight() - 72;
        final String[] colorNames = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
        final int[] colors = { 0, 26316, 52224, 52428, 13369344, 13369548, 16746496, 11184810, 6710886, 255, 65280, 65535, 16711680, 16746632, 16776960, 16777215 };
        class ColorButton extends NavigatorFeatureScreen.ButtonData
        {
            public int index = i;
            
            public ColorButton(final int x, final int y, final String displayString, final int color, final int index) {
                featureScreen.super(x, y, 12, 12, displayString, color);
                this.updateColor();
            }
            
            @Override
            public void press() {
                ColorsSetting.this.setSelected(this.index, !ColorsSetting.this.selected[this.index]);
                this.updateColor();
            }
            
            @Override
            public boolean isLocked() {
                return ColorsSetting.this.isLocked();
            }
            
            public void updateColor() {
                this.color = new Color(ColorsSetting.this.selected[this.index] ? 13421772 : 2236962);
            }
        }
        final ColorButton[] buttons = new ColorButton[this.selected.length];
        for (int i = 0; i < this.selected.length; ++i) {
            switch (i % 4) {
                case 0: {
                    x -= 48;
                    y += 16;
                    break;
                }
                default: {
                    x += 16;
                    break;
                }
            }
            final ColorButton button = featureScreen.new ColorButton(x, y, colorNames[i]);
            featureScreen.addButton((NavigatorFeatureScreen.ButtonData)(buttons[i] = button));
        }
        x += 16;
        y -= 48;
        featureScreen.getClass();
        featureScreen.addButton((NavigatorFeatureScreen.ButtonData)new NavigatorFeatureScreen.ButtonData(featureScreen, x, y, 48, 12, "All On", 4210752) {
            @Override
            public void press() {
                for (int i = 0; i < buttons.length; ++i) {
                    ColorsSetting.this.selected[i] = true;
                    buttons[i].updateColor();
                }
                ColorsSetting.this.update();
            }
            
            @Override
            public boolean isLocked() {
                return ColorsSetting.this.isLocked();
            }
        });
        y += 16;
        featureScreen.getClass();
        featureScreen.addButton((NavigatorFeatureScreen.ButtonData)new NavigatorFeatureScreen.ButtonData(featureScreen, x, y, 48, 12, "All Off", 4210752) {
            @Override
            public void press() {
                for (int i = 0; i < buttons.length; ++i) {
                    ColorsSetting.this.selected[i] = false;
                    buttons[i].updateColor();
                }
                ColorsSetting.this.update();
            }
            
            @Override
            public boolean isLocked() {
                return ColorsSetting.this.isLocked();
            }
        });
    }
    
    @Override
    public final ArrayList<PossibleKeybind> getPossibleKeybinds(final String featureName) {
        return new ArrayList<PossibleKeybind>();
    }
    
    @Override
    public final boolean[] getSelected() {
        return this.isLocked() ? this.lock.getSelected() : this.selected;
    }
    
    public final void setSelected(final int index, final boolean selected) {
        if (this.isLocked()) {
            return;
        }
        this.selected[index] = selected;
        this.update();
    }
    
    public final void lock(final ColorsLock lock) {
        if (lock == this) {
            throw new IllegalArgumentException("Infinite loop of locks within locks");
        }
        this.lock = lock;
        this.update();
    }
    
    public final void unlock() {
        this.lock = null;
        this.update();
    }
    
    public final boolean isLocked() {
        return this.lock != null;
    }
    
    @Override
    public final Component getComponent() {
        return null;
    }
    
    @Override
    public final void fromJson(final JsonElement json) {
        if (!json.isJsonArray()) {
            return;
        }
        final JsonArray array = json.getAsJsonArray();
        if (array.size() != this.selected.length) {
            return;
        }
        for (int i = 0; i < this.selected.length; ++i) {
            final JsonElement element = array.get(i);
            if (element.isJsonPrimitive()) {
                final JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    this.selected[i] = primitive.getAsBoolean();
                }
            }
        }
        this.update();
    }
    
    @Override
    public final JsonElement toJson() {
        return JsonUtils.GSON.toJsonTree((Object)this.selected);
    }
    
    @Override
    public final void legacyFromJson(final JsonObject json) {
        try {
            final JsonArray jsonColors = json.get(this.getName()).getAsJsonArray();
            for (int i = 0; i < this.selected.length; ++i) {
                this.selected[i] = jsonColors.get(i).getAsBoolean();
            }
        }
        catch (final Exception ex) {}
        this.update();
    }
}
