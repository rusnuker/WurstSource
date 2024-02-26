// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.network.Packet;

public final class WConnection
{
    public static void sendPacket(final Packet packet) {
        WMinecraft.getConnection().sendPacket(packet);
    }
    
    public static void sendPacketBypass(final Packet packet) {
        WMinecraft.getConnection().sendPacketBypass(packet);
    }
}
