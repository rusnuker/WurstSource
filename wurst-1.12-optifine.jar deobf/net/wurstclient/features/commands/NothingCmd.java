// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/nothing")
public final class NothingCmd extends Command
{
    public NothingCmd() {
        super("nothing", "Does nothing. Useful for scripting.", new String[0]);
    }
    
    @Override
    public void call(final String[] args) throws CmdError {
    }
    
    @Override
    public String getPrimaryAction() {
        return "Do Nothing";
    }
    
    @Override
    public void doPrimaryAction() {
        NothingCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".nothing", true));
    }
}
