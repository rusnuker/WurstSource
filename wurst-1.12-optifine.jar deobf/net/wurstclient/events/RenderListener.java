// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface RenderListener extends Listener
{
    void onRender(final float p0);
    
    public static class RenderEvent extends Event<RenderListener>
    {
        private final float partialTicks;
        
        public RenderEvent(final float partialTicks) {
            this.partialTicks = partialTicks;
        }
        
        @Override
        public void fire(final ArrayList<RenderListener> listeners) {
            for (final RenderListener listener : listeners) {
                listener.onRender(this.partialTicks);
            }
        }
        
        @Override
        public Class<RenderListener> getListenerType() {
            return RenderListener.class;
        }
    }
}
