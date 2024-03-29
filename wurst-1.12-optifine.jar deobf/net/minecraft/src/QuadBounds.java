// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import net.minecraft.util.EnumFacing;

public class QuadBounds
{
    private float minX;
    private float minY;
    private float minZ;
    private float maxX;
    private float maxY;
    private float maxZ;
    
    public QuadBounds(final int[] p_i73_1_) {
        this.minX = Float.MAX_VALUE;
        this.minY = Float.MAX_VALUE;
        this.minZ = Float.MAX_VALUE;
        this.maxX = -3.4028235E38f;
        this.maxY = -3.4028235E38f;
        this.maxZ = -3.4028235E38f;
        final int i = p_i73_1_.length / 4;
        for (int j = 0; j < 4; ++j) {
            final int k = j * i;
            final float f = Float.intBitsToFloat(p_i73_1_[k + 0]);
            final float f2 = Float.intBitsToFloat(p_i73_1_[k + 1]);
            final float f3 = Float.intBitsToFloat(p_i73_1_[k + 2]);
            if (this.minX > f) {
                this.minX = f;
            }
            if (this.minY > f2) {
                this.minY = f2;
            }
            if (this.minZ > f3) {
                this.minZ = f3;
            }
            if (this.maxX < f) {
                this.maxX = f;
            }
            if (this.maxY < f2) {
                this.maxY = f2;
            }
            if (this.maxZ < f3) {
                this.maxZ = f3;
            }
        }
    }
    
    public float getMinX() {
        return this.minX;
    }
    
    public float getMinY() {
        return this.minY;
    }
    
    public float getMinZ() {
        return this.minZ;
    }
    
    public float getMaxX() {
        return this.maxX;
    }
    
    public float getMaxY() {
        return this.maxY;
    }
    
    public float getMaxZ() {
        return this.maxZ;
    }
    
    public boolean isFaceQuad(final EnumFacing p_isFaceQuad_1_) {
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        switch (p_isFaceQuad_1_) {
            case DOWN: {
                f = this.getMinY();
                f2 = this.getMaxY();
                f3 = 0.0f;
                break;
            }
            case UP: {
                f = this.getMinY();
                f2 = this.getMaxY();
                f3 = 1.0f;
                break;
            }
            case NORTH: {
                f = this.getMinZ();
                f2 = this.getMaxZ();
                f3 = 0.0f;
                break;
            }
            case SOUTH: {
                f = this.getMinZ();
                f2 = this.getMaxZ();
                f3 = 1.0f;
                break;
            }
            case WEST: {
                f = this.getMinX();
                f2 = this.getMaxX();
                f3 = 0.0f;
                break;
            }
            case EAST: {
                f = this.getMinX();
                f2 = this.getMaxX();
                f3 = 1.0f;
                break;
            }
            default: {
                return false;
            }
        }
        return f == f3 && f2 == f3;
    }
    
    public boolean isFullQuad(final EnumFacing p_isFullQuad_1_) {
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        float f4 = 0.0f;
        switch (p_isFullQuad_1_) {
            case DOWN:
            case UP: {
                f = this.getMinX();
                f2 = this.getMaxX();
                f3 = this.getMinZ();
                f4 = this.getMaxZ();
                break;
            }
            case NORTH:
            case SOUTH: {
                f = this.getMinX();
                f2 = this.getMaxX();
                f3 = this.getMinY();
                f4 = this.getMaxY();
                break;
            }
            case WEST:
            case EAST: {
                f = this.getMinY();
                f2 = this.getMaxY();
                f3 = this.getMinZ();
                f4 = this.getMaxZ();
                break;
            }
            default: {
                return false;
            }
        }
        return f == 0.0f && f2 == 1.0f && f3 == 0.0f && f4 == 1.0f;
    }
}
