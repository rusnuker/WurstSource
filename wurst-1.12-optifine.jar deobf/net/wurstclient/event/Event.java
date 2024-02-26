// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.event;

import java.util.ArrayList;

public abstract class Event<T extends Listener>
{
    public abstract void fire(final ArrayList<T> p0);
    
    public abstract Class<T> getListenerType();
}
