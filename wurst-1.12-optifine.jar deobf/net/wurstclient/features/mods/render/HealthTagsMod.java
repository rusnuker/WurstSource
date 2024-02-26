// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "health tags" })
@Bypasses
public final class HealthTagsMod extends Hack
{
    public HealthTagsMod() {
        super("HealthTags", "Shows the health of players in their nametags.");
        this.setCategory(Category.RENDER);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { HealthTagsMod.WURST.hax.nameTagsMod };
    }
}
