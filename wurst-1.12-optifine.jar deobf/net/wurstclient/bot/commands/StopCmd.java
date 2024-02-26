// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot.commands;

import net.minecraft.client.Minecraft;

@Info(help = "Stops Wurst-Bot.", name = "stop", syntax = {})
public class StopCmd extends Command
{
    @Override
    public void execute(final String[] args) throws CmdError {
        if (args.length != 0) {
            this.syntaxError();
        }
        System.out.println("Stopping Wurst-Bot...");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Wurst-Bot stopped.");
            }
        }));
        Minecraft.getMinecraft().shutdown();
    }
}
