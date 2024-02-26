// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.retro;

import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.utils.InventoryUtils;
import net.minecraft.item.ItemFood;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.RetroMod;

@SearchTags({ "FastNom", "fast eat", "fast nom" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class FastEatMod extends RetroMod implements UpdateListener
{
    public FastEatMod() {
        super("FastEat", "Allows you to eat food much faster.\nOM! NOM! NOM!");
        this.setCategory(Category.RETRO);
    }
    
    @Override
    public void onEnable() {
        FastEatMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        FastEatMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (FastEatMod.MC.player.getHealth() <= 0.0f) {
            return;
        }
        if (!FastEatMod.MC.player.onGround) {
            return;
        }
        if (!FastEatMod.MC.gameSettings.keyBindUseItem.pressed) {
            return;
        }
        if (!FastEatMod.MC.player.getFoodStats().needFood()) {
            return;
        }
        if (!InventoryUtils.checkHeldItem(item -> item instanceof ItemFood)) {
            return;
        }
        for (int i = 0; i < 100; ++i) {
            WConnection.sendPacket(new CPacketPlayer(false));
        }
    }
}
