// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface VelocityFromFluidListener extends Listener
{
    void onVelocityFromFluid(final VelocityFromFluidEvent p0);
    
    public static class VelocityFromFluidEvent extends CancellableEvent<VelocityFromFluidListener>
    {
        @Override
        public void fire(final ArrayList<VelocityFromFluidListener> listeners) {
            for (final VelocityFromFluidListener listener : listeners) {
                listener.onVelocityFromFluid(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<VelocityFromFluidListener> getListenerType() {
            return VelocityFromFluidListener.class;
        }
    }
}
