// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.minecraft.util.math.Box;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.settings.Setting;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "jet pack" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false, antiCheat = false, mineplex = false)
public final class JetpackMod extends Hack implements UpdateListener
{
    private final CheckboxSetting flightKickBypass;
    private double flyHeight;
    
    public JetpackMod() {
        super("Jetpack", "Allows you to fly as if you had a jetpack.\n\n§c§lWARNING:§r You will take fall damage if you don't use NoFall.");
        this.flightKickBypass = null;
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { JetpackMod.WURST.hax.noFallMod };
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
        if (this.flightKickBypass != null) {
            this.addSetting(this.flightKickBypass);
        }
    }
    
    @Override
    public void onEnable() {
        JetpackMod.WURST.hax.flightMod.setEnabled(false);
        JetpackMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        JetpackMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (JetpackMod.MC.gameSettings.keyBindJump.pressed) {
            JetpackMod.MC.player.jump();
        }
        if (this.flightKickBypass != null && this.flightKickBypass.isChecked()) {
            this.updateMS();
            this.updateFlyHeight();
            WConnection.sendPacket(new CPacketPlayer(true));
            if ((this.flyHeight <= 290.0 && this.hasTimePassedM(500L)) || (this.flyHeight > 290.0 && this.hasTimePassedM(100L))) {
                this.goToGround();
                this.updateLastMS();
            }
        }
    }
    
    private void updateFlyHeight() {
        double h = 1.0;
        final Box box = JetpackMod.MC.player.getEntityBoundingBox().expand(0.0625, 0.0625, 0.0625);
        this.flyHeight = 0.0;
        while (this.flyHeight < JetpackMod.MC.player.posY) {
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
        final double minY = JetpackMod.MC.player.posY - this.flyHeight;
        if (minY <= 0.0) {
            return;
        }
        double y = JetpackMod.MC.player.posY;
        while (y > minY) {
            y -= 8.0;
            if (y < minY) {
                y = minY;
            }
            final CPacketPlayer.Position packet = new CPacketPlayer.Position(JetpackMod.MC.player.posX, y, JetpackMod.MC.player.posZ, true);
            WConnection.sendPacket(packet);
        }
        y = minY;
        while (y < JetpackMod.MC.player.posY) {
            y += 8.0;
            if (y > JetpackMod.MC.player.posY) {
                y = JetpackMod.MC.player.posY;
            }
            final CPacketPlayer.Position packet = new CPacketPlayer.Position(JetpackMod.MC.player.posX, y, JetpackMod.MC.player.posZ, true);
            WConnection.sendPacket(packet);
        }
    }
}
