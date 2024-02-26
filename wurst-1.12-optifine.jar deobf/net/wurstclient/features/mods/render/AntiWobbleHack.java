// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.Category;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "anti wobble", "NoWobble", "no wobble", "AntiNausea", "anti nausea", "NoNausea", "no nausea" })
public final class AntiWobbleHack extends Hack
{
    public AntiWobbleHack() {
        super("AntiWobble", "Disables the wobble effect caused\nby nausea and portals.");
        this.setCategory(Category.RENDER);
    }
}
