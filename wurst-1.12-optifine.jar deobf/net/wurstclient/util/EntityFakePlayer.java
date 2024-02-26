// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util;

import net.minecraft.entity.player.EntityPlayer;
import net.wurstclient.compatibility.WPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class EntityFakePlayer extends EntityOtherPlayerMP
{
    public EntityFakePlayer() {
        super(WMinecraft.getWorld(), WMinecraft.getPlayer().getGameProfile());
        this.copyLocationAndAnglesFrom(WMinecraft.getPlayer());
        this.inventory.copyInventory(WMinecraft.getPlayer().inventory);
        WPlayer.copyPlayerModel(WMinecraft.getPlayer(), this);
        this.rotationYawHead = WMinecraft.getPlayer().rotationYawHead;
        this.renderYawOffset = WMinecraft.getPlayer().renderYawOffset;
        this.chasingPosX = this.posX;
        this.chasingPosY = this.posY;
        this.chasingPosZ = this.posZ;
        WMinecraft.getWorld().addEntityToWorld(this.getEntityId(), this);
    }
    
    public void resetPlayerPosition() {
        WMinecraft.getPlayer().setPositionAndRotation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
    }
    
    public void despawn() {
        WMinecraft.getWorld().removeEntityFromWorld(this.getEntityId());
    }
}
