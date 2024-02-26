// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.wurstclient.ai.PathPos;
import java.util.ArrayList;
import net.wurstclient.features.commands.PathCmd;
import net.wurstclient.util.RotationUtils;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.ai.PathFinder;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.ai.PathProcessor;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "AFKBot", "anti afk", "afk bot" })
@Bypasses(ghostMode = false)
@DontSaveState
public final class AntiAfkMod extends Hack implements UpdateListener, RenderListener
{
    private final CheckboxSetting useAi;
    private int timer;
    private Random random;
    private BlockPos start;
    private BlockPos nextBlock;
    private RandomPathFinder pathFinder;
    private PathProcessor processor;
    private boolean creativeFlying;
    
    public AntiAfkMod() {
        super("AntiAFK", "Walks around randomly to hide you from AFK detectors.\nNeeds 3x3 blocks of free space.");
        this.useAi = new CheckboxSetting("Use AI", true);
        this.random = new Random();
        this.setCategory(Category.OTHER);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.useAi);
    }
    
    @Override
    public void onEnable() {
        this.start = new BlockPos(AntiAfkMod.MC.player);
        this.nextBlock = null;
        this.pathFinder = new RandomPathFinder(this.start);
        this.creativeFlying = AntiAfkMod.MC.player.abilities.isFlying;
        AntiAfkMod.EVENTS.add(UpdateListener.class, this);
        AntiAfkMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AntiAfkMod.EVENTS.remove(UpdateListener.class, this);
        AntiAfkMod.EVENTS.remove(RenderListener.class, this);
        AntiAfkMod.MC.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(AntiAfkMod.MC.gameSettings.keyBindForward);
        AntiAfkMod.MC.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(AntiAfkMod.MC.gameSettings.keyBindJump);
        this.pathFinder = null;
        this.processor = null;
        PathProcessor.releaseControls();
    }
    
    @Override
    public void onUpdate() {
        if (AntiAfkMod.MC.player.getHealth() <= 0.0f) {
            this.setEnabled(false);
            return;
        }
        AntiAfkMod.MC.player.abilities.isFlying = this.creativeFlying;
        if (this.useAi.isChecked()) {
            if (this.timer > 0) {
                --this.timer;
                if (!AntiAfkMod.WURST.hax.jesusMod.isActive()) {
                    AntiAfkMod.MC.gameSettings.keyBindJump.pressed = AntiAfkMod.MC.player.isInWater();
                }
                return;
            }
            if (!this.pathFinder.isDone() && !this.pathFinder.isFailed()) {
                PathProcessor.lockControls();
                this.pathFinder.think();
                if (!this.pathFinder.isDone() && !this.pathFinder.isFailed()) {
                    return;
                }
                this.pathFinder.formatPath();
                this.processor = this.pathFinder.getProcessor();
            }
            if (this.processor != null && !this.pathFinder.isPathStillValid(this.processor.getIndex())) {
                this.pathFinder = new RandomPathFinder(this.pathFinder);
                return;
            }
            if (!this.processor.isDone()) {
                this.processor.process();
            }
            else {
                this.pathFinder = new RandomPathFinder(this.start);
            }
            if (this.processor.isDone()) {
                PathProcessor.releaseControls();
                this.timer = 40 + this.random.nextInt(21);
            }
        }
        else {
            if (this.timer <= 0 || this.nextBlock == null) {
                this.nextBlock = this.start.add(this.random.nextInt(3) - 1, 0, this.random.nextInt(3) - 1);
                this.timer = 40 + this.random.nextInt(21);
            }
            RotationUtils.faceVectorForWalking(new Vec3d(this.nextBlock).addVector(0.5, 0.5, 0.5));
            if (AntiAfkMod.MC.player.getDistanceSqToCenter(this.nextBlock) > 0.5) {
                AntiAfkMod.MC.gameSettings.keyBindForward.pressed = true;
            }
            else {
                AntiAfkMod.MC.gameSettings.keyBindForward.pressed = false;
            }
            AntiAfkMod.MC.gameSettings.keyBindJump.pressed = AntiAfkMod.MC.player.isInWater();
            if (this.timer > 0) {
                --this.timer;
            }
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (!this.useAi.isChecked()) {
            return;
        }
        final PathCmd pathCmd = AntiAfkMod.WURST.commands.pathCmd;
        this.pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
    }
    
    private class RandomPathFinder extends PathFinder
    {
        public RandomPathFinder(final BlockPos goal) {
            super(goal.add(AntiAfkMod.this.random.nextInt(33) - 16, AntiAfkMod.this.random.nextInt(33) - 16, AntiAfkMod.this.random.nextInt(33) - 16));
            this.setThinkTime(10);
            this.setFallingAllowed(false);
            this.setDivingAllowed(false);
        }
        
        public RandomPathFinder(final PathFinder pathFinder) {
            super(pathFinder);
            this.setFallingAllowed(false);
            this.setDivingAllowed(false);
        }
        
        @Override
        public ArrayList<PathPos> formatPath() {
            this.failed = true;
            return super.formatPath();
        }
    }
}
