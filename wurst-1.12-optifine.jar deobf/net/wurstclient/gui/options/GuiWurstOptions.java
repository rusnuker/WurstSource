// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui.options;

import java.util.Iterator;
import java.util.Arrays;
import net.wurstclient.options.OptionsManager;
import net.wurstclient.util.MiscUtils;
import net.wurstclient.gui.options.zoom.GuiZoomManager;
import net.wurstclient.keybinds.KeybindManagerScreen;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.commands.FriendsCmd;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.client.gui.GuiButton;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.GuiScreen;

public class GuiWurstOptions extends GuiScreen
{
    private GuiScreen prevScreen;
    private String[] toolTips;
    private boolean autoMaximize;
    
    public GuiWurstOptions(final GuiScreen par1GuiScreen) {
        this.toolTips = new String[] { "", "Add/remove friends by clicking them with\nthe middle mouse button.", "Automatically maximizes the Minecraft window.\nNot compatible with OptiFine.\nNot compatible with Mac OS.", "Sends anonymous usage statistics that\nhelp us improve the Wurst Client.", "Downloads and installs Wurst updates automatically.", "", "Keybinds allow you to toggle any mod\nor command by simply pressing a\nbutton.", "Manager for the blocks\nthat X-Ray will show.", "The Zoom Manager allows you to\nchange the zoom key, how far it\nwill zoom in and more.", "", "", "WurstClient.net", "@Wurst_Imperium", "r/WurstClient", "paypal.me/WurstImperium", "" };
        this.autoMaximize = WurstClient.INSTANCE.files.loadAutoMaximize();
        this.prevScreen = par1GuiScreen;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        final FriendsCmd friendsCmd = WurstClient.INSTANCE.commands.friendsCmd;
        final CheckboxSetting middleClickFriends = friendsCmd.getMiddleClickFriends();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 144 - 16, 200, 20, "Back"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 154, this.height / 4 + 24 - 16, 100, 20, "Click Friends: " + (middleClickFriends.isChecked() ? "ON" : "OFF")));
        final GuiButton autoMaximizeButton;
        this.buttonList.add(autoMaximizeButton = new GuiButton(2, this.width / 2 - 154, this.height / 4 + 48 - 16, 100, 20, "AutoMaximize: " + (this.autoMaximize ? "ON" : "OFF")));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 154, this.height / 4 + 72 - 16, 100, 20, "Analytics: " + (WurstClient.INSTANCE.options.google_analytics.enabled ? "ON" : "OFF")));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 154, this.height / 4 + 96 - 16, 100, 20, "Auto Updater: " + (WurstClient.INSTANCE.options.autoUpdater ? "ON" : "OFF")));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 50, this.height / 4 + 24 - 16, 100, 20, "Keybinds"));
        this.buttonList.add(new GuiButton(7, this.width / 2 - 50, this.height / 4 + 48 - 16, 100, 20, "X-Ray Blocks"));
        this.buttonList.add(new GuiButton(8, this.width / 2 - 50, this.height / 4 + 72 - 16, 100, 20, "Zoom"));
        this.buttonList.add(new GuiButton(11, this.width / 2 + 54, this.height / 4 + 24 - 16, 100, 20, "Official Website"));
        this.buttonList.add(new GuiButton(12, this.width / 2 + 54, this.height / 4 + 48 - 16, 100, 20, "Twitter"));
        this.buttonList.add(new GuiButton(13, this.width / 2 + 54, this.height / 4 + 72 - 16, 100, 20, "Subreddit (NEW!)"));
        this.buttonList.add(new GuiButton(14, this.width / 2 + 54, this.height / 4 + 96 - 16, 100, 20, "Donate"));
        final GuiButton guiButton = autoMaximizeButton;
        WMinecraft.isRunningOnMac();
        guiButton.enabled = false;
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) {
        if (!button.enabled) {
            return;
        }
        if (button.id == 0) {
            this.mc.openScreen(this.prevScreen);
        }
        else if (button.id == 1) {
            final FriendsCmd friendsCmd = WurstClient.INSTANCE.commands.friendsCmd;
            final CheckboxSetting middleClickFriends = friendsCmd.getMiddleClickFriends();
            middleClickFriends.setChecked(!middleClickFriends.isChecked());
            button.displayString = "Click Friends: " + (middleClickFriends.isChecked() ? "ON" : "OFF");
            ConfigFiles.OPTIONS.save();
        }
        else if (button.id == 2) {
            this.autoMaximize = !this.autoMaximize;
            button.displayString = "AutoMaximize: " + (this.autoMaximize ? "ON" : "OFF");
            WurstClient.INSTANCE.files.saveAutoMaximize(this.autoMaximize);
        }
        else if (button.id == 3) {
            final OptionsManager.GoogleAnalytics analytics = WurstClient.INSTANCE.options.google_analytics;
            if (analytics.enabled) {
                WurstClient.INSTANCE.analytics.trackEvent("options", "analytics", "disable");
            }
            analytics.enabled = !analytics.enabled;
            if (analytics.enabled) {
                WurstClient.INSTANCE.analytics.trackEvent("options", "analytics", "enable");
            }
            button.displayString = "Analytics: " + (analytics.enabled ? "ON" : "OFF");
            ConfigFiles.OPTIONS.save();
        }
        else if (button.id == 4) {
            WurstClient.INSTANCE.options.autoUpdater = !WurstClient.INSTANCE.options.autoUpdater;
            button.displayString = "Auto Updater: " + (WurstClient.INSTANCE.options.autoUpdater ? "ON" : "OFF");
            ConfigFiles.OPTIONS.save();
        }
        else if (button.id != 5) {
            if (button.id == 6) {
                this.mc.openScreen(new KeybindManagerScreen(this));
            }
            else if (button.id == 7) {
                WurstClient.INSTANCE.hax.xRayMod.showBlockListEditor(this);
            }
            else if (button.id == 8) {
                this.mc.openScreen(new GuiZoomManager(this));
            }
            else if (button.id != 9 && button.id != 10) {
                if (button.id == 11) {
                    MiscUtils.openLink("https://www.wurstclient.net/");
                }
                else if (button.id == 12) {
                    MiscUtils.openLink("https://twitter.com/Wurst_Imperium");
                }
                else if (button.id == 13) {
                    MiscUtils.openLink("https://www.reddit.com/r/WurstClient/");
                }
                else if (button.id == 14) {
                    MiscUtils.openLink("https://www.wurstclient.net/donate/");
                }
                else {
                    final int id = button.id;
                }
            }
        }
    }
    
    @Override
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Wurst Options", this.width / 2, 40, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Settings", this.width / 2 - 104, this.height / 4 + 24 - 28, 13421772);
        this.drawCenteredString(this.fontRendererObj, "Managers", this.width / 2, this.height / 4 + 24 - 28, 13421772);
        this.drawCenteredString(this.fontRendererObj, "Links", this.width / 2 + 104, this.height / 4 + 24 - 28, 13421772);
        super.drawScreen(par1, par2, par3);
        for (final GuiButton button : this.buttonList) {
            if (button.isMouseOver() && !this.toolTips[button.id].isEmpty()) {
                this.drawHoveringText(Arrays.asList(this.toolTips[button.id].split("\n")), par1, par2);
                break;
            }
        }
    }
}
