// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.clickgui.screens;

import java.io.IOException;
import net.wurstclient.clickgui.ClickGui;
import net.minecraft.client.gui.GuiScreen;

public final class ClickGuiScreen extends GuiScreen
{
    private final ClickGui gui;
    
    public ClickGuiScreen(final ClickGui gui) {
        this.gui = gui;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.gui.handleMouseClick(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.gui.render(mouseX, mouseY, partialTicks);
    }
}
