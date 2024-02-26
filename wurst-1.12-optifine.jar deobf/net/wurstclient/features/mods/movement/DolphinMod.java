// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.Category;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "AutoSwim", "auto swim" })
public final class DolphinMod extends Hack implements UpdateListener
{
    public DolphinMod() {
        super("Dolphin", "Makes you bob up in water automatically.\n(just like a dolphin)");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        DolphinMod.EVENTS.add(UpdateListener.class, this);
        DolphinMod.WURST.hax.fishMod.setEnabled(false);
    }
    
    @Override
    public void onDisable() {
        DolphinMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        final EntityPlayerSP player = DolphinMod.MC.player;
        if (!player.isInWater() || player.isSneaking()) {
            return;
        }
        final EntityPlayerSP entityPlayerSP = player;
        entityPlayerSP.motionY += 0.04;
    }
}
