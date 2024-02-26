// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemSoup;
import net.wurstclient.features.special_features.YesCheatSpf;
import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.compatibility.WItem;
import net.minecraft.init.Items;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "AutoStew", "auto soup", "auto stew" })
@Bypasses
public final class AutoSoupMod extends Hack implements UpdateListener
{
    private final SliderSetting health;
    private final CheckboxSetting ignoreScreen;
    private int oldSlot;
    
    public AutoSoupMod() {
        super("AutoSoup", "Automatically eats soup if your health is lower than or equal to the set value.\n\n§lNote:§r This mod ignores hunger and assumes that eating soup directly refills your health.\nIf the server you are playing on is not configured to do that, use AutoEat instead.");
        this.health = new SliderSetting("Health", 6.5, 0.5, 9.5, 0.5, SliderSetting.ValueDisplay.DECIMAL);
        this.ignoreScreen = new CheckboxSetting("Ignore screen", true);
        this.oldSlot = -1;
        this.setCategory(Category.OTHER);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.health);
        this.addSetting(this.ignoreScreen);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { AutoSoupMod.WURST.hax.autoPotionHack, AutoSoupMod.WURST.hax.autoEatMod };
    }
    
    @Override
    public void onEnable() {
        AutoSoupMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoSoupMod.EVENTS.remove(UpdateListener.class, this);
        this.stopIfEating();
    }
    
    @Override
    public void onUpdate() {
        for (int i = 0; i < 36; ++i) {
            final ItemStack stack = AutoSoupMod.MC.player.inventory.getInvStack(i);
            if (stack != null && stack.getItem() == Items.BOWL) {
                if (i != 9) {
                    final ItemStack emptyBowlStack = AutoSoupMod.MC.player.inventory.getInvStack(9);
                    final boolean swap = !WItem.isNullOrEmpty(emptyBowlStack) && emptyBowlStack.getItem() != Items.BOWL;
                    WPlayerController.windowClick_PICKUP((i < 9) ? (36 + i) : i);
                    WPlayerController.windowClick_PICKUP(9);
                    if (swap) {
                        WPlayerController.windowClick_PICKUP((i < 9) ? (36 + i) : i);
                    }
                }
            }
        }
        final int soupInHotbar = this.findSoup(0, 9);
        if (soupInHotbar == -1) {
            this.stopIfEating();
            final int soupInInventory = this.findSoup(9, 36);
            if (soupInInventory != -1) {
                WPlayerController.windowClick_QUICK_MOVE(soupInInventory);
            }
            return;
        }
        if (!this.shouldEatSoup()) {
            this.stopIfEating();
            return;
        }
        if (this.oldSlot == -1) {
            this.oldSlot = AutoSoupMod.MC.player.inventory.selectedSlot;
        }
        AutoSoupMod.MC.player.inventory.selectedSlot = soupInHotbar;
        AutoSoupMod.MC.gameSettings.keyBindUseItem.pressed = true;
        WPlayerController.processRightClick();
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            case GHOST_MODE: {
                this.ignoreScreen.lock(() -> false);
                break;
            }
            default: {
                this.ignoreScreen.unlock();
                break;
            }
        }
    }
    
    private int findSoup(final int startSlot, final int endSlot) {
        for (int i = startSlot; i < endSlot; ++i) {
            final ItemStack stack = AutoSoupMod.MC.player.inventory.getInvStack(i);
            if (stack != null && stack.getItem() instanceof ItemSoup) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean shouldEatSoup() {
        if (AutoSoupMod.MC.player.getHealth() > this.health.getValueF() * 2.0f) {
            return false;
        }
        if (!this.ignoreScreen.isChecked() && AutoSoupMod.MC.currentScreen != null) {
            return false;
        }
        if (AutoSoupMod.MC.currentScreen == null && AutoSoupMod.MC.objectMouseOver != null) {
            final Entity entity = AutoSoupMod.MC.objectMouseOver.entityHit;
            if (entity instanceof EntityVillager || entity instanceof EntityTameable) {
                return false;
            }
            if (AutoSoupMod.MC.objectMouseOver.getBlockPos() != null && WBlock.getBlock(AutoSoupMod.MC.objectMouseOver.getBlockPos()) instanceof BlockContainer) {
                return false;
            }
        }
        return true;
    }
    
    private void stopIfEating() {
        if (this.oldSlot == -1) {
            return;
        }
        AutoSoupMod.MC.gameSettings.keyBindUseItem.pressed = false;
        AutoSoupMod.MC.player.inventory.selectedSlot = this.oldSlot;
        this.oldSlot = -1;
    }
}
