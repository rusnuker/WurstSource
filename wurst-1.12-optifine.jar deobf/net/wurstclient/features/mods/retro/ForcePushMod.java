// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.retro;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.Category;
import net.wurstclient.util.EntityUtils;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.RetroMod;

@SearchTags({ "force push", "paralyze" })
@Bypasses
public final class ForcePushMod extends RetroMod implements UpdateListener
{
    private EntityUtils.TargetSettings targetSettings;
    
    public ForcePushMod() {
        super("ForcePush", "Pushes nearby mobs away from you.\nCan sometimes get you kicked for \"Flying is not enabled\".");
        this.targetSettings = new EntityUtils.TargetSettings() {
            @Override
            public boolean targetBehindWalls() {
                return true;
            }
            
            @Override
            public float getRange() {
                return 1.0f;
            }
        };
        this.setCategory(Category.RETRO);
    }
    
    @Override
    public void onEnable() {
        ForcePushMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        ForcePushMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (ForcePushMod.MC.player.onGround && EntityUtils.getClosestEntity(this.targetSettings) != null) {
            for (int i = 0; i < 1000; ++i) {
                WConnection.sendPacket(new CPacketPlayer(true));
            }
        }
    }
}
