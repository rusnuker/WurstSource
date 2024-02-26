// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WPlayerController;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.ItemListSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto drop", "AutoEject", "auto-eject", "auto eject", "InventoryCleaner", "inventory cleaner", "InvCleaner", "inv cleaner" })
public final class AutoDropHack extends Hack implements UpdateListener
{
    private ItemListSetting items;
    private final String renderName;
    
    public AutoDropHack() {
        super("AutoDrop", "Automatically drops unwanted items.");
        this.items = new ItemListSetting("Items", "Unwanted items that will be dropped.", new String[] { "minecraft:poisonous_potato", "minecraft:red_flower", "minecraft:rotten_flesh", "minecraft:wheat_seeds", "minecraft:yellow_flower" });
        this.renderName = ((Math.random() < 0.01) ? "AutoLinus" : this.getName());
        this.setCategory(Category.ITEMS);
        this.addSetting(this.items);
    }
    
    @Override
    public String getRenderName() {
        return this.renderName;
    }
    
    @Override
    public void onEnable() {
        AutoDropHack.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoDropHack.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        for (int slot = 9; slot < 45; ++slot) {
            int adjustedSlot = slot;
            if (adjustedSlot >= 36) {
                adjustedSlot -= 36;
            }
            final ItemStack stack = AutoDropHack.MC.player.inventory.getInvStack(adjustedSlot);
            if (!WItem.isNullOrEmpty(stack)) {
                final Item item = stack.getItem();
                final String itemName = Item.REGISTRY.getNameForObject(item).toString();
                if (this.items.getItemNames().contains(itemName)) {
                    WPlayerController.windowClick_THROW(slot);
                }
            }
        }
    }
}
