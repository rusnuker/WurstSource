// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.util.MathUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/damage")
public final class DamageCmd extends Command
{
    public DamageCmd() {
        super("damage", "Applies the given amount of damage.", new String[] { "<amount>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            throw new CmdSyntaxError();
        }
        if (!MathUtils.isInteger(args[0])) {
            throw new CmdSyntaxError("Amount must be a number.");
        }
        final int dmg = Integer.parseInt(args[0]);
        if (dmg < 1) {
            throw new CmdError("Amount must be at least 1.");
        }
        if (dmg > 40) {
            throw new CmdError("Amount must be at most 20.");
        }
        if (DamageCmd.MC.player.abilities.creativeMode) {
            throw new CmdError("Cannot damage in creative mode.");
        }
        final double posX = DamageCmd.MC.player.posX;
        final double posY = DamageCmd.MC.player.posY;
        final double posZ = DamageCmd.MC.player.posZ;
        for (int i = 0; i < 80.0 + 20.0 * (dmg - 1.0); ++i) {
            WConnection.sendPacket(new CPacketPlayer.Position(posX, posY + 0.049, posZ, false));
            WConnection.sendPacket(new CPacketPlayer.Position(posX, posY, posZ, false));
        }
        WConnection.sendPacket(new CPacketPlayer.Position(posX, posY, posZ, true));
    }
}
