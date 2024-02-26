// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface PostUpdateListener extends Listener
{
    void afterUpdate();
    
    public static class PostUpdateEvent extends Event<PostUpdateListener>
    {
        public static final PostUpdateEvent INSTANCE;
        
        static {
            INSTANCE = new PostUpdateEvent();
        }
        
        @Override
        public void fire(final ArrayList<PostUpdateListener> listeners) {
            for (final PostUpdateListener listener : listeners) {
                listener.afterUpdate();
            }
        }
        
        @Override
        public Class<PostUpdateListener> getListenerType() {
            return PostUpdateListener.class;
        }
    }
}
