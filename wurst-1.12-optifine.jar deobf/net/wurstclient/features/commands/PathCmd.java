// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.ai.PathPos;
import java.util.ArrayList;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.settings.Setting;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.HelpPage;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Command;

@HelpPage("Commands/path")
public final class PathCmd extends Command implements UpdateListener, RenderListener
{
    private final CheckboxSetting debugMode;
    private final CheckboxSetting depthTest;
    private PathFinder pathFinder;
    private boolean enabled;
    private long startTime;
    private BlockPos lastGoal;
    
    public PathCmd() {
        super("path", "Shows the shortest path to a specific point. Useful for labyrinths and caves.", new String[] { "<x> <y> <z>", "<entity>", "-debug", "-depth", "-refresh" });
        this.debugMode = new CheckboxSetting("Debug mode", false);
        this.depthTest = new CheckboxSetting("Depth test", false);
        this.addSetting(this.debugMode);
        this.addSetting(this.depthTest);
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        boolean refresh = false;
        if (args.length > 0 && args[0].startsWith("-")) {
            final String s;
            switch (s = args[0]) {
                case "-refresh": {
                    if (this.lastGoal == null) {
                        throw new CmdError("Cannot refresh: no previous path.");
                    }
                    refresh = true;
                    break;
                }
                case "-debug": {
                    this.debugMode.toggle();
                    ChatUtils.message("Debug mode " + (this.debugMode.isChecked() ? "on" : "off") + ".");
                    return;
                }
                case "-depth": {
                    this.depthTest.toggle();
                    ChatUtils.message("Depth test " + (this.depthTest.isChecked() ? "on" : "off") + ".");
                    return;
                }
                default:
                    break;
            }
        }
        if (this.enabled) {
            PathCmd.EVENTS.remove(UpdateListener.class, this);
            PathCmd.EVENTS.remove(RenderListener.class, this);
            this.enabled = false;
            if (args.length == 0) {
                return;
            }
        }
        BlockPos goal;
        if (refresh) {
            goal = this.lastGoal;
        }
        else {
            goal = this.argsToPos(args);
            this.lastGoal = goal;
        }
        this.pathFinder = new PathFinder(goal);
        this.enabled = true;
        PathCmd.EVENTS.add(UpdateListener.class, this);
        PathCmd.EVENTS.add(RenderListener.class, this);
        System.out.println("Finding path...");
        this.startTime = System.nanoTime();
    }
    
    @Override
    public void onUpdate() {
        final double passedTime = (System.nanoTime() - this.startTime) / 1000000.0;
        this.pathFinder.think();
        final boolean foundPath = this.pathFinder.isDone();
        if (foundPath || this.pathFinder.isFailed()) {
            ArrayList<PathPos> path = new ArrayList<PathPos>();
            if (foundPath) {
                path = this.pathFinder.formatPath();
            }
            else {
                ChatUtils.error("Could not find a path.");
            }
            PathCmd.EVENTS.remove(UpdateListener.class, this);
            System.out.println("Done after " + passedTime + "ms");
            if (this.debugMode.isChecked()) {
                System.out.println("Length: " + path.size() + ", processed: " + this.pathFinder.countProcessedBlocks() + ", queue: " + this.pathFinder.getQueueSize() + ", cost: " + this.pathFinder.getCost(this.pathFinder.getCurrentPos()));
            }
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        this.pathFinder.renderPath(this.debugMode.isChecked(), this.depthTest.isChecked());
    }
    
    public BlockPos getLastGoal() {
        return this.lastGoal;
    }
    
    public boolean isDebugMode() {
        return this.debugMode.isChecked();
    }
    
    public boolean isDepthTest() {
        return this.depthTest.isChecked();
    }
}
