// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false, antiCheat = false)
public final class SpiderMod extends Hack implements UpdateListener
{
    public SpiderMod() {
        super("Spider", "Allows you to climb up walls like a spider.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        SpiderMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        SpiderMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (SpiderMod.MC.player.isCollidedHorizontally) {
            SpiderMod.MC.player.motionY = 0.2;
        }
    }
}
