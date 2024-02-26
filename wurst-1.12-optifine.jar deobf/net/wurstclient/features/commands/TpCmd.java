// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/tp")
public final class TpCmd extends Command
{
    public TpCmd() {
        super("tp", "Teleports you up to 100 blocks away.\nOnly works on vanilla servers!", new String[] { "<x> <y> <z>", "<entity>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        final BlockPos pos = this.argsToPos(args);
        TpCmd.MC.player.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
}
