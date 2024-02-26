// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import java.util.Iterator;
import net.wurstclient.Category;
import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@DontBlock
@SearchTags({ "legit", "disable" })
public final class PanicMod extends Hack implements UpdateListener
{
    public PanicMod() {
        super("Panic", "Instantly turns off all enabled mods.\nBe careful with this!");
        this.setCategory(Category.OTHER);
    }
    
    @Override
    public void onEnable() {
        PanicMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        PanicMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        for (final Hack mod : PanicMod.WURST.hax.getAll()) {
            if (mod.isEnabled() && mod != this) {
                mod.setEnabled(false);
            }
        }
        this.setEnabled(false);
    }
}
