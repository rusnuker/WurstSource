// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "name tags" })
@Bypasses
public final class NameTagsMod extends Hack
{
    public NameTagsMod() {
        super("NameTags", "Changes the scale of the nametags so you can always read them.\nAlso allows you to see the nametags of sneaking players.");
        this.setCategory(Category.RENDER);
    }
}
