// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/leave")
public final class LeaveCmd extends Command
{
    public LeaveCmd() {
        super("leave", "Instantly disconnects from the server.", new String[0]);
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 1 && args[0].equalsIgnoreCase("taco")) {
            for (int i = 0; i < 128; ++i) {
                LeaveCmd.MC.player.sendAutomaticChatMessage("Taco!");
            }
        }
        else if (args.length != 0) {
            throw new CmdSyntaxError();
        }
        WMinecraft.getWorld().sendQuittingDisconnectingPacket();
    }
    
    @Override
    public String getPrimaryAction() {
        return "Leave";
    }
    
    @Override
    public void doPrimaryAction() {
        LeaveCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".leave", true));
    }
}
