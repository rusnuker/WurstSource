// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods;

import net.wurstclient.features.special_features.YesCheatSpf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemElytra;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "EasyElytra", "extra elytra", "easy elytra" })
@HelpPage("Mods/ExtraElytra")
public final class ExtraElytraMod extends Hack implements UpdateListener
{
    private CheckboxSetting instantFly;
    private final CheckboxSetting speedCtrl;
    private final CheckboxSetting heightCtrl;
    private CheckboxSetting stopInWater;
    private int jumpTimer;
    
    public ExtraElytraMod() {
        super("ExtraElytra", "Makes the Elytra easier to use.");
        this.instantFly = new CheckboxSetting("Instant fly", "Jump to fly, no weird double-jump needed!", true);
        this.speedCtrl = new CheckboxSetting("Speed control", "Control your speed with the Forward and Back keys.\n(default: W and S)\nNo fireworks needed!", true);
        this.heightCtrl = new CheckboxSetting("Height control", "Control your height with the Jump and Sneak keys.\n(default: Spacebar and Shift)\nNo fireworks needed!", false);
        this.stopInWater = new CheckboxSetting("Stop flying in water", true);
        this.setCategory(Category.MOVEMENT);
        this.addSetting(this.instantFly);
        this.addSetting(this.speedCtrl);
        this.addSetting(this.heightCtrl);
        this.addSetting(this.stopInWater);
    }
    
    @Override
    public void onEnable() {
        ExtraElytraMod.WURST.events.add(UpdateListener.class, this);
        this.jumpTimer = 0;
    }
    
    @Override
    public void onDisable() {
        ExtraElytraMod.WURST.events.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (this.jumpTimer > 0) {
            --this.jumpTimer;
        }
        final ItemStack chest = WMinecraft.getPlayer().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest == null || chest.getItem() != Items.ELYTRA) {
            return;
        }
        if (!WMinecraft.getPlayer().isElytraFlying()) {
            if (ItemElytra.isBroken(chest) && ExtraElytraMod.MC.gameSettings.keyBindJump.pressed) {
                this.doInstantFly();
            }
            return;
        }
        if (this.stopInWater.isChecked() && WMinecraft.getPlayer().isInWater()) {
            this.sendStartStopPacket();
            return;
        }
        this.controlSpeed();
        this.controlHeight();
    }
    
    private void sendStartStopPacket() {
        final CPacketEntityAction packet = new CPacketEntityAction(ExtraElytraMod.MC.player, CPacketEntityAction.Action.START_FALL_FLYING);
        WConnection.sendPacket(packet);
    }
    
    private void controlHeight() {
        if (!this.heightCtrl.isChecked()) {
            return;
        }
        if (ExtraElytraMod.MC.gameSettings.keyBindJump.pressed) {
            final EntityPlayerSP player = WMinecraft.getPlayer();
            player.motionY += 0.08;
        }
        else if (ExtraElytraMod.MC.gameSettings.keyBindSneak.pressed) {
            final EntityPlayerSP player2 = WMinecraft.getPlayer();
            player2.motionY -= 0.04;
        }
    }
    
    private void controlSpeed() {
        if (!this.speedCtrl.isChecked()) {
            return;
        }
        final EntityPlayerSP player = WMinecraft.getPlayer();
        final float yaw = (float)Math.toRadians(ExtraElytraMod.MC.player.rotationYaw);
        final Vec3d forward = new Vec3d(-MathHelper.sin(yaw) * 0.05, 0.0, MathHelper.cos(yaw) * 0.05);
        if (ExtraElytraMod.MC.gameSettings.keyBindForward.pressed) {
            final EntityPlayerSP entityPlayerSP = player;
            entityPlayerSP.motionX += forward.xCoord;
            final EntityPlayerSP entityPlayerSP2 = player;
            entityPlayerSP2.motionZ += forward.zCoord;
        }
        else if (ExtraElytraMod.MC.gameSettings.keyBindBack.pressed) {
            final EntityPlayerSP entityPlayerSP3 = player;
            entityPlayerSP3.motionX -= forward.xCoord;
            final EntityPlayerSP entityPlayerSP4 = player;
            entityPlayerSP4.motionZ -= forward.zCoord;
        }
    }
    
    private void doInstantFly() {
        if (!this.instantFly.isChecked()) {
            return;
        }
        if (this.jumpTimer <= 0) {
            this.jumpTimer = 20;
            ExtraElytraMod.MC.player.setJumping(false);
            ExtraElytraMod.MC.player.setSprinting(true);
            ExtraElytraMod.MC.player.jump();
        }
        this.sendStartStopPacket();
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            default: {
                this.speedCtrl.unlock();
                this.heightCtrl.unlock();
                break;
            }
            case GHOST_MODE: {
                this.speedCtrl.lock(() -> false);
                this.heightCtrl.lock(() -> false);
                break;
            }
        }
    }
}
