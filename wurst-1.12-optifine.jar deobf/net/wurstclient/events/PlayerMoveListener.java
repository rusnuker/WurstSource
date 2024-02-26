// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.Event;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.event.Listener;

public interface PlayerMoveListener extends Listener
{
    void onPlayerMove(final EntityPlayerSP p0);
    
    public static class PlayerMoveEvent extends Event<PlayerMoveListener>
    {
        private final EntityPlayerSP player;
        
        public PlayerMoveEvent(final EntityPlayerSP player) {
            this.player = player;
        }
        
        @Override
        public void fire(final ArrayList<PlayerMoveListener> listeners) {
            for (final PlayerMoveListener listener : listeners) {
                listener.onPlayerMove(this.player);
            }
        }
        
        @Override
        public Class<PlayerMoveListener> getListenerType() {
            return PlayerMoveListener.class;
        }
    }
}
