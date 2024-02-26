// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.ai.PathFinder;
import java.io.BufferedWriter;
import com.google.gson.JsonElement;
import java.nio.file.OpenOption;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.nio.file.DirectoryStream;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryIteratorException;
import java.io.IOException;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import java.io.Reader;
import net.wurstclient.util.json.JsonUtils;
import java.nio.file.Files;
import net.wurstclient.files.WurstFolders;
import java.nio.file.Path;
import net.wurstclient.features.special_features.YesCheatSpf;
import net.wurstclient.features.commands.PathCmd;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import net.wurstclient.compatibility.WPlayer;
import net.minecraft.util.math.Box;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.util.RotationUtils;
import java.util.Iterator;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.material.Material;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.util.math.RayTraceResult;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import java.util.TreeMap;
import net.wurstclient.Category;
import net.wurstclient.ai.PathProcessor;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.events.RightClickListener;
import net.wurstclient.features.Hack;

@SearchTags({ "AutoBridge", "AutoFloor", "AutoNazi", "AutoPenis", "AutoPillar", "AutoWall", "AutoWurst", "auto build" })
@HelpPage("Mods/AutoBuild")
@DontSaveState
public final class AutoBuildMod extends Hack implements RightClickListener, UpdateListener, RenderListener
{
    private final SliderSetting range;
    private final ModeSetting mode;
    private final CheckboxSetting checkLOS;
    private final CheckboxSetting useAi;
    private ModeSetting template;
    private int[][][] templates;
    private int blockIndex;
    private final ArrayList<BlockPos> positions;
    private AutoBuildPathFinder pathFinder;
    private PathProcessor processor;
    private boolean done;
    
