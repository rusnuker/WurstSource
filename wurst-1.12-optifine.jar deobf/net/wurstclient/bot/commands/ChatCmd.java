// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot.commands;

import net.wurstclient.compatibility.WMinecraft;

@Info(help = "Sends a chat message. Can be used to run Wurst commands.", name = "chat", syntax = { "<message>" })
public class ChatCmd extends Command
{
    @Override
    public void execute(final String[] args) throws CmdError {
        if (args.length == 0) {
            this.syntaxError();
        }
        if (WMinecraft.getPlayer() == null) {
            this.error("Not connected to any server.");
        }
        String message = args[0];
        for (int i = 1; i < args.length; ++i) {
            message = String.valueOf(message) + " " + args[i];
        }
        WMinecraft.getPlayer().sendChatMessage(message);
    }
}
