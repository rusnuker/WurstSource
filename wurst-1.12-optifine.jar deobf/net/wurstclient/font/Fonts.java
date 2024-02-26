// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.font;

import java.awt.Font;

public class Fonts
{
    public static WurstFontRenderer segoe22;
    public static WurstFontRenderer segoe18;
    public static WurstFontRenderer segoe15;
    
    public static void loadFonts() {
        Fonts.segoe22 = new WurstFontRenderer(new Font("Segoe UI", 0, 44), true, 8);
        Fonts.segoe18 = new WurstFontRenderer(new Font("Segoe UI", 0, 36), true, 8);
        Fonts.segoe15 = new WurstFontRenderer(new Font("Segoe UI", 0, 30), true, 8);
    }
}
