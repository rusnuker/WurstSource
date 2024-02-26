// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.WurstClient;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses
public final class ParkourMod extends Hack implements UpdateListener
{
    public ParkourMod() {
        super("Parkour", "Makes you jump automatically when reaching the edge of a block.\nUseful for parkours, Jump'n'Runs, etc.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        WurstClient.INSTANCE.events.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        WurstClient.INSTANCE.events.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (ParkourMod.MC.player.onGround && !ParkourMod.MC.player.isSneaking() && !ParkourMod.MC.gameSettings.keyBindSneak.pressed && !ParkourMod.MC.gameSettings.keyBindJump.pressed && WMinecraft.getWorld().getCollisionBoxes(ParkourMod.MC.player, ParkourMod.MC.player.getEntityBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)).isEmpty()) {
            ParkourMod.MC.player.jump();
        }
    }
}
