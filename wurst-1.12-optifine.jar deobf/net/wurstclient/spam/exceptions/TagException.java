// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.exceptions;

import net.wurstclient.spam.tag.Tag;

public class TagException extends SpamException
{
    public final Tag tag;
    
    public TagException(final String message, final int line, final Tag tag) {
        super(message, line);
        this.tag = tag;
    }
    
    @Override
    public String getHelp() {
        return this.tag.getHelp();
    }
}
