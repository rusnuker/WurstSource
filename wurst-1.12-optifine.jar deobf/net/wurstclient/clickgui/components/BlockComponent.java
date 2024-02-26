// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.clickgui.components;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.wurstclient.compatibility.WItem;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.gui.FontRenderer;
import net.wurstclient.clickgui.ClickGui;
import org.lwjgl.opengl.GL11;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.clickgui.screens.EditBlockScreen;
import net.wurstclient.WurstClient;
import net.wurstclient.settings.BlockSetting;
import net.wurstclient.clickgui.Component;

public final class BlockComponent extends Component
{
    private static final int BLOCK_WITDH = 24;
    private final BlockSetting setting;
    
    public BlockComponent(final BlockSetting setting) {
        this.setting = setting;
        this.setWidth(this.getDefaultWidth());
        this.setHeight(this.getDefaultHeight());
    }
    
    @Override
    public void handleMouseClick(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseX < this.getX() + this.getWidth() - 24) {
            return;
        }
        if (mouseButton == 0) {
            final GuiScreen currentScreen = WurstClient.MC.currentScreen;
            final EditBlockScreen editScreen = new EditBlockScreen(currentScreen, this.setting);
            WurstClient.MC.openScreen(editScreen);
        }
        else if (mouseButton == 1) {
            this.setting.resetToDefault();
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        final ClickGui gui = WurstClient.INSTANCE.getGui();
        final float[] bgColor = gui.getBgColor();
        final float opacity = gui.getOpacity();
        final int x1 = this.getX();
        final int x2 = x1 + this.getWidth();
        final int x3 = x2 - 24;
        final int y1 = this.getY();
        final int y2 = y1 + this.getHeight();
        final int scroll = this.getParent().isScrollingEnabled() ? this.getParent().getScrollOffset() : 0;
        final boolean hovering = mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2 && mouseY >= -scroll && mouseY < this.getParent().getHeight() - 13 - scroll;
        final boolean hText = hovering && mouseX < x3;
        final boolean hBlock = hovering && mouseX >= x3;
        final ItemStack stack = new ItemStack(this.setting.getBlock());
        if (hText) {
            gui.setTooltip(this.setting.getDescription());
        }
        else if (hBlock) {
            String tooltip = "§6Name:§r " + this.getBlockName(stack);
            tooltip = String.valueOf(tooltip) + "\n§6ID:§r " + this.setting.getBlockName();
            tooltip = String.valueOf(tooltip) + "\n\n§e[left-click]§r to edit";
            tooltip = String.valueOf(tooltip) + "\n§e[right-click]§r to reset";
            gui.setTooltip(tooltip);
        }
        GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], opacity);
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x1, y2);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x2, y1);
        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        final FontRenderer fr = WurstClient.MC.fontRendererObj;
        final String text = String.valueOf(this.setting.getName()) + ":";
        fr.drawString(text, x1, y1 + 2, 15790320);
        this.renderIcon(stack, x3, y1, true);
        GL11.glDisable(3553);
    }
    
    @Override
    public int getDefaultWidth() {
        final FontRenderer tr = WurstClient.MC.fontRendererObj;
        final String text = String.valueOf(this.setting.getName()) + ":";
        return tr.getStringWidth(text) + 24 + 4;
    }
    
    @Override
    public int getDefaultHeight() {
        return 24;
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
    
    private String getBlockName(final ItemStack stack) {
        if (WItem.isNullOrEmpty(stack)) {
            return ChatFormatting.ITALIC + "unknown block" + ChatFormatting.RESET;
        }
        return stack.getDisplayName();
    }
}
