// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.exceptions;

public class InvalidVariableException extends ExceptionWithDefaultHelp
{
    public InvalidVariableException(final String varname, final int line) {
        super("There is no variable called \"" + varname + "\".", line);
    }
}
