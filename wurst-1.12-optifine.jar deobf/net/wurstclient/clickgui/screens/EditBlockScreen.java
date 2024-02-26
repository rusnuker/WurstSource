// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.clickgui.screens;

import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WItem;
import net.minecraft.init.Blocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import net.minecraft.block.Block;
import net.wurstclient.util.BlockUtils;
import java.io.IOException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.settings.BlockSetting;
import net.minecraft.client.gui.GuiScreen;

public final class EditBlockScreen extends GuiScreen
{
    private final GuiScreen prevScreen;
    private final BlockSetting setting;
    private GuiTextField blockField;
    private GuiButton doneButton;
    
    public EditBlockScreen(final GuiScreen prevScreen, final BlockSetting setting) {
        this.prevScreen = prevScreen;
        this.setting = setting;
    }
    
    @Override
    public void initGui() {
        final int x1 = this.width / 2 - 100;
        final int y1 = 60;
        final int y2 = this.height / 3 * 2;
        final FontRenderer tr = this.mc.fontRendererObj;
        final String valueString = this.setting.getBlockName();
        (this.blockField = new GuiTextField(0, tr, x1, y1, 178, 18)).setText(valueString);
        this.blockField.setCursorPosition(0);
        this.blockField.setFocused(true);
        this.doneButton = new GuiButton(1, x1, y2, 200, 20, "Done");
        this.buttonList.add(this.doneButton);
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {
            case 1: {
                this.done();
                break;
            }
        }
    }
    
    private void done() {
        final String value = this.blockField.getText();
        final Block block = BlockUtils.getBlockFromName(value);
        if (block != null) {
            this.setting.setBlock(block);
        }
        this.mc.openScreen(this.prevScreen);
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.blockField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        switch (keyCode) {
            case 28: {
                this.done();
                break;
            }
            case 1: {
                this.mc.openScreen(this.prevScreen);
                break;
            }
        }
        this.blockField.textboxKeyTyped(typedChar, keyCode);
    }
    
    @Override
    public void updateScreen() {
        this.blockField.updateCursorCounter();
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        final FontRenderer tr = this.mc.fontRendererObj;
        this.drawDefaultBackground();
        this.drawCenteredString(tr, this.setting.getName(), this.width / 2, 20, 16777215);
        this.blockField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glPushMatrix();
        GL11.glTranslated((double)(-64 + this.width / 2 - 100), 115.0, 0.0);
        final boolean lblAbove = !this.blockField.getText().isEmpty() || this.blockField.isFocused();
        final String lblText = lblAbove ? "Block name or ID:" : "block name or ID";
        final int lblX = lblAbove ? 50 : 68;
        final int lblY = lblAbove ? -66 : -50;
        final int lblColor = lblAbove ? 15790320 : 8421504;
        this.drawString(tr, lblText, lblX, lblY, lblColor);
        Gui.drawRect(48, -56, 64, -36, -6250336);
        Gui.drawRect(49, -55, 64, -37, -16777216);
        Gui.drawRect(214, -56, 244, -55, -6250336);
        Gui.drawRect(214, -37, 244, -36, -6250336);
        Gui.drawRect(244, -56, 246, -36, -6250336);
        Gui.drawRect(214, -55, 243, -52, -16777216);
        Gui.drawRect(214, -40, 243, -37, -16777216);
        Gui.drawRect(215, -55, 216, -37, -16777216);
        Gui.drawRect(242, -55, 245, -37, -16777216);
        final Block blockToAdd = BlockUtils.getBlockFromName(this.blockField.getText());
        this.renderIcon(new ItemStack(blockToAdd), 52, -52, false);
        GL11.glPopMatrix();
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    private void renderIcon(final ItemStack stack, final int x, final int y, final boolean large) {
        GL11.glPushMatrix();
        GL11.glTranslated((double)x, (double)y, 0.0);
        final double scale = large ? 1.5 : 0.75;
        GL11.glScaled(scale, scale, scale);
        RenderHelper.enableGUIStandardItemLighting();
        final ItemStack grass = new ItemStack(Blocks.GRASS);
        final ItemStack renderStack = WItem.isNullOrEmpty(stack) ? grass : stack;
        WurstClient.MC.getRenderItem().renderItemAndEffectIntoGUI(renderStack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        if (WItem.isNullOrEmpty(stack)) {
            this.renderQuestionMark(x, y, large);
        }
    }
    
    private void renderQuestionMark(final int x, final int y, final boolean large) {
        GL11.glPushMatrix();
        GL11.glTranslated((double)x, (double)y, 0.0);
        if (large) {
            GL11.glScaled(2.0, 2.0, 2.0);
        }
        GL11.glDisable(2929);
        final FontRenderer tr = WurstClient.MC.fontRendererObj;
        tr.drawStringWithShadow("?", 3.0f, 2.0f, 15790320);
        GL11.glEnable(2929);
        GL11.glPopMatrix();
    }
}
