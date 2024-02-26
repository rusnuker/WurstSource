// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.analytics;

import java.util.Random;

public class GoogleAnalyticsV4_7_2 implements IGoogleAnalyticsURLBuilder
{
    public static final String URL_PREFIX = "http://www.google-analytics.com/__utm.gif";
    private AnalyticsConfigData config;
    private Random random;
    
    public GoogleAnalyticsV4_7_2(final AnalyticsConfigData argConfig) {
        this.random = new Random((long)(Math.random() * 9.223372036854776E18));
        this.config = argConfig;
    }
    
    @Override
    public String getGoogleAnalyticsVersion() {
        return "4.7.2";
    }
    
    @Override
    public String buildURL(final AnalyticsRequestData argData) {
        final StringBuilder sb = new StringBuilder();
        sb.append("http://www.google-analytics.com/__utm.gif");
        System.currentTimeMillis();
        sb.append("?utmwv=" + this.getGoogleAnalyticsVersion());
        sb.append("&utmn=" + this.random.nextInt());
        if (argData.getHostName() != null) {
            sb.append("&utmhn=" + this.getURIString(argData.getHostName()));
        }
        if (argData.getEventAction() != null && argData.getEventCategory() != null) {
            sb.append("&utmt=event");
            final String category = this.getURIString(argData.getEventCategory());
            final String action = this.getURIString(argData.getEventAction());
            sb.append("&utme=5(" + category + "*" + action);
            if (argData.getEventLabel() != null) {
                sb.append("*" + this.getURIString(argData.getEventLabel()));
            }
            sb.append(")");
            if (argData.getEventValue() != null) {
                sb.append("(" + argData.getEventValue() + ")");
            }
        }
        else if (argData.getEventAction() != null || argData.getEventCategory() != null) {
            throw new IllegalArgumentException("Event tracking must have both a category and an action");
        }
        if (this.config.getEncoding() != null) {
            sb.append("&utmcs=" + this.getURIString(this.config.getEncoding()));
        }
        else {
            sb.append("&utmcs=-");
        }
        if (this.config.getScreenResolution() != null) {
            sb.append("&utmsr=" + this.getURIString(this.config.getScreenResolution()));
        }
        if (this.config.getColorDepth() != null) {
            sb.append("&utmsc=" + this.getURIString(this.config.getColorDepth()));
        }
        if (this.config.getUserLanguage() != null) {
            sb.append("&utmul=" + this.getURIString(this.config.getUserLanguage()));
        }
        sb.append("&utmje=1");
        if (this.config.getFlashVersion() != null) {
            sb.append("&utmfl=" + this.getURIString(this.config.getFlashVersion()));
        }
        if (argData.getPageTitle() != null) {
            sb.append("&utmdt=" + this.getURIString(argData.getPageTitle()));
        }
        sb.append("&utmhid=" + this.random.nextInt());
        if (argData.getPageURL() != null) {
            sb.append("&utmp=" + this.getURIString(argData.getPageURL()));
        }
        sb.append("&utmac=" + this.config.getTrackingCode());
        final String utmcsr = this.getURIString(argData.getUtmcsr());
        final String utmccn = this.getURIString(argData.getUtmccn());
        final String utmctr = this.getURIString(argData.getUtmctr());
        final String utmcmd = this.getURIString(argData.getUtmcmd());
        final String utmcct = this.getURIString(argData.getUtmcct());
        final int hostnameHash = this.hostnameHash(argData.getHostName());
        final int visitorId = this.config.getVisitorData().getVisitorId();
        final long timestampFirst = this.config.getVisitorData().getTimestampFirst();
        final long timestampPrevious = this.config.getVisitorData().getTimestampPrevious();
        final long timestampCurrent = this.config.getVisitorData().getTimestampCurrent();
        final int visits = this.config.getVisitorData().getVisits();
        sb.append("&utmcc=__utma%3D" + hostnameHash + "." + visitorId + "." + timestampFirst + "." + timestampPrevious + "." + timestampCurrent + "." + visits + "%3B%2B__utmz%3D" + hostnameHash + "." + timestampCurrent + ".1.1.utmcsr%3D" + utmcsr + "%7Cutmccn%3D" + utmccn + "%7Cutmcmd%3D" + utmcmd + ((utmctr != null) ? ("%7Cutmctr%3D" + utmctr) : "") + ((utmcct != null) ? ("%7Cutmcct%3D" + utmcct) : "") + "%3B&gaq=1");
        return sb.toString();
    }
    
    private String getURIString(final String argString) {
        if (argString == null) {
            return null;
        }
        return URIEncoder.encodeURI(argString);
    }
    
    private int hostnameHash(final String hostname) {
        return 999;
    }
    
    @Override
    public void resetSession() {
        this.config.getVisitorData().resetSession();
    }
}
