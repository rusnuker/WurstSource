// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import net.wurstclient.ai.PathPos;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.features.special_features.YesCheatSpf;
import net.wurstclient.features.commands.PathCmd;
import net.minecraft.util.math.Box;
import java.util.stream.Stream;
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
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.util.RotationUtils;
import net.minecraft.entity.Entity;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "fight bot" })
@Bypasses(ghostMode = false)
@DontSaveState
public final class FightBotMod extends Hack implements UpdateListener, RenderListener
{
    private final CheckboxSetting useCooldown;
    private final SliderSetting speed;
    private final SliderSetting range;
    private final CheckboxSetting checkLOS;
    private final SliderSetting distance;
    private final CheckboxSetting useAi;
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
    private EntityPathFinder pathFinder;
    private PathProcessor processor;
    private int ticksProcessing;
    
    public FightBotMod() {
        super("FightBot", "A bot that automatically walks around and kills everything.\nGood for MobArena.");
        this.useCooldown = new CheckboxSetting("Use Attack Cooldown as Speed", true) {
            @Override
            public void update() {
                FightBotMod.this.speed.setDisabled(this.isChecked());
            }
        };
        this.speed = new SliderSetting("Speed", "Attack speed (like Killaura)", 12.0, 0.1, 20.0, 0.1, SliderSetting.ValueDisplay.DECIMAL);
        this.range = new SliderSetting("Range", "Attack range (like Killaura)", 4.25, 1.0, 6.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.checkLOS = new CheckboxSetting("Check line of sight", true);
        this.distance = new SliderSetting("Distance", "How closely to follow the target.\nThis should be set to a lower value than Range.", 3.0, 1.0, 6.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.useAi = new CheckboxSetting("Use AI (experimental)", false);
        this.filterPlayers = new CheckboxSetting("Filter players", "Won't attack other players.", false);
        this.filterSleeping = new CheckboxSetting("Filter sleeping", "Won't attack sleeping players.", false);
        this.filterFlying = new SliderSetting("Filter flying", "Won't attack players that\nare at least the given\ndistance above ground.", 0.0, 0.0, 2.0, 0.05, v -> (v == 0.0) ? "off" : SliderSetting.ValueDisplay.DECIMAL.getValueString(v));
        this.filterMonsters = new CheckboxSetting("Filter monsters", "Won't attack zombies, creepers, etc.", false);
        this.filterPigmen = new CheckboxSetting("Filter pigmen", "Won't attack zombie pigmen.", false);
        this.filterEndermen = new CheckboxSetting("Filter endermen", "Won't attack endermen.", false);
        this.filterAnimals = new CheckboxSetting("Filter animals", "Won't attack pigs, cows, etc.", false);
        this.filterBabies = new CheckboxSetting("Filter babies", "Won't attack baby pigs,\nbaby villagers, etc.", false);
        this.filterPets = new CheckboxSetting("Filter pets", "Won't attack tamed wolves,\ntamed horses, etc.", false);
        this.filterTraders = new CheckboxSetting("Filter traders", "Won't attack villagers, wandering traders, etc.", false);
        this.filterGolems = new CheckboxSetting("Filter golems", "Won't attack iron golems,\nsnow golems and shulkers.", false);
        this.filterInvisible = new CheckboxSetting("Filter invisible", "Won't attack invisible entities.", false);
        this.filterNamed = new CheckboxSetting("Filter named", "Won't attack name-tagged entities.", false);
        this.filterStands = new CheckboxSetting("Filter armor stands", "Won't attack armor stands.", false);
        this.filterCrystals = new CheckboxSetting("Filter end crystals", "Won't attack end crystals.", false);
        this.setCategory(Category.COMBAT);
        if (this.useCooldown != null) {
            this.addSetting(this.useCooldown);
        }
        this.addSetting(this.speed);
        this.addSetting(this.range);
        this.addSetting(this.checkLOS);
        this.addSetting(this.distance);
        this.addSetting(this.useAi);
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
        return new Feature[] { FightBotMod.WURST.hax.followMod, FightBotMod.WURST.hax.protectMod, FightBotMod.WURST.special.targetSpf, FightBotMod.WURST.special.yesCheatSpf };
    }
    
    @Override
    public void onEnable() {
        FightBotMod.WURST.hax.clickAuraMod.setEnabled(false);
        FightBotMod.WURST.hax.killauraLegitMod.setEnabled(false);
        FightBotMod.WURST.hax.killauraMod.setEnabled(false);
        FightBotMod.WURST.hax.multiAuraMod.setEnabled(false);
        FightBotMod.WURST.hax.protectMod.setEnabled(false);
        FightBotMod.WURST.hax.tpAuraMod.setEnabled(false);
        FightBotMod.WURST.hax.triggerBotMod.setEnabled(false);
        this.pathFinder = new EntityPathFinder(FightBotMod.MC.player);
        FightBotMod.EVENTS.add(UpdateListener.class, this);
        FightBotMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        FightBotMod.EVENTS.remove(UpdateListener.class, this);
        FightBotMod.EVENTS.remove(RenderListener.class, this);
        this.pathFinder = null;
        this.processor = null;
        this.ticksProcessing = 0;
        PathProcessor.releaseControls();
    }
    
    @Override
    public void onUpdate() {
        this.updateMS();
        final Entity entity = this.findTarget();
        if (entity == null) {
            return;
        }
        if (this.useAi.isChecked()) {
            if ((this.processor == null || this.processor.isDone() || this.ticksProcessing >= 10 || !this.pathFinder.isPathStillValid(this.processor.getIndex())) && (this.pathFinder.isDone() || this.pathFinder.isFailed())) {
                this.pathFinder = new EntityPathFinder(entity);
                this.processor = null;
                this.ticksProcessing = 0;
            }
            if (!this.pathFinder.isDone() && !this.pathFinder.isFailed()) {
                PathProcessor.lockControls();
                RotationUtils.faceEntityClient(entity);
                this.pathFinder.think();
                this.pathFinder.formatPath();
                this.processor = this.pathFinder.getProcessor();
            }
            if (!this.processor.isDone()) {
                this.processor.process();
                ++this.ticksProcessing;
            }
        }
        else {
            if (FightBotMod.MC.player.isCollidedHorizontally && FightBotMod.MC.player.onGround) {
                FightBotMod.MC.player.jump();
            }
            if (FightBotMod.MC.player.isInWater() && FightBotMod.MC.player.posY < entity.posY) {
                final EntityPlayerSP player = FightBotMod.MC.player;
                player.motionY += 0.04;
            }
            if (!FightBotMod.MC.player.onGround && (FightBotMod.MC.player.abilities.isFlying || FightBotMod.WURST.hax.flightMod.isActive()) && FightBotMod.MC.player.getDistanceSq(entity.posX, FightBotMod.MC.player.posY, entity.posZ) <= WMinecraft.getPlayer().getDistanceSq(FightBotMod.MC.player.posX, entity.posY, FightBotMod.MC.player.posZ)) {
                if (FightBotMod.MC.player.posY > entity.posY + 1.0) {
                    FightBotMod.MC.gameSettings.keyBindSneak.pressed = true;
                }
                else if (FightBotMod.MC.player.posY < entity.posY - 1.0) {
                    FightBotMod.MC.gameSettings.keyBindJump.pressed = true;
                }
            }
            else {
                FightBotMod.MC.gameSettings.keyBindSneak.pressed = false;
                FightBotMod.MC.gameSettings.keyBindJump.pressed = false;
            }
            FightBotMod.MC.gameSettings.keyBindForward.pressed = (FightBotMod.MC.player.getDistanceToEntity(entity) > this.distance.getValueF());
            if (!RotationUtils.faceEntityClient(entity)) {
                return;
            }
        }
        Label_0558: {
            if (this.useCooldown != null && this.useCooldown.isChecked()) {
                if (WPlayer.getCooldown() >= 1.0f) {
                    break Label_0558;
                }
            }
            else if (this.hasTimePassedS(this.speed.getValueF())) {
                break Label_0558;
            }
            return;
        }
        if (FightBotMod.MC.player.getDistanceSqToEntity(entity) > Math.pow(this.range.getValue(), 2.0)) {
            return;
        }
        WPlayer.prepareAttack();
        WPlayer.attackEntity(entity);
        this.updateLastMS();
    }
    
    private Entity findTarget() {
        final EntityPlayerSP player = WMinecraft.getPlayer();
        Stream<Entity> stream = WMinecraft.getWorld().loadedEntityList.parallelStream().filter(e -> e != null && !e.isDead).filter(e -> (e instanceof EntityLivingBase && ((EntityLivingBase)e).getHealth() > 0.0f) || e instanceof EntityEnderCrystal).filter(e -> e != entityPlayerSP).filter(e -> !(e instanceof EntityFakePlayer)).filter(e -> EntityUtils.isCorrectTeam(e)).filter(e -> !FightBotMod.WURST.getFriends().contains(e.getName()));
        if (this.checkLOS.isChecked()) {
            stream = stream.filter(e -> WMinecraft.getPlayer().canEntityBeSeen(e));
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
        return stream.min(Comparator.comparingDouble(e -> FightBotMod.MC.player.getDistanceSqToEntity(e))).orElse(null);
    }
    
    @Override
    public void onRender(final float partialTicks) {
        final PathCmd pathCmd = FightBotMod.WURST.commands.pathCmd;
        this.pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
    }
    
    @Override
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
        switch (profile) {
            default: {
                this.speed.resetUsableMax();
                this.range.resetUsableMax();
                this.distance.resetUsableMax();
                break;
            }
            case ANTICHEAT:
            case OLDER_NCP:
            case LATEST_NCP:
            case GHOST_MODE: {
                this.speed.setUsableMax(12.0);
                this.range.setUsableMax(4.25);
                this.distance.setUsableMax(4.25);
                break;
            }
        }
    }
    
    private class EntityPathFinder extends PathFinder
    {
        private final Entity entity;
        
        public EntityPathFinder(final Entity entity) {
            super(new BlockPos(entity));
            this.entity = entity;
            this.setThinkTime(1);
        }
        
        @Override
        protected boolean checkDone() {
            return this.done = (this.entity.getDistanceSqToCenter(this.current) <= Math.pow(FightBotMod.this.distance.getValue(), 2.0));
        }
        
        @Override
        public ArrayList<PathPos> formatPath() {
            if (!this.done) {
                this.failed = true;
            }
            return super.formatPath();
        }
    }
}
