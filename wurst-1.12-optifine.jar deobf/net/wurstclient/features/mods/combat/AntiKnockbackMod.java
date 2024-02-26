// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "AntiVelocity", "NoKnockback", "AntiKB", "anti knockback", "anti velocity", "no knockback", "anti kb" })
@Bypasses(ghostMode = false)
public final class AntiKnockbackMod extends Hack
{
    private final SliderSetting hStrength;
    private final SliderSetting vStrength;
    
    public AntiKnockbackMod() {
        super("AntiKnockback", "Prevents you from getting pushed by players and mobs.");
        this.hStrength = new SliderSetting("Horizontal Strength", "How far to reduce horizontal knockback.\n100% = no knockback", 1.0, 0.01, 1.0, 0.01, SliderSetting.ValueDisplay.PERCENTAGE);
        this.vStrength = new SliderSetting("Vertical Strength", "How far to reduce vertical knockback.\n100% = no knockback", 1.0, 0.01, 1.0, 0.01, SliderSetting.ValueDisplay.PERCENTAGE);
        this.setCategory(Category.COMBAT);
        this.addSetting(this.hStrength);
        this.addSetting(this.vStrength);
    }
    
    public float getHorizontalModifier() {
        return this.isActive() ? (1.0f - this.hStrength.getValueF()) : 1.0f;
    }
    
    public float getVerticalModifier() {
        return this.isActive() ? (1.0f - this.vStrength.getValueF()) : 1.0f;
    }
}
