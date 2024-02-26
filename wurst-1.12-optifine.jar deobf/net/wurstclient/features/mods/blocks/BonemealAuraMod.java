// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.features.special_features.YesCheatSpf;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WPlayer;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.util.BlockUtils;
import net.minecraft.item.ItemDye;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "bonemeal aura", "bone meal aura", "AutoBone", "auto bone" })
@Bypasses
public final class BonemealAuraMod extends Hack implements UpdateListener
{
    private final SliderSetting range;
    private final ModeSetting mode;
    private final CheckboxSetting saplings;
    private final CheckboxSetting crops;
    private final CheckboxSetting stems;
    private final CheckboxSetting cocoa;
    private final CheckboxSetting other;
    
    public BonemealAuraMod() {
        super("BonemealAura", "Automatically uses bone meal on specific types of plants.\nUse the checkboxes to specify the types of plants.");
        this.range = new SliderSetting("Range", 4.25, 1.0, 6.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.mode = new ModeSetting("Mode", "§lFast§r mode can use bone meal on multiple blocks at once.\n§lLegit§r mode can bypass NoCheat+.", new String[] { "Fast", "Legit" }, 1);
        this.saplings = new CheckboxSetting("Saplings", true);
        this.crops = new CheckboxSetting("Crops", "Wheat, carrots, potatoes and beetroots.", true);
        this.stems = new CheckboxSetting("Stems", "Pumpkins and melons.", true);
        this.cocoa = new CheckboxSetting("Cocoa", true);
        this.other = new CheckboxSetting("Other", false);
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.range);
        this.addSetting(this.mode);
        this.addSetting(this.saplings);
        this.addSetting(this.crops);
        this.addSetting(this.stems);
        this.addSetting(this.cocoa);
        this.addSetting(this.other);
    }
    
    @Override
    public void onEnable() {
        BonemealAuraMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        BonemealAuraMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (BonemealAuraMod.MC.rightClickDelayTimer > 0) {
            return;
        }
        final ItemStack stack = BonemealAuraMod.MC.player.inventory.getCurrentItem();
        if (WItem.isNullOrEmpty(stack) || !(stack.getItem() instanceof ItemDye) || stack.getMetadata() != 15) {
            return;
        }
        final Iterable<BlockPos> validBlocks = BlockUtils.getValidBlocks(this.range.getValue(), p -> this.isCorrectBlock(p));
        if (this.mode.getSelected() == 1) {
            for (final BlockPos pos : validBlocks) {
                if (BlockUtils.rightClickBlockLegit(pos)) {
                    break;
                }
            }
        }
        else {
            boolean shouldSwing = false;
            for (final BlockPos pos2 : validBlocks) {
                if (BlockUtils.rightClickBlockSimple(pos2)) {
                    shouldSwing = true;
                }
            }
            if (shouldSwing) {
                WPlayer.swingArmClient();
            }
        }
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            default: {
                this.range.resetUsableMax();
                this.mode.unlock();
                break;
            }
            case ANTICHEAT:
            case OLDER_NCP:
            case LATEST_NCP:
            case GHOST_MODE: {
                this.range.setUsableMax(4.25);
                this.mode.lock(1);
                break;
            }
        }
    }
    
    private boolean isCorrectBlock(final BlockPos pos) {
        final Block block = WBlock.getBlock(pos);
        if (!(block instanceof IGrowable) || block instanceof BlockGrass || !((IGrowable)block).canGrow(WMinecraft.getWorld(), pos, WBlock.getState(pos), false)) {
            return false;
        }
        if (block instanceof BlockSapling) {
            return this.saplings.isChecked();
        }
        if (block instanceof BlockCrops) {
            return this.crops.isChecked();
        }
        if (block instanceof BlockStem) {
            return this.stems.isChecked();
        }
        if (block instanceof BlockCocoa) {
            return this.cocoa.isChecked();
        }
        return this.other.isChecked();
    }
}
