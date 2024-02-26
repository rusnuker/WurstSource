// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@SearchTags({ "turn off", "hide wurst logo", "ghost mode", "stealth mode", "vanilla Minecraft" })
public final class DisableSpf extends OtherFeature
{
    public DisableSpf() {
        super("Disable Wurst", "To disable Wurst, go to the Statistics screen and press the \"Disable Wurst\" button. It will turn\ninto an \"Enable Wurst\" button once pressed.");
    }
}
