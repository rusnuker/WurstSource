// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.ai;

import net.minecraft.client.settings.GameSettings;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.util.RotationUtils;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;

public abstract class PathProcessor
{
    protected static final WurstClient wurst;
    protected static final Minecraft mc;
    private static final KeyBinding[] CONTROLS;
    protected final ArrayList<PathPos> path;
    protected int index;
    protected boolean done;
    protected int ticksOffPath;
    
    static {
        wurst = WurstClient.INSTANCE;
        mc = Minecraft.getMinecraft();
        CONTROLS = new KeyBinding[] { PathProcessor.mc.gameSettings.keyBindForward, PathProcessor.mc.gameSettings.keyBindBack, PathProcessor.mc.gameSettings.keyBindRight, PathProcessor.mc.gameSettings.keyBindLeft, PathProcessor.mc.gameSettings.keyBindJump, PathProcessor.mc.gameSettings.keyBindSneak };
    }
    
    public PathProcessor(final ArrayList<PathPos> path) {
        if (path.isEmpty()) {
            throw new IllegalStateException("There is no path!");
        }
        this.path = path;
    }
    
    public abstract void process();
    
    public final int getIndex() {
        return this.index;
    }
    
    public final boolean isDone() {
        return this.done;
    }
    
    public final int getTicksOffPath() {
        return this.ticksOffPath;
    }
    
    protected final void facePosition(final BlockPos pos) {
        RotationUtils.faceVectorForWalking(new Vec3d(pos).addVector(0.5, 0.5, 0.5));
    }
    
    public static final void lockControls() {
        KeyBinding[] controls;
        for (int length = (controls = PathProcessor.CONTROLS).length, i = 0; i < length; ++i) {
            final KeyBinding key = controls[i];
            key.pressed = false;
        }
        WMinecraft.getPlayer().setSprinting(false);
    }
    
    public static final void releaseControls() {
        KeyBinding[] controls;
        for (int length = (controls = PathProcessor.CONTROLS).length, i = 0; i < length; ++i) {
            final KeyBinding key = controls[i];
            key.pressed = GameSettings.isKeyDown(key);
        }
    }
}
