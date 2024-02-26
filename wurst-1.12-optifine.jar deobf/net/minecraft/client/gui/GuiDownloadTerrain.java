// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.wurstclient.WurstClient;
import java.io.IOException;
import net.minecraft.client.network.NetHandlerPlayClient;

public class GuiDownloadTerrain extends GuiScreen
{
    private final NetHandlerPlayClient connection;
    private int progress;
    
    public GuiDownloadTerrain(final NetHandlerPlayClient netHandler) {
        this.connection = netHandler;
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
    }
    
    @Override
    public void updateScreen() {
        ++this.progress;
        if (this.progress % 20 == 0 && WurstClient.INSTANCE.options.mc112x_compatibility < 2) {
            this.connection.sendPacket(new CPacketKeepAlive());
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawBackground(0);
        this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingTerrain", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
