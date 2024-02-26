// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.files;

import java.lang.reflect.Field;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;

public final class ConfigFiles
{
    public static final OptionsConfig OPTIONS;
    public static final ModsConfig MODS;
    public static final SettingsConfig SETTINGS;
    public static final NavigatorConfig NAVIGATOR;
    public static final FriendsConfig FRIENDS;
    
    static {
        OPTIONS = new OptionsConfig();
        MODS = new ModsConfig();
        SETTINGS = new SettingsConfig();
        NAVIGATOR = new NavigatorConfig();
        FRIENDS = new FriendsConfig();
    }
    
    public static void initialize() {
        try {
            Field[] fields;
            for (int length = (fields = ConfigFiles.class.getFields()).length, i = 0; i < length; ++i) {
                final Field field = fields[i];
                ((Config)field.get(null)).initialize();
            }
        }
        catch (final ReflectiveOperationException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Initializing config files"));
        }
    }
}
