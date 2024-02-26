// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.bot.commands;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

public abstract class Command
{
    private String name;
    private String help;
    private String[] syntax;
    
    public Command() {
        this.name = this.getClass().getAnnotation(Info.class).name();
        this.help = this.getClass().getAnnotation(Info.class).help();
        this.syntax = this.getClass().getAnnotation(Info.class).syntax();
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getHelp() {
        return this.help;
    }
    
    public String[] getSyntax() {
        return this.syntax;
    }
    
    public void printHelp() {
        String[] split;
        for (int length = (split = this.help.split("\n")).length, i = 0; i < length; ++i) {
            final String line = split[i];
            System.out.println(line);
        }
    }
    
    public void printSyntax() {
        System.out.println("Syntax:");
        String output = this.name;
        if (this.syntax.length != 0) {
            String spaces;
            for (output = String.valueOf(output) + " ", spaces = ""; spaces.length() < output.length(); spaces = String.valueOf(spaces) + " ") {}
            output = String.valueOf(output) + this.syntax[0];
            for (int i = 1; i < this.syntax.length; ++i) {
                output = String.valueOf(output) + "\n" + spaces + this.syntax[i];
            }
        }
        String[] split;
        for (int length = (split = output.split("\n")).length, j = 0; j < length; ++j) {
            final String line = split[j];
            System.out.println(line);
        }
    }
    
    protected final void syntaxError() throws CmdSyntaxError {
        throw new CmdSyntaxError();
    }
    
    protected final void syntaxError(final String message) throws CmdSyntaxError {
        throw new CmdSyntaxError(message);
    }
    
    protected final void error(final String message) throws CmdError {
        throw new CmdError(message);
    }
    
    public abstract void execute(final String[] p0) throws CmdError;
    
    public class CmdSyntaxError extends CmdError
    {
        public CmdSyntaxError() {
        }
        
        public CmdSyntaxError(final String message) {
            super(message);
        }
    }
    
    public class CmdError extends Throwable
    {
        public CmdError() {
        }
        
        public CmdError(final String message) {
            super(message);
        }
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Info {
        String name();
        
        String help();
        
        String[] syntax();
    }
}
