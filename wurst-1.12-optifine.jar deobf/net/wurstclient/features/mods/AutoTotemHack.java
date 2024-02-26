// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods;

import net.minecraft.init.Items;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.compatibility.WItem;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto totem" })
public final class AutoTotemHack extends Hack implements UpdateListener
{
    private final CheckboxSetting showCounter;
    private int nextTickSlot;
    private int totems;
    
    public AutoTotemHack() {
        super("AutoTotem", "Automatically moves totems of undying to your off-hand.");
        this.showCounter = new CheckboxSetting("Show totem counter", "Displays the number of totems you have.", true);
        this.setCategory(Category.COMBAT);
        this.addSetting(this.showCounter);
    }
    
    @Override
    public String getRenderName() {
        if (!this.showCounter.isChecked()) {
            return this.getName();
        }
        switch (this.totems) {
            case 1: {
                return String.valueOf(this.getName()) + " [1 totem]";
            }
            default: {
                return String.valueOf(this.getName()) + " [" + this.totems + " totems]";
            }
        }
    }
    
    @Override
    public void onEnable() {
        this.nextTickSlot = -1;
        this.totems = 0;
        AutoTotemHack.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoTotemHack.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        this.finishMovingTotem();
        final InventoryPlayer inventory = AutoTotemHack.MC.player.inventory;
        final int nextTotemSlot = this.searchForTotems(inventory);
        final ItemStack offhandStack = inventory.getInvStack(40);
        if (this.isTotem(offhandStack)) {
            ++this.totems;
            return;
        }
        if (AutoTotemHack.MC.currentScreen instanceof GuiContainer && !(AutoTotemHack.MC.currentScreen instanceof InventoryEffectRenderer)) {
            return;
        }
        if (nextTotemSlot != -1) {
            this.moveTotem(nextTotemSlot, offhandStack);
        }
    }
    
    private void moveTotem(final int nextTotemSlot, final ItemStack offhandStack) {
        final boolean offhandEmpty = WItem.isNullOrEmpty(offhandStack);
        WPlayerController.windowClick_PICKUP(nextTotemSlot);
        WPlayerController.windowClick_PICKUP(45);
        if (!offhandEmpty) {
            this.nextTickSlot = nextTotemSlot;
        }
    }
    
    private void finishMovingTotem() {
        if (this.nextTickSlot == -1) {
            return;
        }
        WPlayerController.windowClick_PICKUP(this.nextTickSlot);
        this.nextTickSlot = -1;
    }
    
    private int searchForTotems(final InventoryPlayer inventory) {
        this.totems = 0;
        int nextTotemSlot = -1;
        for (int slot = 0; slot <= 36; ++slot) {
            if (this.isTotem(inventory.getInvStack(slot))) {
                ++this.totems;
                if (nextTotemSlot == -1) {
                    nextTotemSlot = ((slot < 9) ? (slot + 36) : slot);
                }
            }
        }
        return nextTotemSlot;
    }
    
    private boolean isTotem(final ItemStack stack) {
        return stack.getItem() == Items.TOTEM_OF_UNDYING;
    }
}
