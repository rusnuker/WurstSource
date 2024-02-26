// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.network.Packet;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface PacketInputListener extends Listener
{
    void onReceivedPacket(final PacketInputEvent p0);
    
    public static class PacketInputEvent extends CancellableEvent<PacketInputListener>
    {
        private final Packet packet;
        
        public PacketInputEvent(final Packet packet) {
            this.packet = packet;
        }
        
        public Packet getPacket() {
            return this.packet;
        }
        
        @Override
        public void fire(final ArrayList<PacketInputListener> listeners) {
            for (final PacketInputListener listener : listeners) {
                listener.onReceivedPacket(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<PacketInputListener> getListenerType() {
            return PacketInputListener.class;
        }
    }
}
