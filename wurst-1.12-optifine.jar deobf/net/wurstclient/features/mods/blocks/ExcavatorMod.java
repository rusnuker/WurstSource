// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.ai.PathFinder;
import net.wurstclient.compatibility.WMath;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.Comparator;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import net.wurstclient.features.special_features.YesCheatSpf;
import net.wurstclient.font.Fonts;
import net.minecraft.client.gui.ScaledResolution;
import net.wurstclient.features.commands.PathCmd;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.ai.PathProcessor;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.PostUpdateListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses
public final class ExcavatorMod extends Hack implements UpdateListener, PostUpdateListener, RenderListener, GUIRenderListener
{
    private Step step;
    private BlockPos posLookingAt;
    private Area area;
    private BlockPos currentBlock;
    private ExcavatorPathFinder pathFinder;
    private PathProcessor processor;
    private final SliderSetting range;
    private final ModeSetting mode;
    
    public ExcavatorMod() {
        super("Excavator", "Automatically breaks all blocks in the selected area.");
        this.range = new SliderSetting("Range", 6.0, 2.0, 6.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.mode = new ModeSetting("Mode", new String[] { "Fast", "Legit" }, 0);
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public void initSettings() {
        this.addSetting(this.range);
        this.addSetting(this.mode);
    }
    
    @Override
    public String getRenderName() {
        String name = this.getName();
        if (this.step == Step.EXCAVATE && this.area != null) {
            name = String.valueOf(name) + " " + (int)((this.area.blocksList.size() - this.area.remainingBlocks) / (float)this.area.blocksList.size() * 100.0f) + "%";
        }
        return name;
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { ExcavatorMod.WURST.commands.excavateCmd, ExcavatorMod.WURST.hax.nukerMod };
    }
    
    @Override
    public void onEnable() {
        ExcavatorMod.WURST.hax.bowAimbotMod.setEnabled(false);
        ExcavatorMod.WURST.hax.templateToolMod.setEnabled(false);
        this.step = Step.START_POS;
        ExcavatorMod.EVENTS.add(UpdateListener.class, this);
        ExcavatorMod.EVENTS.add(PostUpdateListener.class, this);
        ExcavatorMod.EVENTS.add(RenderListener.class, this);
        ExcavatorMod.EVENTS.add(GUIRenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        ExcavatorMod.EVENTS.remove(UpdateListener.class, this);
        ExcavatorMod.EVENTS.remove(PostUpdateListener.class, this);
        ExcavatorMod.EVENTS.remove(RenderListener.class, this);
        ExcavatorMod.EVENTS.remove(GUIRenderListener.class, this);
        Step[] values;
        for (int length = (values = Step.values()).length, i = 0; i < length; ++i) {
            final Step step = values[i];
            Step.access$2(step, null);
        }
        this.posLookingAt = null;
        this.area = null;
        ExcavatorMod.MC.playerController.resetBlockRemoving();
        this.currentBlock = null;
        this.pathFinder = null;
        this.processor = null;
        PathProcessor.releaseControls();
    }
    
    @Override
    public void onUpdate() {
        if (this.step.selectPos) {
            this.handlePositionSelection();
        }
        else if (this.step == Step.SCAN_AREA) {
            this.scanArea();
        }
        else if (this.step == Step.EXCAVATE) {
            this.excavate();
        }
    }
    
    @Override
    public void afterUpdate() {
        if (this.currentBlock != null && this.mode.getSelected() == 1) {
            BlockUtils.breakBlockLegit(this.currentBlock);
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (this.pathFinder != null) {
            final PathCmd pathCmd = ExcavatorMod.WURST.commands.pathCmd;
            this.pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
        }
        final double scale = 0.875;
        final double offset = (1.0 - scale) / 2.0;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        GL11.glTranslated(-ExcavatorMod.MC.getRenderManager().renderPosX, -ExcavatorMod.MC.getRenderManager().renderPosY, -ExcavatorMod.MC.getRenderManager().renderPosZ);
        if (this.area != null) {
            GL11.glEnable(2929);
            if (this.step == Step.SCAN_AREA && this.area.progress < 1.0f) {
                for (int i = Math.max(0, this.area.blocksList.size() - this.area.scanSpeed); i < this.area.blocksList.size(); ++i) {
                    final BlockPos pos = this.area.blocksList.get(i);
                    GL11.glPushMatrix();
                    GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                    GL11.glTranslated(-0.005, -0.005, -0.005);
                    GL11.glScaled(1.01, 1.01, 1.01);
                    GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.15f);
                    RenderUtils.drawSolidBox();
                    GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
                    RenderUtils.drawOutlinedBox();
                    GL11.glPopMatrix();
                }
            }
            GL11.glPushMatrix();
            GL11.glTranslated(this.area.minX + offset, this.area.minY + offset, this.area.minZ + offset);
            GL11.glScaled(this.area.sizeX + scale, this.area.sizeY + scale, this.area.sizeZ + scale);
            if (this.area.progress < 1.0f) {
                GL11.glPushMatrix();
                GL11.glTranslated(0.0, 0.0, (double)this.area.progress);
                GL11.glScaled(1.0, 1.0, 0.0);
                GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.3f);
                RenderUtils.drawSolidBox();
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
                RenderUtils.drawOutlinedBox();
                GL11.glPopMatrix();
            }
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
            RenderUtils.drawOutlinedBox();
            GL11.glPopMatrix();
            GL11.glDisable(2929);
        }
        Step[] access$4;
        for (int length = (access$4 = Step.SELECT_POSITION_STEPS).length, j = 0; j < length; ++j) {
            final Step step = access$4[j];
            final BlockPos pos2 = step.pos;
            if (pos2 != null) {
                GL11.glPushMatrix();
                GL11.glTranslated((double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ());
                GL11.glTranslated(offset, offset, offset);
                GL11.glScaled(scale, scale, scale);
                GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.15f);
                RenderUtils.drawSolidBox();
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
                RenderUtils.drawOutlinedBox();
                GL11.glPopMatrix();
            }
        }
        if (this.posLookingAt != null) {
            GL11.glPushMatrix();
            GL11.glTranslated((double)this.posLookingAt.getX(), (double)this.posLookingAt.getY(), (double)this.posLookingAt.getZ());
            GL11.glTranslated(offset, offset, offset);
            GL11.glScaled(scale, scale, scale);
            GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.15f);
            RenderUtils.drawSolidBox();
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
            RenderUtils.drawOutlinedBox();
            GL11.glPopMatrix();
        }
        if (this.currentBlock != null) {
            GL11.glTranslated((double)this.currentBlock.getX(), (double)this.currentBlock.getY(), (double)this.currentBlock.getZ());
            float progress;
            if (WBlock.getHardness(this.currentBlock) < 1.0f) {
                progress = ExcavatorMod.MC.playerController.curBlockDamageMP;
            }
            else {
                progress = 1.0f;
            }
            if (progress < 1.0f) {
                GL11.glTranslated(0.5, 0.5, 0.5);
                GL11.glScaled((double)progress, (double)progress, (double)progress);
                GL11.glTranslated(-0.5, -0.5, -0.5);
            }
            final float red = progress * 2.0f;
            final float green = 2.0f - red;
            GL11.glColor4f(red, green, 0.0f, 0.25f);
            RenderUtils.drawSolidBox();
            GL11.glColor4f(red, green, 0.0f, 0.5f);
            RenderUtils.drawOutlinedBox();
        }
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    @Override
    public void onRenderGUI() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GL11.glPushMatrix();
        String message;
        if (this.step.selectPos && this.step.pos != null) {
            message = "Press enter to confirm, or select a different position.";
        }
        else {
            message = this.step.message;
        }
        final ScaledResolution sr = new ScaledResolution(ExcavatorMod.MC);
        final int msgWidth = Fonts.segoe15.getStringWidth(message);
        GL11.glTranslated((double)(sr.getScaledWidth() / 2 - msgWidth / 2), (double)(sr.getScaledHeight() / 2 + 1), 0.0);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
        GL11.glBegin(7);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glVertex2d((double)(msgWidth + 2), 0.0);
        GL11.glVertex2d((double)(msgWidth + 2), 10.0);
        GL11.glVertex2d(0.0, 10.0);
        GL11.glEnd();
        GL11.glEnable(3553);
        Fonts.segoe15.drawString(message, 2, -1, -1);
        GL11.glPopMatrix();
        GL11.glEnable(2884);
        GL11.glDisable(3042);
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            default: {
                this.range.resetUsableMax();
                this.mode.unlock();
                break;
            }
            case ANTICHEAT:
            case OLDER_NCP:
            case LATEST_NCP:
            case GHOST_MODE: {
                this.range.setUsableMax(4.25);
                this.mode.lock(1);
                break;
            }
        }
    }
    
