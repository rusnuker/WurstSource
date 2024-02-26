// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.features.Bypasses;
import net.wurstclient.WurstClient;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.Hack;
import java.util.HashSet;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@SearchTags({ "YesCheatPlus", "NoCheat+", "NoCheatPlus", "AntiMAC", "yes cheat plus", "no cheat plus", "anti mac", "ncp bypasses" })
@HelpPage("Special_Features/YesCheat")
public final class YesCheatSpf extends OtherFeature
{
    private final HashSet<Hack> blockedMods;
    private Profile profile;
    public CheckboxSetting modeIndicator;
    private ModeSetting profileSetting;
    
    public YesCheatSpf() {
        super("YesCheat+", "Makes other features bypass AntiCheat plugins or blocks them if they can't.");
        this.blockedMods = new HashSet<Hack>();
        this.profile = Profile.OFF;
        this.modeIndicator = new CheckboxSetting("Mode Indicator", true);
        this.setCategory(Category.OTHER);
        this.addSetting(this.profileSetting = new ModeSetting("Profile", Profile.getNames(), this.profile.ordinal()) {
            @Override
            public void update() {
                YesCheatSpf.access$0(YesCheatSpf.this, Profile.values()[this.getSelected()]);
                YesCheatSpf.this.blockedMods.forEach(mod -> mod.setBlocked(false));
                YesCheatSpf.this.blockedMods.clear();
                YesCheatSpf.WURST.hax.getAll().forEach(mod -> {
                    if (!YesCheatSpf.this.profile.doesBypass(mod.getBypasses())) {
                        YesCheatSpf.this.blockedMods.add(mod);
                    }
                    return;
                });
                YesCheatSpf.this.blockedMods.forEach(mod -> mod.setBlocked(true));
                YesCheatSpf.WURST.hax.getAll().forEach(mod -> mod.onYesCheatUpdate(YesCheatSpf.this.profile));
            }
        });
        this.addSetting(this.modeIndicator);
    }
    
    @Override
    public String getPrimaryAction() {
        return "Next Profile";
    }
    
    @Override
    public void doPrimaryAction() {
        this.profileSetting.nextMode();
    }
    
    public Profile getProfile() {
        return this.profile;
    }
    
    static /* synthetic */ void access$0(final YesCheatSpf yesCheatSpf, final Profile profile) {
        yesCheatSpf.profile = profile;
    }
    
    public enum Profile
    {
        OFF("OFF", 0, "Off", b -> true), 
        MINEPLEX("MINEPLEX", 1, "Mineplex", b -> b.mineplex()), 
        ANTICHEAT("ANTICHEAT", 2, "AntiCheat", b -> b.antiCheat()), 
        OLDER_NCP("OLDER_NCP", 3, "Older NoCheat+", b -> b.olderNCP()), 
        LATEST_NCP("LATEST_NCP", 4, "Latest NoCheat+", b -> b.latestNCP()), 
        GHOST_MODE("GHOST_MODE", 5, "Ghost Mode", b -> b.ghostMode());
        
        private final String name;
        private final BypassTest test;
        
        private Profile(final String name2, final int ordinal, final String name, final BypassTest test) {
            this.name = name;
            this.test = test;
        }
        
        public boolean doesBypass(final Bypasses bypasses) {
            return bypasses == null || this.test.doesBypass(bypasses);
        }
        
        public String getName() {
            return this.name;
        }
        
        public static String[] getNames() {
            final String[] names = new String[values().length];
            for (int i = 0; i < names.length; ++i) {
                names[i] = values()[i].name;
            }
            return names;
        }
    }
    
    private interface BypassTest
    {
        boolean doesBypass(final Bypasses p0);
    }
}
