// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface SetOpaqueCubeListener extends Listener
{
    void onSetOpaqueCube(final SetOpaqueCubeEvent p0);
    
    public static class SetOpaqueCubeEvent extends CancellableEvent<SetOpaqueCubeListener>
    {
        @Override
        public void fire(final ArrayList<SetOpaqueCubeListener> listeners) {
            for (final SetOpaqueCubeListener listener : listeners) {
                listener.onSetOpaqueCube(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<SetOpaqueCubeListener> getListenerType() {
            return SetOpaqueCubeListener.class;
        }
    }
}
