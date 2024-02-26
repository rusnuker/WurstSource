// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.chat;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.features.Hack;

@SearchTags({ "fancy chat" })
@Bypasses(ghostMode = false, mineplex = false)
public final class FancyChatMod extends Hack implements ChatOutputListener
{
    private final String blacklist = "(){}[]|";
    
    public FancyChatMod() {
        super("FancyChat", "Replaces ASCII characters in sent chat messages with fancier unicode characters. Can be\nused to bypass curse word filters on some servers. Does not work on servers that block\nunicode characters.");
        this.setCategory(Category.CHAT);
    }
    
    @Override
    public void onEnable() {
        FancyChatMod.EVENTS.add(ChatOutputListener.class, this);
    }
    
    @Override
    public void onDisable() {
        FancyChatMod.EVENTS.remove(ChatOutputListener.class, this);
    }
    
    @Override
    public void onSentMessage(final ChatOutputEvent event) {
        if (event.getMessage().startsWith("/") || event.getMessage().startsWith(".")) {
            return;
        }
        String out = "";
        char[] charArray;
        for (int length = (charArray = event.getMessage().toCharArray()).length, i = 0; i < length; ++i) {
            final char c = charArray[i];
            if (c >= '!' && c <= '\u0080' && !"(){}[]|".contains(Character.toString(c))) {
                out = String.valueOf(out) + new String(Character.toChars(c + '\ufee0'));
            }
            else {
                out = String.valueOf(out) + c;
            }
        }
        event.setMessage(out);
    }
}
