// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import net.wurstclient.ai.PathPos;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.features.commands.PathCmd;
import net.minecraft.util.math.Box;
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
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.util.RotationUtils;
import java.util.stream.Stream;
import java.util.Comparator;
import net.wurstclient.util.EntityFakePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.minecraft.entity.Entity;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses(ghostMode = false)
@DontSaveState
public final class ProtectMod extends Hack implements UpdateListener, RenderListener
{
    private final CheckboxSetting useCooldown;
    private final SliderSetting speed;
    private final SliderSetting range;
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
    private Entity friend;
    private Entity enemy;
    private double distanceF;
    private double distanceE;
    
    public ProtectMod() {
        super("Protect", "A bot that follows the closest entity and protects it from other entities.\nUse .protect <entity> to protect a specific entity instead of the closest one.");
        this.useCooldown = new CheckboxSetting("Use Attack Cooldown as Speed", true) {
            @Override
            public void update() {
                ProtectMod.this.speed.setDisabled(this.isChecked());
            }
        };
        this.speed = new SliderSetting("Speed", "Attack speed (like Killaura)", 12.0, 0.1, 20.0, 0.1, SliderSetting.ValueDisplay.DECIMAL);
        this.range = new SliderSetting("Range", "Attack range (like Killaura)", 6.0, 1.0, 6.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
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
        this.filterCrystals = new CheckboxSetting("Filter end crystals", "Won't attack end crystals.", true);
        this.distanceF = 2.0;
        this.distanceE = 3.0;
        this.setCategory(Category.COMBAT);
        if (this.useCooldown != null) {
            this.addSetting(this.useCooldown);
        }
        this.addSetting(this.speed);
        this.addSetting(this.range);
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
    public String getRenderName() {
        if (this.friend != null) {
            return "Protecting " + this.friend.getName();
        }
        return "Protect";
    }
    
    @Override
    public void onEnable() {
        ProtectMod.WURST.hax.clickAuraMod.setEnabled(false);
        ProtectMod.WURST.hax.fightBotMod.setEnabled(false);
        ProtectMod.WURST.hax.killauraLegitMod.setEnabled(false);
        ProtectMod.WURST.hax.killauraMod.setEnabled(false);
        ProtectMod.WURST.hax.multiAuraMod.setEnabled(false);
        ProtectMod.WURST.hax.tpAuraMod.setEnabled(false);
        ProtectMod.WURST.hax.triggerBotMod.setEnabled(false);
        final Stream<Entity> stream = WMinecraft.getWorld().loadedEntityList.parallelStream().filter(e -> e != null && !e.isDead).filter(e -> e instanceof EntityLivingBase && ((EntityLivingBase)e).getHealth() > 0.0f).filter(e -> e != ProtectMod.MC.player).filter(e -> !(e instanceof EntityFakePlayer));
        this.friend = stream.min(Comparator.comparingDouble(e -> ProtectMod.MC.player.getDistanceSqToEntity(e))).orElse(null);
        this.pathFinder = new EntityPathFinder(this.friend, this.distanceF);
        ProtectMod.EVENTS.add(UpdateListener.class, this);
        ProtectMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        ProtectMod.EVENTS.remove(UpdateListener.class, this);
        ProtectMod.EVENTS.remove(RenderListener.class, this);
        this.pathFinder = null;
        this.processor = null;
        this.ticksProcessing = 0;
        PathProcessor.releaseControls();
        this.enemy = null;
        if (this.friend != null) {
            ProtectMod.MC.gameSettings.keyBindForward.pressed = false;
            this.friend = null;
        }
    }
    
    @Override
    public void onUpdate() {
        this.updateMS();
        if (this.friend == null || this.friend.isDead || !(this.friend instanceof EntityLivingBase) || ((EntityLivingBase)this.friend).getHealth() <= 0.0f || ProtectMod.MC.player.getHealth() <= 0.0f) {
            this.friend = null;
            this.enemy = null;
            this.setEnabled(false);
            return;
        }
        this.enemy = this.findEnemy();
        final Entity target = (this.enemy == null || ProtectMod.MC.player.getDistanceSqToEntity(this.friend) >= 576.0) ? this.friend : this.enemy;
        final double distance = (target == this.enemy) ? this.distanceE : this.distanceF;
        if (this.useAi.isChecked()) {
            if ((this.processor == null || this.processor.isDone() || this.ticksProcessing >= 10 || !this.pathFinder.isPathStillValid(this.processor.getIndex())) && (this.pathFinder.isDone() || this.pathFinder.isFailed())) {
                this.pathFinder = new EntityPathFinder(target, distance);
                this.processor = null;
                this.ticksProcessing = 0;
            }
            if (!this.pathFinder.isDone() && !this.pathFinder.isFailed()) {
                PathProcessor.lockControls();
                RotationUtils.faceEntityClient(target);
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
            if (ProtectMod.MC.player.isCollidedHorizontally && ProtectMod.MC.player.onGround) {
                ProtectMod.MC.player.jump();
            }
            if (ProtectMod.MC.player.isInWater() && ProtectMod.MC.player.posY < target.posY) {
                final EntityPlayerSP player = ProtectMod.MC.player;
                player.motionY += 0.04;
            }
            if (!ProtectMod.MC.player.onGround && (ProtectMod.MC.player.abilities.isFlying || ProtectMod.WURST.hax.flightMod.isActive()) && ProtectMod.MC.player.getDistanceSq(target.posX, ProtectMod.MC.player.posY, target.posZ) <= WMinecraft.getPlayer().getDistanceSq(ProtectMod.MC.player.posX, target.posY, ProtectMod.MC.player.posZ)) {
                if (ProtectMod.MC.player.posY > target.posY + 1.0) {
                    ProtectMod.MC.gameSettings.keyBindSneak.pressed = true;
                }
                else if (ProtectMod.MC.player.posY < target.posY - 1.0) {
                    ProtectMod.MC.gameSettings.keyBindJump.pressed = true;
                }
            }
            else {
                ProtectMod.MC.gameSettings.keyBindSneak.pressed = false;
                ProtectMod.MC.gameSettings.keyBindJump.pressed = false;
            }
            RotationUtils.faceEntityClient(target);
            ProtectMod.MC.gameSettings.keyBindForward.pressed = (ProtectMod.MC.player.getDistanceToEntity(target) > ((target == this.friend) ? this.distanceF : this.distanceE));
        }
        if (target == this.enemy) {
            Label_0706: {
                if (this.useCooldown != null && this.useCooldown.isChecked()) {
                    if (WPlayer.getCooldown() >= 1.0f) {
                        break Label_0706;
                    }
                }
                else if (this.hasTimePassedS(this.speed.getValueF())) {
                    break Label_0706;
                }
                return;
            }
            WPlayer.prepareAttack();
            WPlayer.attackEntity(this.enemy);
            this.updateLastMS();
        }
    }
    
    private Entity findEnemy() {
        final EntityPlayerSP player = WMinecraft.getPlayer();
        final double rangeSq = Math.pow(this.range.getValue(), 2.0);
        Stream<Entity> stream = WMinecraft.getWorld().loadedEntityList.parallelStream().filter(e -> e != null && !e.isDead).filter(e -> (e instanceof EntityLivingBase && ((EntityLivingBase)e).getHealth() > 0.0f) || e instanceof EntityEnderCrystal).filter(e -> entityPlayerSP.getDistanceSqToEntity(e) <= n).filter(e -> e != entityPlayerSP2 && e != this.friend).filter(e -> !(e instanceof EntityFakePlayer)).filter(e -> EntityUtils.isCorrectTeam(e)).filter(e -> WMinecraft.getPlayer().canEntityBeSeen(e)).filter(e -> !ProtectMod.WURST.getFriends().contains(e.getName()));
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
        return stream.min(Comparator.comparingDouble(e -> ProtectMod.MC.player.getDistanceSqToEntity(e))).orElse(null);
    }
    
    @Override
    public void onRender(final float partialTicks) {
        final PathCmd pathCmd = ProtectMod.WURST.commands.pathCmd;
        this.pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
    }
    
    public void setFriend(final Entity friend) {
        this.friend = friend;
    }
    
    private class EntityPathFinder extends PathFinder
    {
        private final Entity entity;
        private double distanceSq;
        
        public EntityPathFinder(final Entity entity, final double distance) {
            super(new BlockPos(entity));
            this.entity = entity;
            this.distanceSq = distance * distance;
            this.setThinkTime(1);
        }
        
        @Override
        protected boolean checkDone() {
            return this.done = (this.entity.getDistanceSqToCenter(this.current) <= this.distanceSq);
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
