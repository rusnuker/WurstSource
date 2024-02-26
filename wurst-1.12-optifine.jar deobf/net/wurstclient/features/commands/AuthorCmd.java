// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.item.Item;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/author")
public final class AuthorCmd extends Command
{
    public AuthorCmd() {
        super("author", "Changes the held book's author.", new String[] { "<author>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            throw new CmdSyntaxError();
        }
        if (!AuthorCmd.MC.player.abilities.creativeMode) {
            throw new CmdError("Creative mode only.");
        }
        final ItemStack item = AuthorCmd.MC.player.inventory.getCurrentItem();
        if (item == null || Item.getIdFromItem(item.getItem()) != 387) {
            throw new CmdError("You are not holding a written book in your hand.");
        }
        String author = args[0];
        for (int i = 1; i < args.length; ++i) {
            author = String.valueOf(author) + " " + args[i];
        }
        item.setTagInfo("author", new NBTTagString(author));
    }
}
