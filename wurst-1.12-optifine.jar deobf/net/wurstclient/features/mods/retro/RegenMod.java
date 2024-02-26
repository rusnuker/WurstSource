// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.retro;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.RetroMod;

@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class RegenMod extends RetroMod implements UpdateListener
{
    private final SliderSetting speed;
    private final CheckboxSetting pauseInMidAir;
    
    public RegenMod() {
        super("Regen", "Regenerates your health much faster.\nCan sometimes get you kicked for \"Flying is not enabled\".");
        this.speed = new SliderSetting("Speed", 100.0, 10.0, 1000.0, 10.0, SliderSetting.ValueDisplay.INTEGER);
        this.pauseInMidAir = new CheckboxSetting("Pause in mid-air", true);
        this.setCategory(Category.RETRO);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.speed);
        this.addSetting(this.pauseInMidAir);
    }
    
    @Override
    public void onEnable() {
        RegenMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        RegenMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (RegenMod.MC.player.abilities.creativeMode || RegenMod.MC.player.getHealth() == 0.0f) {
            return;
        }
        if (this.pauseInMidAir.isChecked() && !RegenMod.MC.player.onGround) {
            return;
        }
        if (RegenMod.MC.player.getFoodStats().getFoodLevel() < 18) {
            return;
        }
        if (RegenMod.MC.player.getHealth() >= RegenMod.MC.player.getMaxHealth()) {
            return;
        }
        for (int i = 0; i < this.speed.getValueI(); ++i) {
            WConnection.sendPacket(new CPacketPlayer());
        }
    }
}
