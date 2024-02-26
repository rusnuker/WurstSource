// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketEffect;
import net.wurstclient.util.RenderUtils;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.util.RotationUtils;
import net.wurstclient.compatibility.WMath;
import org.lwjgl.opengl.GL11;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.PacketInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "player finder" })
@Bypasses
public final class PlayerFinderMod extends Hack implements UpdateListener, PacketInputListener, RenderListener
{
    private BlockPos pos;
    private BlockPos lastPos;
    
    public PlayerFinderMod() {
        super("PlayerFinder", "Finds far players during thunderstorms.");
        this.setCategory(Category.RENDER);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { PlayerFinderMod.WURST.hax.playerEspMod };
    }
    
    @Override
    public void onEnable() {
        this.pos = null;
        this.lastPos = null;
        PlayerFinderMod.EVENTS.add(UpdateListener.class, this);
        PlayerFinderMod.EVENTS.add(PacketInputListener.class, this);
        PlayerFinderMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        PlayerFinderMod.EVENTS.remove(UpdateListener.class, this);
        PlayerFinderMod.EVENTS.remove(PacketInputListener.class, this);
        PlayerFinderMod.EVENTS.remove(RenderListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (this.pos == null || this.pos.equals(this.lastPos)) {
            return;
        }
        ChatUtils.message("PlayerFinder has detected a player near " + this.pos.getX() + ", " + this.pos.getY() + ", " + this.pos.getZ() + ".");
        this.lastPos = this.pos;
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (this.pos == null) {
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
        GL11.glTranslated(-PlayerFinderMod.MC.getRenderManager().renderPosX, -PlayerFinderMod.MC.getRenderManager().renderPosY, -PlayerFinderMod.MC.getRenderManager().renderPosZ);
        final float x = System.currentTimeMillis() % 2000L / 1000.0f;
        final float red = 0.5f + 0.5f * WMath.sin(x * 3.1415927f);
        final float green = 0.5f + 0.5f * WMath.sin((x + 1.3333334f) * 3.1415927f);
        final float blue = 0.5f + 0.5f * WMath.sin((x + 2.6666667f) * 3.1415927f);
        GL11.glColor4f(red, green, blue, 0.5f);
        GL11.glBegin(1);
        final Vec3d start = RotationUtils.getClientLookVec().addVector(0.0, PlayerFinderMod.MC.player.getEyeHeight(), 0.0).addVector(PlayerFinderMod.MC.getRenderManager().renderPosX, PlayerFinderMod.MC.getRenderManager().renderPosY, PlayerFinderMod.MC.getRenderManager().renderPosZ);
        final Vec3d end = new Vec3d(this.pos).addVector(0.5, 0.5, 0.5);
        GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
        GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
        GL11.glEnd();
        GL11.glPushMatrix();
        GL11.glTranslated((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
        RenderUtils.drawOutlinedBox();
        GL11.glColor4f(red, green, blue, 0.25f);
        RenderUtils.drawSolidBox();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    @Override
    public void onReceivedPacket(final PacketInputEvent event) {
        if (PlayerFinderMod.MC.player == null) {
            return;
        }
        final Packet packet = event.getPacket();
        BlockPos newPos = null;
        if (packet instanceof SPacketEffect) {
            final SPacketEffect effect = (SPacketEffect)packet;
            newPos = effect.getSoundPos();
        }
        else if (packet instanceof SPacketSoundEffect) {
            final SPacketSoundEffect sound = (SPacketSoundEffect)packet;
            newPos = new BlockPos(sound.getX(), sound.getY(), sound.getZ());
        }
        else if (packet instanceof SPacketSpawnGlobalEntity) {
            final SPacketSpawnGlobalEntity lightning = (SPacketSpawnGlobalEntity)packet;
            newPos = new BlockPos(lightning.getX() / 32.0, lightning.getY() / 32.0, lightning.getZ() / 32.0);
        }
        if (newPos == null) {
            return;
        }
        final BlockPos playerPos = new BlockPos(PlayerFinderMod.MC.player);
        if (Math.abs(playerPos.getX() - newPos.getX()) > 256 || Math.abs(playerPos.getZ() - newPos.getZ()) > 256) {
            this.pos = newPos;
        }
    }
}
