// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import java.util.function.ToDoubleFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Box;
import java.util.stream.Stream;
import net.minecraft.client.entity.EntityPlayerSP;
import java.util.Comparator;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.wurstclient.compatibility.WWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.wurstclient.util.EntityUtils;
import net.wurstclient.util.EntityFakePlayer;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.EntityLivingBase;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.entity.Entity;
import net.wurstclient.util.RotationUtils;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "LegitAura", "killaura legit", "kill aura legit", "legit aura" })
public final class KillauraLegitMod extends Hack implements UpdateListener
{
    private final CheckboxSetting useCooldown;
    private final SliderSetting speed;
    private final SliderSetting range;
    private final EnumSetting<Priority> priority;
    private final SliderSetting fov;
    private final CheckboxSetting filterPlayers;
    private final CheckboxSetting filterSleeping;
    private final SliderSetting filterFlying;
    private final CheckboxSetting filterMonsters;
    private final CheckboxSetting filterPigmen;
    private final CheckboxSetting filterEndermen;
    private final CheckboxSetting filterAnimals;
    private final CheckboxSetting filterBabies;
    private final CheckboxSetting filterPets;
    private final CheckboxSetting filterTraders;
    private final CheckboxSetting filterGolems;
    private final CheckboxSetting filterInvisible;
    private final CheckboxSetting filterNamed;
    private final CheckboxSetting filterStands;
    private final CheckboxSetting filterCrystals;
    
