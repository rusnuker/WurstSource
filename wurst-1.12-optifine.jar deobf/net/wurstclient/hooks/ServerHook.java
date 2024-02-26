// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.hooks;

import java.util.NavigableMap;
import net.wurstclient.bot.WurstBot;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.ServerListEntryLanDetected;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import net.wurstclient.util.MiscUtils;
import net.minecraft.client.multiplayer.ServerData;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.HeadlessException;
import javax.swing.JDialog;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import net.wurstclient.files.WurstFolders;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;

public class ServerHook
{
    private static String currentServerIP;
    private static ServerListEntryNormal lastServer;
    
    static {
        ServerHook.currentServerIP = "127.0.0.1:25565";
    }
    
    public static void importServers(final GuiMultiplayer guiMultiplayer) {
        final JFileChooser fileChooser = new JFileChooser(WurstFolders.SERVERLISTS.toFile()) {
            @Override
            protected JDialog createDialog(final Component parent) throws HeadlessException {
                final JDialog dialog = super.createDialog(parent);
                dialog.setAlwaysOnTop(true);
                return dialog;
            }
        };
        fileChooser.setFileSelectionMode(0);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("TXT files", new String[] { "txt" }));
        final int action = fileChooser.showOpenDialog(FrameHook.getFrame());
        if (action == 0) {
            try {
                final File file = fileChooser.getSelectedFile();
                Throwable t = null;
                try {
                    final BufferedReader load = new BufferedReader(new FileReader(file));
                    try {
                        int i = 0;
                        String line = "";
                        while ((line = load.readLine()) != null) {
                            ++i;
                            guiMultiplayer.savedServerList.addServerData(new ServerData("Grief me #" + i, line, false));
                            guiMultiplayer.savedServerList.saveServerList();
                            guiMultiplayer.serverListSelector.setSelectedSlotIndex(-1);
                            guiMultiplayer.serverListSelector.updateOnlineServers(guiMultiplayer.savedServerList);
                        }
                    }
                    finally {
                        if (load != null) {
                            load.close();
                        }
                    }
                }
                finally {
                    if (t == null) {
                        final Throwable exception;
                        t = exception;
                    }
                    else {
                        final Throwable exception;
                        if (t != exception) {
                            t.addSuppressed(exception);
                        }
                    }
                }
                guiMultiplayer.refreshServerList();
            }
            catch (final IOException e) {
                e.printStackTrace();
                MiscUtils.simpleError(e, fileChooser);
            }
        }
    }
    
    public static void exportServers(final GuiMultiplayer guiMultiplayer) {
        final JFileChooser fileChooser = new JFileChooser(WurstFolders.SERVERLISTS.toFile()) {
            @Override
            protected JDialog createDialog(final Component parent) throws HeadlessException {
                final JDialog dialog = super.createDialog(parent);
                dialog.setAlwaysOnTop(true);
                return dialog;
            }
        };
        fileChooser.setFileSelectionMode(0);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("TXT files", new String[] { "txt" }));
        final int action = fileChooser.showSaveDialog(FrameHook.getFrame());
        if (action == 0) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    file = new File(String.valueOf(file.getPath()) + ".txt");
                }
                Throwable t = null;
                try {
                    final PrintWriter save = new PrintWriter(new FileWriter(file));
                    try {
                        for (int i = 0; i < guiMultiplayer.savedServerList.countServers(); ++i) {
                            save.println(guiMultiplayer.savedServerList.getServerData(i).serverIP);
                        }
                    }
                    finally {
                        if (save != null) {
                            save.close();
                        }
                    }
                }
                finally {
                    if (t == null) {
                        final Throwable exception;
                        t = exception;
                    }
                    else {
                        final Throwable exception;
                        if (t != exception) {
                            t.addSuppressed(exception);
                        }
                    }
                }
            }
            catch (final IOException e) {
                e.printStackTrace();
                MiscUtils.simpleError(e, fileChooser);
            }
        }
    }
    
    public static void joinLastServer(final GuiMultiplayer guiMultiplayer) {
        if (ServerHook.lastServer == null) {
            return;
        }
        ServerHook.currentServerIP = ServerHook.lastServer.getServerData().serverIP;
        if (!ServerHook.currentServerIP.contains(":")) {
            ServerHook.currentServerIP = String.valueOf(ServerHook.currentServerIP) + ":25565";
        }
        guiMultiplayer.connectToServer(ServerHook.lastServer.getServerData());
    }
    
    public static void reconnectToLastServer(final GuiScreen prevScreen) {
        if (ServerHook.lastServer == null) {
            return;
        }
        ServerHook.currentServerIP = ServerHook.lastServer.getServerData().serverIP;
        if (!ServerHook.currentServerIP.contains(":")) {
            ServerHook.currentServerIP = String.valueOf(ServerHook.currentServerIP) + ":25565";
        }
        final Minecraft mc = Minecraft.getMinecraft();
        mc.openScreen(new GuiConnecting(prevScreen, mc, ServerHook.lastServer.getServerData()));
    }
    
    public static void updateLastServerFromServerlist(final GuiListExtended.IGuiListEntry entry, final GuiMultiplayer guiMultiplayer) {
        if (entry instanceof ServerListEntryNormal) {
            ServerHook.currentServerIP = ((ServerListEntryNormal)entry).getServerData().serverIP;
            if (!ServerHook.currentServerIP.contains(":")) {
                ServerHook.currentServerIP = String.valueOf(ServerHook.currentServerIP) + ":25565";
            }
            ServerHook.lastServer = (ServerListEntryNormal)((guiMultiplayer.serverListSelector.getSelected() < 0) ? null : guiMultiplayer.serverListSelector.getListEntry(guiMultiplayer.serverListSelector.getSelected()));
        }
        else if (entry instanceof ServerListEntryLanDetected) {
            ServerHook.currentServerIP = ((ServerListEntryLanDetected)entry).getServerData().getServerIpPort();
            ServerHook.lastServer = new ServerListEntryNormal(guiMultiplayer, new ServerData("LAN-Server", ServerHook.currentServerIP, false));
        }
    }
    
    public static void updateLastServerFromDirectConnect(final GuiMultiplayer guiMultiplayer, final ServerData serverData) {
        ServerHook.currentServerIP = serverData.serverIP;
        if (!ServerHook.currentServerIP.contains(":")) {
            ServerHook.currentServerIP = String.valueOf(ServerHook.currentServerIP) + ":25565";
        }
        ServerHook.lastServer = new ServerListEntryNormal(guiMultiplayer, serverData);
    }
    
    public static boolean hasLastServer() {
        return ServerHook.lastServer != null;
    }
    
    public static void setCurrentIpToSingleplayer() {
        ServerHook.currentServerIP = "127.0.0.1:25565";
    }
    
    public static void setCurrentIpToLanServer(final String port) {
        ServerHook.currentServerIP = "127.0.0.1:" + port;
    }
    
    public static String getCurrentServerIP() {
        return ServerHook.currentServerIP;
    }
    
    public static ServerData getLastServerData() {
        return ServerHook.lastServer.getServerData();
    }
    
    public static int getProtocolVersion() {
        if (WurstClient.INSTANCE.options.mc112x_compatibility == 2) {
            return 340;
        }
        if (WurstClient.INSTANCE.options.mc112x_compatibility == 1) {
            return 338;
        }
        final NavigableMap<Integer, String> protocols = WMinecraft.PROTOCOLS;
        if (WurstBot.isEnabled()) {
            return protocols.lastKey();
        }
        final ServerData server = ServerHook.lastServer.getServerData();
        if (!server.pinged || server.pingToServer < 0L) {
            return protocols.lastKey();
        }
        if (!protocols.containsKey(server.version)) {
            return protocols.lastKey();
        }
        return server.version;
    }
}
