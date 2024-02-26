// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui.main;

import net.wurstclient.compatibility.WMath;
import net.minecraft.client.Minecraft;
import java.util.Calendar;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import com.google.gson.JsonObject;
import net.wurstclient.files.ConfigFiles;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.GuiYesNo;
import java.io.Reader;
import java.io.InputStreamReader;
import net.wurstclient.util.json.JsonUtils;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import net.wurstclient.util.MiscUtils;
import net.wurstclient.update.Version;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.altmanager.screens.AltManagerScreen;
import net.wurstclient.WurstClient;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiMainMenu;

public class GuiWurstMainMenu extends GuiMainMenu
{
    private static final ResourceLocation title;
    private static final ResourceLocation santaHat;
    private static boolean startupMessageDisabled;
    private String noticeText;
    private String noticeLink;
    private int noticeWidth2;
    private int noticeWidth1;
    private int noticeX1;
    private int noticeY1;
    private int noticeX2;
    private int noticeY2;
    
    static {
        title = new ResourceLocation("wurst/wurst_380.png");
        santaHat = new ResourceLocation("wurst/santa_hat.png");
        GuiWurstMainMenu.startupMessageDisabled = false;
    }
    
    public GuiWurstMainMenu() {
        this.noticeText = "";
        this.noticeLink = "https://www.wurstclient.net/download/minecraft-1-11-x/";
    }
    
