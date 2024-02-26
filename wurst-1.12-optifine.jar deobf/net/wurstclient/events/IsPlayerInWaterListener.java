// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface IsPlayerInWaterListener extends Listener
{
    void onIsPlayerInWater(final IsPlayerInWaterEvent p0);
    
    public static class IsPlayerInWaterEvent extends Event<IsPlayerInWaterListener>
    {
        private boolean inWater;
        private final boolean normallyInWater;
        
        public IsPlayerInWaterEvent(final boolean inWater) {
            this.inWater = inWater;
            this.normallyInWater = inWater;
        }
        
        public boolean isInWater() {
            return this.inWater;
        }
        
        public void setInWater(final boolean inWater) {
            this.inWater = inWater;
        }
        
        public boolean isNormallyInWater() {
            return this.normallyInWater;
        }
        
        @Override
        public void fire(final ArrayList<IsPlayerInWaterListener> listeners) {
            for (final IsPlayerInWaterListener listener : listeners) {
                listener.onIsPlayerInWater(this);
            }
        }
        
        @Override
        public Class<IsPlayerInWaterListener> getListenerType() {
            return IsPlayerInWaterListener.class;
        }
    }
}
