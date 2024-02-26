// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Bypasses {
    boolean mineplex() default true;
    
    boolean antiCheat() default true;
    
    boolean olderNCP() default true;
    
    boolean latestNCP() default true;
    
    boolean ghostMode() default true;
}
