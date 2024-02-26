// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.hooks.ServerHook;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/sv")
public final class SvCmd extends Command
{
    public SvCmd() {
        super("sv", "Shows the version of the server you are currently playing on.", new String[0]);
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 0) {
            throw new CmdSyntaxError();
        }
        if (SvCmd.MC.isSingleplayer()) {
            throw new CmdError("Can't check server version in singleplayer.");
        }
        ChatUtils.message("Server version: " + ServerHook.getLastServerData().gameVersion);
    }
    
    @Override
    public String getPrimaryAction() {
        return "Get Server Version";
    }
    
    @Override
    public void doPrimaryAction() {
        SvCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".sv", true));
    }
}
