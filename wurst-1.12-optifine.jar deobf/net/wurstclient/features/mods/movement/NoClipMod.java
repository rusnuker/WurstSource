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

@SearchTags({ "no clip" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class NoClipMod extends Hack implements UpdateListener
{
    public NoClipMod() {
        super("NoClip", "Allows you to freely move through blocks.\nA block (e.g. sand) must fall on your head to activate it.\n\n§c§lWARNING:§r You will take damage while moving through blocks!");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        NoClipMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        NoClipMod.EVENTS.remove(UpdateListener.class, this);
        NoClipMod.MC.player.noClip = false;
    }
    
    @Override
    public void onUpdate() {
        NoClipMod.MC.player.noClip = true;
        NoClipMod.MC.player.fallDistance = 0.0f;
        NoClipMod.MC.player.onGround = false;
        NoClipMod.MC.player.abilities.isFlying = false;
        NoClipMod.MC.player.motionX = 0.0;
        NoClipMod.MC.player.motionY = 0.0;
        NoClipMod.MC.player.motionZ = 0.0;
        final float speed = 0.2f;
        NoClipMod.MC.player.jumpMovementFactor = speed;
        if (NoClipMod.MC.gameSettings.keyBindJump.pressed) {
            final EntityPlayerSP player = NoClipMod.MC.player;
            player.motionY += speed;
        }
        if (NoClipMod.MC.gameSettings.keyBindSneak.pressed) {
            final EntityPlayerSP player2 = NoClipMod.MC.player;
            player2.motionY -= speed;
        }
    }
}
