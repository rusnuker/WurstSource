// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.DontBlock;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.OtherFeature;

@SearchTags({ "last server" })
@DontBlock
public final class LastServerOtf extends OtherFeature
{
    public LastServerOtf() {
        super("LastServer", "Wurst adds a \"Last Server\" button to the server selection\nscreen that automatically brings you back to the last\nserver you played on.\n\nUseful when you get kicked and/or have a lot of servers.");
    }
}
