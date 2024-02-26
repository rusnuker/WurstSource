// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.material.Material;

public class BlockHardenedClay extends Block
{
    public BlockHardenedClay() {
        super(Material.ROCK);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state, final IBlockAccess p_180659_2_, final BlockPos p_180659_3_) {
        return MapColor.ADOBE;
    }
}
