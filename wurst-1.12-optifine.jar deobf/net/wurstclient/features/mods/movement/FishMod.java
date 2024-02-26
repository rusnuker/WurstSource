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
public final class FishMod extends Hack implements UpdateListener
{
    public FishMod() {
        super("Fish", "Disables underwater gravity\nso you can swim like a fish.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        FishMod.EVENTS.add(UpdateListener.class, this);
        FishMod.WURST.hax.dolphinMod.setEnabled(false);
    }
    
    @Override
    public void onDisable() {
        FishMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        final EntityPlayerSP player = FishMod.MC.player;
        if (!player.isInWater() || player.isSneaking()) {
            return;
        }
        final EntityPlayerSP entityPlayerSP = player;
        entityPlayerSP.motionY += 0.02;
    }
}
