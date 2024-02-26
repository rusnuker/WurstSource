// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.features.special_features.YesCheatSpf;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.PostUpdateListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "AutoSneaking" })
@Bypasses
public final class SneakMod extends Hack implements UpdateListener, PostUpdateListener
{
    private final ModeSetting mode;
    
    public SneakMod() {
        super("Sneak", "Makes you sneak automatically.");
        this.mode = new ModeSetting("Mode", "§lPacket§r mode makes it look like you're sneaking without slowing you down.\n§lLegit§r mode actually makes you sneak.", new String[] { "Packet", "Legit" }, 0);
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.mode);
    }
    
    @Override
    public String getRenderName() {
        return String.valueOf(this.getName()) + " [" + this.mode.getSelectedMode() + "]";
    }
    
    @Override
    public void onEnable() {
        SneakMod.EVENTS.add(UpdateListener.class, this);
        SneakMod.EVENTS.add(PostUpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        SneakMod.EVENTS.remove(UpdateListener.class, this);
        SneakMod.EVENTS.remove(PostUpdateListener.class, this);
        switch (this.mode.getSelected()) {
            case 0: {
                WConnection.sendPacket(new CPacketEntityAction(SneakMod.MC.player, CPacketEntityAction.Action.STOP_SNEAKING));
                break;
            }
            case 1: {
                SneakMod.MC.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(SneakMod.MC.gameSettings.keyBindSneak);
                break;
            }
        }
    }
    
    @Override
    public void onUpdate() {
        switch (this.mode.getSelected()) {
            case 0: {
                WConnection.sendPacket(new CPacketEntityAction(SneakMod.MC.player, CPacketEntityAction.Action.START_SNEAKING));
                WConnection.sendPacket(new CPacketEntityAction(SneakMod.MC.player, CPacketEntityAction.Action.STOP_SNEAKING));
                break;
            }
            case 1: {
                SneakMod.MC.gameSettings.keyBindSneak.pressed = true;
                break;
            }
        }
    }
    
    @Override
    public void afterUpdate() {
        if (this.mode.getSelected() == 1) {
            return;
        }
        WConnection.sendPacket(new CPacketEntityAction(SneakMod.MC.player, CPacketEntityAction.Action.STOP_SNEAKING));
        WConnection.sendPacket(new CPacketEntityAction(SneakMod.MC.player, CPacketEntityAction.Action.START_SNEAKING));
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            case GHOST_MODE: {
                this.mode.lock(1);
                break;
            }
            default: {
                this.mode.unlock();
                break;
            }
        }
    }
}
