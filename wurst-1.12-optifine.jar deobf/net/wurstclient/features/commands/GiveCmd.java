// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.util.ChatUtils;
import net.wurstclient.utils.InventoryUtils;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.wurstclient.util.MathUtils;
import net.wurstclient.compatibility.WItem;
import net.minecraft.util.ResourceLocation;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/give")
public final class GiveCmd extends Command
{
    public GiveCmd() {
        super("give", "Gives you an item with custom NBT data. Requires creative mode.", new String[] { "<item_name> [<amount>] [<metadata>] [<nbt>]", "<item_id> [<amount>] [<metadata>] [<nbt>]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length < 1) {
            throw new CmdSyntaxError();
        }
        if (!GiveCmd.MC.player.abilities.creativeMode) {
            throw new CmdError("Creative mode only.");
        }
        Item item = WItem.getFromRegistry(new ResourceLocation(args[0]));
        if (item == null && MathUtils.isInteger(args[0])) {
            item = Item.getItemById(Integer.parseInt(args[0]));
        }
        if (item == null) {
            throw new CmdError("Item \"" + args[0] + "\" could not be found.");
        }
        int amount = 1;
        if (args.length >= 2) {
            if (!MathUtils.isInteger(args[1])) {
                throw new CmdSyntaxError("Not a number: " + args[1]);
            }
            amount = Integer.valueOf(args[1]);
            if (amount < 1) {
                throw new CmdError("Amount cannot be less than 1.");
            }
            if (amount > item.getItemStackLimit()) {
                throw new CmdError("Amount is larger than the maximum stack size. (" + item.getItemStackLimit() + ")");
            }
        }
        int metadata = 0;
        if (args.length >= 3) {
            if (!MathUtils.isInteger(args[2])) {
                throw new CmdSyntaxError("Metadata must be a number.");
            }
            metadata = Integer.valueOf(args[2]);
        }
        String nbt = null;
        if (args.length >= 4) {
            nbt = args[3];
            for (int i = 4; i < args.length; ++i) {
                nbt = String.valueOf(nbt) + " " + args[i];
            }
        }
        final ItemStack stack = new ItemStack(item, amount, metadata);
        if (nbt != null) {
            try {
                stack.setTagCompound(JsonToNBT.getTagFromJson(nbt));
            }
            catch (final NBTException e) {
                throw new CmdSyntaxError("NBT data is invalid.");
            }
        }
        if (InventoryUtils.placeStackInHotbar(stack)) {
            ChatUtils.message("Item" + ((amount > 1) ? "s" : "") + " created.");
            return;
        }
        throw new CmdError("Please clear a slot in your hotbar.");
    }
}
