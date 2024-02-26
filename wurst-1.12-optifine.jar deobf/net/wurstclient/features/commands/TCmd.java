// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.features.Hack;
import net.wurstclient.features.HelpPage;
import net.wurstclient.DontBlock;
import net.wurstclient.features.Command;

@DontBlock
@HelpPage("Commands/t")
public final class TCmd extends Command
{
    public TCmd() {
        super("t", "Toggles a mod.", new String[] { "<mod> [on|off]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length < 1 || args.length > 2) {
            throw new CmdSyntaxError();
        }
        final Hack hack = TCmd.WURST.hax.getByName(args[0]);
        if (hack == null) {
            throw new CmdError("Unknown mod: " + args[0]);
        }
        if (args.length != 1) {
            final String lowerCase;
            switch (lowerCase = args[1].toLowerCase()) {
                case "on": {
                    hack.setEnabled(true);
                    return;
                }
                case "off": {
                    hack.setEnabled(false);
                    return;
                }
                default:
                    break;
            }
            throw new CmdSyntaxError();
        }
        hack.setEnabled(!hack.isEnabled());
    }
}
