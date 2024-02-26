// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.retro;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.Category;
import net.wurstclient.compatibility.WPotionEffects;
import net.minecraft.potion.Potion;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.RetroMod;

@SearchTags({ "NoPotion", "Zoot", "anti potions", "no potions" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class AntiPotionMod extends RetroMod implements UpdateListener
{
    private final Potion[] blockedEffects;
    
    public AntiPotionMod() {
        super("AntiPotion", "Blocks bad potion effects.");
        this.blockedEffects = new Potion[] { WPotionEffects.HUNGER, WPotionEffects.SLOWNESS, WPotionEffects.MINING_FATIGUE, WPotionEffects.INSTANT_DAMAGE, WPotionEffects.NAUSEA, WPotionEffects.BLINDNESS, WPotionEffects.WEAKNESS, WPotionEffects.WITHER, WPotionEffects.POISON };
        this.setCategory(Category.RETRO);
    }
    
    @Override
    public void onEnable() {
        AntiPotionMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AntiPotionMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (AntiPotionMod.MC.player.abilities.creativeMode) {
            return;
        }
        if (!AntiPotionMod.MC.player.onGround) {
            return;
        }
        if (!this.hasBadEffect()) {
            return;
        }
        for (int i = 0; i < 1000; ++i) {
            WConnection.sendPacket(new CPacketPlayer());
        }
    }
    
    private boolean hasBadEffect() {
        if (AntiPotionMod.MC.player.getActivePotionEffects().isEmpty()) {
            return false;
        }
        Potion[] blockedEffects;
        for (int length = (blockedEffects = this.blockedEffects).length, i = 0; i < length; ++i) {
            final Potion effect = blockedEffects[i];
            if (AntiPotionMod.MC.player.isPotionActive(effect)) {
                return true;
            }
        }
        return false;
    }
}
