// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import com.google.common.util.concurrent.Runnables;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Mouse;
import com.google.common.base.Strings;
import net.minecraft.src.CustomPanoramaProperties;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.src.CustomPanorama;
import org.lwjgl.util.glu.Project;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import java.net.URI;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.ISaveFormat;
import net.wurstclient.WurstClient;
import net.minecraft.src.Reflector;
import net.minecraft.realms.RealmsBridge;
import java.util.Date;
import java.util.Calendar;
import net.minecraft.client.settings.GameSettings;
import java.util.List;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GLContext;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import com.google.common.collect.Lists;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.texture.DynamicTexture;
import java.util.Random;
import org.apache.logging.log4j.Logger;

public class GuiMainMenu extends GuiScreen
{
    private static final Logger LOGGER;
    private static final Random RANDOM;
    private final float updateCounter;
    public static String splashText;
    private GuiButton buttonResetDemo;
    private float panoramaTimer;
    private DynamicTexture viewportTexture;
    private final Object threadLock;
    public static final String MORE_INFO_TEXT;
    private int openGLWarning2Width;
    private int openGLWarning1Width;
    private int openGLWarningX1;
    private int openGLWarningY1;
    private int openGLWarningX2;
    private int openGLWarningY2;
    private String openGLWarning1;
    private String openGLWarning2;
    private String openGLWarningLink;
    private static final ResourceLocation SPLASH_TEXTS;
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES;
    private static final ResourceLocation[] TITLE_PANORAMA_PATHS;
    private ResourceLocation backgroundTexture;
    private GuiButton realmsButton;
    private boolean hasCheckedForRealmsNotification;
    private GuiScreen realmsNotification;
    private int field_193978_M;
    private int field_193979_N;
    private GuiButton modButton;
    private GuiScreen modUpdateNotification;
    
