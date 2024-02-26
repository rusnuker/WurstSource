// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto leave", "AutoDisconnect", "auto disconnect", "AutoQuit", "auto quit" })
@HelpPage("Mods/AutoLeave")
public final class AutoLeaveMod extends Hack implements UpdateListener
{
    private final SliderSetting health;
    public final EnumSetting<Mode> mode;
    
    public AutoLeaveMod() {
        super("AutoLeave", "Automatically leaves the server when your health is low.\nThe Chars, TP and SelfHurt modes can bypass CombatLog and similar plugins.");
        this.health = new SliderSetting("Health", "Leaves the server when your health\nreaches this value or falls below it.", 4.0, 0.5, 9.5, 0.5, v -> String.valueOf(SliderSetting.ValueDisplay.DECIMAL.getValueString(v)) + " hearts");
        this.mode = new EnumSetting<Mode>("Mode", "§lQuit§r mode just quits the game normally.\nBypasses NoCheat+ but not CombatLog.\n\n§lChars§r mode sends a special chat message that\ncauses the server to kick you.\nBypasses NoCheat+ and some versions of CombatLog.\n\n§lTP§r mode teleports you to an invalid location,\ncausing the server to kick you.\nBypasses CombatLog, but not NoCheat+.\n\n§lSelfHurt§r mode sends the packet for attacking\nanother player, but with yourself as both the attacker\nand the target. This causes the server to kick you.\nBypasses both CombatLog and NoCheat+.", Mode.values(), Mode.QUIT);
        this.setCategory(Category.COMBAT);
        this.addSetting(this.health);
        this.addSetting(this.mode);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { AutoLeaveMod.WURST.commands.leaveCmd };
    }
    
    @Override
    public String getRenderName() {
        return String.valueOf(this.getName()) + " [" + this.mode.getSelected() + "]";
    }
    
    @Override
    public void onEnable() {
        AutoLeaveMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoLeaveMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (AutoLeaveMod.MC.player.abilities.creativeMode) {
            return;
        }
        if (AutoLeaveMod.MC.isSingleplayer() || WMinecraft.getConnection().getPlayerInfoMap().size() == 1) {
            return;
        }
        if (AutoLeaveMod.MC.player.getHealth() > this.health.getValueF() * 2.0f) {
            return;
        }
        switch (this.mode.getSelected()) {
            case QUIT: {
                WMinecraft.getWorld().sendQuittingDisconnectingPacket();
                break;
            }
            case CHARS: {
                WConnection.sendPacket(new CPacketChatMessage("§"));
                break;
            }
            case TELEPORT: {
                WConnection.sendPacket(new CPacketPlayer.Position(3.1E7, 100.0, 3.1E7, false));
                break;
            }
            case SELFHURT: {
                WPlayer.sendAttackPacket(AutoLeaveMod.MC.player);
                break;
            }
        }
        this.setEnabled(false);
    }
    
    public enum Mode
    {
        QUIT("QUIT", 0, "Quit"), 
        CHARS("CHARS", 1, "Chars"), 
        TELEPORT("TELEPORT", 2, "TP"), 
        SELFHURT("SELFHURT", 3, "SelfHurt");
        
        private final String name;
        
        private Mode(final String name2, final int ordinal, final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
