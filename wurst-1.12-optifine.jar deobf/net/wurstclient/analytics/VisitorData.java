// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.analytics;

import net.wurstclient.WurstClient;
import java.security.SecureRandom;

public class VisitorData
{
    private int visitorId;
    private long timestampFirst;
    private long timestampPrevious;
    private long timestampCurrent;
    private int visits;
    
    VisitorData(final int visitorId, final long timestampFirst, final long timestampPrevious, final long timestampCurrent, final int visits) {
        this.visitorId = visitorId;
        this.timestampFirst = timestampFirst;
        this.timestampPrevious = timestampPrevious;
        this.timestampCurrent = timestampCurrent;
        this.visits = visits;
    }
    
    public static VisitorData newVisitor() {
        final int visitorId = new SecureRandom().nextInt() & Integer.MAX_VALUE;
        final long now = now();
        return new VisitorData(visitorId, now, now, now, 1);
    }
    
    public static VisitorData newSession(final int visitorId, final long timestampfirst, final long timestamplast, final int visits) {
        final long now = now();
        WurstClient.INSTANCE.options.google_analytics.last_launch = now;
        WurstClient.INSTANCE.options.google_analytics.launches = visits + 1;
        return new VisitorData(visitorId, timestampfirst, timestamplast, now, visits + 1);
    }
    
    public void resetSession() {
        final long now = now();
        this.timestampPrevious = this.timestampCurrent;
        this.timestampCurrent = now;
        ++this.visits;
    }
    
    private static long now() {
        final long now = System.currentTimeMillis() / 1000L;
        return now;
    }
    
    public int getVisitorId() {
        return this.visitorId;
    }
    
    public long getTimestampFirst() {
        return this.timestampFirst;
    }
    
    public long getTimestampPrevious() {
        return this.timestampPrevious;
    }
    
    public long getTimestampCurrent() {
        return this.timestampCurrent;
    }
    
    public int getVisits() {
        return this.visits;
    }
}
