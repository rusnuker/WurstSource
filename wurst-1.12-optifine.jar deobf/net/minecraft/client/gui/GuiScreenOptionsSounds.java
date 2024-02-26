// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.Minecraft;
import java.io.IOException;
import net.minecraft.util.SoundCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiScreenOptionsSounds extends GuiScreen
{
    private final GuiScreen parent;
    private final GameSettings game_settings_4;
    protected String title;
    private String offDisplayString;
    
    public GuiScreenOptionsSounds(final GuiScreen parentIn, final GameSettings settingsIn) {
        this.title = "Options";
        this.parent = parentIn;
        this.game_settings_4 = settingsIn;
    }
    
    @Override
    public void initGui() {
        this.title = I18n.format("options.sounds.title", new Object[0]);
        this.offDisplayString = I18n.format("options.off", new Object[0]);
        int i = 0;
        this.buttonList.add(new Button(SoundCategory.MASTER.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true));
        i += 2;
        SoundCategory[] values;
        for (int length = (values = SoundCategory.values()).length, l = 0; l < length; ++l) {
            final SoundCategory soundcategory = values[l];
            if (soundcategory != SoundCategory.MASTER) {
                this.buttonList.add(new Button(soundcategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, false));
                ++i;
            }
        }
        final int j = this.width / 2 - 75;
        final int k = this.height / 6 - 12;
        ++i;
        this.buttonList.add(new GuiOptionButton(201, j, k + 24 * (i >> 1), GameSettings.Options.SHOW_SUBTITLES, this.game_settings_4.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES)));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done", new Object[0])));
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.gameSettings.saveOptions();
        }
        super.keyTyped(typedChar, keyCode);
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.openScreen(this.parent);
            }
            else if (button.id == 201) {
                this.mc.gameSettings.setOptionValue(GameSettings.Options.SHOW_SUBTITLES, 1);
                button.displayString = this.mc.gameSettings.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES);
                this.mc.gameSettings.saveOptions();
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    protected String getDisplayString(final SoundCategory category) {
        final float f = this.game_settings_4.getSoundLevel(category);
        return (f == 0.0f) ? this.offDisplayString : (String.valueOf((int)(f * 100.0f)) + "%");
    }
    
    class Button extends GuiButton
    {
        private final SoundCategory category;
        private final String categoryName;
        public float volume;
        public boolean pressed;
        
        public Button(final int p_i46744_2_, final int x, final int y, final SoundCategory categoryIn, final boolean master) {
            super(p_i46744_2_, x, y, master ? 310 : 150, 20, "");
            this.volume = 1.0f;
            this.category = categoryIn;
            this.categoryName = I18n.format("soundCategory." + categoryIn.getName(), new Object[0]);
            this.displayString = String.valueOf(this.categoryName) + ": " + GuiScreenOptionsSounds.this.getDisplayString(categoryIn);
            this.volume = GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(categoryIn);
        }
        
        @Override
        protected int getHoverState(final boolean mouseOver) {
            return 0;
        }
        
        @Override
        protected void mouseDragged(final Minecraft mc, final int mouseX, final int mouseY) {
            if (this.visible) {
                if (this.pressed) {
                    this.volume = (mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                    this.volume = MathHelper.clamp(this.volume, 0.0f, 1.0f);
                    mc.gameSettings.setSoundLevel(this.category, this.volume);
                    mc.gameSettings.saveOptions();
                    this.displayString = String.valueOf(this.categoryName) + ": " + GuiScreenOptionsSounds.this.getDisplayString(this.category);
                }
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                this.drawTexturedModalRect(this.xPosition + (int)(this.volume * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
                this.drawTexturedModalRect(this.xPosition + (int)(this.volume * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
            }
        }
        
        @Override
        public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                this.volume = (mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.volume = MathHelper.clamp(this.volume, 0.0f, 1.0f);
                mc.gameSettings.setSoundLevel(this.category, this.volume);
                mc.gameSettings.saveOptions();
                this.displayString = String.valueOf(this.categoryName) + ": " + GuiScreenOptionsSounds.this.getDisplayString(this.category);
                return this.pressed = true;
            }
            return false;
        }
        
        @Override
        public void playPressSound(final SoundHandler soundHandlerIn) {
        }
        
        @Override
        public void mouseReleased(final int mouseX, final int mouseY) {
            if (this.pressed) {
                GuiScreenOptionsSounds.this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            this.pressed = false;
        }
    }
}
