// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.chat;

import java.util.Iterator;
import net.wurstclient.util.ChatUtils;
import java.util.List;
import java.util.Collections;
import net.minecraft.util.StringUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.Category;
import java.util.ArrayList;
import java.util.Random;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "mass tpa" })
@Bypasses
@DontSaveState
public final class MassTpaMod extends Hack implements UpdateListener, ChatInputListener
{
    private final Random random;
    private final ArrayList<String> players;
    private int index;
    private int timer;
    
    public MassTpaMod() {
        super("MassTPA", "Sends a TPA request to all players.\nStops if someone accepts.");
        this.random = new Random();
        this.players = new ArrayList<String>();
        this.setCategory(Category.CHAT);
    }
    
    @Override
    public void onEnable() {
        this.index = 0;
        this.timer = -1;
        this.players.clear();
        for (final NetworkPlayerInfo info : WMinecraft.getConnection().getPlayerInfoMap()) {
            String name = info.getPlayerNameForReal();
            name = StringUtils.stripControlCodes(name);
            if (name.equals(MassTpaMod.MC.player.getName())) {
                continue;
            }
            this.players.add(name);
        }
        Collections.shuffle(this.players, this.random);
        MassTpaMod.EVENTS.add(ChatInputListener.class, this);
        MassTpaMod.EVENTS.add(UpdateListener.class, this);
        if (this.players.isEmpty()) {
            ChatUtils.error("Couldn't find any players.");
            this.setEnabled(false);
        }
    }
    
    @Override
    public void onDisable() {
        MassTpaMod.EVENTS.remove(ChatInputListener.class, this);
        MassTpaMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (this.timer > -1) {
            --this.timer;
            return;
        }
        if (this.index >= this.players.size()) {
            this.setEnabled(false);
        }
        MassTpaMod.MC.player.sendChatMessage("/tpa " + this.players.get(this.index));
        ++this.index;
        this.timer = 20;
    }
    
    @Override
    public void onReceivedMessage(final ChatInputEvent event) {
        final String message = event.getComponent().getUnformattedText().toLowerCase();
        if (message.startsWith("§c[§6wurst§c]")) {
            return;
        }
        if (message.contains("/help") || message.contains("permission")) {
            event.cancel();
            ChatUtils.error("This server doesn't have TPA.");
            this.setEnabled(false);
        }
        else if ((message.contains("accepted") && message.contains("request")) || (message.contains("akzeptiert") && message.contains("anfrage"))) {
            event.cancel();
            ChatUtils.message("Someone accepted your TPA request. Stopping.");
            this.setEnabled(false);
        }
    }
}
