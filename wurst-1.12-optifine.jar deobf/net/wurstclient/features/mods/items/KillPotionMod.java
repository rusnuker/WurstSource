// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.wurstclient.utils.InventoryUtils;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "KillerPotion", "kill potion", "killer potion" })
@Bypasses
public final class KillPotionMod extends Hack
{
    public KillPotionMod() {
        super("KillPotion", "Generates a potion that can kill anything, even players in Creative mode.\nRequires Creative mode.");
        this.setCategory(Category.ITEMS);
    }
    
    @Override
    public void onEnable() {
        if (!KillPotionMod.MC.player.abilities.creativeMode) {
            ChatUtils.error("Creative mode only.");
            this.setEnabled(false);
            return;
        }
        final ItemStack stack = InventoryUtils.createSplashPotion();
        final NBTTagCompound effect = new NBTTagCompound();
        effect.setInteger("Amplifier", 125);
        effect.setInteger("Duration", 2000);
        effect.setInteger("Id", 6);
        final NBTTagList effects = new NBTTagList();
        effects.appendTag(effect);
        stack.setTagInfo("CustomPotionEffects", effects);
        stack.setStackDisplayName("§rSplash Potion of §4§lDEATH");
        if (InventoryUtils.placeStackInHotbar(stack)) {
            ChatUtils.message("Potion created.");
        }
        else {
            ChatUtils.error("Please clear a slot in your hotbar.");
        }
        this.setEnabled(false);
    }
}
