// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemSword;
import net.wurstclient.utils.InventoryUtils;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto sword" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false, antiCheat = false)
public final class AutoSwordMod extends Hack implements LeftClickListener, UpdateListener
{
    private int oldSlot;
    private int timer;
    
    public AutoSwordMod() {
        super("AutoSword", "Automatically uses the best weapon in your hotbar to attack entities.\nTip: This works with Killaura.");
        this.oldSlot = -1;
        this.setCategory(Category.COMBAT);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { AutoSwordMod.WURST.hax.autoToolMod, AutoSwordMod.WURST.hax.killauraMod };
    }
    
    @Override
    public void onEnable() {
        AutoSwordMod.EVENTS.add(LeftClickListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoSwordMod.EVENTS.remove(LeftClickListener.class, this);
        if (this.oldSlot != -1) {
            AutoSwordMod.MC.player.inventory.selectedSlot = this.oldSlot;
            this.oldSlot = -1;
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.timer > 0) {
            --this.timer;
            return;
        }
        if (this.oldSlot != -1) {
            AutoSwordMod.MC.player.inventory.selectedSlot = this.oldSlot;
            this.oldSlot = -1;
        }
        AutoSwordMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onLeftClick(final LeftClickEvent event) {
        if (AutoSwordMod.MC.objectMouseOver == null || AutoSwordMod.MC.objectMouseOver.entityHit == null) {
            return;
        }
        this.setSlot();
    }
    
    public void setSlot() {
        if (!this.isActive()) {
            return;
        }
        if (AutoSwordMod.WURST.hax.autoEatMod.isEating()) {
            return;
        }
        float bestDamage = 0.0f;
        int bestSlot = -1;
        for (int i = 0; i < 9; ++i) {
            if (!InventoryUtils.isSlotEmpty(i)) {
                final Item item = AutoSwordMod.MC.player.inventory.getInvStack(i).getItem();
                float damage = 0.0f;
                if (item instanceof ItemSword) {
                    damage = ((ItemSword)item).attackDamage;
                }
                else if (item instanceof ItemTool) {
                    damage = ((ItemTool)item).damageVsEntity;
                }
                if (damage > bestDamage) {
                    bestDamage = damage;
                    bestSlot = i;
                }
            }
        }
        if (bestSlot == -1) {
            return;
        }
        if (this.oldSlot == -1) {
            this.oldSlot = AutoSwordMod.MC.player.inventory.selectedSlot;
        }
        AutoSwordMod.MC.player.inventory.selectedSlot = bestSlot;
        this.timer = 4;
        AutoSwordMod.EVENTS.add(UpdateListener.class, this);
    }
}
