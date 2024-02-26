// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "InventoryWalk", "MenuWalk", "inv walk", "inventory walk", "menu walk" })
@Bypasses
public final class InvWalkMod extends Hack
{
    public InvWalkMod() {
        super("InvWalk", "Allows you to walk while the inventory is open.");
        this.setCategory(Category.MOVEMENT);
    }
}
