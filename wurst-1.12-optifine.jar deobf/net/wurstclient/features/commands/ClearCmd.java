// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.compatibility.WChat;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/clear")
public final class ClearCmd extends Command
{
    public ClearCmd() {
        super("clear", "Clears the chat completely.", new String[0]);
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            WChat.clearMessages();
            return;
        }
        throw new CmdSyntaxError();
    }
}
