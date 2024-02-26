// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui.multiplayer;

import java.util.Iterator;
import java.util.Arrays;
import java.io.IOException;
import net.minecraft.client.multiplayer.ServerData;
import net.wurstclient.options.OptionsManager;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

public class GuiCleanUp extends GuiScreen
{
    private GuiMultiplayer prevScreen;
    private boolean removeAll;
    private String[] toolTips;
    
    public GuiCleanUp(final GuiMultiplayer prevMultiplayerMenu) {
        this.toolTips = new String[] { "", "Start the Clean Up with the settings\nyou specified above.\nIt might look like the game is not\nreacting for a couple of seconds.", "Servers that clearly don't exist.", "Servers that run a different Minecraft\nversion than you.", "All servers that failed the last ping.\nMake sure that the last ping is complete\nbefore you do this. That means: Go back,\npress the refresh button and wait until\nall servers are done refreshing.", "All servers where the name starts with \"Grief me\"\nUseful for removing servers found by ServerFinder.", "This will completely clear your server\nlist. §cUse with caution!§r", "Renames your servers to \"Grief me #1\",\n\"Grief me #2\", etc." };
        this.prevScreen = prevMultiplayerMenu;
    }
    
    @Override
    public void updateScreen() {
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 168 + 12, "Cancel"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 144 + 12, "Clean Up"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 - 24 + 12, "Unknown Hosts: " + this.removeOrKeep(WurstClient.INSTANCE.options.cleanupUnknown)));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 0 + 12, "Outdated Servers: " + this.removeOrKeep(WurstClient.INSTANCE.options.cleanupOutdated)));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + 12, "Failed Ping: " + this.removeOrKeep(WurstClient.INSTANCE.options.cleanupFailed)));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + 12, "\"Grief me\" Servers: " + this.removeOrKeep(WurstClient.INSTANCE.options.cleanupGriefMe)));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 100, this.height / 4 + 72 + 12, "§cRemove all Servers: " + this.yesOrNo(this.removeAll)));
        this.buttonList.add(new GuiButton(7, this.width / 2 - 100, this.height / 4 + 96 + 12, "Rename all Servers: " + this.yesOrNo(WurstClient.INSTANCE.options.cleanupRename)));
        WurstClient.INSTANCE.analytics.trackPageView("/multiplayer/clean-up", "Clean Up");
    }
    
    private String yesOrNo(final boolean bool) {
        return bool ? "Yes" : "No";
    }
    
    private String removeOrKeep(final boolean bool) {
        return bool ? "Remove" : "Keep";
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    protected void actionPerformed(final GuiButton clickedButton) {
        if (clickedButton.enabled) {
            if (clickedButton.id == 0) {
                this.mc.openScreen(this.prevScreen);
            }
            else {
                final OptionsManager options = WurstClient.INSTANCE.options;
                if (clickedButton.id == 1) {
                    WurstClient.INSTANCE.analytics.trackEvent("clean up", "start");
                    if (this.removeAll) {
                        this.prevScreen.savedServerList.clearServerList();
                        this.prevScreen.savedServerList.saveServerList();
                        this.prevScreen.serverListSelector.setSelectedSlotIndex(-1);
                        this.prevScreen.serverListSelector.updateOnlineServers(this.prevScreen.savedServerList);
                        this.mc.openScreen(this.prevScreen);
                        return;
                    }
                    for (int i = this.prevScreen.savedServerList.countServers() - 1; i >= 0; --i) {
                        final ServerData server = this.prevScreen.savedServerList.getServerData(i);
                        if ((options.cleanupUnknown && "§4Can't resolve hostname".equals(server.serverMOTD)) || (options.cleanupOutdated && !WMinecraft.PROTOCOLS.containsKey(server.version)) || (options.cleanupFailed && server.pingToServer != -2L && server.pingToServer < 0L) || (options.cleanupGriefMe && server.serverName.startsWith("Grief me"))) {
                            this.prevScreen.savedServerList.removeServerData(i);
                            this.prevScreen.savedServerList.saveServerList();
                            this.prevScreen.serverListSelector.setSelectedSlotIndex(-1);
                            this.prevScreen.serverListSelector.updateOnlineServers(this.prevScreen.savedServerList);
                        }
                    }
                    if (options.cleanupRename) {
                        for (int i = 0; i < this.prevScreen.savedServerList.countServers(); ++i) {
                            final ServerData server = this.prevScreen.savedServerList.getServerData(i);
                            server.serverName = "Grief me #" + (i + 1);
                            this.prevScreen.savedServerList.saveServerList();
                            this.prevScreen.serverListSelector.setSelectedSlotIndex(-1);
                            this.prevScreen.serverListSelector.updateOnlineServers(this.prevScreen.savedServerList);
                        }
                    }
                    this.mc.openScreen(this.prevScreen);
                }
                else if (clickedButton.id == 2) {
                    options.cleanupUnknown = !options.cleanupUnknown;
                    clickedButton.displayString = "Unknown Hosts: " + this.removeOrKeep(options.cleanupUnknown);
                    ConfigFiles.OPTIONS.save();
                    WurstClient.INSTANCE.analytics.trackEvent("clean up", "unknown host", this.removeOrKeep(options.cleanupUnknown));
                }
                else if (clickedButton.id == 3) {
                    options.cleanupOutdated = !options.cleanupOutdated;
                    clickedButton.displayString = "Outdated Servers: " + this.removeOrKeep(options.cleanupOutdated);
                    ConfigFiles.OPTIONS.save();
                    WurstClient.INSTANCE.analytics.trackEvent("clean up", "outdated servers", this.removeOrKeep(options.cleanupOutdated));
                }
                else if (clickedButton.id == 4) {
                    options.cleanupFailed = !options.cleanupFailed;
                    clickedButton.displayString = "Failed Ping: " + this.removeOrKeep(options.cleanupFailed);
                    ConfigFiles.OPTIONS.save();
                    WurstClient.INSTANCE.analytics.trackEvent("clean up", "failed ping", this.removeOrKeep(options.cleanupFailed));
                }
                else if (clickedButton.id == 5) {
                    options.cleanupGriefMe = !options.cleanupGriefMe;
                    ConfigFiles.OPTIONS.save();
                    clickedButton.displayString = "\"Grief Me\" Servers: " + this.removeOrKeep(options.cleanupGriefMe);
                    WurstClient.INSTANCE.analytics.trackEvent("clean up", "grief me", this.removeOrKeep(options.cleanupGriefMe));
                }
                else if (clickedButton.id == 6) {
                    this.removeAll = !this.removeAll;
                    clickedButton.displayString = "§cRemove all Servers: " + this.yesOrNo(this.removeAll);
                    WurstClient.INSTANCE.analytics.trackEvent("clean up", "remove all servers", this.yesOrNo(this.removeAll));
                }
                else if (clickedButton.id == 7) {
                    options.cleanupRename = !options.cleanupRename;
                    clickedButton.displayString = "Rename all Servers: " + this.yesOrNo(options.cleanupRename);
                    ConfigFiles.OPTIONS.save();
                    WurstClient.INSTANCE.analytics.trackEvent("clean up", "rename servers", this.yesOrNo(options.cleanupRename));
                }
            }
        }
    }
    
    @Override
    protected void keyTyped(final char par1, final int par2) {
        if (par2 == 28 || par2 == 156) {
            this.actionPerformed(this.buttonList.get(0));
        }
    }
    
    @Override
    protected void mouseClicked(final int par1, final int par2, final int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
    }
    
    @Override
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Clean Up", this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Please select the servers you want to remove:", this.width / 2, 36, 10526880);
        super.drawScreen(par1, par2, par3);
        for (final GuiButton button : this.buttonList) {
            if (button.isMouseOver() && !this.toolTips[button.id].isEmpty()) {
                this.drawHoveringText(Arrays.asList(this.toolTips[button.id].split("\n")), par1, par2);
                break;
            }
        }
    }
}
