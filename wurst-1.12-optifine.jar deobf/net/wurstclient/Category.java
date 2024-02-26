// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient;

public enum Category
{
    BLOCKS("BLOCKS", 0, "Blocks"), 
    MOVEMENT("MOVEMENT", 1, "Movement"), 
    COMBAT("COMBAT", 2, "Combat"), 
    RENDER("RENDER", 3, "Render"), 
    CHAT("CHAT", 4, "Chat"), 
    FUN("FUN", 5, "Fun"), 
    ITEMS("ITEMS", 6, "Items"), 
    RETRO("RETRO", 7, "Retro"), 
    OTHER("OTHER", 8, "Other");
    
    private final String name;
    
    private Category(final String name2, final int ordinal, final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
