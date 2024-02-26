// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WPotionEffects;
import net.wurstclient.utils.InventoryUtils;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "AutoPotion", "auto potion", "AutoSplashPotion", "auto splash potion" })
public final class AutoPotionHack extends Hack implements UpdateListener
{
    private final SliderSetting health;
    private int timer;
    
    public AutoPotionHack() {
        super("AutoPotion", "Automatically throws splash potions of\ninstant health when your health is low.");
        this.health = new SliderSetting("Health", "Throws a potion when your health\nreaches this value or falls below it.", 6.0, 0.5, 9.5, 0.5, v -> String.valueOf(SliderSetting.ValueDisplay.DECIMAL.getValueString(v)) + " hearts");
        this.setCategory(Category.COMBAT);
        this.addSetting(this.health);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { AutoPotionHack.WURST.hax.autoSoupMod, AutoPotionHack.WURST.hax.potionSaverMod };
    }
    
    @Override
    public void onEnable() {
        AutoPotionHack.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoPotionHack.EVENTS.remove(UpdateListener.class, this);
        this.timer = 0;
    }
    
    @Override
    public void onUpdate() {
        final int potionInHotbar = this.findPotion(0, 9);
        if (potionInHotbar == -1) {
            final int potionInInventory = this.findPotion(9, 36);
            if (potionInInventory != -1) {
                WPlayerController.windowClick_QUICK_MOVE(potionInInventory);
            }
            return;
        }
        if (this.timer > 0) {
            --this.timer;
            return;
        }
        if (AutoPotionHack.MC.player.getHealth() > this.health.getValueF() * 2.0f) {
            return;
        }
        final int oldSlot = AutoPotionHack.MC.player.inventory.selectedSlot;
        AutoPotionHack.MC.player.inventory.selectedSlot = potionInHotbar;
        WConnection.sendPacket(new CPacketPlayer.Rotation(AutoPotionHack.MC.player.rotationYaw, 90.0f, AutoPotionHack.MC.player.onGround));
        WPlayerController.processRightClick();
        AutoPotionHack.MC.player.inventory.selectedSlot = oldSlot;
        WConnection.sendPacket(new CPacketPlayer.Rotation(AutoPotionHack.MC.player.rotationYaw, AutoPotionHack.MC.player.rotationPitch, AutoPotionHack.MC.player.onGround));
        this.timer = 10;
    }
    
    private int findPotion(final int startSlot, final int endSlot) {
        for (int i = startSlot; i < endSlot; ++i) {
            final ItemStack stack = AutoPotionHack.MC.player.inventory.getInvStack(i);
            if (InventoryUtils.isSplashPotion(stack)) {
                if (InventoryUtils.hasEffect(stack, WPotionEffects.INSTANT_HEALTH)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
