// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager.screens;

import org.lwjgl.opengl.GL11;
import net.wurstclient.altmanager.AltRenderer;
import net.wurstclient.altmanager.NameGenerator;
import java.io.IOException;
import net.wurstclient.util.MiscUtils;
import java.util.Collection;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import net.wurstclient.hooks.FrameHook;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.HeadlessException;
import javax.swing.JDialog;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.altmanager.Alt;
import net.wurstclient.altmanager.LoginManager;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiButton;
import net.wurstclient.altmanager.AltManager;
import net.minecraft.client.gui.GuiScreen;

public final class AltManagerScreen extends GuiScreen
{
    private final GuiScreen prevScreen;
    private final AltManager altManager;
    private GuiAltList listGui;
    private boolean shouldAsk;
    private int errorTimer;
    private GuiButton useButton;
    private GuiButton starButton;
    private GuiButton editButton;
    private GuiButton deleteButton;
    
    public AltManagerScreen(final GuiScreen prevScreen, final AltManager altManager) {
        this.shouldAsk = true;
        this.prevScreen = prevScreen;
        this.altManager = altManager;
    }
    
    @Override
    public void initGui() {
        (this.listGui = new GuiAltList(this.mc, this, this.altManager.getList())).registerScrollButtons(7, 8);
        this.listGui.elementClicked(-1, false, 0, 0);
        if (this.altManager.getList().isEmpty() && this.shouldAsk) {
            this.mc.openScreen(new GuiYesNo(this, "Your alt list is empty.", "Would you like some random alts to get started?", 0));
        }
        this.buttonList.clear();
        this.buttonList.add(this.useButton = new GuiButton(0, this.width / 2 - 154, this.height - 52, 100, 20, "Use"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 50, this.height - 52, 100, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 54, this.height - 52, 100, 20, "Add"));
        this.buttonList.add(this.starButton = new GuiButton(3, this.width / 2 - 154, this.height - 28, 75, 20, "Star"));
        this.buttonList.add(this.editButton = new GuiButton(4, this.width / 2 - 76, this.height - 28, 74, 20, "Edit"));
        this.buttonList.add(this.deleteButton = new GuiButton(5, this.width / 2 + 2, this.height - 28, 74, 20, "Delete"));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 80, this.height - 28, 75, 20, "Cancel"));
        this.buttonList.add(new GuiButton(7, 8, 8, 100, 20, "Import Alts"));
    }
    
    @Override
    public void updateScreen() {
        final boolean altSelected = this.listGui.getSelectedSlot() >= 0 && this.listGui.getSelectedSlot() < this.altManager.getList().size();
        this.useButton.enabled = altSelected;
        this.starButton.enabled = altSelected;
        this.editButton.enabled = altSelected;
        this.deleteButton.enabled = altSelected;
    }
    
    public void actionPerformed(final GuiButton button) {
        if (!button.enabled) {
            return;
        }
        switch (button.id) {
            case 0: {
                this.pressUse();
                break;
            }
            case 1: {
                this.mc.openScreen(new DirectLoginScreen(this));
                break;
            }
            case 2: {
                this.mc.openScreen(new AddAltScreen(this, this.altManager));
                break;
            }
            case 3: {
                this.pressStar();
                break;
            }
            case 4: {
                this.pressEdit();
                break;
            }
            case 5: {
                this.pressDelete();
                break;
            }
            case 6: {
                this.mc.openScreen(this.prevScreen);
                break;
            }
            case 7: {
                this.pressImportAlts();
                break;
            }
        }
    }
    
    private void pressUse() {
        final Alt alt = this.listGui.getSelectedAlt();
        if (alt.isCracked()) {
            LoginManager.changeCrackedName(alt.getEmail());
            this.mc.openScreen(this.prevScreen);
            return;
        }
        final String error = LoginManager.login(alt.getEmail(), alt.getPassword());
        if (!error.isEmpty()) {
            this.errorTimer = 8;
            return;
        }
        this.altManager.setChecked(this.listGui.getSelectedSlot(), this.mc.getSession().getUsername());
        this.mc.openScreen(this.prevScreen);
    }
    
    private void pressStar() {
        final Alt alt = this.listGui.getSelectedAlt();
        this.altManager.setStarred(this.listGui.getSelectedSlot(), !alt.isStarred());
        this.listGui.setSelectedSlot(-1);
    }
    
    private void pressEdit() {
        final Alt alt = this.listGui.getSelectedAlt();
        this.mc.openScreen(new EditAltScreen(this, this.altManager, alt));
    }
    
    private void pressDelete() {
        final String text = "Are you sure you want to remove this alt?";
        final String altName = this.listGui.getSelectedAlt().getNameOrEmail();
        final String message = "\"" + altName + "\" will be lost forever! (A long time!)";
        this.mc.openScreen(new GuiYesNo(this, text, message, "Delete", "Cancel", 1));
    }
    
    private void pressImportAlts() {
        new Thread(() -> {
            final JFileChooser fileChooser = new JFileChooser(WurstFolders.MAIN.toFile()) {
                @Override
                protected JDialog createDialog(final Component parent) throws HeadlessException {
                    final JDialog dialog = super.createDialog(parent);
                    dialog.setAlwaysOnTop(true);
                    return dialog;
                }
            };
            fileChooser.setFileSelectionMode(0);
            fileChooser.setAcceptAllFileFilterUsed(false);
            new FileNameExtensionFilter("TXT file (username:password)", new String[] { "txt" });
            final FileNameExtensionFilter filter;
            final Object o;
            ((JFileChooser)o).addChoosableFileFilter(filter);
            if (fileChooser.showOpenDialog(FrameHook.getFrame()) == 0) {
                try {
                    final File file = fileChooser.getSelectedFile();
                    final ArrayList<Alt> alts = new ArrayList<Alt>();
                    try {
                        new BufferedReader(new FileReader(file));
                        final BufferedReader bufferedReader;
                        final BufferedReader load = bufferedReader;
                        try {
                            final String line = "";
                            Label_0146_1: {
                                break Label_0146_1;
                                String s;
                                do {
                                    final String line2;
                                    final String[] data = line2.split(":");
                                    if (data.length == 2) {
                                        alts.add(new Alt(data[0], data[1], null));
                                    }
                                    s = (line2 = load.readLine());
                                } while (s != null);
                            }
                        }
                        finally {
                            if (load != null) {
                                load.close();
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
                    this.altManager.addAll(alts);
                }
                catch (final IOException e) {
                    e.printStackTrace();
                    MiscUtils.simpleError(e, fileChooser);
                }
            }
        }).start();
    }
    
    @Override
    public void confirmClicked(final boolean confirmed, final int id) {
        if (id == 0) {
            if (confirmed) {
                final ArrayList<Alt> alts = new ArrayList<Alt>();
                for (int i = 0; i < 8; ++i) {
                    alts.add(new Alt(NameGenerator.generateName(), null, null));
                }
                this.altManager.addAll(alts);
            }
            this.shouldAsk = false;
        }
        else if (id == 1 && confirmed) {
            this.altManager.remove(this.listGui.getSelectedSlot());
        }
        this.mc.openScreen(this);
    }
    
    @Override
    protected void keyTyped(final char par1, final int par2) {
        if (par2 == 28 || par2 == 156) {
            this.actionPerformed(this.buttonList.get(0));
        }
    }
    
    @Override
    protected void mouseClicked(final int par1, final int par2, final int par3) throws IOException {
        if (par2 >= 36 && par2 <= this.height - 57 && (par1 >= this.width / 2 + 140 || par1 <= this.width / 2 - 126)) {
            this.listGui.elementClicked(-1, false, 0, 0);
        }
        super.mouseClicked(par1, par2, par3);
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.listGui.handleMouseInput();
    }
    
    @Override
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawDefaultBackground();
        this.listGui.drawScreen(par1, par2, par3);
        if (this.listGui.getSelectedSlot() != -1 && this.listGui.getSelectedSlot() < this.altManager.getList().size()) {
            final Alt alt = this.listGui.getSelectedAlt();
            AltRenderer.drawAltBack(alt.getNameOrEmail(), (this.width / 2 - 125) / 2 - 32, this.height / 2 - 64 - 9, 64, 128);
            AltRenderer.drawAltBody(alt.getNameOrEmail(), this.width - (this.width / 2 - 140) / 2 - 32, this.height / 2 - 64 - 9, 64, 128);
        }
        this.drawCenteredString(this.fontRendererObj, "Alt Manager", this.width / 2, 4, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Alts: " + this.altManager.getList().size(), this.width / 2, 14, 10526880);
        this.drawCenteredString(this.fontRendererObj, "premium: " + this.altManager.getNumPremium() + ", cracked: " + this.altManager.getNumCracked(), this.width / 2, 24, 10526880);
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
