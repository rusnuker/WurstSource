// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "NoCactus", "anti cactus", "no cactus" })
@Bypasses(ghostMode = false, latestNCP = false)
public final class AntiCactusMod extends Hack
{
    public AntiCactusMod() {
        super("AntiCactus", "Protects you from cactus damage.");
        this.setCategory(Category.BLOCKS);
    }
}
