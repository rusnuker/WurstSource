// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import net.wurstclient.util.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/getpos")
public final class GetPosCmd extends Command
{
    public GetPosCmd() {
        super("getpos", "Shows your current position or copies it to the clipboard.", new String[] { "[copy]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length > 1) {
            throw new CmdSyntaxError();
        }
        final BlockPos blockpos = new BlockPos(GetPosCmd.MC.player);
        final String pos = String.valueOf(blockpos.getX()) + " " + blockpos.getY() + " " + blockpos.getZ();
        if (args.length == 0) {
            ChatUtils.message("Position: " + pos);
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("copy")) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(pos), null);
            ChatUtils.message("Position copied to clipboard.");
        }
    }
    
    @Override
    public String getPrimaryAction() {
        return "Get Position";
    }
    
    @Override
    public void doPrimaryAction() {
        GetPosCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".getpos", true));
    }
}
