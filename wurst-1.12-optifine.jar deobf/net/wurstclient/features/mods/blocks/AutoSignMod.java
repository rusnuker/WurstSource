// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.Category;
import net.minecraft.util.text.ITextComponent;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "auto sign" })
@HelpPage("Mods/AutoSign")
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false, antiCheat = false)
@DontSaveState
public final class AutoSignMod extends Hack
{
    private ITextComponent[] signText;
    
    public AutoSignMod() {
        super("AutoSign", "Instantly writes whatever text you want on every sign you place. Once activated, you can\nwrite normally on one sign to specify the text for all other signs.");
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public void onDisable() {
        this.signText = null;
    }
    
    public ITextComponent[] getSignText() {
        return this.signText;
    }
    
    public void setSignText(final ITextComponent[] signText) {
        if (this.isActive() && this.signText == null) {
            this.signText = signText;
        }
    }
}
