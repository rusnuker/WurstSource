// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "safe walk" })
@Bypasses
public final class SafeWalkMod extends Hack
{
    public SafeWalkMod() {
        super("SafeWalk", "Prevents you from falling off edges.");
        this.setCategory(Category.MOVEMENT);
    }
}
