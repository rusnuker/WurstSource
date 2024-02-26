// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.utils;

import java.util.Iterator;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potion;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.item.ItemStack;

public class InventoryUtils
{
    public static boolean placeStackInHotbar(final ItemStack stack) {
        for (int i = 0; i < 9; ++i) {
            if (isSlotEmpty(i)) {
                WConnection.sendPacket(new CPacketCreativeInventoryAction(36 + i, stack));
                return true;
            }
        }
        return false;
    }
    
    public static void placeStackInArmor(final int armorSlot, final ItemStack stack) {
        WMinecraft.getPlayer().inventory.armorInventory.set(armorSlot, stack);
    }
    
    public static boolean isSlotEmpty(final int slot) {
        return WItem.isNullOrEmpty(WMinecraft.getPlayer().inventory.getInvStack(slot));
    }
    
    public static boolean isSplashPotion(final ItemStack stack) {
        return stack.getItem() == Items.SPLASH_POTION;
    }
    
    public static ItemStack createSplashPotion() {
        return new ItemStack(Items.SPLASH_POTION);
    }
    
    public static float getStrVsBlock(final ItemStack stack, final BlockPos pos) {
        return stack.getStrVsBlock(WBlock.getState(pos));
    }
    
    public static boolean hasEffect(final ItemStack stack, final Potion potion) {
        for (final PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
            if (effect.getPotion() == potion) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean checkHeldItem(final ItemValidator validator) {
        final ItemStack stack = WMinecraft.getPlayer().inventory.getCurrentItem();
        return !WItem.isNullOrEmpty(stack) && validator.isValid(stack.getItem());
    }
    
    public interface ItemValidator
    {
        boolean isValid(final Item p0);
    }
}
