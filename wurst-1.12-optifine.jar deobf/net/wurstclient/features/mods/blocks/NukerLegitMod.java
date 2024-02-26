// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.settings.SliderLock;
import net.wurstclient.util.BlockUtils;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.features.Hack;

@SearchTags({ "LegitNuker", "nuker legit", "legit nuker" })
@Bypasses
public final class NukerLegitMod extends Hack implements LeftClickListener, RenderListener, UpdateListener
{
    private final CheckboxSetting useNuker;
    private final SliderSetting range;
    private final ModeSetting mode;
    private BlockPos currentBlock;
    private BlockUtils.BlockValidator validator;
    
    public NukerLegitMod() {
        super("NukerLegit", "Slower Nuker that bypasses all AntiCheat plugins.\nNot required on normal NoCheat+ servers!");
        this.useNuker = new CheckboxSetting("Use Nuker settings", true) {
            @Override
            public void update() {
                if (this.isChecked()) {
                    final NukerMod nuker = NukerLegitMod.WURST.hax.nukerMod;
                    NukerLegitMod.this.range.lock(nuker.range);
                    NukerLegitMod.this.mode.lock(nuker.mode.getSelected());
                }
                else {
                    NukerLegitMod.this.range.unlock();
                    NukerLegitMod.this.mode.unlock();
                }
            }
        };
        this.range = new SliderSetting("Range", 4.25, 1.0, 4.25, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.mode = new ModeSetting("Mode", new String[] { "Normal", "ID", "Flat", "Smash" }, 0) {
            @Override
            public void update() {
                switch (this.getSelected()) {
                    default: {
                        NukerLegitMod.access$3(NukerLegitMod.this, pos -> true);
                        break;
                    }
                    case 1: {
                        NukerLegitMod.access$3(NukerLegitMod.this, pos -> NukerLegitMod.WURST.hax.nukerMod.getId() == WBlock.getId(pos));
                        break;
                    }
                    case 2: {
                        NukerLegitMod.access$3(NukerLegitMod.this, pos -> pos.getY() >= NukerLegitMod.MC.player.posY);
                        break;
                    }
                    case 3: {
                        NukerLegitMod.access$3(NukerLegitMod.this, pos -> WBlock.getHardness(pos) >= 1.0f);
                        break;
                    }
                }
            }
        };
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.useNuker);
        this.addSetting(this.range);
        this.addSetting(this.mode);
    }
    
    @Override
    public String getRenderName() {
        switch (this.mode.getSelected()) {
            case 0: {
                return "NukerLegit";
            }
            case 1: {
                return "IDNukerLegit [" + NukerLegitMod.WURST.hax.nukerMod.getId() + "]";
            }
            default: {
                return String.valueOf(this.mode.getSelectedMode()) + "NukerLegit";
            }
        }
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { NukerLegitMod.WURST.hax.nukerMod, NukerLegitMod.WURST.hax.speedNukerMod, NukerLegitMod.WURST.hax.tunnellerMod };
    }
    
    @Override
    public void onEnable() {
        NukerLegitMod.WURST.hax.nukerMod.setEnabled(false);
        NukerLegitMod.WURST.hax.speedNukerMod.setEnabled(false);
        NukerLegitMod.WURST.hax.tunnellerMod.setEnabled(false);
        NukerLegitMod.EVENTS.add(LeftClickListener.class, this);
        NukerLegitMod.EVENTS.add(UpdateListener.class, this);
        NukerLegitMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        NukerLegitMod.EVENTS.remove(LeftClickListener.class, this);
        NukerLegitMod.EVENTS.remove(UpdateListener.class, this);
        NukerLegitMod.EVENTS.remove(RenderListener.class, this);
        NukerLegitMod.MC.gameSettings.keyBindAttack.pressed = false;
        this.currentBlock = null;
        NukerLegitMod.WURST.hax.nukerMod.setId(0);
    }
    
    @Override
    public void onLeftClick(final LeftClickEvent event) {
        if (NukerLegitMod.MC.objectMouseOver == null || NukerLegitMod.MC.objectMouseOver.getBlockPos() == null) {
            return;
        }
        if (this.mode.getSelected() != 1) {
            return;
        }
        if (WBlock.getMaterial(NukerLegitMod.MC.objectMouseOver.getBlockPos()) == Material.AIR) {
            return;
        }
        NukerLegitMod.WURST.hax.nukerMod.setId(WBlock.getId(NukerLegitMod.MC.objectMouseOver.getBlockPos()));
    }
    
    @Override
    public void onUpdate() {
        if (this.mode.getSelected() == 1 && NukerLegitMod.WURST.hax.nukerMod.getId() == 0) {
            return;
        }
        this.currentBlock = null;
        final Iterable<BlockPos> validBlocks = BlockUtils.getValidBlocksByDistance(this.range.getValue(), false, this.validator);
        for (final BlockPos pos : validBlocks) {
            if (!BlockUtils.breakBlockExtraLegit(pos)) {
                continue;
            }
            this.currentBlock = pos;
            break;
        }
        if (this.currentBlock == null) {
            NukerLegitMod.MC.gameSettings.keyBindAttack.pressed = false;
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (this.currentBlock == null) {
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
        GL11.glTranslated(-NukerLegitMod.MC.getRenderManager().renderPosX, -NukerLegitMod.MC.getRenderManager().renderPosY, -NukerLegitMod.MC.getRenderManager().renderPosZ);
        GL11.glTranslated((double)this.currentBlock.getX(), (double)this.currentBlock.getY(), (double)this.currentBlock.getZ());
        float progress;
        if (WBlock.getHardness(this.currentBlock) < 1.0f) {
            progress = NukerLegitMod.MC.playerController.curBlockDamageMP;
        }
        else {
            progress = 1.0f;
        }
        if (progress < 1.0f) {
            GL11.glTranslated(0.5, 0.5, 0.5);
            GL11.glScaled((double)progress, (double)progress, (double)progress);
            GL11.glTranslated(-0.5, -0.5, -0.5);
        }
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
    
    static /* synthetic */ void access$3(final NukerLegitMod nukerLegitMod, final BlockUtils.BlockValidator validator) {
        nukerLegitMod.validator = validator;
    }
}
