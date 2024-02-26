// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.exceptions;

import net.wurstclient.spam.tag.Tag;

public class InvalidArgumentException extends TagException
{
    public InvalidArgumentException(final String message, final int line, final Tag tag) {
        super(message, line, tag);
    }
}
