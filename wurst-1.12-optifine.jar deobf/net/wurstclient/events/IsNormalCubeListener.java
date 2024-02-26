// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface IsNormalCubeListener extends Listener
{
    void onIsNormalCube(final IsNormalCubeEvent p0);
    
    public static class IsNormalCubeEvent extends CancellableEvent<IsNormalCubeListener>
    {
        @Override
        public void fire(final ArrayList<IsNormalCubeListener> listeners) {
            for (final IsNormalCubeListener listener : listeners) {
                listener.onIsNormalCube(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<IsNormalCubeListener> getListenerType() {
            return IsNormalCubeListener.class;
        }
    }
}
