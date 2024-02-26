// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;
import net.wurstclient.font.Fonts;
import org.lwjgl.opengl.GL11;
import net.wurstclient.WurstClient;
import net.minecraft.util.ResourceLocation;

public final class WurstLogo
{
    private static final ResourceLocation texture;
    
    static {
        texture = new ResourceLocation("wurst/wurst_128.png");
    }
    
    public void render() {
        if (!WurstClient.INSTANCE.special.wurstLogoSpf.isVisible()) {
            return;
        }
        final String version = this.getVersionString();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        if (WurstClient.INSTANCE.hax.rainbowUiMod.isActive()) {
            final float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
            GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5f);
        }
        else {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
        }
        this.drawQuads(0, 6, Fonts.segoe22.getStringWidth(version) + 76, 18);
        GL11.glEnable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        Fonts.segoe22.drawString(version, 74, 4, -16777216);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(WurstLogo.texture);
        Gui.drawModalRectWithCustomSizedTexture(0, 3, 0.0f, 0.0f, 72, 18, 72.0f, 18.0f);
    }
    
    private String getVersionString() {
        String version = "v6.35.3";
        version = String.valueOf(version) + " MC1.12";
        version = String.valueOf(version) + " OF";
        if (WurstClient.INSTANCE.updater.isOutdated()) {
            version = String.valueOf(version) + " (outdated)";
        }
        return version;
    }
    
    private void drawQuads(final int x1, final int y1, final int x2, final int y2) {
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y1);
        GL11.glVertex2i(x2, y1);
        GL11.glVertex2i(x2, y2);
        GL11.glVertex2i(x1, y2);
        GL11.glEnd();
    }
}
