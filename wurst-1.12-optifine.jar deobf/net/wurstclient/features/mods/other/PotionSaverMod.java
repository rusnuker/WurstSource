// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "potion saver" })
@Bypasses
public final class PotionSaverMod extends Hack
{
    public PotionSaverMod() {
        super("PotionSaver", "Freezes all potion effects while you are standing still.");
        this.setCategory(Category.OTHER);
    }
    
    public boolean isFrozen() {
        return this.isActive() && !PotionSaverMod.MC.player.getActivePotionEffects().isEmpty() && PotionSaverMod.MC.player.motionX == 0.0 && PotionSaverMod.MC.player.motionZ == 0.0;
    }
}
