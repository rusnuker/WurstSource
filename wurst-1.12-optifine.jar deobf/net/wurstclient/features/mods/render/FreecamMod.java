// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.minecraft.util.math.Vec3d;
import net.wurstclient.util.RotationUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.util.RenderUtils;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.util.EntityFakePlayer;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.SetOpaqueCubeListener;
import net.wurstclient.events.IsNormalCubeListener;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.PlayerMoveListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "free camera", "spectator" })
@Bypasses
@DontSaveState
public final class FreecamMod extends Hack implements UpdateListener, PlayerMoveListener, CameraTransformViewBobbingListener, IsNormalCubeListener, SetOpaqueCubeListener, RenderListener
{
    private final SliderSetting speed;
    private final CheckboxSetting tracer;
    private EntityFakePlayer fakePlayer;
    private int playerBox;
    
    public FreecamMod() {
        super("Freecam", "Allows you to move the camera without moving your character.");
        this.speed = new SliderSetting("Speed", 1.0, 0.05, 10.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.tracer = new CheckboxSetting("Tracer", "Draws a line to your character's actual position.", false);
        this.setCategory(Category.RENDER);
        this.addSetting(this.speed);
        this.addSetting(this.tracer);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { FreecamMod.WURST.hax.remoteViewMod };
    }
    
    @Override
    public void onEnable() {
        FreecamMod.EVENTS.add(UpdateListener.class, this);
        FreecamMod.EVENTS.add(PlayerMoveListener.class, this);
        FreecamMod.EVENTS.add(CameraTransformViewBobbingListener.class, this);
        FreecamMod.EVENTS.add(IsNormalCubeListener.class, this);
        FreecamMod.EVENTS.add(SetOpaqueCubeListener.class, this);
        FreecamMod.EVENTS.add(RenderListener.class, this);
        this.fakePlayer = new EntityFakePlayer();
        final GameSettings gs = FreecamMod.MC.gameSettings;
        final KeyBinding[] bindings = { gs.keyBindForward, gs.keyBindBack, gs.keyBindLeft, gs.keyBindRight, gs.keyBindJump, gs.keyBindSneak };
        KeyBinding[] array;
        for (int length = (array = bindings).length, i = 0; i < length; ++i) {
            final KeyBinding binding = array[i];
            binding.pressed = GameSettings.isKeyDown(binding);
        }
        GL11.glNewList(this.playerBox = GL11.glGenLists(1), 4864);
        final Box bb = new Box(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5);
        RenderUtils.drawOutlinedBox(bb);
        GL11.glEndList();
    }
    
    @Override
    public void onDisable() {
        FreecamMod.EVENTS.remove(UpdateListener.class, this);
        FreecamMod.EVENTS.remove(PlayerMoveListener.class, this);
        FreecamMod.EVENTS.remove(CameraTransformViewBobbingListener.class, this);
        FreecamMod.EVENTS.remove(IsNormalCubeListener.class, this);
        FreecamMod.EVENTS.remove(SetOpaqueCubeListener.class, this);
        FreecamMod.EVENTS.remove(RenderListener.class, this);
        this.fakePlayer.resetPlayerPosition();
        this.fakePlayer.despawn();
        final EntityPlayerSP player = FreecamMod.MC.player;
        player.motionX = 0.0;
        player.motionY = 0.0;
        player.motionZ = 0.0;
        FreecamMod.MC.renderGlobal.loadRenderers();
        GL11.glDeleteLists(this.playerBox, 1);
        this.playerBox = 0;
    }
    
    @Override
    public void onUpdate() {
        final EntityPlayerSP player = FreecamMod.MC.player;
        player.motionX = 0.0;
        player.motionY = 0.0;
        player.motionZ = 0.0;
        player.onGround = false;
        player.jumpMovementFactor = this.speed.getValueF();
        if (FreecamMod.MC.gameSettings.keyBindJump.pressed) {
            final EntityPlayerSP entityPlayerSP = player;
            entityPlayerSP.motionY += this.speed.getValue();
        }
        if (FreecamMod.MC.gameSettings.keyBindSneak.pressed) {
            final EntityPlayerSP entityPlayerSP2 = player;
            entityPlayerSP2.motionY -= this.speed.getValue();
        }
    }
    
    @Override
    public void onPlayerMove(final EntityPlayerSP player) {
        player.noClip = true;
    }
    
    @Override
    public void onCameraTransformViewBobbing(final CameraTransformViewBobbingEvent event) {
        if (this.tracer.isChecked()) {
            event.cancel();
        }
    }
    
    @Override
    public void onIsNormalCube(final IsNormalCubeEvent event) {
        event.cancel();
    }
    
    @Override
    public void onSetOpaqueCube(final SetOpaqueCubeEvent event) {
        event.cancel();
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (this.fakePlayer == null || !this.tracer.isChecked()) {
            return;
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        GL11.glTranslated(-FreecamMod.MC.getRenderManager().renderPosX, -FreecamMod.MC.getRenderManager().renderPosY, -FreecamMod.MC.getRenderManager().renderPosZ);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
        GL11.glPushMatrix();
        GL11.glTranslated(this.fakePlayer.posX, this.fakePlayer.posY, this.fakePlayer.posZ);
        GL11.glScaled(this.fakePlayer.width + 0.1, this.fakePlayer.height + 0.1, this.fakePlayer.width + 0.1);
        GL11.glCallList(this.playerBox);
        GL11.glPopMatrix();
        final Vec3d start = RotationUtils.getClientLookVec().addVector(0.0, FreecamMod.MC.player.getEyeHeight(), 0.0).addVector(FreecamMod.MC.getRenderManager().renderPosX, FreecamMod.MC.getRenderManager().renderPosY, FreecamMod.MC.getRenderManager().renderPosZ);
        final Vec3d end = this.fakePlayer.getEntityBoundingBox().getCenter();
        GL11.glBegin(1);
        GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
        GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
}