    @Override
    public void initGui() {
        super.initGui();
        for (final GuiButton button : this.buttonList) {
            button.yPosition = Math.min(button.yPosition, this.height - 56);
        }
        this.noticeWidth1 = this.fontRendererObj.getStringWidth(this.noticeText);
        this.noticeWidth2 = this.fontRendererObj.getStringWidth(GuiMainMenu.MORE_INFO_TEXT);
        final int noticeWidth = Math.max(this.noticeWidth1, this.noticeWidth2);
        this.noticeX1 = (this.width - noticeWidth) / 2;
        this.noticeY1 = this.buttonList.get(0).yPosition - 24;
        this.noticeX2 = this.noticeX1 + noticeWidth;
        this.noticeY2 = this.noticeY1 + 24;
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 3: {
                this.mc.openScreen(new AltManagerScreen(this, WurstClient.INSTANCE.getAltManager()));
                break;
            }
        }
    }
    
    @Override
    public void confirmClicked(final boolean result, final int id) {
        super.confirmClicked(result, id);
        if (id == 64) {
            if (result) {
                WurstClient.INSTANCE.analytics.trackEvent("changelog", "go play");
            }
            else {
                MiscUtils.openLink(new Version("6.35.3").getChangelogLink());
                WurstClient.INSTANCE.analytics.trackEvent("changelog", "view changelog");
            }
            this.mc.openScreen(this);
        }
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        if (GuiWurstMainMenu.startupMessageDisabled) {
            return;
        }
        if (WurstClient.INSTANCE.updater.isOutdated() && WurstClient.INSTANCE.options.autoUpdater) {
            WurstClient.INSTANCE.analytics.trackEvent("updater", "update to v" + WurstClient.INSTANCE.updater.getLatestVersion(), "from 6.35.3");
            WurstClient.INSTANCE.updater.update();
            GuiWurstMainMenu.startupMessageDisabled = true;
        }
        if (GuiWurstMainMenu.startupMessageDisabled) {
            return;
        }
        try {
            final HttpsURLConnection connection = (HttpsURLConnection)new URL("https://www.wurstclient.net/api/v1/messages.json").openConnection();
            connection.connect();
            final JsonObject json = JsonUtils.JSON_PARSER.parse((Reader)new InputStreamReader(connection.getInputStream(), "UTF-8")).getAsJsonObject();
            if (json.get("6.35.3") != null) {
                System.out.println("Emergency message found!");
                this.mc.openScreen(new GuiMessage(json.get("6.35.3").getAsJsonObject()));
                GuiWurstMainMenu.startupMessageDisabled = true;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        if (GuiWurstMainMenu.startupMessageDisabled) {
            return;
        }
        if (!"6.35.3".equals(WurstClient.INSTANCE.options.lastLaunchedVersion)) {
            this.mc.openScreen(new GuiYesNo(this, "Successfully updated to Wurst v6.35.3", "", "Go Play", "View Changelog", 64));
            WurstClient.INSTANCE.options.lastLaunchedVersion = "6.35.3";
            ConfigFiles.OPTIONS.save();
        }
        GuiWurstMainMenu.startupMessageDisabled = true;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (!WurstClient.INSTANCE.isEnabled()) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            return;
        }
        GlStateManager.disableAlpha();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        this.mc.getTextureManager().bindTexture(GuiWurstMainMenu.title);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        int x = this.width / 2 - 128;
        int y = 36;
        int w = 256;
        int h = 64;
        float fw = 256.0f;
        float fh = 64.0f;
        float u = 0.0f;
        float v = 0.0f;
        if (GuiMainMenu.splashText.equals("umop-apisdn!")) {
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef((float)(-this.width), (float)(-h - 60), 0.0f);
        }
        Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
        if (Calendar.getInstance().get(2) == 11) {
            this.mc.getTextureManager().bindTexture(GuiWurstMainMenu.santaHat);
            x += 112;
            y -= 36;
            h = 48;
            w = 48;
            fw = 48.0f;
            fh = 48.0f;
            u = 0.0f;
            v = 0.0f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
        }
        if (GuiMainMenu.splashText.equals("umop-apisdn!")) {
            GL11.glRotatef(-180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef((float)(-this.width), (float)(-h - 60), 0.0f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.width / 2 + 90), 72.0f, 0.0f);
        GlStateManager.rotate(-20.0f, 0.0f, 0.0f, 1.0f);
        float splashScale = 1.8f - Math.abs(WMath.sin(Minecraft.getSystemTime() % 1000L / 1000.0f * 3.1415927f * 2.0f) * 0.1f);
        splashScale = splashScale * 100.0f / (this.fontRendererObj.getStringWidth(GuiWurstMainMenu.splashText) + 32);
        GlStateManager.scale(splashScale, splashScale, splashScale);
        this.drawCenteredString(this.fontRendererObj, GuiWurstMainMenu.splashText, 0, 0, -256);
        GlStateManager.popMatrix();
        final String vMinecraft = "Minecraft 1.12.2";
        final String cMinecraft1 = "Copyright Mojang AB";
        final String cMinecraft2 = "Do not distribute!";
        this.drawString(this.fontRendererObj, vMinecraft, this.width - this.fontRendererObj.getStringWidth(vMinecraft) - 8, 8, 16777215);
        this.drawString(this.fontRendererObj, cMinecraft1, this.width - this.fontRendererObj.getStringWidth(cMinecraft1) - 8, 18, 16777215);
        this.drawString(this.fontRendererObj, cMinecraft2, this.width - this.fontRendererObj.getStringWidth(cMinecraft2) - 8, 28, 16777215);
        this.drawString(this.fontRendererObj, "Wurst Client 6.35.3" + (WurstClient.INSTANCE.updater.isOutdated() ? " (outdated)" : ""), 8, 8, 16777215);
        this.drawString(this.fontRendererObj, "Copyright Alexander01998", 8, 18, 16777215);
        this.drawString(this.fontRendererObj, "All rights reserved.", 8, 28, 16777215);
        this.drawCenteredString(this.fontRendererObj, "§nwww.WurstClient.net", this.width / 2, this.height - 26, 16777215);
        for (final Object button : this.buttonList) {
            ((GuiButton)button).drawButton(this.mc, mouseX, mouseY);
        }
        if (this.noticeText != null && this.noticeText.length() > 0) {
            Gui.drawRect(this.noticeX1 - 2, this.noticeY1 - 2, this.noticeX2 + 2, this.noticeY2 - 1, 1428160512);
            this.drawString(this.fontRendererObj, this.noticeText, this.noticeX1, this.noticeY1, -1);
            this.drawString(this.fontRendererObj, GuiMainMenu.MORE_INFO_TEXT, (this.width - this.noticeWidth2) / 2, this.buttonList.get(0).yPosition - 12, -1);
        }
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final int linkWidth = this.fontRendererObj.getStringWidth("§nwww.WurstClient.net");
        if (mouseButton == 0 && mouseY >= this.height - 26 && mouseY < this.height - 16 && mouseX > this.width / 2 - linkWidth / 2 && mouseX < this.width / 2 + linkWidth / 2) {
            MiscUtils.openLink("https://www.wurstclient.net/");
        }
        if (this.noticeText.length() > 0 && mouseX >= this.noticeX1 && mouseX <= this.noticeX2 && mouseY >= this.noticeY1 && mouseY <= this.noticeY2) {
            MiscUtils.openLink(this.noticeLink);
        }
    }
}
