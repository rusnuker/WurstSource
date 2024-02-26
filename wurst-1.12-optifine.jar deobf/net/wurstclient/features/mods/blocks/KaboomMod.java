// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.util.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false, antiCheat = false, mineplex = false)
public final class KaboomMod extends Hack implements UpdateListener
{
    private final SliderSetting power;
    
    public KaboomMod() {
        super("Kaboom", "Breaks blocks around you like an explosion.\nThis can be a lot faster than Nuker if the server doesn't have NoCheat+.\nIt works best with fast tools and weak blocks.\nNote that this is not an actual explosion.");
        this.power = new SliderSetting("Power", 128.0, 32.0, 512.0, 32.0, SliderSetting.ValueDisplay.INTEGER);
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.power);
    }
    
    @Override
    public void onEnable() {
        KaboomMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        KaboomMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (!KaboomMod.MC.player.abilities.creativeMode && !KaboomMod.MC.player.onGround) {
            return;
        }
        new Explosion(WMinecraft.getWorld(), KaboomMod.MC.player, KaboomMod.MC.player.posX, KaboomMod.MC.player.posY, KaboomMod.MC.player.posZ, 6.0f, false, true).doExplosionB(true);
        final Iterable<BlockPos> validBlocks = BlockUtils.getValidBlocksByDistanceReversed(6.0, true, p -> true);
        for (final BlockPos pos : validBlocks) {
            for (int i = 0; i < this.power.getValueI(); ++i) {
                BlockUtils.breakBlockPacketSpam(pos);
            }
        }
        this.setEnabled(false);
    }
}
