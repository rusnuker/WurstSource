// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
public final class ReconnectOtf extends OtherFeature
{
    public ReconnectOtf() {
        super("Reconnect", "Whenever you get kicked from a server, Wurst gives you a\n\"Reconnect\" button that lets you instantly join again.");
    }
}
