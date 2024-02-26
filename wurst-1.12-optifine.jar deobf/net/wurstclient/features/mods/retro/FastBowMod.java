// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.retro;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WPlayerController;
import net.minecraft.item.ItemBow;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.RetroMod;

@SearchTags({ "RapidFire", "BowSpam", "fast bow", "rapid fire", "bow spam" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class FastBowMod extends RetroMod implements UpdateListener
{
    public FastBowMod() {
        super("FastBow", "Turns your bow into a machine gun.\nTip: This also works with BowAimbot.");
        this.setCategory(Category.RETRO);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { FastBowMod.WURST.hax.bowAimbotMod };
    }
    
    @Override
    public void onEnable() {
        FastBowMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (!FastBowMod.MC.gameSettings.keyBindUseItem.pressed) {
            return;
        }
        if (!FastBowMod.MC.player.onGround && !FastBowMod.MC.player.abilities.creativeMode) {
            return;
        }
        if (FastBowMod.MC.player.getHealth() <= 0.0f) {
            return;
        }
        final ItemStack stack = FastBowMod.MC.player.inventory.getCurrentItem();
        if (WItem.isNullOrEmpty(stack) || !(stack.getItem() instanceof ItemBow)) {
            return;
        }
        WPlayerController.processRightClick();
        for (int i = 0; i < 20; ++i) {
            WConnection.sendPacket(new CPacketPlayer(false));
        }
        FastBowMod.MC.playerController.onStoppedUsingItem(FastBowMod.MC.player);
    }
    
    @Override
    public void onDisable() {
        FastBowMod.EVENTS.remove(UpdateListener.class, this);
    }
}