    public KillauraLegitMod() {
        super("KillauraLegit", "Slower Killaura that is harder to detect.\nNot required on normal NoCheat+ servers!");
        this.useCooldown = new CheckboxSetting("Use Attack Cooldown as Speed", true) {
            @Override
            public void update() {
                KillauraLegitMod.this.speed.setDisabled(this.isChecked());
            }
        };
        this.speed = new SliderSetting("Speed", 12.0, 0.1, 12.0, 0.1, SliderSetting.ValueDisplay.DECIMAL);
        this.range = new SliderSetting("Range", 4.25, 1.0, 4.25, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.priority = new EnumSetting<Priority>("Priority", "Determines which entity will be attacked first.\n�lDistance�r - Attacks the closest entity.\n�lAngle�r - Attacks the entity that requires\nthe least head movement.\n�lHealth�r - Attacks the weakest entity.", Priority.values(), Priority.ANGLE);
        this.fov = new SliderSetting("FOV", 360.0, 30.0, 360.0, 10.0, SliderSetting.ValueDisplay.DEGREES);
        this.filterPlayers = new CheckboxSetting("Filter players", "Won't attack other players.", false);
        this.filterSleeping = new CheckboxSetting("Filter sleeping", "Won't attack sleeping players.", true);
        this.filterFlying = new SliderSetting("Filter flying", "Won't attack players that\nare at least the given\ndistance above ground.", 0.5, 0.0, 2.0, 0.05, v -> (v == 0.0) ? "off" : SliderSetting.ValueDisplay.DECIMAL.getValueString(v));
        this.filterMonsters = new CheckboxSetting("Filter monsters", "Won't attack zombies, creepers, etc.", false);
        this.filterPigmen = new CheckboxSetting("Filter pigmen", "Won't attack zombie pigmen.", false);
        this.filterEndermen = new CheckboxSetting("Filter endermen", "Won't attack endermen.", false);
        this.filterAnimals = new CheckboxSetting("Filter animals", "Won't attack pigs, cows, etc.", false);
        this.filterBabies = new CheckboxSetting("Filter babies", "Won't attack baby pigs,\nbaby villagers, etc.", false);
        this.filterPets = new CheckboxSetting("Filter pets", "Won't attack tamed wolves,\ntamed horses, etc.", false);
        this.filterTraders = new CheckboxSetting("Filter traders", "Won't attack villagers, wandering traders, etc.", false);
        this.filterGolems = new CheckboxSetting("Filter golems", "Won't attack iron golems,\nsnow golems and shulkers.", false);
        this.filterInvisible = new CheckboxSetting("Filter invisible", "Won't attack invisible entities.", true);
        this.filterNamed = new CheckboxSetting("Filter named", "Won't attack name-tagged entities.", false);
        this.filterStands = new CheckboxSetting("Filter armor stands", "Won't attack armor stands.", false);
        this.filterCrystals = new CheckboxSetting("Filter end crystals", "Won't attack end crystals.", false);
        this.setCategory(Category.COMBAT);
        if (this.useCooldown != null) {
            this.addSetting(this.useCooldown);
        }
        this.addSetting(this.speed);
        this.addSetting(this.range);
        this.addSetting(this.priority);
        this.addSetting(this.fov);
        this.addSetting(this.filterPlayers);
        this.addSetting(this.filterSleeping);
        this.addSetting(this.filterFlying);
        this.addSetting(this.filterMonsters);
        this.addSetting(this.filterPigmen);
        this.addSetting(this.filterEndermen);
        this.addSetting(this.filterAnimals);
        this.addSetting(this.filterBabies);
        this.addSetting(this.filterPets);
        this.addSetting(this.filterTraders);
        this.addSetting(this.filterGolems);
        this.addSetting(this.filterInvisible);
        this.addSetting(this.filterNamed);
        this.addSetting(this.filterStands);
        this.addSetting(this.filterCrystals);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { KillauraLegitMod.WURST.hax.killauraMod, KillauraLegitMod.WURST.special.targetSpf };
    }
    
    @Override
    public void onEnable() {
        KillauraLegitMod.WURST.hax.clickAuraMod.setEnabled(false);
        KillauraLegitMod.WURST.hax.fightBotMod.setEnabled(false);
        KillauraLegitMod.WURST.hax.killauraMod.setEnabled(false);
        KillauraLegitMod.WURST.hax.multiAuraMod.setEnabled(false);
        KillauraLegitMod.WURST.hax.protectMod.setEnabled(false);
        KillauraLegitMod.WURST.hax.tpAuraMod.setEnabled(false);
        KillauraLegitMod.WURST.hax.triggerBotMod.setEnabled(false);
        KillauraLegitMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        KillauraLegitMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        this.updateMS();
        Label_0047: {
            if (this.useCooldown != null && this.useCooldown.isChecked()) {
                if (WPlayer.getCooldown() >= 1.0f) {
                    break Label_0047;
                }
            }
            else if (this.hasTimePassedS(this.speed.getValueF())) {
                break Label_0047;
            }
            return;
        }
        final Entity entity = this.findTarget();
        if (entity == null) {
            return;
        }
        if (!RotationUtils.faceEntityClient(entity)) {
            return;
        }
        if (KillauraLegitMod.WURST.hax.criticalsMod.isActive() && KillauraLegitMod.MC.player.onGround) {
            KillauraLegitMod.MC.player.jump();
        }
        WPlayer.attackEntity(entity);
        this.updateLastMS();
    }
    
    private Entity findTarget() {
        final EntityPlayerSP player = WMinecraft.getPlayer();
        final double rangeSq = Math.pow(this.range.getValue(), 2.0);
        Stream<Entity> stream = WMinecraft.getWorld().loadedEntityList.parallelStream().filter(e -> e != null && !e.isDead).filter(e -> (e instanceof EntityLivingBase && ((EntityLivingBase)e).getHealth() > 0.0f) || e instanceof EntityEnderCrystal).filter(e -> entityPlayerSP.getDistanceSqToEntity(e) <= n).filter(e -> e != entityPlayerSP2).filter(e -> !(e instanceof EntityFakePlayer)).filter(e -> EntityUtils.isCorrectTeam(e)).filter(e -> WMinecraft.getPlayer().canEntityBeSeen(e)).filter(e -> !KillauraLegitMod.WURST.getFriends().contains(e.getName()));
        if (this.fov.getValue() < 360.0) {
            stream = stream.filter(e -> RotationUtils.getAngleToClientRotation(e.boundingBox.getCenter()) <= this.fov.getValueF() / 2.0f);
        }
        if (this.filterPlayers.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityPlayer));
        }
        if (this.filterSleeping.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityPlayer) || !((EntityPlayer)e).isPlayerSleeping());
        }
        if (this.filterFlying.getValue() > 0.0) {
            stream = stream.filter(e -> {
                if (!(e instanceof EntityPlayer)) {
                    return true;
                }
                else {
                    final Box box = e.getEntityBoundingBox();
                    final Box box2 = box.union(box.offset(0.0, -this.filterFlying.getValue(), 0.0));
                    return WWorld.collidesWithAnyBlock(box2);
                }
            });
        }
        if (this.filterMonsters.isChecked()) {
            stream = stream.filter(e -> !(e instanceof IMob));
        }
        if (this.filterPigmen.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityPigZombie));
        }
        if (this.filterEndermen.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityEnderman));
        }
        if (this.filterAnimals.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityAgeable) && !(e instanceof EntityAmbientCreature) && !(e instanceof EntityWaterMob));
        }
        if (this.filterBabies.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityAgeable) || !((EntityAgeable)e).isChild());
        }
        if (this.filterPets.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityTameable) || !((EntityTameable)e).isTamed()).filter(e -> !(e instanceof AbstractHorse) || !((AbstractHorse)e).isTame());
        }
        if (this.filterTraders.isChecked()) {
            stream = stream.filter(e -> !(e instanceof IMerchant));
        }
        if (this.filterGolems.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityGolem));
        }
        if (this.filterInvisible.isChecked()) {
            stream = stream.filter(e -> !e.isInvisible());
        }
        if (this.filterNamed.isChecked()) {
            stream = stream.filter(e -> !e.hasCustomName());
        }
        if (this.filterStands.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityArmorStand));
        }
        if (this.filterCrystals.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityEnderCrystal));
        }
        return stream.min(this.priority.getSelected().comparator).orElse(null);
    }
    
    private enum Priority
    {
        DISTANCE("Distance", e -> KillauraLegitMod.MC.player.getDistanceSqToEntity(e)), 
        ANGLE("Angle", e -> RotationUtils.getAngleToServerRotation(e.getEntityBoundingBox().getCenter())), 
        HEALTH("Health", e -> (e instanceof EntityLivingBase) ? ((EntityLivingBase)e).getHealth() : Double.POSITIVE_INFINITY);
        
        private final String name;
        private final Comparator<Entity> comparator;
        
        private Priority(final String name, final ToDoubleFunction<Entity> keyExtractor) {
            this.name = name;
            this.comparator = Comparator.comparingDouble((ToDoubleFunction<? super Entity>)keyExtractor);
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
