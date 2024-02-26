// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.files;

import com.google.gson.JsonObject;
import java.util.Iterator;
import net.wurstclient.settings.Setting;
import java.util.Map;
import net.wurstclient.WurstClient;
import net.wurstclient.features.Feature;
import java.util.HashMap;
import com.google.gson.JsonElement;

public final class SettingsConfig extends Config
{
    public SettingsConfig() {
        super("settings.json");
    }
    
    @Override
    protected void loadFromJson(final JsonElement json) {
        if (!json.isJsonObject()) {
            return;
        }
        final HashMap<String, Feature> features = new HashMap<String, Feature>();
        for (final Feature feature : WurstClient.INSTANCE.navigator.getList()) {
            features.put(feature.getName(), feature);
        }
        for (final Map.Entry<String, JsonElement> e : json.getAsJsonObject().entrySet()) {
            if (!e.getValue().isJsonObject()) {
                continue;
            }
            final Feature feature2 = features.get(e.getKey());
            if (feature2 == null) {
                continue;
            }
            final HashMap<String, Setting> settings = new HashMap<String, Setting>();
            for (final Setting setting : feature2.getSettings()) {
                settings.put(setting.getName().toLowerCase(), setting);
            }
            for (final Map.Entry<String, JsonElement> e2 : e.getValue().getAsJsonObject().entrySet()) {
                final String key = e2.getKey().toLowerCase();
                if (!settings.containsKey(key)) {
                    continue;
                }
                settings.get(key).fromJson(e2.getValue());
            }
        }
    }
    
    @Override
    protected JsonElement saveToJson() {
        final JsonObject json = new JsonObject();
        for (final Feature feature : WurstClient.INSTANCE.navigator.getList()) {
            if (feature.getSettings().isEmpty()) {
                continue;
            }
            final JsonObject settings = new JsonObject();
            for (final Setting setting : feature.getSettings()) {
                settings.add(setting.getName(), setting.toJson());
            }
            json.add(feature.getName(), (JsonElement)settings);
        }
        return (JsonElement)json;
    }
}
