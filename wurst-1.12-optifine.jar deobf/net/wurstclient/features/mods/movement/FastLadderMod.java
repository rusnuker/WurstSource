// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "FastClimb", "fast ladder", "fast climb" })
@Bypasses(ghostMode = false, latestNCP = false)
public final class FastLadderMod extends Hack implements UpdateListener
{
    public FastLadderMod() {
        super("FastLadder", "Allows you to climb up ladders faster.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        FastLadderMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        FastLadderMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (!FastLadderMod.MC.player.isOnLadder() || !FastLadderMod.MC.player.isCollidedHorizontally) {
            return;
        }
        if (FastLadderMod.MC.player.movementInput.moveForward == 0.0f && FastLadderMod.MC.player.movementInput.moveStrafe == 0.0f) {
            return;
        }
        FastLadderMod.MC.player.motionY = 0.2872;
    }
}
