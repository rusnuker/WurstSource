// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public class StringUtils
{
    private static final Pattern PATTERN_CONTROL_CODE;
    
    static {
        PATTERN_CONTROL_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    }
    
    public static String ticksToElapsedTime(final int ticks) {
        int i = ticks / 20;
        final int j = i / 60;
        i %= 60;
        return (i < 10) ? (String.valueOf(j) + ":0" + i) : (String.valueOf(j) + ":" + i);
    }
    
    public static String stripControlCodes(final String text) {
        return StringUtils.PATTERN_CONTROL_CODE.matcher(text).replaceAll("");
    }
    
    public static boolean isNullOrEmpty(@Nullable final String string) {
        return org.apache.commons.lang3.StringUtils.isEmpty((CharSequence)string);
    }
}
