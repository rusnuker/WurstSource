// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.RenderListener;
import net.wurstclient.features.Hack;

@Bypasses
public final class OverlayMod extends Hack implements RenderListener
{
    public OverlayMod() {
        super("Overlay", "Renders the Nuker animation whenever you mine a block.");
        this.setCategory(Category.RENDER);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { OverlayMod.WURST.hax.nukerMod };
    }
    
    @Override
    public void onEnable() {
        OverlayMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        OverlayMod.EVENTS.remove(RenderListener.class, this);
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (OverlayMod.MC.objectMouseOver == null) {
            return;
        }
        final BlockPos pos = OverlayMod.MC.objectMouseOver.getBlockPos();
        if (pos == null) {
            return;
        }
        if (!OverlayMod.MC.playerController.getIsHittingBlock()) {
            return;
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        GL11.glTranslated(-OverlayMod.MC.getRenderManager().renderPosX, -OverlayMod.MC.getRenderManager().renderPosY, -OverlayMod.MC.getRenderManager().renderPosZ);
        GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        final float progress = OverlayMod.MC.playerController.curBlockDamageMP;
        GL11.glTranslated(0.5, 0.5, 0.5);
        GL11.glScaled((double)progress, (double)progress, (double)progress);
        GL11.glTranslated(-0.5, -0.5, -0.5);
        final float red = progress * 2.0f;
        final float green = 2.0f - red;
        GL11.glColor4f(red, green, 0.0f, 0.25f);
        RenderUtils.drawSolidBox();
        GL11.glColor4f(red, green, 0.0f, 0.5f);
        RenderUtils.drawOutlinedBox();
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
}
