// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.math.RayTraceResult;
import net.wurstclient.compatibility.WPotion;
import net.wurstclient.compatibility.WPotionEffects;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import java.util.Comparator;
import net.minecraft.item.ItemFood;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto eat", "AutoFood", "auto food", "AutoFeeder", "auto feeder", "AutoFeeding", "auto feeding", "AutoSoup", "auto soup" })
public final class AutoEatMod extends Hack implements UpdateListener
{
    private final CheckboxSetting eatWhileWalking;
    private final EnumSetting<FoodPriority> foodPriority;
    private final CheckboxSetting allowHunger;
    private final CheckboxSetting allowPoison;
    private final CheckboxSetting allowChorus;
    private int oldSlot;
    
    public AutoEatMod() {
        super("AutoEat", "Automatically eats food when necessary.");
        this.eatWhileWalking = new CheckboxSetting("Eat while walking", "Slows you down, not recommended.", false);
        this.foodPriority = new EnumSetting<FoodPriority>("Prefer food with", FoodPriority.values(), FoodPriority.HIGH_SATURATION);
        this.allowHunger = new CheckboxSetting("Allow hunger effect", "Rotten flesh applies a harmless 'hunger' effect.\nIt is safe to eat and useful as emergency food.", true);
        this.allowPoison = new CheckboxSetting("Allow poison effect", "Poisoned food applies damage over time.\nNot recommended.", false);
        this.allowChorus = new CheckboxSetting("Allow chorus fruit", "Eating chorus fruit teleports you to a random location.\nNot recommended.", false);
        this.oldSlot = -1;
        this.setCategory(Category.ITEMS);
        this.addSetting(this.eatWhileWalking);
        this.addSetting(this.foodPriority);
        this.addSetting(this.allowHunger);
        this.addSetting(this.allowPoison);
        if (this.allowChorus != null) {
            this.addSetting(this.allowChorus);
        }
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { AutoEatMod.WURST.hax.autoSoupMod };
    }
    
    @Override
    public void onEnable() {
        AutoEatMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoEatMod.EVENTS.remove(UpdateListener.class, this);
        this.stopIfEating();
    }
    
    @Override
    public void onUpdate() {
        if (!this.shouldEat()) {
            this.stopIfEating();
            return;
        }
        final int bestSlot = this.getBestSlot();
        if (bestSlot == -1) {
            this.stopIfEating();
            return;
        }
        if (!this.isEating()) {
            this.oldSlot = AutoEatMod.MC.player.inventory.selectedSlot;
        }
        AutoEatMod.MC.player.inventory.selectedSlot = bestSlot;
        AutoEatMod.MC.gameSettings.keyBindUseItem.pressed = true;
        WPlayerController.processRightClick();
    }
    
    private int getBestSlot() {
        int bestSlot = -1;
        ItemFood bestFood = null;
        final Comparator<ItemFood> comparator = this.foodPriority.getSelected().comparator;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoEatMod.MC.player.inventory.getInvStack(i);
            if (!WItem.isNullOrEmpty(stack)) {
                if (stack.getItem() instanceof ItemFood) {
                    final ItemFood food = (ItemFood)stack.getItem();
                    if (this.isAllowedFood(food)) {
                        if (bestFood == null || comparator.compare(food, bestFood) > 0) {
                            bestFood = food;
                            bestSlot = i;
                        }
                    }
                }
            }
        }
        return bestSlot;
    }
    
    private boolean isAllowedFood(final ItemFood food) {
        if (this.allowChorus != null && !this.allowChorus.isChecked() && food == Items.CHORUS_FRUIT) {
            return false;
        }
        final int effect = food.getPotionId();
        return (this.allowHunger.isChecked() || effect != WPotion.getIdFromPotion(WPotionEffects.HUNGER)) && (this.allowPoison.isChecked() || effect != WPotion.getIdFromPotion(WPotionEffects.POISON));
    }
    
    private boolean shouldEat() {
        return !AutoEatMod.MC.player.abilities.creativeMode && AutoEatMod.MC.player.canEat(false) && (this.eatWhileWalking.isChecked() || (AutoEatMod.MC.player.moveForward == 0.0f && AutoEatMod.MC.player.moveStrafing == 0.0f)) && !this.isClickable(AutoEatMod.MC.objectMouseOver);
    }
    
    private boolean isClickable(final RayTraceResult hitResult) {
        if (hitResult == null) {
            return false;
        }
        if (hitResult.typeOfHit == RayTraceResult.Type.ENTITY) {
            final Entity entity = hitResult.entityHit;
            return entity instanceof EntityVillager || entity instanceof EntityTameable;
        }
        if (hitResult.typeOfHit != RayTraceResult.Type.BLOCK) {
            return false;
        }
        final BlockPos pos = hitResult.getBlockPos();
        if (pos == null) {
            return false;
        }
        final Block block = AutoEatMod.MC.world.getBlockState(pos).getBlock();
        return block instanceof BlockContainer || block instanceof BlockWorkbench;
    }
    
    public boolean isEating() {
        return this.oldSlot != -1;
    }
    
    private void stopIfEating() {
        if (!this.isEating()) {
            return;
        }
        AutoEatMod.MC.gameSettings.keyBindUseItem.pressed = false;
        AutoEatMod.MC.player.inventory.selectedSlot = this.oldSlot;
        this.oldSlot = -1;
    }
    
    public enum FoodPriority
    {
        HIGH_HUNGER("High Food Points", Comparator.comparingInt(food -> food.getHealAmount(null))), 
        HIGH_SATURATION("High Saturation", Comparator.comparingDouble(food -> food.getSaturationModifier(null))), 
        LOW_HUNGER("Low Food Points", Comparator.comparingInt(food -> food.getHealAmount(null)).reversed()), 
        LOW_SATURATION("Low Saturation", Comparator.comparingDouble(food -> food.getSaturationModifier(null)).reversed());
        
        private final String name;
        private final Comparator<ItemFood> comparator;
        
        private FoodPriority(final String name, final Comparator<ItemFood> comparator) {
            this.name = name;
            this.comparator = comparator;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
