// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.minecraft.util.math.Vec3d;
import net.wurstclient.util.RotationUtils;
import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WEntityRenderer;
import net.wurstclient.compatibility.WItem;
import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.util.RenderUtils;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.update.Version;
import net.minecraft.entity.item.EntityItem;
import java.util.ArrayList;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "item esp", "ItemTracers", "item tracers" })
public final class ItemEspMod extends Hack implements UpdateListener, CameraTransformViewBobbingListener, RenderListener
{
    private final CheckboxSetting names;
    private final EnumSetting<Style> style;
    private final EnumSetting<BoxSize> boxSize;
    private int itemBox;
    private final ArrayList<EntityItem> items;
    
    public ItemEspMod() {
        super("ItemESP", "Highlights nearby items.");
        this.names = (new Version("1.12").isLowerThan("1.10") ? null : new CheckboxSetting("Show item names", true));
        this.style = new EnumSetting<Style>("Style", Style.values(), Style.BOXES);
        this.boxSize = new EnumSetting<BoxSize>("Box size", "§lAccurate§r mode shows the exact\nhitbox of each item.\n§lFancy§r mode shows larger boxes\nthat look better.", BoxSize.values(), BoxSize.FANCY);
        this.items = new ArrayList<EntityItem>();
        this.setCategory(Category.RENDER);
        if (this.names != null) {
            this.addSetting(this.names);
        }
        this.addSetting(this.style);
        this.addSetting(this.boxSize);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { ItemEspMod.WURST.hax.chestEspMod, ItemEspMod.WURST.hax.mobEspMod, ItemEspMod.WURST.hax.playerEspMod };
    }
    
    @Override
    public void onEnable() {
        ItemEspMod.EVENTS.add(UpdateListener.class, this);
        ItemEspMod.EVENTS.add(CameraTransformViewBobbingListener.class, this);
        ItemEspMod.EVENTS.add(RenderListener.class, this);
        GL11.glNewList(this.itemBox = GL11.glGenLists(1), 4864);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
        RenderUtils.drawOutlinedBox(new Box(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5));
        GL11.glEndList();
    }
    
    @Override
    public void onDisable() {
        ItemEspMod.EVENTS.remove(UpdateListener.class, this);
        ItemEspMod.EVENTS.remove(CameraTransformViewBobbingListener.class, this);
        ItemEspMod.EVENTS.remove(RenderListener.class, this);
        GL11.glDeleteLists(this.itemBox, 1);
    }
    
    @Override
    public void onUpdate() {
        this.items.clear();
        for (final Entity entity : WMinecraft.getWorld().loadedEntityList) {
            if (entity instanceof EntityItem) {
                this.items.add((EntityItem)entity);
            }
        }
    }
    
    @Override
    public void onCameraTransformViewBobbing(final CameraTransformViewBobbingEvent event) {
        if (this.style.getSelected().lines) {
            event.cancel();
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glPushMatrix();
        RenderUtils.applyRegionalRenderOffset();
        final BlockPos camPos = RenderUtils.getCameraBlockPos();
        final int regionX = (camPos.getX() >> 9) * 512;
        final int regionZ = (camPos.getZ() >> 9) * 512;
        this.renderBoxes(partialTicks, regionX, regionZ);
        if (this.style.getSelected().lines) {
            this.renderTracers(partialTicks, regionX, regionZ);
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    private void renderBoxes(final double partialTicks, final int regionX, final int regionZ) {
        final double extraSize = this.boxSize.getSelected().extraSize;
        for (final EntityItem e : this.items) {
            GL11.glPushMatrix();
            GL11.glTranslated(e.prevPosX + (e.posX - e.prevPosX) * partialTicks - regionX, e.prevPosY + (e.posY - e.prevPosY) * partialTicks, e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks - regionZ);
            if (this.style.getSelected().boxes) {
                GL11.glPushMatrix();
                GL11.glScaled(e.width + extraSize, e.height + extraSize, e.width + extraSize);
                GL11.glCallList(this.itemBox);
                GL11.glPopMatrix();
            }
            if (this.names != null && this.names.isChecked()) {
                final ItemStack stack = e.getEntityItem();
                WEntityRenderer.drawNameplate(ItemEspMod.MC.fontRendererObj, String.valueOf(WItem.getStackSize(stack)) + "x " + stack.getDisplayName(), 0.0f, 1.0f, 0.0f, 0, ItemEspMod.MC.getRenderManager().playerViewY, ItemEspMod.MC.getRenderManager().playerViewX, ItemEspMod.MC.gameSettings.thirdPersonView == 2, false);
                GL11.glDisable(2896);
            }
            GL11.glPopMatrix();
        }
    }
    
    private void renderTracers(final double partialTicks, final int regionX, final int regionZ) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
        final Vec3d start = RotationUtils.getClientLookVec().addVector(0.0, ItemEspMod.MC.player.getEyeHeight(), 0.0).addVector(ItemEspMod.MC.getRenderManager().renderPosX, ItemEspMod.MC.getRenderManager().renderPosY, ItemEspMod.MC.getRenderManager().renderPosZ);
        GL11.glBegin(1);
        for (final EntityItem e : this.items) {
            final Vec3d end = e.getEntityBoundingBox().getCenter().subtract(new Vec3d(e.posX, e.posY, e.posZ).subtract(e.prevPosX, e.prevPosY, e.prevPosZ).scale(1.0 - partialTicks));
            GL11.glVertex3d(start.xCoord - regionX, start.yCoord, start.zCoord - regionZ);
            GL11.glVertex3d(end.xCoord - regionX, end.yCoord, end.zCoord - regionZ);
        }
        GL11.glEnd();
    }
    
    private enum BoxSize
    {
        ACCURATE("ACCURATE", 0, "Accurate", 0.0), 
        FANCY("FANCY", 1, "Fancy", 0.1);
        
        private final String name;
        private final double extraSize;
        
        private BoxSize(final String name2, final int ordinal, final String name, final double extraSize) {
            this.name = name;
            this.extraSize = extraSize;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    private enum Style
    {
        BOXES("BOXES", 0, "Boxes only", true, false), 
        LINES("LINES", 1, "Lines only", false, true), 
        LINES_AND_BOXES("LINES_AND_BOXES", 2, "Lines and boxes", true, true);
        
        private final String name;
        private final boolean boxes;
        private final boolean lines;
        
        private Style(final String name2, final int ordinal, final String name, final boolean boxes, final boolean lines) {
            this.name = name;
            this.boxes = boxes;
            this.lines = lines;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
