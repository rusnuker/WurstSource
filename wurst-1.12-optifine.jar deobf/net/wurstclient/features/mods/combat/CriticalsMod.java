// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.features.Hack;

@SearchTags({ "Crits" })
@Bypasses(ghostMode = false)
public final class CriticalsMod extends Hack implements LeftClickListener
{
    private final ModeSetting mode;
    
    public CriticalsMod() {
        super("Criticals", "Changes all your hits to critical hits.");
        this.mode = new ModeSetting("Mode", new String[] { "Jump", "Packet" }, 1);
        this.setCategory(Category.COMBAT);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.mode);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { CriticalsMod.WURST.hax.killauraMod, CriticalsMod.WURST.hax.triggerBotMod };
    }
    
    @Override
    public void onEnable() {
        CriticalsMod.EVENTS.add(LeftClickListener.class, this);
    }
    
    @Override
    public void onDisable() {
        CriticalsMod.EVENTS.remove(LeftClickListener.class, this);
    }
    
    @Override
    public void onLeftClick(final LeftClickEvent event) {
        if (CriticalsMod.MC.objectMouseOver == null || !(CriticalsMod.MC.objectMouseOver.entityHit instanceof EntityLivingBase)) {
            return;
        }
        this.doCritical();
    }
    
    public void doCritical() {
        if (!this.isActive()) {
            return;
        }
        if (!CriticalsMod.MC.player.onGround) {
            return;
        }
        if (CriticalsMod.MC.player.isInWater() || CriticalsMod.MC.player.isInLava()) {
            return;
        }
        switch (this.mode.getSelected()) {
            case 0: {
                CriticalsMod.MC.player.motionY = 0.10000000149011612;
                CriticalsMod.MC.player.fallDistance = 0.1f;
                CriticalsMod.MC.player.onGround = false;
                break;
            }
            case 1: {
                final double posX = CriticalsMod.MC.player.posX;
                final double posY = CriticalsMod.MC.player.posY;
                final double posZ = CriticalsMod.MC.player.posZ;
                WConnection.sendPacket(new CPacketPlayer.Position(posX, posY + 0.0625, posZ, true));
                WConnection.sendPacket(new CPacketPlayer.Position(posX, posY, posZ, false));
                WConnection.sendPacket(new CPacketPlayer.Position(posX, posY + 1.1E-5, posZ, false));
                WConnection.sendPacket(new CPacketPlayer.Position(posX, posY, posZ, false));
                break;
            }
        }
    }
}
