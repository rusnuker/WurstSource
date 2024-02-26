// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.wurstclient.utils.InventoryUtils;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "troll potion" })
@Bypasses
public final class TrollPotionMod extends Hack
{
    public TrollPotionMod() {
        super("TrollPotion", "Generates a potion with many annoying effects on it.");
        this.setCategory(Category.ITEMS);
    }
    
    @Override
    public void onEnable() {
        if (!TrollPotionMod.MC.player.abilities.creativeMode) {
            ChatUtils.error("Creative mode only.");
            this.setEnabled(false);
            return;
        }
        final ItemStack stack = InventoryUtils.createSplashPotion();
        final NBTTagList effects = new NBTTagList();
        for (int i = 1; i <= 23; ++i) {
            final NBTTagCompound effect = new NBTTagCompound();
            effect.setInteger("Amplifier", Integer.MAX_VALUE);
            effect.setInteger("Duration", Integer.MAX_VALUE);
            effect.setInteger("Id", i);
            effects.appendTag(effect);
        }
        stack.setTagInfo("CustomPotionEffects", effects);
        stack.setStackDisplayName("§rSplash Potion of Trolling");
        if (InventoryUtils.placeStackInHotbar(stack)) {
            ChatUtils.message("Potion created.");
        }
        else {
            ChatUtils.error("Please clear a slot in your hotbar.");
        }
        this.setEnabled(false);
    }
}
