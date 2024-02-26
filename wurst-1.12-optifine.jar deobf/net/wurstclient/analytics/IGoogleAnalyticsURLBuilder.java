// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.analytics;

public interface IGoogleAnalyticsURLBuilder
{
    void resetSession();
    
    String getGoogleAnalyticsVersion();
    
    String buildURL(final AnalyticsRequestData p0);
}
