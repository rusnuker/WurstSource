// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.files;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.util.json.JsonUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class FileManager
{
    public final File autoMaximize;
    
    public FileManager() {
        this.autoMaximize = new File(WurstFolders.MAIN.toFile(), "automaximize.json");
    }
    
    public boolean loadAutoMaximize() {
        boolean autoMaximizeEnabled = false;
        if (!this.autoMaximize.exists()) {
            this.saveAutoMaximize(true);
        }
        try {
            Throwable t = null;
            try {
                final BufferedReader load = new BufferedReader(new FileReader(this.autoMaximize));
                try {
                    if (JsonUtils.GSON.fromJson((Reader)load, (Class)Boolean.class)) {
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
        catch (final Exception e) {
            e.printStackTrace();
        }
        return autoMaximizeEnabled;
    }
    
    public void saveAutoMaximize(final boolean autoMaximizeEnabled) {
        try {
            if (!this.autoMaximize.getParentFile().exists()) {
                this.autoMaximize.getParentFile().mkdirs();
            }
            Throwable t = null;
            try {
                final PrintWriter save = new PrintWriter(new FileWriter(this.autoMaximize));
                try {
                    save.println(JsonUtils.PRETTY_GSON.toJson((Object)autoMaximizeEnabled));
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
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
