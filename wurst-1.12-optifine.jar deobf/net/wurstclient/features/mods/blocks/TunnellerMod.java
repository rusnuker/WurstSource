// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.minecraft.block.BlockTorch;
import net.minecraft.world.World;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockLiquid;
import java.util.HashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.wurstclient.compatibility.WItem;
import java.util.Comparator;
import net.wurstclient.util.RotationUtils;
import java.util.Iterator;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.compatibility.WBlock;
import java.util.List;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.ArrayList;
import net.wurstclient.WurstClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.util.RenderUtils;
import net.minecraft.util.math.Box;
import net.minecraft.client.settings.GameSettings;
import net.wurstclient.features.mods.LocalHackList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses
@DontSaveState
public final class TunnellerMod extends Hack implements UpdateListener, RenderListener
{
    private final EnumSetting<TunnelSize> size;
    private final SliderSetting limit;
    private final CheckboxSetting torches;
    private BlockPos start;
    private EnumFacing direction;
    private int length;
    private Task[] tasks;
    private int[] displayLists;
    private BlockPos currentBlock;
    private float progress;
    private float prevProgress;
    
    public TunnellerMod() {
        super("Tunneller", "Automatically digs a tunnel.\n\n" + ChatFormatting.RED + ChatFormatting.BOLD + "WARNING:" + ChatFormatting.RESET + " Although this bot will try to avoid\n" + "lava and other dangers, there is no guarantee\n" + "that it won't die. Only send it out with gear\n" + "that you don't mind losing.");
        this.size = new EnumSetting<TunnelSize>("Tunnel size", TunnelSize.values(), TunnelSize.SIZE_3X3);
        this.limit = new SliderSetting("Limit", "Automatically stops once the tunnel\nhas reached the given length.\n\n0 = no limit", 0.0, 0.0, 1000.0, 1.0, v -> {
            String string;
            if (v == 0.0) {
                string = "disabled";
            }
            else if (v == 1.0) {
                string = "1 block";
            }
            else {
                string = String.valueOf((int)v) + " blocks";
            }
            return string;
        });
        this.torches = new CheckboxSetting("Place torches", "Places just enough torches\nto prevent mobs from\nspawning inside the tunnel.", false);
        this.displayLists = new int[5];
        this.setCategory(Category.BLOCKS);
        this.addSetting(this.size);
        this.addSetting(this.limit);
        this.addSetting(this.torches);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { TunnellerMod.WURST.hax.nukerMod, TunnellerMod.WURST.hax.nukerLegitMod, TunnellerMod.WURST.hax.speedNukerMod };
    }
    
    @Override
    public String getRenderName() {
        if (this.limit.getValueI() == 0) {
            return this.getName();
        }
        return String.valueOf(this.getName()) + " [" + this.length + "/" + this.limit.getValueI() + "]";
    }
    
    @Override
    public void onEnable() {
        TunnellerMod.EVENTS.add(UpdateListener.class, this);
        TunnellerMod.EVENTS.add(RenderListener.class, this);
        for (int i = 0; i < this.displayLists.length; ++i) {
            this.displayLists[i] = GL11.glGenLists(1);
        }
        final EntityPlayerSP player = TunnellerMod.MC.player;
        this.start = new BlockPos(player);
        this.direction = player.getHorizontalFacing();
        this.length = 0;
        this.tasks = new Task[] { new DodgeLiquidTask((DodgeLiquidTask)null), new FillInFloorTask((FillInFloorTask)null), new PlaceTorchTask((PlaceTorchTask)null), new DigTunnelTask((DigTunnelTask)null), new WalkForwardTask((WalkForwardTask)null) };
        this.updateCyanList();
    }
    
