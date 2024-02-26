// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.block.BlockFalling;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.Box;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public final class WBlock
{
    public static IBlockState getState(final BlockPos pos) {
        return WMinecraft.getWorld().getBlockState(pos);
    }
    
    public static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }
    
    public static int getId(final BlockPos pos) {
        return Block.getIdFromBlock(getBlock(pos));
    }
    
    public static String getName(final Block block) {
        return new StringBuilder().append(Block.REGISTRY.getNameForObject(block)).toString();
    }
    
    public static Material getMaterial(final BlockPos pos) {
        return getState(pos).getMaterial();
    }
    
    public static int getIntegerProperty(final IBlockState state, final PropertyInteger prop) {
        return state.getValue((IProperty<Integer>)prop);
    }
    
    public static Box getBoundingBox(final BlockPos pos) {
        return getState(pos).getBoundingBox(WMinecraft.getWorld(), pos).offset(pos);
    }
    
    public static boolean canBeClicked(final BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }
    
    public static float getHardness(final BlockPos pos) {
        return getState(pos).getPlayerRelativeBlockHardness(WMinecraft.getPlayer(), WMinecraft.getWorld(), pos);
    }
    
    public static boolean canFallThrough(final BlockPos pos) {
        return BlockFalling.canFallThrough(getState(pos));
    }
    
    public static boolean isFullyOpaque(final BlockPos pos) {
        return getState(pos).isFullyOpaque();
    }
}
