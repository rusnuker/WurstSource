// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import net.wurstclient.WurstClient;
import net.minecraft.client.Minecraft;

public class Timer
{
    public int elapsedTicks;
    public float renderPartialTicks;
    public float field_194148_c;
    private long lastSyncSysClock;
    private float field_194149_e;
    
    public Timer(final float tps) {
        this.field_194149_e = 1000.0f / tps;
        this.lastSyncSysClock = Minecraft.getSystemTime();
    }
    
    public void updateTimer() {
        final float timerSpeed = WurstClient.INSTANCE.hax.timerMod.getTimerSpeed();
        final long i = Minecraft.getSystemTime();
        this.field_194148_c = (i - this.lastSyncSysClock) / this.field_194149_e * timerSpeed;
        this.lastSyncSysClock = i;
        this.renderPartialTicks += this.field_194148_c;
        this.elapsedTicks = (int)this.renderPartialTicks;
        this.renderPartialTicks -= this.elapsedTicks;
    }
}