    @Override
    public void onDisable() {
        TunnellerMod.EVENTS.remove(UpdateListener.class, this);
        TunnellerMod.EVENTS.remove(RenderListener.class, this);
        if (this.currentBlock != null) {
            TunnellerMod.MC.playerController.isHittingBlock = true;
            TunnellerMod.MC.playerController.resetBlockRemoving();
            this.currentBlock = null;
        }
        int[] displayLists;
        for (int length = (displayLists = this.displayLists).length, i = 0; i < length; ++i) {
            final int displayList = displayLists[i];
            GL11.glDeleteLists(displayList, 1);
        }
    }
    
    @Override
    public void onUpdate() {
        final LocalHackList mods = TunnellerMod.WURST.hax;
        final Hack[] incompatibleMods = { mods.autoToolMod, mods.autoWalkMod, mods.blinkMod, mods.flightMod, mods.nukerMod, mods.nukerLegitMod, mods.speedNukerMod, mods.sneakMod };
        Hack[] array;
        for (int length = (array = incompatibleMods).length, i = 0; i < length; ++i) {
            final Hack hack = array[i];
            hack.setEnabled(false);
        }
        if (mods.freecamMod.isEnabled()) {
            return;
        }
        final GameSettings gs = TunnellerMod.MC.gameSettings;
        final KeyBinding[] bindings = { gs.keyBindForward, gs.keyBindBack, gs.keyBindLeft, gs.keyBindRight, gs.keyBindJump, gs.keyBindSneak };
        KeyBinding[] array2;
        for (int length2 = (array2 = bindings).length, j = 0; j < length2; ++j) {
            final KeyBinding binding = array2[j];
            binding.pressed = false;
        }
        Task[] tasks;
        for (int length3 = (tasks = this.tasks).length, k = 0; k < length3; ++k) {
            final Task task = tasks[k];
            if (task.canRun()) {
                task.run();
                break;
            }
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        GL11.glTranslated(-TunnellerMod.MC.getRenderManager().renderPosX, -TunnellerMod.MC.getRenderManager().renderPosY, -TunnellerMod.MC.getRenderManager().renderPosZ);
        int[] displayLists;
        for (int length = (displayLists = this.displayLists).length, i = 0; i < length; ++i) {
            final int displayList = displayLists[i];
            GL11.glCallList(displayList);
        }
        if (this.currentBlock != null) {
            final float p = this.prevProgress + (this.progress - this.prevProgress) * partialTicks;
            final float red = p * 2.0f;
            final float green = 2.0f - red;
            GL11.glTranslated((double)this.currentBlock.getX(), (double)this.currentBlock.getY(), (double)this.currentBlock.getZ());
            if (p < 1.0f) {
                GL11.glTranslated(0.5, 0.5, 0.5);
                GL11.glScaled((double)p, (double)p, (double)p);
                GL11.glTranslated(-0.5, -0.5, -0.5);
            }
            final Box box2 = new Box(BlockPos.ORIGIN);
            GL11.glColor4f(red, green, 0.0f, 0.25f);
            RenderUtils.drawSolidBox(box2);
            GL11.glColor4f(red, green, 0.0f, 0.5f);
            RenderUtils.drawOutlinedBox(box2);
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    private void updateCyanList() {
        GL11.glNewList(this.displayLists[0], 4864);
        GL11.glPushMatrix();
        GL11.glTranslated((double)this.start.getX(), (double)this.start.getY(), (double)this.start.getZ());
        GL11.glTranslated(0.5, 0.5, 0.5);
        GL11.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        GL11.glBegin(1);
        RenderUtils.drawNode(new Box(-0.25, -0.25, -0.25, 0.25, 0.25, 0.25));
        GL11.glEnd();
        RenderUtils.drawArrow(new Vec3d(this.direction.getDirectionVec()).scale(0.25), new Vec3d(this.direction.getDirectionVec()).scale(Math.max(0.5, this.length)));
        GL11.glPopMatrix();
        GL11.glEndList();
    }
    
    private BlockPos offset(final BlockPos pos, final Vec3i vec) {
        return pos.offset(this.direction.rotateYCCW(), vec.getX()).up(vec.getY());
    }
    
    private int getDistance(final BlockPos pos1, final BlockPos pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY()) + Math.abs(pos1.getZ() - pos2.getZ());
    }
    
    static /* synthetic */ void access$8(final TunnellerMod tunnellerMod, final BlockPos currentBlock) {
        tunnellerMod.currentBlock = currentBlock;
    }
    
    static /* synthetic */ void access$10(final TunnellerMod tunnellerMod, final float progress) {
        tunnellerMod.progress = progress;
    }
    
    static /* synthetic */ void access$11(final TunnellerMod tunnellerMod, final float prevProgress) {
        tunnellerMod.prevProgress = prevProgress;
    }
    
    static /* synthetic */ void access$12(final TunnellerMod tunnellerMod, final int length) {
        tunnellerMod.length = length;
    }
    
    private enum TunnelSize
    {
        SIZE_1X2("SIZE_1X2", 0, "1x2", new Vec3i(0, 1, 0), new Vec3i(0, 0, 0), 4, 13), 
        SIZE_3X3("SIZE_3X3", 1, "3x3", new Vec3i(1, 2, 0), new Vec3i(-1, 0, 0), 4, 11);
        
        private final String name;
        private final Vec3i from;
        private final Vec3i to;
        private final int maxRange;
        private final int torchDistance;
        
        private TunnelSize(final String name2, final int ordinal, final String name, final Vec3i from, final Vec3i to, final int maxRange, final int torchDistance) {
            this.name = name;
            this.from = from;
            this.to = to;
            this.maxRange = maxRange;
            this.torchDistance = torchDistance;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    private abstract static class Task
    {
        public abstract boolean canRun();
        
        public abstract void run();
    }
    
    private class DigTunnelTask extends Task
    {
        private int requiredDistance;
        
        private DigTunnelTask() {
            super(null);
        }
        
        @Override
        public boolean canRun() {
            final BlockPos player = new BlockPos(TunnellerMod.MC.player);
            final BlockPos base = TunnellerMod.this.start.offset(TunnellerMod.this.direction, TunnellerMod.this.length);
            final int distance = TunnellerMod.this.getDistance(player, base);
            if (distance <= 1) {
                this.requiredDistance = TunnellerMod.this.size.getSelected().maxRange;
            }
            else if (distance > TunnellerMod.this.size.getSelected().maxRange) {
                this.requiredDistance = 1;
            }
            return distance <= this.requiredDistance;
        }
        
        @Override
        public void run() {
            final BlockPos base = TunnellerMod.this.start.offset(TunnellerMod.this.direction, TunnellerMod.this.length);
            final BlockPos from = TunnellerMod.this.offset(base, TunnellerMod.this.size.getSelected().from);
            final BlockPos to = TunnellerMod.this.offset(base, TunnellerMod.this.size.getSelected().to);
            final ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
            BlockPos.getAllInBox(from, to).forEach(blocks::add);
            Collections.reverse(blocks);
            GL11.glNewList(TunnellerMod.this.displayLists[1], 4864);
            final Box box = new Box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
            for (final BlockPos pos : blocks) {
                GL11.glPushMatrix();
                GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                RenderUtils.drawOutlinedBox(box);
                GL11.glPopMatrix();
            }
            GL11.glEndList();
            TunnellerMod.access$8(TunnellerMod.this, null);
            for (final BlockPos pos : blocks) {
                if (!WBlock.canBeClicked(pos)) {
                    continue;
                }
                TunnellerMod.access$8(TunnellerMod.this, pos);
                break;
            }
            if (TunnellerMod.this.currentBlock == null) {
                TunnellerMod.MC.playerController.resetBlockRemoving();
                TunnellerMod.access$10(TunnellerMod.this, 1.0f);
                TunnellerMod.access$11(TunnellerMod.this, 1.0f);
                final TunnellerMod this$0 = TunnellerMod.this;
                TunnellerMod.access$12(this$0, this$0.length + 1);
                if (TunnellerMod.this.limit.getValueI() == 0 || TunnellerMod.this.length < TunnellerMod.this.limit.getValueI()) {
                    TunnellerMod.this.updateCyanList();
                }
                else {
                    ChatUtils.message("Tunnel completed.");
                    TunnellerMod.this.setEnabled(false);
                }
                return;
            }
            TunnellerMod.WURST.hax.autoToolMod.equipBestTool(TunnellerMod.this.currentBlock, false, true, false);
            BlockUtils.breakBlockSimple(TunnellerMod.this.currentBlock);
            if (TunnellerMod.MC.player.abilities.creativeMode || WBlock.getHardness(TunnellerMod.this.currentBlock) >= 1.0f) {
                TunnellerMod.access$10(TunnellerMod.this, 1.0f);
                TunnellerMod.access$11(TunnellerMod.this, 1.0f);
                return;
            }
            TunnellerMod.access$11(TunnellerMod.this, TunnellerMod.this.progress);
            TunnellerMod.access$10(TunnellerMod.this, TunnellerMod.MC.playerController.curBlockDamageMP);
            if (TunnellerMod.this.progress < TunnellerMod.this.prevProgress) {
                TunnellerMod.access$11(TunnellerMod.this, TunnellerMod.this.progress);
            }
        }
    }
    
    private class WalkForwardTask extends Task
    {
        private WalkForwardTask() {
            super(null);
        }
        
        @Override
        public boolean canRun() {
            final BlockPos player = new BlockPos(TunnellerMod.MC.player);
            final BlockPos base = TunnellerMod.this.start.offset(TunnellerMod.this.direction, TunnellerMod.this.length);
            return TunnellerMod.this.getDistance(player, base) > 1;
        }
        
        @Override
        public void run() {
            final BlockPos base = TunnellerMod.this.start.offset(TunnellerMod.this.direction, TunnellerMod.this.length);
            final Vec3d vec = new Vec3d(base).addVector(0.5, 0.5, 0.5);
            RotationUtils.faceVectorForWalking(vec);
            TunnellerMod.MC.gameSettings.keyBindForward.pressed = true;
        }
    }
    
    private class FillInFloorTask extends Task
    {
        private final ArrayList<BlockPos> blocks;
        
        private FillInFloorTask() {
            super(null);
            this.blocks = new ArrayList<BlockPos>();
        }
        
        @Override
        public boolean canRun() {
            final BlockPos player = new BlockPos(TunnellerMod.MC.player);
            final BlockPos from = this.offsetFloor(player, TunnellerMod.this.size.getSelected().from);
            final BlockPos to = this.offsetFloor(player, TunnellerMod.this.size.getSelected().to);
            this.blocks.clear();
            for (final BlockPos pos : BlockPos.getAllInBox(from, to)) {
                if (!WBlock.getState(pos).isFullBlock()) {
                    this.blocks.add(pos);
                }
            }
            GL11.glNewList(TunnellerMod.this.displayLists[2], 4864);
            final Box box = new Box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);
            GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
            for (final BlockPos pos2 : this.blocks) {
                GL11.glPushMatrix();
                GL11.glTranslated((double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ());
                GL11.glBegin(1);
                RenderUtils.drawOutlinedBox(box);
                GL11.glEnd();
                GL11.glPopMatrix();
            }
            GL11.glEndList();
            return !this.blocks.isEmpty();
        }
        
        private BlockPos offsetFloor(final BlockPos pos, final Vec3i vec) {
            return pos.offset(TunnellerMod.this.direction.rotateYCCW(), vec.getX()).down();
        }
        
        @Override
        public void run() {
            TunnellerMod.MC.gameSettings.keyBindSneak.pressed = true;
            TunnellerMod.MC.player.motionX = 0.0;
            TunnellerMod.MC.player.motionZ = 0.0;
            final Vec3d eyes = RotationUtils.getEyesPos().addVector(-0.5, -0.5, -0.5);
            final Comparator<BlockPos> comparator = Comparator.comparingDouble(p -> vec3d.squareDistanceTo(new Vec3d(p)));
            final BlockPos pos = this.blocks.stream().max(comparator).get();
            if (!this.equipSolidBlock(pos)) {
                ChatUtils.error("Found a hole in the tunnel's floor but don't have any blocks to fill it with.");
                TunnellerMod.this.setEnabled(false);
                return;
            }
            if (WBlock.getMaterial(pos).isReplaceable()) {
                BlockUtils.placeBlockSimple(pos);
            }
            else {
                TunnellerMod.WURST.hax.autoToolMod.equipBestTool(pos, false, true, false);
                BlockUtils.breakBlockSimple(pos);
            }
        }
        
        private boolean equipSolidBlock(final BlockPos pos) {
            for (int slot = 0; slot < 9; ++slot) {
                final ItemStack stack = TunnellerMod.MC.player.inventory.getInvStack(slot);
                if (!WItem.isNullOrEmpty(stack)) {
                    if (stack.getItem() instanceof ItemBlock) {
                        final Block block = Block.getBlockFromItem(stack.getItem());
                        if (block.getDefaultState().isFullBlock()) {
                            if (!(block instanceof BlockFalling) || !WBlock.canFallThrough(pos.down())) {
                                TunnellerMod.MC.player.inventory.selectedSlot = slot;
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
    }
    
    private class DodgeLiquidTask extends Task
    {
        private final HashSet<BlockPos> liquids;
        private int disableTimer;
        
        private DodgeLiquidTask() {
            super(null);
            this.liquids = new HashSet<BlockPos>();
            this.disableTimer = 60;
        }
        
        @Override
        public boolean canRun() {
            if (!this.liquids.isEmpty()) {
                return true;
            }
            final BlockPos base = TunnellerMod.this.start.offset(TunnellerMod.this.direction, TunnellerMod.this.length);
            final BlockPos from = TunnellerMod.this.offset(base, TunnellerMod.this.size.getSelected().from);
            final BlockPos to = TunnellerMod.this.offset(base, TunnellerMod.this.size.getSelected().to);
            final int maxY = Math.max(from.getY(), to.getY());
            for (final BlockPos pos : BlockPos.getAllInBox(from, to)) {
                for (int maxOffset = Math.min(TunnellerMod.this.size.getSelected().maxRange, TunnellerMod.this.length), i = 0; i <= maxOffset; ++i) {
                    final BlockPos pos2 = pos.offset(TunnellerMod.this.direction.getOpposite(), i);
                    if (WBlock.getBlock(pos2) instanceof BlockLiquid) {
                        this.liquids.add(pos2);
                    }
                }
                if (WBlock.getState(pos).isFullBlock()) {
                    continue;
                }
                final BlockPos pos3 = pos.offset(TunnellerMod.this.direction);
                if (WBlock.getBlock(pos3) instanceof BlockLiquid) {
                    this.liquids.add(pos3);
                }
                if (pos.getY() != maxY) {
                    continue;
                }
                final BlockPos pos4 = pos.up();
                if (!(WBlock.getBlock(pos4) instanceof BlockLiquid)) {
                    continue;
                }
                this.liquids.add(pos4);
            }
            if (this.liquids.isEmpty()) {
                return false;
            }
            ChatUtils.error("The tunnel is flooded, cannot continue.");
            GL11.glNewList(TunnellerMod.this.displayLists[3], 4864);
            final Box box = new Box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);
            GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
            for (final BlockPos pos5 : this.liquids) {
                GL11.glPushMatrix();
                GL11.glTranslated((double)pos5.getX(), (double)pos5.getY(), (double)pos5.getZ());
                GL11.glBegin(1);
                RenderUtils.drawOutlinedBox(box);
                GL11.glEnd();
                GL11.glPopMatrix();
            }
            GL11.glEndList();
            return true;
        }
        
        @Override
        public void run() {
            final BlockPos player = new BlockPos(TunnellerMod.MC.player);
            final KeyBinding forward = TunnellerMod.MC.gameSettings.keyBindForward;
            final Vec3d diffVec = new Vec3d(player.subtract(TunnellerMod.this.start));
            final Vec3d dirVec = new Vec3d(TunnellerMod.this.direction.getDirectionVec());
            final double dotProduct = diffVec.dotProduct(dirVec);
            final BlockPos pos1 = TunnellerMod.this.start.offset(TunnellerMod.this.direction, (int)dotProduct);
            if (!player.equals(pos1)) {
                RotationUtils.faceVectorForWalking(this.toVec3d(pos1));
                forward.pressed = true;
                return;
            }
            final BlockPos pos2 = TunnellerMod.this.start.offset(TunnellerMod.this.direction, Math.max(0, TunnellerMod.this.length - 10));
            if (!player.equals(pos2)) {
                RotationUtils.faceVectorForWalking(this.toVec3d(pos2));
                forward.pressed = true;
                TunnellerMod.MC.player.setSprinting(true);
                return;
            }
            final BlockPos pos3 = TunnellerMod.this.start.offset(TunnellerMod.this.direction, TunnellerMod.this.length + 1);
            RotationUtils.faceVectorForWalking(this.toVec3d(pos3));
            forward.pressed = false;
            TunnellerMod.MC.player.setSprinting(false);
            if (this.disableTimer > 0) {
                --this.disableTimer;
                return;
            }
            TunnellerMod.this.setEnabled(false);
        }
        
        private Vec3d toVec3d(final BlockPos pos) {
            return new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        }
    }
    
    private class PlaceTorchTask extends Task
    {
        private BlockPos lastTorch;
        private BlockPos nextTorch;
        
        private PlaceTorchTask() {
            super(null);
            this.nextTorch = TunnellerMod.this.start;
        }
        
        @Override
        public boolean canRun() {
            if (!TunnellerMod.this.torches.isChecked()) {
                this.lastTorch = null;
                this.nextTorch = new BlockPos(TunnellerMod.MC.player);
                GL11.glNewList(TunnellerMod.this.displayLists[4], 4864);
                GL11.glEndList();
                return false;
            }
            if (this.lastTorch != null) {
                this.nextTorch = this.lastTorch.offset(TunnellerMod.this.direction, TunnellerMod.this.size.getSelected().torchDistance);
            }
            GL11.glNewList(TunnellerMod.this.displayLists[4], 4864);
            GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
            final Vec3d torchVec = new Vec3d(this.nextTorch).addVector(0.5, 0.0, 0.5);
            RenderUtils.drawArrow(torchVec, torchVec.addVector(0.0, 0.5, 0.0));
            GL11.glEndList();
            final BlockPos base = TunnellerMod.this.start.offset(TunnellerMod.this.direction, TunnellerMod.this.length);
            return TunnellerMod.this.getDistance(TunnellerMod.this.start, base) > TunnellerMod.this.getDistance(TunnellerMod.this.start, this.nextTorch) && Blocks.TORCH.canPlaceBlockAt(WMinecraft.getWorld(), this.nextTorch);
        }
        
        @Override
        public void run() {
            if (!this.equipTorch()) {
                ChatUtils.error("Out of torches.");
                TunnellerMod.this.setEnabled(false);
                return;
            }
            TunnellerMod.MC.gameSettings.keyBindSneak.pressed = true;
            BlockUtils.placeBlockSimple(this.nextTorch);
            if (WBlock.getBlock(this.nextTorch) instanceof BlockTorch) {
                this.lastTorch = this.nextTorch;
            }
        }
        
        private boolean equipTorch() {
            for (int slot = 0; slot < 9; ++slot) {
                final ItemStack stack = TunnellerMod.MC.player.inventory.getInvStack(slot);
                if (!WItem.isNullOrEmpty(stack)) {
                    if (stack.getItem() instanceof ItemBlock) {
                        final Block block = Block.getBlockFromItem(stack.getItem());
                        if (block instanceof BlockTorch) {
                            TunnellerMod.MC.player.inventory.selectedSlot = slot;
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
}
