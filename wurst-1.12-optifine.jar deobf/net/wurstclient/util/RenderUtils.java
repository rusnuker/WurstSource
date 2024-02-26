// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util;

import net.minecraft.util.math.Vec3d;
import java.awt.Color;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Box;

public class RenderUtils
{
    private static final Box DEFAULT_AABB;
    
    static {
        DEFAULT_AABB = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }
    
    public static void scissorBox(final int x, final int y, final int xend, final int yend) {
        final int width = xend - x;
        final int height = yend - y;
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final int factor = sr.getScaleFactor();
        final int bottomY = Minecraft.getMinecraft().currentScreen.height - yend;
        GL11.glScissor(x * factor, bottomY * factor, width * factor, height * factor);
    }
    
    public static void applyRenderOffset() {
        final RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        GL11.glTranslated(-rm.renderPosX, -rm.renderPosY, -rm.renderPosZ);
    }
    
    public static void applyRegionalRenderOffset() {
        final RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        final BlockPos blockPos = getCameraBlockPos();
        final int regionX = (blockPos.getX() >> 9) * 512;
        final int regionZ = (blockPos.getZ() >> 9) * 512;
        GL11.glTranslated(regionX - rm.renderPosX, -rm.renderPosY, regionZ - rm.renderPosZ);
    }
    
    public static BlockPos getCameraBlockPos() {
        final RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        return new BlockPos(rm.renderPosX, rm.renderPosY, rm.renderPosZ);
    }
    
    public static void setColor(final Color c) {
        GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
    }
    
    public static void drawSolidBox() {
        drawSolidBox(RenderUtils.DEFAULT_AABB);
    }
    
    public static void drawSolidBox(final Box bb) {
        GL11.glBegin(7);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glEnd();
    }
    
    public static void drawOutlinedBox() {
        drawOutlinedBox(RenderUtils.DEFAULT_AABB);
    }
    
    public static void drawOutlinedBox(final Box bb) {
        GL11.glBegin(1);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glEnd();
    }
    
    public static void drawCrossBox() {
        drawCrossBox(RenderUtils.DEFAULT_AABB);
    }
    
    public static void drawCrossBox(final Box bb) {
        GL11.glBegin(1);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glEnd();
    }
    
    public static void drawNode(final Box bb) {
        final double midX = (bb.minX + bb.maxX) / 2.0;
        final double midY = (bb.minY + bb.maxY) / 2.0;
        final double midZ = (bb.minZ + bb.maxZ) / 2.0;
        GL11.glVertex3d(midX, midY, bb.maxZ);
        GL11.glVertex3d(bb.minX, midY, midZ);
        GL11.glVertex3d(bb.minX, midY, midZ);
        GL11.glVertex3d(midX, midY, bb.minZ);
        GL11.glVertex3d(midX, midY, bb.minZ);
        GL11.glVertex3d(bb.maxX, midY, midZ);
        GL11.glVertex3d(bb.maxX, midY, midZ);
        GL11.glVertex3d(midX, midY, bb.maxZ);
        GL11.glVertex3d(midX, bb.maxY, midZ);
        GL11.glVertex3d(bb.maxX, midY, midZ);
        GL11.glVertex3d(midX, bb.maxY, midZ);
        GL11.glVertex3d(bb.minX, midY, midZ);
        GL11.glVertex3d(midX, bb.maxY, midZ);
        GL11.glVertex3d(midX, midY, bb.minZ);
        GL11.glVertex3d(midX, bb.maxY, midZ);
        GL11.glVertex3d(midX, midY, bb.maxZ);
        GL11.glVertex3d(midX, bb.minY, midZ);
        GL11.glVertex3d(bb.maxX, midY, midZ);
        GL11.glVertex3d(midX, bb.minY, midZ);
        GL11.glVertex3d(bb.minX, midY, midZ);
        GL11.glVertex3d(midX, bb.minY, midZ);
        GL11.glVertex3d(midX, midY, bb.minZ);
        GL11.glVertex3d(midX, bb.minY, midZ);
        GL11.glVertex3d(midX, midY, bb.maxZ);
    }
    
    public static void drawArrow(final Vec3d from, final Vec3d to) {
        final double startX = from.xCoord;
        final double startY = from.yCoord;
        final double startZ = from.zCoord;
        final double endX = to.xCoord;
        final double endY = to.yCoord;
        final double endZ = to.zCoord;
        GL11.glPushMatrix();
        GL11.glBegin(1);
        GL11.glVertex3d(startX, startY, startZ);
        GL11.glVertex3d(endX, endY, endZ);
        GL11.glEnd();
        GL11.glTranslated(endX, endY, endZ);
        GL11.glScaled(0.1, 0.1, 0.1);
        final double angleX = Math.atan2(endY - startY, startZ - endZ);
        GL11.glRotated(Math.toDegrees(angleX) + 90.0, 1.0, 0.0, 0.0);
        final double angleZ = Math.atan2(endX - startX, Math.sqrt(Math.pow(endY - startY, 2.0) + Math.pow(endZ - startZ, 2.0)));
        GL11.glRotated(Math.toDegrees(angleZ), 0.0, 0.0, 1.0);
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
}
