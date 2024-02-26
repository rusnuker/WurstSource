// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.util.ChatUtils;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.features.HelpPage;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Command;

@HelpPage("Commands/goto")
public final class GoToCmd extends Command implements UpdateListener, RenderListener
{
    private PathFinder pathFinder;
    private PathProcessor processor;
    private boolean enabled;
    
    public GoToCmd() {
        super("goto", "Walks or flies you to a specific location.", new String[] { "<x> <y> <z>", "<entity>", "-path" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (this.enabled) {
            this.disable();
            if (args.length == 0) {
                return;
            }
        }
        if (args.length == 1 && args[0].equals("-path")) {
            final BlockPos goal = GoToCmd.WURST.commands.pathCmd.getLastGoal();
            if (goal == null) {
                throw new CmdError("No previous position on .path.");
            }
            this.pathFinder = new PathFinder(goal);
        }
        else {
            final BlockPos goal = this.argsToPos(args);
            this.pathFinder = new PathFinder(goal);
        }
        this.enabled = true;
        GoToCmd.EVENTS.add(UpdateListener.class, this);
        GoToCmd.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (!this.pathFinder.isDone()) {
            PathProcessor.lockControls();
            this.pathFinder.think();
            if (!this.pathFinder.isDone()) {
                if (this.pathFinder.isFailed()) {
                    ChatUtils.error("Could not find a path.");
                    this.disable();
                }
                return;
            }
            this.pathFinder.formatPath();
            this.processor = this.pathFinder.getProcessor();
            System.out.println("Done");
        }
        if (this.processor != null && !this.pathFinder.isPathStillValid(this.processor.getIndex())) {
            System.out.println("Updating path...");
            this.pathFinder = new PathFinder(this.pathFinder.getGoal());
            return;
        }
        this.processor.process();
        if (this.processor.isDone()) {
            this.disable();
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        final PathCmd pathCmd = GoToCmd.WURST.commands.pathCmd;
        this.pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
    }
    
    private void disable() {
        GoToCmd.EVENTS.remove(UpdateListener.class, this);
        GoToCmd.EVENTS.remove(RenderListener.class, this);
        this.pathFinder = null;
        this.processor = null;
        PathProcessor.releaseControls();
        this.enabled = false;
    }
    
    public boolean isActive() {
        return this.enabled;
    }
}
