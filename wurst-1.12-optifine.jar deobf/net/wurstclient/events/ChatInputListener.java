// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.client.gui.ChatLine;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface ChatInputListener extends Listener
{
    void onReceivedMessage(final ChatInputEvent p0);
    
    public static class ChatInputEvent extends CancellableEvent<ChatInputListener>
    {
        private ITextComponent component;
        private List<ChatLine> chatLines;
        
        public ChatInputEvent(final ITextComponent component, final List<ChatLine> chatLines) {
            this.component = component;
            this.chatLines = chatLines;
        }
        
        public ITextComponent getComponent() {
            return this.component;
        }
        
        public void setComponent(final ITextComponent component) {
            this.component = component;
        }
        
        public List<ChatLine> getChatLines() {
            return this.chatLines;
        }
        
        @Override
        public void fire(final ArrayList<ChatInputListener> listeners) {
            for (final ChatInputListener listener : listeners) {
                listener.onReceivedMessage(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<ChatInputListener> getListenerType() {
            return ChatInputListener.class;
        }
    }
}
