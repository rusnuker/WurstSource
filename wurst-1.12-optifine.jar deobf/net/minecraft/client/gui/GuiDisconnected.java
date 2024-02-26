// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import java.util.Iterator;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.hooks.ServerHook;
import net.wurstclient.WurstClient;
import java.io.IOException;
import net.minecraft.client.resources.I18n;
import java.util.List;
import net.minecraft.util.text.ITextComponent;

public class GuiDisconnected extends GuiScreen
{
    private final String reason;
    private final ITextComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int textHeight;
    private int autoReconnectTimer;
    
    public GuiDisconnected(final GuiScreen screen, final String reasonLocalizationKey, final ITextComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
        System.out.println("Disconnected.\n    Message: " + ((this.message == null) ? null : this.message.getUnformattedText()) + "\n    Reason: " + this.reason);
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.textHeight = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT, this.height - 30), I18n.format("gui.toMenu", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT + 24, "Reconnect"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT + 48, "AutoReconnect"));
        if ((this.message.getUnformattedText().toLowerCase().contains("whitelist") || this.message.getUnformattedText().toLowerCase().contains("white-list")) && this.parentScreen instanceof GuiMultiplayer && ((GuiMultiplayer)this.parentScreen).serverListSelector.getSelected() != -1) {
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT + 72, "\ufffd4Delete Server\ufffdr"));
        }
        if (WurstClient.INSTANCE.options.autoReconnect) {
            this.autoReconnectTimer = 100;
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.openScreen(this.parentScreen);
        }
        else if (button.id == 1) {
            ServerHook.reconnectToLastServer(this.parentScreen);
        }
        else if (button.id == 2) {
            WurstClient.INSTANCE.options.autoReconnect = !WurstClient.INSTANCE.options.autoReconnect;
            ConfigFiles.OPTIONS.save();
            if (WurstClient.INSTANCE.options.autoReconnect) {
                this.autoReconnectTimer = 100;
            }
        }
        else if (button.id == 3) {
            final GuiMultiplayer servers = (GuiMultiplayer)this.parentScreen;
            servers.savedServerList.removeServerData(servers.serverListSelector.getSelected());
            servers.savedServerList.saveServerList();
            servers.serverListSelector.setSelectedSlotIndex(-1);
            servers.serverListSelector.updateOnlineServers(servers.savedServerList);
            this.mc.openScreen(servers);
        }
    }
    
    @Override
    public void updateScreen() {
        if (WurstClient.INSTANCE.options.autoReconnect) {
            if (this.buttonList.size() < 2) {
                return;
            }
            this.buttonList.get(2).displayString = "AutoReconnect (" + (this.autoReconnectTimer / 20 + 1) + ")";
            if (this.autoReconnectTimer > 0) {
                --this.autoReconnectTimer;
            }
            else {
                try {
                    this.actionPerformed(this.buttonList.get(1));
                }
                catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            this.buttonList.get(2).displayString = "AutoReconnect";
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.textHeight / 2;
        if (this.multilineMessage != null) {
            for (final String s : this.multilineMessage) {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += this.fontRendererObj.FONT_HEIGHT;
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
