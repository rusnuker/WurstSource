// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@SearchTags({ "Server Finder" })
@HelpPage("Special_Features/Server_Finder")
public final class ServerFinderSpf extends OtherFeature
{
    public ServerFinderSpf() {
        super("ServerFinder", "ServerFinder is a tool for finding easy-to-grief Minecraft servers quickly and with little effort.\nIt usually finds around 75 - 200 servers.");
    }
}
