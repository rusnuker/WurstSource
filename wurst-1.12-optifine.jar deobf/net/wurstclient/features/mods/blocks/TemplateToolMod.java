// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.files.WurstFolders;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import net.wurstclient.compatibility.WMath;
import java.util.ArrayList;
import net.wurstclient.WurstClient;
import java.io.IOException;
import net.wurstclient.util.ChatUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.TextComponentString;
import java.io.Writer;
import java.io.FileWriter;
import java.io.PrintWriter;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import net.wurstclient.util.json.JsonUtils;
import com.google.gson.JsonArray;
import net.wurstclient.compatibility.WMinecraft;
import com.google.gson.JsonObject;
import net.wurstclient.font.Fonts;
import net.minecraft.client.gui.ScaledResolution;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import java.util.Iterator;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.wurstclient.compatibility.WBlock;
import org.lwjgl.input.Keyboard;
import net.wurstclient.features.Feature;
import net.wurstclient.Category;
import java.io.File;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses
public final class TemplateToolMod extends Hack implements UpdateListener, RenderListener, GUIRenderListener
{
    private Step step;
    private BlockPos posLookingAt;
    private Area area;
    private Template template;
    private File file;
    
    public TemplateToolMod() {
        super("TemplateTool", "Allows you to create custom templates for AutoBuild by scanning existing buildings.");
        this.setCategory(Category.BLOCKS);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { TemplateToolMod.WURST.hax.autoBuildMod };
    }
    
