// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.chat;

import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;
import javax.swing.text.BadLocationException;
import net.wurstclient.hooks.FrameHook;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import java.awt.FlowLayout;
import javax.swing.Box;
import java.util.Map;
import java.util.Iterator;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellEditor;
import javax.swing.JTable;
import net.wurstclient.spam.tag.Tag;
import net.wurstclient.spam.SpamProcessor;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import net.wurstclient.spam.exceptions.UnreadableTagException;
import net.wurstclient.files.ConfigFiles;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Dialog;
import javax.swing.JSeparator;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import net.wurstclient.util.MiscUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.HeadlessException;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import net.wurstclient.files.WurstFolders;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Frame;
import net.wurstclient.Category;
import javax.swing.JTextArea;
import javax.swing.JSpinner;
import javax.swing.JDialog;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.Hack;

@Bypasses
@DontSaveState
public final class SpammerMod extends Hack
{
    private JDialog dialog;
    private static JSpinner delaySpinner;
    private JTextArea spamArea;
    private String spam;
    
    public SpammerMod() {
        super("Spammer", "Automatically spams messages in the chat. It can also run Wurst commands automatically.");
        this.setCategory(Category.CHAT);
    }
    
    @Override
    public void onEnable() {
        new Thread("Spammer") {
            @Override
            public void run() {
                SpammerMod.access$0(SpammerMod.this, new JDialog((Frame)null, SpammerMod.this.getName(), false));
                SpammerMod.this.dialog.setDefaultCloseOperation(2);
                SpammerMod.this.dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(final WindowEvent e) {
                        SpammerMod.WURST.hax.spammerMod.setEnabled(false);
                    }
                });
                final JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, 1));
                final JMenuBar menubar = new JMenuBar();
                final JMenu fileMenu = new JMenu("File");
                final JMenuItem fileLoad = new JMenuItem("Load spam from file");
                fileLoad.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final JFileChooser fileChooser = new JFileChooser(WurstFolders.SPAM.toFile()) {
                            @Override
                            protected JDialog createDialog(final Component parent) throws HeadlessException {
                                final JDialog dialog = super.createDialog(parent);
                                dialog.setAlwaysOnTop(true);
                                return dialog;
                            }
                        };
                        fileChooser.setFileSelectionMode(0);
                        fileChooser.setAcceptAllFileFilterUsed(false);
                        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("All supported files", new String[] { "wspam", "txt" }));
                        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("WSPAM files", new String[] { "wspam" }));
                        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("TXT files", new String[] { "txt" }));
                        final int action = fileChooser.showOpenDialog(SpammerMod.this.dialog);
                        if (action == 0) {
                            try {
                                final File file = fileChooser.getSelectedFile();
                                Throwable t = null;
                                try {
                                    final BufferedReader load = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                                    try {
                                        String newspam = load.readLine();
                                        String line = "";
                                        while ((line = load.readLine()) != null) {
                                            newspam = String.valueOf(newspam) + "\n" + line;
                                        }
                                        SpammerMod.this.spamArea.setText(newspam);
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
                                SpammerMod.this.updateSpam();
                            }
                            catch (final IOException e2) {
                                e2.printStackTrace();
                                MiscUtils.simpleError(e2, fileChooser);
                            }
                        }
                    }
                });
                fileMenu.add(fileLoad);
                final JMenuItem fileSave = new JMenuItem("Save spam to file");
                fileSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final JFileChooser fileChooser = new JFileChooser(WurstFolders.SPAM.toFile()) {
                            @Override
                            protected JDialog createDialog(final Component parent) throws HeadlessException {
                                final JDialog dialog = super.createDialog(parent);
                                dialog.setAlwaysOnTop(true);
                                return dialog;
                            }
                        };
                        fileChooser.setFileSelectionMode(0);
                        fileChooser.setAcceptAllFileFilterUsed(false);
                        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("WSPAM files", new String[] { "wspam" }));
                        final int action = fileChooser.showSaveDialog(SpammerMod.this.dialog);
                        if (action == 0) {
                            try {
                                File file = fileChooser.getSelectedFile();
                                if (!file.getName().endsWith(".wspam")) {
                                    file = new File(String.valueOf(file.getPath()) + ".wspam");
                                }
                                Throwable t = null;
                                try {
                                    final PrintWriter save = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                                    try {
                                        SpammerMod.this.updateSpam();
                                        String[] split;
                                        for (int length = (split = SpammerMod.this.spam.split("\n")).length, i = 0; i < length; ++i) {
                                            final String line = split[i];
                                            save.println(line);
                                        }
                                    }
                                    finally {
                                        if (save != null) {
                                            save.close();
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
                            catch (final IOException e2) {
                                e2.printStackTrace();
                                MiscUtils.simpleError(e2, fileChooser);
                            }
                        }
                    }
                });
                fileMenu.add(fileSave);
                fileMenu.add(new JSeparator());
                final JMenuItem fileOpenFolder = new JMenuItem("Open spam folder");
                fileOpenFolder.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        MiscUtils.openFile(WurstFolders.SPAM);
                    }
                });
                fileMenu.add(fileOpenFolder);
                menubar.add(fileMenu);
                final JMenuItem fileOpenLink = new JMenuItem("Get more spam online");
                fileOpenLink.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        MiscUtils.openLink("https://www.wurstclient.net/downloads/wspam/");
                    }
                });
                fileMenu.add(fileOpenLink);
                menubar.add(fileMenu);
                final JMenu editMenu = new JMenu("Edit");
                final JMenuItem editNewVar = new JMenuItem("New variable");
                editNewVar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final JDialog editDialog = new JDialog(SpammerMod.this.dialog, "New variable");
                        final JPanel mainPanel = new JPanel();
                        mainPanel.setLayout(new BoxLayout(mainPanel, 1));
                        final JPanel namePanel = new JPanel();
                        final JLabel nameLabel = new JLabel("Variable name");
                        namePanel.add(nameLabel);
                        final JTextField nameField = new JTextField(16);
                        namePanel.add(nameField);
                        mainPanel.add(namePanel);
                        final JPanel valuePanel = new JPanel();
                        final JLabel valueLabel = new JLabel("Variable value");
                        valuePanel.add(valueLabel);
                        final JTextField valueField = new JTextField(16);
                        valuePanel.add(valueField);
                        mainPanel.add(valuePanel);
                        final JPanel createPanel = new JPanel();
                        final JButton createButton = new JButton("Create variable");
                        createButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                SpammerMod.this.updateSpam();
                                SpammerMod.this.spamArea.setText("<var " + (nameField.getText().isEmpty() ? "undefined" : nameField.getText()) + ">" + (valueField.getText().isEmpty() ? "undefined" : valueField.getText()) + "</var><!--\n-->" + SpammerMod.this.spam);
                                editDialog.dispose();
                            }
                        });
                        createPanel.add(createButton);
                        mainPanel.add(createPanel);
                        editDialog.setContentPane(mainPanel);
                        editDialog.pack();
                        editDialog.setLocationRelativeTo(SpammerMod.this.dialog);
                        editDialog.setAlwaysOnTop(true);
                        editDialog.setVisible(true);
                    }
                });
                editMenu.add(editNewVar);
                menubar.add(editMenu);
                final JMenu viewMenu = new JMenu("View");
                final JCheckBoxMenuItem viewFont = new JCheckBoxMenuItem("Simulate ingame font", SpammerMod.WURST.options.spamFont);
                viewFont.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SpammerMod.WURST.options.spamFont = !SpammerMod.WURST.options.spamFont;
                        ConfigFiles.OPTIONS.save();
                        SpammerMod.this.updateFont();
                    }
                });
                viewMenu.add(viewFont);
                menubar.add(viewMenu);
                final JMenu helpMenu = new JMenu("Help");
                final JMenuItem helpIntro = new JMenuItem("Introduction to WSPAM");
                helpIntro.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        JOptionPane.showOptionDialog(SpammerMod.this.dialog, new UnreadableTagException("", 0).getHelp(), "Help", -1, 1, null, new String[] { "OK" }, 0);
                    }
                });
                helpMenu.add(helpIntro);
                final JMenuItem helpTaglist = new JMenuItem("Available Tags");
                helpTaglist.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final JDialog helpDialog = new JDialog(SpammerMod.this.dialog, "Available tags");
                        final Object[][] rowData = new Object[SpamProcessor.tagManager.getActiveTags().size()][3];
                        final Iterator itr = SpamProcessor.tagManager.getActiveTags().iterator();
                        int i = 0;
                        while (itr.hasNext()) {
                            final Tag tag = itr.next();
                            rowData[i][0] = tag.getName();
                            rowData[i][1] = tag.getDescription();
                            rowData[i][2] = tag.getSyntax();
                            ++i;
                        }
                        final JTable table = new JTable(rowData, new Object[] { "Name", "Description", "Syntax" });
                        table.setDefaultEditor(Object.class, null);
                        table.setFillsViewportHeight(true);
                        table.setCellSelectionEnabled(true);
                        final JScrollPane tablePane = new JScrollPane(table);
                        helpDialog.setContentPane(tablePane);
                        helpDialog.pack();
                        helpDialog.setLocationRelativeTo(SpammerMod.this.dialog);
                        helpDialog.setAlwaysOnTop(true);
                        helpDialog.setVisible(true);
                    }
                });
                helpMenu.add(helpTaglist);
                final JMenuItem helpVarlist = new JMenuItem("Pre-defined variables");
                helpVarlist.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final JDialog helpDialog = new JDialog(SpammerMod.this.dialog, "Pre-defined variables");
                        final Object[][] rowData = new Object[SpamProcessor.varManager.getSpammerVars().size()][2];
                        final Iterator itr = SpamProcessor.varManager.getSpammerVars().entrySet().iterator();
                        int i = 0;
                        while (itr.hasNext()) {
                            final Map.Entry var = itr.next();
                            rowData[i][0] = "§_" + var.getKey() + ";";
                            rowData[i][1] = "\"" + var.getValue() + "\"";
                            if (var.getValue().equals(" ")) {
                                rowData[i][1] = "\" \" (space)";
                            }
                            else if (var.getValue().equals("\n")) {
                                rowData[i][1] = "\"\" (line break)";
                            }
                            ++i;
                        }
                        final JTable table = new JTable(rowData, new Object[] { "Name", "Value" });
                        table.setDefaultEditor(Object.class, null);
                        table.setFillsViewportHeight(true);
                        table.setCellSelectionEnabled(true);
                        final JScrollPane tablePane = new JScrollPane(table);
                        helpDialog.setContentPane(tablePane);
                        helpDialog.pack();
                        helpDialog.setLocationRelativeTo(SpammerMod.this.dialog);
                        helpDialog.setAlwaysOnTop(true);
                        helpDialog.setVisible(true);
                    }
                });
                helpMenu.add(helpVarlist);
                menubar.add(helpMenu);
                menubar.add(Box.createHorizontalGlue());
                panel.add(menubar);
                final JPanel delayPanel = new JPanel(new FlowLayout(1, 4, 4));
                final JLabel delayLabel = new JLabel("Delay between messages:");
                delayPanel.add(delayLabel);
                SpammerMod.access$7(new JSpinner(new SpinnerNumberModel(SpammerMod.WURST.options.spamDelay, 0, 3600000, 50)));
                SpammerMod.delaySpinner.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(final ChangeEvent e) {
                        SpammerMod.WURST.options.spamDelay = (int)SpammerMod.delaySpinner.getValue();
                        ConfigFiles.OPTIONS.save();
                    }
                });
                SpammerMod.delaySpinner.setEditor(new JSpinner.NumberEditor(SpammerMod.delaySpinner, "#'ms'"));
                delayPanel.add(SpammerMod.delaySpinner);
                panel.add(delayPanel);
                SpammerMod.access$9(SpammerMod.this, new JTextArea());
                SpammerMod.this.spamArea.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void removeUpdate(final DocumentEvent e) {
                        SpammerMod.this.updateSpam();
                    }
                    
                    @Override
                    public void insertUpdate(final DocumentEvent e) {
                        SpammerMod.this.updateSpam();
                    }
                    
                    @Override
                    public void changedUpdate(final DocumentEvent e) {
                        SpammerMod.this.updateSpam();
                    }
                });
                final JScrollPane spamPane = new JScrollPane(SpammerMod.this.spamArea);
                SpammerMod.this.updateFont();
                spamPane.setPreferredSize(new Dimension(500, 200));
                panel.add(spamPane);
                final JButton startButton = new JButton("Spam");
                startButton.setAlignmentX(0.5f);
                startButton.setFont(new Font(startButton.getFont().getFamily(), 1, 18));
                startButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    SpammerMod.this.updateSpam();
                                    SpamProcessor.process(SpammerMod.this.spam, SpammerMod.this, true);
                                    SpammerMod.access$10(SpammerMod.this, SpamProcessor.process(SpammerMod.this.spam, SpammerMod.this, false));
                                    if (SpammerMod.this.spam == null) {
                                        return;
                                    }
                                    for (int i = 0; i < SpammerMod.this.spam.split("\n").length; ++i) {
                                        final String message = SpammerMod.this.spam.split("\n")[i];
                                        SpammerMod.MC.player.sendAutomaticChatMessage(message);
                                        Thread.sleep(SpammerMod.WURST.options.spamDelay);
                                    }
                                }
                                catch (final Exception e) {
                                    System.err.println("Exception in Spammer:");
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
                panel.add(startButton);
                SpammerMod.this.dialog.setContentPane(panel);
                SpammerMod.this.dialog.pack();
                SpammerMod.this.dialog.setLocationRelativeTo(FrameHook.getFrame());
                SpammerMod.this.dialog.setAlwaysOnTop(true);
                SpammerMod.MC.setIngameNotInFocus();
                SpammerMod.this.dialog.setVisible(true);
            }
        }.start();
    }
    
    @Override
    public void onDisable() {
        this.spam = null;
        new Thread() {
            @Override
            public void run() {
                if (SpammerMod.this.dialog != null) {
                    SpammerMod.this.dialog.dispose();
                }
            }
        }.start();
    }
    
    private void updateSpam() {
        try {
            this.spam = this.spamArea.getDocument().getText(0, this.spamArea.getDocument().getLength());
        }
        catch (final BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void updateFont() {
        try {
            Font mcfont = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream("assets/minecraft/font/mcfont.ttf"));
            mcfont = mcfont.deriveFont(12.0f);
            final Font defaultFont = new Font("Monospaced", 0, 14);
            this.spamArea.setFont(SpammerMod.WURST.options.spamFont ? mcfont : defaultFont);
        }
        catch (final Exception e1) {
            e1.printStackTrace();
        }
    }
    
    public static void updateDelaySpinner() {
        if (SpammerMod.delaySpinner != null) {
            SpammerMod.delaySpinner.setValue(SpammerMod.WURST.options.spamDelay);
        }
    }
    
    public JDialog getDialog() {
        return this.dialog;
    }
    
    public void goToLine(final int line) {
        int lineStart = 0;
        int lineEnd = 0;
        int currentLine = 0;
        if (line >= this.spam.split("\n").length) {
            lineStart = this.spam.lastIndexOf("\n") + 1;
            currentLine = line;
        }
        while (currentLine < line) {
            lineStart = this.spam.indexOf("\n", lineStart) + 1;
            ++currentLine;
        }
        if (this.spam.indexOf("\n", lineStart) > -1) {
            lineEnd = this.spam.indexOf("\n", lineStart);
        }
        else {
            lineEnd = this.spam.length();
        }
        this.spamArea.setCaretPosition(lineStart);
        this.spamArea.setSelectionStart(lineStart);
        this.spamArea.setSelectionEnd(lineEnd);
        this.spamArea.requestFocus();
    }
    
    static /* synthetic */ void access$0(final SpammerMod spammerMod, final JDialog dialog) {
        spammerMod.dialog = dialog;
    }
    
    static /* synthetic */ void access$7(final JSpinner delaySpinner) {
        SpammerMod.delaySpinner = delaySpinner;
    }
    
    static /* synthetic */ void access$9(final SpammerMod spammerMod, final JTextArea spamArea) {
        spammerMod.spamArea = spamArea;
    }
    
    static /* synthetic */ void access$10(final SpammerMod spammerMod, final String spam) {
        spammerMod.spam = spam;
    }
}
