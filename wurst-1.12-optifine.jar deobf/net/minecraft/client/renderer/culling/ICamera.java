// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.culling;

import net.minecraft.util.math.Box;

public interface ICamera
{
    boolean isBoundingBoxInFrustum(final Box p0);
    
    void setPosition(final double p0, final double p1, final double p2);
}
