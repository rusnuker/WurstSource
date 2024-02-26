// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.analytics;

public class AnalyticsRequestData
{
    private String pageTitle;
    private String hostName;
    private String pageURL;
    private String eventCategory;
    private String eventAction;
    private String eventLabel;
    private Integer eventValue;
    private String utmcsr;
    private String utmccn;
    private String utmctr;
    private String utmcmd;
    private String utmcct;
    
    public AnalyticsRequestData() {
        this.pageTitle = null;
        this.hostName = null;
        this.pageURL = null;
        this.eventCategory = null;
        this.eventAction = null;
        this.eventLabel = null;
        this.eventValue = null;
        this.utmcsr = "(direct)";
        this.utmccn = "(direct)";
        this.utmctr = null;
        this.utmcmd = "(none)";
        this.utmcct = null;
    }
    
    public void setReferrer(final String argSite, final String argPage) {
        this.utmcmd = "referral";
        this.utmcct = argPage;
        this.utmccn = "(referral)";
        this.utmcsr = argSite;
        this.utmctr = null;
    }
    
    public void setSearchReferrer(final String argSearchSource, final String argSearchKeywords) {
        this.utmcsr = argSearchSource;
        this.utmctr = argSearchKeywords;
        this.utmcmd = "organic";
        this.utmccn = "(organic)";
        this.utmcct = null;
    }
    
    public String getUtmcsr() {
        return this.utmcsr;
    }
    
    public String getUtmccn() {
        return this.utmccn;
    }
    
    public String getUtmctr() {
        return this.utmctr;
    }
    
    public String getUtmcmd() {
        return this.utmcmd;
    }
    
    public String getUtmcct() {
        return this.utmcct;
    }
    
    public String getEventAction() {
        return this.eventAction;
    }
    
    public String getEventCategory() {
        return this.eventCategory;
    }
    
    public String getEventLabel() {
        return this.eventLabel;
    }
    
    public Integer getEventValue() {
        return this.eventValue;
    }
    
    public String getHostName() {
        return this.hostName;
    }
    
    public String getPageTitle() {
        return this.pageTitle;
    }
    
    public String getPageURL() {
        return this.pageURL;
    }
    
    public void setEventAction(final String argEventAction) {
        this.eventAction = argEventAction;
    }
    
    public void setEventCategory(final String argEventCategory) {
        this.eventCategory = argEventCategory;
    }
    
    public void setEventLabel(final String argEventLabel) {
        this.eventLabel = argEventLabel;
    }
    
    public void setEventValue(final Integer argEventValue) {
        this.eventValue = argEventValue;
    }
    
    public void setHostName(final String argHostName) {
        this.hostName = argHostName;
    }
    
    public void setPageTitle(final String argContentTitle) {
        this.pageTitle = argContentTitle;
    }
    
    public void setPageURL(final String argPageURL) {
        this.pageURL = argPageURL;
    }
}
