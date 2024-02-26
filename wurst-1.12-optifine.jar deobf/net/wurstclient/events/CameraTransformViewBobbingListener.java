// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.events;

import java.util.Iterator;
import java.util.ArrayList;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface CameraTransformViewBobbingListener extends Listener
{
    void onCameraTransformViewBobbing(final CameraTransformViewBobbingEvent p0);
    
    public static class CameraTransformViewBobbingEvent extends CancellableEvent<CameraTransformViewBobbingListener>
    {
        @Override
        public void fire(final ArrayList<CameraTransformViewBobbingListener> listeners) {
            for (final CameraTransformViewBobbingListener listener : listeners) {
                listener.onCameraTransformViewBobbing(this);
                if (this.isCancelled()) {
                    break;
                }
            }
        }
        
        @Override
        public Class<CameraTransformViewBobbingListener> getListenerType() {
            return CameraTransformViewBobbingListener.class;
        }
    }
}
