// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

public class RangeListInt
{
    private RangeInt[] ranges;
    
    public RangeListInt() {
        this.ranges = new RangeInt[0];
    }
    
    public void addRange(final RangeInt p_addRange_1_) {
        this.ranges = (RangeInt[])Config.addObjectToArray(this.ranges, p_addRange_1_);
    }
    
    public boolean isInRange(final int p_isInRange_1_) {
        for (int i = 0; i < this.ranges.length; ++i) {
            final RangeInt rangeint = this.ranges[i];
            if (rangeint.isInRange(p_isInRange_1_)) {
                return true;
            }
        }
        return false;
    }
    
    public int getCountRanges() {
        return this.ranges.length;
    }
    
    public RangeInt getRange(final int p_getRange_1_) {
        return this.ranges[p_getRange_1_];
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("[");
        for (int i = 0; i < this.ranges.length; ++i) {
            final RangeInt rangeint = this.ranges[i];
            if (i > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append(rangeint.toString());
        }
        stringbuffer.append("]");
        return stringbuffer.toString();
    }
}
