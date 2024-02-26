// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.util.math.MathHelper;

public final class WMath
{
    public static int clamp(final int num, final int min, final int max) {
        return (num < min) ? min : ((num > max) ? max : num);
    }
    
    public static float clamp(final float num, final float min, final float max) {
        return (num < min) ? min : ((num > max) ? max : num);
    }
    
    public static double clamp(final double num, final double min, final double max) {
        return (num < min) ? min : ((num > max) ? max : num);
    }
    
    public static float sin(final float value) {
        return MathHelper.sin(value);
    }
    
    public static float cos(final float value) {
        return MathHelper.cos(value);
    }
    
    public static float wrapDegrees(final float value) {
        return MathHelper.wrapDegrees(value);
    }
    
    public static double wrapDegrees(final double value) {
        return MathHelper.wrapDegrees(value);
    }
}
