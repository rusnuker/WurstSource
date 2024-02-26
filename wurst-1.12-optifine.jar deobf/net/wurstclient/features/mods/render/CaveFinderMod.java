// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.SetOpaqueCubeListener;
import net.wurstclient.features.Hack;

@SearchTags({ "WallHack", "cave finder", "wall hack" })
@Bypasses
public final class CaveFinderMod extends Hack implements SetOpaqueCubeListener
{
    public CaveFinderMod() {
        super("CaveFinder", "Allows you to see caves through walls.\nNot compatible with shaders.");
        this.setCategory(Category.RENDER);
    }
    
    @Override
    public void onEnable() {
        CaveFinderMod.EVENTS.add(SetOpaqueCubeListener.class, this);
    }
    
    @Override
    public void onDisable() {
        CaveFinderMod.EVENTS.remove(SetOpaqueCubeListener.class, this);
    }
    
    @Override
    public void onSetOpaqueCube(final SetOpaqueCubeEvent event) {
        event.cancel();
    }
}
