// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features;

import java.util.Iterator;
import java.util.Collection;
import net.wurstclient.settings.Setting;
import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;

public abstract class OtherFeature extends Feature
{
    private final String name;
    private final String description;
    
    public OtherFeature(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
    
    @Override
    public final String getName() {
        return this.name;
    }
    
    @Override
    public final String getType() {
        return "Other Feature";
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public boolean isEnabled() {
        return false;
    }
    
    @Override
    public boolean isBlocked() {
        return false;
    }
    
    @Override
    public ArrayList<PossibleKeybind> getPossibleKeybinds() {
        final ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<PossibleKeybind>();
        for (final Setting setting : this.getSettings()) {
            possibleKeybinds.addAll(setting.getPossibleKeybinds(this.name));
        }
        return possibleKeybinds;
    }
    
    @Override
    public String getPrimaryAction() {
        return "";
    }
    
    @Override
    public void doPrimaryAction() {
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[0];
    }
}
