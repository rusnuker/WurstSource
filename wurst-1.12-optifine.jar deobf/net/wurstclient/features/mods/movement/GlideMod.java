// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import net.minecraft.block.Block;
import java.util.stream.Stream;
import net.minecraft.util.math.Box;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.block.BlockLiquid;
import java.util.function.Function;
import net.wurstclient.compatibility.WBlock;
import java.util.stream.StreamSupport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WWorld;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class GlideMod extends Hack implements UpdateListener
{
    private final SliderSetting fallSpeed;
    private final SliderSetting moveSpeed;
    private final SliderSetting minHeight;
    
    public GlideMod() {
        super("Glide", "Makes you glide down slowly when falling.");
        this.fallSpeed = new SliderSetting("Fall speed", 0.125, 0.005, 0.25, 0.005, SliderSetting.ValueDisplay.DECIMAL);
        this.moveSpeed = new SliderSetting("Move speed", "Horizontal movement factor.", 1.2, 1.0, 5.0, 0.05, SliderSetting.ValueDisplay.PERCENTAGE);
        this.minHeight = new SliderSetting("Min height", "Won't glide when you are\ntoo close to the ground.", 0.0, 0.0, 2.0, 0.01, v -> (v == 0.0) ? "disabled" : SliderSetting.ValueDisplay.DECIMAL.getValueString(v));
        this.setCategory(Category.MOVEMENT);
        this.addSetting(this.fallSpeed);
        this.addSetting(this.moveSpeed);
        this.addSetting(this.minHeight);
    }
    
    @Override
    public void onEnable() {
        GlideMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        GlideMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        final EntityPlayerSP player = GlideMod.MC.player;
        if (!player.isAirBorne || player.isInWater() || player.isInLava() || player.isOnLadder() || player.motionY >= 0.0) {
            return;
        }
        if (this.minHeight.getValue() > 0.0) {
            Box box = player.getEntityBoundingBox();
            box = box.union(box.offset(0.0, -this.minHeight.getValue(), 0.0));
            if (WWorld.collidesWithAnyBlock(box)) {
                return;
            }
            final BlockPos min = new BlockPos(new Vec3d(box.minX, box.minY, box.minZ));
            final BlockPos max = new BlockPos(new Vec3d(box.maxX, box.maxY, box.maxZ));
            final Stream<BlockPos> stream = StreamSupport.stream(BlockPos.getAllInBox(min, max).spliterator(), true);
            if (stream.map((Function<? super BlockPos, ?>)WBlock::getBlock).anyMatch(b -> b instanceof BlockLiquid)) {
                return;
            }
        }
        player.motionY = Math.max(player.motionY, -this.fallSpeed.getValue());
        final EntityPlayerSP entityPlayerSP = player;
        entityPlayerSP.jumpMovementFactor *= this.moveSpeed.getValueF();
    }
}
