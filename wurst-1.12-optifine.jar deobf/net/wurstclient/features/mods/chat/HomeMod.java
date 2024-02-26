// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.chat;

import net.wurstclient.util.ChatUtils;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses
public final class HomeMod extends Hack implements UpdateListener, ChatInputListener
{
    private int disableTimer;
    
    public HomeMod() {
        super("/home", "Types §l/home§r instantly.");
        this.setCategory(Category.CHAT);
    }
    
    @Override
    public void onEnable() {
        this.disableTimer = 0;
        HomeMod.EVENTS.add(ChatInputListener.class, this);
        HomeMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        HomeMod.EVENTS.remove(ChatInputListener.class, this);
        HomeMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (this.disableTimer >= 4) {
            this.setEnabled(false);
        }
        else if (this.disableTimer == 0) {
            HomeMod.MC.player.sendChatMessage("/home");
        }
        ++this.disableTimer;
    }
    
    @Override
    public void onReceivedMessage(final ChatInputEvent event) {
        final String message = event.getComponent().getUnformattedText();
        if (message.startsWith("§c[§6Wurst§c]§f ")) {
            return;
        }
        if (message.toLowerCase().contains("/help") || message.toLowerCase().contains("permission")) {
            event.cancel();
            ChatUtils.error("This server doesn't have /home.");
        }
    }
}
