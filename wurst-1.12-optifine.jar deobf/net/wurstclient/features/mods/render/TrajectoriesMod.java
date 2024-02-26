// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.util.RenderUtils;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPotion;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.wurstclient.compatibility.WMath;
import net.minecraft.item.ItemBow;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.features.Hack;

@SearchTags({ "ArrowTrajectories", "ArrowPrediction", "aim assist", "arrow trajectories" })
@Bypasses
public final class TrajectoriesMod extends Hack implements RenderListener
{
    public TrajectoriesMod() {
        super("Trajectories", "Predicts the flight path of arrows and throwable items.");
        this.setCategory(Category.RENDER);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { TrajectoriesMod.WURST.hax.bowAimbotMod, TrajectoriesMod.WURST.hax.fastBowMod };
    }
    
    @Override
    public void onEnable() {
        TrajectoriesMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        TrajectoriesMod.EVENTS.remove(RenderListener.class, this);
    }
    
    @Override
    public void onRender(final float partialTicks) {
        final EntityPlayerSP player = TrajectoriesMod.MC.player;
        final ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            return;
        }
        if (!WItem.isThrowable(stack)) {
            return;
        }
        final boolean usingBow = stack.getItem() instanceof ItemBow;
        double arrowPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * TrajectoriesMod.MC.timer.renderPartialTicks - WMath.cos((float)Math.toRadians(player.rotationYaw)) * 0.16f;
        double arrowPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * Minecraft.getMinecraft().timer.renderPartialTicks + player.getEyeHeight() - 0.1;
        double arrowPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * Minecraft.getMinecraft().timer.renderPartialTicks - WMath.sin((float)Math.toRadians(player.rotationYaw)) * 0.16f;
        final float arrowMotionFactor = usingBow ? 1.0f : 0.4f;
        final float yaw = (float)Math.toRadians(player.rotationYaw);
        final float pitch = (float)Math.toRadians(player.rotationPitch);
        float arrowMotionX = -WMath.sin(yaw) * WMath.cos(pitch) * arrowMotionFactor;
        float arrowMotionY = -WMath.sin(pitch) * arrowMotionFactor;
        float arrowMotionZ = WMath.cos(yaw) * WMath.cos(pitch) * arrowMotionFactor;
        final double arrowMotion = Math.sqrt(arrowMotionX * arrowMotionX + arrowMotionY * arrowMotionY + arrowMotionZ * arrowMotionZ);
        arrowMotionX /= (float)arrowMotion;
        arrowMotionY /= (float)arrowMotion;
        arrowMotionZ /= (float)arrowMotion;
        if (usingBow) {
            float bowPower = (72000 - player.getItemInUseCount()) / 20.0f;
            bowPower = (bowPower * bowPower + bowPower * 2.0f) / 3.0f;
            if (bowPower > 1.0f || bowPower <= 0.1f) {
                bowPower = 1.0f;
            }
            bowPower *= 3.0f;
            arrowMotionX *= bowPower;
            arrowMotionY *= bowPower;
            arrowMotionZ *= bowPower;
        }
        else {
            arrowMotionX *= 1.5;
            arrowMotionY *= 1.5;
            arrowMotionZ *= 1.5;
        }
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        final RenderManager renderManager = TrajectoriesMod.MC.getRenderManager();
        final double gravity = usingBow ? 0.05 : ((stack.getItem() instanceof ItemPotion) ? 0.4 : ((stack.getItem() instanceof ItemFishingRod) ? 0.15 : 0.03));
        final Vec3d playerVector = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.75f);
        GL11.glBegin(3);
        for (int i = 0; i < 1000; ++i) {
            GL11.glVertex3d(arrowPosX - renderManager.renderPosX, arrowPosY - renderManager.renderPosY, arrowPosZ - renderManager.renderPosZ);
            arrowPosX += arrowMotionX * 0.1;
            arrowPosY += arrowMotionY * 0.1;
            arrowPosZ += arrowMotionZ * 0.1;
            arrowMotionX *= (float)0.999;
            arrowMotionY *= (float)0.999;
            arrowMotionZ *= (float)0.999;
            arrowMotionY -= (float)(gravity * 0.1);
            if (WMinecraft.getWorld().rayTraceBlocks(playerVector, new Vec3d(arrowPosX, arrowPosY, arrowPosZ)) != null) {
                break;
            }
        }
        GL11.glEnd();
        final double renderX = arrowPosX - renderManager.renderPosX;
        final double renderY = arrowPosY - renderManager.renderPosY;
        final double renderZ = arrowPosZ - renderManager.renderPosZ;
        GL11.glPushMatrix();
        GL11.glTranslated(renderX - 0.5, renderY - 0.5, renderZ - 0.5);
        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.25f);
        RenderUtils.drawSolidBox();
        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderUtils.drawOutlinedBox();
        GL11.glPopMatrix();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }
}
