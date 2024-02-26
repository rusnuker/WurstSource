// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui.main;

import java.io.IOException;
import net.wurstclient.util.MiscUtils;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.GuiYesNo;
import java.util.Iterator;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.GuiButton;
import com.google.gson.JsonElement;
import java.util.Map;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiScreen;

public class GuiMessage extends GuiScreen
{
    private String title;
    private String body;
    private JsonObject buttons;
    private String cancel;
    
    public GuiMessage(final JsonObject json) {
        this.title = json.get("title").getAsString();
        this.body = json.get("body").getAsString();
        this.buttons = json.get("buttons").getAsJsonObject();
        this.cancel = json.get("cancel").getAsString();
    }
    
    @Override
    public void initGui() {
        int i = 0;
        for (final Map.Entry<String, JsonElement> entry : this.buttons.entrySet()) {
            this.buttonList.add(new GuiButton(i, this.width / 2 - 100, this.height / 3 * 2 + i * 24, 200, 20, entry.getKey()));
            ++i;
        }
        if (this.cancel.equals("allowed") || this.cancel.equals("prompt")) {
            this.buttonList.add(new GuiButton(i, this.width / 2 - 50, this.height / 3 * 2 + i * 24, 100, 20, "Cancel"));
        }
        WurstClient.INSTANCE.analytics.trackPageView("/message/v6.35.3", this.title);
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == this.buttons.entrySet().size()) {
            if (this.cancel.equals("allowed")) {
                this.mc.openScreen(new GuiWurstMainMenu());
            }
            else if (this.cancel.equals("prompt")) {
                this.mc.openScreen(new GuiYesNo(this, "Are you sure you want to cancel?", "", 0));
            }
        }
        else {
            MiscUtils.openLink(this.buttons.get(button.displayString).getAsString());
            WurstClient.INSTANCE.analytics.trackEvent("message", "click", "v6.35.3", button.id);
        }
    }
    
    @Override
    public void confirmClicked(final boolean result, final int id) {
        super.confirmClicked(result, id);
        if (result) {
            this.mc.openScreen(new GuiWurstMainMenu());
            WurstClient.INSTANCE.analytics.trackEvent("message", "cancel", "v6.35.3");
        }
        else {
            this.mc.openScreen(this);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, this.height / 4, -1);
        int i = 0;
        String[] split;
        for (int length = (split = this.body.split("\n")).length, j = 0; j < length; ++j) {
            final String line = split[j];
            this.drawCenteredString(this.fontRendererObj, line, this.width / 2, this.height / 4 + 16 + i * 12, -1);
            ++i;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
