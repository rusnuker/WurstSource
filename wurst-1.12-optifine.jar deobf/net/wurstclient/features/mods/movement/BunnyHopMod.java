// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.movement;

import java.util.function.Predicate;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "AutoJump", "BHop", "bunny hop", "auto jump" })
@Bypasses
public final class BunnyHopMod extends Hack implements UpdateListener
{
    private final EnumSetting<JumpIf> jumpIf;
    
    public BunnyHopMod() {
        super("BunnyHop", "Makes you jump automatically.");
        this.jumpIf = new EnumSetting<JumpIf>("Jump if", JumpIf.values(), JumpIf.SPRINTING);
        this.setCategory(Category.MOVEMENT);
        this.addSetting(this.jumpIf);
    }
    
    @Override
    public String getRenderName() {
        return String.valueOf(this.getName()) + " [" + this.jumpIf.getSelected().name + "]";
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { BunnyHopMod.WURST.hax.autoSprintMod };
    }
    
    @Override
    public void onEnable() {
        BunnyHopMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        BunnyHopMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        final EntityPlayerSP player = BunnyHopMod.MC.player;
        if (!player.onGround || player.isSneaking()) {
            return;
        }
        if (this.jumpIf.getSelected().condition.test(player)) {
            player.jump();
        }
    }
    
    private enum JumpIf
    {
        SPRINTING("Sprinting", p -> p.isSprinting() && (p.moveForward != 0.0f || p.moveStrafing != 0.0f)), 
        WALKING("Walking", p -> p.moveForward != 0.0f || p.moveStrafing != 0.0f), 
        ALWAYS("Always", p -> true);
        
        private final String name;
        private final Predicate<EntityPlayerSP> condition;
        
        private JumpIf(final String name, final Predicate<EntityPlayerSP> condition) {
            this.name = name;
            this.condition = condition;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
