// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.navigator.NavigatorMainScreen;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.DontBlock;
import net.wurstclient.features.Hack;

@DontBlock
@DontSaveState
@SearchTags({ "ClickGUI", "click gui", "SearchGUI", "search gui", "HackMenu", "hack menu" })
public final class NavigatorMod extends Hack
{
    public NavigatorMod() {
        super("Navigator", "");
    }
    
    @Override
    public void onEnable() {
        if (!(NavigatorMod.MC.currentScreen instanceof NavigatorMainScreen)) {
            NavigatorMod.MC.openScreen(new NavigatorMainScreen());
        }
        this.setEnabled(false);
    }
}
