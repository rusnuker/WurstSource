// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.client.Minecraft;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "name protect" })
@Bypasses
public final class NameProtectMod extends Hack
{
    public NameProtectMod() {
        super("NameProtect", "Hides all player names.");
        this.setCategory(Category.RENDER);
    }
    
    public String protect(final String string) {
        if (!this.isActive() || NameProtectMod.MC.player == null) {
            return string;
        }
        final String me = Minecraft.getMinecraft().session.getUsername();
        if (string.contains(me)) {
            return string.replace(me, "§oMe§r");
        }
        int i = 0;
        for (final NetworkPlayerInfo info : WMinecraft.getConnection().getPlayerInfoMap()) {
            ++i;
            final String name = info.getPlayerNameForReal().replaceAll("§\\w", "");
            if (string.contains(name)) {
                return string.replace(name, "§oPlayer" + i + "§r");
            }
        }
        for (final EntityPlayer player : WMinecraft.getWorld().playerEntities) {
            ++i;
            final String name = player.getName();
            if (string.contains(name)) {
                return string.replace(name, "§oPlayer" + i + "§r");
            }
        }
        return string;
    }
}
