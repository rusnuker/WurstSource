// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot.commands;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import net.wurstclient.compatibility.WMinecraft;

@Info(help = "Fixes you if you get stuck in a menu.", name = "fixme", syntax = {})
public class FixmeCmd extends Command
{
    @Override
    public void execute(final String[] args) throws CmdError {
        if (args.length != 0) {
            this.syntaxError();
        }
        if (WMinecraft.getPlayer() == null) {
            this.error("Not connected to any server.");
        }
        Minecraft.getMinecraft().openScreen(null);
        System.out.println("Closed all open menus.");
    }
}
