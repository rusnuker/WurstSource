// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "BoatFlight", "boat fly", "boat flight" })
@HelpPage("Mods/BoatFly")
@Bypasses(ghostMode = false, latestNCP = false)
public final class BoatFlyMod extends Hack implements UpdateListener
{
    public BoatFlyMod() {
        super("BoatFly", "Allows you to fly with boats");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        BoatFlyMod.WURST.events.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        BoatFlyMod.WURST.events.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (!WMinecraft.getPlayer().isRiding()) {
            return;
        }
        WMinecraft.getPlayer().getRidingEntity().motionY = (BoatFlyMod.MC.gameSettings.keyBindJump.pressed ? 0.3 : 0.0);
    }
}
