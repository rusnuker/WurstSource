// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;

public final class WPlayerController
{
    private static PlayerControllerMP getPlayerController() {
        return Minecraft.getMinecraft().playerController;
    }
    
    public static ItemStack windowClick_PICKUP(final int slot) {
        return getPlayerController().windowClick(0, slot, 0, ClickType.PICKUP, WMinecraft.getPlayer());
    }
    
    public static ItemStack windowClick_QUICK_MOVE(final int slot) {
        return getPlayerController().windowClick(0, slot, 0, ClickType.QUICK_MOVE, WMinecraft.getPlayer());
    }
    
    public static ItemStack windowClick_THROW(final int slot) {
        return getPlayerController().windowClick(0, slot, 1, ClickType.THROW, WMinecraft.getPlayer());
    }
    
    public static void processRightClick() {
        getPlayerController().processRightClick(WMinecraft.getPlayer(), WMinecraft.getWorld(), EnumHand.MAIN_HAND);
    }
    
    public static void processRightClickBlock(final BlockPos pos, final EnumFacing side, final Vec3d hitVec) {
        getPlayerController().processRightClickBlock(WMinecraft.getPlayer(), WMinecraft.getWorld(), pos, side, hitVec, EnumHand.MAIN_HAND);
    }
}
