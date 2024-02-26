// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.features.Command;

@HelpPage("Commands/annoy")
public final class AnnoyCmd extends Command implements ChatInputListener
{
    private boolean toggled;
    private String name;
    
    public AnnoyCmd() {
        super("annoy", "Annoys a player by repeating everything he says.", new String[] { "[<player>]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        this.toggled = !this.toggled;
        if (this.toggled) {
            if (args.length != 1) {
                this.toggled = false;
                throw new CmdSyntaxError();
            }
            this.name = args[0];
            ChatUtils.message("Now annoying " + this.name + ".");
            if (this.name.equals(AnnoyCmd.MC.player.getName())) {
                ChatUtils.warning("Annoying yourself is a bad idea!");
            }
            AnnoyCmd.EVENTS.add(ChatInputListener.class, this);
        }
        else {
            AnnoyCmd.EVENTS.remove(ChatInputListener.class, this);
            if (this.name != null) {
                ChatUtils.message("No longer annoying " + this.name + ".");
                this.name = null;
            }
        }
    }
    
    @Override
    public void onReceivedMessage(final ChatInputEvent event) {
        final String message = new String(event.getComponent().getUnformattedText());
        if (message.startsWith("§c[§6Wurst§c]§f ")) {
            return;
        }
        if (message.startsWith("<" + this.name + ">") || message.contains(String.valueOf(this.name) + ">")) {
            final String repeatMessage = message.substring(message.indexOf(">") + 1);
            AnnoyCmd.MC.player.sendChatMessage(repeatMessage);
        }
        else if (message.contains("] " + this.name + ":") || message.contains("]" + this.name + ":")) {
            final String repeatMessage = message.substring(message.indexOf(":") + 1);
            AnnoyCmd.MC.player.sendChatMessage(repeatMessage);
        }
    }
}
