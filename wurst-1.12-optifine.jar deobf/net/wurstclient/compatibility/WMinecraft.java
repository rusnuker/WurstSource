// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.entity.EntityPlayerSP;
import java.util.Collections;
import java.util.TreeMap;
import net.minecraft.client.Minecraft;
import java.util.NavigableMap;

public final class WMinecraft
{
    public static final String VERSION = "1.12";
    public static final String DISPLAY_VERSION = "1.12.2";
    public static final boolean OPTIFINE = true;
    public static final boolean REALMS = false;
    public static final boolean COOLDOWN = true;
    public static final NavigableMap<Integer, String> PROTOCOLS;
    private static final Minecraft mc;
    
    static {
        final TreeMap<Integer, String> protocols = new TreeMap<Integer, String>();
        protocols.put(335, "1.12");
        PROTOCOLS = Collections.unmodifiableNavigableMap((NavigableMap<Integer, ? extends String>)protocols);
        mc = Minecraft.getMinecraft();
    }
    
    public static EntityPlayerSP getPlayer() {
        return WMinecraft.mc.player;
    }
    
    public static WorldClient getWorld() {
        return WMinecraft.mc.world;
    }
    
    public static NetHandlerPlayClient getConnection() {
        return getPlayer().connection;
    }
    
    public static boolean isRunningOnMac() {
        return Minecraft.IS_RUNNING_ON_MAC;
    }
}
