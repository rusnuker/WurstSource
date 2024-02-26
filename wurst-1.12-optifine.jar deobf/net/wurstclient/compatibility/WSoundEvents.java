// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.compatibility;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;

public final class WSoundEvents
{
    public static boolean isBobberSplash(final SPacketSoundEffect soundEffect) {
        return SoundEvents.ENTITY_BOBBER_SPLASH.equals(soundEffect.getSound());
    }
    
    public static void playButtonClick() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
}
