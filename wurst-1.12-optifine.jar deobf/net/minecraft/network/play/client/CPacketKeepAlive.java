// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.Packet;

public class CPacketKeepAlive implements Packet<INetHandlerPlayServer>
{
    private int key;
    
    public CPacketKeepAlive() {
    }
    
    public CPacketKeepAlive(final int idIn) {
        this.key = idIn;
    }
    
    @Override
    public void processPacket(final INetHandlerPlayServer handler) {
        handler.processKeepAlive(this);
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.key = buf.readVarIntFromBuffer();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.key);
    }
    
    public int getKey() {
        return this.key;
    }
}
