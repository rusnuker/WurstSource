// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface GUIRenderListener extends Listener
{
    void onRenderGUI();
    
    public static class GUIRenderEvent extends Event<GUIRenderListener>
    {
        public static final GUIRenderEvent INSTANCE;
        
        static {
            INSTANCE = new GUIRenderEvent();
        }
        
        @Override
        public void fire(final ArrayList<GUIRenderListener> listeners) {
            for (final GUIRenderListener listener : listeners) {
                listener.onRenderGUI();
            }
        }
        
        @Override
        public Class<GUIRenderListener> getListenerType() {
            return GUIRenderListener.class;
        }
    }
}
