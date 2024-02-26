// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import net.minecraft.util.math.Vec3d;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;

public class PlayerControllerOF extends PlayerControllerMP
{
    private boolean acting;
    
    public PlayerControllerOF(final Minecraft p_i69_1_, final NetHandlerPlayClient p_i69_2_) {
        super(p_i69_1_, p_i69_2_);
        this.acting = false;
    }
    
    @Override
    public boolean clickBlock(final BlockPos loc, final EnumFacing face) {
        this.acting = true;
        final boolean flag = super.clickBlock(loc, face);
        this.acting = false;
        return flag;
    }
    
    @Override
    public boolean onPlayerDamageBlock(final BlockPos posBlock, final EnumFacing directionFacing) {
        this.acting = true;
        final boolean flag = super.onPlayerDamageBlock(posBlock, directionFacing);
        this.acting = false;
        return flag;
    }
    
    @Override
    public EnumActionResult processRightClick(final EntityPlayer player, final World worldIn, final EnumHand stack) {
        this.acting = true;
        final EnumActionResult enumactionresult = super.processRightClick(player, worldIn, stack);
        this.acting = false;
        return enumactionresult;
    }
    
    @Override
    public EnumActionResult processRightClickBlock(final EntityPlayerSP player, final WorldClient worldIn, final BlockPos stack, final EnumFacing pos, final Vec3d facing, final EnumHand vec) {
        this.acting = true;
        final EnumActionResult enumactionresult = super.processRightClickBlock(player, worldIn, stack, pos, facing, vec);
        this.acting = false;
        return enumactionresult;
    }
    
    public boolean isActing() {
        return this.acting;
    }
}
