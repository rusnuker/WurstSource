// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener;
import net.minecraft.item.ItemStack;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/repair")
public final class RepairCmd extends Command
{
    public RepairCmd() {
        super("repair", "Repairs the held item. Requires creative mode.", new String[0]);
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length > 0) {
            throw new CmdSyntaxError();
        }
        final EntityPlayerSP player = RepairCmd.MC.player;
        if (!player.abilities.creativeMode) {
            throw new CmdError("Creative mode only.");
        }
        final ItemStack item = player.inventory.getCurrentItem();
        if (item == null) {
            throw new CmdError("You need an item in your hand.");
        }
        if (!item.isItemStackDamageable()) {
            throw new CmdError("This item can't take damage.");
        }
        if (!item.isItemDamaged()) {
            throw new CmdError("This item is not damaged.");
        }
        item.setItemDamage(0);
        WConnection.sendPacket(new CPacketCreativeInventoryAction(36 + player.inventory.selectedSlot, item));
    }
    
    @Override
    public String getPrimaryAction() {
        return "Repair Current Item";
    }
    
    @Override
    public void doPrimaryAction() {
        RepairCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".repair", true));
    }
}
