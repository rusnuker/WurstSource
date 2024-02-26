// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.features.special_features.YesCheatSpf;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import java.util.Iterator;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.util.BlockUtils;
import net.minecraft.block.material.Material;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.Entity;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.ModeSetting;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "instant bunker" })
@Bypasses
public final class InstantBunkerMod extends Hack implements UpdateListener, RenderListener
{
    private final int[][] template;
    private final ArrayList<BlockPos> positions;
    private final ModeSetting mode;
    private int blockIndex;
    private boolean building;
    
    public InstantBunkerMod() {
        super("InstantBunker", "Builds a small bunker around you. Needs 57 blocks.");
        this.template = new int[][] { { 2, 0, 2 }, { -2, 0, 2 }, { 2, 0, -2 }, { -2, 0, -2 }, { 2, 1, 2 }, { -2, 1, 2 }, { 2, 1, -2 }, { -2, 1, -2 }, { 2, 2, 2 }, { -2, 2, 2 }, { 2, 2, -2 }, { -2, 2, -2 }, { 1, 2, 2 }, { 0, 2, 2 }, { -1, 2, 2 }, { 2, 2, 1 }, { 2, 2, 0 }, { 2, 2, -1 }, { -2, 2, 1 }, { -2, 2, 0 }, { -2, 2, -1 }, { 1, 2, -2 }, { 0, 2, -2 }, { -1, 2, -2 }, { 1, 0, 2 }, { 0, 0, 2 }, { -1, 0, 2 }, { 2, 0, 1 }, { 2, 0, 0 }, { 2, 0, -1 }, { -2, 0, 1 }, { -2, 0, 0 }, { -2, 0, -1 }, { 1, 0, -2 }, { 0, 0, -2 }, { -1, 0, -2 }, { 1, 1, 2 }, { 0, 1, 2 }, { -1, 1, 2 }, { 2, 1, 1 }, { 2, 1, 0 }, { 2, 1, -1 }, { -2, 1, 1 }, { -2, 1, 0 }, { -2, 1, -1 }, { 1, 1, -2 }, { 0, 1, -2 }, { -1, 1, -2 }, { 1, 2, 1 }, { -1, 2, 1 }, { 1, 2, -1 }, { -1, 2, -1 }, { 0, 2, 1 }, { 1, 2, 0 }, { -1, 2, 0 }, { 0, 2, -1 }, { 0, 2, 0 } };
        this.positions = new ArrayList<BlockPos>();
        this.mode = new ModeSetting("Mode", "§lInstant§r mode places all 57 blocks at once.\n§lLegit§r mode builds the bunker like a normal player would (bypasses NoCheat+).", new String[] { "Instant", "Legit" }, 0);
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.mode);
    }
    
    @Override
    public void onEnable() {
        final BlockPos startPos = new BlockPos(InstantBunkerMod.MC.player);
        final EnumFacing facing = InstantBunkerMod.MC.player.getHorizontalFacing();
        final EnumFacing facing2 = facing.rotateYCCW();
        this.positions.clear();
        int[][] template;
        for (int length = (template = this.template).length, i = 0; i < length; ++i) {
            final int[] pos = template[i];
            this.positions.add(startPos.up(pos[1]).offset(facing, pos[2]).offset(facing2, pos[0]));
        }
        if (this.mode.getSelected() == 1) {
            this.blockIndex = 0;
            this.building = true;
            InstantBunkerMod.MC.rightClickDelayTimer = 4;
        }
        InstantBunkerMod.EVENTS.add(UpdateListener.class, this);
        InstantBunkerMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        InstantBunkerMod.EVENTS.remove(UpdateListener.class, this);
        InstantBunkerMod.EVENTS.remove(RenderListener.class, this);
        this.building = false;
    }
    
    @Override
    public void onUpdate() {
        if (!this.building) {
            for (final BlockPos pos : this.positions) {
                if (WBlock.getMaterial(pos) == Material.AIR) {
                    BlockUtils.placeBlockSimple_old(pos);
                }
            }
            WPlayer.swingArmClient();
            this.setEnabled(false);
            return;
        }
        if (this.blockIndex < this.positions.size() && (InstantBunkerMod.MC.rightClickDelayTimer == 0 || InstantBunkerMod.WURST.hax.fastPlaceMod.isActive())) {
            final BlockPos pos = this.positions.get(this.blockIndex);
            if (WBlock.getMaterial(pos) == Material.AIR) {
                if (!BlockUtils.placeBlockLegit(pos)) {
                    final BlockPos playerPos = new BlockPos(InstantBunkerMod.MC.player);
                    if (InstantBunkerMod.MC.player.onGround && Math.abs(pos.getX() - playerPos.getX()) == 2 && pos.getY() - playerPos.getY() == 2 && Math.abs(pos.getZ() - playerPos.getZ()) == 2) {
                        InstantBunkerMod.MC.player.jump();
                    }
                }
            }
            else {
                ++this.blockIndex;
                if (this.blockIndex == this.positions.size()) {
                    this.setEnabled(this.building = false);
                }
            }
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (!this.building || this.blockIndex >= this.positions.size()) {
            return;
        }
        final double scale = 0.875;
        final double offset = (1.0 - scale) / 2.0;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GL11.glPushMatrix();
        GL11.glTranslated(-InstantBunkerMod.MC.getRenderManager().renderPosX, -InstantBunkerMod.MC.getRenderManager().renderPosY, -InstantBunkerMod.MC.getRenderManager().renderPosZ);
        GL11.glDepthMask(false);
        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.15f);
        final BlockPos pos = this.positions.get(this.blockIndex);
        GL11.glPushMatrix();
        GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        GL11.glTranslated(offset, offset, offset);
        GL11.glScaled(scale, scale, scale);
        RenderUtils.drawSolidBox();
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
        for (int i = this.blockIndex; i < this.positions.size(); ++i) {
            final BlockPos pos2 = this.positions.get(i);
            GL11.glPushMatrix();
            GL11.glTranslated((double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ());
            GL11.glTranslated(offset, offset, offset);
            GL11.glScaled(scale, scale, scale);
            RenderUtils.drawOutlinedBox();
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            case OFF:
            case MINEPLEX: {
                this.mode.unlock();
                break;
            }
            default: {
                this.mode.lock(1);
                break;
            }
        }
    }
}
