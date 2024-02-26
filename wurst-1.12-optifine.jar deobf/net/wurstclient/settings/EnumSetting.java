// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.settings;

import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;
import net.wurstclient.navigator.NavigatorFeatureScreen;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import net.wurstclient.clickgui.components.ComboBox;
import net.wurstclient.clickgui.Component;
import net.wurstclient.files.ConfigFiles;
import java.util.Objects;

public final class EnumSetting<T extends Enum> extends Setting
{
    private final T[] values;
    private T selected;
    private final T defaultSelected;
    
    public EnumSetting(final String name, final String description, final T[] values, final T selected) {
        super(name, description);
        this.values = Objects.requireNonNull(values);
        this.selected = Objects.requireNonNull(selected);
        this.defaultSelected = selected;
    }
    
    public EnumSetting(final String name, final T[] values, final T selected) {
        this(name, null, values, selected);
    }
    
    public T[] getValues() {
        return this.values;
    }
    
    public T getSelected() {
        return this.selected;
    }
    
    public T getDefaultSelected() {
        return this.defaultSelected;
    }
    
    public void setSelected(final T selected) {
        this.selected = Objects.requireNonNull(selected);
        ConfigFiles.SETTINGS.save();
    }
    
    public void setSelected(final String selected) {
        T[] values;
        for (int length = (values = this.values).length, i = 0; i < length; ++i) {
            final T value = values[i];
            if (value.toString().equalsIgnoreCase(selected)) {
                this.setSelected(value);
                break;
            }
        }
    }
    
    @Override
    public Component getComponent() {
        return new ComboBox(this);
    }
    
    @Override
    public void fromJson(final JsonElement json) {
        if (!json.isJsonPrimitive()) {
            return;
        }
        final JsonPrimitive primitive = json.getAsJsonPrimitive();
        if (!primitive.isString()) {
            return;
        }
        this.setSelected(primitive.getAsString());
    }
    
    @Override
    public JsonElement toJson() {
        return (JsonElement)new JsonPrimitive(this.selected.toString());
    }
    
    @Override
    public void addToFeatureScreen(final NavigatorFeatureScreen featureScreen) {
    }
    
    @Override
    public ArrayList<PossibleKeybind> getPossibleKeybinds(final String featureName) {
        return new ArrayList<PossibleKeybind>();
    }
}
