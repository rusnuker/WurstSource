// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.clickgui.screens;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.client.renderer.RenderHelper;
import net.wurstclient.compatibility.WItem;
import net.minecraft.client.gui.FontRenderer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import java.io.IOException;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.item.Item;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.settings.ItemListSetting;
import net.minecraft.client.gui.GuiScreen;

public final class EditItemListScreen extends GuiScreen
{
    private final GuiScreen prevScreen;
    private final ItemListSetting itemList;
    private ListGui listGui;
    private GuiTextField itemNameField;
    private GuiButton addButton;
    private GuiButton removeButton;
    private GuiButton doneButton;
    private Item itemToAdd;
    
    public EditItemListScreen(final GuiScreen prevScreen, final ItemListSetting itemList) {
        this.prevScreen = prevScreen;
        this.itemList = itemList;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void initGui() {
        (this.listGui = new ListGui(this.mc, this, this.itemList.getItemNames())).registerScrollButtons(7, 8);
        this.itemNameField = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 152, this.height - 55, 150, 18);
        this.buttonList.add(this.addButton = new GuiButton(0, this.width / 2 - 2, this.height - 56, 30, 20, "Add"));
        this.buttonList.add(this.removeButton = new GuiButton(1, this.width / 2 + 52, this.height - 56, 100, 20, "Remove Selected"));
        this.buttonList.add(new GuiButton(2, this.width - 108, 8, 100, 20, "Reset to Defaults"));
        this.buttonList.add(this.doneButton = new GuiButton(3, this.width / 2 - 100, this.height - 28, "Done"));
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) {
        if (!button.enabled) {
            return;
        }
        switch (button.id) {
            case 0: {
                this.itemList.add(this.itemToAdd);
                this.itemNameField.setText("");
                break;
            }
            case 1: {
                this.itemList.remove(this.listGui.selected);
                break;
            }
            case 2: {
                this.mc.openScreen(new GuiYesNo(this, "Reset to Defaults", "Are you sure?", 0));
                break;
            }
            case 3: {
                this.mc.openScreen(this.prevScreen);
                break;
            }
        }
    }
    
