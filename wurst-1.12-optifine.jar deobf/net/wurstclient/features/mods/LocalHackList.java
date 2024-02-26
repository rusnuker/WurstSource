// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods;

import net.wurstclient.features.HackList;

public final class LocalHackList extends HackList
{
    public final AutoTotemHack autoTotemHack;
    public final BoatFlyMod boatFlyMod;
    public final ExtraElytraMod extraElytraMod;
    
    public LocalHackList() {
        this.autoTotemHack = new AutoTotemHack();
        this.boatFlyMod = new BoatFlyMod();
        this.extraElytraMod = new ExtraElytraMod();
        this.registerHax(LocalHackList.class, "Initializing version-specific Wurst hacks");
    }
}
