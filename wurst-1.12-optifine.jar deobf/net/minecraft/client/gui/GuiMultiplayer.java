// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.wurstclient.spam.SpamProcessor;
import net.minecraft.client.multiplayer.GuiConnecting;
import com.google.common.collect.Lists;
import com.google.common.base.Splitter;
import net.wurstclient.gui.multiplayer.GuiCleanUp;
import net.wurstclient.gui.multiplayer.GuiServerFinder;
import net.wurstclient.compatibility.Gui1121Mode;
import net.minecraft.client.network.LanServerInfo;
import java.util.List;
import net.wurstclient.hooks.ServerHook;
import net.wurstclient.update.Version;
import net.wurstclient.WurstClient;
import net.minecraft.client.resources.I18n;
import java.io.IOException;
import org.lwjgl.input.Keyboard;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.ServerPinger;
import org.apache.logging.log4j.Logger;

public class GuiMultiplayer extends GuiScreen
{
    private static final Logger LOGGER;
    private final ServerPinger oldServerPinger;
    private final GuiScreen parentScreen;
    public ServerSelectionList serverListSelector;
    public ServerList savedServerList;
    private GuiButton btnEditServer;
    private GuiButton btnSelectServer;
    private GuiButton btnDeleteServer;
    private boolean deletingServer;
    private boolean addingServer;
    private boolean editingServer;
    private boolean directConnect;
    private String hoveringText;
    private ServerData selectedServer;
    private LanServerDetector.LanServerList lanServerList;
    private LanServerDetector.ThreadLanServerFind lanServerDetector;
    private boolean initialized;
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public GuiMultiplayer(final GuiScreen parentScreen) {
        this.oldServerPinger = new ServerPinger();
        this.parentScreen = parentScreen;
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        if (this.initialized) {
            this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
        }
        else {
            this.initialized = true;
            (this.savedServerList = new ServerList(this.mc)).loadServerList();
            this.lanServerList = new LanServerDetector.LanServerList();
            try {
                (this.lanServerDetector = new LanServerDetector.ThreadLanServerFind(this.lanServerList)).start();
            }
            catch (final Exception exception) {
                GuiMultiplayer.LOGGER.warn("Unable to start LAN server detection: {}", (Object)exception.getMessage());
            }
            (this.serverListSelector = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36)).updateOnlineServers(this.savedServerList);
        }
        this.createButtons();
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (this.serverListSelector != null) {
            this.serverListSelector.handleMouseInput();
        }
    }
    
    public void createButtons() {
        this.btnEditServer = this.addButton(new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit", new Object[0])));
        this.btnDeleteServer = this.addButton(new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete", new Object[0])));
        this.btnSelectServer = this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct", new Object[0])));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add", new Object[0])));
        this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel", new Object[0])));
        if (WurstClient.INSTANCE.isEnabled()) {
            this.buttonList.add(new GuiButton(5, this.width / 2 - 154, 10, 100, 20, "Last server"));
            this.buttonList.add(new GuiButton(6, this.width / 2 + 54, 10, 100, 20, "Version: " + new Version("1.12." + WurstClient.INSTANCE.options.mc112x_compatibility)));
            this.buttonList.add(new GuiButton(10, this.width / 2 - 154 - 104, this.height - 52, 100, 20, "Import Servers"));
            this.buttonList.add(new GuiButton(11, this.width / 2 - 154 - 104, this.height - 28, 100, 20, "Export Servers"));
            this.buttonList.add(new GuiButton(12, this.width / 2 + 154 + 4, this.height - 52, 100, 20, "Server Finder"));
            this.buttonList.add(new GuiButton(13, this.width / 2 + 154 + 4, this.height - 28, 100, 20, "Clean Up"));
        }
        this.selectServer(this.serverListSelector.getSelected());
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.lanServerList.getWasUpdated()) {
            final List<LanServerInfo> list = this.lanServerList.getLanServers();
            this.lanServerList.setWasNotUpdated();
            this.serverListSelector.updateNetworkServers(list);
        }
        this.oldServerPinger.pingPendingNetworks();
        if (WurstClient.INSTANCE.isEnabled()) {
            this.buttonList.get(7).enabled = ServerHook.hasLastServer();
        }
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        this.oldServerPinger.clearPendingNetworks();
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            final GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (this.serverListSelector.getSelected() < 0) ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());
            if (button.id == 2 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                final String s4 = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData().serverName;
                if (s4 != null) {
                    this.deletingServer = true;
                    final String s5 = I18n.format("selectServer.deleteQuestion", new Object[0]);
                    final String s6 = "'" + s4 + "' " + I18n.format("selectServer.deleteWarning", new Object[0]);
                    final String s7 = I18n.format("selectServer.deleteButton", new Object[0]);
                    final String s8 = I18n.format("gui.cancel", new Object[0]);
                    final GuiYesNo guiyesno = new GuiYesNo(this, s5, s6, s7, s8, this.serverListSelector.getSelected());
                    this.mc.openScreen(guiyesno);
                }
            }
            else if (button.id == 1) {
                this.connectToSelected();
            }
            else if (button.id == 4) {
                this.directConnect = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "", false);
                this.mc.openScreen(new GuiScreenServerList(this, this.selectedServer));
            }
            else if (button.id == 3) {
                this.addingServer = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "", false);
                this.mc.openScreen(new GuiScreenAddServer(this, this.selectedServer));
            }
            else if (button.id == 7 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.editingServer = true;
                final ServerData serverdata = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                (this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false)).copyFrom(serverdata);
                this.mc.openScreen(new GuiScreenAddServer(this, this.selectedServer));
            }
            else if (button.id == 0) {
                this.mc.openScreen(this.parentScreen);
            }
            else if (button.id == 8) {
                this.refreshServerList();
            }
            else if (button.id == 5) {
                ServerHook.joinLastServer(this);
            }
            else if (button.id == 6) {
                this.mc.openScreen(new Gui1121Mode(this));
            }
            else if (button.id == 10) {
                new Thread(() -> ServerHook.importServers(this)).start();
            }
            else if (button.id == 11) {
                new Thread(() -> ServerHook.exportServers(this)).start();
            }
            else if (button.id == 12) {
                this.mc.openScreen(new GuiServerFinder(this));
            }
            else if (button.id == 13) {
                this.mc.openScreen(new GuiCleanUp(this));
            }
        }
    }
    
    public void refreshServerList() {
        this.mc.openScreen(new GuiMultiplayer(this.parentScreen));
    }
    
    @Override
    public void confirmClicked(final boolean result, final int id) {
        final GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (this.serverListSelector.getSelected() < 0) ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());
        if (this.deletingServer) {
            this.deletingServer = false;
            if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.savedServerList.removeServerData(this.serverListSelector.getSelected());
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }
            this.mc.openScreen(this);
        }
        else if (this.directConnect) {
            this.directConnect = false;
            if (result) {
                this.connectToServer(this.selectedServer);
            }
            else {
                this.mc.openScreen(this);
            }
        }
        else if (this.addingServer) {
            this.addingServer = false;
            if (result) {
                this.savedServerList.addServerData(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }
            this.mc.openScreen(this);
        }
        else if (this.editingServer) {
            this.editingServer = false;
            if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                final ServerData serverdata = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                serverdata.serverName = this.selectedServer.serverName;
                serverdata.serverIP = this.selectedServer.serverIP;
                serverdata.copyFrom(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }
            this.mc.openScreen(this);
        }
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        final int i = this.serverListSelector.getSelected();
        final GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (i < 0) ? null : this.serverListSelector.getListEntry(i);
        if (keyCode == 63) {
            this.refreshServerList();
        }
        else if (i >= 0) {
            if (keyCode == 211) {
                this.confirmClicked(this.deletingServer = true, i);
            }
            if (keyCode == 200) {
                if (isShiftKeyDown()) {
                    if (i > 0 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                        this.savedServerList.swapServers(i, i - 1);
                        this.selectServer(this.serverListSelector.getSelected() - 1);
                        this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                        this.serverListSelector.updateOnlineServers(this.savedServerList);
                    }
                }
                else if (i > 0) {
                    this.selectServer(this.serverListSelector.getSelected() - 1);
                    this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                    if (this.serverListSelector.getListEntry(this.serverListSelector.getSelected()) instanceof ServerListEntryLanScan) {
                        if (this.serverListSelector.getSelected() > 0) {
                            this.selectServer(this.serverListSelector.getSize() - 1);
                            this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                        }
                        else {
                            this.selectServer(-1);
                        }
                    }
                }
                else {
                    this.selectServer(-1);
                }
            }
            else if (keyCode == 208) {
                if (isShiftKeyDown()) {
                    if (i < this.savedServerList.countServers() - 1) {
                        this.savedServerList.swapServers(i, i + 1);
                        this.selectServer(i + 1);
                        this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                        this.serverListSelector.updateOnlineServers(this.savedServerList);
                    }
                }
                else if (i < this.serverListSelector.getSize()) {
                    this.selectServer(this.serverListSelector.getSelected() + 1);
                    this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                    if (this.serverListSelector.getListEntry(this.serverListSelector.getSelected()) instanceof ServerListEntryLanScan) {
                        if (this.serverListSelector.getSelected() < this.serverListSelector.getSize() - 1) {
                            this.selectServer(this.serverListSelector.getSize() + 1);
                            this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                        }
                        else {
                            this.selectServer(-1);
                        }
                    }
                }
                else {
                    this.selectServer(-1);
                }
            }
            else if (keyCode != 28 && keyCode != 156) {
                super.keyTyped(typedChar, keyCode);
            }
            else {
                this.actionPerformed(this.buttonList.get(2));
            }
        }
        else {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.hoveringText = null;
        this.drawDefaultBackground();
        this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.title", new Object[0]), this.width / 2, 16, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hoveringText != null) {
            this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split((CharSequence)this.hoveringText)), mouseX, mouseY);
        }
    }
    
    public void connectToSelected() {
        final GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (this.serverListSelector.getSelected() < 0) ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());
        ServerHook.updateLastServerFromServerlist(guilistextended$iguilistentry, this);
        if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
            this.connectToServer(((ServerListEntryNormal)guilistextended$iguilistentry).getServerData());
        }
        else if (guilistextended$iguilistentry instanceof ServerListEntryLanDetected) {
            final LanServerInfo lanserverinfo = ((ServerListEntryLanDetected)guilistextended$iguilistentry).getServerData();
            this.connectToServer(new ServerData(lanserverinfo.getServerMotd(), lanserverinfo.getServerIpPort(), true));
        }
    }
    
    public void connectToServer(final ServerData server) {
        this.mc.openScreen(new GuiConnecting(this, this.mc, server));
        SpamProcessor.runScript("joinserver", "Runs whenever you join a server.");
    }
    
    public void selectServer(final int index) {
        this.serverListSelector.setSelectedSlotIndex(index);
        final GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (index < 0) ? null : this.serverListSelector.getListEntry(index);
        this.btnSelectServer.enabled = false;
        this.btnEditServer.enabled = false;
        this.btnDeleteServer.enabled = false;
        if (guilistextended$iguilistentry != null && !(guilistextended$iguilistentry instanceof ServerListEntryLanScan)) {
            this.btnSelectServer.enabled = true;
            if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.btnEditServer.enabled = true;
                this.btnDeleteServer.enabled = true;
            }
        }
    }
    
    public ServerPinger getOldServerPinger() {
        return this.oldServerPinger;
    }
    
    public void setHoveringText(final String p_146793_1_) {
        this.hoveringText = p_146793_1_;
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.serverListSelector.mouseReleased(mouseX, mouseY, state);
    }
    
    public ServerList getServerList() {
        return this.savedServerList;
    }
    
    public boolean canMoveUp(final ServerListEntryNormal p_175392_1_, final int p_175392_2_) {
        return p_175392_2_ > 0;
    }
    
    public boolean canMoveDown(final ServerListEntryNormal p_175394_1_, final int p_175394_2_) {
        return p_175394_2_ < this.savedServerList.countServers() - 1;
    }
    
    public void moveServerUp(final ServerListEntryNormal p_175391_1_, final int p_175391_2_, final boolean p_175391_3_) {
        final int i = p_175391_3_ ? 0 : (p_175391_2_ - 1);
        this.savedServerList.swapServers(p_175391_2_, i);
        if (this.serverListSelector.getSelected() == p_175391_2_) {
            this.selectServer(i);
        }
        this.serverListSelector.updateOnlineServers(this.savedServerList);
    }
    
    public void moveServerDown(final ServerListEntryNormal p_175393_1_, final int p_175393_2_, final boolean p_175393_3_) {
        final int i = p_175393_3_ ? (this.savedServerList.countServers() - 1) : (p_175393_2_ + 1);
        this.savedServerList.swapServers(p_175393_2_, i);
        if (this.serverListSelector.getSelected() == p_175393_2_) {
            this.selectServer(i);
        }
        this.serverListSelector.updateOnlineServers(this.savedServerList);
    }
}