    public void enableWithArea(final BlockPos pos1, final BlockPos pos2) {
        this.setEnabled(true);
        Step.access$2(Step.START_POS, pos1);
        Step.access$2(Step.END_POS, pos2);
        this.step = Step.SCAN_AREA;
    }
    
    private void handlePositionSelection() {
        if (this.step.pos != null && Keyboard.isKeyDown(28)) {
            this.step = Step.values()[this.step.ordinal() + 1];
            if (!this.step.selectPos) {
                this.posLookingAt = null;
            }
            return;
        }
        if (ExcavatorMod.MC.objectMouseOver != null && ExcavatorMod.MC.objectMouseOver.getBlockPos() != null) {
            this.posLookingAt = ExcavatorMod.MC.objectMouseOver.getBlockPos();
            if (ExcavatorMod.MC.gameSettings.keyBindSneak.pressed) {
                this.posLookingAt = this.posLookingAt.offset(ExcavatorMod.MC.objectMouseOver.sideHit);
            }
        }
        else {
            this.posLookingAt = null;
        }
        if (this.posLookingAt != null && ExcavatorMod.MC.gameSettings.keyBindUseItem.pressed) {
            Step.access$2(this.step, this.posLookingAt);
        }
    }
    
    private void scanArea() {
        if (this.area == null) {
            this.area = new Area(Step.START_POS.pos, Step.END_POS.pos, null);
            Step.access$2(Step.START_POS, null);
            Step.access$2(Step.END_POS, null);
        }
        for (int i = 0; i < this.area.scanSpeed && this.area.iterator.hasNext(); ++i) {
            final Area area = this.area;
            Area.access$13(area, area.scannedBlocks + 1);
            final BlockPos pos = this.area.iterator.next();
            if (WBlock.canBeClicked(pos)) {
                this.area.blocksList.add(pos);
                this.area.blocksSet.add(pos);
            }
        }
        Area.access$16(this.area, this.area.scannedBlocks / (float)this.area.totalBlocks);
        if (!this.area.iterator.hasNext()) {
            Area.access$17(this.area, this.area.blocksList.size());
            this.step = Step.values()[this.step.ordinal() + 1];
        }
    }
    
