// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.settings;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import net.wurstclient.clickgui.components.Checkbox;
import net.wurstclient.clickgui.Component;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;
import net.wurstclient.navigator.NavigatorFeatureScreen;

public class CheckboxSetting extends Setting implements CheckboxLock
{
    private boolean checked;
    private final boolean checkedByDefault;
    private CheckboxLock lock;
    private int y;
    
    public CheckboxSetting(final String name, final String description, final boolean checked) {
        super(name, description);
        this.checked = checked;
        this.checkedByDefault = checked;
    }
    
    public CheckboxSetting(final String name, final boolean checked) {
        this(name, null, checked);
    }
    
    @Override
    public final void addToFeatureScreen(final NavigatorFeatureScreen featureScreen) {
        this.y = 60 + featureScreen.getTextHeight() + 4;
        featureScreen.addText("\n\n");
        featureScreen.addCheckbox(this);
    }
    
    @Override
    public final ArrayList<PossibleKeybind> getPossibleKeybinds(final String featureName) {
        final ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<PossibleKeybind>();
        final String fullName = String.valueOf(featureName) + " " + this.getName();
        final String command = ".setcheckbox " + featureName.toLowerCase() + " " + this.getName().toLowerCase().replace(" ", "_") + " ";
        possibleKeybinds.add(new PossibleKeybind(String.valueOf(command) + "toggle", "Toggle " + fullName));
        possibleKeybinds.add(new PossibleKeybind(String.valueOf(command) + "on", "Enable " + fullName));
        possibleKeybinds.add(new PossibleKeybind(String.valueOf(command) + "off", "Disable " + fullName));
        return possibleKeybinds;
    }
    
    @Override
    public final boolean isChecked() {
        return this.isLocked() ? this.lock.isChecked() : this.checked;
    }
    
    public final boolean isCheckedByDefault() {
        return this.checkedByDefault;
    }
    
    public final void setChecked(final boolean checked) {
        if (this.isLocked()) {
            return;
        }
        this.checked = checked;
        this.update();
        ConfigFiles.SETTINGS.save();
    }
    
    public final void toggle() {
        this.setChecked(!this.isChecked());
    }
    
    public final void lock(final CheckboxLock lock) {
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
    
    public final int getY() {
        return this.y;
    }
    
    @Override
    public final Component getComponent() {
        return new Checkbox(this);
    }
    
    @Override
    public final void fromJson(final JsonElement json) {
        if (!json.isJsonPrimitive()) {
            return;
        }
        final JsonPrimitive primitive = json.getAsJsonPrimitive();
        if (!primitive.isBoolean()) {
            return;
        }
        this.checked = primitive.getAsBoolean();
        this.update();
    }
    
    @Override
    public final JsonElement toJson() {
        return (JsonElement)new JsonPrimitive(Boolean.valueOf(this.checked));
    }
    
    @Override
    public final void legacyFromJson(final JsonObject json) {
        try {
            this.checked = json.get(this.getName()).getAsBoolean();
        }
        catch (final Exception ex) {}
        this.update();
    }
}
