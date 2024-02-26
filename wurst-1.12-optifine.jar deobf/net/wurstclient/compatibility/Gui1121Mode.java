// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.wurstclient.update.Version;
import java.io.IOException;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class Gui1121Mode extends GuiScreen
{
    private final GuiScreen prevScreen;
    
    public Gui1121Mode(final GuiScreen prevScreen) {
        this.prevScreen = prevScreen;
    }
    
    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 150, this.height / 3 * 2, 100, 20, "MC 1.12"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 50, this.height / 3 * 2, 100, 20, "MC 1.12.1"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 50, this.height / 3 * 2, 100, 20, "MC 1.12.2"));
        this.buttonList.add(new GuiButton(-1, this.width / 2 - 100, this.height / 3 * 2 + 40, "Cancel"));
        final int version = WurstClient.INSTANCE.options.mc112x_compatibility;
        if (version >= 0 && version < this.buttonList.size() - 1) {
            this.buttonList.get(version).enabled = false;
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
            case 1:
            case 2: {
                WurstClient.INSTANCE.options.mc112x_compatibility = button.id;
                ConfigFiles.OPTIONS.save();
                this.mc.shutdown();
                break;
            }
            case -1: {
                this.mc.openScreen(this.prevScreen);
                break;
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Minecraft 1.12.X Compatibility Mode", this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, "\ufffdaCurrent version: " + new Version("1.12." + WurstClient.INSTANCE.options.mc112x_compatibility), this.width / 2, 80, 10526880);
        this.drawCenteredString(this.fontRendererObj, "Only one version can be selected at any time.", this.width / 2, 110, 10526880);
        this.drawCenteredString(this.fontRendererObj, "Changing this option requires the game to be restarted.", this.width / 2, 120, 10526880);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
