// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.keybinds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import java.io.IOException;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.GuiYesNo;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public final class KeybindManagerScreen extends GuiScreen
{
    private final GuiScreen prevScreen;
    private ListGui listGui;
    private GuiButton addButton;
    private GuiButton editButton;
    private GuiButton removeButton;
    private GuiButton backButton;
    
    public KeybindManagerScreen(final GuiScreen prevScreen) {
        this.prevScreen = prevScreen;
    }
    
    @Override
    public void initGui() {
        this.listGui = new ListGui(this.mc, this.width, this.height, 36, this.height - 56, 30);
        this.buttonList.add(this.addButton = new GuiButton(0, this.width / 2 - 102, this.height - 52, 100, 20, "Add"));
        this.buttonList.add(this.editButton = new GuiButton(1, this.width / 2 + 2, this.height - 52, 100, 20, "Edit"));
        this.buttonList.add(this.removeButton = new GuiButton(2, this.width / 2 - 102, this.height - 28, 100, 20, "Remove"));
        this.buttonList.add(this.backButton = new GuiButton(3, this.width / 2 + 2, this.height - 28, 100, 20, "Back"));
        this.buttonList.add(new GuiButton(4, 8, 8, 100, 20, "Reset Keybinds"));
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        switch (button.id) {
            case 0: {
                this.mc.openScreen(new KeybindEditorScreen(this));
                break;
            }
            case 1: {
                final KeybindList.Keybind keybind = WurstClient.INSTANCE.getKeybinds().get(this.listGui.selectedSlot);
                this.mc.openScreen(new KeybindEditorScreen(this, keybind.getKey(), keybind.getCommands()));
                break;
            }
            case 2: {
                final KeybindList.Keybind keybind2 = WurstClient.INSTANCE.getKeybinds().get(this.listGui.selectedSlot);
                WurstClient.INSTANCE.getKeybinds().remove(keybind2.getKey());
                break;
            }
            case 3: {
                this.mc.openScreen(this.prevScreen);
                break;
            }
            case 4: {
                this.mc.openScreen(new GuiYesNo(this, "Are you sure you want to reset your keybinds?", "This cannot be undone!", 0));
                break;
            }
        }
    }
    
    @Override
    public void confirmClicked(final boolean confirmed, final int id) {
        if (confirmed) {
            WurstClient.INSTANCE.getKeybinds().loadDefaults();
        }
        this.mc.openScreen(this);
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.listGui.handleMouseInput();
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int button) throws IOException {
        if (y >= 36 && y <= this.height - 57 && (x >= this.width / 2 + 140 || x <= this.width / 2 - 126)) {
            this.listGui.elementClicked(-1, false, 0, 0);
        }
        super.mouseClicked(x, y, button);
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 28) {
            this.actionPerformed(this.editButton.enabled ? this.editButton : this.addButton);
        }
        else if (keyCode == 1) {
            this.actionPerformed(this.backButton);
        }
    }
    
    @Override
    public void updateScreen() {
        final boolean inBounds = this.listGui.selectedSlot > -1 && this.listGui.selectedSlot < this.listGui.getSize();
        this.editButton.enabled = inBounds;
        this.removeButton.enabled = inBounds;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.listGui.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, "Keybind Manager", this.width / 2, 8, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Keybinds: " + this.listGui.getSize(), this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    private static final class ListGui extends GuiSlot
    {
        private int selectedSlot;
        
        public ListGui(final Minecraft mc, final int width, final int height, final int top, final int bottom, final int slotHeight) {
            super(mc, width, height, top, bottom, slotHeight);
            this.selectedSlot = -1;
        }
        
        @Override
        protected boolean isSelected(final int index) {
            return this.selectedSlot == index;
        }
        
        @Override
        protected int getSize() {
            return WurstClient.INSTANCE.getKeybinds().size();
        }
        
        @Override
        protected void elementClicked(final int index, final boolean isDoubleClick, final int mouseX, final int mouseY) {
            this.selectedSlot = index;
        }
        
        @Override
        protected void drawBackground() {
        }
        
        @Override
        protected void drawSlot(final int id, final int x, final int y, final int slotHeight, final int mouseX, final int mouseY, final float partialTicks) {
            final KeybindList.Keybind keybind = WurstClient.INSTANCE.getKeybinds().get(id);
            this.mc.fontRendererObj.drawString("Key: " + keybind.getKey(), x + 3, y + 3, 10526880);
            this.mc.fontRendererObj.drawString("Commands: " + keybind.getCommands(), x + 3, y + 15, 10526880);
        }
    }
}
