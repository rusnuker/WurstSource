// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.compatibility.WEnchantments;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemSword;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.util.BlockUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.events.PlayerDamageBlockListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto tool", "AutoSwitch", "auto switch" })
public final class AutoToolMod extends Hack implements PlayerDamageBlockListener, UpdateListener
{
    private final CheckboxSetting useSwords;
    private final CheckboxSetting useHands;
    private final CheckboxSetting repairMode;
    private final CheckboxSetting switchBack;
    private int prevSelectedSlot;
    
    public AutoToolMod() {
        super("AutoTool", "Automatically equips the fastest applicable tool\nin your hotbar when you try to break a block.");
        this.useSwords = new CheckboxSetting("Use swords", "Uses swords to break\nleaves, cobwebs, etc.", false);
        this.useHands = new CheckboxSetting("Use hands", "Uses an empty hand or a\nnon-damageable item when\nno applicable tool is found.", true);
        this.repairMode = new CheckboxSetting("Repair mode", "Won't use tools that are about to break.", false);
        this.switchBack = new CheckboxSetting("Switch back", "After using a tool, automatically switches\nback to the previously selected slot.", true);
        this.setCategory(Category.BLOCKS);
        this.addSetting(this.useSwords);
        this.addSetting(this.useHands);
        this.addSetting(this.repairMode);
        this.addSetting(this.switchBack);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { AutoToolMod.WURST.hax.autoSwordMod, AutoToolMod.WURST.hax.nukerMod };
    }
    
    @Override
    public void onEnable() {
        AutoToolMod.EVENTS.add(PlayerDamageBlockListener.class, this);
        AutoToolMod.EVENTS.add(UpdateListener.class, this);
        this.prevSelectedSlot = -1;
    }
    
    @Override
    public void onDisable() {
        AutoToolMod.EVENTS.remove(PlayerDamageBlockListener.class, this);
        AutoToolMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onPlayerDamageBlock(final PlayerDamageBlockEvent event) {
        final BlockPos pos = event.getPos();
        if (!WBlock.canBeClicked(pos)) {
            return;
        }
        if (this.prevSelectedSlot == -1) {
            this.prevSelectedSlot = AutoToolMod.MC.player.inventory.selectedSlot;
        }
        this.equipBestTool(pos, this.useSwords.isChecked(), this.useHands.isChecked(), this.repairMode.isChecked());
    }
    
    @Override
    public void onUpdate() {
        if (this.prevSelectedSlot == -1 || AutoToolMod.MC.playerController.isHittingBlock) {
            return;
        }
        if (this.switchBack.isChecked()) {
            AutoToolMod.MC.player.inventory.selectedSlot = this.prevSelectedSlot;
        }
        this.prevSelectedSlot = -1;
    }
    
    public void equipIfEnabled(final BlockPos pos) {
        if (!this.isEnabled()) {
            return;
        }
        this.equipBestTool(pos, this.useSwords.isChecked(), this.useHands.isChecked(), this.repairMode.isChecked());
    }
    
    public void equipBestTool(final BlockPos pos, final boolean useSwords, final boolean useHands, final boolean repairMode) {
        final EntityPlayer player = AutoToolMod.MC.player;
        if (player.abilities.creativeMode) {
            return;
        }
        final int bestSlot = this.getBestSlot(pos, useSwords, repairMode);
        if (bestSlot != -1) {
            player.inventory.selectedSlot = bestSlot;
            return;
        }
        final ItemStack heldItem = player.getHeldItemMainhand();
        if (!this.isDamageable(heldItem)) {
            return;
        }
        if (repairMode && this.isTooDamaged(heldItem)) {
            this.selectFallbackSlot();
            return;
        }
        if (useHands && this.isWrongTool(heldItem, pos)) {
            this.selectFallbackSlot();
        }
    }
    
    private int getBestSlot(final BlockPos pos, final boolean useSwords, final boolean repairMode) {
        final EntityPlayerSP player = AutoToolMod.MC.player;
        final InventoryPlayer inventory = player.inventory;
        final ItemStack heldItem = AutoToolMod.MC.player.getHeldItemMainhand();
        final IBlockState state = BlockUtils.getState(pos);
        float bestSpeed = this.getMiningSpeed(heldItem, state);
        int bestSlot = -1;
        for (int slot = 0; slot < 9; ++slot) {
            if (slot != inventory.selectedSlot) {
                final ItemStack stack = inventory.getInvStack(slot);
                final float speed = this.getMiningSpeed(stack, state);
                if (speed > bestSpeed) {
                    if (useSwords || WItem.isNullOrEmpty(stack) || !(stack.getItem() instanceof ItemSword)) {
                        if (!repairMode || !this.isTooDamaged(stack)) {
                            bestSpeed = speed;
                            bestSlot = slot;
                        }
                    }
                }
            }
        }
        return bestSlot;
    }
    
    private float getMiningSpeed(final ItemStack stack, final IBlockState state) {
        float speed = WItem.getDestroySpeed(stack, state);
        if (speed > 1.0f && !WItem.isNullOrEmpty(stack)) {
            final int efficiency = WEnchantments.getEnchantmentLevel(WEnchantments.EFFICIENCY, stack);
            if (efficiency > 0) {
                speed += efficiency * efficiency + 1;
            }
        }
        return speed;
    }
    
    private boolean isDamageable(final ItemStack stack) {
        return !WItem.isNullOrEmpty(stack) && stack.getItem().isDamageable();
    }
    
    private boolean isTooDamaged(final ItemStack stack) {
        return !WItem.isNullOrEmpty(stack) && stack.getMaxDamage() - stack.getItemDamage() <= 4;
    }
    
    private boolean isWrongTool(final ItemStack heldItem, final BlockPos pos) {
        final IBlockState state = BlockUtils.getState(pos);
        return this.getMiningSpeed(heldItem, state) <= 1.0f;
    }
    
    private void selectFallbackSlot() {
        final int fallbackSlot = this.getFallbackSlot();
        final InventoryPlayer inventory = AutoToolMod.MC.player.inventory;
        if (fallbackSlot == -1) {
            if (inventory.selectedSlot == 8) {
                inventory.selectedSlot = 0;
            }
            else {
                final InventoryPlayer inventoryPlayer = inventory;
                ++inventoryPlayer.selectedSlot;
            }
            return;
        }
        inventory.selectedSlot = fallbackSlot;
    }
    
    private int getFallbackSlot() {
        final InventoryPlayer inventory = AutoToolMod.MC.player.inventory;
        for (int slot = 0; slot < 9; ++slot) {
            if (slot != inventory.selectedSlot) {
                final ItemStack stack = inventory.getInvStack(slot);
                if (!this.isDamageable(stack)) {
                    return slot;
                }
            }
        }
        return -1;
    }
}
