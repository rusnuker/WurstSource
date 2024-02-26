// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager.screens;

import org.lwjgl.opengl.GL11;
import net.wurstclient.altmanager.AltRenderer;
import java.io.InputStream;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.CopyOption;
import java.net.URI;
import net.wurstclient.util.MiscUtils;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.altmanager.NameGenerator;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiButton;
import net.wurstclient.altmanager.PasswordField;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiScreen;

public abstract class AltEditorScreen extends GuiScreen
{
    protected final GuiScreen prevScreen;
    private GuiTextField emailBox;
    private PasswordField passwordBox;
    private GuiButton doneButton;
    private GuiButton stealSkinButton;
    protected String message;
    private int errorTimer;
    
    public AltEditorScreen(final GuiScreen prevScreen) {
        this.message = "";
        this.prevScreen = prevScreen;
    }
    
    @Override
    public final void updateScreen() {
        this.emailBox.updateCursorCounter();
        this.passwordBox.updateCursorCounter();
        final String email = this.emailBox.getText().trim();
        final boolean alex = email.equalsIgnoreCase("Alexander01998");
        this.doneButton.enabled = (!email.isEmpty() && (!alex || !this.passwordBox.getText().isEmpty()));
        this.stealSkinButton.enabled = !alex;
    }
    
    protected abstract String getTitle();
    
    protected final String getEmail() {
        return this.emailBox.getText();
    }
    
    protected final String getPassword() {
        return this.passwordBox.getText();
    }
    
    protected String getDefaultEmail() {
        return this.mc.session.getUsername();
    }
    
    protected String getDefaultPassword() {
        return "";
    }
    
    protected abstract String getDoneButtonText();
    
    protected abstract void pressDoneButton();
    
    protected final void doErrorEffect() {
        this.errorTimer = 8;
    }
    
    @Override
    public final void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(this.doneButton = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72 + 12, this.getDoneButtonText()));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, "Cancel"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 96 + 12, "Random Name"));
        this.buttonList.add(this.stealSkinButton = new GuiButton(4, this.width - (this.width / 2 - 100) / 2 - 64, this.height - 32, 128, 20, "Steal Skin"));
        this.buttonList.add(new GuiButton(5, (this.width / 2 - 100) / 2 - 64, this.height - 32, 128, 20, "Open Skin Folder"));
        (this.emailBox = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20)).setMaxStringLength(48);
        this.emailBox.setFocused(true);
        this.emailBox.setText(this.getDefaultEmail());
        (this.passwordBox = new PasswordField(this.fontRendererObj, this.width / 2 - 100, 100, 200, 20)).setFocused(false);
        this.passwordBox.setText(this.getDefaultPassword());
    }
    
    @Override
    public final void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    protected final void actionPerformed(final GuiButton button) {
        if (!button.enabled) {
            return;
        }
        switch (button.id) {
            case 0: {
                this.pressDoneButton();
                break;
            }
            case 1: {
                this.mc.openScreen(this.prevScreen);
                break;
            }
            case 3: {
                this.emailBox.setText(NameGenerator.generateName());
                break;
            }
            case 4: {
                this.message = this.stealSkin(this.getEmail());
                break;
            }
            case 5: {
                MiscUtils.openFile(WurstFolders.SKINS);
                break;
            }
        }
    }
    
    private final String stealSkin(final String name) {
        final String skin = String.valueOf(name) + ".png";
        final URI u = URI.create("http://skins.minecraft.net/MinecraftSkins/").resolve(skin);
        final Path path = WurstFolders.SKINS.resolve(skin);
        try {
            Throwable t = null;
            try {
                final InputStream in = u.toURL().openStream();
                try {
                    Files.copy(in, path, new CopyOption[0]);
                    return "§a§lSaved skin as " + skin;
                }
                finally {
                    if (in != null) {
                        in.close();
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
            e.printStackTrace();
            return "§4§lSkin could not be saved.";
        }
    }
    
    @Override
    protected final void keyTyped(final char par1, final int par2) {
        this.emailBox.textboxKeyTyped(par1, par2);
        this.passwordBox.textboxKeyTyped(par1, par2);
        if (par2 == 28 || par2 == 156) {
            this.actionPerformed(this.doneButton);
        }
    }
    
    @Override
    protected final void mouseClicked(final int par1, final int par2, final int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
        this.emailBox.mouseClicked(par1, par2, par3);
        this.passwordBox.mouseClicked(par1, par2, par3);
        if (this.emailBox.isFocused() || this.passwordBox.isFocused()) {
            this.message = "";
        }
    }
    
    @Override
    public final void drawScreen(final int par1, final int par2, final float par3) {
        this.drawDefaultBackground();
        AltRenderer.drawAltBack(this.emailBox.getText(), (this.width / 2 - 100) / 2 - 64, this.height / 2 - 128, 128, 256);
        AltRenderer.drawAltBody(this.emailBox.getText(), this.width - (this.width / 2 - 100) / 2 - 64, this.height / 2 - 128, 128, 256);
        this.drawCenteredString(this.fontRendererObj, this.getTitle(), this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, "Name or E-Mail", this.width / 2 - 100, 47, 10526880);
        this.drawString(this.fontRendererObj, "Password", this.width / 2 - 100, 87, 10526880);
        this.drawCenteredString(this.fontRendererObj, this.message, this.width / 2, 142, 16777215);
        this.emailBox.drawTextBox();
        this.passwordBox.drawTextBox();
        if (this.errorTimer > 0) {
            GL11.glDisable(3553);
            GL11.glDisable(2884);
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 0.0f, 0.0f, this.errorTimer / 16.0f);
            GL11.glBegin(7);
            GL11.glVertex2d(0.0, 0.0);
            GL11.glVertex2d((double)this.width, 0.0);
            GL11.glVertex2d((double)this.width, (double)this.height);
            GL11.glVertex2d(0.0, (double)this.height);
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glEnable(2884);
            GL11.glDisable(3042);
            --this.errorTimer;
        }
        super.drawScreen(par1, par2, par3);
    }
}
