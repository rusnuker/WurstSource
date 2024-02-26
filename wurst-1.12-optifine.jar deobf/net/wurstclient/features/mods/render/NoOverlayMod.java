// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "AntiPumkin", "no overlay" })
@Bypasses
public final class NoOverlayMod extends Hack
{
    public NoOverlayMod() {
        super("NoOverlay", "Blocks the overlays of pumpkins, water, fire, and lava.");
        this.setCategory(Category.RENDER);
    }
}
