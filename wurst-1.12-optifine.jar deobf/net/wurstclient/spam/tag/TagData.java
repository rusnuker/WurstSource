// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.tag;

public class TagData
{
    private int tagStart;
    private int tagLength;
    private int tagLine;
    private String tagName;
    private String[] tagArgs;
    private boolean tagClosed;
    private String tag;
    private String tagContent;
    private int tagContentLength;
    private String spam;
    
    public boolean isTagClosed() {
        return this.tagClosed;
    }
    
    public void setTagClosed(final boolean tagClosed) {
        this.tagClosed = tagClosed;
    }
    
    public TagData(final int tagStart, final int tagLength, final int tagLine, final String tagName, final String[] tagArgs, final boolean tagClosed, final String tag, final String tagContent, final int tagContentLength, final String spam) {
        this.tagStart = tagStart;
        this.tagLength = tagLength;
        this.tagLine = tagLine;
        this.tagName = tagName;
        this.tagArgs = tagArgs;
        this.tagClosed = tagClosed;
        this.tag = tag;
        this.tagContent = tagContent;
        this.tagContentLength = tagContentLength;
        this.spam = spam;
    }
    
    public int getTagStart() {
        return this.tagStart;
    }
    
    public void setTagStart(final int tagStart) {
        this.tagStart = tagStart;
    }
    
    public int getTagLength() {
        return this.tagLength;
    }
    
    public void setTagLength(final int tagLength) {
        this.tagLength = tagLength;
    }
    
    public int getTagLine() {
        return this.tagLine;
    }
    
    public void setTagLine(final int tagLine) {
        this.tagLine = tagLine;
    }
    
    public String getTagName() {
        return this.tagName;
    }
    
    public void setTagName(final String tagName) {
        this.tagName = tagName;
    }
    
    public String[] getTagArgs() {
        return this.tagArgs;
    }
    
    public void setTagArgs(final String[] tagArgs) {
        this.tagArgs = tagArgs;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public void setTag(final String tag) {
        this.tag = tag;
    }
    
    public String getTagContent() {
        return this.tagContent;
    }
    
    public void setTagContent(final String tagContent) {
        this.tagContent = tagContent;
    }
    
    public int getTagContentLength() {
        return this.tagContentLength;
    }
    
    public void setTagContentLength(final int tagContentLength) {
        this.tagContentLength = tagContentLength;
    }
    
    public String getSpam() {
        return this.spam;
    }
    
    public void setSpam(final String spam) {
        this.spam = spam;
    }
}
