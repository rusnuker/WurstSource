// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.network.Packet;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface PacketOutputListener extends Listener
{
    void onSentPacket(final PacketOutputEvent p0);
    
    public static class PacketOutputEvent extends CancellableEvent<PacketOutputListener>
    {
        private Packet packet;
        
        public PacketOutputEvent(final Packet packet) {
            this.packet = packet;
        }
        
        public Packet getPacket() {
            return this.packet;
        }
        
        public void setPacket(final Packet packet) {
            this.packet = packet;
        }
        
        @Override
        public void fire(final ArrayList<PacketOutputListener> listeners) {
            for (final PacketOutputListener listener : listeners) {
                listener.onSentPacket(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<PacketOutputListener> getListenerType() {
            return PacketOutputListener.class;
        }
    }
}
