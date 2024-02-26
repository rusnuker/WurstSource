// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot.commands;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.wurstclient.gui.main.GuiWurstMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.Minecraft;

@Info(help = "Joins a server.", name = "join", syntax = { "<ip>" })
public final class JoinCmd extends Command
{
    @Override
    public void execute(final String[] args) throws CmdError {
        if (args.length != 1) {
            this.syntaxError();
        }
        final Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> {
            new GuiConnecting(new GuiWurstMainMenu(), mcIn, new ServerData("", array[0], false));
            final GuiConnecting guiScreenIn;
            mcIn.openScreen(guiScreenIn);
            System.out.println("Joined " + array[0] + " as " + mcIn.session.getUsername());
        });
    }
}
