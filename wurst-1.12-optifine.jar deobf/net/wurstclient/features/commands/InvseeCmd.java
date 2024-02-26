// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.entity.Entity;
import java.util.Iterator;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.events.RenderListener;
import net.wurstclient.features.Command;

@HelpPage("Commands/invsee")
public final class InvseeCmd extends Command implements RenderListener
{
    private String playerName;
    
    public InvseeCmd() {
        super("invsee", "Allows you to see parts of another player's inventory.", new String[] { "<player>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 1) {
            throw new CmdSyntaxError();
        }
        if (InvseeCmd.MC.player.abilities.creativeMode) {
            ChatUtils.error("Survival mode only.");
            return;
        }
        this.playerName = args[0];
        InvseeCmd.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onRender(final float partialTicks) {
        boolean found = false;
        for (final Object entity : WMinecraft.getWorld().loadedEntityList) {
            if (entity instanceof EntityOtherPlayerMP) {
                final EntityOtherPlayerMP player = (EntityOtherPlayerMP)entity;
                if (!player.getName().equals(this.playerName)) {
                    continue;
                }
                ChatUtils.message("Showing inventory of " + player.getName() + ".");
                InvseeCmd.MC.openScreen(new GuiInventory(player));
                found = true;
            }
        }
        if (!found) {
            ChatUtils.error("Player not found.");
        }
        this.playerName = null;
        InvseeCmd.EVENTS.remove(RenderListener.class, this);
    }
}
