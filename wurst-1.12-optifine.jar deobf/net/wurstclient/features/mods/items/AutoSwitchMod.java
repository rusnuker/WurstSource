// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import net.minecraft.entity.player.InventoryPlayer;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto switch" })
@Bypasses
public final class AutoSwitchMod extends Hack implements UpdateListener
{
    public AutoSwitchMod() {
        super("AutoSwitch", "Switches the item in your hand all the time.\nTip: Use this in combination with BuildRandom while having a lot of different colored wool\nblocks in your hotbar.");
        this.setCategory(Category.ITEMS);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { AutoSwitchMod.WURST.hax.buildRandomMod };
    }
    
    @Override
    public void onEnable() {
        AutoSwitchMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoSwitchMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (AutoSwitchMod.MC.player.inventory.selectedSlot == 8) {
            AutoSwitchMod.MC.player.inventory.selectedSlot = 0;
        }
        else {
            final InventoryPlayer inventory = AutoSwitchMod.MC.player.inventory;
            ++inventory.selectedSlot;
        }
    }
}
