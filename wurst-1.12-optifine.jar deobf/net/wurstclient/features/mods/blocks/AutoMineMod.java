// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.util.BlockUtils;
import java.util.Arrays;
import net.wurstclient.Category;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto mine", "AutoBreak", "auto break" })
public final class AutoMineMod extends Hack implements UpdateListener
{
    private BlockPos currentBlock;
    
    public AutoMineMod() {
        super("AutoMine", "Automatically mines any block that you look at.");
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public void onEnable() {
        AutoMineMod.WURST.hax.nukerMod.setEnabled(false);
        AutoMineMod.WURST.hax.nukerLegitMod.setEnabled(false);
        AutoMineMod.WURST.hax.speedNukerMod.setEnabled(false);
        AutoMineMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoMineMod.EVENTS.remove(UpdateListener.class, this);
        this.stopMiningAndResetProgress();
    }
    
    @Override
    public void onUpdate() {
        this.setCurrentBlockFromHitResult();
        if (this.currentBlock != null) {
            this.breakCurrentBlock();
        }
    }
    
    private void setCurrentBlockFromHitResult() {
        if (AutoMineMod.MC.objectMouseOver == null || AutoMineMod.MC.objectMouseOver.getBlockPos() == null) {
            this.stopMiningAndResetProgress();
            return;
        }
        this.currentBlock = AutoMineMod.MC.objectMouseOver.getBlockPos();
    }
    
    private void breakCurrentBlock() {
        if (AutoMineMod.MC.player.abilities.creativeMode) {
            BlockUtils.breakBlocksPacketSpam(Arrays.asList(this.currentBlock));
        }
        else {
            BlockUtils.breakBlockSimple_old(this.currentBlock);
        }
    }
    
    private void stopMiningAndResetProgress() {
        if (this.currentBlock == null) {
            return;
        }
        AutoMineMod.MC.playerController.isHittingBlock = true;
        AutoMineMod.MC.playerController.resetBlockRemoving();
        this.currentBlock = null;
    }
}
