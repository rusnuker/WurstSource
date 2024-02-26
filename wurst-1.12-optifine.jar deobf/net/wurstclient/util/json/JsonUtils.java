// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util.json;

import com.google.gson.JsonPrimitive;
import java.io.BufferedWriter;
import java.nio.file.OpenOption;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.io.IOException;
import java.io.BufferedReader;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.nio.file.Files;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.Gson;

public enum JsonUtils
{
    public static final Gson GSON;
    public static final Gson PRETTY_GSON;
    public static final JsonParser JSON_PARSER;
    
    static {
        GSON = new Gson();
        PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
        JSON_PARSER = new JsonParser();
    }
    
    private JsonUtils(final String name, final int ordinal) {
    }
    
    public static JsonElement parseFile(final Path path) throws IOException, JsonException {
        try {
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
        catch (final JsonParseException e) {
            throw new JsonException((Throwable)e);
        }
    }
    
    public static JsonElement parseURL(final String url) throws IOException, JsonException {
        final URI uri = URI.create(url);
        try {
            Throwable t = null;
            try {
                final InputStream input = uri.toURL().openStream();
                try {
                    final InputStreamReader reader = new InputStreamReader(input);
                    final BufferedReader bufferedReader = new BufferedReader(reader);
                    return new JsonParser().parse((Reader)bufferedReader);
                }
                finally {
                    if (input != null) {
                        input.close();
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
        catch (final JsonParseException e) {
            throw new JsonException((Throwable)e);
        }
    }
    
    public static WsonArray parseFileToArray(final Path path) throws IOException, JsonException {
        final JsonElement json = parseFile(path);
        if (!json.isJsonArray()) {
            throw new JsonException();
        }
        return new WsonArray(json.getAsJsonArray());
    }
    
    public static WsonArray parseURLToArray(final String url) throws IOException, JsonException {
        final JsonElement json = parseURL(url);
        if (!json.isJsonArray()) {
            throw new JsonException();
        }
        return new WsonArray(json.getAsJsonArray());
    }
    
    public static WsonObject parseFileToObject(final Path path) throws IOException, JsonException {
        final JsonElement json = parseFile(path);
        if (!json.isJsonObject()) {
            throw new JsonException();
        }
        return new WsonObject(json.getAsJsonObject());
    }
    
    public static WsonObject parseURLToObject(final String url) throws IOException, JsonException {
        final JsonElement json = parseURL(url);
        if (!json.isJsonObject()) {
            throw new JsonException();
        }
        return new WsonObject(json.getAsJsonObject());
    }
    
    public static void toJson(final JsonElement json, final Path path) throws IOException, JsonException {
        try {
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
        catch (final JsonParseException e) {
            throw new JsonException((Throwable)e);
        }
    }
    
    public static boolean isBoolean(final JsonElement json) {
        if (json == null || !json.isJsonPrimitive()) {
            return false;
        }
        final JsonPrimitive primitive = json.getAsJsonPrimitive();
        return primitive.isBoolean();
    }
    
    public static boolean getAsBoolean(final JsonElement json) throws JsonException {
        if (!isBoolean(json)) {
            throw new JsonException();
        }
        return json.getAsBoolean();
    }
    
    public static boolean getAsBoolean(final JsonElement json, final boolean fallback) {
        if (!isBoolean(json)) {
            return fallback;
        }
        return json.getAsBoolean();
    }
    
    public static boolean isNumber(final JsonElement json) {
        if (json == null || !json.isJsonPrimitive()) {
            return false;
        }
        final JsonPrimitive primitive = json.getAsJsonPrimitive();
        return primitive.isNumber();
    }
    
    public static int getAsInt(final JsonElement json) throws JsonException {
        if (!isNumber(json)) {
            throw new JsonException();
        }
        return json.getAsInt();
    }
    
    public static int getAsInt(final JsonElement json, final int fallback) {
        if (!isNumber(json)) {
            return fallback;
        }
        return json.getAsInt();
    }
    
    public static long getAsLong(final JsonElement json) throws JsonException {
        if (!isNumber(json)) {
            throw new JsonException();
        }
        return json.getAsLong();
    }
    
    public static long getAsLong(final JsonElement json, final long fallback) {
        if (!isNumber(json)) {
            return fallback;
        }
        return json.getAsLong();
    }
    
    public static boolean isString(final JsonElement json) {
        if (json == null || !json.isJsonPrimitive()) {
            return false;
        }
        final JsonPrimitive primitive = json.getAsJsonPrimitive();
        return primitive.isString();
    }
    
    public static String getAsString(final JsonElement json) throws JsonException {
        if (!isString(json)) {
            throw new JsonException();
        }
        return json.getAsString();
    }
    
    public static String getAsString(final JsonElement json, final String fallback) {
        if (!isString(json)) {
            return fallback;
        }
        return json.getAsString();
    }
    
    public static WsonArray getAsArray(final JsonElement json) throws JsonException {
        if (!json.isJsonArray()) {
            throw new JsonException();
        }
        return new WsonArray(json.getAsJsonArray());
    }
}
