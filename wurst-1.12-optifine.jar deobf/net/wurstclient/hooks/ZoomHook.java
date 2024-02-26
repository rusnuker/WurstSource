// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.hooks;

import net.wurstclient.WurstClient;

public class ZoomHook
{
    public static float changeFovBasedOnZoom(final float fov) {
        return WurstClient.INSTANCE.special.zoomSpf.changeFovBasedOnZoom(fov);
    }
}
