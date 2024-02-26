// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.lang.reflect.Field;

public class FieldLocatorType implements IFieldLocator
{
    private ReflectorClass reflectorClass;
    private Class targetFieldType;
    private int targetFieldIndex;
    
    public FieldLocatorType(final ReflectorClass p_i36_1_, final Class p_i36_2_) {
        this(p_i36_1_, p_i36_2_, 0);
    }
    
    public FieldLocatorType(final ReflectorClass p_i37_1_, final Class p_i37_2_, final int p_i37_3_) {
        this.reflectorClass = null;
        this.targetFieldType = null;
        this.reflectorClass = p_i37_1_;
        this.targetFieldType = p_i37_2_;
        this.targetFieldIndex = p_i37_3_;
    }
    
    @Override
    public Field getField() {
        final Class oclass = this.reflectorClass.getTargetClass();
        if (oclass == null) {
            return null;
        }
        try {
            final Field[] afield = oclass.getDeclaredFields();
            int i = 0;
            for (int j = 0; j < afield.length; ++j) {
                final Field field = afield[j];
                if (field.getType() == this.targetFieldType) {
                    if (i == this.targetFieldIndex) {
                        field.setAccessible(true);
                        return field;
                    }
                    ++i;
                }
            }
            Config.log("(Reflector) Field not present: " + oclass.getName() + ".(type: " + this.targetFieldType + ", index: " + this.targetFieldIndex + ")");
            return null;
        }
        catch (final SecurityException securityexception) {
            securityexception.printStackTrace();
            return null;
        }
        catch (final Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}
