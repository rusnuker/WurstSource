// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.util.text.ITextComponent;

public class ChatLine
{
    private final int updateCounterCreated;
    private final ITextComponent lineString;
    private final int chatLineID;
    
    public ChatLine(final int p_i45000_1_, final ITextComponent p_i45000_2_, final int p_i45000_3_) {
        this.lineString = p_i45000_2_;
        this.updateCounterCreated = p_i45000_1_;
        this.chatLineID = p_i45000_3_;
    }
    
    public ITextComponent getChatComponent() {
        return this.lineString;
    }
    
    public int getUpdatedCounter() {
        return this.updateCounterCreated;
    }
    
    public int getChatLineID() {
        return this.chatLineID;
    }
}
