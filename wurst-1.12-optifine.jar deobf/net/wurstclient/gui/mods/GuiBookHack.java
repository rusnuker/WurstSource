// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui.mods;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreen;

public class GuiBookHack extends GuiScreen
{
    private GuiScreenBook prevScreen;
    private GuiTextField commandBox;
    
    public GuiBookHack(final GuiScreenBook prevScreen) {
        this.prevScreen = prevScreen;
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 3 * 2, 200, 20, "Done"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 3 * 2 + 24, 200, 20, "Cancel"));
        (this.commandBox = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20)).setMaxStringLength(100);
        this.commandBox.setFocused(true);
        this.commandBox.setText("/");
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        switch (button.id) {
            case 0: {
                this.prevScreen.signWithCommand(this.commandBox.getText());
                break;
            }
            case 1: {
                this.mc.openScreen(this.prevScreen);
                break;
            }
        }
    }
    
    @Override
    public void updateScreen() {
        this.commandBox.updateCursorCounter();
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int button) throws IOException {
        super.mouseClicked(x, y, button);
        this.commandBox.mouseClicked(x, y, button);
    }
    
    @Override
    protected void keyTyped(final char ch, final int keyCode) {
        this.commandBox.textboxKeyTyped(ch, keyCode);
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "BookHack", this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, "Command", this.width / 2 - 100, 47, 10526880);
        this.drawCenteredString(this.fontRendererObj, "The command you type in here will be", this.width / 2, 100, 10526880);
        this.drawCenteredString(this.fontRendererObj, "executed by anyone who clicks the text", this.width / 2, 110, 10526880);
        this.drawCenteredString(this.fontRendererObj, "in your book.", this.width / 2, 120, 10526880);
        this.commandBox.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
