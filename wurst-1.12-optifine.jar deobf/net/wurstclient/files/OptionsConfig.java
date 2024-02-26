// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.files;

import net.wurstclient.WurstClient;
import net.wurstclient.util.json.JsonUtils;
import net.wurstclient.options.OptionsManager;
import com.google.gson.JsonElement;

public final class OptionsConfig extends Config
{
    public OptionsConfig() {
        super("options.json");
    }
    
    @Override
    protected void loadFromJson(final JsonElement json) {
        final OptionsManager newOptions = (OptionsManager)JsonUtils.GSON.fromJson(json, (Class)OptionsManager.class);
        if (newOptions != null) {
            WurstClient.INSTANCE.options = newOptions;
        }
    }
    
    @Override
    protected JsonElement saveToJson() {
        return JsonUtils.GSON.toJsonTree((Object)WurstClient.INSTANCE.options);
    }
}
