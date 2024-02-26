// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface GetAmbientOcclusionLightValueListener extends Listener
{
    void onGetAmbientOcclusionLightValue(final GetAmbientOcclusionLightValueEvent p0);
    
    public static class GetAmbientOcclusionLightValueEvent extends Event<GetAmbientOcclusionLightValueListener>
    {
        private final IBlockState state;
        private float lightValue;
        private final float defaultLightValue;
        
        public GetAmbientOcclusionLightValueEvent(final IBlockState state, final float lightValue) {
            this.state = state;
            this.lightValue = lightValue;
            this.defaultLightValue = lightValue;
        }
        
        public IBlockState getState() {
            return this.state;
        }
        
        public float getLightValue() {
            return this.lightValue;
        }
        
        public void setLightValue(final float lightValue) {
            this.lightValue = lightValue;
        }
        
        public float getDefaultLightValue() {
            return this.defaultLightValue;
        }
        
        @Override
        public void fire(final ArrayList<GetAmbientOcclusionLightValueListener> listeners) {
            for (final GetAmbientOcclusionLightValueListener listener : listeners) {
                listener.onGetAmbientOcclusionLightValue(this);
            }
        }
        
        @Override
        public Class<GetAmbientOcclusionLightValueListener> getListenerType() {
            return GetAmbientOcclusionLightValueListener.class;
        }
    }
}
