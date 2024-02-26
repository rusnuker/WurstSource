// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.PotionEffect;
import java.util.List;
import net.minecraft.item.ItemStack;

public final class WPotion
{
    public static List<PotionEffect> getEffectsFromStack(final ItemStack stack) {
        return PotionUtils.getEffectsFromStack(stack);
    }
    
    public static int getIdFromEffect(final PotionEffect effect) {
        return Potion.getIdFromPotion(effect.getPotion());
    }
    
    public static int getIdFromPotion(final Potion potion) {
        return Potion.getIdFromPotion(potion);
    }
    
    public static int getIdFromResourceLocation(final String location) {
        return Potion.getIdFromPotion(Potion.getPotionFromResourceLocation(location));
    }
}
