// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.analytics;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.net.SocketAddress;
import java.util.regex.MatchResult;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.LinkedList;
import java.net.Proxy;
import java.util.logging.Logger;

public class JGoogleAnalyticsTracker
{
    private static Logger logger;
    private static final ThreadGroup asyncThreadGroup;
    private static long asyncThreadsRunning;
    private static Proxy proxy;
    private static LinkedList<String> fifo;
    private static volatile Thread backgroundThread;
    private static boolean backgroundThreadMayRun;
    private GoogleAnalyticsVersion gaVersion;
    private AnalyticsConfigData configData;
    private IGoogleAnalyticsURLBuilder builder;
    private DispatchMode mode;
    private boolean enabled;
    
    static {
        JGoogleAnalyticsTracker.logger = Logger.getLogger(JGoogleAnalyticsTracker.class.getName());
        asyncThreadGroup = new ThreadGroup("Async Google Analytics Threads");
        JGoogleAnalyticsTracker.asyncThreadsRunning = 0L;
        JGoogleAnalyticsTracker.proxy = Proxy.NO_PROXY;
        JGoogleAnalyticsTracker.fifo = new LinkedList<String>();
        JGoogleAnalyticsTracker.backgroundThread = null;
        JGoogleAnalyticsTracker.backgroundThreadMayRun = false;
        JGoogleAnalyticsTracker.asyncThreadGroup.setMaxPriority(1);
        JGoogleAnalyticsTracker.asyncThreadGroup.setDaemon(true);
    }
    
    public JGoogleAnalyticsTracker(final AnalyticsConfigData argConfigData, final GoogleAnalyticsVersion argVersion) {
        this(argConfigData, argVersion, DispatchMode.SINGLE_THREAD);
    }
    
    public JGoogleAnalyticsTracker(final AnalyticsConfigData argConfigData, final GoogleAnalyticsVersion argVersion, final DispatchMode argMode) {
        this.gaVersion = argVersion;
        this.configData = argConfigData;
        this.createBuilder();
        this.enabled = true;
        this.setDispatchMode(argMode);
    }
    
    public void setDispatchMode(DispatchMode argMode) {
        if (argMode == null) {
            argMode = DispatchMode.SINGLE_THREAD;
        }
        if (argMode == DispatchMode.SINGLE_THREAD) {
            this.startBackgroundThread();
        }
        this.mode = argMode;
    }
    
    public DispatchMode getDispatchMode() {
        return this.mode;
    }
    
    public boolean isSynchronous() {
        return this.mode == DispatchMode.SYNCHRONOUS;
    }
    
    public boolean isSingleThreaded() {
        return this.mode == DispatchMode.SINGLE_THREAD;
    }
    
    public boolean isMultiThreaded() {
        return this.mode == DispatchMode.MULTI_THREAD;
    }
    
    public void resetSession() {
        this.builder.resetSession();
    }
    
