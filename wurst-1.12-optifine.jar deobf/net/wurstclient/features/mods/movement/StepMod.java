// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.features.special_features.YesCheatSpf;
import java.util.Iterator;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Box;
import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false)
public final class StepMod extends Hack implements UpdateListener
{
    private final ModeSetting mode;
    private final SliderSetting height;
    
    public StepMod() {
        super("Step", "Allows you to step up full blocks.");
        this.mode = new ModeSetting("Mode", "§lSimple§r mode can step up multiple blocks (enables Height slider).\n§lLegit§r mode can bypass NoCheat+.", new String[] { "Simple", "Legit" }, 1) {
            @Override
            public void update() {
                StepMod.this.height.setDisabled(this.getSelected() == 1);
            }
        };
        this.height = new SliderSetting("Height", 1.0, 1.0, 100.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.mode);
        this.addSetting(this.height);
    }
    
    @Override
    public void onEnable() {
        StepMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        StepMod.EVENTS.remove(UpdateListener.class, this);
        StepMod.MC.player.stepHeight = 0.5f;
    }
    
    @Override
    public void onUpdate() {
        if (this.mode.getSelected() == 0) {
            StepMod.MC.player.stepHeight = this.height.getValueF();
            return;
        }
        final EntityPlayerSP player = StepMod.MC.player;
        player.stepHeight = 0.5f;
        if (!player.isCollidedHorizontally) {
            return;
        }
        if (!player.onGround || player.isOnLadder() || player.isInWater() || player.isInLava()) {
            return;
        }
        if (player.movementInput.moveForward == 0.0f && player.movementInput.moveStrafe == 0.0f) {
            return;
        }
        if (player.movementInput.jump) {
            return;
        }
        final Box box = player.getEntityBoundingBox().offset(0.0, 0.05, 0.0).expandXyz(0.05);
        if (!WMinecraft.getWorld().getCollisionBoxes(player, box.offset(0.0, 1.0, 0.0)).isEmpty()) {
            return;
        }
        double stepHeight = -1.0;
        for (final Box bb : WMinecraft.getWorld().getCollisionBoxes(player, box)) {
            if (bb.maxY > stepHeight) {
                stepHeight = bb.maxY;
            }
        }
        stepHeight -= player.posY;
        if (stepHeight < 0.0 || stepHeight > 1.0) {
            return;
        }
        WConnection.sendPacket(new CPacketPlayer.Position(player.posX, player.posY + 0.42 * stepHeight, player.posZ, player.onGround));
        WConnection.sendPacket(new CPacketPlayer.Position(player.posX, player.posY + 0.753 * stepHeight, player.posZ, player.onGround));
        player.setPosition(player.posX, player.posY + 1.0 * stepHeight, player.posZ);
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            default: {
                this.mode.unlock();
                break;
            }
            case ANTICHEAT:
            case OLDER_NCP:
            case LATEST_NCP: {
                this.mode.lock(1);
                break;
            }
        }
    }
    
    public boolean isAutoJumpAllowed() {
        return !this.isActive() && !StepMod.WURST.commands.goToCmd.isActive();
    }
}
