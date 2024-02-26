// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.item.ItemStack;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/rename")
public final class RenameCmd extends Command
{
    public RenameCmd() {
        super("rename", "Renames the item in your hand. Use $ for colors, use $$ for $.", new String[] { "<new_name>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (!RenameCmd.MC.player.abilities.creativeMode) {
            throw new CmdError("Creative mode only.");
        }
        if (args.length == 0) {
            throw new CmdSyntaxError();
        }
        String message = args[0];
        for (int i = 1; i < args.length; ++i) {
            message = String.valueOf(message) + " " + args[i];
        }
        message = message.replace("$", "§").replace("§§", "$");
        final ItemStack item = RenameCmd.MC.player.inventory.getCurrentItem();
        if (item == null) {
            throw new CmdError("There is no item in your hand.");
        }
        item.setStackDisplayName(message);
        ChatUtils.message("Renamed item to \"" + message + "§r\".");
    }
}
