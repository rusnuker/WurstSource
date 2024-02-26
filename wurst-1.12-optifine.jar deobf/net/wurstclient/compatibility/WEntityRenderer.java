// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.gui.FontRenderer;

public final class WEntityRenderer
{
    public static void drawNameplate(final FontRenderer fontRendererIn, final String str, final float x, final float y, final float z, final int verticalShift, final float viewerYaw, final float viewerPitch, final boolean isThirdPersonFrontal, final boolean isSneaking) {
        EntityRenderer.drawNameplate(fontRendererIn, str, x, y, z, verticalShift, viewerYaw, viewerPitch, isThirdPersonFrontal, isSneaking);
    }
}
