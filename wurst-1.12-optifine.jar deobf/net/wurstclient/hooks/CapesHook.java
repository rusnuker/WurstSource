// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.hooks;

import java.io.Reader;
import java.io.InputStreamReader;
import net.wurstclient.util.json.JsonUtils;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import com.mojang.authlib.GameProfile;
import com.google.gson.JsonObject;

public class CapesHook
{
    private static JsonObject capes;
    
    public static void checkCape(final GameProfile player, final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map) {
        if (CapesHook.capes == null) {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection)new URL("https://www.wurstclient.net/api/v1/capes.json").openConnection();
                connection.connect();
                CapesHook.capes = JsonUtils.JSON_PARSER.parse((Reader)new InputStreamReader(connection.getInputStream())).getAsJsonObject();
            }
            catch (final Exception e) {
                System.err.println("[Wurst] Failed to load capes from wurstclient.net!");
                e.printStackTrace();
                return;
            }
        }
        try {
            if (CapesHook.capes.has(player.getName())) {
                map.put(MinecraftProfileTexture.Type.CAPE, new MinecraftProfileTexture(CapesHook.capes.get(player.getName()).getAsString(), (Map)null));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
