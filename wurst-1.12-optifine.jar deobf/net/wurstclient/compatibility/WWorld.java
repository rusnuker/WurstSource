// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.util.math.Box;

public final class WWorld
{
    public static boolean collidesWithAnyBlock(final Box bb) {
        return WMinecraft.getWorld().collidesWithAnyBlock(bb);
    }
}
