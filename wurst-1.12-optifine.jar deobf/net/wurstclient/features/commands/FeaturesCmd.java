// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.features.Bypasses;
import java.util.Iterator;
import net.wurstclient.features.OtherFeature;
import net.wurstclient.features.Hack;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/features")
public final class FeaturesCmd extends Command
{
    public FeaturesCmd() {
        super("features", "Shows the feature count and some other statistics.", new String[0]);
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 0) {
            throw new CmdSyntaxError();
        }
        final int hax = FeaturesCmd.WURST.hax.size();
        final int cmds = FeaturesCmd.WURST.commands.countCommands();
        final int otfs = FeaturesCmd.WURST.special.countFeatures();
        final int all = hax + cmds + otfs;
        ChatUtils.message("All features: " + all);
        ChatUtils.message("Hacks: " + hax);
        ChatUtils.message("Commands: " + cmds);
        ChatUtils.message("Other features: " + otfs);
        int settings = 0;
        for (final Hack hack : FeaturesCmd.WURST.hax.getAll()) {
            settings += hack.getSettings().size();
        }
        for (final Command cmd : FeaturesCmd.WURST.commands.getAllCommands()) {
            settings += cmd.getSettings().size();
        }
        for (final OtherFeature otf : FeaturesCmd.WURST.special.getAllFeatures()) {
            settings += otf.getSettings().size();
        }
        ChatUtils.message("Settings: " + settings);
        int bypassingHax = 0;
        for (final Hack hack2 : FeaturesCmd.WURST.hax.getAll()) {
            final Bypasses bypasses = hack2.getBypasses();
            if (bypasses != null) {
                if (!bypasses.latestNCP()) {
                    continue;
                }
                ++bypassingHax;
            }
        }
        ChatUtils.message("NoCheat+ bypassing hacks: " + bypassingHax);
    }
    
    @Override
    public String getPrimaryAction() {
        return "Show Statistics";
    }
    
    @Override
    public void doPrimaryAction() {
        FeaturesCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".features", true));
    }
}
