// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface RenderBlockModelListener extends Listener
{
    void onRenderBlockModel(final RenderBlockModelEvent p0);
    
    public static class RenderBlockModelEvent extends CancellableEvent<RenderBlockModelListener>
    {
        private final IBlockState state;
        
        public RenderBlockModelEvent(final IBlockState state) {
            this.state = state;
        }
        
        public IBlockState getState() {
            return this.state;
        }
        
        @Override
        public void fire(final ArrayList<RenderBlockModelListener> listeners) {
            for (final RenderBlockModelListener listener : listeners) {
                listener.onRenderBlockModel(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<RenderBlockModelListener> getListenerType() {
            return RenderBlockModelListener.class;
        }
    }
}
