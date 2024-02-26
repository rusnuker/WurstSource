// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.gui.GuiOptionSlider;

public class GuiOptionSliderOF extends GuiOptionSlider implements IOptionControl
{
    private GameSettings.Options option;
    
    public GuiOptionSliderOF(final int p_i47_1_, final int p_i47_2_, final int p_i47_3_, final GameSettings.Options p_i47_4_) {
        super(p_i47_1_, p_i47_2_, p_i47_3_, p_i47_4_);
        this.option = null;
        this.option = p_i47_4_;
    }
    
    @Override
    public GameSettings.Options getOption() {
        return this.option;
    }
}
