// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot.commands;

import net.minecraft.client.Minecraft;
import net.wurstclient.altmanager.LoginManager;

@Info(help = "Logs you in with a premium or cracked account.", name = "login", syntax = { "<email> <password>", "<name>" })
public class LoginCmd extends Command
{
    @Override
    public void execute(final String[] args) throws CmdError {
        if (args.length < 1 || args.length > 2) {
            this.syntaxError();
        }
        if (args.length == 1) {
            LoginManager.changeCrackedName(args[0]);
            System.out.println("Changed name to \"" + args[0] + "\".");
        }
        else {
            final String error = LoginManager.login(args[0], args[1]);
            if (error.isEmpty()) {
                System.out.println("Logged in as " + Minecraft.getMinecraft().session.getUsername());
            }
            else {
                this.error(error);
            }
        }
    }
}