    public AutoBuildMod() {
        super("AutoBuild", "Automatically builds the selected template whenever you place a block.\nCustom templates can be created by using TemplateTool.");
        this.range = new SliderSetting("Range", "How far to reach when placing blocks.\nRecommended values:\n6.0 for vanilla\n4.25 for NoCheat+", 6.0, 1.0, 10.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.mode = new ModeSetting("Mode", "§lFast§r mode can place multiple blocks at once.\n§lLegit§r mode can bypass NoCheat+.", new String[] { "Fast", "Legit" }, 0);
        this.checkLOS = new CheckboxSetting("Check line of sight", "Makes sure that you don't reach through walls\nwhen placing blocks. Can help with AntiCheat\nplugins but slows down building.", false);
        this.useAi = new CheckboxSetting("Use AI (experimental)", false) {
            @Override
            public void update() {
                if (!this.isChecked()) {
                    AutoBuildMod.access$0(AutoBuildMod.this, null);
                    AutoBuildMod.access$1(AutoBuildMod.this, null);
                    PathProcessor.releaseControls();
                }
            }
        };
        this.positions = new ArrayList<BlockPos>();
        this.setCategory(Category.BLOCKS);
        this.loadTemplates();
    }
    
    @Override
    public String getRenderName() {
        String name = String.valueOf(this.getName()) + " [" + this.template.getSelectedMode() + "]";
        if (this.blockIndex > 0) {
            name = String.valueOf(name) + " " + (int)(this.blockIndex / (float)this.positions.size() * 100.0f) + "%";
        }
        return name;
    }
    
    public void setTemplates(final TreeMap<String, int[][]> templates) {
        this.getSettings().clear();
        this.templates = templates.values().toArray(new int[templates.size()][][]);
        int selected;
        if (this.template != null && this.template.getSelected() < templates.size()) {
            selected = this.template.getSelected();
        }
        else {
            selected = 0;
        }
        this.addSetting(this.template = new ModeSetting("Template", templates.keySet().toArray(new String[templates.size()]), selected));
        this.addSetting(this.range);
        this.addSetting(this.checkLOS);
        this.addSetting(this.mode);
        this.addSetting(this.useAi);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { AutoBuildMod.WURST.hax.templateToolMod, AutoBuildMod.WURST.hax.buildRandomMod, AutoBuildMod.WURST.hax.fastPlaceMod };
    }
    
    @Override
    public void onEnable() {
        AutoBuildMod.EVENTS.add(RightClickListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoBuildMod.EVENTS.remove(RightClickListener.class, this);
        AutoBuildMod.EVENTS.remove(UpdateListener.class, this);
        AutoBuildMod.EVENTS.remove(RenderListener.class, this);
        this.blockIndex = 0;
        this.pathFinder = null;
        this.processor = null;
        this.done = false;
        PathProcessor.releaseControls();
    }
    
    @Override
    public void onRightClick(final RightClickEvent event) {
        if (AutoBuildMod.MC.objectMouseOver == null || AutoBuildMod.MC.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || AutoBuildMod.MC.objectMouseOver.getBlockPos() == null || WBlock.getMaterial(AutoBuildMod.MC.objectMouseOver.getBlockPos()) == Material.AIR) {
            return;
        }
        final BlockPos startPos = AutoBuildMod.MC.objectMouseOver.getBlockPos().offset(AutoBuildMod.MC.objectMouseOver.sideHit);
        final EnumFacing front = AutoBuildMod.MC.player.getHorizontalFacing();
        final EnumFacing left = front.rotateYCCW();
        this.positions.clear();
        int[][] array;
        for (int length = (array = this.templates[this.template.getSelected()]).length, i = 0; i < length; ++i) {
            final int[] pos = array[i];
            this.positions.add(startPos.up(pos[1]).offset(front, pos[2]).offset(left, pos[0]));
        }
        if (this.mode.getSelected() == 0 && this.positions.size() <= 64) {
            for (final BlockPos pos2 : this.positions) {
                if (WBlock.getMaterial(pos2).isReplaceable()) {
                    this.placeBlockSimple_old(pos2);
                }
            }
        }
        else {
            AutoBuildMod.EVENTS.add(UpdateListener.class, this);
            AutoBuildMod.EVENTS.add(RenderListener.class, this);
            AutoBuildMod.EVENTS.remove(RightClickListener.class, this);
        }
    }
    
    private boolean placeBlockSimple_old(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double rangeSq = Math.pow(this.range.getValue(), 2.0);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final BlockPos neighbor = pos.offset(side);
            if (WBlock.canBeClicked(neighbor)) {
                final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= rangeSq) {
                    if (!this.checkLOS.isChecked() || WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                        WPlayerController.processRightClickBlock(neighbor, side.getOpposite(), hitVec);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void onUpdate() {
        if (AutoBuildMod.WURST.hax.timerMod.isActive() && AutoBuildMod.WURST.hax.timerMod.getTimerSpeed() > 1.0f) {
            this.blockIndex = 0;
        }
        BlockPos pos;
        for (pos = this.positions.get(this.blockIndex); !WBlock.getMaterial(pos).isReplaceable(); pos = this.positions.get(this.blockIndex)) {
            ++this.blockIndex;
            if (this.blockIndex == this.positions.size()) {
                AutoBuildMod.EVENTS.remove(UpdateListener.class, this);
                AutoBuildMod.EVENTS.remove(RenderListener.class, this);
                AutoBuildMod.EVENTS.add(RightClickListener.class, this);
                this.blockIndex = 0;
                if (this.pathFinder != null) {
                    this.pathFinder = null;
                    this.processor = null;
                    this.done = false;
                    PathProcessor.releaseControls();
                }
                return;
            }
        }
        if (this.useAi.isChecked()) {
            final Vec3d eyesPos = RotationUtils.getEyesPos();
            if (AutoBuildMod.MC.player.boundingBox.intersectsWith(new Box(pos)) || eyesPos.squareDistanceTo(new Vec3d(pos).addVector(0.5, 0.5, 0.5)) > 9.0) {
                if (this.pathFinder != null && (this.done || !this.pathFinder.getGoal().equals(pos))) {
                    this.pathFinder = null;
                    this.processor = null;
                    this.done = false;
                    PathProcessor.releaseControls();
                }
                if (this.pathFinder == null) {
                    (this.pathFinder = new AutoBuildPathFinder(pos)).setThinkTime(10);
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
                if (this.processor != null && !this.pathFinder.isPathStillValid(0)) {
                    this.pathFinder = new AutoBuildPathFinder(this.pathFinder);
                    return;
                }
                this.processor.process();
                if (this.processor.isDone() || this.processor.getTicksOffPath() >= 40) {
                    this.done = true;
                }
            }
            else if (this.pathFinder != null) {
                this.pathFinder = null;
                this.processor = null;
                this.done = false;
                PathProcessor.releaseControls();
            }
        }
        if (this.mode.getSelected() == 0) {
            for (int i = this.blockIndex; i < this.positions.size(); ++i) {
                if (i >= this.blockIndex + 64) {
                    break;
                }
                pos = this.positions.get(i);
                if (WBlock.getMaterial(pos).isReplaceable()) {
                    if (AutoBuildMod.MC.player.boundingBox.intersectsWith(new Box(pos))) {
                        break;
                    }
                    this.placeBlockSimple_old(pos);
                }
            }
        }
        else if (this.mode.getSelected() == 1) {
            if (AutoBuildMod.MC.rightClickDelayTimer > 0) {
                return;
            }
            this.placeBlockLegit(pos);
        }
    }
    
    private boolean placeBlockLegit(final BlockPos pos) {
        final Vec3d eyesPos = RotationUtils.getEyesPos();
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        final double rangeSq = Math.pow(this.range.getValue(), 2.0);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final BlockPos neighbor = pos.offset(side);
            if (WBlock.canBeClicked(neighbor)) {
                final Vec3d dirVec = new Vec3d(side.getDirectionVec());
                final Vec3d hitVec = posVec.add(dirVec.scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= rangeSq) {
                    if (distanceSqPosVec <= eyesPos.squareDistanceTo(posVec.add(dirVec))) {
                        if (!this.checkLOS.isChecked() || WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                            RotationUtils.faceVectorPacketInstant(hitVec);
                            WPlayerController.processRightClickBlock(neighbor, side.getOpposite(), hitVec);
                            WPlayer.swingArmClient();
                            AutoBuildMod.MC.rightClickDelayTimer = 4;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (this.useAi.isChecked() && this.pathFinder != null) {
            final PathCmd pathCmd = AutoBuildMod.WURST.commands.pathCmd;
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
        GL11.glPushMatrix();
        GL11.glTranslated(-AutoBuildMod.MC.getRenderManager().renderPosX, -AutoBuildMod.MC.getRenderManager().renderPosY, -AutoBuildMod.MC.getRenderManager().renderPosZ);
        final int greenBoxes = (this.mode.getSelected() == 0) ? 64 : 1;
        for (int i = this.blockIndex; i < this.positions.size() && i < this.blockIndex + greenBoxes; ++i) {
            final BlockPos pos = this.positions.get(i);
            if (WBlock.getMaterial(pos).isReplaceable()) {
                GL11.glPushMatrix();
                GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                GL11.glTranslated(offset, offset, offset);
                GL11.glScaled(scale, scale, scale);
                GL11.glDepthMask(false);
                GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.15f);
                RenderUtils.drawSolidBox();
                GL11.glDepthMask(true);
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
                RenderUtils.drawOutlinedBox();
                GL11.glPopMatrix();
            }
        }
        for (int i = this.blockIndex + greenBoxes; i < this.positions.size() && i < this.blockIndex + 1024; ++i) {
            final BlockPos pos = this.positions.get(i);
            GL11.glPushMatrix();
            GL11.glTranslated((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
            GL11.glTranslated(offset, offset, offset);
            GL11.glScaled(scale, scale, scale);
            RenderUtils.drawOutlinedBox();
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        if (profile.ordinal() >= YesCheatSpf.Profile.ANTICHEAT.ordinal()) {
            this.mode.lock(1);
            this.range.setUsableMax(4.25);
            this.checkLOS.lock(() -> true);
        }
        else {
            this.mode.unlock();
            this.range.resetUsableMax();
            this.checkLOS.unlock();
        }
    }
    
    public void loadTemplates() {
        final TreeMap<String, int[][]> templates = new TreeMap<String, int[][]>();
        final ArrayList<Path> oldTemplates = new ArrayList<Path>();
        try {
            Throwable t = null;
            try {
                final DirectoryStream<Path> stream = Files.newDirectoryStream(WurstFolders.AUTOBUILD, "*.json");
                try {
                    for (final Path path : stream) {
                        try {
                            Throwable t2 = null;
                            try {
                                final BufferedReader reader = Files.newBufferedReader(path);
                                try {
                                    final JsonObject json = JsonUtils.JSON_PARSER.parse((Reader)reader).getAsJsonObject();
                                    final int[][] blocks = (int[][])JsonUtils.GSON.fromJson(json.get("blocks"), (Class)int[][].class);
                                    if (blocks[0].length == 4) {
                                        oldTemplates.add(path);
                                        continue;
                                    }
                                    String name = path.getFileName().toString();
                                    name = name.substring(0, name.lastIndexOf(".json"));
                                    templates.put(name, blocks);
                                    continue;
                                }
                                finally {
                                    if (reader != null) {
                                        reader.close();
                                    }
                                }
                            }
                            finally {
                                if (t2 == null) {
                                    final Throwable exception;
                                    t2 = exception;
                                }
                                else {
                                    final Throwable exception;
                                    if (t2 != exception) {
                                        t2.addSuppressed(exception);
                                    }
                                }
                            }
                        }
                        catch (final Exception e) {
                            System.err.println("Failed to load template: " + path.getFileName());
                            e.printStackTrace();
                        }
                    }
                }
                finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
            finally {
                if (t == null) {
                    final Throwable exception2;
                    t = exception2;
                }
                else {
                    final Throwable exception2;
                    if (t != exception2) {
                        t.addSuppressed(exception2);
                    }
                }
            }
        }
        catch (final IOException | DirectoryIteratorException e2) {
            throw new ReportedException(CrashReport.makeCrashReport(e2, "Loading AutoBuild templates"));
        }
        for (final Path path2 : oldTemplates) {
            try {
                final Path newPath = path2.resolveSibling(path2.getFileName() + "_old");
                Files.move(path2, newPath, new CopyOption[0]);
            }
            catch (final IOException e3) {
                e3.printStackTrace();
            }
        }
        if (templates.isEmpty() || !oldTemplates.isEmpty()) {
            this.createDefaultTemplates();
            this.loadTemplates();
            return;
        }
        this.setTemplates(templates);
    }
    
    private void createDefaultTemplates() {
        DefaultTemplates[] values;
        for (int length = (values = DefaultTemplates.values()).length, i = 0; i < length; ++i) {
            final DefaultTemplates template = values[i];
            final JsonObject json = new JsonObject();
            json.add("blocks", JsonUtils.GSON.toJsonTree((Object)template.data));
            final Path path = WurstFolders.AUTOBUILD.resolve(String.valueOf(template.name) + ".json");
            try {
                Throwable t = null;
                try {
                    final BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);
                    try {
                        JsonUtils.PRETTY_GSON.toJson((JsonElement)json, (Appendable)writer);
                    }
                    finally {
                        if (writer != null) {
                            writer.close();
                        }
                    }
                }
                finally {
                    if (t == null) {
                        final Throwable exception;
                        t = exception;
                    }
                    else {
                        final Throwable exception;
                        if (t != exception) {
                            t.addSuppressed(exception);
                        }
                    }
                }
            }
            catch (final IOException e) {
                System.out.println("Failed to save " + path.getFileName());
                e.printStackTrace();
            }
        }
    }
    
    static /* synthetic */ void access$0(final AutoBuildMod autoBuildMod, final AutoBuildPathFinder pathFinder) {
        autoBuildMod.pathFinder = pathFinder;
    }
    
    static /* synthetic */ void access$1(final AutoBuildMod autoBuildMod, final PathProcessor processor) {
        autoBuildMod.processor = processor;
    }
    
    private enum DefaultTemplates
    {
        BRIDGE("BRIDGE", 0, "Bridge", new int[][] { new int[3], { 1, 0, 0 }, { 1, 0, -1 }, { 0, 0, -1 }, { -1, 0, -1 }, { -1, 0, 0 }, { -1, 0, -2 }, { 0, 0, -2 }, { 1, 0, -2 }, { 1, 0, -3 }, { 0, 0, -3 }, { -1, 0, -3 }, { -1, 0, -4 }, { 0, 0, -4 }, { 1, 0, -4 }, { 1, 0, -5 }, { 0, 0, -5 }, { -1, 0, -5 } }), 
        FLOOR("FLOOR", 1, "Floor", new int[][] { new int[3], { 0, 0, 1 }, { 1, 0, 1 }, { 1, 0, 0 }, { 1, 0, -1 }, { 0, 0, -1 }, { -1, 0, -1 }, { -1, 0, 0 }, { -1, 0, 1 }, { -1, 0, 2 }, { 0, 0, 2 }, { 1, 0, 2 }, { 2, 0, 2 }, { 2, 0, 1 }, { 2, 0, 0 }, { 2, 0, -1 }, { 2, 0, -2 }, { 1, 0, -2 }, { 0, 0, -2 }, { -1, 0, -2 }, { -2, 0, -2 }, { -2, 0, -1 }, { -2, 0, 0 }, { -2, 0, 1 }, { -2, 0, 2 }, { -2, 0, 3 }, { -1, 0, 3 }, { 0, 0, 3 }, { 1, 0, 3 }, { 2, 0, 3 }, { 3, 0, 3 }, { 3, 0, 2 }, { 3, 0, 1 }, { 3, 0, 0 }, { 3, 0, -1 }, { 3, 0, -2 }, { 3, 0, -3 }, { 2, 0, -3 }, { 1, 0, -3 }, { 0, 0, -3 }, { -1, 0, -3 }, { -2, 0, -3 }, { -3, 0, -3 }, { -3, 0, -2 }, { -3, 0, -1 }, { -3, 0, 0 }, { -3, 0, 1 }, { -3, 0, 2 }, { -3, 0, 3 } }), 
        PENIS("PENIS", 2, "Penis", new int[][] { new int[3], { 0, 0, 1 }, { 1, 0, 1 }, { 1, 0, 0 }, { 1, 1, 0 }, { 0, 1, 0 }, { 0, 1, 1 }, { 1, 1, 1 }, { 1, 2, 1 }, { 0, 2, 1 }, { 0, 2, 0 }, { 1, 2, 0 }, { 1, 3, 0 }, { 0, 3, 0 }, { 0, 3, 1 }, { 1, 3, 1 }, { 1, 4, 1 }, { 0, 4, 1 }, { 0, 4, 0 }, { 1, 4, 0 }, { 1, 5, 0 }, { 0, 5, 0 }, { 0, 5, 1 }, { 1, 5, 1 }, { 1, 6, 1 }, { 0, 6, 1 }, { 0, 6, 0 }, { 1, 6, 0 }, { 1, 7, 0 }, { 0, 7, 0 }, { 0, 7, 1 }, { 1, 7, 1 }, { -1, 0, -1 }, { -1, 1, -1 }, { -2, 1, -1 }, { -2, 0, -1 }, { -2, 0, -2 }, { -1, 0, -2 }, { -1, 1, -2 }, { -2, 1, -2 }, { 2, 0, -1 }, { 2, 1, -1 }, { 2, 1, -2 }, { 2, 0, -2 }, { 3, 0, -2 }, { 3, 0, -1 }, { 3, 1, -1 }, { 3, 1, -2 } }), 
        PILLAR("PILLAR", 3, "Pillar", new int[][] { new int[3], { 0, 1, 0 }, { 0, 2, 0 }, { 0, 3, 0 }, { 0, 4, 0 }, { 0, 5, 0 }, { 0, 6, 0 } }), 
        SWASTIKA("SWASTIKA", 4, "Swastika", new int[][] { new int[3], { 1, 0, 0 }, { 2, 0, 0 }, { 0, 1, 0 }, { 0, 2, 0 }, { 1, 2, 0 }, { 2, 2, 0 }, { 2, 3, 0 }, { 2, 4, 0 }, { 0, 3, 0 }, { 0, 4, 0 }, { -1, 4, 0 }, { -2, 4, 0 }, { -1, 2, 0 }, { -2, 2, 0 }, { -2, 1, 0 }, { -2, 0, 0 } }), 
        TREE("TREE", 5, "Tree", new int[][] { new int[3], { 0, 1, 0 }, { 0, 2, 0 }, { 0, 3, 0 }, { 0, 4, 0 }, { 0, 3, -1 }, { 0, 3, 1 }, { -1, 3, 0 }, { 1, 3, 0 }, { 0, 5, 0 }, { 0, 4, -1 }, { 0, 4, 1 }, { -1, 4, 0 }, { 1, 4, 0 }, { 0, 3, -2 }, { -1, 3, -1 }, { 1, 3, -1 }, { 0, 3, 2 }, { -1, 3, 1 }, { 1, 3, 1 }, { -2, 3, 0 }, { 2, 3, 0 }, { 0, 6, 0 }, { 0, 5, -1 }, { 0, 5, 1 }, { -1, 5, 0 }, { 1, 5, 0 }, { 0, 4, -2 }, { -1, 4, -1 }, { 1, 4, -1 }, { 0, 4, 2 }, { -1, 4, 1 }, { 1, 4, 1 }, { -2, 4, 0 }, { 2, 4, 0 }, { -1, 3, -2 }, { 1, 3, -2 }, { -2, 3, -1 }, { 2, 3, -1 }, { -1, 3, 2 }, { 1, 3, 2 }, { -2, 3, 1 }, { 2, 3, 1 }, { 0, 6, -1 }, { 0, 6, 1 }, { -1, 6, 0 }, { 1, 6, 0 }, { 1, 5, 1 }, { -1, 4, -2 }, { 1, 4, -2 }, { -2, 4, -1 }, { 2, 4, -1 }, { -1, 4, 2 }, { 1, 4, 2 }, { -2, 4, 1 }, { 2, 4, 1 }, { -2, 3, -2 }, { 2, 3, -2 }, { 2, 3, 2 }, { 2, 4, -2 } }), 
        WALL("WALL", 6, "Wall", new int[][] { new int[3], { 1, 0, 0 }, { 1, 1, 0 }, { 0, 1, 0 }, { -1, 1, 0 }, { -1, 0, 0 }, { -2, 0, 0 }, { -2, 1, 0 }, { -2, 2, 0 }, { -1, 2, 0 }, { 0, 2, 0 }, { 1, 2, 0 }, { 2, 2, 0 }, { 2, 1, 0 }, { 2, 0, 0 }, { 3, 0, 0 }, { 3, 1, 0 }, { 3, 2, 0 }, { 3, 3, 0 }, { 2, 3, 0 }, { 1, 3, 0 }, { 0, 3, 0 }, { -1, 3, 0 }, { -2, 3, 0 }, { -3, 3, 0 }, { -3, 2, 0 }, { -3, 1, 0 }, { -3, 0, 0 }, { -3, 4, 0 }, { -2, 4, 0 }, { -1, 4, 0 }, { 0, 4, 0 }, { 1, 4, 0 }, { 2, 4, 0 }, { 3, 4, 0 }, { 3, 5, 0 }, { 2, 5, 0 }, { 1, 5, 0 }, { 0, 5, 0 }, { -1, 5, 0 }, { -2, 5, 0 }, { -3, 5, 0 }, { -3, 6, 0 }, { -2, 6, 0 }, { -1, 6, 0 }, { 0, 6, 0 }, { 1, 6, 0 }, { 2, 6, 0 }, { 3, 6, 0 } }), 
        WURST("WURST", 7, "Wurst", new int[][] { new int[3], { 1, 0, 0 }, { 1, 1, 0 }, { 0, 1, 0 }, { 0, 1, 1 }, { 1, 1, 1 }, { 2, 1, 1 }, { 2, 1, 0 }, { 2, 0, 0 }, { 2, 1, -1 }, { 1, 1, -1 }, { 0, 1, -1 }, { -1, 1, -1 }, { -1, 1, 0 }, { -1, 0, 0 }, { -2, 0, 0 }, { -2, 1, 0 }, { -2, 1, 1 }, { -1, 1, 1 }, { -1, 2, 0 }, { 0, 2, 0 }, { 1, 2, 0 }, { 2, 2, 0 }, { 3, 1, 0 }, { -2, 1, -1 }, { -2, 2, 0 }, { -3, 1, 0 } });
        
        private final String name;
        private final int[][] data;
        
        private DefaultTemplates(final String name2, final int ordinal, final String name, final int[][] data) {
            this.name = name;
            this.data = data;
        }
    }
    
    private class AutoBuildPathFinder extends PathFinder
    {
        public AutoBuildPathFinder(final BlockPos goal) {
            super(goal);
        }
        
        public AutoBuildPathFinder(final AutoBuildPathFinder pathFinder) {
            super(pathFinder);
        }
        
        @Override
        protected boolean checkDone() {
            final BlockPos goal = this.getGoal();
            return this.done = (goal.down(2).equals(this.current) || goal.up().equals(this.current) || goal.north().equals(this.current) || goal.south().equals(this.current) || goal.west().equals(this.current) || goal.east().equals(this.current) || goal.down().north().equals(this.current) || goal.down().south().equals(this.current) || goal.down().west().equals(this.current) || goal.down().east().equals(this.current));
        }
    }
}
