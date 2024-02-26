// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.minecraft.item.ItemStack;
import net.wurstclient.util.BlockUtils;
import net.minecraft.block.BlockFalling;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "scaffold walk", "BridgeWalk", "bridge walk", "AutoBridge", "auto bridge", "tower" })
@Bypasses(ghostMode = false)
public final class ScaffoldWalkMod extends Hack implements UpdateListener
{
    public ScaffoldWalkMod() {
        super("ScaffoldWalk", "Automatically places blocks below your feet.");
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { ScaffoldWalkMod.WURST.hax.safeWalkMod };
    }
    
    @Override
    public void onEnable() {
        ScaffoldWalkMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        ScaffoldWalkMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        final BlockPos belowPlayer = new BlockPos(ScaffoldWalkMod.MC.player).down();
        if (!WBlock.getMaterial(belowPlayer).isReplaceable()) {
            return;
        }
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = ScaffoldWalkMod.MC.player.inventory.getInvStack(i);
            if (!WItem.isNullOrEmpty(stack)) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = Block.getBlockFromItem(stack.getItem());
                    if (block.getDefaultState().getBoundingBox(ScaffoldWalkMod.MC.world, belowPlayer).getAverageEdgeLength() >= 1.0) {
                        if (!(block instanceof BlockFalling) || !BlockFalling.canFallThrough(BlockUtils.getState(belowPlayer.down()))) {
                            newSlot = i;
                            break;
                        }
                    }
                }
            }
        }
        if (newSlot == -1) {
            return;
        }
        final int oldSlot = ScaffoldWalkMod.MC.player.inventory.selectedSlot;
        ScaffoldWalkMod.MC.player.inventory.selectedSlot = newSlot;
        BlockUtils.placeBlockScaffold(belowPlayer);
        ScaffoldWalkMod.MC.player.inventory.selectedSlot = oldSlot;
    }
}
