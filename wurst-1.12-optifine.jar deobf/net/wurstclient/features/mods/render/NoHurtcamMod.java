// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "no hurtcam", "no hurt cam" })
@Bypasses
public final class NoHurtcamMod extends Hack
{
    public NoHurtcamMod() {
        super("NoHurtcam", "Disables the shaking effect when you get hurt.");
        this.setCategory(Category.RENDER);
    }
}
