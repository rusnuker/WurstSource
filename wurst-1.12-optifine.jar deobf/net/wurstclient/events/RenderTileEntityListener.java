// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.tileentity.TileEntity;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface RenderTileEntityListener extends Listener
{
    void onRenderTileEntity(final RenderTileEntityEvent p0);
    
    public static class RenderTileEntityEvent extends CancellableEvent<RenderTileEntityListener>
    {
        private final TileEntity tileEntity;
        
        public RenderTileEntityEvent(final TileEntity tileEntity) {
            this.tileEntity = tileEntity;
        }
        
        public TileEntity getTileEntity() {
            return this.tileEntity;
        }
        
        @Override
        public void fire(final ArrayList<RenderTileEntityListener> listeners) {
            for (final RenderTileEntityListener listener : listeners) {
                listener.onRenderTileEntity(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<RenderTileEntityListener> getListenerType() {
            return RenderTileEntityListener.class;
        }
    }
}
