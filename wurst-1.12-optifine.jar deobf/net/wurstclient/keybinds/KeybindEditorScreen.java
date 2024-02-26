// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.keybinds;

import java.io.IOException;
import net.wurstclient.WurstClient;
import net.wurstclient.gui.options.GuiPressAKey;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.gui.options.GuiPressAKeyCallback;
import net.minecraft.client.gui.GuiScreen;

public final class KeybindEditorScreen extends GuiScreen implements GuiPressAKeyCallback
{
    private final GuiScreen prevScreen;
    private String key;
    private final String oldKey;
    private final String oldCommands;
    private GuiTextField commandField;
    
    public KeybindEditorScreen(final GuiScreen prevScreen) {
        this.prevScreen = prevScreen;
        this.key = "NONE";
        this.oldKey = null;
        this.oldCommands = null;
    }
    
    public KeybindEditorScreen(final GuiScreen prevScreen, final String key, final String commands) {
        this.prevScreen = prevScreen;
        this.key = key;
        this.oldKey = key;
        this.oldCommands = commands;
    }
    
    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 60, "Change Key"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 72, "Save"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 96, "Cancel"));
        (this.commandField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 100, 200, 20)).setMaxStringLength(65536);
        this.commandField.setFocused(true);
        if (this.oldCommands != null) {
            this.commandField.setText(this.oldCommands);
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        switch (button.id) {
            case 0: {
                this.mc.openScreen(new GuiPressAKey(this));
                break;
            }
            case 1: {
                if (this.oldKey != null) {
                    WurstClient.INSTANCE.getKeybinds().remove(this.oldKey);
                }
                WurstClient.INSTANCE.getKeybinds().add(this.key, this.commandField.getText());
                this.mc.openScreen(this.prevScreen);
                break;
            }
            case 2: {
                this.mc.openScreen(this.prevScreen);
                break;
            }
        }
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int button) throws IOException {
        super.mouseClicked(x, y, button);
        this.commandField.mouseClicked(x, y, button);
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        this.commandField.textboxKeyTyped(typedChar, keyCode);
    }
    
    @Override
    public void updateScreen() {
        this.commandField.updateCursorCounter();
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawBackground(0);
        this.drawCenteredString(this.fontRendererObj, String.valueOf((this.oldKey != null) ? "Edit" : "Add") + " Keybind", this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, "Key: " + this.key, this.width / 2 - 100, 47, 10526880);
        this.drawString(this.fontRendererObj, "Commands (separated by ';')", this.width / 2 - 100, 87, 10526880);
        this.commandField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void setKey(final String key) {
        this.key = key;
    }
}
