// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui.options;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;

public class GuiPressAKey extends GuiScreen
{
    private GuiPressAKeyCallback prevScreen;
    
    public GuiPressAKey(final GuiPressAKeyCallback prevScreen) {
        if (!(prevScreen instanceof GuiScreen)) {
            throw new IllegalArgumentException("prevScreen is not an instance of GuiScreen");
        }
        this.prevScreen = prevScreen;
    }
    
    @Override
    protected void keyTyped(final char par1, final int par2) {
        this.prevScreen.setKey(Keyboard.getKeyName(par2));
        this.mc.openScreen((GuiScreen)this.prevScreen);
    }
    
    @Override
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawBackground(0);
        this.drawCenteredString(this.fontRendererObj, "Press a key", this.width / 2, this.height / 4 + 48, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}
