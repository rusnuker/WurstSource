// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.chunk.RenderChunk;
import java.util.List;

public abstract class ChunkRenderContainer
{
    private double viewEntityX;
    private double viewEntityY;
    private double viewEntityZ;
    protected List<RenderChunk> renderChunks;
    protected boolean initialized;
    
    public ChunkRenderContainer() {
        this.renderChunks = Lists.newArrayListWithCapacity(17424);
    }
    
    public void initialize(final double viewEntityXIn, final double viewEntityYIn, final double viewEntityZIn) {
        this.initialized = true;
        this.renderChunks.clear();
        this.viewEntityX = viewEntityXIn;
        this.viewEntityY = viewEntityYIn;
        this.viewEntityZ = viewEntityZIn;
    }
    
    public void preRenderChunk(final RenderChunk renderChunkIn) {
        final BlockPos blockpos = renderChunkIn.getPosition();
        GlStateManager.translate((float)(blockpos.getX() - this.viewEntityX), (float)(blockpos.getY() - this.viewEntityY), (float)(blockpos.getZ() - this.viewEntityZ));
    }
    
    public void addRenderChunk(final RenderChunk renderChunkIn, final BlockRenderLayer layer) {
        this.renderChunks.add(renderChunkIn);
    }
    
    public abstract void renderChunkLayer(final BlockRenderLayer p0);
}
