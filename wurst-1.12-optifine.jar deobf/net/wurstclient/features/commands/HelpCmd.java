// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import java.util.Iterator;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.MathUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.DontBlock;
import net.wurstclient.features.Command;

@DontBlock
@HelpPage("Commands/help")
public final class HelpCmd extends Command
{
    public HelpCmd() {
        super("help", "Shows the command list or the help for a command.", new String[] { "[<page>]", "[<command>]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            this.call(new String[] { "1" });
            return;
        }
        final int pages = (int)Math.ceil(HelpCmd.WURST.commands.countCommands() / 8.0);
        if (MathUtils.isInteger(args[0])) {
            final int page = Integer.valueOf(args[0]);
            if (page > pages || page < 1) {
                throw new CmdSyntaxError("Invalid page: " + page);
            }
            ChatUtils.message("Available commands: " + HelpCmd.WURST.commands.countCommands());
            ChatUtils.message("Command list (page " + page + "/" + pages + "):");
            final Iterator<Command> itr = HelpCmd.WURST.commands.getAllCommands().iterator();
            int i = 0;
            while (itr.hasNext()) {
                final Command cmd = itr.next();
                if (i >= (page - 1) * 8 && i < (page - 1) * 8 + 8) {
                    ChatUtils.message(cmd.getCmdName());
                }
                ++i;
            }
        }
        else {
            final Command cmd2 = HelpCmd.WURST.commands.getCommandByName(args[0]);
            if (cmd2 == null) {
                throw new CmdError("Command \"" + args[0] + "\" could not be found.");
            }
            ChatUtils.message("Available help for ." + args[0] + ":");
            cmd2.printHelp();
            cmd2.printSyntax();
        }
    }
}
