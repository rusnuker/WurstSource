// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.ColorsSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.HelpPage;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@HelpPage("Special_Features/Target")
@SearchTags({ "AntiBot", "anti bot", "AntiKillauraBot", "anti killaura bot" })
public final class TargetSpf extends OtherFeature
{
    public final CheckboxSetting players;
    public final CheckboxSetting animals;
    public final CheckboxSetting monsters;
    public final CheckboxSetting golems;
    public final CheckboxSetting sleepingPlayers;
    public final CheckboxSetting invisiblePlayers;
    public final CheckboxSetting invisibleMobs;
    public final CheckboxSetting teams;
    public final ColorsSetting teamColors;
    
    public TargetSpf() {
        super("Target", "Controls what entities are targeted by other features. Also allows you to\nbypass AntiAura plugins by filtering out fake entities.");
        this.players = new CheckboxSetting("Players", true);
        this.animals = new CheckboxSetting("Animals", true);
        this.monsters = new CheckboxSetting("Monsters", true);
        this.golems = new CheckboxSetting("Golems", true);
        this.sleepingPlayers = new CheckboxSetting("Sleeping players", false);
        this.invisiblePlayers = new CheckboxSetting("Invisible players", false);
        this.invisibleMobs = new CheckboxSetting("Invisible mobs", false);
        this.teams = new CheckboxSetting("Teams", false);
        this.teamColors = new ColorsSetting("Team Colors", new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true });
        this.addSetting(this.players);
        this.addSetting(this.animals);
        this.addSetting(this.monsters);
        this.addSetting(this.golems);
        this.addSetting(this.sleepingPlayers);
        this.addSetting(this.invisiblePlayers);
        this.addSetting(this.invisibleMobs);
        this.addSetting(this.teams);
        this.addSetting(this.teamColors);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { TargetSpf.WURST.hax.killauraMod, TargetSpf.WURST.hax.killauraLegitMod, TargetSpf.WURST.hax.multiAuraMod, TargetSpf.WURST.hax.clickAuraMod, TargetSpf.WURST.hax.triggerBotMod, TargetSpf.WURST.hax.bowAimbotMod };
    }
}
