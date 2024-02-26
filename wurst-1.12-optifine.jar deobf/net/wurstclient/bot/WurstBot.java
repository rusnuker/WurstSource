// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot;

import net.wurstclient.bot.commands.Command;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.minecraft.client.main.Main;
import net.wurstclient.bot.commands.CommandManager;

public class WurstBot
{
    private static boolean enabled;
    private static WurstBot wurstBot;
    private final CommandManager commandManager;
    
    static {
        WurstBot.enabled = false;
    }
    
    public WurstBot() {
        this.commandManager = new CommandManager();
    }
    
    public static void main(final String[] args) {
        System.out.println("Starting Wurst-Bot...");
        WurstBot.enabled = true;
        WurstBot.wurstBot = new WurstBot();
        Main.main(new String[] { "--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}" });
    }
    
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WurstBot.this.run();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }, "Wurst-Bot").start();
    }
    
    private void run() throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println();
        System.out.println("           +++++++++++++++++++++++++++++++++++++++++++++++           ");
        System.out.println("       +++#++++##++++#+#+++++++#++######+++++######+#######+++       ");
        System.out.println("     +++++#++++##++++#+#+++++++#++#+++++##++#++++++++++#++++++++     ");
        System.out.println("    +++++++#++#++#++#++#+++++++#++#######++++######++++#+++++++++    ");
        System.out.println("     ++++++#++#++#++#+++#+++++#+++#+++##+++++++++++#+++#++++++++     ");
        System.out.println("       +++++##++++##+++++#####++++#+++++##+++######++++#++++++       ");
        System.out.println("           +++++++++++++++++++++++++++++++++++++++++++++++           ");
        System.out.println();
        System.out.println("Wurst-Bot v6.35.3");
        System.out.println("Type \"help\" for a list of commands.");
        while (true) {
            final String input = br.readLine();
            final String commandName = input.split(" ")[0];
            String[] args;
            if (input.contains(" ")) {
                args = input.substring(input.indexOf(" ") + 1).split(" ");
            }
            else {
                args = new String[0];
            }
            final Command command = this.commandManager.getCommandByName(commandName);
            if (command != null) {
                try {
                    command.execute(args);
                }
                catch (final Command.CmdSyntaxError e) {
                    if (e.getMessage() != null) {
                        System.err.println("Syntax error: " + e.getMessage());
                    }
                    else {
                        System.err.println("Syntax error!");
                    }
                    command.printSyntax();
                }
                catch (final Command.CmdError e2) {
                    System.err.println(e2.getMessage());
                }
                catch (final Exception e3) {
                    e3.printStackTrace();
                }
            }
            else {
                System.err.println("\"" + commandName + "\" is not a valid command.");
            }
        }
    }
    
    public static boolean isEnabled() {
        return WurstBot.enabled;
    }
    
    public static WurstBot getBot() {
        return WurstBot.wurstBot;
    }
    
    public CommandManager getCommandManager() {
        return this.commandManager;
    }
}
