// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.tag;

import net.wurstclient.spam.exceptions.SpamException;
import net.wurstclient.features.mods.chat.SpammerMod;
import net.wurstclient.spam.SpamProcessor;

public abstract class Tag
{
    private String name;
    private String description;
    private String syntax;
    private String example;
    
    public Tag(final String name, final String description, final String syntax, final String example) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.example = example;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getSyntax() {
        return this.syntax;
    }
    
    public String getExample() {
        return this.example;
    }
    
    public String getHelp() {
        return "<html><table width=\"512px\"><tr><td><h1>&lt;" + this.name + "&gt;</h1>" + "<h2>Description</h2>" + "<p>" + this.format(this.description) + "</p>" + "<h2>Syntax</h2>" + "<div bgcolor=\"#000000\" color=\"#00ff00\">" + "<code>" + this.format(this.syntax) + "</code>" + "</div>" + "<h2>Example</h2>" + "<div bgcolor=\"#000000\" color=\"#00ff00\">" + "<code>" + this.format(this.example) + "</code>" + "</div><br>" + "<p>Would be processed to:</p><br>" + "<div bgcolor=\"#444444\" color=\"#ffffff\">" + "<p>" + this.format(SpamProcessor.process(this.example, null, false)) + "</p>" + "</div>" + "</td></tr></table>" + "</html>";
    }
    
    private String format(final String string) {
        return string.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>");
    }
    
    public abstract String process(final TagData p0) throws SpamException;
}
