// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.fun;

import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.compatibility.WPotionEffects;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.wurstclient.Category;
import net.wurstclient.features.DontSaveState;
import net.wurstclient.features.Bypasses;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@Bypasses
@DontSaveState
public final class LsdMod extends Hack implements UpdateListener
{
    public LsdMod() {
        super("LSD", "Causes hallucinations.");
        this.setCategory(Category.FUN);
    }
    
    @Override
    public void onEnable() {
        if (!OpenGlHelper.shadersSupported) {
            LsdMod.EVENTS.add(UpdateListener.class, this);
            return;
        }
        if (!(LsdMod.MC.getRenderViewEntity() instanceof EntityPlayer)) {
            this.setEnabled(false);
            return;
        }
        if (LsdMod.MC.entityRenderer.theShaderGroup != null) {
            LsdMod.MC.entityRenderer.theShaderGroup.deleteShaderGroup();
        }
        LsdMod.MC.entityRenderer.shaderIndex = 19;
        LsdMod.MC.entityRenderer.loadShader(EntityRenderer.SHADERS_TEXTURES[19]);
    }
    
    @Override
    public void onDisable() {
        if (!OpenGlHelper.shadersSupported) {
            LsdMod.EVENTS.remove(UpdateListener.class, this);
            WPlayer.removePotionEffect(WPotionEffects.NAUSEA);
            return;
        }
        if (LsdMod.MC.entityRenderer.theShaderGroup != null) {
            LsdMod.MC.entityRenderer.theShaderGroup.deleteShaderGroup();
            LsdMod.MC.entityRenderer.theShaderGroup = null;
        }
    }
    
    @Override
    public void onUpdate() {
        if (!OpenGlHelper.shadersSupported) {
            WPlayer.addPotionEffect(WPotionEffects.NAUSEA);
        }
    }
}
