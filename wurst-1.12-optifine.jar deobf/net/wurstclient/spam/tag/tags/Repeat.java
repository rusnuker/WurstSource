// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.tag.tags;

import net.wurstclient.spam.exceptions.SpamException;
import net.wurstclient.spam.exceptions.InvalidArgumentException;
import net.wurstclient.util.MathUtils;
import net.wurstclient.spam.exceptions.MissingArgumentException;
import net.wurstclient.spam.tag.TagData;
import net.wurstclient.spam.tag.Tag;

public class Repeat extends Tag
{
    public Repeat() {
        super("repeat", "Repeats a chat message or a part of a chat message.", "<repeat number>", "Repeating a part of a message:\nSpam<repeat 2>, spam</repeat>!\n\nRepeating a message:<repeat 3>\nSpam!</repeat>");
    }
    
    @Override
    public String process(final TagData tagData) throws SpamException {
        String processed = "";
        if (tagData.getTagArgs().length == 0) {
            throw new MissingArgumentException("The <repeat> tag requires at least one argument.", tagData.getTagLine(), this);
        }
        if (!MathUtils.isInteger(tagData.getTagArgs()[0])) {
            throw new InvalidArgumentException("Invalid number in <repeat> tag: \"" + tagData.getTagArgs()[0] + "\"", tagData.getTagLine(), this);
        }
        for (int count = Integer.parseInt(tagData.getTagArgs()[0]), i = 0; i < count; ++i) {
            processed = String.valueOf(processed) + tagData.getTagContent();
        }
        return processed;
    }
}
