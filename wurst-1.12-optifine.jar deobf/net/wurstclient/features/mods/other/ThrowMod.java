// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false)
public final class ThrowMod extends Hack implements UpdateListener
{
    public ThrowMod() {
        super("Throw", "Uses an item multiple times.\nThis can cause a lot of lag and even crash a server.\nWorks best with snowballs or eggs.\nUse the .throw command to change the amount of uses per click.");
        this.setCategory(Category.OTHER);
    }
    
    @Override
    public String getRenderName() {
        return String.valueOf(this.getName()) + " [" + ThrowMod.WURST.options.throwAmount + "]";
    }
    
    @Override
    public void onEnable() {
        ThrowMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        ThrowMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if ((ThrowMod.MC.rightClickDelayTimer == 4 || ThrowMod.WURST.hax.fastPlaceMod.isActive()) && ThrowMod.MC.gameSettings.keyBindUseItem.pressed) {
            if (ThrowMod.MC.objectMouseOver == null || ThrowMod.MC.player.inventory.getCurrentItem() == null) {
                return;
            }
            for (int i = 0; i < ThrowMod.WURST.options.throwAmount - 1; ++i) {
                ThrowMod.MC.rightClickMouse();
            }
        }
    }
}
