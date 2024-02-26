// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.compatibility.WItem;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.util.RotationUtils;
import java.util.Iterator;
import net.minecraft.entity.item.EntityMinecartChest;
import java.util.Collection;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.wurstclient.compatibility.WTileEntity;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.tileentity.TileEntity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.minecraft.util.math.BlockPos;
import java.util.LinkedHashSet;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import java.util.ArrayList;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "ChestFinder", "StorageESP", "ChestTracers", "chest esp", "chest finder", "storage esp", "chest tracers" })
public final class ChestEspMod extends Hack implements UpdateListener, CameraTransformViewBobbingListener, RenderListener
{
    private final EnumSetting<Style> style;
    private final ArrayList<Box> basicChests;
    private final ArrayList<Box> trappedChests;
    private final ArrayList<Box> enderChests;
    private final ArrayList<Entity> minecarts;
    private int solidBox;
    private int outlinedBox;
    private int crossBox;
    private int normalChests;
    private int chestCounter;
    private TileEntityChest openChest;
    private final LinkedHashSet<BlockPos> emptyChests;
    private final LinkedHashSet<BlockPos> nonEmptyChests;
    
    public ChestEspMod() {
        super("ChestESP", "Highlights nearby chests.\n§agreen§r - normal chests\n§6orange§r - trapped chests\n§bcyan§r - ender chests\n§dpurple§r - shulker boxes\n[  ] - empty\n[X] - not empty");
        this.style = new EnumSetting<Style>("Style", Style.values(), Style.BOXES);
        this.basicChests = new ArrayList<Box>();
        this.trappedChests = new ArrayList<Box>();
        this.enderChests = new ArrayList<Box>();
        this.minecarts = new ArrayList<Entity>();
        this.emptyChests = new LinkedHashSet<BlockPos>();
        this.nonEmptyChests = new LinkedHashSet<BlockPos>();
        this.setCategory(Category.RENDER);
        this.addSetting(this.style);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { ChestEspMod.WURST.hax.autoStealMod, ChestEspMod.WURST.hax.itemEspMod };
    }
    
    @Override
    public String getRenderName() {
        return String.valueOf(this.getName()) + ((this.chestCounter == 1) ? " [1 chest]" : (" [" + this.chestCounter + " chests]"));
    }
    
    @Override
    public void onEnable() {
        ChestEspMod.EVENTS.add(UpdateListener.class, this);
        ChestEspMod.EVENTS.add(CameraTransformViewBobbingListener.class, this);
        ChestEspMod.EVENTS.add(RenderListener.class, this);
        this.emptyChests.clear();
        this.nonEmptyChests.clear();
        GL11.glNewList(this.solidBox = GL11.glGenLists(1), 4864);
        RenderUtils.drawSolidBox();
        GL11.glEndList();
        GL11.glNewList(this.outlinedBox = GL11.glGenLists(1), 4864);
        RenderUtils.drawOutlinedBox();
        GL11.glEndList();
        GL11.glNewList(this.crossBox = GL11.glGenLists(1), 4864);
        RenderUtils.drawCrossBox();
        GL11.glEndList();
        this.normalChests = GL11.glGenLists(1);
    }
    
    @Override
    public void onDisable() {
        ChestEspMod.EVENTS.remove(UpdateListener.class, this);
        ChestEspMod.EVENTS.remove(CameraTransformViewBobbingListener.class, this);
        ChestEspMod.EVENTS.remove(RenderListener.class, this);
        this.deleteDisplayLists();
    }
    
    private void deleteDisplayLists() {
        GL11.glDeleteLists(this.solidBox, 1);
        GL11.glDeleteLists(this.outlinedBox, 1);
        GL11.glDeleteLists(this.crossBox, 1);
        GL11.glDeleteLists(this.normalChests, 1);
    }
    
