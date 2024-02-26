// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features;

public abstract class RetroMod extends Hack
{
    private static final String NOTICE = "\n\n§6§lNotice:§r This mod only works on servers that are based on Minecraft 1.8 (such as Mineplex).";
    
    public RetroMod(final String name, final String description) {
        super(name, String.valueOf(description) + "\n\n§6§lNotice:§r This mod only works on servers that are based on Minecraft 1.8 (such as Mineplex).");
    }
}
