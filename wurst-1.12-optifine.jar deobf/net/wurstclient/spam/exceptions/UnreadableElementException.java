// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.exceptions;

public class UnreadableElementException extends ExceptionWithDefaultHelp
{
    public UnreadableElementException(final String element, final int line) {
        super("This could not be read: " + (element.contains("\n") ? element.substring(0, element.indexOf("\n")) : element), line);
    }
}
