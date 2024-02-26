// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.client.Minecraft;

public final class WChat
{
    public static void clearMessages() {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages(true);
    }
}
