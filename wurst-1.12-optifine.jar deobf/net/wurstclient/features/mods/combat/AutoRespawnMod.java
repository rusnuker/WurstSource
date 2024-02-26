// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.DeathListener;
import net.wurstclient.features.Hack;

@SearchTags({ "auto respawn" })
@Bypasses
public final class AutoRespawnMod extends Hack implements DeathListener
{
    public AutoRespawnMod() {
        super("AutoRespawn", "Automatically respawns you whenever you die.");
        this.setCategory(Category.COMBAT);
    }
    
    @Override
    public void onEnable() {
        AutoRespawnMod.EVENTS.add(DeathListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoRespawnMod.EVENTS.remove(DeathListener.class, this);
    }
    
    @Override
    public void onDeath() {
        AutoRespawnMod.MC.player.respawnPlayer();
        AutoRespawnMod.MC.openScreen(null);
    }
}
