// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.command;

public class NumberInvalidException extends CommandException
{
    public NumberInvalidException() {
        this("commands.generic.num.invalid", new Object[0]);
    }
    
    public NumberInvalidException(final String message, final Object... replacements) {
        super(message, replacements);
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
