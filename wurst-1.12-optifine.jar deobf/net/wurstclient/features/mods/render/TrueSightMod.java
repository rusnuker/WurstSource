// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "true sight" })
@Bypasses
public final class TrueSightMod extends Hack
{
    public TrueSightMod() {
        super("TrueSight", "Allows you to see invisible entities.");
        this.setCategory(Category.RENDER);
    }
}
