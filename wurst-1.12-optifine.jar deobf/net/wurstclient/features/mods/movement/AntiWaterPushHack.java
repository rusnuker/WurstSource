// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.IsPlayerInWaterListener;
import net.wurstclient.events.VelocityFromFluidListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "anti water push", "NoWaterPush", "no water push" })
public final class AntiWaterPushHack extends Hack implements UpdateListener, VelocityFromFluidListener, IsPlayerInWaterListener
{
    private final CheckboxSetting preventSlowdown;
    
    public AntiWaterPushHack() {
        super("AntiWaterPush", "Prevents you from getting pushed by water.");
        this.preventSlowdown = new CheckboxSetting("Prevent slowdown", "Allows you to walk underwater at full speed.\nSome servers consider this a speedhack.", false);
        this.setCategory(Category.MOVEMENT);
        this.addSetting(this.preventSlowdown);
    }
    
    @Override
    public void onEnable() {
        AntiWaterPushHack.EVENTS.add(UpdateListener.class, this);
        AntiWaterPushHack.EVENTS.add(VelocityFromFluidListener.class, this);
        AntiWaterPushHack.EVENTS.add(IsPlayerInWaterListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AntiWaterPushHack.EVENTS.remove(UpdateListener.class, this);
        AntiWaterPushHack.EVENTS.remove(VelocityFromFluidListener.class, this);
        AntiWaterPushHack.EVENTS.remove(IsPlayerInWaterListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (!this.preventSlowdown.isChecked()) {
            return;
        }
        if (!AntiWaterPushHack.MC.gameSettings.keyBindJump.isPressed()) {
            return;
        }
        if (!AntiWaterPushHack.MC.player.onGround) {
            return;
        }
        if (!AntiWaterPushHack.MC.player.isInWaterBypass()) {
            return;
        }
        AntiWaterPushHack.MC.player.jump();
    }
    
    @Override
    public void onVelocityFromFluid(final VelocityFromFluidEvent event) {
        event.cancel();
    }
    
    @Override
    public void onIsPlayerInWater(final IsPlayerInWaterEvent event) {
        if (this.preventSlowdown.isChecked()) {
            event.setInWater(false);
        }
    }
    
    public boolean isPreventingSlowdown() {
        return this.preventSlowdown.isChecked();
    }
}
