// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util.json;

import java.util.Iterator;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Objects;
import com.google.gson.JsonObject;

public final class WsonObject
{
    private final JsonObject json;
    
    public WsonObject(final JsonObject json) {
        this.json = Objects.requireNonNull(json);
    }
    
    public boolean getBoolean(final String key) throws JsonException {
        return JsonUtils.getAsBoolean(this.json.get(key));
    }
    
    public int getInt(final String key) throws JsonException {
        return JsonUtils.getAsInt(this.json.get(key));
    }
    
    public long getLong(final String key) throws JsonException {
        return JsonUtils.getAsLong(this.json.get(key));
    }
    
    public String getString(final String key) throws JsonException {
        return JsonUtils.getAsString(this.json.get(key));
    }
    
    public WsonArray getArray(final String key) throws JsonException {
        return JsonUtils.getAsArray(this.json.get(key));
    }
    
    public LinkedHashMap<String, String> getAllStrings() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        for (final Map.Entry<String, JsonElement> entry : this.json.entrySet()) {
            final JsonElement value = entry.getValue();
            if (!JsonUtils.isString(value)) {
                continue;
            }
            map.put(entry.getKey(), value.getAsString());
        }
        return map;
    }
    
    public LinkedHashMap<String, Number> getAllNumbers() {
        final LinkedHashMap<String, Number> map = new LinkedHashMap<String, Number>();
        for (final Map.Entry<String, JsonElement> entry : this.json.entrySet()) {
            final JsonElement value = entry.getValue();
            if (!JsonUtils.isNumber(value)) {
                continue;
            }
            map.put(entry.getKey(), value.getAsNumber());
        }
        return map;
    }
    
    public LinkedHashMap<String, JsonObject> getAllJsonObjects() {
        final LinkedHashMap<String, JsonObject> map = new LinkedHashMap<String, JsonObject>();
        for (final Map.Entry<String, JsonElement> entry : this.json.entrySet()) {
            final JsonElement value = entry.getValue();
            if (!value.isJsonObject()) {
                continue;
            }
            map.put(entry.getKey(), value.getAsJsonObject());
        }
        return map;
    }
    
    public JsonObject toJsonObject() {
        return this.json;
    }
}
