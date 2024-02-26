// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import java.util.List;
import java.util.Collections;
import java.util.stream.Stream;
import net.minecraft.world.World;
import net.minecraft.client.entity.EntityPlayerSP;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.wurstclient.util.EntityFakePlayer;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.clickgui.Component;
import net.wurstclient.clickgui.Radar;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.minecraft.entity.Entity;
import java.util.ArrayList;
import net.wurstclient.clickgui.Window;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "MiniMap", "mini map" })
@Bypasses
public final class RadarMod extends Hack implements UpdateListener
{
    private final Window window;
    private final ArrayList<Entity> entities;
    private final SliderSetting radius;
    private final CheckboxSetting rotate;
    private final CheckboxSetting filterPlayers;
    private final CheckboxSetting filterSleeping;
    private final CheckboxSetting filterMonsters;
    private final CheckboxSetting filterAnimals;
    private final CheckboxSetting filterInvisible;
    
    public RadarMod() {
        super("Radar", "Shows the location of nearby entities.\n" + ChatFormatting.RED + "red" + ChatFormatting.RESET + " - players\n" + ChatFormatting.GOLD + "orange" + ChatFormatting.RESET + " - monsters\n" + ChatFormatting.GREEN + "green" + ChatFormatting.RESET + " - animals\n" + ChatFormatting.GRAY + "gray" + ChatFormatting.RESET + " - others\n");
        this.entities = new ArrayList<Entity>();
        this.radius = new SliderSetting("Radius", "Radius in blocks.", 100.0, 1.0, 100.0, 1.0, SliderSetting.ValueDisplay.INTEGER);
        this.rotate = new CheckboxSetting("Rotate with player", true);
        this.filterPlayers = new CheckboxSetting("Filter players", "Won't show other players.", false);
        this.filterSleeping = new CheckboxSetting("Filter sleeping", "Won't show sleeping players.", false);
        this.filterMonsters = new CheckboxSetting("Filter monsters", "Won't show zombies, creepers, etc.", false);
        this.filterAnimals = new CheckboxSetting("Filter animals", "Won't show pigs, cows, etc.", false);
        this.filterInvisible = new CheckboxSetting("Filter invisible", "Won't show invisible entities.", false);
        this.setCategory(Category.RENDER);
        this.addSetting(this.radius);
        this.addSetting(this.rotate);
        this.addSetting(this.filterPlayers);
        this.addSetting(this.filterSleeping);
        this.addSetting(this.filterMonsters);
        this.addSetting(this.filterAnimals);
        this.addSetting(this.filterInvisible);
        (this.window = new Window("Radar")).setPinned(true);
        this.window.setInvisible(true);
        this.window.add(new Radar(this));
    }
    
    @Override
    public void onEnable() {
        RadarMod.EVENTS.add(UpdateListener.class, this);
        this.window.setInvisible(false);
    }
    
    @Override
    public void onDisable() {
        RadarMod.EVENTS.remove(UpdateListener.class, this);
        this.window.setInvisible(true);
    }
    
    @Override
    public void onUpdate() {
        final EntityPlayerSP player = RadarMod.MC.player;
        final World world = WMinecraft.getWorld();
        this.entities.clear();
        Stream<Entity> stream = world.loadedEntityList.parallelStream().filter(e -> !e.isDead && e != entityPlayerSP).filter(e -> !(e instanceof EntityFakePlayer)).filter(e -> e instanceof EntityLivingBase).filter(e -> ((EntityLivingBase)e).getHealth() > 0.0f);
        if (this.filterPlayers.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityPlayer));
        }
        if (this.filterSleeping.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityPlayer) || !((EntityPlayer)e).isPlayerSleeping());
        }
        if (this.filterMonsters.isChecked()) {
            stream = stream.filter(e -> !(e instanceof IMob));
        }
        if (this.filterAnimals.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityAnimal) && !(e instanceof EntityAmbientCreature) && !(e instanceof EntityWaterMob));
        }
        if (this.filterInvisible.isChecked()) {
            stream = stream.filter(e -> !e.isInvisible());
        }
        this.entities.addAll(stream.collect((Collector<? super Entity, ?, Collection<? extends Entity>>)Collectors.toList()));
    }
    
    public Window getWindow() {
        return this.window;
    }
    
    public Iterable<Entity> getEntities() {
        return (Iterable<Entity>)Collections.unmodifiableList((List<?>)this.entities);
    }
    
    public double getRadius() {
        return this.radius.getValue();
    }
    
    public boolean isRotateEnabled() {
        return this.rotate.isChecked();
    }
}
