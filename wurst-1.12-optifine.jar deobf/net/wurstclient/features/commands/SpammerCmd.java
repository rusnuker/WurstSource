// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.spam.SpamProcessor;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.mods.chat.SpammerMod;
import net.wurstclient.util.MathUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/spammer")
public final class SpammerCmd extends Command
{
    public SpammerCmd() {
        super("spammer", "Changes the delay of Spammer or spams spam from a file.", new String[] { "delay <delay_in_ms>", "spam <file>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError();
        }
        if (args[0].equalsIgnoreCase("delay")) {
            if (!MathUtils.isInteger(args[1])) {
                throw new CmdSyntaxError();
            }
            int newDelay = Integer.parseInt(args[1]);
            if (newDelay % 50 > 0) {
                newDelay -= newDelay % 50;
            }
            SpammerCmd.WURST.options.spamDelay = newDelay;
            SpammerMod.updateDelaySpinner();
            ChatUtils.message("Spammer delay set to " + newDelay + "ms.");
        }
        else if (args[0].equalsIgnoreCase("spam") && !SpamProcessor.runSpam(args[1])) {
            ChatUtils.error("File does not exist.");
        }
    }
}
