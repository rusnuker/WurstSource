// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import net.wurstclient.compatibility.WPlayerController;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import java.util.Random;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "item generator", "drop infinite" })
public final class ItemGeneratorHack extends Hack implements UpdateListener
{
    private final SliderSetting speed;
    private final SliderSetting stackSize;
    private final Random random;
    
    public ItemGeneratorHack() {
        super("ItemGenerator", "Generates random items and drops them on the ground.\n§oCreative mode only.§r");
        this.speed = new SliderSetting("Speed", "§4§lWARNING:§r High speeds will cause a ton\nof lag and can easily crash the game!", 1.0, 1.0, 36.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.stackSize = new SliderSetting("Stack size", "How many items to place in each stack.\nDoesn't seem to affect performance.", 1.0, 1.0, 64.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.random = new Random();
        this.setCategory(Category.ITEMS);
        this.addSetting(this.speed);
        this.addSetting(this.stackSize);
    }
    
    @Override
    public void onEnable() {
        ItemGeneratorHack.EVENTS.add(UpdateListener.class, this);
        if (!ItemGeneratorHack.MC.player.abilities.creativeMode) {
            ChatUtils.error("Creative mode only.");
            this.setEnabled(false);
        }
    }
    
    @Override
    public void onDisable() {
        ItemGeneratorHack.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        final int stacks = this.speed.getValueI();
        for (int i = 9; i < 9 + stacks; ++i) {
            final Item item = Item.REGISTRY.getRandomObject(this.random);
            final ItemStack stack = new ItemStack(item, this.stackSize.getValueI());
            final CPacketCreativeInventoryAction packet = new CPacketCreativeInventoryAction(i, stack);
            WConnection.sendPacket(packet);
        }
        for (int i = 9; i < 9 + stacks; ++i) {
            WPlayerController.windowClick_THROW(i);
        }
    }
}
