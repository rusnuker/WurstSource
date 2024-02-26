// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.event;

public abstract class CancellableEvent<T extends Listener> extends Event<T>
{
    private boolean cancelled;
    
    public CancellableEvent() {
        this.cancelled = false;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
}
