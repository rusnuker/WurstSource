// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;
import net.minecraft.block.material.Material;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.features.Hack;

@SearchTags({ "FastNuker", "speed nuker", "fast nuker" })
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false, antiCheat = false, mineplex = false)
public final class SpeedNukerMod extends Hack implements LeftClickListener, UpdateListener
{
    private final SliderSetting range;
    private final EnumSetting<Mode> mode;
    
    public SpeedNukerMod() {
        super("SpeedNuker", "Faster version of Nuker that cannot bypass NoCheat+.");
        this.range = new SliderSetting("Range", 5.0, 1.0, 6.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.mode = new EnumSetting<Mode>("Mode", Mode.values(), Mode.NORMAL);
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.range);
        this.addSetting(this.mode);
    }
    
    @Override
    public String getRenderName() {
        return this.mode.getSelected().renderName.get();
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { SpeedNukerMod.WURST.hax.nukerMod, SpeedNukerMod.WURST.hax.nukerLegitMod, SpeedNukerMod.WURST.hax.kaboomMod, SpeedNukerMod.WURST.hax.tunnellerMod };
    }
    
    @Override
    public void onEnable() {
        SpeedNukerMod.WURST.hax.nukerMod.setEnabled(false);
        SpeedNukerMod.WURST.hax.nukerLegitMod.setEnabled(false);
        SpeedNukerMod.WURST.hax.tunnellerMod.setEnabled(false);
        SpeedNukerMod.EVENTS.add(LeftClickListener.class, this);
        SpeedNukerMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        SpeedNukerMod.EVENTS.remove(LeftClickListener.class, this);
        SpeedNukerMod.EVENTS.remove(UpdateListener.class, this);
        SpeedNukerMod.WURST.hax.nukerMod.setId(0);
    }
    
    @Override
    public void onUpdate() {
        if (this.mode.getSelected() == Mode.ID && SpeedNukerMod.WURST.hax.nukerMod.getId() == 0) {
            return;
        }
        final Iterable<BlockPos> validBlocks = BlockUtils.getValidBlocksByDistanceReversed(this.range.getValue(), true, this.mode.getSelected().validator);
        validBlocks.forEach(pos -> BlockUtils.breakBlockPacketSpam(pos));
    }
    
    @Override
    public void onLeftClick(final LeftClickEvent event) {
        if (SpeedNukerMod.MC.objectMouseOver == null || SpeedNukerMod.MC.objectMouseOver.getBlockPos() == null) {
            return;
        }
        if (this.mode.getSelected() != Mode.ID) {
            return;
        }
        if (WBlock.getMaterial(SpeedNukerMod.MC.objectMouseOver.getBlockPos()) == Material.AIR) {
            return;
        }
        SpeedNukerMod.WURST.hax.nukerMod.setId(WBlock.getId(SpeedNukerMod.MC.objectMouseOver.getBlockPos()));
    }
    
    private enum Mode
    {
        NORMAL("Normal", () -> "SpeedNuker", pos -> true), 
        ID("ID", () -> "IDSpeedNuker [" + SpeedNukerMod.WURST.hax.nukerMod.getId() + "]", pos -> SpeedNukerMod.WURST.hax.nukerMod.getId() == WBlock.getId(pos)), 
        FLAT("Flat", () -> "FlatSpeedNuker", pos -> pos.getY() >= SpeedNukerMod.MC.player.posY), 
        SMASH("Smash", () -> "SmashSpeedNuker", pos -> WBlock.getHardness(pos) >= 1.0f);
        
        private final String name;
        private final Supplier<String> renderName;
        private final BlockUtils.BlockValidator validator;
        
        private Mode(final String name, final Supplier<String> renderName, final BlockUtils.BlockValidator validator) {
            this.name = name;
            this.renderName = renderName;
            this.validator = validator;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
