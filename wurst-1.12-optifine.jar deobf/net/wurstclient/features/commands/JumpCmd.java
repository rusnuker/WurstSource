// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/jump")
public final class JumpCmd extends Command
{
    public JumpCmd() {
        super("jump", "Makes you jump once.", new String[0]);
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 0) {
            throw new CmdSyntaxError();
        }
        if (!JumpCmd.MC.player.onGround && !JumpCmd.WURST.hax.jetpackMod.isActive()) {
            throw new CmdError("Can't jump in mid-air.");
        }
        JumpCmd.MC.player.jump();
    }
    
    @Override
    public String getPrimaryAction() {
        return "Jump";
    }
    
    @Override
    public void doPrimaryAction() {
        JumpCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".jump", true));
    }
}
