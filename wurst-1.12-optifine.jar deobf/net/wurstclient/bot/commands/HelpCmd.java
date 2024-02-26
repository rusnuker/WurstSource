// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot.commands;

import java.util.Iterator;
import net.wurstclient.util.MathUtils;
import net.wurstclient.bot.WurstBot;

@Info(help = "Shows the command list or the help for a command.", name = "help", syntax = { "[<page>]", "[<command>]" })
public class HelpCmd extends Command
{
    @Override
    public void execute(final String[] args) throws CmdError {
        if (args.length == 0) {
            this.execute(new String[] { "1" });
            return;
        }
        final int pages = (int)Math.ceil(WurstBot.getBot().getCommandManager().countCommands() / 8.0);
        if (MathUtils.isInteger(args[0])) {
            final int page = Integer.valueOf(args[0]);
            if (page > pages || page < 1) {
                this.syntaxError("Invalid page: " + page);
            }
            System.out.println("Available commands: " + WurstBot.getBot().getCommandManager().countCommands());
            System.out.println("Command list (page " + page + "/" + pages + "):");
            final Iterator<Command> itr = WurstBot.getBot().getCommandManager().getAllCommands().iterator();
            int i = 0;
            while (itr.hasNext()) {
                final Command cmd = itr.next();
                if (i >= (page - 1) * 8 && i < (page - 1) * 8 + 8) {
                    System.out.println(cmd.getName());
                }
                ++i;
            }
        }
        else {
            final Command cmd2 = WurstBot.getBot().getCommandManager().getCommandByName(args[0]);
            if (cmd2 != null) {
                System.out.println("Available help for \"" + args[0] + "\":");
                cmd2.printHelp();
                cmd2.printSyntax();
            }
            else {
                this.syntaxError();
            }
        }
    }
}
