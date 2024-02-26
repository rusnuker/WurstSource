// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.analytics;

import net.wurstclient.WurstClient;

public class AnalyticsConfigData
{
    private final String trackingCode;
    private String encoding;
    private String screenResolution;
    private String colorDepth;
    private String userLanguage;
    private String flashVersion;
    private String userAgent;
    private VisitorData visitorData;
    
    public AnalyticsConfigData(final String argTrackingCode) {
        this(argTrackingCode, VisitorData.newSession(WurstClient.INSTANCE.options.google_analytics.id, WurstClient.INSTANCE.options.google_analytics.first_launch, WurstClient.INSTANCE.options.google_analytics.last_launch, WurstClient.INSTANCE.options.google_analytics.launches));
    }
    
    public AnalyticsConfigData(final String argTrackingCode, final VisitorData visitorData) {
        this.encoding = "UTF-8";
        this.screenResolution = null;
        this.colorDepth = null;
        this.userLanguage = null;
        this.flashVersion = null;
        this.userAgent = null;
        if (argTrackingCode == null) {
            throw new RuntimeException("Tracking code cannot be null");
        }
        this.trackingCode = argTrackingCode;
        this.visitorData = visitorData;
    }
    
    public String getColorDepth() {
        return this.colorDepth;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public String getFlashVersion() {
        return this.flashVersion;
    }
    
    public String getScreenResolution() {
        return this.screenResolution;
    }
    
    public String getTrackingCode() {
        return this.trackingCode;
    }
    
    public String getUserLanguage() {
        return this.userLanguage;
    }
    
    public String getUserAgent() {
        return this.userAgent;
    }
    
    public VisitorData getVisitorData() {
        return this.visitorData;
    }
    
    public void setColorDepth(final String argColorDepth) {
        this.colorDepth = argColorDepth;
    }
    
    public void setEncoding(final String argEncoding) {
        this.encoding = argEncoding;
    }
    
    public void setFlashVersion(final String argFlashVersion) {
        this.flashVersion = argFlashVersion;
    }
    
    public void setScreenResolution(final String argScreenResolution) {
        this.screenResolution = argScreenResolution;
    }
    
    public void setUserLanguage(final String argUserLanguage) {
        this.userLanguage = argUserLanguage;
    }
    
    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }
}
