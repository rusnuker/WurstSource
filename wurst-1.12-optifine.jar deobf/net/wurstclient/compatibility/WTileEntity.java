// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.block.BlockChest;
import net.minecraft.tileentity.TileEntityChest;

public final class WTileEntity
{
    public static boolean isTrappedChest(final TileEntityChest chest) {
        return chest.getChestType() == BlockChest.Type.TRAP;
    }
}
