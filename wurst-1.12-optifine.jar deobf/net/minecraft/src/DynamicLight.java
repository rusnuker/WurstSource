// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.util.Iterator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.RenderGlobal;
import java.util.HashSet;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import net.minecraft.entity.Entity;

public class DynamicLight
{
    private Entity entity;
    private double offsetY;
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private int lastLightLevel;
    private boolean underwater;
    private long timeCheckMs;
    private Set<BlockPos> setLitChunkPos;
    private BlockPos.MutableBlockPos blockPosMutable;
    
    public DynamicLight(final Entity p_i33_1_) {
        this.entity = null;
        this.offsetY = 0.0;
        this.lastPosX = -2.147483648E9;
        this.lastPosY = -2.147483648E9;
        this.lastPosZ = -2.147483648E9;
        this.lastLightLevel = 0;
        this.underwater = false;
        this.timeCheckMs = 0L;
        this.setLitChunkPos = new HashSet<BlockPos>();
        this.blockPosMutable = new BlockPos.MutableBlockPos();
        this.entity = p_i33_1_;
        this.offsetY = p_i33_1_.getEyeHeight();
    }
    
    public void update(final RenderGlobal p_update_1_) {
        if (Config.isDynamicLightsFast()) {
            final long i = System.currentTimeMillis();
            if (i < this.timeCheckMs + 500L) {
                return;
            }
            this.timeCheckMs = i;
        }
        final double d6 = this.entity.posX - 0.5;
        final double d7 = this.entity.posY - 0.5 + this.offsetY;
        final double d8 = this.entity.posZ - 0.5;
        final int j = DynamicLights.getLightLevel(this.entity);
        final double d9 = d6 - this.lastPosX;
        final double d10 = d7 - this.lastPosY;
        final double d11 = d8 - this.lastPosZ;
        final double d12 = 0.1;
        if (Math.abs(d9) > d12 || Math.abs(d10) > d12 || Math.abs(d11) > d12 || this.lastLightLevel != j) {
            this.lastPosX = d6;
            this.lastPosY = d7;
            this.lastPosZ = d8;
            this.lastLightLevel = j;
            this.underwater = false;
            final World world = p_update_1_.getWorld();
            if (world != null) {
                this.blockPosMutable.setPos(MathHelper.floor(d6), MathHelper.floor(d7), MathHelper.floor(d8));
                final IBlockState iblockstate = world.getBlockState(this.blockPosMutable);
                final Block block = iblockstate.getBlock();
                this.underwater = (block == Blocks.WATER);
            }
            final Set<BlockPos> set = new HashSet<BlockPos>();
            if (j > 0) {
                final EnumFacing enumfacing2 = ((MathHelper.floor(d6) & 0xF) >= 8) ? EnumFacing.EAST : EnumFacing.WEST;
                final EnumFacing enumfacing3 = ((MathHelper.floor(d7) & 0xF) >= 8) ? EnumFacing.UP : EnumFacing.DOWN;
                final EnumFacing enumfacing4 = ((MathHelper.floor(d8) & 0xF) >= 8) ? EnumFacing.SOUTH : EnumFacing.NORTH;
                final BlockPos blockpos = new BlockPos(d6, d7, d8);
                final RenderChunk renderchunk = p_update_1_.getRenderChunk(blockpos);
                final BlockPos blockpos2 = this.getChunkPos(renderchunk, blockpos, enumfacing2);
                final RenderChunk renderchunk2 = p_update_1_.getRenderChunk(blockpos2);
                final BlockPos blockpos3 = this.getChunkPos(renderchunk, blockpos, enumfacing4);
                final RenderChunk renderchunk3 = p_update_1_.getRenderChunk(blockpos3);
                final BlockPos blockpos4 = this.getChunkPos(renderchunk2, blockpos2, enumfacing4);
                final RenderChunk renderchunk4 = p_update_1_.getRenderChunk(blockpos4);
                final BlockPos blockpos5 = this.getChunkPos(renderchunk, blockpos, enumfacing3);
                final RenderChunk renderchunk5 = p_update_1_.getRenderChunk(blockpos5);
                final BlockPos blockpos6 = this.getChunkPos(renderchunk5, blockpos5, enumfacing2);
                final RenderChunk renderchunk6 = p_update_1_.getRenderChunk(blockpos6);
                final BlockPos blockpos7 = this.getChunkPos(renderchunk5, blockpos5, enumfacing4);
                final RenderChunk renderchunk7 = p_update_1_.getRenderChunk(blockpos7);
                final BlockPos blockpos8 = this.getChunkPos(renderchunk6, blockpos6, enumfacing4);
                final RenderChunk renderchunk8 = p_update_1_.getRenderChunk(blockpos8);
                this.updateChunkLight(renderchunk, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk2, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk3, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk4, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk5, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk6, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk7, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk8, this.setLitChunkPos, set);
            }
            this.updateLitChunks(p_update_1_);
            this.setLitChunkPos = set;
        }
    }
    
    private BlockPos getChunkPos(final RenderChunk p_getChunkPos_1_, final BlockPos p_getChunkPos_2_, final EnumFacing p_getChunkPos_3_) {
        return (p_getChunkPos_1_ != null) ? p_getChunkPos_1_.getBlockPosOffset16(p_getChunkPos_3_) : p_getChunkPos_2_.offset(p_getChunkPos_3_, 16);
    }
    
    private void updateChunkLight(final RenderChunk p_updateChunkLight_1_, final Set<BlockPos> p_updateChunkLight_2_, final Set<BlockPos> p_updateChunkLight_3_) {
        if (p_updateChunkLight_1_ != null) {
            final CompiledChunk compiledchunk = p_updateChunkLight_1_.getCompiledChunk();
            if (compiledchunk != null && !compiledchunk.isEmpty()) {
                p_updateChunkLight_1_.setNeedsUpdate(false);
            }
            final BlockPos blockpos = p_updateChunkLight_1_.getPosition().toImmutable();
            if (p_updateChunkLight_2_ != null) {
                p_updateChunkLight_2_.remove(blockpos);
            }
            if (p_updateChunkLight_3_ != null) {
                p_updateChunkLight_3_.add(blockpos);
            }
        }
    }
    
    public void updateLitChunks(final RenderGlobal p_updateLitChunks_1_) {
        for (final BlockPos blockpos : this.setLitChunkPos) {
            final RenderChunk renderchunk = p_updateLitChunks_1_.getRenderChunk(blockpos);
            this.updateChunkLight(renderchunk, null, null);
        }
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    public double getLastPosX() {
        return this.lastPosX;
    }
    
    public double getLastPosY() {
        return this.lastPosY;
    }
    
    public double getLastPosZ() {
        return this.lastPosZ;
    }
    
    public int getLastLightLevel() {
        return this.lastLightLevel;
    }
    
    public boolean isUnderwater() {
        return this.underwater;
    }
    
    public double getOffsetY() {
        return this.offsetY;
    }
    
    @Override
    public String toString() {
        return "Entity: " + this.entity + ", offsetY: " + this.offsetY;
    }
}
