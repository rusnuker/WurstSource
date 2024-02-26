// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Command;

public final class ExcavateCmd extends Command
{
    public ExcavateCmd() {
        super("excavate", "Automatically destroys all blocks in the selected area.", new String[] { "<x1> <y1> <z1> <x2> <y2> <z2>" });
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { ExcavateCmd.WURST.hax.excavatorMod };
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 6) {
            throw new CmdSyntaxError();
        }
        final BlockPos pos1 = this.argsToPos(args[0], args[1], args[2]);
        final BlockPos pos2 = this.argsToPos(args[3], args[4], args[5]);
        ExcavateCmd.WURST.hax.excavatorMod.enableWithArea(pos1, pos2);
    }
}
