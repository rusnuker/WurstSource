// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.DontBlock;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.OtherFeature;

@SearchTags({ "Clean Up" })
@DontBlock
public final class CleanUpOtf extends OtherFeature
{
    public CleanUpOtf() {
        super("CleanUp", "Cleans up your server list.\nTo use it, press the 'Clean Up' button\non the server selection screen.");
    }
}
