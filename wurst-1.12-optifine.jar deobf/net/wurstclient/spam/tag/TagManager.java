// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.tag;

import net.wurstclient.spam.tag.tags.Var;
import net.wurstclient.spam.tag.tags.Repeat;
import net.wurstclient.spam.tag.tags.Random;
import net.wurstclient.spam.exceptions.SpamException;
import java.util.Iterator;
import net.wurstclient.spam.exceptions.InvalidTagException;
import java.util.ArrayList;

public class TagManager
{
    private final ArrayList<Tag> activeTags;
    
    public Tag getTagByName(final String name, final int line) throws SpamException {
        for (final Tag activeTag : this.activeTags) {
            if (activeTag.getName().equals(name)) {
                return activeTag;
            }
        }
        throw new InvalidTagException(name, line);
    }
    
    public ArrayList<Tag> getActiveTags() {
        return this.activeTags;
    }
    
    public String process(final TagData tagData) throws SpamException {
        final Tag tag = this.getTagByName(tagData.getTagName(), tagData.getTagLine());
        final String processedTag = tag.process(tagData);
        return processedTag;
    }
    
    public TagManager() {
        (this.activeTags = new ArrayList<Tag>()).add(new Random());
        this.activeTags.add(new Repeat());
        this.activeTags.add(new Var());
    }
}
