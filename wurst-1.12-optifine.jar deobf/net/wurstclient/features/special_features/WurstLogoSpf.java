// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import java.util.function.BooleanSupplier;
import net.wurstclient.WurstClient;
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@SearchTags({ "wurst logo", "top left corner" })
public final class WurstLogoSpf extends OtherFeature
{
    private final EnumSetting<Visibility> visibility;
    
    public WurstLogoSpf() {
        super("WurstLogo", "Shows the Wurst logo and version on the screen.");
        this.addSetting(this.visibility = new EnumSetting<Visibility>("Visibility", Visibility.values(), Visibility.ALWAYS));
    }
    
    public boolean isVisible() {
        return this.visibility.getSelected().isVisible();
    }
    
    public enum Visibility
    {
        ALWAYS("ALWAYS", 0, "Always", () -> true), 
        ONLY_OUTDATED("ONLY_OUTDATED", 1, "Only when outdated", () -> WurstLogoSpf.WURST.updater.isOutdated());
        
        private final String name;
        private final BooleanSupplier visible;
        
        private Visibility(final String name2, final int ordinal, final String name, final BooleanSupplier visible) {
            this.name = name;
            this.visible = visible;
        }
        
        public boolean isVisible() {
            return this.visible.getAsBoolean();
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
