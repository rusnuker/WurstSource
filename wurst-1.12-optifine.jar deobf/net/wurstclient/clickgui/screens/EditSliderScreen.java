// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.clickgui.screens;

import java.io.IOException;
import net.wurstclient.util.MathUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.settings.SliderSetting;
import net.minecraft.client.gui.GuiScreen;

public final class EditSliderScreen extends GuiScreen
{
    private final GuiScreen prevScreen;
    private final SliderSetting slider;
    private GuiTextField valueField;
    private GuiButton doneButton;
    
    public EditSliderScreen(final GuiScreen prevScreen, final SliderSetting slider) {
        this.prevScreen = prevScreen;
        this.slider = slider;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void initGui() {
        (this.valueField = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20)).setText(SliderSetting.ValueDisplay.DECIMAL.getValueString(this.slider.getValue()));
        this.valueField.setSelectionPos(0);
        this.valueField.setFocused(true);
        this.buttonList.add(this.doneButton = new GuiButton(0, this.width / 2 - 100, this.height / 3 * 2, "Done"));
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        final String value = this.valueField.getText();
        if (MathUtils.isDouble(value)) {
            this.slider.setValue(Double.parseDouble(value));
        }
        this.mc.openScreen(this.prevScreen);
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.valueField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        this.valueField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 28) {
            this.actionPerformed(this.doneButton);
        }
        else if (keyCode == 1) {
            this.mc.openScreen(this.prevScreen);
        }
    }
    
    @Override
    public void updateScreen() {
        this.valueField.updateCursorCounter();
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.slider.getName(), this.width / 2, 20, 16777215);
        this.valueField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
