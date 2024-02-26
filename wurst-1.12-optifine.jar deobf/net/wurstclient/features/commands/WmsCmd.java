// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/wms")
public final class WmsCmd extends Command
{
    public WmsCmd() {
        super("wms", "Enables/disables Wurst messages or sends a message.", new String[] { "(on | off)", "echo <message>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            throw new CmdSyntaxError();
        }
        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
            ChatUtils.setEnabled(args[0].equalsIgnoreCase("on"));
        }
        else {
            if (!args[0].equalsIgnoreCase("echo") || args.length < 2) {
                throw new CmdSyntaxError();
            }
            String message = args[1];
            for (int i = 2; i < args.length; ++i) {
                message = String.valueOf(message) + " " + args[i];
            }
            ChatUtils.cmd(message);
        }
    }
}
