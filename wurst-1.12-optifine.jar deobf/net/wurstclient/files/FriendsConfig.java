// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.files;

import net.wurstclient.util.json.JsonUtils;
import net.wurstclient.options.FriendsList;
import net.wurstclient.WurstClient;
import com.google.gson.JsonElement;

public final class FriendsConfig extends Config
{
    public FriendsConfig() {
        super("friends.json");
    }
    
    @Override
    protected void loadFromJson(final JsonElement json) {
        WurstClient.INSTANCE.friends = (FriendsList)JsonUtils.GSON.fromJson(json, (Class)FriendsList.class);
    }
    
    @Override
    protected JsonElement saveToJson() {
        return JsonUtils.GSON.toJsonTree((Object)WurstClient.INSTANCE.friends);
    }
}