    private void excavate() {
        final boolean legit = this.mode.getSelected() == 1;
        this.currentBlock = null;
        BlockPos pos = null;
        final Iterable<BlockPos> validBlocks = BlockUtils.getValidBlocksByDistance(this.range.getValue(), !legit, pos -> this.area.blocksSet.contains(pos));
        if (ExcavatorMod.MC.player.abilities.creativeMode && !legit) {
            ExcavatorMod.MC.playerController.resetBlockRemoving();
            final Iterator<BlockPos> iterator = validBlocks.iterator();
            if (iterator.hasNext()) {
                pos = iterator.next();
                this.currentBlock = pos;
            }
            validBlocks.forEach(pos -> BlockUtils.breakBlockPacketSpam(pos));
        }
        else {
            final ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
            for (final BlockPos pos2 : validBlocks) {
                blocks.add(pos2);
            }
            blocks.sort(Comparator.comparingInt(pos -> -pos.getY()));
            for (final BlockPos pos2 : blocks) {
                boolean successful;
                if (legit) {
                    successful = BlockUtils.prepareToBreakBlockLegit(pos2);
                }
                else {
                    successful = BlockUtils.breakBlockSimple_old(pos2);
                }
                if (successful) {
                    this.currentBlock = pos2;
                    break;
                }
            }
            if (this.currentBlock == null) {
                ExcavatorMod.MC.playerController.resetBlockRemoving();
            }
        }
        final Predicate<BlockPos> pClickable = pos -> WBlock.canBeClicked(pos);
        Area.access$17(this.area, (int)this.area.blocksList.parallelStream().filter(pClickable).count());
        if (this.area.remainingBlocks == 0) {
            this.setEnabled(false);
            return;
        }
        if (this.pathFinder == null) {
            final Comparator<BlockPos> cDistance = Comparator.comparingDouble(pos -> ExcavatorMod.MC.player.getDistanceSqToCenter(pos));
            final Comparator<BlockPos> cAltitude = Comparator.comparingInt(pos -> -pos.getY());
            final BlockPos closestBlock = (BlockPos)this.area.blocksList.parallelStream().filter(pClickable).min(cAltitude.thenComparing(cDistance)).get();
            this.pathFinder = new ExcavatorPathFinder(closestBlock);
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
            this.pathFinder = new ExcavatorPathFinder(this.pathFinder);
            return;
        }
        this.processor.process();
        if (this.processor.isDone()) {
            this.pathFinder = null;
            this.processor = null;
            PathProcessor.releaseControls();
        }
    }
    
