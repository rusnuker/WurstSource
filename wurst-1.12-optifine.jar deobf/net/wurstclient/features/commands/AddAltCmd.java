// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import java.util.Iterator;
import net.wurstclient.altmanager.AltManager;
import net.minecraft.util.StringUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.altmanager.Alt;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/addalt")
public final class AddAltCmd extends Command
{
    public AddAltCmd() {
        super("addalt", "Adds a player to your alt list.", new String[] { "<player>", "all" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 1) {
            throw new CmdSyntaxError();
        }
        final String name = args[0];
        final String s;
        switch (s = name) {
            case "all": {
                this.addAll();
                return;
            }
            default:
                break;
        }
        this.add(name);
    }
    
    private void add(final String name) {
        if (name.equalsIgnoreCase("Alexander01998")) {
            return;
        }
        AddAltCmd.WURST.getAltManager().add(new Alt(name, null, null));
        ChatUtils.message("Added 1 alt.");
    }
    
    private void addAll() {
        int alts = 0;
        final AltManager altManager = AddAltCmd.WURST.getAltManager();
        final String playerName = AddAltCmd.MC.getSession().getProfile().getName();
        for (final NetworkPlayerInfo info : WMinecraft.getConnection().getPlayerInfoMap()) {
            String name = info.getPlayerNameForReal();
            name = StringUtils.stripControlCodes(name);
            if (altManager.contains(name)) {
                continue;
            }
            if (name.equals(playerName)) {
                continue;
            }
            if (name.equals("Alexander01998")) {
                continue;
            }
            altManager.add(new Alt(name, null, null));
            ++alts;
        }
        ChatUtils.message("Added " + alts + ((alts == 1) ? " alt." : " alts."));
    }
}
