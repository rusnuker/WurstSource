// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.chat;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ChatType;
import net.minecraft.client.Minecraft;

public class NormalChatListener implements IChatListener
{
    private final Minecraft field_192581_a;
    
    public NormalChatListener(final Minecraft p_i47393_1_) {
        this.field_192581_a = p_i47393_1_;
    }
    
    @Override
    public void func_192576_a(final ChatType p_192576_1_, final ITextComponent p_192576_2_) {
        this.field_192581_a.ingameGUI.getChatGUI().printChatMessage(p_192576_2_);
    }
}
