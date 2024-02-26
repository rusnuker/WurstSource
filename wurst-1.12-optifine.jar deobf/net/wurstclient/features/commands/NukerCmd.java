// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.features.mods.blocks.NukerMod;
import net.minecraft.block.Block;
import net.wurstclient.util.MathUtils;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/nuker")
public final class NukerCmd extends Command
{
    public NukerCmd() {
        super("nuker", "Changes the settings of Nuker.", new String[] { "mode (normal|id|flat|smash)", "id <block_id>", "name <block_name>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        final NukerMod nuker = NukerCmd.WURST.hax.nukerMod;
        if (args.length != 2) {
            throw new CmdSyntaxError();
        }
        if (args[0].toLowerCase().equals("mode")) {
            final String[] modeNames = nuker.mode.getModes();
            final String newModeName = args[1];
            int newMode = -1;
            for (int i = 0; i < modeNames.length; ++i) {
                if (newModeName.equals(modeNames[i].toLowerCase())) {
                    newMode = i;
                }
            }
            if (newMode == -1) {
                throw new CmdSyntaxError("Invalid mode");
            }
            if (newMode != nuker.mode.getSelected()) {
                nuker.mode.setSelected(newMode);
            }
            ChatUtils.message("Nuker mode set to \"" + args[1] + "\".");
        }
        else if (args[0].equalsIgnoreCase("id") && MathUtils.isInteger(args[1])) {
            if (nuker.mode.getSelected() != 1) {
                nuker.mode.setSelected(1);
                ChatUtils.message("Nuker mode set to \"" + args[0] + "\".");
            }
            nuker.setId(Integer.valueOf(args[1]));
            ChatUtils.message("Nuker ID set to \"" + args[1] + "\".");
        }
        else {
            if (!args[0].equalsIgnoreCase("name")) {
                throw new CmdSyntaxError();
            }
            if (nuker.mode.getSelected() != 1) {
                nuker.mode.setSelected(1);
                ChatUtils.message("Nuker mode set to \"" + args[0] + "\".");
            }
            final int newId = Block.getIdFromBlock(Block.getBlockFromName(args[1]));
            if (newId == -1) {
                throw new CmdError("The block \"" + args[1] + "\" could not be found.");
            }
            nuker.setId(newId);
            ChatUtils.message("Nuker ID set to " + newId + " (" + args[1] + ").");
        }
    }
}
