// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.files;

import net.wurstclient.features.Feature;
import java.util.Iterator;
import com.google.gson.JsonObject;
import net.wurstclient.settings.Setting;
import net.wurstclient.WurstClient;
import com.google.gson.JsonElement;

public final class NavigatorConfig extends Config
{
    public NavigatorConfig() {
        super("navigator.json");
    }
    
    @Override
    protected void loadFromJson(final JsonElement json) {
        WurstClient.INSTANCE.navigator.forEach(feature -> {
            final String featureName = feature.getName();
            if (!(!jsonElement.getAsJsonObject().has(featureName))) {
                final JsonObject jsonFeature = jsonElement.getAsJsonObject().get(featureName).getAsJsonObject();
                if (jsonFeature.has("preference")) {
                    WurstClient.INSTANCE.navigator.setPreference(featureName, jsonFeature.get("preference").getAsLong());
                }
                if (jsonFeature.has("settings")) {
                    final JsonObject jsonSettings = jsonFeature.get("settings").getAsJsonObject();
                    feature.getSettings().iterator();
                    final Iterator iterator;
                    while (iterator.hasNext()) {
                        final Setting setting = iterator.next();
                        try {
                            setting.legacyFromJson(jsonSettings);
                        }
                        catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    
    @Override
    protected JsonElement saveToJson() {
        final JsonObject json = new JsonObject();
        WurstClient.INSTANCE.navigator.forEach(feature -> {
            final JsonObject jsonFeature = new JsonObject();
            final long preference = WurstClient.INSTANCE.navigator.getPreference(feature.getName());
            if (preference != 0L) {
                jsonFeature.addProperty("preference", (Number)preference);
            }
            if (!jsonFeature.entrySet().isEmpty()) {
                jsonObject.add(feature.getName(), (JsonElement)jsonFeature);
            }
            return;
        });
        return (JsonElement)json;
    }
}