    static {
        LOGGER = LogManager.getLogger();
        RANDOM = new Random();
        MORE_INFO_TEXT = "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information.";
        SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
        MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
        TITLE_PANORAMA_PATHS = new ResourceLocation[] { new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png") };
    }
    
    public GuiMainMenu() {
        this.threadLock = new Object();
        this.openGLWarning2 = GuiMainMenu.MORE_INFO_TEXT;
        GuiMainMenu.splashText = "missingno";
        IResource iresource = null;
        Label_0177: {
            try {
                final List<String> list = Lists.newArrayList();
                iresource = Minecraft.getMinecraft().getResourceManager().getResource(GuiMainMenu.SPLASH_TEXTS);
                final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
                String s;
                while ((s = bufferedreader.readLine()) != null) {
                    s = s.trim();
                    if (!s.isEmpty()) {
                        list.add(s);
                    }
                }
                if (!list.isEmpty()) {
                    do {
                        GuiMainMenu.splashText = list.get(GuiMainMenu.RANDOM.nextInt(list.size()));
                    } while (GuiMainMenu.splashText.hashCode() == 125780783);
                }
            }
            catch (final IOException ex) {
                break Label_0177;
            }
            finally {
                IOUtils.closeQuietly((Closeable)iresource);
            }
            IOUtils.closeQuietly((Closeable)iresource);
        }
        this.updateCounter = GuiMainMenu.RANDOM.nextFloat();
        this.openGLWarning1 = "";
        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
            this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }
    
    private boolean areRealmsNotificationsEnabled() {
        return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && this.realmsNotification != null;
    }
    
    @Override
    public void updateScreen() {
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotification.updateScreen();
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
    }
    
    @Override
    public void initGui() {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        this.field_193978_M = this.fontRendererObj.getStringWidth("Copyright Mojang AB. Do not distribute!");
        this.field_193979_N = this.width - this.field_193978_M - 2;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
            GuiMainMenu.splashText = "Merry X-mas!";
        }
        else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
            GuiMainMenu.splashText = "Happy new year!";
        }
        else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
            GuiMainMenu.splashText = "OOoooOOOoooo! Spooky!";
        }
        final int i = 24;
        final int j = this.height / 4 + 48;
        if (this.mc.isDemo()) {
            this.addDemoButtons(j, 24);
        }
        else {
            this.addSingleplayerMultiplayerButtons(j, 24);
        }
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit", new Object[0])));
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12));
        synchronized (this.threadLock) {
            this.openGLWarning1Width = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            this.openGLWarning2Width = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            final int k = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);
            this.openGLWarningX1 = (this.width - k) / 2;
            this.openGLWarningY1 = this.buttonList.get(0).yPosition - 24;
            this.openGLWarningX2 = this.openGLWarningX1 + k;
            this.openGLWarningY2 = this.openGLWarningY1 + 24;
            monitorexit(this.threadLock);
        }
        this.mc.setConnectedToRealms(false);
        if (Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && !this.hasCheckedForRealmsNotification) {
            final RealmsBridge realmsbridge = new RealmsBridge();
            this.realmsNotification = realmsbridge.getNotificationScreen(this);
            this.hasCheckedForRealmsNotification = true;
        }
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotification.setGuiSize(this.width, this.height);
            this.realmsNotification.initGui();
        }
        if (Reflector.NotificationModUpdateScreen_init.exists()) {
            this.modUpdateNotification = (GuiScreen)Reflector.call(Reflector.NotificationModUpdateScreen_init, this, this.modButton);
        }
    }
    
    private void addSingleplayerMultiplayerButtons(final int p_73969_1_, final int p_73969_2_) {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer", new Object[0])));
        if (WurstClient.INSTANCE.isEnabled()) {
            final boolean realms = false;
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, realms ? 98 : 200, 20, "Alt Manager"));
            if (realms) {
                this.realmsButton = this.addButton(new GuiButton(14, this.width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("menu.online", new Object[0])));
            }
        }
        else if (Reflector.GuiModList_Constructor.exists()) {
            this.realmsButton = this.addButton(new GuiButton(14, this.width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("menu.online", new Object[0]).replace("Minecraft", "").trim()));
            this.buttonList.add(this.modButton = new GuiButton(6, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("fml.menu.mods", new Object[0])));
        }
        else {
            this.realmsButton = this.addButton(new GuiButton(14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, I18n.format("menu.online", new Object[0])));
        }
    }
    
    private void addDemoButtons(final int p_73972_1_, final int p_73972_2_) {
        this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo", new Object[0])));
        this.buttonResetDemo = this.addButton(new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo", new Object[0])));
        final ISaveFormat isaveformat = this.mc.getSaveLoader();
        final WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
        if (worldinfo == null) {
            this.buttonResetDemo.enabled = false;
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.openScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (button.id == 5) {
            this.mc.openScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }
        if (button.id == 1) {
            this.mc.openScreen(new GuiWorldSelection(this));
        }
        if (button.id == 2) {
            this.mc.openScreen(new GuiMultiplayer(this));
        }
        if (button.id == 14 && this.realmsButton.visible) {
            this.switchToRealms();
        }
        if (button.id == 4) {
            this.mc.shutdown();
        }
        if (button.id == 6 && Reflector.GuiModList_Constructor.exists()) {
            this.mc.openScreen((GuiScreen)Reflector.newInstance(Reflector.GuiModList_Constructor, this));
        }
        if (button.id == 11) {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", WorldServerDemo.DEMO_WORLD_SETTINGS);
        }
        if (button.id == 12) {
            final ISaveFormat isaveformat = this.mc.getSaveLoader();
            final WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
            if (worldinfo != null) {
                this.mc.openScreen(new GuiYesNo(this, I18n.format("selectWorld.deleteQuestion", new Object[0]), "'" + worldinfo.getWorldName() + "' " + I18n.format("selectWorld.deleteWarning", new Object[0]), I18n.format("selectWorld.deleteButton", new Object[0]), I18n.format("gui.cancel", new Object[0]), 12));
            }
        }
    }
    
    private void switchToRealms() {
        final RealmsBridge realmsbridge = new RealmsBridge();
        realmsbridge.switchToRealms(this);
    }
    
    @Override
    public void confirmClicked(final boolean result, final int id) {
        if (result && id == 12) {
            final ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.openScreen(this);
        }
        else if (id == 12) {
            this.mc.openScreen(this);
        }
        else if (id == 13) {
            if (result) {
                try {
                    final Class<?> oclass = Class.forName("java.awt.Desktop");
                    final Object object = oclass.getMethod("getDesktop", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
                }
                catch (final Throwable throwable1) {
                    GuiMainMenu.LOGGER.error("Couldn't open link", throwable1);
                }
            }
            this.mc.openScreen(this);
        }
    }
    
    private void drawPanorama(final int mouseX, final int mouseY, final float partialTicks) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final int i = 8;
        int j = 64;
        final CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
        if (custompanoramaproperties != null) {
            j = custompanoramaproperties.getBlur1();
        }
        for (int k = 0; k < j; ++k) {
            GlStateManager.pushMatrix();
            final float f = (k % 8 / 8.0f - 0.5f) / 64.0f;
            final float f2 = (k / 8 / 8.0f - 0.5f) / 64.0f;
            final float f3 = 0.0f;
            GlStateManager.translate(f, f2, 0.0f);
            GlStateManager.rotate(MathHelper.sin(this.panoramaTimer / 400.0f) * 25.0f + 20.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(-this.panoramaTimer * 0.1f, 0.0f, 1.0f, 0.0f);
            for (int l = 0; l < 6; ++l) {
                GlStateManager.pushMatrix();
                if (l == 1) {
                    GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (l == 2) {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                }
                if (l == 3) {
                    GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (l == 4) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                }
                if (l == 5) {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
                }
                ResourceLocation[] aresourcelocation = GuiMainMenu.TITLE_PANORAMA_PATHS;
                if (custompanoramaproperties != null) {
                    aresourcelocation = custompanoramaproperties.getPanoramaLocations();
                }
                this.mc.getTextureManager().bindTexture(aresourcelocation[l]);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                final int i2 = 255 / (k + 1);
                final float f4 = 0.0f;
                bufferbuilder.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, i2).endVertex();
                bufferbuilder.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, i2).endVertex();
                bufferbuilder.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, i2).endVertex();
                bufferbuilder.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, i2).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }
        bufferbuilder.setTranslation(0.0, 0.0, 0.0);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }
    
    private void rotateAndBlurSkybox() {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GlStateManager.glTexParameteri(3553, 10241, 9729);
        GlStateManager.glTexParameteri(3553, 10240, 9729);
        GlStateManager.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.colorMask(true, true, true, false);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        final int i = 3;
        int j = 3;
        final CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
        if (custompanoramaproperties != null) {
            j = custompanoramaproperties.getBlur2();
        }
        for (int k = 0; k < j; ++k) {
            final float f = 1.0f / (k + 1);
            final int l = this.width;
            final int i2 = this.height;
            final float f2 = (k - 1) / 256.0f;
            bufferbuilder.pos(l, i2, this.zLevel).tex(0.0f + f2, 1.0).color(1.0f, 1.0f, 1.0f, f).endVertex();
            bufferbuilder.pos(l, 0.0, this.zLevel).tex(1.0f + f2, 1.0).color(1.0f, 1.0f, 1.0f, f).endVertex();
            bufferbuilder.pos(0.0, 0.0, this.zLevel).tex(1.0f + f2, 0.0).color(1.0f, 1.0f, 1.0f, f).endVertex();
            bufferbuilder.pos(0.0, i2, this.zLevel).tex(0.0f + f2, 0.0).color(1.0f, 1.0f, 1.0f, f).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }
    
    protected void renderSkybox(final int mouseX, final int mouseY, final float partialTicks) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(mouseX, mouseY, partialTicks);
        this.rotateAndBlurSkybox();
        int i = 3;
        final CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
        if (custompanoramaproperties != null) {
            i = custompanoramaproperties.getBlur3();
        }
        for (int j = 0; j < i; ++j) {
            this.rotateAndBlurSkybox();
            this.rotateAndBlurSkybox();
        }
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        final float f2 = 120.0f / ((this.width > this.height) ? this.width : this.height);
        final float f3 = this.height * f2 / 256.0f;
        final float f4 = this.width * f2 / 256.0f;
        final int k = this.width;
        final int l = this.height;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0, l, this.zLevel).tex(0.5f - f3, 0.5f + f4).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferbuilder.pos(k, l, this.zLevel).tex(0.5f - f3, 0.5f - f4).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferbuilder.pos(k, 0.0, this.zLevel).tex(0.5f + f3, 0.5f - f4).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferbuilder.pos(0.0, 0.0, this.zLevel).tex(0.5f + f3, 0.5f + f4).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        tessellator.draw();
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.panoramaTimer += partialTicks;
        GlStateManager.disableAlpha();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        final int i = 274;
        final int j = this.width / 2 - 137;
        final int k = 30;
        int l = -2130706433;
        int i2 = 16777215;
        int j2 = 0;
        int k2 = Integer.MIN_VALUE;
        final CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
        if (custompanoramaproperties != null) {
            l = custompanoramaproperties.getOverlay1Top();
            i2 = custompanoramaproperties.getOverlay1Bottom();
            j2 = custompanoramaproperties.getOverlay2Top();
            k2 = custompanoramaproperties.getOverlay2Bottom();
        }
        if (l != 0 || i2 != 0) {
            this.drawGradientRect(0, 0, this.width, this.height, l, i2);
        }
        if (j2 != 0 || k2 != 0) {
            this.drawGradientRect(0, 0, this.width, this.height, j2, k2);
        }
        this.mc.getTextureManager().bindTexture(GuiMainMenu.MINECRAFT_TITLE_TEXTURES);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.updateCounter < 1.0E-4) {
            this.drawTexturedModalRect(j + 0, 30, 0, 0, 99, 44);
            this.drawTexturedModalRect(j + 99, 30, 129, 0, 27, 44);
            this.drawTexturedModalRect(j + 99 + 26, 30, 126, 0, 3, 44);
            this.drawTexturedModalRect(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
        }
        else {
            this.drawTexturedModalRect(j + 0, 30, 0, 0, 155, 44);
            this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
        }
        if (Reflector.ForgeHooksClient_renderMainMenu.exists()) {
            GuiMainMenu.splashText = Reflector.callString(Reflector.ForgeHooksClient_renderMainMenu, this, this.fontRendererObj, this.width, this.height, GuiMainMenu.splashText);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.width / 2 + 90), 70.0f, 0.0f);
        GlStateManager.rotate(-20.0f, 0.0f, 0.0f, 1.0f);
        float f = 1.8f - MathHelper.abs(MathHelper.sin(Minecraft.getSystemTime() % 1000L / 1000.0f * 6.2831855f) * 0.1f);
        f = f * 100.0f / (this.fontRendererObj.getStringWidth(GuiMainMenu.splashText) + 32);
        GlStateManager.scale(f, f, f);
        this.drawCenteredString(this.fontRendererObj, GuiMainMenu.splashText, 0, -8, -256);
        GlStateManager.popMatrix();
        String s = "Minecraft 1.12.2";
        if (this.mc.isDemo()) {
            s = String.valueOf(s) + " Demo";
        }
        else {
            s = String.valueOf(s) + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : ("/" + this.mc.getVersionType()));
        }
        if (Reflector.FMLCommonHandler_getBrandings.exists()) {
            final Object object = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);
            final List<String> list = Lists.reverse((List)Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, true));
            for (int l2 = 0; l2 < list.size(); ++l2) {
                final String s2 = list.get(l2);
                if (!Strings.isNullOrEmpty(s2)) {
                    this.drawString(this.fontRendererObj, s2, 2, this.height - (10 + l2 * (this.fontRendererObj.FONT_HEIGHT + 1)), 16777215);
                }
            }
        }
        else {
            this.drawString(this.fontRendererObj, s, 2, this.height - 10, -1);
        }
        this.drawString(this.fontRendererObj, "Copyright Mojang AB. Do not distribute!", this.field_193979_N, this.height - 10, -1);
        if (mouseX > this.field_193979_N && mouseX < this.field_193979_N + this.field_193978_M && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow()) {
            Gui.drawRect(this.field_193979_N, this.height - 1, this.field_193979_N + this.field_193978_M, this.height, -1);
        }
        if (this.openGLWarning1 != null && !this.openGLWarning1.isEmpty()) {
            Gui.drawRect(this.openGLWarningX1 - 2, this.openGLWarningY1 - 2, this.openGLWarningX2 + 2, this.openGLWarningY2 - 1, 1428160512);
            this.drawString(this.fontRendererObj, this.openGLWarning1, this.openGLWarningX1, this.openGLWarningY1, -1);
            this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.openGLWarning2Width) / 2, this.buttonList.get(0).yPosition - 12, -1);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotification.drawScreen(mouseX, mouseY, partialTicks);
        }
        if (this.modUpdateNotification != null) {
            this.modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        synchronized (this.threadLock) {
            if (!this.openGLWarning1.isEmpty() && !StringUtils.isNullOrEmpty(this.openGLWarningLink) && mouseX >= this.openGLWarningX1 && mouseX <= this.openGLWarningX2 && mouseY >= this.openGLWarningY1 && mouseY <= this.openGLWarningY2) {
                final GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.openScreen(guiconfirmopenlink);
            }
            monitorexit(this.threadLock);
        }
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotification.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (mouseX > this.field_193979_N && mouseX < this.field_193979_N + this.field_193978_M && mouseY > this.height - 10 && mouseY < this.height) {
            this.mc.openScreen(new GuiWinGame(false, Runnables.doNothing()));
        }
    }
    
    @Override
    public void onGuiClosed() {
        if (this.realmsNotification != null) {
            this.realmsNotification.onGuiClosed();
        }
    }
}
