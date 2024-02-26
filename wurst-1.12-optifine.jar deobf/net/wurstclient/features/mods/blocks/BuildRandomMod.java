// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.wurstclient.compatibility.WItem;
import net.minecraft.entity.Entity;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "build random", "RandomBuild", "random build", "PlaceRandom", "place random", "RandomPlace", "random place" })
@Bypasses
public final class BuildRandomMod extends Hack implements UpdateListener, RenderListener
{
    private final EnumSetting<Mode> mode;
    private final CheckboxSetting checkItem;
    private final CheckboxSetting fastPlace;
    private final Random random;
    private BlockPos lastPos;
    
    public BuildRandomMod() {
        super("BuildRandom", "Randomly places blocks around you.\nTip: Using this mod in combination with FastPlace will make it faster.");
        this.mode = new EnumSetting<Mode>("Mode", "§lFast§r mode can place blocks behind other blocks.\n§lLegit§r mode can bypass NoCheat+.", Mode.values(), Mode.FAST);
        this.checkItem = new CheckboxSetting("Check held item", "Only builds when you are actually holding a block.\nTurn this off to build with fire, water, lava,\nspawn eggs, or if you just want to right click\nwith an empty hand in random places.", true);
        this.fastPlace = new CheckboxSetting("Always FastPlace", "Builds as if FastPlace was enabled,\neven if it's not.", false);
        this.random = new Random();
        this.setCategory(Category.BLOCKS);
        this.addSetting(this.mode);
        this.addSetting(this.checkItem);
        this.addSetting(this.fastPlace);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { BuildRandomMod.WURST.hax.fastPlaceMod, BuildRandomMod.WURST.hax.autoSwitchMod, BuildRandomMod.WURST.hax.autoBuildMod };
    }
    
    @Override
    public void onEnable() {
        BuildRandomMod.EVENTS.add(UpdateListener.class, this);
        BuildRandomMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        this.lastPos = null;
        BuildRandomMod.EVENTS.remove(UpdateListener.class, this);
        BuildRandomMod.EVENTS.remove(RenderListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        this.lastPos = null;
        if (BuildRandomMod.WURST.hax.freecamMod.isActive() || BuildRandomMod.WURST.hax.remoteViewMod.isActive()) {
            return;
        }
        if (BuildRandomMod.MC.rightClickDelayTimer > 0 && !BuildRandomMod.WURST.hax.fastPlaceMod.isActive() && !this.fastPlace.isChecked()) {
            return;
        }
        if (!this.checkHeldItem()) {
            return;
        }
        final boolean legitMode = this.mode.getSelected() == Mode.LEGIT;
        final int range = legitMode ? 5 : 6;
        final int bound = range * 2 + 1;
        int attempts = 0;
        BlockPos pos;
        do {
            pos = new BlockPos(BuildRandomMod.MC.player).add(this.random.nextInt(bound) - range, this.random.nextInt(bound) - range, this.random.nextInt(bound) - range);
        } while (++attempts < 128 && !this.tryToPlaceBlock(legitMode, pos));
    }
    
    private boolean checkHeldItem() {
        if (!this.checkItem.isChecked()) {
            return true;
        }
        final ItemStack stack = BuildRandomMod.MC.player.inventory.getCurrentItem();
        return !WItem.isNullOrEmpty(stack) && stack.getItem() instanceof ItemBlock;
    }
    
    private boolean tryToPlaceBlock(final boolean legitMode, final BlockPos pos) {
        if (!WBlock.getMaterial(pos).isReplaceable()) {
            return false;
        }
        if (legitMode) {
            if (!BlockUtils.placeBlockLegit(pos)) {
                return false;
            }
            BuildRandomMod.MC.rightClickDelayTimer = 4;
        }
        else {
            if (!BlockUtils.placeBlockSimple_old(pos)) {
                return false;
            }
            WPlayer.swingArmClient();
            BuildRandomMod.MC.rightClickDelayTimer = 4;
        }
        this.lastPos = pos;
        return true;
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (this.lastPos == null) {
            return;
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        RenderUtils.applyRenderOffset();
        GL11.glTranslated((double)this.lastPos.getX(), (double)this.lastPos.getY(), (double)this.lastPos.getZ());
        final float red = partialTicks * 2.0f;
        final float green = 2.0f - red;
        GL11.glColor4f(red, green, 0.0f, 0.25f);
        RenderUtils.drawSolidBox();
        GL11.glColor4f(red, green, 0.0f, 0.5f);
        RenderUtils.drawOutlinedBox();
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    private enum Mode
    {
        FAST("FAST", 0, "Fast"), 
        LEGIT("LEGIT", 1, "Legit");
        
        private final String name;
        
        private Mode(final String name2, final int ordinal, final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
