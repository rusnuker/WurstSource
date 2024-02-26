// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemAir;
import net.minecraft.item.Item;

public final class WItem
{
    public static boolean isNullOrEmpty(final Item item) {
        return item == null || item instanceof ItemAir;
    }
    
    public static boolean isNullOrEmpty(final ItemStack stack) {
        return stack == null || stack.func_190926_b();
    }
    
    public static int getArmorType(final ItemArmor armor) {
        return armor.armorType.ordinal() - 2;
    }
    
    public static float getArmorToughness(final ItemArmor armor) {
        return armor.toughness;
    }
    
    public static boolean isThrowable(final ItemStack stack) {
        final Item item = stack.getItem();
        return item instanceof ItemBow || item instanceof ItemSnowball || item instanceof ItemEgg || item instanceof ItemEnderPearl || item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemFishingRod;
    }
    
    public static boolean isPotion(final ItemStack stack) {
        return (stack != null && stack.getItem() instanceof ItemPotion) || stack.getItem() instanceof ItemSplashPotion;
    }
    
    public static Item getFromRegistry(final ResourceLocation location) {
        return Item.REGISTRY.getObject(location);
    }
    
    public static int getStackSize(final ItemStack stack) {
        return stack.func_190916_E();
    }
    
    public static float getDestroySpeed(final ItemStack stack, final IBlockState state) {
        return isNullOrEmpty(stack) ? 1.0f : stack.getStrVsBlock(state);
    }
}
