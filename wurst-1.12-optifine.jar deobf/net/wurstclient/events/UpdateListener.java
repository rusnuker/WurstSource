// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface UpdateListener extends Listener
{
    void onUpdate();
    
    public static class UpdateEvent extends Event<UpdateListener>
    {
        public static final UpdateEvent INSTANCE;
        
        static {
            INSTANCE = new UpdateEvent();
        }
        
        @Override
        public void fire(final ArrayList<UpdateListener> listeners) {
            for (final UpdateListener listener : listeners) {
                listener.onUpdate();
            }
        }
        
        @Override
        public Class<UpdateListener> getListenerType() {
            return UpdateListener.class;
        }
    }
}
