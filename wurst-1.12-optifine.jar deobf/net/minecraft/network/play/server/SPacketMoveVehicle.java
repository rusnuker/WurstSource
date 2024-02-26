// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public class SPacketMoveVehicle implements Packet<INetHandlerPlayClient>
{
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    
    public SPacketMoveVehicle() {
    }
    
    public SPacketMoveVehicle(final Entity entityIn) {
        this.x = entityIn.posX;
        this.y = entityIn.posY;
        this.z = entityIn.posZ;
        this.yaw = entityIn.rotationYaw;
        this.pitch = entityIn.rotationPitch;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleMoveVehicle(this);
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public float getPitch() {
        return this.pitch;
    }
}
