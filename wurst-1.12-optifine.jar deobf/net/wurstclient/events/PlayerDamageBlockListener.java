// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface PlayerDamageBlockListener extends Listener
{
    void onPlayerDamageBlock(final PlayerDamageBlockEvent p0);
    
    public static class PlayerDamageBlockEvent extends Event<PlayerDamageBlockListener>
    {
        private final BlockPos pos;
        private final EnumFacing facing;
        
        public PlayerDamageBlockEvent(final BlockPos pos, final EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }
        
        @Override
        public void fire(final ArrayList<PlayerDamageBlockListener> listeners) {
            for (final PlayerDamageBlockListener listener : listeners) {
                listener.onPlayerDamageBlock(this);
            }
        }
        
        @Override
        public Class<PlayerDamageBlockListener> getListenerType() {
            return PlayerDamageBlockListener.class;
        }
        
        public BlockPos getPos() {
            return this.pos;
        }
        
        public EnumFacing getFacing() {
            return this.facing;
        }
    }
}
