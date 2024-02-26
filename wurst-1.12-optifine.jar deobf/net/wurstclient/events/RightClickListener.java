// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface RightClickListener extends Listener
{
    void onRightClick(final RightClickEvent p0);
    
    public static class RightClickEvent extends CancellableEvent<RightClickListener>
    {
        @Override
        public void fire(final ArrayList<RightClickListener> listeners) {
            for (final RightClickListener listener : listeners) {
                listener.onRightClick(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<RightClickListener> getListenerType() {
            return RightClickListener.class;
        }
    }
}
