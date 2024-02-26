// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam;

import net.wurstclient.spam.exceptions.SpamException;
import java.awt.HeadlessException;
import javax.swing.Icon;
import net.wurstclient.spam.exceptions.InvalidVariableException;
import net.wurstclient.spam.exceptions.UnreadableVariableException;
import net.wurstclient.spam.tag.TagData;
import net.wurstclient.spam.exceptions.UnreadableTagException;
import net.wurstclient.util.MiscUtils;
import net.wurstclient.features.mods.chat.SpammerMod;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import net.wurstclient.compatibility.WMinecraft;
import java.awt.Component;
import javax.swing.JOptionPane;
import net.wurstclient.hooks.FrameHook;
import java.io.StringWriter;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.WurstClient;
import net.wurstclient.spam.tag.TagManager;

public class SpamProcessor
{
    public static TagManager tagManager;
    public static VarManager varManager;
    
    static {
        SpamProcessor.tagManager = new TagManager();
        SpamProcessor.varManager = new VarManager();
    }
    
    public static void runScript(final String filename, final String description) {
        if (!WurstClient.INSTANCE.isEnabled()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final File file = new File(WurstFolders.SCRIPTS.toFile(), String.valueOf(filename) + ".wspam");
                try {
                    final long startTime = System.currentTimeMillis();
                    while (!canSpam()) {
                        Thread.sleep(50L);
                        if (System.currentTimeMillis() > startTime + 10000L) {
                            return;
                        }
                    }
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    if (!file.exists()) {
                        Throwable t = null;
                        try {
                            final PrintWriter save = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                            try {
                                save.println("<!--");
                                String[] split;
                                for (int length = (split = description.split("\n")).length, i = 0; i < length; ++i) {
                                    final String line = split[i];
                                    save.println("  " + line);
                                }
                                save.println("-->");
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
                    runFile(file);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                    final StringWriter tracewriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(tracewriter));
                    final String message = "An error occurred while running " + file.getName() + ":\n" + e.getLocalizedMessage() + "\n" + tracewriter.toString();
                    JOptionPane.showMessageDialog(FrameHook.getFrame(), message, "Error", 0);
                }
            }
        }).start();
    }
    
