// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.Category;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "AntiBlindness", "NoBlindness", "anti blindness", "no blindness" })
public final class AntiBlindMod extends Hack
{
    public AntiBlindMod() {
        super("AntiBlind", "Prevents blindness.");
        this.setCategory(Category.RENDER);
    }
}