    @Override
    public void onUpdate() {
        final ArrayList<Box> basicNew = new ArrayList<Box>();
        final ArrayList<Box> basicEmpty = new ArrayList<Box>();
        final ArrayList<Box> basicNotEmpty = new ArrayList<Box>();
        final ArrayList<Box> trappedNew = new ArrayList<Box>();
        final ArrayList<Box> trappedEmpty = new ArrayList<Box>();
        final ArrayList<Box> trappedNotEmpty = new ArrayList<Box>();
        this.enderChests.clear();
        for (final TileEntity tileEntity : WMinecraft.getWorld().loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest) {
                final TileEntityChest chest = (TileEntityChest)tileEntity;
                if (chest.adjacentChestXPos != null) {
                    continue;
                }
                if (chest.adjacentChestZPos != null) {
                    continue;
                }
                Box bb = WBlock.getBoundingBox(chest.getPos());
                if (bb == null) {
                    continue;
                }
                if (chest.adjacentChestXNeg != null) {
                    final BlockPos pos2 = chest.adjacentChestXNeg.getPos();
                    final Box bb2 = WBlock.getBoundingBox(pos2);
                    bb = bb.union(bb2);
                }
                else if (chest.adjacentChestZNeg != null) {
                    final BlockPos pos2 = chest.adjacentChestZNeg.getPos();
                    final Box bb2 = WBlock.getBoundingBox(pos2);
                    bb = bb.union(bb2);
                }
                final boolean trapped = WTileEntity.isTrappedChest(chest);
                if (this.emptyChests.contains(chest.getPos())) {
                    if (trapped) {
                        trappedEmpty.add(bb);
                    }
                    else {
                        basicEmpty.add(bb);
                    }
                }
                else if (this.nonEmptyChests.contains(chest.getPos())) {
                    if (trapped) {
                        trappedNotEmpty.add(bb);
                    }
                    else {
                        basicNotEmpty.add(bb);
                    }
                }
                else if (trapped) {
                    trappedNew.add(bb);
                }
                else {
                    basicNew.add(bb);
                }
            }
            else {
                if (!(tileEntity instanceof TileEntityEnderChest)) {
                    continue;
                }
                final Box bb3 = WBlock.getBoundingBox(tileEntity.getPos());
                this.enderChests.add(bb3);
            }
        }
        this.basicChests.clear();
        this.basicChests.addAll(basicNew);
        this.basicChests.addAll(basicEmpty);
        this.basicChests.addAll(basicNotEmpty);
        this.trappedChests.clear();
        this.trappedChests.addAll(trappedNew);
        this.trappedChests.addAll(trappedEmpty);
        this.trappedChests.addAll(trappedNotEmpty);
        final BlockPos camPos = RenderUtils.getCameraBlockPos();
        final int regionX = (camPos.getX() >> 9) * 512;
        final int regionZ = (camPos.getZ() >> 9) * 512;
        GL11.glNewList(this.normalChests, 4864);
        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.25f);
        this.renderBoxes(basicNew, this.solidBox, regionX, regionZ);
        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
        this.renderBoxes(basicNew, this.outlinedBox, regionX, regionZ);
        this.renderBoxes(basicEmpty, this.outlinedBox, regionX, regionZ);
        this.renderBoxes(basicNotEmpty, this.outlinedBox, regionX, regionZ);
        this.renderBoxes(basicNotEmpty, this.crossBox, regionX, regionZ);
        GL11.glColor4f(1.0f, 0.5f, 0.0f, 0.25f);
        this.renderBoxes(trappedNew, this.solidBox, regionX, regionZ);
        GL11.glColor4f(1.0f, 0.5f, 0.0f, 0.5f);
        this.renderBoxes(trappedNew, this.outlinedBox, regionX, regionZ);
        this.renderBoxes(trappedEmpty, this.outlinedBox, regionX, regionZ);
        this.renderBoxes(trappedNotEmpty, this.outlinedBox, regionX, regionZ);
        this.renderBoxes(trappedNotEmpty, this.crossBox, regionX, regionZ);
        GL11.glColor4f(0.0f, 1.0f, 1.0f, 0.25f);
        this.renderBoxes(this.enderChests, this.solidBox, regionX, regionZ);
        GL11.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        this.renderBoxes(this.enderChests, this.outlinedBox, regionX, regionZ);
        GL11.glEndList();
        this.minecarts.clear();
        for (final Entity entity : WMinecraft.getWorld().loadedEntityList) {
            if (entity instanceof EntityMinecartChest) {
                this.minecarts.add(entity);
            }
        }
        this.chestCounter = basicNew.size() + basicEmpty.size() + basicNotEmpty.size() + trappedNew.size() + trappedEmpty.size() + trappedNotEmpty.size() + this.enderChests.size() + this.minecarts.size();
    }
    
    @Override
    public void onCameraTransformViewBobbing(final CameraTransformViewBobbingEvent event) {
        if (this.style.getSelected().lines) {
            event.cancel();
        }
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
        RenderUtils.applyRegionalRenderOffset();
        final ArrayList<Box> minecartBoxes = new ArrayList<Box>(this.minecarts.size());
        this.minecarts.forEach(e -> {
            final double offsetX = -(e.posX - e.lastTickPosX) + (e.posX - e.lastTickPosX) * n;
            final double offsetY = -(e.posY - e.lastTickPosY) + (e.posY - e.lastTickPosY) * n;
            final double offsetZ = -(e.posZ - e.lastTickPosZ) + (e.posZ - e.lastTickPosZ) * n;
            list.add(e.boundingBox.offset(offsetX, offsetY, offsetZ));
            return;
        });
        final BlockPos camPos = RenderUtils.getCameraBlockPos();
        final int regionX = (camPos.getX() >> 9) * 512;
        final int regionZ = (camPos.getZ() >> 9) * 512;
        if (this.style.getSelected().boxes) {
            GL11.glCallList(this.normalChests);
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.25f);
            this.renderBoxes(minecartBoxes, this.solidBox, regionX, regionZ);
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
            this.renderBoxes(minecartBoxes, this.outlinedBox, regionX, regionZ);
        }
        if (this.style.getSelected().lines) {
            final Vec3d start = RotationUtils.getClientLookVec().addVector(0.0, ChestEspMod.MC.player.getEyeHeight(), 0.0).addVector(ChestEspMod.MC.getRenderManager().renderPosX, ChestEspMod.MC.getRenderManager().renderPosY, ChestEspMod.MC.getRenderManager().renderPosZ);
            GL11.glBegin(1);
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
            this.renderLines(start, this.basicChests, regionX, regionZ);
            this.renderLines(start, minecartBoxes, regionX, regionZ);
            GL11.glColor4f(1.0f, 0.5f, 0.0f, 0.5f);
            this.renderLines(start, this.trappedChests, regionX, regionZ);
            GL11.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
            this.renderLines(start, this.enderChests, regionX, regionZ);
            GL11.glEnd();
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    private void renderBoxes(final ArrayList<Box> boxes, final int displayList, final int regionX, final int regionZ) {
        for (final Box bb : boxes) {
            GL11.glPushMatrix();
            GL11.glTranslated(bb.minX - regionX, bb.minY, bb.minZ - regionZ);
            GL11.glScaled(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ);
            GL11.glCallList(displayList);
            GL11.glPopMatrix();
        }
    }
    
    private void renderLines(final Vec3d start, final ArrayList<Box> boxes, final int regionX, final int regionZ) {
        for (final Box bb : boxes) {
            final Vec3d end = bb.getCenter();
            GL11.glVertex3d(start.xCoord - regionX, start.yCoord, start.zCoord - regionZ);
            GL11.glVertex3d(end.xCoord - regionX, end.yCoord, end.zCoord - regionZ);
        }
    }
    
    public void openChest(final BlockPos pos) {
        final TileEntity tileEntity = WMinecraft.getWorld().getTileEntity(pos);
        if (tileEntity instanceof TileEntityChest) {
            this.openChest = (TileEntityChest)tileEntity;
            if (this.openChest.adjacentChestXPos != null) {
                this.openChest = this.openChest.adjacentChestXPos;
            }
            if (this.openChest.adjacentChestZPos != null) {
                this.openChest = this.openChest.adjacentChestZPos;
            }
        }
    }
    
    public void closeChest(final Container chest) {
        if (this.openChest == null) {
            return;
        }
        boolean empty = true;
        for (int i = 0; i < chest.inventorySlots.size() - 36; ++i) {
            if (!WItem.isNullOrEmpty(chest.inventorySlots.get(i).getStack())) {
                empty = false;
                break;
            }
        }
        final BlockPos pos = this.openChest.getPos();
        if (empty) {
            if (!this.emptyChests.contains(pos)) {
                this.emptyChests.add(pos);
            }
            this.nonEmptyChests.remove(pos);
        }
        else {
            if (!this.nonEmptyChests.contains(pos)) {
                this.nonEmptyChests.add(pos);
            }
            this.emptyChests.remove(pos);
        }
        this.openChest = null;
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
