// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.wurstclient.ai.PathPos;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.features.commands.PathCmd;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.util.RotationUtils;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.settings.Setting;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import net.wurstclient.util.EntityUtils;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.ai.PathProcessor;
import net.minecraft.entity.Entity;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses
@DontSaveState
public final class FollowMod extends Hack implements UpdateListener, RenderListener
{
    private Entity entity;
    private EntityPathFinder pathFinder;
    private PathProcessor processor;
    private int ticksProcessing;
    private float distanceSq;
    private final SliderSetting distance;
    private final CheckboxSetting useAi;
    private final EntityUtils.TargetSettings targetSettingsFind;
    private final EntityUtils.TargetSettings targetSettingsKeep;
    
    public FollowMod() {
        super("Follow", "A bot that follows the closest entity.\nVery annoying.");
        this.distance = new SliderSetting("Distance", 1.0, 1.0, 12.0, 0.5, SliderSetting.ValueDisplay.DECIMAL) {
            @Override
            public void update() {
                FollowMod.access$0(FollowMod.this, (float)Math.pow(this.getValue(), 2.0));
            }
        };
        this.useAi = new CheckboxSetting("Use AI (experimental)", false);
        this.targetSettingsFind = new EntityUtils.TargetSettings() {
            @Override
            public boolean targetFriends() {
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
            public boolean targetPlayers() {
                return true;
            }
            
            @Override
            public boolean targetAnimals() {
                return true;
            }
            
            @Override
            public boolean targetMonsters() {
                return true;
            }
            
            @Override
            public boolean targetGolems() {
                return true;
            }
            
            @Override
            public boolean targetSleepingPlayers() {
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
            
            @Override
            public boolean targetTeams() {
                return false;
            }
        };
        this.setCategory(Category.MOVEMENT);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { FollowMod.WURST.commands.followCmd };
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.distance);
        this.addSetting(this.useAi);
    }
    
    @Override
    public String getRenderName() {
        if (this.entity != null) {
            return "Following " + this.entity.getName();
        }
        return "Follow";
    }
    
    @Override
    public void onEnable() {
        if (this.entity == null) {
            this.entity = EntityUtils.getClosestEntity(this.targetSettingsFind);
            if (this.entity == null) {
                ChatUtils.error("Could not find a valid entity.");
                this.setEnabled(false);
                return;
            }
        }
        this.pathFinder = new EntityPathFinder();
        FollowMod.EVENTS.add(UpdateListener.class, this);
        FollowMod.EVENTS.add(RenderListener.class, this);
        ChatUtils.message("Now following " + this.entity.getName());
    }
    
    @Override
    public void onDisable() {
        FollowMod.EVENTS.remove(UpdateListener.class, this);
        FollowMod.EVENTS.remove(RenderListener.class, this);
        this.pathFinder = null;
        this.processor = null;
        this.ticksProcessing = 0;
        PathProcessor.releaseControls();
        if (this.entity != null) {
            ChatUtils.message("No longer following " + this.entity.getName());
        }
        this.entity = null;
    }
    
    @Override
    public void onUpdate() {
        if (FollowMod.MC.player.getHealth() <= 0.0f) {
            if (this.entity == null) {
                ChatUtils.message("No longer following entity");
            }
            this.setEnabled(false);
            return;
        }
        if (!EntityUtils.isCorrectEntity(this.entity, this.targetSettingsKeep)) {
            this.entity = EntityUtils.getClosestEntityWithName(this.entity.getName(), this.targetSettingsKeep);
            if (this.entity == null) {
                ChatUtils.message("No longer following entity");
                this.setEnabled(false);
                return;
            }
            this.pathFinder = new EntityPathFinder();
            this.processor = null;
            this.ticksProcessing = 0;
        }
        if (this.useAi.isChecked()) {
            if ((this.processor == null || this.processor.isDone() || this.ticksProcessing >= 10 || !this.pathFinder.isPathStillValid(this.processor.getIndex())) && (this.pathFinder.isDone() || this.pathFinder.isFailed())) {
                this.pathFinder = new EntityPathFinder();
                this.processor = null;
                this.ticksProcessing = 0;
            }
            if (!this.pathFinder.isDone() && !this.pathFinder.isFailed()) {
                PathProcessor.lockControls();
                RotationUtils.faceEntityClient(this.entity);
                this.pathFinder.think();
                this.pathFinder.formatPath();
                this.processor = this.pathFinder.getProcessor();
            }
            if (!this.processor.isDone()) {
                this.processor.process();
                ++this.ticksProcessing;
            }
        }
        else {
            if (FollowMod.MC.player.isCollidedHorizontally && FollowMod.MC.player.onGround) {
                FollowMod.MC.player.jump();
            }
            if (FollowMod.MC.player.isInWater() && FollowMod.MC.player.posY < this.entity.posY) {
                final EntityPlayerSP player = FollowMod.MC.player;
                player.motionY += 0.04;
            }
            if (!FollowMod.MC.player.onGround && (FollowMod.MC.player.abilities.isFlying || FollowMod.WURST.hax.flightMod.isActive()) && FollowMod.MC.player.getDistanceSq(this.entity.posX, FollowMod.MC.player.posY, this.entity.posZ) <= WMinecraft.getPlayer().getDistanceSq(FollowMod.MC.player.posX, this.entity.posY, FollowMod.MC.player.posZ)) {
                if (FollowMod.MC.player.posY > this.entity.posY + 1.0) {
                    FollowMod.MC.gameSettings.keyBindSneak.pressed = true;
                }
                else if (FollowMod.MC.player.posY < this.entity.posY - 1.0) {
                    FollowMod.MC.gameSettings.keyBindJump.pressed = true;
                }
            }
            else {
                FollowMod.MC.gameSettings.keyBindSneak.pressed = false;
                FollowMod.MC.gameSettings.keyBindJump.pressed = false;
            }
            RotationUtils.faceEntityClient(this.entity);
            FollowMod.MC.gameSettings.keyBindForward.pressed = (FollowMod.MC.player.getDistanceSq(this.entity.posX, FollowMod.MC.player.posY, this.entity.posZ) > this.distanceSq);
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        final PathCmd pathCmd = FollowMod.WURST.commands.pathCmd;
        this.pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
    }
    
    public void setEntity(final Entity entity) {
        this.entity = entity;
    }
    
    static /* synthetic */ void access$0(final FollowMod followMod, final float distanceSq) {
        followMod.distanceSq = distanceSq;
    }
    
    private class EntityPathFinder extends PathFinder
    {
        public EntityPathFinder() {
            super(new BlockPos(FollowMod.this.entity));
            this.setThinkTime(1);
        }
        
        @Override
        protected boolean checkDone() {
            return this.done = (FollowMod.this.entity.getDistanceSqToCenter(this.current) <= FollowMod.this.distanceSq);
        }
        
        @Override
        public ArrayList<PathPos> formatPath() {
            if (!this.done) {
                this.failed = true;
            }
            return super.formatPath();
        }
    }
}
