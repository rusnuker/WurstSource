// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.settings;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import net.wurstclient.clickgui.components.ComboBox2;
import net.wurstclient.clickgui.Component;
import net.wurstclient.files.ConfigFiles;
import java.awt.Color;
import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;
import net.wurstclient.navigator.NavigatorFeatureScreen;

public class ModeSetting extends Setting
{
    private String[] modes;
    private int selected;
    private final int defaultSelected;
    private NavigatorFeatureScreen.ButtonData[] buttons;
    private boolean locked;
    private int lockSelected;
    
    public ModeSetting(final String name, final String description, final String[] modes, final int selected) {
        super(name, description);
        this.modes = modes;
        this.selected = selected;
        this.defaultSelected = selected;
    }
    
    public ModeSetting(final String name, final String[] modes, final int selected) {
        this(name, null, modes, selected);
    }
    
    @Override
    public final void addToFeatureScreen(final NavigatorFeatureScreen featureScreen) {
        featureScreen.addText("\n" + this.getName() + ":");
        int y = 0;
        this.buttons = new NavigatorFeatureScreen.ButtonData[this.modes.length];
        for (int i = 0; i < this.modes.length; ++i) {
            int x = featureScreen.getMiddleX();
            switch (i % 3) {
                case 0: {
                    x -= 150;
                    y = 60 + featureScreen.getTextHeight() + 3;
                    featureScreen.addText("\n\n");
                    break;
                }
                case 1: {
                    x -= 49;
                    break;
                }
                case 2: {
                    x += 52;
                    break;
                }
            }
            final int iFinal = i;
            featureScreen.getClass();
            final NavigatorFeatureScreen.ButtonData button = new NavigatorFeatureScreen.ButtonData(featureScreen, x, y, 97, 14, this.modes[i], (i == this.getSelected()) ? 65280 : 4210752) {
                @Override
                public void press() {
                    ModeSetting.this.setSelected(iFinal);
                }
                
                @Override
                public boolean isLocked() {
                    return ModeSetting.this.locked;
                }
            };
            featureScreen.addButton(this.buttons[i] = button);
        }
    }
    
    @Override
    public final ArrayList<PossibleKeybind> getPossibleKeybinds(final String featureName) {
        final ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<PossibleKeybind>();
        final String fullName = String.valueOf(featureName) + " " + this.getName();
        final String command = ".setmode " + featureName.toLowerCase() + " " + this.getName().toLowerCase().replace(" ", "_") + " ";
        final String description = "Set " + fullName + " to ";
        possibleKeybinds.add(new PossibleKeybind(String.valueOf(command) + "next", "Next " + fullName));
        possibleKeybinds.add(new PossibleKeybind(String.valueOf(command) + "prev", "Previous " + fullName));
        String[] modes;
        for (int length = (modes = this.modes).length, i = 0; i < length; ++i) {
            final String mode = modes[i];
            possibleKeybinds.add(new PossibleKeybind(String.valueOf(command) + mode.toLowerCase().replace(" ", "_"), String.valueOf(description) + mode));
        }
        return possibleKeybinds;
    }
    
    public final int getSelected() {
        return this.locked ? this.lockSelected : this.selected;
    }
    
    public final int getDefaultSelected() {
        return this.defaultSelected;
    }
    
    public final void setSelected(final int selected) {
        if (!this.locked) {
            this.selected = selected;
            if (this.buttons != null) {
                for (int i = 0; i < this.buttons.length; ++i) {
                    this.buttons[i].color = ((i == selected) ? new Color(65280) : new Color(4210752));
                }
            }
            this.update();
            ConfigFiles.SETTINGS.save();
        }
    }
    
    public final String[] getModes() {
        return this.modes;
    }
    
    public final String getSelectedMode() {
        return this.modes[this.getSelected()];
    }
    
    public final void nextMode() {
        ++this.selected;
        if (this.selected >= this.modes.length) {
            this.selected = 0;
        }
        this.update();
    }
    
    public final void prevMode() {
        --this.selected;
        if (this.selected <= -1) {
            this.selected = this.modes.length - 1;
        }
        this.update();
    }
    
    public final int indexOf(final String mode) {
        for (int i = 0; i < this.modes.length; ++i) {
            if (this.modes[i].equalsIgnoreCase(mode)) {
                return i;
            }
        }
        return -1;
    }
    
    public final void lock(final int lockSelected) {
        this.lockSelected = lockSelected;
        if (this.buttons != null) {
            for (int i = 0; i < this.buttons.length; ++i) {
                this.buttons[i].color = ((i == lockSelected) ? new Color(65280) : new Color(4210752));
            }
        }
        this.locked = true;
        this.update();
    }
    
    public final void unlock() {
        this.locked = false;
        this.setSelected(this.selected);
    }
    
    public final boolean isLocked() {
        return this.locked;
    }
    
    @Override
    public final Component getComponent() {
        return new ComboBox2(this);
    }
    
    @Override
    public final void fromJson(final JsonElement json) {
        if (!json.isJsonPrimitive()) {
            return;
        }
        final JsonPrimitive primitive = json.getAsJsonPrimitive();
        if (!primitive.isNumber()) {
            return;
        }
        final int selected = primitive.getAsInt();
        if (selected < 0 || selected > this.modes.length - 1) {
            return;
        }
        this.selected = selected;
        if (this.buttons != null) {
            for (int i = 0; i < this.buttons.length; ++i) {
                this.buttons[i].color = ((i == selected) ? new Color(65280) : new Color(4210752));
            }
        }
        this.update();
    }
    
    @Override
    public final JsonElement toJson() {
        return (JsonElement)new JsonPrimitive((Number)this.selected);
    }
    
    @Override
    public final void legacyFromJson(final JsonObject json) {
        int selected = this.selected;
        try {
            selected = json.get(this.getName()).getAsInt();
        }
        catch (final Exception ex) {}
        if (selected < 0 || selected > this.modes.length - 1) {
            return;
        }
        if (!this.locked) {
            this.selected = selected;
            if (this.buttons != null) {
                for (int i = 0; i < this.buttons.length; ++i) {
                    this.buttons[i].color = ((i == selected) ? new Color(65280) : new Color(4210752));
                }
            }
            this.update();
        }
    }
}
