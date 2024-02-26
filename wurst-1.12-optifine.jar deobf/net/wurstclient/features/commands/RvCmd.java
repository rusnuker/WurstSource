// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/rv")
public final class RvCmd extends Command
{
    public RvCmd() {
        super("rv", "Toggles RemoteView or makes it target a specific entity.", new String[] { "[<player>]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            RvCmd.WURST.hax.remoteViewMod.onToggledByCommand(null);
            return;
        }
        if (args.length == 1) {
            RvCmd.WURST.hax.remoteViewMod.onToggledByCommand(args[0]);
            return;
        }
        throw new CmdSyntaxError("Too many arguments.");
    }
}
