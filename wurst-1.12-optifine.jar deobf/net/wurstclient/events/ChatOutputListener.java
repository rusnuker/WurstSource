// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface ChatOutputListener extends Listener
{
    void onSentMessage(final ChatOutputEvent p0);
    
    public static class ChatOutputEvent extends CancellableEvent<ChatOutputListener>
    {
        private String message;
        private boolean automatic;
        
        public ChatOutputEvent(final String message, final boolean automatic) {
            this.message = message;
            this.automatic = automatic;
        }
        
        public String getMessage() {
            return this.message;
        }
        
        public void setMessage(final String message) {
            this.message = message;
        }
        
        public boolean isAutomatic() {
            return this.automatic;
        }
        
        @Override
        public void fire(final ArrayList<ChatOutputListener> listeners) {
            for (final ChatOutputListener listener : listeners) {
                listener.onSentMessage(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<ChatOutputListener> getListenerType() {
            return ChatOutputListener.class;
        }
    }
}
