// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.ai;

import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.BlockPos;

public final class PathRenderer
{
    public static void renderArrow(final BlockPos start, final BlockPos end) {
        final int startX = start.getX();
        final int startY = start.getY();
        final int startZ = start.getZ();
        final int endX = end.getX();
        final int endY = end.getY();
        final int endZ = end.getZ();
        GL11.glPushMatrix();
        GL11.glBegin(1);
        GL11.glVertex3d((double)startX, (double)startY, (double)startZ);
        GL11.glVertex3d((double)endX, (double)endY, (double)endZ);
        GL11.glEnd();
        GL11.glTranslated((double)endX, (double)endY, (double)endZ);
        final double scale = 0.0625;
        GL11.glScaled(scale, scale, scale);
        GL11.glRotated(Math.toDegrees(Math.atan2(endY - startY, startZ - endZ)) + 90.0, 1.0, 0.0, 0.0);
        GL11.glRotated(Math.toDegrees(Math.atan2(endX - startX, Math.sqrt(Math.pow(endY - startY, 2.0) + Math.pow(endZ - startZ, 2.0)))), 0.0, 0.0, 1.0);
        GL11.glBegin(1);
        GL11.glVertex3d(0.0, 2.0, 1.0);
        GL11.glVertex3d(-1.0, 2.0, 0.0);
        GL11.glVertex3d(-1.0, 2.0, 0.0);
        GL11.glVertex3d(0.0, 2.0, -1.0);
        GL11.glVertex3d(0.0, 2.0, -1.0);
        GL11.glVertex3d(1.0, 2.0, 0.0);
        GL11.glVertex3d(1.0, 2.0, 0.0);
        GL11.glVertex3d(0.0, 2.0, 1.0);
        GL11.glVertex3d(1.0, 2.0, 0.0);
        GL11.glVertex3d(-1.0, 2.0, 0.0);
        GL11.glVertex3d(0.0, 2.0, 1.0);
        GL11.glVertex3d(0.0, 2.0, -1.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(1.0, 2.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(-1.0, 2.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 2.0, -1.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 2.0, 1.0);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    
    public static void renderNode(final BlockPos pos) {
        GL11.glPushMatrix();
        GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        GL11.glScaled(0.1, 0.1, 0.1);
        GL11.glBegin(1);
        GL11.glVertex3d(0.0, 0.0, 1.0);
        GL11.glVertex3d(-1.0, 0.0, 0.0);
        GL11.glVertex3d(-1.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, -1.0);
        GL11.glVertex3d(0.0, 0.0, -1.0);
        GL11.glVertex3d(1.0, 0.0, 0.0);
        GL11.glVertex3d(1.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 1.0);
        GL11.glVertex3d(0.0, 1.0, 0.0);
        GL11.glVertex3d(1.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 1.0, 0.0);
        GL11.glVertex3d(-1.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 1.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, -1.0);
        GL11.glVertex3d(0.0, 1.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 1.0);
        GL11.glVertex3d(0.0, -1.0, 0.0);
        GL11.glVertex3d(1.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, -1.0, 0.0);
        GL11.glVertex3d(-1.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, -1.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, -1.0);
        GL11.glVertex3d(0.0, -1.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 1.0);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
}
