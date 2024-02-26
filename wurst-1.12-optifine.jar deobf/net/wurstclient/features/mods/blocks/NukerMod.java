// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.wurstclient.features.special_features.YesCheatSpf;
import net.wurstclient.util.RenderUtils;
import net.minecraft.util.math.Box;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.RayTraceResult;
import java.util.Iterator;
import java.util.stream.Stream;
import net.wurstclient.util.BlockUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.function.Predicate;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import java.util.stream.StreamSupport;
import net.wurstclient.util.RotationUtils;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import java.util.ArrayDeque;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.PostUpdateListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.features.Hack;

@Bypasses
public final class NukerMod extends Hack implements LeftClickListener, UpdateListener, PostUpdateListener, RenderListener
{
    public final SliderSetting range;
    public final ModeSetting mode;
    private final ModeSetting mode2;
    private final ArrayDeque<Set<BlockPos>> prevBlocks;
    private BlockPos currentBlock;
    private float progress;
    private float prevProgress;
    private int id;
    
    public NukerMod() {
        super("Nuker", "Automatically breaks blocks around you.");
        this.range = new SliderSetting("Range", 6.0, 1.0, 6.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.mode = new ModeSetting("Mode", new String[] { "Normal", "ID", "Flat", "Smash" }, 0);
        this.mode2 = new ModeSetting("Mode 2", new String[] { "Fast", "Legit" }, 0);
        this.prevBlocks = new ArrayDeque<Set<BlockPos>>();
        this.setCategory(Category.BLOCKS);
        this.addSetting(this.range);
        this.addSetting(this.mode);
        this.addSetting(this.mode2);
    }
    
    @Override
    public String getRenderName() {
        return Mode.values()[this.mode.getSelected()].getRenderName(this);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { NukerMod.WURST.hax.nukerLegitMod, NukerMod.WURST.hax.speedNukerMod, NukerMod.WURST.hax.tunnellerMod };
    }
    
    @Override
    public void onEnable() {
        NukerMod.WURST.hax.nukerLegitMod.setEnabled(false);
        NukerMod.WURST.hax.speedNukerMod.setEnabled(false);
        NukerMod.WURST.hax.tunnellerMod.setEnabled(false);
        NukerMod.EVENTS.add(LeftClickListener.class, this);
        NukerMod.EVENTS.add(UpdateListener.class, this);
        NukerMod.EVENTS.add(PostUpdateListener.class, this);
        NukerMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        NukerMod.EVENTS.remove(LeftClickListener.class, this);
        NukerMod.EVENTS.remove(UpdateListener.class, this);
        NukerMod.EVENTS.remove(PostUpdateListener.class, this);
        NukerMod.EVENTS.remove(RenderListener.class, this);
        if (this.currentBlock != null) {
            NukerMod.MC.playerController.isHittingBlock = true;
            NukerMod.MC.playerController.resetBlockRemoving();
        }
        this.prevBlocks.clear();
        this.currentBlock = null;
        this.id = 0;
    }
    
    @Override
    public void onUpdate() {
        if (this.mode.getSelected() == 1 && this.id == 0) {
            return;
        }
        this.currentBlock = null;
        final Vec3d eyesPos = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
        final BlockPos eyesBlock = new BlockPos(RotationUtils.getEyesPos());
        final double rangeSq = Math.pow(this.range.getValue(), 2.0);
        final int blockRange = (int)Math.ceil(this.range.getValue());
        final boolean legit = this.mode2.getSelected() == 1;
        final Stream<BlockPos> stream = StreamSupport.stream(BlockPos.getAllInBox(eyesBlock.add(blockRange, blockRange, blockRange), eyesBlock.add(-blockRange, -blockRange, -blockRange)).spliterator(), true);
        BlockPos pos = null;
        final List<BlockPos> blocks = stream.filter(pos -> vec3d.squareDistanceTo(new Vec3d(pos)) <= n).filter(pos -> WBlock.canBeClicked(pos)).filter(Mode.values()[this.mode.getSelected()].getValidator(this)).sorted(Comparator.comparingDouble(pos -> vec3d2.squareDistanceTo(new Vec3d(pos)))).collect((Collector<? super BlockPos, ?, List<BlockPos>>)Collectors.toList());
        if (NukerMod.MC.player.abilities.creativeMode && !legit) {
            Stream<BlockPos> stream2 = blocks.parallelStream();
            for (final Set<BlockPos> set : this.prevBlocks) {
                stream2 = stream2.filter(pos -> !set2.contains(pos));
            }
            final List<BlockPos> blocks2 = stream2.collect((Collector<? super BlockPos, ?, List<BlockPos>>)Collectors.toList());
            this.prevBlocks.addLast(new HashSet<BlockPos>(blocks2));
            while (this.prevBlocks.size() > 5) {
                this.prevBlocks.removeFirst();
            }
            if (!blocks2.isEmpty()) {
                this.currentBlock = blocks2.get(0);
            }
            NukerMod.MC.playerController.resetBlockRemoving();
            this.progress = 1.0f;
            this.prevProgress = 1.0f;
            BlockUtils.breakBlocksPacketSpam(blocks2);
            return;
        }
        final Iterator<BlockPos> iterator2 = blocks.iterator();
        while (iterator2.hasNext()) {
            pos = iterator2.next();
            final boolean successful = legit ? BlockUtils.prepareToBreakBlockLegit(pos) : BlockUtils.breakBlockSimple_old(pos);
            if (successful) {
                this.currentBlock = pos;
                break;
            }
        }
        if (this.currentBlock == null) {
            NukerMod.MC.playerController.resetBlockRemoving();
        }
        if (this.currentBlock != null && WBlock.getHardness(this.currentBlock) < 1.0f && !NukerMod.MC.player.abilities.creativeMode) {
            this.prevProgress = this.progress;
            this.progress = NukerMod.MC.playerController.curBlockDamageMP;
            if (this.progress < this.prevProgress) {
                this.prevProgress = this.progress;
            }
        }
        else {
            this.progress = 1.0f;
            this.prevProgress = 1.0f;
        }
    }
    
    @Override
    public void afterUpdate() {
        final boolean legit = this.mode2.getSelected() == 1;
        if (this.currentBlock != null && legit) {
            BlockUtils.breakBlockLegit(this.currentBlock);
        }
    }
    
    @Override
    public void onLeftClick(final LeftClickEvent event) {
        if (NukerMod.MC.objectMouseOver == null || NukerMod.MC.objectMouseOver.getBlockPos() == null || NukerMod.MC.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }
        if (this.mode.getSelected() == Mode.ID.ordinal()) {
            this.id = WBlock.getId(NukerMod.MC.objectMouseOver.getBlockPos());
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (this.currentBlock == null) {
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
        GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
        final Box box = new Box(BlockPos.ORIGIN);
        final float p = this.prevProgress + (this.progress - this.prevProgress) * partialTicks;
        final float red = p * 2.0f;
        final float green = 2.0f - red;
        GL11.glTranslated((double)this.currentBlock.getX(), (double)this.currentBlock.getY(), (double)this.currentBlock.getZ());
        if (p < 1.0f) {
            GL11.glTranslated(0.5, 0.5, 0.5);
            GL11.glScaled((double)p, (double)p, (double)p);
            GL11.glTranslated(-0.5, -0.5, -0.5);
        }
        GL11.glColor4f(red, green, 0.0f, 0.25f);
        RenderUtils.drawSolidBox(box);
        GL11.glColor4f(red, green, 0.0f, 0.5f);
        RenderUtils.drawOutlinedBox(box);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            default: {
                this.range.resetUsableMax();
                this.mode2.unlock();
                break;
            }
            case ANTICHEAT:
            case OLDER_NCP:
            case LATEST_NCP:
            case GHOST_MODE: {
                this.range.setUsableMax(4.25);
                this.mode2.lock(1);
                break;
            }
        }
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    private enum Mode
    {
        NORMAL("Normal", n -> n.getName(), (n, p) -> true), 
        ID("ID", n -> "IDNuker [" + n.id + "]", (n, p) -> WBlock.getId(p) == n.id), 
        FLAT("Flat", n -> "FlatNuker", (n, p) -> p.getY() >= NukerMod.MC.player.getPosition().getY()), 
        SMASH("Smash", n -> "SmashNuker", (n, p) -> WBlock.getHardness(p) >= 1.0f);
        
        private final String name;
        private final Function<NukerMod, String> renderName;
        private final BiPredicate<NukerMod, BlockPos> validator;
        
        private Mode(final String name, final Function<NukerMod, String> renderName, final BiPredicate<NukerMod, BlockPos> validator) {
            this.name = name;
            this.renderName = renderName;
            this.validator = validator;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public String getRenderName(final NukerMod n) {
            return this.renderName.apply(n);
        }
        
        public Predicate<BlockPos> getValidator(final NukerMod n) {
            return p -> this.validator.test(nukerMod, p);
        }
    }
}