    public void setEnabled(final boolean argEnabled) {
        this.enabled = argEnabled;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public static void setProxy(final Proxy argProxy) {
        JGoogleAnalyticsTracker.proxy = ((argProxy != null) ? argProxy : Proxy.NO_PROXY);
    }
    
    public static void setProxy(String proxyAddr) {
        if (proxyAddr != null) {
            proxyAddr = null;
            int proxyPort = 8080;
            Throwable t = null;
            try {
                final Scanner s = new Scanner(proxyAddr);
                try {
                    s.findInLine("(http://|)([^:/]+)(:|)([0-9]*)(/|)");
                    final MatchResult m = s.match();
                    if (m.groupCount() >= 2) {
                        proxyAddr = m.group(2);
                    }
                    if (m.groupCount() >= 4 && m.group(4).length() != 0) {
                        proxyPort = Integer.parseInt(m.group(4));
                    }
                }
                finally {
                    if (s != null) {
                        s.close();
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
            if (proxyAddr != null) {
                final SocketAddress sa = new InetSocketAddress(proxyAddr, proxyPort);
                setProxy(new Proxy(Proxy.Type.HTTP, sa));
            }
        }
    }
    
    public static void completeBackgroundTasks(final long timeoutMillis) {
        boolean fifoEmpty = false;
        boolean asyncThreadsCompleted = false;
        final long absTimeout = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < absTimeout) {
            synchronized (JGoogleAnalyticsTracker.fifo) {
                fifoEmpty = (JGoogleAnalyticsTracker.fifo.size() == 0);
                monitorexit(JGoogleAnalyticsTracker.fifo);
            }
            synchronized (JGoogleAnalyticsTracker.class) {
                asyncThreadsCompleted = (JGoogleAnalyticsTracker.asyncThreadsRunning == 0L);
                monitorexit(JGoogleAnalyticsTracker.class);
            }
            if (fifoEmpty && asyncThreadsCompleted) {
                break;
            }
            try {
                Thread.sleep(100L);
            }
            catch (final InterruptedException e) {
                break;
            }
        }
    }
    
    public void trackPageView(final String argPageURL, final String argPageTitle, final String argHostName) {
        if (argPageURL == null) {
            throw new IllegalArgumentException("Page URL cannot be null, Google will not track the data.");
        }
        final AnalyticsRequestData data = new AnalyticsRequestData();
        data.setHostName(argHostName);
        data.setPageTitle(argPageTitle);
        data.setPageURL(argPageURL);
        this.makeCustomRequest(data);
    }
    
    public void trackPageViewFromReferrer(final String argPageURL, final String argPageTitle, final String argHostName, final String argReferrerSite, final String argReferrerPage) {
        if (argPageURL == null) {
            throw new IllegalArgumentException("Page URL cannot be null, Google will not track the data.");
        }
        final AnalyticsRequestData data = new AnalyticsRequestData();
        data.setHostName(argHostName);
        data.setPageTitle(argPageTitle);
        data.setPageURL(argPageURL);
        data.setReferrer(argReferrerSite, argReferrerPage);
        this.makeCustomRequest(data);
    }
    
    public void trackPageViewFromSearch(final String argPageURL, final String argPageTitle, final String argHostName, final String argSearchSource, final String argSearchKeywords) {
        if (argPageURL == null) {
            throw new IllegalArgumentException("Page URL cannot be null, Google will not track the data.");
        }
        final AnalyticsRequestData data = new AnalyticsRequestData();
        data.setHostName(argHostName);
        data.setPageTitle(argPageTitle);
        data.setPageURL(argPageURL);
        data.setSearchReferrer(argSearchSource, argSearchKeywords);
        this.makeCustomRequest(data);
    }
    
    public void trackEvent(final String argCategory, final String argAction) {
        this.trackEvent(argCategory, argAction, null, null);
    }
    
    public void trackEvent(final String argCategory, final String argAction, final String argLabel) {
        this.trackEvent(argCategory, argAction, argLabel, null);
    }
    
    public void trackEvent(final String argCategory, final String argAction, final String argLabel, final Integer argValue) {
        final AnalyticsRequestData data = new AnalyticsRequestData();
        data.setEventCategory(argCategory);
        data.setEventAction(argAction);
        data.setEventLabel(argLabel);
        data.setEventValue(argValue);
        this.makeCustomRequest(data);
    }
    
    public synchronized void makeCustomRequest(final AnalyticsRequestData argData) {
        if (!this.enabled) {
            JGoogleAnalyticsTracker.logger.log(Level.CONFIG, "Ignoring tracking request, enabled is false");
            return;
        }
        if (argData == null) {
            throw new NullPointerException("Data cannot be null");
        }
        if (this.builder == null) {
            throw new NullPointerException("Class was not initialized");
        }
        final String url = this.builder.buildURL(argData);
        final String userAgent = this.configData.getUserAgent();
        switch (this.mode) {
            case MULTI_THREAD: {
                final Thread t = new Thread(JGoogleAnalyticsTracker.asyncThreadGroup, "AnalyticsThread-" + JGoogleAnalyticsTracker.asyncThreadGroup.activeCount()) {
                    @Override
                    public void run() {
                        synchronized (JGoogleAnalyticsTracker.class) {
                            JGoogleAnalyticsTracker.access$1(JGoogleAnalyticsTracker.asyncThreadsRunning + 1L);
                            monitorexit(JGoogleAnalyticsTracker.class);
                        }
                        try {
                            dispatchRequest(url, userAgent);
                        }
                        finally {
                            synchronized (JGoogleAnalyticsTracker.class) {
                                JGoogleAnalyticsTracker.access$1(JGoogleAnalyticsTracker.asyncThreadsRunning - 1L);
                                monitorexit(JGoogleAnalyticsTracker.class);
                            }
                        }
                        synchronized (JGoogleAnalyticsTracker.class) {
                            JGoogleAnalyticsTracker.access$1(JGoogleAnalyticsTracker.asyncThreadsRunning - 1L);
                            monitorexit(JGoogleAnalyticsTracker.class);
                        }
                    }
                };
                t.setDaemon(true);
                t.start();
                break;
            }
            case SYNCHRONOUS: {
                dispatchRequest(url, userAgent);
                break;
            }
            default: {
                synchronized (JGoogleAnalyticsTracker.fifo) {
                    JGoogleAnalyticsTracker.fifo.addLast(url);
                    JGoogleAnalyticsTracker.fifo.notify();
                    monitorexit(JGoogleAnalyticsTracker.fifo);
                }
                if (!JGoogleAnalyticsTracker.backgroundThreadMayRun) {
                    JGoogleAnalyticsTracker.logger.log(Level.SEVERE, "A tracker request has been added to the queue but the background thread isn't running.", url);
                    break;
                }
                break;
            }
        }
    }
    
    private static void dispatchRequest(final String argURL, final String userAgent) {
        try {
            final URL url = new URL(argURL);
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection(JGoogleAnalyticsTracker.proxy);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            if (userAgent != null) {
                connection.addRequestProperty("User-Agent", userAgent);
            }
            connection.connect();
            final int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                JGoogleAnalyticsTracker.logger.log(Level.SEVERE, "JGoogleAnalyticsTracker: Error requesting url '" + argURL + "', received response code " + responseCode);
            }
            else {
                JGoogleAnalyticsTracker.logger.log(Level.CONFIG, "JGoogleAnalyticsTracker: Tracking success for url '" + argURL + "'");
            }
        }
        catch (final Exception e) {
            JGoogleAnalyticsTracker.logger.log(Level.SEVERE, "Error making tracking request", e);
        }
    }
    
    private void createBuilder() {
        switch (this.gaVersion) {
            case V_4_7_2: {
                this.builder = new GoogleAnalyticsV4_7_2(this.configData);
                break;
            }
            default: {
                this.builder = new GoogleAnalyticsV4_7_2(this.configData);
                break;
            }
        }
    }
    
    private synchronized void startBackgroundThread() {
        if (JGoogleAnalyticsTracker.backgroundThread == null) {
            JGoogleAnalyticsTracker.backgroundThreadMayRun = true;
            (JGoogleAnalyticsTracker.backgroundThread = new Thread(JGoogleAnalyticsTracker.asyncThreadGroup, "AnalyticsBackgroundThread") {
                @Override
                public void run() {
                    JGoogleAnalyticsTracker.logger.log(Level.CONFIG, "AnalyticsBackgroundThread started");
                    while (JGoogleAnalyticsTracker.backgroundThreadMayRun) {
                        try {
                            String url = null;
                            synchronized (JGoogleAnalyticsTracker.fifo) {
                                if (JGoogleAnalyticsTracker.fifo.isEmpty()) {
                                    JGoogleAnalyticsTracker.fifo.wait();
                                }
                                if (!JGoogleAnalyticsTracker.fifo.isEmpty()) {
                                    url = JGoogleAnalyticsTracker.fifo.getFirst();
                                }
                                monitorexit(JGoogleAnalyticsTracker.fifo);
                            }
                            if (url == null) {
                                continue;
                            }
                            try {
                                dispatchRequest(url, JGoogleAnalyticsTracker.this.configData.getUserAgent());
                            }
                            finally {
                                synchronized (JGoogleAnalyticsTracker.fifo) {
                                    JGoogleAnalyticsTracker.fifo.removeFirst();
                                    monitorexit(JGoogleAnalyticsTracker.fifo);
                                }
                            }
                            synchronized (JGoogleAnalyticsTracker.fifo) {
                                JGoogleAnalyticsTracker.fifo.removeFirst();
                                monitorexit(JGoogleAnalyticsTracker.fifo);
                            }
                        }
                        catch (final Exception e) {
                            JGoogleAnalyticsTracker.logger.log(Level.SEVERE, "Got exception from dispatch thread", e);
                        }
                    }
                }
            }).setDaemon(true);
            JGoogleAnalyticsTracker.backgroundThread.start();
        }
    }
    
    public static synchronized void stopBackgroundThread(final long timeoutMillis) {
        JGoogleAnalyticsTracker.backgroundThreadMayRun = false;
        JGoogleAnalyticsTracker.fifo.notify();
        if (JGoogleAnalyticsTracker.backgroundThread != null && timeoutMillis > 0L) {
            try {
                JGoogleAnalyticsTracker.backgroundThread.join(timeoutMillis);
            }
            catch (final InterruptedException ex) {}
            JGoogleAnalyticsTracker.backgroundThread = null;
        }
    }
    
    static /* synthetic */ void access$1(final long asyncThreadsRunning) {
        JGoogleAnalyticsTracker.asyncThreadsRunning = asyncThreadsRunning;
    }
    
    public enum DispatchMode
    {
        SYNCHRONOUS("SYNCHRONOUS", 0), 
        MULTI_THREAD("MULTI_THREAD", 1), 
        SINGLE_THREAD("SINGLE_THREAD", 2);
        
        private DispatchMode(final String name, final int ordinal) {
        }
    }
    
    public enum GoogleAnalyticsVersion
    {
        V_4_7_2("V_4_7_2", 0);
        
        private GoogleAnalyticsVersion(final String name, final int ordinal) {
        }
    }
}
