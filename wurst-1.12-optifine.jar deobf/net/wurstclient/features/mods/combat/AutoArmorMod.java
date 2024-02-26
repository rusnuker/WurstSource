// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.wurstclient.compatibility.WEnchantments;
import net.wurstclient.compatibility.WEntityEquipmentSlot;
import net.minecraft.network.play.client.CPacketClickWindow;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.compatibility.WPlayerController;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.item.ItemArmor;
import net.wurstclient.compatibility.WItem;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.PacketOutputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto armor" })
@Bypasses
public final class AutoArmorMod extends Hack implements UpdateListener, PacketOutputListener
{
    private final CheckboxSetting useEnchantments;
    private final CheckboxSetting swapWhileMoving;
    private final SliderSetting delay;
    private int timer;
    
    public AutoArmorMod() {
        super("AutoArmor", "Manages your armor automatically.");
        this.useEnchantments = new CheckboxSetting("Use enchantments", "Whether or not to consider the Protection\nenchantment when calculating armor strength.", true);
        this.swapWhileMoving = new CheckboxSetting("Swap while moving", "Whether or not to swap armor pieces\nwhile the player is moving.\n\n" + ChatFormatting.RED + ChatFormatting.BOLD + "WARNING:" + ChatFormatting.RESET + " This would not be possible\n" + "without cheats. It may raise suspicion.", false);
        this.delay = new SliderSetting("Delay", "Amount of ticks to wait before swapping\nthe next piece of armor.", 2.0, 0.0, 20.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.setCategory(Category.COMBAT);
        this.addSetting(this.useEnchantments);
        this.addSetting(this.swapWhileMoving);
        this.addSetting(this.delay);
    }
    
    @Override
    public void onEnable() {
        this.timer = 0;
        AutoArmorMod.EVENTS.add(UpdateListener.class, this);
        AutoArmorMod.EVENTS.add(PacketOutputListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoArmorMod.EVENTS.remove(UpdateListener.class, this);
        AutoArmorMod.EVENTS.remove(PacketOutputListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (this.timer > 0) {
            --this.timer;
            return;
        }
        if (AutoArmorMod.MC.currentScreen instanceof GuiContainer && !(AutoArmorMod.MC.currentScreen instanceof InventoryEffectRenderer)) {
            return;
        }
        final EntityPlayerSP player = AutoArmorMod.MC.player;
        final InventoryPlayer inventory = player.inventory;
        if (!this.swapWhileMoving.isChecked() && (player.movementInput.moveForward != 0.0f || player.movementInput.moveStrafe != 0.0f)) {
            return;
        }
        final int[] bestArmorSlots = new int[4];
        final int[] bestArmorValues = new int[4];
        for (int type = 0; type < 4; ++type) {
            bestArmorSlots[type] = -1;
            final ItemStack stack = inventory.armorItemInSlot(type);
            if (!WItem.isNullOrEmpty(stack)) {
                if (stack.getItem() instanceof ItemArmor) {
                    final ItemArmor item = (ItemArmor)stack.getItem();
                    bestArmorValues[type] = this.getArmorValue(item, stack);
                }
            }
        }
        for (int slot = 0; slot < 36; ++slot) {
            final ItemStack stack = inventory.getInvStack(slot);
            if (!WItem.isNullOrEmpty(stack)) {
                if (stack.getItem() instanceof ItemArmor) {
                    final ItemArmor item = (ItemArmor)stack.getItem();
                    final int armorType = WItem.getArmorType(item);
                    final int armorValue = this.getArmorValue(item, stack);
                    if (armorValue > bestArmorValues[armorType]) {
                        bestArmorSlots[armorType] = slot;
                        bestArmorValues[armorType] = armorValue;
                    }
                }
            }
        }
        final ArrayList<Integer> types = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(types);
        for (final int type2 : types) {
            int slot2 = bestArmorSlots[type2];
            if (slot2 == -1) {
                continue;
            }
            final ItemStack oldArmor = inventory.armorItemInSlot(type2);
            if (!WItem.isNullOrEmpty(oldArmor) && inventory.getFirstEmptyStack() == -1) {
                continue;
            }
            if (slot2 < 9) {
                slot2 += 36;
            }
            if (!WItem.isNullOrEmpty(oldArmor)) {
                WPlayerController.windowClick_QUICK_MOVE(8 - type2);
            }
            WPlayerController.windowClick_QUICK_MOVE(slot2);
            break;
        }
    }
    
    @Override
    public void onSentPacket(final PacketOutputEvent event) {
        if (event.getPacket() instanceof CPacketClickWindow) {
            this.timer = this.delay.getValueI();
        }
    }
    
    private int getArmorValue(final ItemArmor item, final ItemStack stack) {
        final int armorPoints = item.damageReduceAmount;
        int prtPoints = 0;
        final int armorToughness = (int)WItem.getArmorToughness(item);
        final int armorType = item.getArmorMaterial().getDamageReductionAmount(WEntityEquipmentSlot.LEGS);
        if (this.useEnchantments.isChecked()) {
            final Enchantment protection = WEnchantments.PROTECTION;
            final int prtLvl = WEnchantments.getEnchantmentLevel(protection, stack);
            final EntityPlayerSP player = AutoArmorMod.MC.player;
            final DamageSource dmgSource = DamageSource.causePlayerDamage(player);
            prtPoints = protection.calcModifierDamage(prtLvl, dmgSource);
        }
        return armorPoints * 5 + prtPoints * 3 + armorToughness + armorType;
    }
}
