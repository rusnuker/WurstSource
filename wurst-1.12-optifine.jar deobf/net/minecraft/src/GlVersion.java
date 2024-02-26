// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

public class GlVersion
{
    private int major;
    private int minor;
    private int release;
    private String suffix;
    
    public GlVersion(final int p_i40_1_, final int p_i40_2_) {
        this(p_i40_1_, p_i40_2_, 0);
    }
    
    public GlVersion(final int p_i41_1_, final int p_i41_2_, final int p_i41_3_) {
        this(p_i41_1_, p_i41_2_, p_i41_3_, null);
    }
    
    public GlVersion(final int p_i42_1_, final int p_i42_2_, final int p_i42_3_, final String p_i42_4_) {
        this.major = p_i42_1_;
        this.minor = p_i42_2_;
        this.release = p_i42_3_;
        this.suffix = p_i42_4_;
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    public int getRelease() {
        return this.release;
    }
    
    public int toInt() {
        if (this.minor > 9) {
            return this.major * 100 + this.minor;
        }
        return (this.release > 9) ? (this.major * 100 + this.minor * 10 + 9) : (this.major * 100 + this.minor * 10 + this.release);
    }
    
    @Override
    public String toString() {
        return (this.suffix == null) ? (this.major + "." + this.minor + "." + this.release) : (this.major + "." + this.minor + "." + this.release + this.suffix);
    }
}
