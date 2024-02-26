// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.minecraft.client.settings.GameSettings;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "NightVision", "full bright", "brightness", "night vision" })
public final class FullbrightMod extends Hack implements UpdateListener
{
    private final CheckboxSetting fade;
    private final CheckboxSetting shaderMode;
    private boolean wasShaderMode;
    
    public FullbrightMod() {
        super("Fullbright", "Allows you to see in the dark.");
        this.fade = new CheckboxSetting("Fade", "Slowly fades between brightness and darkness.", true);
        this.shaderMode = new CheckboxSetting("Shader compatibility mode", false);
        this.setCategory(Category.RENDER);
        this.addSetting(this.fade);
        if (this.shaderMode != null) {
            this.addSetting(this.shaderMode);
        }
        FullbrightMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onEnable() {
        if (this.shaderMode != null) {
            if (this.shaderMode.isChecked()) {
                FullbrightMod.MC.renderGlobal.loadRenderers();
            }
            this.wasShaderMode = this.shaderMode.isChecked();
        }
    }
    
    @Override
    public void onDisable() {
        if (this.shaderMode != null && this.shaderMode.isChecked()) {
            FullbrightMod.MC.renderGlobal.loadRenderers();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.shaderMode != null && this.isActive()) {
            final boolean isShaderMode = this.shaderMode.isChecked();
            if (isShaderMode != this.wasShaderMode) {
                FullbrightMod.MC.renderGlobal.loadRenderers();
                this.wasShaderMode = isShaderMode;
            }
        }
        if (this.isActive()) {
            this.approachGamma(16.0f);
        }
        else {
            this.approachGamma(0.5f);
        }
    }
    
    private void approachGamma(final float target) {
        final GameSettings settings = FullbrightMod.MC.gameSettings;
        if (!this.fade.isChecked() || FullbrightMod.WURST.hax.panicMod.isActive() || Math.abs(settings.gammaSetting - target) <= 0.5) {
            settings.gammaSetting = target;
            return;
        }
        if (settings.gammaSetting < target) {
            final GameSettings gameSettings = settings;
            gameSettings.gammaSetting += 0.5;
        }
        else {
            final GameSettings gameSettings2 = settings;
            gameSettings2.gammaSetting -= 0.5;
        }
    }
    
    public boolean useShaderMode() {
        return this.isActive() && this.shaderMode != null && this.shaderMode.isChecked();
    }
}
