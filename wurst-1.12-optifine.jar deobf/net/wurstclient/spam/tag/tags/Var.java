// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.tag.tags;

import net.wurstclient.spam.exceptions.SpamException;
import net.wurstclient.spam.SpamProcessor;
import net.wurstclient.spam.exceptions.InvalidArgumentException;
import net.wurstclient.spam.exceptions.MissingArgumentException;
import net.wurstclient.spam.tag.TagData;
import net.wurstclient.spam.tag.Tag;

public class Var extends Tag
{
    public Var() {
        super("var", "Defines a new variable.", "<var name>value</var>", "<var link>example.com</var><!--\n-->Check out my website: §link;");
    }
    
    @Override
    public String process(final TagData tagData) throws SpamException {
        if (tagData.getTagArgs().length == 0) {
            throw new MissingArgumentException("The <var> tag requires at least one argument.", tagData.getTagLine(), this);
        }
        if (tagData.getTagArgs()[0].startsWith("_")) {
            throw new InvalidArgumentException("You cannot define variables that start with \"_\".", tagData.getTagLine(), this);
        }
        SpamProcessor.varManager.addUserVar(tagData.getTagArgs()[0], tagData.getTagContent());
        return "";
    }
}
