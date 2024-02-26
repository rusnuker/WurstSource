// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.exceptions;

public class InvalidTagException extends ExceptionWithDefaultHelp
{
    public InvalidTagException(final String tagname, final int line) {
        super("There is no tag called \"" + tagname + "\".", line);
    }
}
