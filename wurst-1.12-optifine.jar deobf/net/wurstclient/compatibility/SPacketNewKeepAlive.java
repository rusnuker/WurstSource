// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public class SPacketNewKeepAlive implements Packet<INetHandlerPlayClient>
{
    private long id;
    
    public SPacketNewKeepAlive() {
    }
    
    public SPacketNewKeepAlive(final long idIn) {
        this.id = idIn;
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleNewKeepAlive(this);
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.id = buf.readLong();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeLong(this.id);
    }
    
    public long getId() {
        return this.id;
    }
}
