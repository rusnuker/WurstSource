// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.features.mods.movement.BlinkMod;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/blink")
public final class BlinkCmd extends Command
{
    public BlinkCmd() {
        super("blink", "Enables, disables or cancels Blink.", new String[] { "[on|off]", "cancel" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length > 1) {
            throw new CmdSyntaxError();
        }
        final BlinkMod blinkHack = BlinkCmd.WURST.hax.blinkMod;
        if (args.length == 0) {
            blinkHack.toggle();
            return;
        }
        final String lowerCase;
        switch (lowerCase = args[0].toLowerCase()) {
            case "cancel": {
                this.cancel(blinkHack);
                return;
            }
            case "on": {
                blinkHack.setEnabled(true);
                return;
            }
            case "off": {
                blinkHack.setEnabled(false);
                return;
            }
            default:
                break;
        }
        throw new CmdSyntaxError();
    }
    
    private void cancel(final BlinkMod blinkHack) throws CmdException {
        if (!blinkHack.isEnabled()) {
            throw new CmdError("Cannot cancel, Blink is already turned off!");
        }
        blinkHack.cancel();
    }
}
