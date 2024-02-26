// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.files;

import java.lang.reflect.Field;
import java.io.IOException;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import net.minecraft.client.Minecraft;
import java.nio.file.Path;

public final class WurstFolders
{
    public static final Path MAIN;
    public static final Path AUTOBUILD;
    public static final Path SKINS;
    public static final Path SERVERLISTS;
    public static final Path SPAM;
    public static final Path SCRIPTS;
    public static final Path RSA;
    
    static {
        MAIN = Minecraft.getMinecraft().mcDataDir.toPath().resolve("wurst");
        AUTOBUILD = WurstFolders.MAIN.resolve("autobuild");
        SKINS = WurstFolders.MAIN.resolve("skins");
        SERVERLISTS = WurstFolders.MAIN.resolve("serverlists");
        SPAM = WurstFolders.MAIN.resolve("spam");
        SCRIPTS = WurstFolders.SPAM.resolve("autorun");
        RSA = Paths.get(System.getProperty("user.home"), ".ssh");
    }
    
    public static void initialize() {
        if (System.getProperty("user.home") == null) {
            throw new RuntimeException("user.home property is missing!");
        }
        try {
            Field[] fields;
            for (int length = (fields = WurstFolders.class.getFields()).length, i = 0; i < length; ++i) {
                final Field field = fields[i];
                final Path path = (Path)field.get(null);
                if (!Files.exists(path, new LinkOption[0])) {
                    Files.createDirectory(path, (FileAttribute<?>[])new FileAttribute[0]);
                }
            }
        }
        catch (final ReflectiveOperationException | IOException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Initializing config files"));
        }
    }
}
