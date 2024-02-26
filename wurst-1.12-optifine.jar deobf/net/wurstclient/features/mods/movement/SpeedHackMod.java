// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "speed hack" })
@Bypasses(ghostMode = false, latestNCP = false)
public final class SpeedHackMod extends Hack implements UpdateListener
{
    public SpeedHackMod() {
        super("SpeedHack", "Allows you to run roughly 2.5x faster than you would by sprinting and jumping.\n\n§6§lNotice:§r This mod was patched in NoCheat+ version 3.13.2. It will only bypass older versions\nof NoCheat+. Type §l/ncp version§r to check the NoCheat+ version of a server.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        SpeedHackMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        SpeedHackMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (SpeedHackMod.MC.player.isSneaking() || (SpeedHackMod.MC.player.moveForward == 0.0f && SpeedHackMod.MC.player.moveStrafing == 0.0f)) {
            return;
        }
        if (SpeedHackMod.MC.player.moveForward > 0.0f && !SpeedHackMod.MC.player.isCollidedHorizontally) {
            SpeedHackMod.MC.player.setSprinting(true);
        }
        if (SpeedHackMod.MC.player.onGround) {
            final EntityPlayerSP player = SpeedHackMod.MC.player;
            player.motionY += 0.1;
            final EntityPlayerSP player2 = SpeedHackMod.MC.player;
            player2.motionX *= 1.8;
            final EntityPlayerSP player3 = SpeedHackMod.MC.player;
            player3.motionZ *= 1.8;
            final double currentSpeed = Math.sqrt(Math.pow(SpeedHackMod.MC.player.motionX, 2.0) + Math.pow(SpeedHackMod.MC.player.motionZ, 2.0));
            final double maxSpeed = 0.6600000262260437;
            if (currentSpeed > maxSpeed) {
                SpeedHackMod.MC.player.motionX = SpeedHackMod.MC.player.motionX / currentSpeed * maxSpeed;
                SpeedHackMod.MC.player.motionZ = SpeedHackMod.MC.player.motionZ / currentSpeed * maxSpeed;
            }
        }
    }
}
