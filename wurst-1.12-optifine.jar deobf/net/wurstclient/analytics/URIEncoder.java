// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.analytics;

public class URIEncoder
{
    private static String mark;
    
    static {
        URIEncoder.mark = "-_.!~*'()\"";
    }
    
    public static String encodeURI(final String argString) {
        final StringBuffer uri = new StringBuffer();
        final char[] chars = argString.toCharArray();
        char[] array;
        for (int length = (array = chars).length, i = 0; i < length; ++i) {
            final char c = array[i];
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || URIEncoder.mark.indexOf(c) != -1) {
                uri.append(c);
            }
            else {
                uri.append("%");
                uri.append(Integer.toHexString(c));
            }
        }
        return uri.toString();
    }
}
