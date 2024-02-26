// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.exceptions;

public class SpamException extends Exception
{
    public final int line;
    
    public SpamException(final String message, final int line) {
        super(message);
        this.line = line;
    }
    
    public String getHelp() {
        return "<html><center>Error! No help available.<br>Please report this at <a href=\"\">wurstclient.net/bugs</a>!";
    }
}
