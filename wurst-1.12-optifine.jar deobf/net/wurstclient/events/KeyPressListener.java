// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface KeyPressListener extends Listener
{
    void onKeyPress(final KeyPressEvent p0);
    
    public static class KeyPressEvent extends Event<KeyPressListener>
    {
        private final int keyCode;
        private final String keyName;
        
        public KeyPressEvent(final int keyCode, final String keyName) {
            this.keyCode = keyCode;
            this.keyName = keyName;
        }
        
        @Override
        public void fire(final ArrayList<KeyPressListener> listeners) {
            for (final KeyPressListener listener : listeners) {
                listener.onKeyPress(this);
            }
        }
        
        @Override
        public Class<KeyPressListener> getListenerType() {
            return KeyPressListener.class;
        }
        
        public int getKeyCode() {
            return this.keyCode;
        }
        
        public String getKeyName() {
            return this.keyName;
        }
    }
}
