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
import net.wurstclient.hooks.ServerHook;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/ip")
public final class IpCmd extends Command
{
    public IpCmd() {
        super("ip", "Shows the IP of the server you are currently playing on or copies it to the clipboard.", new String[] { "[copy]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            ChatUtils.message("IP: " + ServerHook.getCurrentServerIP());
        }
        else {
            if (!args[0].toLowerCase().equals("copy")) {
                throw new CmdSyntaxError();
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(ServerHook.getCurrentServerIP()), null);
            ChatUtils.message("IP copied to clipboard.");
        }
    }
    
    @Override
    public String getPrimaryAction() {
        return "Get IP";
    }
    
    @Override
    public void doPrimaryAction() {
        IpCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".ip", true));
    }
}
