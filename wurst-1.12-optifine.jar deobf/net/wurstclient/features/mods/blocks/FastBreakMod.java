// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "FastMine", "SpeedMine", "SpeedyGonzales", "fast break", "fast mine", "speed mine", "speedy gonzales", "NoBreakDelay", "no break delay" })
@Bypasses
public final class FastBreakMod extends Hack
{
    private final EnumSetting<Mode> mode;
    private final SliderSetting speed;
    
    public FastBreakMod() {
        super("FastBreak", "Allows you to break blocks faster.\nTip: This works with Nuker.");
        this.mode = new EnumSetting<Mode>("Mode", Mode.values(), Mode.INSTANT);
        this.speed = new SliderSetting("Speed", "Only works in §lNormal§r mode.", 2.0, 1.0, 5.0, 0.05, SliderSetting.ValueDisplay.DECIMAL);
        this.setCategory(Category.BLOCKS);
        this.addSetting(this.mode);
        this.addSetting(this.speed);
    }
    
    @Override
    public String getRenderName() {
        switch (this.mode.getSelected()) {
            case NORMAL: {
                return String.valueOf(this.getName()) + " [" + this.speed.getValueString() + "x]";
            }
            default: {
                return String.valueOf(this.getName()) + " [" + this.mode.getSelected() + "]";
            }
        }
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { FastBreakMod.WURST.hax.fastPlaceMod, FastBreakMod.WURST.hax.autoMineMod, FastBreakMod.WURST.hax.nukerMod };
    }
    
    public float getHardnessModifier() {
        if (!this.isActive()) {
            return 1.0f;
        }
        if (this.mode.getSelected() != Mode.NORMAL) {
            return 1.0f;
        }
        return this.speed.getValueF();
    }
    
    public boolean shouldSpamPackets() {
        return this.isActive() && this.mode.getSelected() == Mode.INSTANT;
    }
    
    private enum Mode
    {
        NORMAL("NORMAL", 0, "Normal"), 
        INSTANT("INSTANT", 1, "Instant"), 
        LEGIT("LEGIT", 2, "Legit");
        
        private final String name;
        
        private Mode(final String name2, final int ordinal, final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
