// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@SearchTags({ "tab gui", "HackMenu", "hack menu", "SideBar", "side bar", "blocks movement combat render chat fun items retro other" })
public final class TabGuiSpf extends OtherFeature
{
    private final ModeSetting status;
    
    public TabGuiSpf() {
        super("TabGui", "Allows you to quickly toggle mods while playing.\nUse the arrow keys to navigate.");
        this.addSetting(this.status = new ModeSetting("Status", new String[] { "Enabled", "Disabled" }, 1));
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { TabGuiSpf.WURST.special.hackListSpf };
    }
    
    public boolean isHidden() {
        return this.status.getSelected() == 1;
    }
}
