// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class LiquidsMod extends Hack
{
    public LiquidsMod() {
        super("Liquids", "Allows you to place blocks against liquids.");
        this.setCategory(Category.BLOCKS);
    }
}
