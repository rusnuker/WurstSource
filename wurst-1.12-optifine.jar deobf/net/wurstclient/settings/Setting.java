// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.settings;

import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;
import net.wurstclient.navigator.NavigatorFeatureScreen;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.wurstclient.clickgui.Component;
import java.util.Objects;

public abstract class Setting
{
    private final String name;
    private final String description;
    
    public Setting(final String name, final String description) {
        this.name = Objects.requireNonNull(name);
        this.description = description;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final String getDescription() {
        return this.description;
    }
    
    public abstract Component getComponent();
    
    public abstract void fromJson(final JsonElement p0);
    
    public abstract JsonElement toJson();
    
    public void legacyFromJson(final JsonObject json) {
    }
    
    public void update() {
    }
    
    public void addToFeatureScreen(final NavigatorFeatureScreen featureScreen) {
    }
    
    public abstract ArrayList<PossibleKeybind> getPossibleKeybinds(final String p0);
}
