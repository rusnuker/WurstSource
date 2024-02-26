// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.minecraft.entity.player.EntityPlayer;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.util.EntityUtils;
import net.wurstclient.util.EntityFakePlayer;
import net.minecraft.entity.Entity;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "remote view" })
@Bypasses
@DontSaveState
public final class RemoteViewMod extends Hack implements UpdateListener
{
    private Entity entity;
    private boolean wasInvisible;
    private EntityFakePlayer fakePlayer;
    private final EntityUtils.TargetSettings targetSettingsFind;
    private final EntityUtils.TargetSettings targetSettingsKeep;
    
    public RemoteViewMod() {
        super("RemoteView", "Allows you to see the world as someone else.\nUse the .rv command to make it target a specific entity.");
        this.entity = null;
        this.targetSettingsFind = new EntityUtils.TargetSettings() {
            @Override
            public boolean targetFriends() {
                return true;
            }
            
            @Override
            public boolean targetBehindWalls() {
                return true;
            }
        };
        this.targetSettingsKeep = new EntityUtils.TargetSettings() {
            @Override
            public boolean targetFriends() {
                return true;
            }
            
            @Override
            public boolean targetBehindWalls() {
                return true;
            }
            
            @Override
            public boolean targetInvisiblePlayers() {
                return true;
            }
            
            @Override
            public boolean targetInvisibleMobs() {
                return true;
            }
        };
        this.setCategory(Category.RENDER);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { RemoteViewMod.WURST.commands.rvCmd };
    }
    
    @Override
    public void onEnable() {
        if (this.entity == null) {
            this.entity = EntityUtils.getClosestEntity(this.targetSettingsFind);
            if (this.entity == null) {
                ChatUtils.message("There is no nearby entity.");
                this.setEnabled(false);
                return;
            }
        }
        this.wasInvisible = this.entity.isInvisibleToPlayer(RemoteViewMod.MC.player);
        RemoteViewMod.MC.player.noClip = true;
        this.fakePlayer = new EntityFakePlayer();
        ChatUtils.message("Now viewing " + this.entity.getName() + ".");
        RemoteViewMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        RemoteViewMod.EVENTS.remove(UpdateListener.class, this);
        if (this.entity != null) {
            ChatUtils.message("No longer viewing " + this.entity.getName() + ".");
            this.entity.setInvisible(this.wasInvisible);
            this.entity = null;
        }
        RemoteViewMod.MC.player.noClip = false;
        if (this.fakePlayer != null) {
            this.fakePlayer.resetPlayerPosition();
            this.fakePlayer.despawn();
        }
    }
    
    public void onToggledByCommand(final String viewName) {
        if (!this.isEnabled() && viewName != null && !viewName.isEmpty()) {
            this.entity = EntityUtils.getClosestEntityWithName(viewName, this.targetSettingsFind);
        }
        this.toggle();
    }
    
    @Override
    public void onUpdate() {
        if (!EntityUtils.isCorrectEntity(this.entity, this.targetSettingsKeep)) {
            this.setEnabled(false);
            return;
        }
        RemoteViewMod.MC.player.copyLocationAndAnglesFrom(this.entity);
        RemoteViewMod.MC.player.motionX = 0.0;
        RemoteViewMod.MC.player.motionY = 0.0;
        RemoteViewMod.MC.player.motionZ = 0.0;
        this.entity.setInvisible(true);
    }
}
