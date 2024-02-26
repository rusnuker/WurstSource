// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

public class ChatUtils
{
    private static boolean enabled;
    
    static {
        ChatUtils.enabled = true;
    }
    
    public static void setEnabled(final boolean enabled) {
        ChatUtils.enabled = enabled;
    }
    
    public static void component(final ITextComponent component) {
        if (ChatUtils.enabled) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§c[§6Wurst§c]§f ").appendSibling(component));
        }
    }
    
    public static void message(final String message) {
        component(new TextComponentString(message));
    }
    
    public static void warning(final String message) {
        message("§c[§6§lWARNING§c]§f " + message);
    }
    
    public static void error(final String message) {
        message("§c[§4§lERROR§c]§f " + message);
    }
    
    public static void success(final String message) {
        message("§a[§2§lSUCCESS§a]§f " + message);
    }
    
    public static void failure(final String message) {
        message("§c[§4§lFAILURE§c]§f " + message);
    }
    
    public static void cmd(final String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§c[§6Wurst§c]§f §0§l<§aCMD§0§l>§f " + message));
    }
}
