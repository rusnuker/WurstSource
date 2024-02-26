// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.files;

import java.io.BufferedWriter;
import java.nio.file.OpenOption;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;
import net.wurstclient.util.json.JsonUtils;
import com.google.gson.JsonElement;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public abstract class Config
{
    private final Path path;
    
    public Config(final String name) {
        this.path = WurstFolders.MAIN.resolve(name);
    }
    
    public final void initialize() {
        if (Files.exists(this.path, new LinkOption[0])) {
            this.load();
        }
        else {
            this.save();
        }
    }
    
    public final void load() {
        try {
            this.loadFromJson(this.readFile(this.path));
        }
        catch (final Exception e) {
            System.out.println("Failed to load " + this.path.getFileName());
            e.printStackTrace();
        }
    }
    
    public final void save() {
        try {
            this.writeFile(this.path, this.saveToJson());
        }
        catch (final Exception e) {
            System.out.println("Failed to save " + this.path.getFileName());
            e.printStackTrace();
        }
    }
    
    protected JsonElement readFile(final Path path) throws IOException {
        Throwable t = null;
        try {
            final BufferedReader reader = Files.newBufferedReader(path);
            try {
                return JsonUtils.JSON_PARSER.parse((Reader)reader);
            }
            finally {
                if (reader != null) {
                    reader.close();
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
    
    protected void writeFile(final Path path, final JsonElement json) throws IOException {
        Throwable t = null;
        try {
            final BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);
            try {
                JsonUtils.PRETTY_GSON.toJson(json, (Appendable)writer);
            }
            finally {
                if (writer != null) {
                    writer.close();
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
    
    protected abstract void loadFromJson(final JsonElement p0);
    
    protected abstract JsonElement saveToJson();
}