    @Override
    public void confirmClicked(final boolean result, final int id) {
        if (id == 0 && result) {
            this.itemList.resetToDefaults();
        }
        super.confirmClicked(result, id);
        this.mc.openScreen(this);
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.listGui.handleMouseInput();
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.itemNameField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX < (this.width - 220) / 2 || mouseX > this.width / 2 + 129 || mouseY < 32 || mouseY > this.height - 64) {
            ListGui.access$1(this.listGui, -1);
        }
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        this.itemNameField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 28) {
            this.actionPerformed(this.addButton);
        }
        else if (keyCode == 211) {
            this.actionPerformed(this.removeButton);
        }
        else if (keyCode == 1) {
            this.actionPerformed(this.doneButton);
        }
    }
    
    @Override
    public void updateScreen() {
        this.itemNameField.updateCursorCounter();
        this.itemToAdd = Item.getByNameOrId(this.itemNameField.getText());
        this.addButton.enabled = (this.itemToAdd != null);
        this.removeButton.enabled = (this.listGui.selected >= 0 && this.listGui.selected < this.listGui.list.size());
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.listGui.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.mc.fontRendererObj, String.valueOf(this.itemList.getName()) + " (" + this.listGui.getSize() + ")", this.width / 2, 12, 16777215);
        this.itemNameField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glPushMatrix();
        GL11.glTranslated((double)(-64 + this.width / 2 - 152), 0.0, 0.0);
        if (this.itemNameField.getText().isEmpty() && !this.itemNameField.isFocused()) {
            this.drawString(this.mc.fontRendererObj, "item name or ID", 68, this.height - 50, 8421504);
        }
        Gui.drawRect(48, this.height - 56, 64, this.height - 36, -6250336);
        Gui.drawRect(49, this.height - 55, 64, this.height - 37, -16777216);
        Gui.drawRect(214, this.height - 56, 244, this.height - 55, -6250336);
        Gui.drawRect(214, this.height - 37, 244, this.height - 36, -6250336);
        Gui.drawRect(244, this.height - 56, 246, this.height - 36, -6250336);
        Gui.drawRect(214, this.height - 55, 243, this.height - 52, -16777216);
        Gui.drawRect(214, this.height - 40, 243, this.height - 37, -16777216);
        Gui.drawRect(215, this.height - 55, 216, this.height - 37, -16777216);
        Gui.drawRect(242, this.height - 55, 245, this.height - 37, -16777216);
        this.listGui.renderIconAndGetName(new ItemStack(this.itemToAdd), 52, this.height - 52, false);
        GL11.glPopMatrix();
    }
    
    private static class ListGui extends GuiSlot
    {
        private final Minecraft mc;
        private final List<String> list;
        private int selected;
        
        public ListGui(final Minecraft mc, final EditItemListScreen screen, final List<String> list) {
            super(mc, screen.width, screen.height, 32, screen.height - 64, 30);
            this.selected = -1;
            this.mc = mc;
            this.list = list;
        }
        
        @Override
        protected int getSize() {
            return this.list.size();
        }
        
        @Override
        protected void elementClicked(final int index, final boolean var2, final int var3, final int var4) {
            if (index >= 0 && index < this.list.size()) {
                this.selected = index;
            }
        }
        
        @Override
        protected boolean isSelected(final int index) {
            return index == this.selected;
        }
        
        @Override
        protected void drawBackground() {
        }
        
        @Override
        protected void drawSlot(final int index, final int x, final int y, final int var4, final int var5, final int var6, final float partialTicks) {
            final String name = this.list.get(index);
            final ItemStack stack = new ItemStack(Item.getByNameOrId(name));
            final FontRenderer fr = this.mc.fontRendererObj;
            final String displayName = this.renderIconAndGetName(stack, x + 1, y + 1, true);
            fr.drawString(displayName, x + 28, y, 15790320);
            fr.drawString(name, x + 28, y + 9, 10526880);
            fr.drawString("ID: " + Item.REGISTRY.getNameForObject(Item.getByNameOrId(name)), x + 28, y + 18, 10526880);
        }
        
        private String renderIconAndGetName(final ItemStack stack, final int x, final int y, final boolean large) {
            if (WItem.isNullOrEmpty(stack)) {
                GL11.glPushMatrix();
                GL11.glTranslated((double)x, (double)y, 0.0);
                if (large) {
                    GL11.glScaled(1.5, 1.5, 1.5);
                }
                else {
                    GL11.glScaled(0.75, 0.75, 0.75);
                }
                RenderHelper.enableGUIStandardItemLighting();
                this.mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Blocks.GRASS), 0, 0);
                RenderHelper.disableStandardItemLighting();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated((double)x, (double)y, 0.0);
                if (large) {
                    GL11.glScaled(2.0, 2.0, 2.0);
                }
                GL11.glDisable(2929);
                final FontRenderer fr = this.mc.fontRendererObj;
                fr.drawString("?", 3.0f, 2.0f, 15790320, true);
                GL11.glEnable(2929);
                GL11.glPopMatrix();
                return ChatFormatting.ITALIC + "unknown item" + ChatFormatting.RESET;
            }
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, 0.0);
            if (large) {
                GL11.glScaled(1.5, 1.5, 1.5);
            }
            else {
                GL11.glScaled(0.75, 0.75, 0.75);
            }
            RenderHelper.enableGUIStandardItemLighting();
            this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
            RenderHelper.disableStandardItemLighting();
            GL11.glPopMatrix();
            return stack.getDisplayName();
        }
        
        static /* synthetic */ void access$1(final ListGui listGui, final int selected) {
            listGui.selected = selected;
        }
    }
}
