// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.files.ConfigFiles;
import net.wurstclient.util.MathUtils;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/throw")
public final class ThrowCmd extends Command
{
    public ThrowCmd() {
        super("throw", "Changes the amount of Throw or toggles it.", new String[] { "[amount <amount>]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            ThrowCmd.WURST.hax.throwMod.toggle();
            ChatUtils.message("Throw turned " + (ThrowCmd.WURST.hax.throwMod.isEnabled() ? "on" : "off") + ".");
        }
        else {
            if (args.length != 2 || !args[0].equalsIgnoreCase("amount") || !MathUtils.isInteger(args[1])) {
                throw new CmdSyntaxError();
            }
            if (Integer.valueOf(args[1]) < 1) {
                ChatUtils.error("Throw amount must be at least 1.");
                return;
            }
            ThrowCmd.WURST.options.throwAmount = Integer.valueOf(args[1]);
            ConfigFiles.OPTIONS.save();
            ChatUtils.message("Throw amount set to " + args[1] + ".");
        }
    }
}
