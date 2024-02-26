// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.client.entity.EntityPlayerSP;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Command;

@SearchTags({ "view nbt", "NBTViewer", "nbt viewer" })
public final class ViewNbtCmd extends Command
{
    public ViewNbtCmd() {
        super("viewnbt", "Shows you the NBT data of an item.", new String[] { "[copy]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        final EntityPlayerSP player = ViewNbtCmd.MC.player;
        final ItemStack stack = player.inventory.getCurrentItem();
        if (WItem.isNullOrEmpty(stack)) {
            throw new CmdError("You must hold an item in your main hand.");
        }
        final NBTTagCompound tag = stack.getTagCompound();
        final String nbt = (tag == null) ? "" : tag.toString();
        final String lowerCase;
        switch (lowerCase = String.join(" ", (CharSequence[])args).toLowerCase()) {
            case "": {
                ChatUtils.message("NBT: " + nbt);
                return;
            }
            case "copy": {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(nbt), null);
                ChatUtils.message("NBT data copied to clipboard.");
                return;
            }
            default:
                break;
        }
        throw new CmdSyntaxError();
    }
}
