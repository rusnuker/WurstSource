// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/gm")
public final class GmCmd extends Command
{
    public GmCmd() {
        super("gm", "Types \"/gamemode <args>\".\nUseful for servers that don't support /gm.", new String[] { "<gamemode>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 1) {
            throw new CmdSyntaxError();
        }
        GmCmd.MC.player.sendChatMessage("/gamemode " + args[0]);
    }
}
