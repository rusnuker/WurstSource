// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "fast place" })
@Bypasses
public final class FastPlaceMod extends Hack implements UpdateListener
{
    public FastPlaceMod() {
        super("FastPlace", "Allows you to place blocks 5 times faster.\nTip: This can speed up mods like AutoBuild.");
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { FastPlaceMod.WURST.hax.fastBreakMod, FastPlaceMod.WURST.hax.autoBuildMod, FastPlaceMod.WURST.hax.buildRandomMod };
    }
    
    @Override
    public void onEnable() {
        FastPlaceMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        FastPlaceMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        FastPlaceMod.MC.rightClickDelayTimer = 0;
    }
}
