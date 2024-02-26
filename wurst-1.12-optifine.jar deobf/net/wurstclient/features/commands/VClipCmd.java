// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.util.MathUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/vclip")
public final class VClipCmd extends Command
{
    public VClipCmd() {
        super("vclip", "Teleports you up/down. Can glitch you through floors & ceilings.\nThe maximum distance is 100 blocks on vanilla servers and 10 blocks on Bukkit servers.", new String[] { "<height>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 1) {
            throw new CmdSyntaxError();
        }
        if (MathUtils.isInteger(args[0])) {
            VClipCmd.MC.player.setPosition(VClipCmd.MC.player.posX, VClipCmd.MC.player.posY + Integer.valueOf(args[0]), VClipCmd.MC.player.posZ);
            return;
        }
        throw new CmdSyntaxError();
    }
}
