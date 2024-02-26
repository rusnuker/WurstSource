// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.minecraft.util.math.Box;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.special_features.YesCheatSpf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "FlyHack", "fly hack", "flying" })
@Bypasses(ghostMode = false, latestNCP = false)
public final class FlightMod extends Hack implements UpdateListener
{
    private final ModeSetting mode;
    public final SliderSetting speed;
    private final CheckboxSetting flightKickBypass;
    private double flyHeight;
    private double startY;
    
    public FlightMod() {
        super("Flight", "Allows you to you fly.\n\n§c§lWARNING:§r You will take fall damage if you don't use NoFall.");
        this.mode = new ModeSetting("Mode", new String[] { "Normal", "Mineplex", "Old NCP" }, 0) {
            @Override
            public void update() {
                if (this.getSelected() > 0) {
                    FlightMod.this.speed.setDisabled(true);
                    if (FlightMod.this.flightKickBypass != null) {
                        FlightMod.this.flightKickBypass.lock(() -> false);
                    }
                }
                else {
                    FlightMod.this.speed.setDisabled(false);
                    if (FlightMod.this.flightKickBypass != null) {
                        FlightMod.this.flightKickBypass.unlock();
                    }
                }
            }
        };
        this.speed = new SliderSetting("Speed", 1.0, 0.05, 5.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.flightKickBypass = null;
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public String getRenderName() {
        if (this.flightKickBypass == null || !this.flightKickBypass.isChecked()) {
            return this.getName();
        }
        return String.valueOf(this.getName()) + "[Kick: " + ((this.flyHeight <= 300.0) ? "Safe" : "Unsafe") + "]";
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.mode);
        this.addSetting(this.speed);
        if (this.flightKickBypass != null) {
            this.addSetting(this.flightKickBypass);
        }
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { FlightMod.WURST.hax.noFallMod, FlightMod.WURST.hax.jetpackMod, FlightMod.WURST.hax.glideMod, FlightMod.WURST.special.yesCheatSpf };
    }
    
    @Override
    public void onEnable() {
        FlightMod.WURST.hax.jetpackMod.setEnabled(false);
        if (this.mode.getSelected() > 0) {
            final double startX = FlightMod.MC.player.posX;
            this.startY = FlightMod.MC.player.posY;
            final double startZ = FlightMod.MC.player.posZ;
            for (int i = 0; i < 4; ++i) {
                WConnection.sendPacket(new CPacketPlayer.Position(startX, this.startY + 1.01, startZ, false));
                WConnection.sendPacket(new CPacketPlayer.Position(startX, this.startY, startZ, false));
            }
            FlightMod.MC.player.jump();
        }
        FlightMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        FlightMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        switch (this.mode.getSelected()) {
            case 0: {
                FlightMod.MC.player.abilities.isFlying = false;
                FlightMod.MC.player.motionX = 0.0;
                FlightMod.MC.player.motionY = 0.0;
                FlightMod.MC.player.motionZ = 0.0;
                FlightMod.MC.player.jumpMovementFactor = this.speed.getValueF();
                if (FlightMod.MC.gameSettings.keyBindJump.pressed) {
                    final EntityPlayerSP player = FlightMod.MC.player;
                    player.motionY += this.speed.getValue();
                }
                if (FlightMod.MC.gameSettings.keyBindSneak.pressed) {
                    final EntityPlayerSP player2 = FlightMod.MC.player;
                    player2.motionY -= this.speed.getValue();
                }
                if (this.flightKickBypass == null || !this.flightKickBypass.isChecked()) {
                    break;
                }
                this.updateMS();
                this.updateFlyHeight();
                WConnection.sendPacket(new CPacketPlayer(true));
                if ((this.flyHeight <= 290.0 && this.hasTimePassedM(500L)) || (this.flyHeight > 290.0 && this.hasTimePassedM(100L))) {
                    this.goToGround();
                    this.updateLastMS();
                    break;
                }
                break;
            }
            case 1: {
                this.updateMS();
                if (!FlightMod.MC.player.onGround) {
                    if (FlightMod.MC.gameSettings.keyBindJump.pressed && this.hasTimePassedS(2.0f)) {
                        FlightMod.MC.player.setPosition(FlightMod.MC.player.posX, FlightMod.MC.player.posY + 8.0, FlightMod.MC.player.posZ);
                        this.updateLastMS();
                    }
                    else if (FlightMod.MC.gameSettings.keyBindSneak.pressed) {
                        FlightMod.MC.player.motionY = -0.4;
                    }
                    else {
                        FlightMod.MC.player.motionY = -0.02;
                    }
                }
                FlightMod.MC.player.jumpMovementFactor = 0.04f;
                break;
            }
            case 2: {
                if (FlightMod.MC.player.onGround) {
                    break;
                }
                if (FlightMod.MC.gameSettings.keyBindJump.pressed && FlightMod.MC.player.posY < this.startY - 1.0) {
                    FlightMod.MC.player.motionY = 0.2;
                    break;
                }
                FlightMod.MC.player.motionY = -0.02;
                break;
            }
        }
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            default: {
                this.mode.unlock();
                break;
            }
            case MINEPLEX: {
                this.mode.lock(1);
                break;
            }
            case ANTICHEAT:
            case OLDER_NCP: {
                this.mode.lock(2);
                break;
            }
        }
    }
    
    private void updateFlyHeight() {
        double h = 1.0;
        final Box box = FlightMod.MC.player.getEntityBoundingBox().expand(0.0625, 0.0625, 0.0625);
        this.flyHeight = 0.0;
        while (this.flyHeight < FlightMod.MC.player.posY) {
            final Box nextBox = box.offset(0.0, -this.flyHeight, 0.0);
            if (WMinecraft.getWorld().checkBlockCollision(nextBox)) {
                if (h < 0.0625) {
                    break;
                }
                this.flyHeight -= h;
                h /= 2.0;
            }
            this.flyHeight += h;
        }
    }
    
    private void goToGround() {
        if (this.flyHeight > 300.0) {
            return;
        }
        final double minY = FlightMod.MC.player.posY - this.flyHeight;
        if (minY <= 0.0) {
            return;
        }
        double y = FlightMod.MC.player.posY;
        while (y > minY) {
            y -= 8.0;
            if (y < minY) {
                y = minY;
            }
            final CPacketPlayer.Position packet = new CPacketPlayer.Position(FlightMod.MC.player.posX, y, FlightMod.MC.player.posZ, true);
            WConnection.sendPacket(packet);
        }
        y = minY;
        while (y < FlightMod.MC.player.posY) {
            y += 8.0;
            if (y > FlightMod.MC.player.posY) {
                y = FlightMod.MC.player.posY;
            }
            final CPacketPlayer.Position packet = new CPacketPlayer.Position(FlightMod.MC.player.posX, y, FlightMod.MC.player.posZ, true);
            WConnection.sendPacket(packet);
        }
    }
}
