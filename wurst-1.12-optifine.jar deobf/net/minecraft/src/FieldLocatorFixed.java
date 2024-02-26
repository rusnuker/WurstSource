// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.lang.reflect.Field;

public class FieldLocatorFixed implements IFieldLocator
{
    private Field field;
    
    public FieldLocatorFixed(final Field p_i34_1_) {
        this.field = p_i34_1_;
    }
    
    @Override
    public Field getField() {
        return this.field;
    }
}
