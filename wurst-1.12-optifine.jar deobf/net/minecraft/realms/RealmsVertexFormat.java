// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.realms;

import java.util.Iterator;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RealmsVertexFormat
{
    private VertexFormat v;
    
    public RealmsVertexFormat(final VertexFormat vIn) {
        this.v = vIn;
    }
    
    public RealmsVertexFormat from(final VertexFormat p_from_1_) {
        this.v = p_from_1_;
        return this;
    }
    
    public VertexFormat getVertexFormat() {
        return this.v;
    }
    
    public void clear() {
        this.v.clear();
    }
    
    public int getUvOffset(final int p_getUvOffset_1_) {
        return this.v.getUvOffsetById(p_getUvOffset_1_);
    }
    
    public int getElementCount() {
        return this.v.getElementCount();
    }
    
    public boolean hasColor() {
        return this.v.hasColor();
    }
    
    public boolean hasUv(final int p_hasUv_1_) {
        return this.v.hasUvOffset(p_hasUv_1_);
    }
    
    public RealmsVertexFormatElement getElement(final int p_getElement_1_) {
        return new RealmsVertexFormatElement(this.v.getElement(p_getElement_1_));
    }
    
    public RealmsVertexFormat addElement(final RealmsVertexFormatElement p_addElement_1_) {
        return this.from(this.v.addElement(p_addElement_1_.getVertexFormatElement()));
    }
    
    public int getColorOffset() {
        return this.v.getColorOffset();
    }
    
    public List<RealmsVertexFormatElement> getElements() {
        final List<RealmsVertexFormatElement> list = Lists.newArrayList();
        for (final VertexFormatElement vertexformatelement : this.v.getElements()) {
            list.add(new RealmsVertexFormatElement(vertexformatelement));
        }
        return list;
    }
    
    public boolean hasNormal() {
        return this.v.hasNormal();
    }
    
    public int getVertexSize() {
        return this.v.getNextOffset();
    }
    
    public int getOffset(final int p_getOffset_1_) {
        return this.v.getOffset(p_getOffset_1_);
    }
    
    public int getNormalOffset() {
        return this.v.getNormalOffset();
    }
    
    public int getIntegerSize() {
        return this.v.getIntegerSize();
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        return this.v.equals(p_equals_1_);
    }
    
    @Override
    public int hashCode() {
        return this.v.hashCode();
    }
    
    @Override
    public String toString() {
        return this.v.toString();
    }
}
