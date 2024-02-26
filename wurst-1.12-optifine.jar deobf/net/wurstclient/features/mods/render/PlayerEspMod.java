// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.minecraft.util.math.Vec3d;
import net.wurstclient.util.RotationUtils;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import java.util.stream.Stream;
import net.minecraft.world.World;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import net.wurstclient.util.EntityFakePlayer;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.util.RenderUtils;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "player esp", "PlayerTracers", "player tracers" })
public final class PlayerEspMod extends Hack implements UpdateListener, CameraTransformViewBobbingListener, RenderListener
{
    private final EnumSetting<Style> style;
    private final EnumSetting<BoxSize> boxSize;
    private final CheckboxSetting filterSleeping;
    private final CheckboxSetting filterInvisible;
    private int playerBox;
    private final ArrayList<EntityPlayer> players;
    
    public PlayerEspMod() {
        super("PlayerESP", "Highlights nearby players.\nESP boxes of friends will appear in blue.");
        this.style = new EnumSetting<Style>("Style", Style.values(), Style.LINES_AND_BOXES);
        this.boxSize = new EnumSetting<BoxSize>("Box size", "§lAccurate§r mode shows the exact\nhitbox of each player.\n§lFancy§r mode shows slightly larger\nboxes that look better.", BoxSize.values(), BoxSize.FANCY);
        this.filterSleeping = new CheckboxSetting("Filter sleeping", "Won't show sleeping players.", false);
        this.filterInvisible = new CheckboxSetting("Filter invisible", "Won't show invisible players.", false);
        this.players = new ArrayList<EntityPlayer>();
        this.setCategory(Category.RENDER);
        this.addSetting(this.style);
        this.addSetting(this.boxSize);
        this.addSetting(this.filterSleeping);
        this.addSetting(this.filterInvisible);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { PlayerEspMod.WURST.hax.mobEspMod, PlayerEspMod.WURST.hax.itemEspMod, PlayerEspMod.WURST.hax.chestEspMod, PlayerEspMod.WURST.hax.playerFinderMod, PlayerEspMod.WURST.special.targetSpf };
    }
    
    @Override
    public void onEnable() {
        PlayerEspMod.EVENTS.add(UpdateListener.class, this);
        PlayerEspMod.EVENTS.add(CameraTransformViewBobbingListener.class, this);
        PlayerEspMod.EVENTS.add(RenderListener.class, this);
        GL11.glNewList(this.playerBox = GL11.glGenLists(1), 4864);
        final Box bb = new Box(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5);
        RenderUtils.drawOutlinedBox(bb);
        GL11.glEndList();
    }
    
    @Override
    public void onDisable() {
        PlayerEspMod.EVENTS.remove(UpdateListener.class, this);
        PlayerEspMod.EVENTS.remove(CameraTransformViewBobbingListener.class, this);
        PlayerEspMod.EVENTS.remove(RenderListener.class, this);
        GL11.glDeleteLists(this.playerBox, 1);
        this.playerBox = 0;
    }
    
    @Override
    public void onUpdate() {
        final EntityPlayer player = PlayerEspMod.MC.player;
        final World world = WMinecraft.getWorld();
        this.players.clear();
        Stream<EntityPlayer> stream = world.playerEntities.parallelStream().filter(e -> !e.isDead && e.getHealth() > 0.0f).filter(e -> e != entityPlayer).filter(e -> !(e instanceof EntityFakePlayer)).filter(e -> Math.abs(e.posY - entityPlayer2.posY) <= 1000000.0);
        if (this.filterSleeping.isChecked()) {
            stream = stream.filter(e -> !e.isPlayerSleeping());
        }
        if (this.filterInvisible.isChecked()) {
            stream = stream.filter(e -> !e.isInvisible());
        }
        this.players.addAll(stream.collect((Collector<? super EntityPlayer, ?, Collection<? extends EntityPlayer>>)Collectors.toList()));
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
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        RenderUtils.applyRegionalRenderOffset();
        final BlockPos camPos = RenderUtils.getCameraBlockPos();
        final int regionX = (camPos.getX() >> 9) * 512;
        final int regionZ = (camPos.getZ() >> 9) * 512;
        if (this.style.getSelected().boxes) {
            this.renderBoxes(partialTicks, regionX, regionZ);
        }
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
        for (final EntityPlayer e : this.players) {
            GL11.glPushMatrix();
            GL11.glTranslated(e.prevPosX + (e.posX - e.prevPosX) * partialTicks - regionX, e.prevPosY + (e.posY - e.prevPosY) * partialTicks, e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks - regionZ);
            GL11.glScaled(e.width + extraSize, e.height + extraSize, e.width + extraSize);
            if (PlayerEspMod.WURST.friends.contains(e.getName())) {
                GL11.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
            }
            else {
                final float f = PlayerEspMod.MC.player.getDistanceToEntity(e) / 20.0f;
                GL11.glColor4f(2.0f - f, f, 0.0f, 0.5f);
            }
            GL11.glCallList(this.playerBox);
            GL11.glPopMatrix();
        }
    }
    
    private void renderTracers(final double partialTicks, final int regionX, final int regionZ) {
        final Vec3d start = RotationUtils.getClientLookVec().addVector(0.0, PlayerEspMod.MC.player.getEyeHeight(), 0.0).addVector(PlayerEspMod.MC.getRenderManager().renderPosX, PlayerEspMod.MC.getRenderManager().renderPosY, PlayerEspMod.MC.getRenderManager().renderPosZ);
        GL11.glBegin(1);
        for (final EntityPlayer e : this.players) {
            final Vec3d end = e.getEntityBoundingBox().getCenter().subtract(new Vec3d(e.posX, e.posY, e.posZ).subtract(e.prevPosX, e.prevPosY, e.prevPosZ).scale(1.0 - partialTicks));
            if (PlayerEspMod.WURST.friends.contains(e.getName())) {
                GL11.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
            }
            else {
                final float f = PlayerEspMod.MC.player.getDistanceToEntity(e) / 20.0f;
                GL11.glColor4f(2.0f - f, f, 0.0f, 0.5f);
            }
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
