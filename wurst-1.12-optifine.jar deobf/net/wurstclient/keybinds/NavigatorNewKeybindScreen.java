// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.keybinds;

import java.util.Iterator;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.FontRenderer;
import net.wurstclient.font.Fonts;
import org.lwjgl.input.Keyboard;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.GuiButton;
import net.wurstclient.navigator.NavigatorFeatureScreen;
import java.util.ArrayList;
import net.wurstclient.navigator.NavigatorScreen;

public class NavigatorNewKeybindScreen extends NavigatorScreen
{
    private ArrayList<PossibleKeybind> possibleKeybinds;
    private NavigatorFeatureScreen parent;
    private int hoveredCommand;
    private int selectedCommand;
    private String selectedKey;
    private String text;
    private GuiButton okButton;
    private boolean choosingKey;
    
    public NavigatorNewKeybindScreen(final ArrayList<PossibleKeybind> possibleKeybinds, final NavigatorFeatureScreen parent) {
        this.hoveredCommand = -1;
        this.selectedCommand = -1;
        this.selectedKey = "NONE";
        this.possibleKeybinds = possibleKeybinds;
        this.parent = parent;
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        switch (button.id) {
            case 0: {
                if (this.choosingKey) {
                    String newCommands = this.possibleKeybinds.get(this.selectedCommand).getCommand();
                    final String oldCommands = WurstClient.INSTANCE.getKeybinds().getCommands(this.selectedKey);
                    if (oldCommands != null) {
                        newCommands = String.valueOf(oldCommands) + " ; " + newCommands;
                    }
                    WurstClient.INSTANCE.getKeybinds().add(this.selectedKey, newCommands);
                    WurstClient.INSTANCE.navigator.addPreference(this.parent.getFeature().getName());
                    ConfigFiles.NAVIGATOR.save();
                    this.mc.openScreen(this.parent);
                    break;
                }
                this.choosingKey = true;
                this.okButton.enabled = false;
                break;
            }
            case 1: {
                this.mc.openScreen(this.parent);
                break;
            }
        }
    }
    
    @Override
    protected void onResize() {
        this.okButton = new GuiButton(0, this.width / 2 - 151, this.height - 65, 149, 18, "OK");
        this.okButton.enabled = (this.selectedCommand != -1);
        this.buttonList.add(this.okButton);
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, this.height - 65, 149, 18, "Cancel"));
    }
    
    @Override
    protected void onKeyPress(final char typedChar, final int keyCode) {
        if (this.choosingKey) {
            this.selectedKey = Keyboard.getKeyName(keyCode);
            this.okButton.enabled = !this.selectedKey.equals("NONE");
        }
        else if (keyCode == 1) {
            this.mc.openScreen(this.parent);
        }
    }
    
    @Override
    protected void onMouseClick(final int x, final int y, final int button) {
        if (this.hoveredCommand != -1) {
            this.selectedCommand = this.hoveredCommand;
            this.okButton.enabled = true;
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
        if (this.choosingKey) {
            this.text = "Now press the key that should trigger this keybind.";
            if (!this.selectedKey.equals("NONE")) {
                this.text = String.valueOf(this.text) + "\n\nKey: " + this.selectedKey;
                String commands = WurstClient.INSTANCE.getKeybinds().getCommands(this.selectedKey);
                if (commands != null) {
                    this.text = String.valueOf(this.text) + "\n\nWARNING: This key is already bound to the following command(s):";
                    commands = commands.replace(";", "§").replace("§§", ";");
                    String[] split;
                    for (int length = (split = commands.split("§")).length, i = 0; i < length; ++i) {
                        final String cmd = split[i];
                        this.text = String.valueOf(this.text) + "\n- " + cmd;
                    }
                }
            }
        }
        else {
            this.text = "Select what this keybind should do.";
        }
        if (this.choosingKey) {
            this.setContentHeight(Fonts.segoe15.getStringHeight(this.text));
        }
        else {
            this.setContentHeight(this.possibleKeybinds.size() * 24 - 10);
        }
    }
    
    @Override
    protected void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawCenteredString(Fonts.segoe22, "New Keybind", this.middleX, 32, 16777215);
        GL11.glDisable(3553);
        final int bgx1 = this.middleX - 154;
        final int bgx2 = this.middleX + 154;
        final int bgy1 = 60;
        final int bgy2 = this.height - 43;
        RenderUtils.scissorBox(bgx1, bgy1, bgx2, bgy2 - (this.buttonList.isEmpty() ? 0 : 24));
        GL11.glEnable(3089);
        if (!this.choosingKey) {
            this.hoveredCommand = -1;
            int yi = bgy1 - 12 + this.scroll;
            for (int i = 0; i < this.possibleKeybinds.size(); ++i) {
                yi += 24;
                final PossibleKeybind possibleKeybind = this.possibleKeybinds.get(i);
                final int x1 = bgx1 + 2;
                final int x2 = bgx2 - 2;
                final int y1 = yi;
                final int y2 = y1 + 20;
                if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2 && mouseY <= bgy2 - 24) {
                    if ((this.hoveredCommand = i) == this.selectedCommand) {
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.375f);
                    }
                    else {
                        GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.375f);
                    }
                }
                else if (i == this.selectedCommand) {
                    GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.25f);
                }
                else {
                    GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.25f);
                }
                this.drawBox(x1, y1, x2, y2);
                this.drawString(Fonts.segoe15, String.valueOf(possibleKeybind.getDescription()) + "\n" + possibleKeybind.getCommand(), x1 + 1, y1 - 1, 16777215);
                GL11.glDisable(3553);
            }
        }
        this.drawString(Fonts.segoe15, this.text, bgx1 + 2, bgy1 + this.scroll, 16777215);
        GL11.glDisable(3089);
        for (final GuiButton button : this.buttonList) {
            final GuiButton element = button;
            final int x1 = button.xPosition;
            final int x2 = x1 + button.getButtonWidth();
            final int y1 = button.yPosition;
            final int y2 = y1 + 18;
            if (!button.enabled) {
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.25f);
            }
            else if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) {
                GL11.glColor4f(0.375f, 0.375f, 0.375f, 0.25f);
            }
            else {
                GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.25f);
            }
            GL11.glDisable(3553);
            this.drawBox(x1, y1, x2, y2);
            this.drawCenteredString(Fonts.segoe18, button.displayString, (x1 + x2) / 2, y1 + 2, 16777215);
        }
    }
}
