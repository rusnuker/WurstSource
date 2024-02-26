// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.fun;

import java.util.Set;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.wurstclient.Category;
import java.util.Random;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "SkinBlinker", "SpookySkin", "skin blinker", "spooky skin" })
@Bypasses(ghostMode = false)
public final class SkinDerpMod extends Hack implements UpdateListener
{
    private final Random random;
    
    public SkinDerpMod() {
        super("SkinDerp", "Randomly toggles parts of your skin.");
        this.random = new Random();
        this.setCategory(Category.FUN);
    }
    
    @Override
    public void onEnable() {
        SkinDerpMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        SkinDerpMod.EVENTS.remove(UpdateListener.class, this);
        EnumPlayerModelParts[] values;
        for (int length = (values = EnumPlayerModelParts.values()).length, i = 0; i < length; ++i) {
            final EnumPlayerModelParts part = values[i];
            SkinDerpMod.MC.gameSettings.setModelPartEnabled(part, true);
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.random.nextInt(4) != 0) {
            return;
        }
        final Set activeParts = SkinDerpMod.MC.gameSettings.getModelParts();
        EnumPlayerModelParts[] values;
        for (int length = (values = EnumPlayerModelParts.values()).length, i = 0; i < length; ++i) {
            final EnumPlayerModelParts part = values[i];
            SkinDerpMod.MC.gameSettings.setModelPartEnabled(part, !activeParts.contains(part));
        }
    }
}
