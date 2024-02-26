// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.util.MiscUtils;
import net.wurstclient.update.Version;
import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@SearchTags({ "change log", "new features", "wurst update" })
public final class ChangelogSpf extends OtherFeature
{
    public ChangelogSpf() {
        super("Changelog", "Opens the changelog in your browser.");
    }
    
    @Override
    public String getPrimaryAction() {
        return "View Changelog";
    }
    
    @Override
    public void doPrimaryAction() {
        MiscUtils.openLink(new Version("6.35.3").getChangelogLink());
    }
}
