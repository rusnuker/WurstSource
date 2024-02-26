// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Enchantments;
import net.minecraft.enchantment.Enchantment;

public final class WEnchantments
{
    public static final Enchantment PROTECTION;
    public static final Enchantment EFFICIENCY;
    public static final Enchantment SILK_TOUCH;
    public static final Enchantment LUCK_OF_THE_SEA;
    public static final Enchantment LURE;
    public static final Enchantment UNBREAKING;
    public static final Enchantment MENDING;
    
    static {
        PROTECTION = Enchantments.PROTECTION;
        EFFICIENCY = Enchantments.EFFICIENCY;
        SILK_TOUCH = Enchantments.SILK_TOUCH;
        LUCK_OF_THE_SEA = Enchantments.LUCK_OF_THE_SEA;
        LURE = Enchantments.LURE;
        UNBREAKING = Enchantments.UNBREAKING;
        MENDING = Enchantments.MENDING;
    }
    
    public static int getEnchantmentLevel(final Enchantment enchantment, final ItemStack stack) {
        if (enchantment == null) {
            return 0;
        }
        return EnchantmentHelper.getEnchantmentLevel(enchantment, stack);
    }
    
    public static boolean hasVanishingCurse(final ItemStack stack) {
        return EnchantmentHelper.func_190939_c(stack);
    }
}