    private enum Step
    {
        START_POS("START_POS", 0, "Select start position.", true), 
        END_POS("END_POS", 1, "Select end position.", true), 
        SCAN_AREA("SCAN_AREA", 2, "Scanning area...", false), 
        EXCAVATE("EXCAVATE", 3, "Excavating...", false);
        
        private static final Step[] SELECT_POSITION_STEPS;
        private final String message;
        private boolean selectPos;
        private BlockPos pos;
        
        static {
            SELECT_POSITION_STEPS = new Step[] { Step.START_POS, Step.END_POS };
        }
        
        private Step(final String name, final int ordinal, final String message, final boolean selectPos) {
            this.message = message;
            this.selectPos = selectPos;
        }
        
        static /* synthetic */ void access$2(final Step step, final BlockPos pos) {
            step.pos = pos;
        }
    }
    
    private static class Area
    {
        private final int minX;
        private final int minY;
        private final int minZ;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;
        private final int totalBlocks;
        private final int scanSpeed;
        private final Iterator<BlockPos> iterator;
        private int scannedBlocks;
        private int remainingBlocks;
        private float progress;
        private final ArrayList<BlockPos> blocksList;
        private final HashSet<BlockPos> blocksSet;
        
        private Area(final BlockPos start, final BlockPos end) {
            this.blocksList = new ArrayList<BlockPos>();
            this.blocksSet = new HashSet<BlockPos>();
            final int startX = start.getX();
            final int startY = start.getY();
            final int startZ = start.getZ();
            final int endX = end.getX();
            final int endY = end.getY();
            final int endZ = end.getZ();
            this.minX = Math.min(startX, endX);
            this.minY = Math.min(startY, endY);
            this.minZ = Math.min(startZ, endZ);
            this.sizeX = Math.abs(startX - endX);
            this.sizeY = Math.abs(startY - endY);
            this.sizeZ = Math.abs(startZ - endZ);
            this.totalBlocks = (this.sizeX + 1) * (this.sizeY + 1) * (this.sizeZ + 1);
            this.scanSpeed = WMath.clamp(this.totalBlocks / 30, 1, 16384);
            this.iterator = BlockPos.getAllInBox(start, end).iterator();
        }
        
        static /* synthetic */ void access$13(final Area area, final int scannedBlocks) {
            area.scannedBlocks = scannedBlocks;
        }
        
        static /* synthetic */ void access$16(final Area area, final float progress) {
            area.progress = progress;
        }
        
        static /* synthetic */ void access$17(final Area area, final int remainingBlocks) {
            area.remainingBlocks = remainingBlocks;
        }
    }
    
    private static class ExcavatorPathFinder extends PathFinder
    {
        public ExcavatorPathFinder(final BlockPos goal) {
            super(goal);
            this.setThinkTime(10);
        }
        
        public ExcavatorPathFinder(final ExcavatorPathFinder pathFinder) {
            super(pathFinder);
        }
        
        @Override
        protected boolean checkDone() {
            final BlockPos goal = this.getGoal();
            return this.done = (goal.down(2).equals(this.current) || goal.up().equals(this.current) || goal.north().equals(this.current) || goal.south().equals(this.current) || goal.west().equals(this.current) || goal.east().equals(this.current));
        }
    }
}
