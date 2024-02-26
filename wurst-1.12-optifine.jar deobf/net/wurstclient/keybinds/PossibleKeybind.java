// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.keybinds;

public final class PossibleKeybind
{
    private final String command;
    private final String description;
    
    public PossibleKeybind(final String command, final String description) {
        this.command = command;
        this.description = description;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public String getDescription() {
        return this.description;
    }
}
