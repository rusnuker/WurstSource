// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.gen.feature;

import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.World;

public class WorldGenPumpkin extends WorldGenerator
{
    @Override
    public boolean generate(final World worldIn, final Random rand, final BlockPos position) {
        for (int i = 0; i < 64; ++i) {
            final BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.GRASS && Blocks.PUMPKIN.canPlaceBlockAt(worldIn, blockpos)) {
                worldIn.setBlockState(blockpos, Blocks.PUMPKIN.getDefaultState().withProperty((IProperty<Comparable>)BlockPumpkin.FACING, EnumFacing.Plane.HORIZONTAL.random(rand)), 2);
            }
        }
        return true;
    }
}