    public static boolean runSpam(final String filename) {
        final File file = new File(WurstFolders.SPAM.toFile(), String.valueOf(filename) + ".wspam");
        if (!file.exists()) {
            return false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final long startTime = System.currentTimeMillis();
                    while (!canSpam()) {
                        Thread.sleep(50L);
                        if (System.currentTimeMillis() > startTime + 10000L) {
                            return;
                        }
                    }
                    runFile(file);
                }
                catch (final Exception e) {
                    if (e instanceof NullPointerException && WMinecraft.getPlayer() == null) {
                        return;
                    }
                    e.printStackTrace();
                    final StringWriter tracewriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(tracewriter));
                    final String message = "An error occurred while running " + file.getName() + ":\n" + e.getLocalizedMessage() + "\n" + tracewriter.toString();
                    JOptionPane.showMessageDialog(FrameHook.getFrame(), message, "Error", 0);
                }
            }
        }).start();
        return true;
    }
    
    private static void runFile(final File file) throws Exception {
        try {
            Throwable t = null;
            String content;
            try {
                final BufferedReader load = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                try {
                    content = load.readLine();
                    String line = "";
                    while ((line = load.readLine()) != null) {
                        content = String.valueOf(content) + "\n" + line;
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
            final String spam = process(content, null, false);
            if (spam == null || spam.isEmpty()) {
                return;
            }
            for (int i = 0; i < spam.split("\n").length; ++i) {
                WMinecraft.getPlayer().sendAutomaticChatMessage(spam.split("\n")[i]);
                Thread.sleep(WurstClient.INSTANCE.options.spamDelay);
            }
        }
        catch (final NullPointerException e) {
            if (WMinecraft.getPlayer() != null) {
                throw e;
            }
        }
    }
    
    private static boolean canSpam() {
        return WMinecraft.getPlayer() != null && WMinecraft.getWorld() != null;
    }
    
    public static String process(String spam, final SpammerMod spammerMod, final boolean test) {
        try {
            log("### Cleaning up variables...");
            SpamProcessor.varManager.clearUserVars();
            log("### Processing spam...");
            final String source = new String(spam);
            log("### Processing comments...");
            if (test) {
                spam = spam.replace("<!--", "#!--");
            }
            else {
                spam = spam.replaceAll("(?s)<!--.*?-->", "");
            }
            log("** Processed comments:\n" + spam);
            log("### Processing tags...");
            while (spam.contains("<")) {
                log("** Processing tag...");
                final int tagStart = spam.indexOf("<");
                log("TagStart: " + tagStart);
                final int tagLine = MiscUtils.countMatches(spam.substring(0, tagStart), "\n");
                log("TagLine: " + tagLine);
                String tagName = null;
                String tag = spam.substring(tagStart);
                try {
                    tagName = tag.substring(1, tag.indexOf(">")).split(" ")[0];
                }
                catch (final StringIndexOutOfBoundsException e1) {
                    throw new UnreadableTagException(source.substring(tagStart), tagLine);
                }
                log("TagName: " + tagName);
                String[] tagArgs;
                try {
                    tagArgs = tag.substring(tagName.length() + 2, tag.indexOf(">")).split(" ");
                }
                catch (final StringIndexOutOfBoundsException e2) {
                    tagArgs = new String[0];
                }
                log("TagArgs:");
                for (int i = 0; i < tagArgs.length; ++i) {
                    log("No. " + i + ": " + tagArgs[i]);
                }
                String tmpTag = new String(tag);
                int tmpSubTags = 0;
                int tagLength = tag.length();
                boolean tagClosed = false;
                int tagContentLength = tag.length();
                log("+ Calculating TagLength...");
                while (tmpTag.contains("<")) {
                    log("Found subtag: " + tmpTag.substring(tmpTag.indexOf("<"), tmpTag.indexOf("<") + 2) + " at " + tmpTag.indexOf("<"));
                    if (tmpTag.substring(tmpTag.indexOf("<") + 1, tmpTag.indexOf("<") + 2).equals("/")) {
                        --tmpSubTags;
                    }
                    else {
                        ++tmpSubTags;
                    }
                    log("Subtags left: " + tmpSubTags);
                    if (tmpSubTags == 0) {
                        tagLength = tmpTag.indexOf("<") + tagName.length() + 3;
                        tagContentLength = tmpTag.indexOf("<");
                        log("TagContentLength: " + tagContentLength);
                        tmpTag = tmpTag.replaceFirst("<", "#");
                        tagClosed = true;
                        break;
                    }
                    tmpTag = tmpTag.replaceFirst("<", "#");
                }
                log("TagLength: " + tagLength);
                tag = tag.substring(0, tagLength);
                log("Raw Tag:\n" + tag);
                final String tagContent = tag.substring(tag.indexOf(">") + 1, tagContentLength);
                log("TagContent: " + tagContent);
                final TagData tagData = new TagData(tagStart, tagLength, tagLine, tagName, tagArgs, tagClosed, tag, tagContent, tagContentLength, spam);
                final String tagReplacement = SpamProcessor.tagManager.process(tagData);
                if (test) {
                    spam = String.valueOf(spam.substring(0, tagStart)) + (tagClosed ? tag.replaceFirst("<", "#").replaceFirst("(?s)(.*)<", "$1#") : tag.replaceFirst("<", "#")) + spam.substring(tagStart + tagLength, spam.length());
                }
                else {
                    spam = String.valueOf(spam.substring(0, tagStart)) + tagReplacement + spam.substring(tagStart + tagLength, spam.length());
                }
                log("** Processed tag:\n" + spam);
            }
            log("### Processing variables...");
            while (spam.contains("§")) {
                log("** Processing variable...");
                final int varStart = spam.indexOf("§");
                log("VarStart: " + varStart);
                final int varLine = MiscUtils.countMatches(spam.substring(0, varStart), "\n");
                log("VarLine: " + varLine);
                final int varEnd = spam.indexOf(";", varStart) + 1;
                log("VarEnd: " + varEnd);
                String var = spam.substring(varStart);
                try {
                    if (varEnd <= 0) {
                        throw new Exception();
                    }
                    var = spam.substring(varStart, varEnd);
                }
                catch (final Exception e3) {
                    throw new UnreadableVariableException(source.substring(varStart), varLine);
                }
                log("Var: " + var);
                final String varName = spam.substring(varStart + 1, varEnd - 1);
                log("VarName: " + varName);
                log("** Processed variable:\n" + spam);
                final String varReplacement = SpamProcessor.varManager.getValueOfVar(varName);
                if (varReplacement == null) {
                    throw new InvalidVariableException(varName, varLine);
                }
                if (test) {
                    spam = String.valueOf(spam.substring(0, varStart)) + var.replace("§", "*") + spam.substring(varEnd, spam.length());
                }
                else {
                    spam = String.valueOf(spam.substring(0, varStart)) + varReplacement + spam.substring(varEnd, spam.length());
                }
            }
            log("### Final Spam:\n" + spam);
        }
        catch (final SpamException e4) {
            if (!test) {
                return null;
            }
            if (spammerMod == null) {
                e4.printStackTrace();
                return null;
            }
            final String message = String.valueOf(e4.getClass().getSimpleName()) + " at line " + (e4.line + 1) + ":\n" + e4.getMessage();
            switch (JOptionPane.showOptionDialog(spammerMod.getDialog(), message, "Error", -1, 0, null, new String[] { "Go to line", "Show help" }, 0)) {
                case 0: {
                    spammerMod.goToLine(e4.line);
                    break;
                }
                case 1: {
                    try {
                        JOptionPane.showOptionDialog(spammerMod.getDialog(), e4.getHelp(), "Help", -1, 1, null, new String[] { "OK" }, 0);
                    }
                    catch (final HeadlessException e5) {
                        e5.printStackTrace();
                    }
                    break;
                }
            }
            return null;
        }
        catch (final Exception e6) {
            System.err.println("Unknown exception in SpamProcessor:");
            e6.printStackTrace();
            return null;
        }
        return spam;
    }
    
    private static void log(final String log) {
        if (!"".isEmpty()) {
            System.out.println(log);
        }
    }
}
