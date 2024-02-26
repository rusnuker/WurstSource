// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot.commands;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;

public class CommandManager
{
    private final TreeMap<String, Command> commands;
    
    public CommandManager() {
        this.commands = new TreeMap<String, Command>(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        this.addCommand(new ChatCmd());
        this.addCommand(new FixmeCmd());
        this.addCommand(new HelpCmd());
        this.addCommand(new JoinCmd());
        this.addCommand(new LoginCmd());
        this.addCommand(new ProxyCmd());
        this.addCommand(new StopCmd());
    }
    
    public Command getCommandByClass(final Class<?> commandClass) {
        return this.commands.get(commandClass.getAnnotation(Command.Info.class).name());
    }
    
    public Command getCommandByName(final String name) {
        return this.commands.get(name);
    }
    
    public Collection<Command> getAllCommands() {
        return this.commands.values();
    }
    
    public int countCommands() {
        return this.commands.size();
    }
    
    private void addCommand(final Command commmand) {
        this.commands.put(commmand.getName(), commmand);
    }
}
