// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.gui.GuiOptionButton;

public class GuiOptionButtonOF extends GuiOptionButton implements IOptionControl
{
    private GameSettings.Options option;
    
    public GuiOptionButtonOF(final int p_i46_1_, final int p_i46_2_, final int p_i46_3_, final GameSettings.Options p_i46_4_, final String p_i46_5_) {
        super(p_i46_1_, p_i46_2_, p_i46_3_, p_i46_4_, p_i46_5_);
        this.option = null;
        this.option = p_i46_4_;
    }
    
    @Override
    public GameSettings.Options getOption() {
        return this.option;
    }
}
