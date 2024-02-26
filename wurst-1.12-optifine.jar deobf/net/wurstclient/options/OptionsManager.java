// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.options;

import java.security.SecureRandom;
import net.wurstclient.files.WurstFolders;

public class OptionsManager
{
    public boolean autoReconnect;
    public boolean cleanupFailed;
    public boolean cleanupOutdated;
    public boolean cleanupRename;
    public boolean cleanupUnknown;
    public boolean cleanupGriefMe;
    public boolean forceOPDontWait;
    public boolean spamFont;
    public boolean autoUpdater;
    public int forceOPDelay;
    public int ghostHandID;
    public int searchID;
    public int serverFinderThreads;
    public int spamDelay;
    public int throwAmount;
    public int mc112x_compatibility;
    public String forceOPList;
    public String lastLaunchedVersion;
    public GoogleAnalytics google_analytics;
    
    public OptionsManager() {
        this.autoReconnect = false;
        this.cleanupFailed = true;
        this.cleanupOutdated = true;
        this.cleanupRename = true;
        this.cleanupUnknown = true;
        this.cleanupGriefMe = false;
        this.forceOPDontWait = false;
        this.spamFont = false;
        this.autoUpdater = false;
        this.forceOPDelay = 1000;
        this.ghostHandID = 54;
        this.searchID = 116;
        this.serverFinderThreads = 128;
        this.spamDelay = 1000;
        this.throwAmount = 16;
        this.mc112x_compatibility = 2;
        this.forceOPList = WurstFolders.MAIN.toString();
        this.lastLaunchedVersion = "0";
        this.google_analytics = new GoogleAnalytics();
    }
    
    public class GoogleAnalytics
    {
        public boolean enabled;
        public int id;
        public long first_launch;
        public long last_launch;
        public int launches;
        
        public GoogleAnalytics() {
            this.enabled = true;
            this.id = (new SecureRandom().nextInt() & Integer.MAX_VALUE);
            this.first_launch = System.currentTimeMillis() / 1000L;
            this.last_launch = System.currentTimeMillis() / 1000L;
            this.launches = 0;
        }
    }
}
