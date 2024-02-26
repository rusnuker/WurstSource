// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.compatibility.WEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/enchant")
public final class EnchantCmd extends Command
{
    public EnchantCmd() {
        super("enchant", "Enchants items with everything.", new String[] { "[all]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (!EnchantCmd.MC.player.abilities.creativeMode) {
            throw new CmdError("Creative mode only.");
        }
        if (args.length == 0) {
            final ItemStack currentItem = EnchantCmd.MC.player.inventory.getCurrentItem();
            if (currentItem == null) {
                throw new CmdError("There is no item in your hand.");
            }
            for (final Enchantment enchantment : Enchantment.REGISTRY) {
                try {
                    if (enchantment == WEnchantments.SILK_TOUCH) {
                        continue;
                    }
                    currentItem.addEnchantment(enchantment, 127);
                }
                catch (final Exception ex) {}
            }
        }
        else {
            if (!args[0].equals("all")) {
                throw new CmdSyntaxError();
            }
            int items = 0;
            for (int i = 0; i < 40; ++i) {
                final ItemStack currentItem2 = EnchantCmd.MC.player.inventory.getInvStack(i);
                if (currentItem2 != null) {
                    ++items;
                    for (final Enchantment enchantment2 : Enchantment.REGISTRY) {
                        try {
                            if (enchantment2 == WEnchantments.SILK_TOUCH) {
                                continue;
                            }
                            currentItem2.addEnchantment(enchantment2, 127);
                        }
                        catch (final Exception ex2) {}
                    }
                }
            }
            if (items == 1) {
                ChatUtils.message("Enchanted 1 item.");
            }
            else {
                ChatUtils.message("Enchanted " + items + " items.");
            }
        }
    }
    
    @Override
    public String getPrimaryAction() {
        return "Enchant Current Item";
    }
    
    @Override
    public void doPrimaryAction() {
        EnchantCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".enchant", true));
    }
}
