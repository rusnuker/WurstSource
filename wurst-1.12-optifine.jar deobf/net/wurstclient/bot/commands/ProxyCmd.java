// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot.commands;

import net.wurstclient.util.MathUtils;

@Info(help = "Changes the proxy used for server connections. Must be a SOCKS proxy.", name = "proxy", syntax = { "<ip>:<port>", "none" })
public class ProxyCmd extends Command
{
    @Override
    public void execute(final String[] args) throws CmdError {
        if (args.length < 1 || args.length > 2) {
            this.syntaxError();
        }
        if (args[0].contains(":")) {
            final String ip = args[0].split(":")[0];
            final String portSring = args[0].split(":")[1];
            if (!MathUtils.isInteger(portSring)) {
                this.syntaxError("Invalid port: " + portSring);
            }
            try {
                System.setProperty("socksProxyHost", ip);
                System.setProperty("socksProxyPort", portSring);
            }
            catch (final Exception e) {
                this.error(e.getMessage());
            }
        }
        else if (args[0].equalsIgnoreCase("none")) {
            System.setProperty("socksProxyHost", "");
            System.setProperty("socksProxyPort", "");
        }
        else {
            this.syntaxError("Not a proxy: " + args[0]);
        }
        System.out.println("Proxy set to " + args[0]);
    }
}
