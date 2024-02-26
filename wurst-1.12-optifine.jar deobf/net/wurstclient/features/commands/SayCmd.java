// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Command;

@SearchTags({ ".legit", "dots in chat", "command bypass", "prefix" })
@HelpPage("Commands/say")
public final class SayCmd extends Command
{
    public SayCmd() {
        super("say", "Sends a chat message, even if the message starts with a dot.", new String[] { "<message>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length > 0) {
            String message = args[0];
            for (int i = 1; i < args.length; ++i) {
                message = String.valueOf(message) + " " + args[i];
            }
            WConnection.sendPacket(new CPacketChatMessage(message));
            return;
        }
        throw new CmdSyntaxError("Message required.");
    }
}
