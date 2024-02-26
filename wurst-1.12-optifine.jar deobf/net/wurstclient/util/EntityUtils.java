// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.util.math.Box;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.wurstclient.compatibility.WWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WMinecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

public class EntityUtils
{
    public static final TargetSettings DEFAULT_SETTINGS;
    
    static {
        DEFAULT_SETTINGS = new TargetSettings();
    }
    
    public static boolean isCorrectEntity(final Entity en, final TargetSettings settings) {
        if (en == null) {
            return false;
        }
        if (en instanceof EntityLivingBase && (((EntityLivingBase)en).isDead || ((EntityLivingBase)en).getHealth() <= 0.0f)) {
            return false;
        }
        if (WMinecraft.getPlayer().getDistanceToEntity(en) > settings.getRange()) {
            return false;
        }
        if (settings.getFOV() < 360.0f && RotationUtils.getAngleToClientRotation(en.boundingBox.getCenter()) > settings.getFOV() / 2.0f) {
            return false;
        }
        if (!settings.targetBehindWalls() && !WMinecraft.getPlayer().canEntityBeSeen(en)) {
            return false;
        }
        if (!settings.targetFriends() && WurstClient.INSTANCE.friends.contains(en.getName())) {
            return false;
        }
        if (en instanceof EntityPlayer) {
            if (!settings.targetPlayers()) {
                if (!((EntityPlayer)en).isPlayerSleeping() && !en.isInvisible()) {
                    return false;
                }
            }
            else if (!settings.targetSleepingPlayers()) {
                if (((EntityPlayer)en).isPlayerSleeping()) {
                    return false;
                }
            }
            else if (!settings.targetInvisiblePlayers() && en.isInvisible()) {
                return false;
            }
            final double filterFlying = settings.getFilterFlying();
            if (filterFlying > 0.0) {
                Box box = en.getEntityBoundingBox();
                box = box.union(box.offset(0.0, -filterFlying, 0.0));
                if (!WWorld.collidesWithAnyBlock(box)) {
                    return false;
                }
            }
            if (settings.targetTeams() && !checkName(((EntityPlayer)en).getDisplayName().getFormattedText(), settings.getTeamColors())) {
                return false;
            }
            if (en == WMinecraft.getPlayer()) {
                return false;
            }
            if (((EntityPlayer)en).getName().equals(WMinecraft.getPlayer().getName())) {
                return false;
            }
        }
        else {
            if (!(en instanceof EntityLiving)) {
                return false;
            }
            if (en.isInvisible()) {
                if (!settings.targetInvisibleMobs()) {
                    return false;
                }
            }
            else if (en instanceof EntityAgeable || en instanceof EntityAmbientCreature || en instanceof EntityWaterMob) {
                if (!settings.targetAnimals()) {
                    return false;
                }
            }
            else if (en instanceof EntityMob || en instanceof EntitySlime || en instanceof EntityFlying) {
                if (!settings.targetMonsters()) {
                    return false;
                }
            }
            else {
                if (!(en instanceof EntityGolem)) {
                    return false;
                }
                if (!settings.targetGolems()) {
                    return false;
                }
            }
            if (settings.targetTeams() && en.hasCustomName() && !checkName(en.getCustomNameTag(), settings.getTeamColors())) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isCorrectTeam(final Entity e) {
        return !EntityUtils.DEFAULT_SETTINGS.targetTeams() || !(e instanceof EntityLiving) || !e.hasCustomName() || checkName(e.getCustomNameTag(), EntityUtils.DEFAULT_SETTINGS.getTeamColors());
    }
    
    private static boolean checkName(final String name, final boolean[] teamColors) {
        final String[] colors = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
        boolean hasKnownColor = false;
        for (int i = 0; i < 16; ++i) {
            if (name.contains("§" + colors[i])) {
                hasKnownColor = true;
                if (teamColors[i]) {
                    return true;
                }
            }
        }
        return !hasKnownColor && teamColors[15];
    }
    
    public static ArrayList<Entity> getValidEntities(final TargetSettings settings) {
        final ArrayList<Entity> validEntities = new ArrayList<Entity>();
        for (final Entity entity : WMinecraft.getWorld().loadedEntityList) {
            if (isCorrectEntity(entity, settings)) {
                validEntities.add(entity);
            }
            if (validEntities.size() >= 64) {
                break;
            }
        }
        return validEntities;
    }
    
    public static Entity getClosestEntity(final TargetSettings settings) {
        Entity closestEntity = null;
        for (final Entity entity : WMinecraft.getWorld().loadedEntityList) {
            if (isCorrectEntity(entity, settings) && (closestEntity == null || WMinecraft.getPlayer().getDistanceToEntity(entity) < WMinecraft.getPlayer().getDistanceToEntity(closestEntity))) {
                closestEntity = entity;
            }
        }
        return closestEntity;
    }
    
    public static Entity getBestEntityToAttack(final TargetSettings settings) {
        Entity bestEntity = null;
        float bestAngle = Float.POSITIVE_INFINITY;
        for (final Entity entity : WMinecraft.getWorld().loadedEntityList) {
            if (!isCorrectEntity(entity, settings)) {
                continue;
            }
            final float angle = RotationUtils.getAngleToServerRotation(entity.boundingBox.getCenter());
            if (angle >= bestAngle) {
                continue;
            }
            bestEntity = entity;
            bestAngle = angle;
        }
        return bestEntity;
    }
    
    public static Entity getClosestEntityOtherThan(final Entity otherEntity, final TargetSettings settings) {
        Entity closestEnemy = null;
        for (final Entity entity : WMinecraft.getWorld().loadedEntityList) {
            if (isCorrectEntity(entity, settings) && entity != otherEntity && (closestEnemy == null || WMinecraft.getPlayer().getDistanceToEntity(entity) < WMinecraft.getPlayer().getDistanceToEntity(closestEnemy))) {
                closestEnemy = entity;
            }
        }
        return closestEnemy;
    }
    
    public static Entity getClosestEntityWithName(final String name, final TargetSettings settings) {
        Entity closestEntity = null;
        for (final Entity entity : WMinecraft.getWorld().loadedEntityList) {
            if (!isCorrectEntity(entity, settings)) {
                continue;
            }
            if (!entity.getName().equalsIgnoreCase(name)) {
                continue;
            }
            if (closestEntity != null && WMinecraft.getPlayer().getDistanceSqToEntity(entity) >= WMinecraft.getPlayer().getDistanceSqToEntity(closestEntity)) {
                continue;
            }
            closestEntity = entity;
        }
        return closestEntity;
    }
    
    public static class TargetSettings
    {
        public boolean targetFriends() {
            return false;
        }
        
        public boolean targetBehindWalls() {
            return false;
        }
        
        public float getRange() {
            return Float.POSITIVE_INFINITY;
        }
        
        public float getFOV() {
            return 360.0f;
        }
        
        public boolean targetPlayers() {
            return WurstClient.INSTANCE.special.targetSpf.players.isChecked();
        }
        
        public boolean targetAnimals() {
            return WurstClient.INSTANCE.special.targetSpf.animals.isChecked();
        }
        
        public boolean targetMonsters() {
            return WurstClient.INSTANCE.special.targetSpf.monsters.isChecked();
        }
        
        public boolean targetGolems() {
            return WurstClient.INSTANCE.special.targetSpf.golems.isChecked();
        }
        
        public boolean targetSleepingPlayers() {
            return WurstClient.INSTANCE.special.targetSpf.sleepingPlayers.isChecked();
        }
        
        public boolean targetInvisiblePlayers() {
            return WurstClient.INSTANCE.special.targetSpf.invisiblePlayers.isChecked();
        }
        
        public boolean targetInvisibleMobs() {
            return WurstClient.INSTANCE.special.targetSpf.invisibleMobs.isChecked();
        }
        
        public double getFilterFlying() {
            return 0.0;
        }
        
        public boolean targetTeams() {
            return WurstClient.INSTANCE.special.targetSpf.teams.isChecked();
        }
        
        public boolean[] getTeamColors() {
            return WurstClient.INSTANCE.special.targetSpf.teamColors.getSelected();
        }
    }
}
