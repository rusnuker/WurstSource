// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui.multiplayer;

import java.io.IOException;
import net.minecraft.client.multiplayer.ServerData;
import net.wurstclient.util.MiscUtils;
import java.net.UnknownHostException;
import net.wurstclient.servers.WurstServerPinger;
import java.util.ArrayList;
import java.net.InetAddress;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.WurstClient;
import org.lwjgl.input.Keyboard;
import net.wurstclient.util.MathUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

public class GuiServerFinder extends GuiScreen
{
    private static final String[] stateStrings;
    private GuiMultiplayer prevScreen;
    private GuiTextField ipBox;
    private GuiTextField maxThreadsBox;
    private int checked;
    private int working;
    private ServerFinderState state;
    
    static {
        stateStrings = new String[] { "", "�2Searching...", "�2Resolving...", "�4Unknown Host!", "�4Cancelled!", "�2Done!", "�4An error occurred!" };
    }
    
    public GuiServerFinder(final GuiMultiplayer prevMultiplayerMenu) {
        this.prevScreen = prevMultiplayerMenu;
    }
    
    @Override
    public void updateScreen() {
        this.ipBox.updateCursorCounter();
        this.buttonList.get(0).displayString = (this.state.isRunning() ? "Cancel" : "Search");
        this.ipBox.setEnabled(!this.state.isRunning());
        this.maxThreadsBox.setEnabled(!this.state.isRunning());
        this.buttonList.get(0).enabled = (MathUtils.isInteger(this.maxThreadsBox.getText()) && !this.ipBox.getText().isEmpty());
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, "Search"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, "Tutorial"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 144 + 12, "Back"));
        (this.ipBox = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, this.height / 4 + 34, 200, 20)).setMaxStringLength(200);
        this.ipBox.setFocused(true);
        (this.maxThreadsBox = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 32, this.height / 4 + 58, 26, 12)).setMaxStringLength(3);
        this.maxThreadsBox.setFocused(false);
        this.maxThreadsBox.setText(Integer.toString(WurstClient.INSTANCE.options.serverFinderThreads));
        this.state = ServerFinderState.NOT_RUNNING;
        WurstClient.INSTANCE.analytics.trackPageView("/multiplayer/server-finder", "Server Finder");
    }
    
    @Override
    public void onGuiClosed() {
        this.state = ServerFinderState.CANCELLED;
        WurstClient.INSTANCE.analytics.trackEvent("server finder", "cancel", "gui closed", this.working);
        if (MathUtils.isInteger(this.maxThreadsBox.getText())) {
            WurstClient.INSTANCE.options.serverFinderThreads = Integer.valueOf(this.maxThreadsBox.getText());
            ConfigFiles.OPTIONS.save();
        }
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    protected void actionPerformed(final GuiButton clickedButton) {
        if (clickedButton.enabled) {
            if (clickedButton.id == 0) {
                if (this.state.isRunning()) {
                    this.state = ServerFinderState.CANCELLED;
                    WurstClient.INSTANCE.analytics.trackEvent("server finder", "cancel", "cancel button", this.working);
                }
                else {
                    if (MathUtils.isInteger(this.maxThreadsBox.getText())) {
                        WurstClient.INSTANCE.options.serverFinderThreads = Integer.valueOf(this.maxThreadsBox.getText());
                        ConfigFiles.OPTIONS.save();
                    }
                    this.state = ServerFinderState.RESOLVING;
                    this.checked = 0;
                    this.working = 0;
                    new Thread("Server Finder") {
                        @Override
                        public void run() {
                            try {
                                final InetAddress addr = InetAddress.getByName(GuiServerFinder.this.ipBox.getText().split(":")[0].trim());
                                final int[] ipParts = new int[4];
                                for (int i = 0; i < 4; ++i) {
                                    ipParts[i] = (addr.getAddress()[i] & 0xFF);
                                }
                                GuiServerFinder.access$2(GuiServerFinder.this, ServerFinderState.SEARCHING);
                                final ArrayList<WurstServerPinger> pingers = new ArrayList<WurstServerPinger>();
                                final int[] changes = { 0, 1, -1, 2, -2, 3, -3 };
                                int[] array;
                                for (int length = (array = changes).length, j = 0; j < length; ++j) {
                                    final int change = array[j];
                                    for (int i2 = 0; i2 <= 255; ++i2) {
                                        if (GuiServerFinder.this.state == ServerFinderState.CANCELLED) {
                                            return;
                                        }
                                        final int[] ipParts2 = ipParts.clone();
                                        ipParts2[2] = (ipParts[2] + change & 0xFF);
                                        ipParts2[3] = i2;
                                        final String ip = String.valueOf(ipParts2[0]) + "." + ipParts2[1] + "." + ipParts2[2] + "." + ipParts2[3];
                                        final WurstServerPinger pinger = new WurstServerPinger();
                                        pinger.ping(ip);
                                        pingers.add(pinger);
                                        while (pingers.size() >= WurstClient.INSTANCE.options.serverFinderThreads) {
                                            if (GuiServerFinder.this.state == ServerFinderState.CANCELLED) {
                                                return;
                                            }
                                            GuiServerFinder.this.updatePingers(pingers);
                                        }
                                    }
                                }
                                while (pingers.size() > 0) {
                                    if (GuiServerFinder.this.state == ServerFinderState.CANCELLED) {
                                        return;
                                    }
                                    GuiServerFinder.this.updatePingers(pingers);
                                }
                                WurstClient.INSTANCE.analytics.trackEvent("server finder", "complete", "", GuiServerFinder.this.working);
                                GuiServerFinder.access$2(GuiServerFinder.this, ServerFinderState.DONE);
                            }
                            catch (final UnknownHostException e) {
                                GuiServerFinder.access$2(GuiServerFinder.this, ServerFinderState.UNKNOWN_HOST);
                                WurstClient.INSTANCE.analytics.trackEvent("server finder", "unknown host");
                            }
                            catch (final Exception e2) {
                                e2.printStackTrace();
                                GuiServerFinder.access$2(GuiServerFinder.this, ServerFinderState.ERROR);
                                WurstClient.INSTANCE.analytics.trackEvent("server finder", "error");
                            }
                        }
                    }.start();
                    WurstClient.INSTANCE.analytics.trackEvent("server finder", "start");
                }
            }
            else if (clickedButton.id == 1) {
                MiscUtils.openLink("https://www.wurstclient.net/wiki/Special_Features/Server_Finder/");
            }
            else if (clickedButton.id == 2) {
                this.mc.openScreen(this.prevScreen);
            }
        }
    }
    
    private boolean serverInList(final String ip) {
        for (int i = 0; i < this.prevScreen.savedServerList.countServers(); ++i) {
            if (this.prevScreen.savedServerList.getServerData(i).serverIP.equals(ip)) {
                return true;
            }
        }
        return false;
    }
    
    private void updatePingers(final ArrayList<WurstServerPinger> pingers) {
        for (int i = 0; i < pingers.size(); ++i) {
            if (!pingers.get(i).isStillPinging()) {
                ++this.checked;
                if (pingers.get(i).isWorking()) {
                    ++this.working;
                    if (!this.serverInList(pingers.get(i).server.serverIP)) {
                        this.prevScreen.savedServerList.addServerData(new ServerData("Grief me #" + this.working, pingers.get(i).server.serverIP, false));
                        this.prevScreen.savedServerList.saveServerList();
                        this.prevScreen.serverListSelector.setSelectedSlotIndex(-1);
                        this.prevScreen.serverListSelector.updateOnlineServers(this.prevScreen.savedServerList);
                    }
                }
                pingers.remove(i);
            }
        }
    }
    
    @Override
    protected void keyTyped(final char par1, final int par2) {
        this.ipBox.textboxKeyTyped(par1, par2);
        this.maxThreadsBox.textboxKeyTyped(par1, par2);
        if (par2 == 28 || par2 == 156) {
            this.actionPerformed(this.buttonList.get(0));
        }
    }
    
    @Override
    protected void mouseClicked(final int par1, final int par2, final int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
        this.ipBox.mouseClicked(par1, par2, par3);
        this.maxThreadsBox.mouseClicked(par1, par2, par3);
    }
    
    @Override
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Server Finder", this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, "This will search for servers with similar IPs", this.width / 2, 40, 10526880);
        this.drawCenteredString(this.fontRendererObj, "to the IP you type into the field below.", this.width / 2, 50, 10526880);
        this.drawCenteredString(this.fontRendererObj, "The servers it finds will be added to your server list.", this.width / 2, 60, 10526880);
        this.drawString(this.fontRendererObj, "Server address:", this.width / 2 - 100, this.height / 4 + 24, 10526880);
        this.ipBox.drawTextBox();
        this.drawString(this.fontRendererObj, "Max. threads:", this.width / 2 - 100, this.height / 4 + 60, 10526880);
        this.maxThreadsBox.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, this.state.toString(), this.width / 2, this.height / 4 + 73, 10526880);
        this.drawString(this.fontRendererObj, "Checked: " + this.checked + " / 1792", this.width / 2 - 100, this.height / 4 + 84, 10526880);
        this.drawString(this.fontRendererObj, "Working: " + this.working, this.width / 2 - 100, this.height / 4 + 94, 10526880);
        super.drawScreen(par1, par2, par3);
    }
    
    static /* synthetic */ void access$2(final GuiServerFinder guiServerFinder, final ServerFinderState state) {
        guiServerFinder.state = state;
    }
    
    enum ServerFinderState
    {
        NOT_RUNNING("NOT_RUNNING", 0), 
        SEARCHING("SEARCHING", 1), 
        RESOLVING("RESOLVING", 2), 
        UNKNOWN_HOST("UNKNOWN_HOST", 3), 
        CANCELLED("CANCELLED", 4), 
        DONE("DONE", 5), 
        ERROR("ERROR", 6);
        
        private ServerFinderState(final String name, final int ordinal) {
        }
        
        public boolean isRunning() {
            return this == ServerFinderState.SEARCHING || this == ServerFinderState.RESOLVING;
        }
        
        @Override
        public String toString() {
            return GuiServerFinder.stateStrings[this.ordinal()];
        }
    }
}
