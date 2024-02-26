// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager;

import com.google.gson.JsonElement;
import net.wurstclient.util.json.JsonUtils;
import java.util.Iterator;
import java.util.Collection;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import net.wurstclient.util.json.WsonObject;
import net.wurstclient.util.json.JsonException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public final class AltsFile
{
    private final Path path;
    private boolean disableSaving;
    private final Encryption encryption;
    
    public AltsFile(final Path path) {
        this.path = path;
        this.encryption = new Encryption();
    }
    
    public void load(final AltManager altManager) {
        try {
            final WsonObject wson = this.encryption.parseFileToObject(this.path);
            this.loadAlts(wson, altManager);
        }
        catch (final NoSuchFileException ex) {}
        catch (final IOException | JsonException e) {
            System.out.println("Couldn't load " + this.path.getFileName());
            e.printStackTrace();
            this.renameCorrupted();
        }
        this.save(altManager);
    }
    
    private void renameCorrupted() {
        try {
            final Path newPath = this.path.resolveSibling("!CORRUPTED_" + this.path.getFileName());
            Files.move(this.path, newPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Renamed to " + newPath.getFileName());
        }
        catch (final IOException e2) {
            System.out.println("Couldn't rename corrupted file " + this.path.getFileName());
            e2.printStackTrace();
        }
    }
    
    private void loadAlts(final WsonObject wson, final AltManager altManager) {
        final ArrayList<Alt> alts = new ArrayList<Alt>();
        for (final Map.Entry<String, JsonObject> e : wson.getAllJsonObjects().entrySet()) {
            final String email = e.getKey();
            final JsonObject jsonAlt = e.getValue();
            alts.add(this.loadAlt(email, jsonAlt));
        }
        try {
            this.disableSaving = true;
            altManager.addAll(alts);
        }
        finally {
            this.disableSaving = false;
        }
        this.disableSaving = false;
    }
    
    private Alt loadAlt(final String email, final JsonObject jsonAlt) {
        final String password = JsonUtils.getAsString(jsonAlt.get("password"), "");
        final String name = JsonUtils.getAsString(jsonAlt.get("name"), "");
        final boolean starred = JsonUtils.getAsBoolean(jsonAlt.get("starred"), false);
        return new Alt(email, password, name, starred);
    }
    
    public void save(final AltManager alts) {
        if (this.disableSaving) {
            return;
        }
        final JsonObject json = this.createJson(alts);
        try {
            this.encryption.toEncryptedJson(json, this.path);
        }
        catch (final IOException | JsonException e) {
            System.out.println("Couldn't save " + this.path.getFileName());
            e.printStackTrace();
        }
    }
    
    private JsonObject createJson(final AltManager alts) {
        final JsonObject json = new JsonObject();
        for (final Alt alt : alts.getList()) {
            final JsonObject jsonAlt = new JsonObject();
            jsonAlt.addProperty("password", alt.getPassword());
            jsonAlt.addProperty("name", alt.getName());
            jsonAlt.addProperty("starred", Boolean.valueOf(alt.isStarred()));
            json.add(alt.getEmail(), (JsonElement)jsonAlt);
        }
        return json;
    }
}
