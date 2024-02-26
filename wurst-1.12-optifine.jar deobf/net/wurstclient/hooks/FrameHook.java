// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.hooks;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import net.wurstclient.compatibility.WMinecraft;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import org.lwjgl.LWJGLException;
import java.io.InputStream;
import java.awt.Image;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import java.awt.Component;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import java.awt.Color;
import java.awt.Canvas;
import net.wurstclient.bot.WurstBot;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.resources.DefaultResourcePack;
import javax.swing.JFrame;

public class FrameHook
{
    private static JFrame frame;
    
    public static void createFrame(final DefaultResourcePack mcDefaultResourcePack, final Logger logger) throws LWJGLException {
        if (!isAutoMaximize() && !WurstBot.isEnabled()) {
            return;
        }
        FrameHook.frame = new JFrame("Minecraft 1.12.2");
        final Canvas canvas = new Canvas();
        canvas.setBackground(new Color(16, 16, 16));
        Display.setParent(canvas);
        final Minecraft mc = Minecraft.getMinecraft();
        canvas.setSize(mc.displayWidth, mc.displayHeight);
        FrameHook.frame.add(canvas);
        FrameHook.frame.setDefaultCloseOperation(3);
        FrameHook.frame.pack();
        FrameHook.frame.setLocationRelativeTo(null);
        try {
            Throwable t = null;
            try {
                final InputStream icon16 = mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                try {
                    try {
                        final InputStream icon17 = mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
                        try {
                            final ArrayList<BufferedImage> icons = new ArrayList<BufferedImage>();
                            icons.add(ImageIO.read(icon16));
                            icons.add(ImageIO.read(icon17));
                            FrameHook.frame.setIconImages(icons);
                        }
                        finally {
                            if (icon17 != null) {
                                icon17.close();
                            }
                        }
                    }
                    finally {}
                }
                finally {
                    if (icon16 != null) {
                        icon16.close();
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
        catch (final Exception e) {
            logger.error("Couldn't set icon", (Throwable)e);
        }
        if (!WurstBot.isEnabled()) {
            FrameHook.frame.setVisible(true);
        }
    }
    
    private static boolean isAutoMaximize() {
        final File autoMaximizeFile = new File(Minecraft.getMinecraft().mcDataDir + "/wurst/automaximize.json");
        boolean autoMaximizeEnabled = false;
        if (!autoMaximizeFile.exists()) {
            createAutoMaximizeFile(autoMaximizeFile);
        }
        try {
            Throwable t = null;
            try {
                final BufferedReader load = new BufferedReader(new FileReader(autoMaximizeFile));
                try {
                    final String line = load.readLine();
                    Minecraft.getMinecraft();
                    if (line.equals("true")) {
                        WMinecraft.isRunningOnMac();
                    }
                    autoMaximizeEnabled = false;
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
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        return autoMaximizeEnabled;
    }
    
    private static void createAutoMaximizeFile(final File autoMaximizeFile) {
        try {
            if (!autoMaximizeFile.getParentFile().exists()) {
                autoMaximizeFile.getParentFile().mkdirs();
            }
            Throwable t = null;
            try {
                final PrintWriter save = new PrintWriter(new FileWriter(autoMaximizeFile));
                try {
                    final PrintWriter printWriter = save;
                    WMinecraft.isRunningOnMac();
                    printWriter.println(Boolean.toString(false));
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
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void maximize() {
        if (FrameHook.frame != null) {
            FrameHook.frame.setExtendedState(6);
        }
    }
    
    public static JFrame getFrame() {
        return FrameHook.frame;
    }
}
