// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public final class SPacketSomethingNew implements Packet<INetHandlerPlayClient>
{
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        buf.readByte();
        buf.readVarIntFromBuffer();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeByte(0);
        buf.writeVarIntToBuffer(0);
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
    }
}
