// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.utils.InventoryUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "CmdBlock", "CommandBlock", "cmd block", "command block" })
@Bypasses
public final class CmdBlockMod extends Hack
{
    public CmdBlockMod() {
        super("CMD-Block", "Allows you to make a Command Block without having OP.\nRequires creative mode.\nAppears to be patched on Spigot.");
        this.setCategory(Category.ITEMS);
    }
    
    @Override
    public void onEnable() {
        if (!CmdBlockMod.MC.player.abilities.creativeMode) {
            ChatUtils.error("Creative mode only.");
            this.setEnabled(false);
            return;
        }
        CmdBlockMod.MC.openScreen(new GuiCmdBlock(CmdBlockMod.MC.currentScreen));
        this.setEnabled(false);
    }
    
    public void createCmdBlock(final String cmd) {
        final ItemStack stack = new ItemStack(Blocks.COMMAND_BLOCK);
        final NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setTag("Command", new NBTTagString(cmd));
        stack.writeToNBT(nbtTagCompound);
        stack.setTagInfo("BlockEntityTag", nbtTagCompound);
        if (InventoryUtils.placeStackInHotbar(stack)) {
            ChatUtils.message("Command Block created.");
        }
        else {
            ChatUtils.error("Please clear a slot in your hotbar.");
        }
    }
    
    private class GuiCmdBlock extends GuiScreen
    {
        private GuiScreen prevScreen;
        private GuiTextField commandBox;
        
        public GuiCmdBlock(final GuiScreen prevScreen) {
            this.prevScreen = prevScreen;
        }
        
        @Override
        public void initGui() {
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
                    Minecraft.getMinecraft().openScreen(this.prevScreen);
                    CmdBlockMod.this.createCmdBlock(this.commandBox.getText());
                    break;
                }
                case 1: {
                    Minecraft.getMinecraft().openScreen(this.prevScreen);
                    break;
                }
            }
        }
        
        @Override
        public void updateScreen() {
            this.commandBox.updateCursorCounter();
        }
        
        @Override
        protected void keyTyped(final char typedChar, final int keyCode) {
            this.commandBox.textboxKeyTyped(typedChar, keyCode);
        }
        
        @Override
        protected void mouseClicked(final int x, final int y, final int button) throws IOException {
            super.mouseClicked(x, y, button);
            this.commandBox.mouseClicked(x, y, button);
        }
        
        @Override
        public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, "CMD-Block", this.width / 2, 20, 16777215);
            this.drawString(this.fontRendererObj, "Command", this.width / 2 - 100, 47, 10526880);
            this.drawCenteredString(this.fontRendererObj, "The command you type in here will be", this.width / 2, 100, 10526880);
            this.drawCenteredString(this.fontRendererObj, "executed by the Command Block.", this.width / 2, 110, 10526880);
            this.commandBox.drawTextBox();
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}
