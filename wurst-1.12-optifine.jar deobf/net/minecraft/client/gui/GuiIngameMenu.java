// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import java.io.IOException;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.realms.RealmsBridge;
import net.wurstclient.gui.main.GuiWurstMainMenu;
import net.minecraft.client.multiplayer.WorldClient;
import net.wurstclient.gui.options.GuiWurstOptions;
import net.wurstclient.WurstClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiIngameMenu extends GuiScreen
{
    private int saveStep;
    private int visibleTime;
    private static final ResourceLocation wurstTextures;
    
    static {
        wurstTextures = new ResourceLocation("wurst/wurst_128.png");
    }
    
    @Override
    public void initGui() {
        this.saveStep = 0;
        this.buttonList.clear();
        final int i = -16;
        final int j = 98;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 - 16, I18n.format("menu.returnToMenu", new Object[0])));
        if (!this.mc.isIntegratedServerRunning()) {
            this.buttonList.get(0).displayString = I18n.format("menu.disconnect", new Object[0]);
        }
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 - 16, I18n.format("menu.returnToGame", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 - 16, 98, 20, I18n.format("menu.options", new Object[0])));
        if (WurstClient.INSTANCE.isEnabled()) {
            this.buttonList.add(new GuiButton(8, this.width / 2 - 100, this.height / 4 + 72 + i, "            Options"));
        }
        final GuiButton guibutton = this.addButton(new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 - 16, 98, 20, I18n.format("menu.shareToLan", new Object[0])));
        guibutton.enabled = (this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic());
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 - 16, 98, 20, I18n.format("gui.advancements", new Object[0])));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 - 16, 98, 20, I18n.format("gui.stats", new Object[0])));
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {
            case 8: {
                this.mc.openScreen(new GuiWurstOptions(this));
                break;
            }
            case 0: {
                this.mc.openScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            }
            case 1: {
                final boolean flag = this.mc.isIntegratedServerRunning();
                final boolean flag2 = this.mc.isConnectedToRealms();
                button.enabled = false;
                this.mc.world.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                if (flag) {
                    this.mc.openScreen(new GuiWurstMainMenu());
                    break;
                }
                if (flag2) {
                    final RealmsBridge realmsbridge = new RealmsBridge();
                    realmsbridge.switchToRealms(new GuiWurstMainMenu());
                    break;
                }
                this.mc.openScreen(new GuiMultiplayer(new GuiWurstMainMenu()));
                break;
            }
            case 4: {
                this.mc.openScreen(null);
                this.mc.setIngameFocus();
                break;
            }
            case 5: {
                this.mc.openScreen(new GuiScreenAdvancements(this.mc.player.connection.func_191982_f()));
                break;
            }
            case 6: {
                this.mc.openScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
                break;
            }
            case 7: {
                this.mc.openScreen(new GuiShareToLan(this));
                break;
            }
        }
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        ++this.visibleTime;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("menu.game", new Object[0]), this.width / 2, 40, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!WurstClient.INSTANCE.isEnabled()) {
            return;
        }
        GL11.glEnable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiIngameMenu.wurstTextures);
        final int x = this.width / 2 - 68;
        final int y = this.height / 4 + 73 - 15;
        final int w = 63;
        final int h = 16;
        final float fw = 63.0f;
        final float fh = 16.0f;
        final float u = 0.0f;
        final float v = 0.0f;
        Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
    }
}
