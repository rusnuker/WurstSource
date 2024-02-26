// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface ShouldSideBeRenderedListener extends Listener
{
    void onShouldSideBeRendered(final ShouldSideBeRenderedEvent p0);
    
    public static class ShouldSideBeRenderedEvent extends Event<ShouldSideBeRenderedListener>
    {
        private final IBlockState state;
        private boolean rendered;
        private final boolean normallyRendered;
        
        public ShouldSideBeRenderedEvent(final IBlockState state, final boolean rendered) {
            this.state = state;
            this.rendered = rendered;
            this.normallyRendered = rendered;
        }
        
        public IBlockState getState() {
            return this.state;
        }
        
        public boolean isRendered() {
            return this.rendered;
        }
        
        public void setRendered(final boolean rendered) {
            this.rendered = rendered;
        }
        
        public boolean isNormallyRendered() {
            return this.normallyRendered;
        }
        
        @Override
        public void fire(final ArrayList<ShouldSideBeRenderedListener> listeners) {
            for (final ShouldSideBeRenderedListener listener : listeners) {
                listener.onShouldSideBeRendered(this);
            }
        }
        
        @Override
        public Class<ShouldSideBeRenderedListener> getListenerType() {
            return ShouldSideBeRenderedListener.class;
        }
    }
}
