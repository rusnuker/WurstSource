// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.item;

import net.minecraft.block.SoundType;
import net.minecraft.util.math.Box;
import net.minecraft.block.state.IBlockState;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;

public class ItemSlab extends ItemBlock
{
    private final BlockSlab singleSlab;
    private final BlockSlab doubleSlab;
    
    public ItemSlab(final Block block, final BlockSlab singleSlab, final BlockSlab doubleSlab) {
        super(block);
        this.singleSlab = singleSlab;
        this.doubleSlab = doubleSlab;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    @Override
    public int getMetadata(final int damage) {
        return damage;
    }
    
    @Override
    public String getUnlocalizedName(final ItemStack stack) {
        return this.singleSlab.getUnlocalizedName(stack.getMetadata());
    }
    
    @Override
    public EnumActionResult onItemUse(final EntityPlayer stack, final World playerIn, final BlockPos worldIn, final EnumHand pos, final EnumFacing hand, final float facing, final float hitX, final float hitY) {
        final ItemStack itemstack = stack.getHeldItem(pos);
        if (!itemstack.func_190926_b() && stack.canPlayerEdit(worldIn.offset(hand), hand, itemstack)) {
            final Comparable<?> comparable = this.singleSlab.getTypeForItem(itemstack);
            final IBlockState iblockstate = playerIn.getBlockState(worldIn);
            if (iblockstate.getBlock() == this.singleSlab) {
                final IProperty<?> iproperty = this.singleSlab.getVariantProperty();
                final Comparable<?> comparable2 = iblockstate.getValue(iproperty);
                final BlockSlab.EnumBlockHalf blockslab$enumblockhalf = iblockstate.getValue(BlockSlab.HALF);
                if (((hand == EnumFacing.UP && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM) || (hand == EnumFacing.DOWN && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.TOP)) && comparable2 == comparable) {
                    final IBlockState iblockstate2 = this.makeState(iproperty, comparable2);
                    final Box axisalignedbb = iblockstate2.getCollisionBoundingBox(playerIn, worldIn);
                    if (axisalignedbb != Block.NULL_AABB && playerIn.checkNoEntityCollision(axisalignedbb.offset(worldIn)) && playerIn.setBlockState(worldIn, iblockstate2, 11)) {
                        final SoundType soundtype = this.doubleSlab.getSoundType();
                        playerIn.playSound(stack, worldIn, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
                        itemstack.func_190918_g(1);
                        if (stack instanceof EntityPlayerMP) {
                            CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)stack, worldIn, itemstack);
                        }
                    }
                    return EnumActionResult.SUCCESS;
                }
            }
            return this.tryPlace(stack, itemstack, playerIn, worldIn.offset(hand), comparable) ? EnumActionResult.SUCCESS : super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY);
        }
        return EnumActionResult.FAIL;
    }
    
    @Override
    public boolean canPlaceBlockOnSide(final World worldIn, BlockPos pos, final EnumFacing side, final EntityPlayer player, final ItemStack stack) {
        final BlockPos blockpos = pos;
        final IProperty<?> iproperty = this.singleSlab.getVariantProperty();
        final Comparable<?> comparable = this.singleSlab.getTypeForItem(stack);
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getBlock() == this.singleSlab) {
            final boolean flag = iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;
            if (((side == EnumFacing.UP && !flag) || (side == EnumFacing.DOWN && flag)) && comparable == iblockstate.getValue(iproperty)) {
                return true;
            }
        }
        pos = pos.offset(side);
        final IBlockState iblockstate2 = worldIn.getBlockState(pos);
        return (iblockstate2.getBlock() == this.singleSlab && comparable == iblockstate2.getValue(iproperty)) || super.canPlaceBlockOnSide(worldIn, blockpos, side, player, stack);
    }
    
    private boolean tryPlace(final EntityPlayer player, final ItemStack stack, final World worldIn, final BlockPos pos, final Object itemSlabType) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getBlock() == this.singleSlab) {
            final Comparable<?> comparable = iblockstate.getValue(this.singleSlab.getVariantProperty());
            if (comparable == itemSlabType) {
                final IBlockState iblockstate2 = this.makeState(this.singleSlab.getVariantProperty(), comparable);
                final Box axisalignedbb = iblockstate2.getCollisionBoundingBox(worldIn, pos);
                if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) && worldIn.setBlockState(pos, iblockstate2, 11)) {
                    final SoundType soundtype = this.doubleSlab.getSoundType();
                    worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
                    stack.func_190918_g(1);
                }
                return true;
            }
        }
        return false;
    }
    
    protected <T extends Comparable<T>> IBlockState makeState(final IProperty<T> p_185055_1_, final Comparable<?> p_185055_2_) {
        return this.doubleSlab.getDefaultState().withProperty(p_185055_1_, p_185055_2_);
    }
}
