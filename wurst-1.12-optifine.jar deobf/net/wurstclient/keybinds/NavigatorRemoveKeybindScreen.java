// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.keybinds;

import java.util.Iterator;
import java.util.Map;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.FontRenderer;
import net.wurstclient.font.Fonts;
import net.wurstclient.files.ConfigFiles;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import net.wurstclient.WurstClient;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import java.util.TreeMap;
import net.wurstclient.navigator.NavigatorFeatureScreen;
import net.wurstclient.navigator.NavigatorScreen;

public class NavigatorRemoveKeybindScreen extends NavigatorScreen
{
    private NavigatorFeatureScreen parent;
    private TreeMap<String, PossibleKeybind> existingKeybinds;
    private String hoveredKey;
    private String selectedKey;
    private String text;
    private GuiButton removeButton;
    
    public NavigatorRemoveKeybindScreen(final TreeMap<String, PossibleKeybind> existingKeybinds, final NavigatorFeatureScreen parent) {
        this.hoveredKey = "";
        this.selectedKey = "";
        this.text = "Select the keybind you want to remove.";
        this.existingKeybinds = existingKeybinds;
        this.parent = parent;
    }
    
    @Override
    protected void onResize() {
        this.removeButton = new GuiButton(0, this.width / 2 - 151, this.height - 65, 149, 18, "Remove");
        this.removeButton.enabled = !this.selectedKey.isEmpty();
        this.buttonList.add(this.removeButton);
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, this.height - 65, 149, 18, "Cancel"));
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        switch (button.id) {
            case 0: {
                this.remove();
                break;
            }
            case 1: {
                this.mc.openScreen(this.parent);
                break;
            }
        }
    }
    
    private void remove() {
        final String oldCommands = WurstClient.INSTANCE.getKeybinds().getCommands(this.selectedKey);
        if (oldCommands == null) {
            return;
        }
        final ArrayList<String> commandsList = new ArrayList<String>(Arrays.asList(oldCommands.replace(";", "§").replace("§§", ";").split("§")));
        final String command = this.existingKeybinds.get(this.selectedKey).getCommand();
        while (commandsList.contains(command)) {
            commandsList.remove(command);
        }
        if (commandsList.isEmpty()) {
            WurstClient.INSTANCE.getKeybinds().remove(this.selectedKey);
        }
        else {
            final String newCommands = String.join("§", commandsList).replace(";", "§§").replace("§", ";");
            WurstClient.INSTANCE.getKeybinds().add(this.selectedKey, newCommands);
        }
        WurstClient.INSTANCE.navigator.addPreference(this.parent.getFeature().getName());
        ConfigFiles.NAVIGATOR.save();
        this.mc.openScreen(this.parent);
    }
    
    @Override
    protected void onKeyPress(final char typedChar, final int keyCode) {
        if (keyCode == 1) {
            this.mc.openScreen(this.parent);
        }
    }
    
    @Override
    protected void onMouseClick(final int x, final int y, final int button) {
        if (!this.hoveredKey.isEmpty()) {
            this.selectedKey = this.hoveredKey;
            this.removeButton.enabled = true;
        }
    }
    
    @Override
    protected void onMouseDrag(final int x, final int y, final int button, final long timeDragged) {
    }
    
    @Override
    protected void onMouseRelease(final int x, final int y, final int button) {
    }
    
    @Override
    protected void onUpdate() {
        this.setContentHeight(this.existingKeybinds.size() * 24 - 10);
    }
    
    @Override
    protected void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawCenteredString(Fonts.segoe22, "Remove Keybind", this.middleX, 32, 16777215);
        GL11.glDisable(3553);
        final int bgx1 = this.middleX - 154;
        final int bgx2 = this.middleX + 154;
        final int bgy1 = 60;
        final int bgy2 = this.height - 43;
        RenderUtils.scissorBox(bgx1, bgy1, bgx2, bgy2 - (this.buttonList.isEmpty() ? 0 : 24));
        GL11.glEnable(3089);
        this.hoveredKey = "";
        int yi = bgy1 - 12 + this.scroll;
        for (final Map.Entry<String, PossibleKeybind> entry : this.existingKeybinds.entrySet()) {
            final String key = entry.getKey();
            final PossibleKeybind keybind = entry.getValue();
            yi += 24;
            final int x1 = bgx1 + 2;
            final int x2 = bgx2 - 2;
            final int y1 = yi;
            final int y2 = y1 + 20;
            if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) {
                this.hoveredKey = key;
                if (key.equals(this.selectedKey)) {
                    GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.375f);
                }
                else {
                    GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.375f);
                }
            }
            else if (key.equals(this.selectedKey)) {
                GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.25f);
            }
            else {
                GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.25f);
            }
            this.drawBox(x1, y1, x2, y2);
            this.drawString(Fonts.segoe15, String.valueOf(key) + ": " + keybind.getDescription() + "\n" + keybind.getCommand(), x1 + 1, y1 - 1, 16777215);
            GL11.glDisable(3553);
        }
        this.drawString(Fonts.segoe15, this.text, bgx1 + 2, bgy1 + this.scroll, 16777215);
        GL11.glDisable(3089);
        for (final GuiButton button : this.buttonList) {
            final GuiButton element = button;
            final int x3 = button.xPosition;
            final int x4 = x3 + button.getButtonWidth();
            final int y3 = button.yPosition;
            final int y4 = y3 + 18;
            if (!button.enabled) {
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.25f);
            }
            else if (mouseX >= x3 && mouseX <= x4 && mouseY >= y3 && mouseY <= y4) {
                GL11.glColor4f(0.375f, 0.375f, 0.375f, 0.25f);
            }
            else {
                GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.25f);
            }
            GL11.glDisable(3553);
            this.drawBox(x3, y3, x4, y4);
            this.drawCenteredString(Fonts.segoe18, button.displayString, (x3 + x4) / 2, y3 + 2, 16777215);
        }
    }
}
