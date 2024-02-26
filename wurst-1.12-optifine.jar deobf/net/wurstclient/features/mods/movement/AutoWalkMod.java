// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.minecraft.client.settings.GameSettings;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto walk" })
@Bypasses
public final class AutoWalkMod extends Hack implements UpdateListener
{
    public AutoWalkMod() {
        super("AutoWalk", "Makes you walk automatically.");
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { AutoWalkMod.WURST.hax.autoSprintMod, AutoWalkMod.WURST.commands.goToCmd };
    }
    
    @Override
    public void onEnable() {
        AutoWalkMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoWalkMod.EVENTS.remove(UpdateListener.class, this);
        AutoWalkMod.MC.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(AutoWalkMod.MC.gameSettings.keyBindForward);
    }
    
    @Override
    public void onUpdate() {
        AutoWalkMod.MC.gameSettings.keyBindForward.pressed = true;
    }
}
