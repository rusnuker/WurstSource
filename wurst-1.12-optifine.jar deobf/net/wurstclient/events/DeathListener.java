// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface DeathListener extends Listener
{
    void onDeath();
    
    public static class DeathEvent extends Event<DeathListener>
    {
        public static final DeathEvent INSTANCE;
        
        static {
            INSTANCE = new DeathEvent();
        }
        
        @Override
        public void fire(final ArrayList<DeathListener> listeners) {
            for (final DeathListener listener : listeners) {
                listener.onDeath();
            }
        }
        
        @Override
        public Class<DeathListener> getListenerType() {
            return DeathListener.class;
        }
    }
}
