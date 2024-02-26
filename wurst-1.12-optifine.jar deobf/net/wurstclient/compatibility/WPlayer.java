// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.wurstclient.WurstClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

public final class WPlayer
{
    public static void swingArmClient() {
        WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
    }
    
    public static void swingArmPacket() {
        WConnection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
    }
    
    public static void prepareAttack() {
        WurstClient.INSTANCE.hax.autoSwordMod.setSlot();
        WurstClient.INSTANCE.hax.criticalsMod.doCritical();
    }
    
    public static void attackEntity(final Entity entity) {
        Minecraft.getMinecraft().playerController.attackEntity(WMinecraft.getPlayer(), entity);
        swingArmClient();
    }
    
    public static void sendAttackPacket(final Entity entity) {
        WConnection.sendPacket(new CPacketUseEntity(entity, EnumHand.MAIN_HAND));
    }
    
    public static float getCooldown() {
        return WMinecraft.getPlayer().getCooledAttackStrength(0.0f);
    }
    
    public static void addPotionEffect(final Potion potion) {
        WMinecraft.getPlayer().addPotionEffect(new PotionEffect(potion, 10801220));
    }
    
    public static void removePotionEffect(final Potion potion) {
        WMinecraft.getPlayer().removePotionEffect(potion);
    }
    
    public static void copyPlayerModel(final EntityPlayer from, final EntityPlayer to) {
        to.getDataManager().set(EntityPlayer.PLAYER_MODEL_FLAG, (Byte)from.getDataManager().get((DataParameter<T>)EntityPlayer.PLAYER_MODEL_FLAG));
    }
}
