// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import java.io.IOException;
import net.minecraft.client.entity.AbstractClientPlayer;
import java.util.HashSet;
import net.minecraft.client.Minecraft;

public final class AltRenderer
{
    private static final Minecraft mc;
    private static final HashSet<String> loadedSkins;
    
    static {
        mc = Minecraft.getMinecraft();
        loadedSkins = new HashSet<String>();
    }
    
    private static void bindSkinTexture(final String name) {
        final ResourceLocation location = AbstractClientPlayer.getLocationSkin(name);
        if (AltRenderer.loadedSkins.contains(name)) {
            AltRenderer.mc.getTextureManager().bindTexture(location);
            return;
        }
        try {
            final ThreadDownloadImageData img = AbstractClientPlayer.getDownloadImageSkin(location, name);
            img.loadTexture(AltRenderer.mc.getResourceManager());
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        AltRenderer.mc.getTextureManager().bindTexture(location);
        AltRenderer.loadedSkins.add(name);
    }
    
    public static void drawAltFace(final String name, final int x, final int y, final int w, final int h, final boolean selected) {
        try {
            bindSkinTexture(name);
            GL11.glEnable(3042);
            if (selected) {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            else {
                GL11.glColor4f(0.9f, 0.9f, 0.9f, 1.0f);
            }
            float fw = 192.0f;
            float fh = 192.0f;
            float u = 24.0f;
            float v = 24.0f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            fw = 192.0f;
            fh = 192.0f;
            u = 120.0f;
            v = 24.0f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            GL11.glDisable(3042);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void drawAltBody(final String name, int x, int y, final int width, final int height) {
        try {
            bindSkinTexture(name);
            final boolean slim = DefaultPlayerSkin.getSkinType(EntityPlayer.getOfflineUUID(name)).equals("slim");
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            x += width / 4;
            y += 0;
            int w = width / 2;
            int h = height / 4;
            final float fw = (float)(height * 2);
            final float fh = (float)(height * 2);
            float u = (float)(height / 4);
            float v = (float)(height / 4);
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 2;
            h = height / 4;
            u = (float)(height / 4 * 5);
            v = (float)(height / 4);
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += height / 4;
            w = width / 2;
            h = height / 8 * 3;
            u = height / 4 * 2.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 2;
            h = height / 8 * 3;
            u = height / 4 * 2.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x -= width / 16 * (slim ? 3 : 4);
            y += (slim ? (height / 32) : 0);
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * 5.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * 5.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += width / 16 * (slim ? 11 : 12);
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * 5.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * 5.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x -= width / 2;
            y += height / 32 * (slim ? 11 : 12);
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 0.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 0.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += width / 4;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 0.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 0.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            GL11.glDisable(3042);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void drawAltBack(final String name, int x, int y, final int width, final int height) {
        try {
            bindSkinTexture(name);
            final boolean slim = DefaultPlayerSkin.getSkinType(EntityPlayer.getOfflineUUID(name)).equals("slim");
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            x += width / 4;
            y += 0;
            int w = width / 2;
            int h = height / 4;
            final float fw = (float)(height * 2);
            final float fh = (float)(height * 2);
            float u = (float)(height / 4 * 3);
            float v = (float)(height / 4);
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 2;
            h = height / 4;
            u = (float)(height / 4 * 7);
            v = (float)(height / 4);
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += height / 4;
            w = width / 2;
            h = height / 8 * 3;
            u = (float)(height / 4 * 4);
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 2;
            h = height / 8 * 3;
            u = (float)(height / 4 * 4);
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x -= width / 16 * (slim ? 3 : 4);
            y += (slim ? (height / 32) : 0);
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * (slim ? 6.375f : 6.5f);
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * (slim ? 6.375f : 6.5f);
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += width / 16 * (slim ? 11 : 12);
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * (slim ? 6.375f : 6.5f);
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * (slim ? 6.375f : 6.5f);
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x -= width / 2;
            y += height / 32 * (slim ? 11 : 12);
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 1.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 1.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += width / 4;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 1.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 1.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            GL11.glDisable(3042);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
