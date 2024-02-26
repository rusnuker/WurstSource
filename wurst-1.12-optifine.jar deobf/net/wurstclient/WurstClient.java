// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient;

import net.minecraft.client.settings.KeyBinding;
import java.lang.reflect.Field;
import java.util.Map;
import org.apache.logging.log4j.core.lookup.Interpolator;
import java.nio.file.Path;
import net.wurstclient.hooks.FrameHook;
import net.wurstclient.update.Version;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.font.Fonts;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import net.wurstclient.update.Updater;
import net.wurstclient.features.special_features.OtfManager;
import net.wurstclient.options.OptionsManager;
import net.wurstclient.keybinds.KeybindProcessor;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.keybinds.KeybindList;
import net.wurstclient.navigator.Navigator;
import net.wurstclient.features.mods.LocalHackList;
import net.wurstclient.options.FriendsList;
import net.wurstclient.files.FileManager;
import net.wurstclient.altmanager.AltManager;
import net.wurstclient.event.EventManager;
import net.wurstclient.features.commands.CmdManager;
import net.wurstclient.analytics.AnalyticsManager;
import net.minecraft.client.Minecraft;

public enum WurstClient
{
    INSTANCE("INSTANCE", 0);
    
    public static final Minecraft MC;
    public static final String VERSION = "6.35.3";
    public AnalyticsManager analytics;
    public CmdManager commands;
    public EventManager events;
    private AltManager altManager;
    public FileManager files;
    public FriendsList friends;
    public LocalHackList hax;
    public Navigator navigator;
    private KeybindList keybinds;
    private ClickGui gui;
    private KeybindProcessor keybindProcessor;
    public OptionsManager options;
    public OtfManager special;
    public Updater updater;
    private boolean enabled;
    
    static {
        MC = Minecraft.getMinecraft();
    }
    
    private WurstClient(final String name, final int ordinal) {
        this.enabled = true;
    }
    
    public void startClient() {
        ((LoggerContext)LogManager.getContext(false)).addPropertyChangeListener((PropertyChangeListener)new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("config")) {
                    applyLog4ShellFix();
                }
            }
        });
        applyLog4ShellFix();
        Fonts.loadFonts();
        WurstFolders.initialize();
        this.events = new EventManager();
        this.hax = new LocalHackList();
        this.commands = new CmdManager();
        this.special = new OtfManager();
        this.files = new FileManager();
        this.updater = new Updater();
        this.options = new OptionsManager();
        this.friends = new FriendsList();
        this.navigator = new Navigator();
        ConfigFiles.initialize();
        final Path altsFile = WurstFolders.MAIN.resolve("alts.json");
        this.altManager = new AltManager(altsFile);
        (this.keybinds = new KeybindList(WurstFolders.MAIN.resolve("keybinds.json"))).init();
        (this.gui = new ClickGui(WurstFolders.MAIN.resolve("windows.json"))).init();
        this.keybindProcessor = new KeybindProcessor(this.hax, this.keybinds, this.commands);
        this.navigator.sortFeatures();
        this.updater.checkForUpdate();
        (this.analytics = new AnalyticsManager("UA-52838431-5", "client.wurstclient.net")).trackPageView("/mc1.12-of/v6.35.3", "Wurst 6.35.3 MC1.12 OF");
        ConfigFiles.OPTIONS.save();
        final Version last = new Version(this.options.lastLaunchedVersion);
        if (last.isLowerThan("6.17") && last.isHigherThan("0")) {
            ConfigFiles.SETTINGS.save();
            ConfigFiles.NAVIGATOR.save();
        }
        FrameHook.maximize();
    }
    
    private static void applyLog4ShellFix() {
        try {
            final Interpolator interpolator = (Interpolator)((LoggerContext)LogManager.getContext(false)).getConfiguration().getStrSubstitutor().getVariableResolver();
            if (interpolator == null) {
                return;
            }
            boolean removed = false;
            Field[] declaredFields;
            for (int length = (declaredFields = Interpolator.class.getDeclaredFields()).length, i = 0; i < length; ++i) {
                final Field field = declaredFields[i];
                if (Map.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    removed = (((Map)field.get(interpolator)).remove("jndi") != null);
                    if (removed) {
                        break;
                    }
                }
            }
            if (!removed) {
                throw new RuntimeException("couldn't find jndi lookup entry");
            }
            System.out.println("Removed JNDI lookup");
        }
        catch (final Throwable t) {
            System.out.println("Couldn't remove JNDI lookup (probably already removed by another mod/launcher)");
            t.printStackTrace();
        }
    }
    
    public AnalyticsManager getAnalytics() {
        return this.analytics;
    }
    
    public EventManager getEventManager() {
        return this.events;
    }
    
    public void saveSettings() {
        ConfigFiles.SETTINGS.save();
    }
    
    public LocalHackList getHax() {
        return this.hax;
    }
    
    public CmdManager getCmds() {
        return this.commands;
    }
    
    public OtfManager getOtfs() {
        return this.special;
    }
    
    public KeybindList getKeybinds() {
        return this.keybinds;
    }
    
    public ClickGui getGui() {
        return this.gui;
    }
    
    public Navigator getNavigator() {
        return this.navigator;
    }
    
    public KeybindProcessor getKeybindProcessor() {
        return this.keybindProcessor;
    }
    
    public FriendsList getFriends() {
        return this.friends;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        if (!(this.enabled = enabled)) {
            this.hax.panicMod.setEnabled(true);
            this.hax.panicMod.onUpdate();
        }
    }
    
    public Updater getUpdater() {
        return this.updater;
    }
    
    public KeyBinding getZoomKey() {
        return WurstClient.MC.gameSettings.keyBindZoom;
    }
    
    public AltManager getAltManager() {
        return this.altManager;
    }
}
