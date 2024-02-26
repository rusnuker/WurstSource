// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface LeftClickListener extends Listener
{
    void onLeftClick(final LeftClickEvent p0);
    
    public static class LeftClickEvent extends CancellableEvent<LeftClickListener>
    {
        @Override
        public void fire(final ArrayList<LeftClickListener> listeners) {
            for (final LeftClickListener listener : listeners) {
                listener.onLeftClick(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<LeftClickListener> getListenerType() {
            return LeftClickListener.class;
        }
    }
}
