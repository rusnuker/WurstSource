// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.files;

import com.google.gson.JsonObject;
import net.wurstclient.features.Hack;
import java.util.Iterator;
import net.wurstclient.WurstClient;
import java.util.Map;
import com.google.gson.JsonElement;

public final class ModsConfig extends Config
{
    public ModsConfig() {
        super("modules.json");
    }
    
    @Override
    protected void loadFromJson(final JsonElement json) {
        for (final Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            final Hack mod = WurstClient.INSTANCE.hax.getByName(entry.getKey());
            if (mod != null) {
                if (!mod.isStateSaved()) {
                    continue;
                }
                final JsonObject jsonMod = entry.getValue().getAsJsonObject();
                if (!jsonMod.get("enabled").getAsBoolean()) {
                    continue;
                }
                mod.enableOnStartup();
            }
        }
    }
    
    @Override
    protected JsonElement saveToJson() {
        final JsonObject json = new JsonObject();
        final JsonObject jsonMod = new JsonObject();
        jsonMod.addProperty("enabled", Boolean.valueOf(true));
        for (final Hack mod : WurstClient.INSTANCE.hax.getAll()) {
            if (mod.isEnabled() && mod.isStateSaved()) {
                json.add(mod.getName(), (JsonElement)jsonMod);
            }
        }
        return (JsonElement)json;
    }
}
