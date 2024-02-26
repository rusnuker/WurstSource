// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.analytics;

import net.wurstclient.bot.WurstBot;
import net.wurstclient.options.OptionsManager;
import net.wurstclient.WurstClient;

public class AnalyticsManager
{
    private final JGoogleAnalyticsTracker tracker;
    public final String ANALYTICS_CODE;
    public final String HOSTNAME;
    public long lastRequest;
    
    public AnalyticsManager(final String analyticsCode, final String hostName) {
        if (WurstClient.INSTANCE.options.google_analytics == null) {
            WurstClient.INSTANCE.options.google_analytics = WurstClient.INSTANCE.options.new GoogleAnalytics();
        }
        this.tracker = new JGoogleAnalyticsTracker(new AnalyticsConfigData(analyticsCode), JGoogleAnalyticsTracker.GoogleAnalyticsVersion.V_4_7_2);
        this.ANALYTICS_CODE = analyticsCode;
        this.HOSTNAME = hostName;
        this.lastRequest = System.currentTimeMillis();
        JGoogleAnalyticsTracker.setProxy(System.getenv("http_proxy"));
    }
    
    private boolean shouldTrack() {
        return WurstClient.INSTANCE.options.google_analytics.enabled && !WurstBot.isEnabled();
    }
    
    public void trackPageView(final String url, final String title) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackPageView(url, title, this.HOSTNAME);
        this.lastRequest = System.currentTimeMillis();
    }
    
    public void trackPageViewFromReferrer(final String url, final String title, final String referrerSite, final String referrerPage) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackPageViewFromReferrer(url, title, this.HOSTNAME, referrerSite, referrerPage);
        this.lastRequest = System.currentTimeMillis();
    }
    
    public void trackPageViewFromSearch(final String url, final String title, final String searchSite, final String keywords) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackPageViewFromSearch(url, title, this.HOSTNAME, searchSite, keywords);
        this.lastRequest = System.currentTimeMillis();
    }
    
    public void trackEvent(final String category, final String action) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackEvent(category, action);
        this.lastRequest = System.currentTimeMillis();
    }
    
    public void trackEvent(final String category, final String action, final String label) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackEvent(category, action, label);
        this.lastRequest = System.currentTimeMillis();
    }
    
    public void trackEvent(final String category, final String action, final String label, final int value) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackEvent(category, action, label, new Integer(value));
        this.lastRequest = System.currentTimeMillis();
    }
    
    public void makeCustomRequest(final AnalyticsRequestData data) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.makeCustomRequest(data);
        this.lastRequest = System.currentTimeMillis();
    }
}