    @Override
    public void onEnable() {
        if (TemplateToolMod.WURST.hax.bowAimbotMod.isEnabled()) {
            TemplateToolMod.WURST.hax.bowAimbotMod.setEnabled(false);
        }
        this.step = Step.START_POS;
        TemplateToolMod.EVENTS.add(UpdateListener.class, this);
        TemplateToolMod.EVENTS.add(RenderListener.class, this);
        TemplateToolMod.EVENTS.add(GUIRenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        TemplateToolMod.EVENTS.remove(UpdateListener.class, this);
        TemplateToolMod.EVENTS.remove(RenderListener.class, this);
        TemplateToolMod.EVENTS.remove(GUIRenderListener.class, this);
        Step[] values;
        for (int length = (values = Step.values()).length, i = 0; i < length; ++i) {
            final Step step = values[i];
            Step.access$2(step, null);
        }
        this.posLookingAt = null;
        this.area = null;
        this.template = null;
        this.file = null;
    }
    
    @Override
    public void onUpdate() {
        if (this.step.selectPos) {
            if (this.step.pos != null && Keyboard.isKeyDown(28)) {
                this.step = Step.values()[this.step.ordinal() + 1];
                if (!this.step.selectPos) {
                    this.posLookingAt = null;
                }
                return;
            }
            if (TemplateToolMod.MC.objectMouseOver != null && TemplateToolMod.MC.objectMouseOver.getBlockPos() != null) {
                this.posLookingAt = TemplateToolMod.MC.objectMouseOver.getBlockPos();
                if (TemplateToolMod.MC.gameSettings.keyBindSneak.pressed) {
                    this.posLookingAt = this.posLookingAt.offset(TemplateToolMod.MC.objectMouseOver.sideHit);
                }
            }
            else {
                this.posLookingAt = null;
            }
            if (this.posLookingAt != null && TemplateToolMod.MC.gameSettings.keyBindUseItem.pressed) {
                Step.access$2(this.step, this.posLookingAt);
            }
        }
        else if (this.step == Step.SCAN_AREA) {
            if (this.area == null) {
                this.area = new Area(Step.START_POS.pos, Step.END_POS.pos, null);
                Step.access$2(Step.START_POS, null);
                Step.access$2(Step.END_POS, null);
            }
            for (int i = 0; i < this.area.scanSpeed && this.area.iterator.hasNext(); ++i) {
                final Area area = this.area;
                Area.access$4(area, area.scannedBlocks + 1);
                final BlockPos pos = this.area.iterator.next();
                if (!WBlock.getMaterial(pos).isReplaceable()) {
                    this.area.blocksFound.add(pos);
                }
            }
            Area.access$7(this.area, this.area.scannedBlocks / (float)this.area.totalBlocks);
            if (!this.area.iterator.hasNext()) {
                this.step = Step.values()[this.step.ordinal() + 1];
            }
        }
        else if (this.step == Step.CREATE_TEMPLATE) {
            if (this.template == null) {
                this.template = new Template(Step.FIRST_BLOCK.pos, this.area.blocksFound.size());
            }
            if (!this.area.blocksFound.isEmpty()) {
                for (int min = Math.max(0, this.area.blocksFound.size() - this.template.scanSpeed), j = this.area.blocksFound.size() - 1; j >= min; --j) {
                    final BlockPos pos2 = this.area.blocksFound.get(j);
                    this.template.remainingBlocks.add(pos2);
                    this.area.blocksFound.remove(j);
                }
                Template.access$3(this.template, this.template.remainingBlocks.size() / (float)this.template.totalBlocks);
                return;
            }
            if (this.template.sortedBlocks.isEmpty() && !this.template.remainingBlocks.isEmpty()) {
                final BlockPos first = this.template.remainingBlocks.first();
                this.template.sortedBlocks.add(first);
                this.template.remainingBlocks.remove(first);
                Template.access$5(this.template, first);
            }
            for (int i = 0; i < this.template.scanSpeed && !this.template.remainingBlocks.isEmpty(); ++i) {
                BlockPos current = this.template.remainingBlocks.first();
                double dCurrent = Double.MAX_VALUE;
                for (final BlockPos pos3 : this.template.remainingBlocks) {
                    final double dPos = this.template.lastAddedBlock.distanceSq(pos3);
                    if (dPos >= dCurrent) {
                        continue;
                    }
                    EnumFacing[] values;
                    for (int length = (values = EnumFacing.values()).length, k = 0; k < length; ++k) {
                        final EnumFacing facing = values[k];
                        final BlockPos next = pos3.offset(facing);
                        if (this.template.sortedBlocks.contains(next)) {
                            current = pos3;
                            dCurrent = dPos;
                        }
                    }
                }
                this.template.sortedBlocks.add(current);
                this.template.remainingBlocks.remove(current);
                Template.access$5(this.template, current);
            }
            Template.access$3(this.template, this.template.remainingBlocks.size() / (float)this.template.totalBlocks);
            if (this.template.sortedBlocks.size() == this.template.totalBlocks) {
                this.step = Step.values()[this.step.ordinal() + 1];
                TemplateToolMod.MC.openScreen(new GuiChooseName(null));
            }
        }
    }
    
    @Override
    public void onRender(final float partialTicks) {
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
        GL11.glTranslated(-TemplateToolMod.MC.getRenderManager().renderPosX, -TemplateToolMod.MC.getRenderManager().renderPosY, -TemplateToolMod.MC.getRenderManager().renderPosZ);
        if (this.area != null) {
            GL11.glEnable(2929);
            if (this.step == Step.SCAN_AREA && this.area.progress < 1.0f) {
                for (int i = Math.max(0, this.area.blocksFound.size() - this.area.scanSpeed); i < this.area.blocksFound.size(); ++i) {
                    final BlockPos pos = this.area.blocksFound.get(i);
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
            else if (this.template != null && this.template.progress > 0.0f) {
                GL11.glPushMatrix();
                GL11.glTranslated(0.0, 0.0, (double)this.template.progress);
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
        if (this.template != null) {
            for (final BlockPos pos2 : this.template.sortedBlocks) {
                GL11.glPushMatrix();
                GL11.glTranslated((double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ());
                GL11.glTranslated(offset, offset, offset);
                GL11.glScaled(scale, scale, scale);
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
                RenderUtils.drawOutlinedBox();
                GL11.glPopMatrix();
            }
        }
        Step[] access$5;
        for (int length = (access$5 = Step.SELECT_POSITION_STEPS).length, j = 0; j < length; ++j) {
            final Step step = access$5[j];
            final BlockPos pos3 = step.pos;
            if (pos3 != null) {
                GL11.glPushMatrix();
                GL11.glTranslated((double)pos3.getX(), (double)pos3.getY(), (double)pos3.getZ());
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
        else if (this.step == Step.FILE_NAME && this.file != null && this.file.exists()) {
            message = "WARNING: This file already exists.";
        }
        else {
            message = this.step.message;
        }
        final ScaledResolution sr = new ScaledResolution(TemplateToolMod.MC);
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
    
    private void saveFile() {
        this.step = Step.values()[this.step.ordinal() + 1];
        new Thread(() -> {
            final JsonObject json = new JsonObject();
            final EnumFacing front = EnumFacing.getHorizontal(4 - WMinecraft.getPlayer().getHorizontalFacing().getHorizontalIndex());
            final EnumFacing left = front.rotateYCCW();
            final JsonArray jsonBlocks = new JsonArray();
            this.template.sortedBlocks.iterator();
            final Iterator iterator;
            while (iterator.hasNext()) {
                final BlockPos pos = iterator.next();
                final BlockPos pos2 = pos.subtract(Step.FIRST_BLOCK.pos);
                final BlockPos pos3 = new BlockPos(0, pos2.getY(), 0).offset(front, pos2.getZ()).offset(left, pos2.getX());
                jsonBlocks.add(JsonUtils.GSON.toJsonTree((Object)new int[] { pos3.getX(), pos3.getY(), pos3.getZ() }, (Type)int[].class));
            }
            json.add("blocks", (JsonElement)jsonBlocks);
            try {
                try {
                    new PrintWriter(new FileWriter(this.file));
                    final PrintWriter printWriter;
                    final PrintWriter save = printWriter;
                    try {
                        save.print(JsonUtils.PRETTY_GSON.toJson((JsonElement)json));
                        final TextComponentString message = new TextComponentString("Saved template as ");
                        final TextComponentString link = new TextComponentString(this.file.getName());
                        final ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_FILE, this.file.getParentFile().getAbsolutePath());
                        link.getStyle().setUnderlined(true).setClickEvent(event);
                        message.appendSibling(link);
                        ChatUtils.component(message);
                    }
                    finally {
                        if (save != null) {
                            save.close();
                        }
                    }
                }
                finally {
                    Throwable t = null;
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
                e.printStackTrace();
                ChatUtils.error("File could not be saved.");
            }
            TemplateToolMod.WURST.hax.autoBuildMod.loadTemplates();
            this.setEnabled(false);
        }, "TemplateTool").start();
    }
    
    static /* synthetic */ void access$3(final TemplateToolMod templateToolMod, final File file) {
        templateToolMod.file = file;
    }
    
    private enum Step
    {
        START_POS("START_POS", 0, "Select start position.", true), 
        END_POS("END_POS", 1, "Select end position.", true), 
        SCAN_AREA("SCAN_AREA", 2, "Scanning area...", false), 
        FIRST_BLOCK("FIRST_BLOCK", 3, "Select the first block to be placed by AutoBuild.", true), 
        CREATE_TEMPLATE("CREATE_TEMPLATE", 4, "Creating template...", false), 
        FILE_NAME("FILE_NAME", 5, "Choose a name for this template.", false), 
        SAVE_FILE("SAVE_FILE", 6, "Saving file...", false);
        
        private static final Step[] SELECT_POSITION_STEPS;
        private final String message;
        private boolean selectPos;
        private BlockPos pos;
        
        static {
            SELECT_POSITION_STEPS = new Step[] { Step.START_POS, Step.END_POS, Step.FIRST_BLOCK };
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
        private float progress;
        private final ArrayList<BlockPos> blocksFound;
        
        private Area(final BlockPos start, final BlockPos end) {
            this.blocksFound = new ArrayList<BlockPos>();
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
            this.scanSpeed = WMath.clamp(this.totalBlocks / 30, 1, 1024);
            this.iterator = BlockPos.getAllInBox(start, end).iterator();
        }
        
        static /* synthetic */ void access$4(final Area area, final int scannedBlocks) {
            area.scannedBlocks = scannedBlocks;
        }
        
        static /* synthetic */ void access$7(final Area area, final float progress) {
            area.progress = progress;
        }
    }
    
    private static class Template
    {
        private final int totalBlocks;
        private final int scanSpeed;
        private float progress;
        private final TreeSet<BlockPos> remainingBlocks;
        private final LinkedHashSet<BlockPos> sortedBlocks;
        private BlockPos lastAddedBlock;
        
        public Template(final BlockPos firstBlock, final int blocksFound) {
            this.sortedBlocks = new LinkedHashSet<BlockPos>();
            this.totalBlocks = blocksFound;
            this.scanSpeed = WMath.clamp(blocksFound / 15, 1, 1024);
            this.remainingBlocks = new TreeSet<BlockPos>((o1, o2) -> {
                final int distanceDiff = Double.compare(o1.distanceSq(blockPos), o2.distanceSq(blockPos));
                if (distanceDiff != 0) {
                    return distanceDiff;
                }
                else {
                    return o1.compareTo((Vec3i)o2);
                }
            });
        }
        
        static /* synthetic */ void access$3(final Template template, final float progress) {
            template.progress = progress;
        }
        
        static /* synthetic */ void access$5(final Template template, final BlockPos lastAddedBlock) {
            template.lastAddedBlock = lastAddedBlock;
        }
    }
    
    private static class GuiChooseName extends GuiScreen
    {
        private final GuiTextField nameField;
        private final GuiButton doneButton;
        private final GuiButton cancelButton;
        
        private GuiChooseName() {
            this.nameField = new GuiTextField(0, Fonts.segoe15, 0, 0, 198, 16);
            this.doneButton = new GuiButton(0, 0, 0, 150, 20, "Done");
            this.cancelButton = new GuiButton(1, 0, 0, 100, 15, "Cancel");
        }
        
        @Override
        public void initGui() {
            final int middleX = this.width / 2;
            final int middleY = this.height / 2;
            this.nameField.xPosition = middleX - 99;
            this.nameField.yPosition = middleY + 16;
            this.nameField.setEnableBackgroundDrawing(false);
            this.nameField.setMaxStringLength(32);
            this.nameField.setFocused(true);
            this.nameField.setTextColor(16777215);
            this.doneButton.xPosition = middleX - 75;
            this.doneButton.yPosition = middleY + 38;
            this.buttonList.add(this.doneButton);
            this.cancelButton.xPosition = middleX - 50;
            this.cancelButton.yPosition = middleY + 62;
            this.buttonList.add(this.cancelButton);
        }
        
        @Override
        protected void actionPerformed(final GuiButton button) throws IOException {
            switch (button.id) {
                case 0: {
                    if (this.nameField.getText().isEmpty() || TemplateToolMod.WURST.hax.templateToolMod.file == null) {
                        return;
                    }
                    this.mc.openScreen(null);
                    TemplateToolMod.WURST.hax.templateToolMod.saveFile();
                    break;
                }
                case 1: {
                    this.mc.openScreen(null);
                    TemplateToolMod.WURST.hax.templateToolMod.setEnabled(false);
                    break;
                }
            }
        }
        
        @Override
        public void updateScreen() {
            this.nameField.updateCursorCounter();
            if (!this.nameField.getText().isEmpty()) {
                TemplateToolMod.access$3(TemplateToolMod.WURST.hax.templateToolMod, new File(WurstFolders.AUTOBUILD.toFile(), String.valueOf(this.nameField.getText()) + ".json"));
            }
        }
        
        @Override
        protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
            if (keyCode == 1) {
                this.actionPerformed(this.cancelButton);
                return;
            }
            if (keyCode == 28) {
                this.actionPerformed(this.doneButton);
                return;
            }
            this.nameField.textboxKeyTyped(typedChar, keyCode);
        }
        
        @Override
        public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
            final int middleX = this.width / 2;
            final int middleY = this.height / 2;
            final int x1 = middleX - 100;
            final int y1 = middleY + 15;
            final int x2 = middleX + 100;
            final int y2 = middleY + 26;
            GL11.glDisable(3553);
            GL11.glDisable(2884);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
            GL11.glBegin(7);
            GL11.glVertex2d((double)x1, (double)y1);
            GL11.glVertex2d((double)x2, (double)y1);
            GL11.glVertex2d((double)x2, (double)y2);
            GL11.glVertex2d((double)x1, (double)y2);
            GL11.glEnd();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnable(3553);
            GL11.glEnable(2884);
            this.nameField.drawTextBox();
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
        
        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }
    }
}
