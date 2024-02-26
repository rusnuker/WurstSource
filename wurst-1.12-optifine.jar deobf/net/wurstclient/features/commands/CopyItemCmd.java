// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.entity.Entity;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.utils.InventoryUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/copyitem")
public final class CopyItemCmd extends Command
{
    public CopyItemCmd() {
        super("copyitem", "Allows you to copy items that other people are holding\nor wearing. Requires creative mode.", new String[] { "<player> (hand|head|chest|legs|feet)" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError();
        }
        if (!CopyItemCmd.MC.player.abilities.creativeMode) {
            throw new CmdError("Creative mode only.");
        }
        ItemStack item = null;
    Label_0304:
        for (final Object entity : WMinecraft.getWorld().loadedEntityList) {
            if (entity instanceof EntityOtherPlayerMP) {
                final EntityOtherPlayerMP player = (EntityOtherPlayerMP)entity;
                if (player.getName().equalsIgnoreCase(args[0])) {
                    final String lowerCase;
                    switch (lowerCase = args[1].toLowerCase()) {
                        case "feet": {
                            item = player.inventory.armorItemInSlot(0);
                            break Label_0304;
                        }
                        case "hand": {
                            item = player.inventory.getCurrentItem();
                            break Label_0304;
                        }
                        case "head": {
                            item = player.inventory.armorItemInSlot(3);
                            break Label_0304;
                        }
                        case "legs": {
                            item = player.inventory.armorItemInSlot(1);
                            break Label_0304;
                        }
                        case "chest": {
                            item = player.inventory.armorItemInSlot(2);
                            break Label_0304;
                        }
                        default:
                            break;
                    }
                    throw new CmdSyntaxError();
                }
                continue;
            }
        }
        if (item == null) {
            throw new CmdError("Player \"" + args[0] + "\" could not be found.");
        }
        if (InventoryUtils.placeStackInHotbar(item)) {
            ChatUtils.message("Item copied.");
            return;
        }
        throw new CmdError("Please clear a slot in your hotbar.");
    }
}
