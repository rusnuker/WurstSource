// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import java.util.Iterator;
import net.wurstclient.util.RenderUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WMath;
import org.lwjgl.opengl.GL11;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.minecraft.util.math.Box;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.features.Hack;

@SearchTags({ "prophunt esp" })
@Bypasses
public final class ProphuntEspMod extends Hack implements RenderListener
{
    private static final Box FAKE_BLOCK_BOX;
    
    static {
        FAKE_BLOCK_BOX = new Box(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5);
    }
    
    public ProphuntEspMod() {
        super("ProphuntESP", "Allows you to see fake blocks in Prophunt.");
        this.setCategory(Category.RENDER);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { ProphuntEspMod.WURST.hax.playerEspMod, ProphuntEspMod.WURST.hax.mobEspMod };
    }
    
    @Override
    public void onEnable() {
        ProphuntEspMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        ProphuntEspMod.EVENTS.remove(RenderListener.class, this);
    }
    
    @Override
    public void onRender(final float partialTicks) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        GL11.glTranslated(-ProphuntEspMod.MC.getRenderManager().renderPosX, -ProphuntEspMod.MC.getRenderManager().renderPosY, -ProphuntEspMod.MC.getRenderManager().renderPosZ);
        final float alpha = 0.5f + 0.25f * WMath.sin(System.currentTimeMillis() % 1000L / 500.0f * 3.1415927f);
        GL11.glColor4f(1.0f, 0.0f, 0.0f, alpha);
        for (final Entity entity : WMinecraft.getWorld().loadedEntityList) {
            if (!(entity instanceof EntityLiving)) {
                continue;
            }
            if (!entity.isInvisible()) {
                continue;
            }
            if (ProphuntEspMod.MC.player.getDistanceSqToEntity(entity) < 0.25) {
                continue;
            }
            GL11.glPushMatrix();
            GL11.glTranslated(entity.posX, entity.posY, entity.posZ);
            RenderUtils.drawOutlinedBox(ProphuntEspMod.FAKE_BLOCK_BOX);
            RenderUtils.drawSolidBox(ProphuntEspMod.FAKE_BLOCK_BOX);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
}
