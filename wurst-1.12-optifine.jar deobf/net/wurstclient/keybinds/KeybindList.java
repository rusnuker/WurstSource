// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.keybinds;

import java.util.function.Function;
import java.util.Comparator;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.util.Iterator;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import org.lwjgl.input.Keyboard;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.TreeMap;
import java.nio.file.NoSuchFileException;
import java.io.Reader;
import net.wurstclient.util.json.JsonUtils;
import java.nio.file.Files;
import java.util.ArrayList;
import java.nio.file.Path;

public final class KeybindList
{
    private final Path path;
    private final ArrayList<Keybind> keybinds;
    
    public KeybindList(final Path file) {
        this.keybinds = new ArrayList<Keybind>();
        this.path = file;
    }
    
    public void init() {
        JsonObject json;
        try {
            Throwable t = null;
            try {
                final BufferedReader reader = Files.newBufferedReader(this.path);
                try {
                    json = JsonUtils.JSON_PARSER.parse((Reader)reader).getAsJsonObject();
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
        catch (final NoSuchFileException e) {
            this.loadDefaults();
            return;
        }
        catch (final Exception e2) {
            System.out.println("Failed to load " + this.path.getFileName());
            e2.printStackTrace();
            this.loadDefaults();
            return;
        }
        this.keybinds.clear();
        final TreeMap<String, String> keybinds2 = new TreeMap<String, String>();
        for (final Map.Entry<String, JsonElement> entry : json.entrySet()) {
            final String key = entry.getKey().toUpperCase();
            if (Keyboard.getKeyIndex(key) == 0) {
                continue;
            }
            final JsonElement value = entry.getValue();
            String commands;
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                commands = value.getAsString();
            }
            else {
                if (!value.isJsonArray()) {
                    continue;
                }
                final ArrayList<String> commands2 = new ArrayList<String>();
                for (final JsonElement e3 : value.getAsJsonArray()) {
                    if (e3.isJsonPrimitive() && e3.getAsJsonPrimitive().isString()) {
                        commands2.add(e3.getAsString());
                    }
                }
                commands = String.join(";", commands2);
            }
            keybinds2.put(key, commands);
        }
        for (final Map.Entry<String, String> entry2 : keybinds2.entrySet()) {
            this.keybinds.add(new Keybind(entry2.getKey(), entry2.getValue()));
        }
        if (this.keybinds.isEmpty()) {
            this.loadDefaults();
        }
        this.save();
    }
    
    public void loadDefaults() {
        this.keybinds.clear();
        this.keybinds.add(new Keybind("B", "fastplace;fastbreak"));
        this.keybinds.add(new Keybind("C", "fullbright"));
        this.keybinds.add(new Keybind("G", "flight"));
        this.keybinds.add(new Keybind("GRAVE", "speednuker"));
        this.keybinds.add(new Keybind("H", "/home"));
        this.keybinds.add(new Keybind("J", "jesus"));
        this.keybinds.add(new Keybind("K", "multiaura"));
        this.keybinds.add(new Keybind("LCONTROL", "navigator"));
        this.keybinds.add(new Keybind("N", "nuker"));
        this.keybinds.add(new Keybind("R", "killaura"));
        this.keybinds.add(new Keybind("RSHIFT", "navigator"));
        this.keybinds.add(new Keybind("RCONTROL", "clickgui"));
        this.keybinds.add(new Keybind("U", "freecam"));
        this.keybinds.add(new Keybind("X", "x-ray"));
        this.keybinds.add(new Keybind("Z", "sneak"));
        this.save();
    }
    
    private void save() {
        final JsonObject json = new JsonObject();
        for (final Keybind keybind : this.keybinds) {
            json.addProperty(keybind.getKey(), keybind.getCommands());
        }
        try {
            Throwable t = null;
            try {
                final BufferedWriter writer = Files.newBufferedWriter(this.path, new OpenOption[0]);
                try {
                    JsonUtils.PRETTY_GSON.toJson((JsonElement)json, (Appendable)writer);
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
        catch (final IOException e) {
            System.out.println("Failed to save " + this.path.getFileName());
            e.printStackTrace();
        }
    }
    
    public int size() {
        return this.keybinds.size();
    }
    
    public Keybind get(final int index) {
        return this.keybinds.get(index);
    }
    
    public String getCommands(final String key) {
        for (final Keybind keybind : this.keybinds) {
            if (!key.equals(keybind.getKey())) {
                continue;
            }
            return keybind.getCommands();
        }
        return null;
    }
    
    public void add(final String key, final String commands) {
        this.keybinds.removeIf(keybind -> s.equals(keybind.getKey()));
        this.keybinds.add(new Keybind(key, commands));
        this.keybinds.sort(Comparator.comparing((Function<? super Keybind, ? extends Comparable>)Keybind::getKey));
        this.save();
    }
    
    public void remove(final String key) {
        this.keybinds.removeIf(keybind -> s.equals(keybind.getKey()));
        this.save();
    }
    
    public void removeAll() {
        this.keybinds.clear();
        this.save();
    }
    
    public static class Keybind
    {
        private final String key;
        private final String commands;
        
        public Keybind(final String key, final String commands) {
            this.key = key;
            this.commands = commands;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public String getCommands() {
            return this.commands;
        }
    }
}
