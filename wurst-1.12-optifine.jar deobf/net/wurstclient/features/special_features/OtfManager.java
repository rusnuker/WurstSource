// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import java.util.Collection;
import java.lang.reflect.Field;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import net.wurstclient.features.OtherFeature;
import java.util.TreeMap;

public final class OtfManager
{
    private final TreeMap<String, OtherFeature> features;
    public final BookHackSpf bookHackSpf;
    public final ChangelogSpf changelogSpf;
    public final CleanUpOtf cleanUpOtf;
    public final DisableSpf disableSpf;
    public final HackListSpf hackListSpf;
    public final LastServerOtf lastServerOtf;
    public final ReconnectOtf reconnectOtf;
    public final ServerFinderSpf serverFinderSpf;
    public final TabGuiSpf tabGuiSpf;
    public final TargetSpf targetSpf;
    public final WurstCapesOtf wurstCapesOtf;
    public final WurstLogoSpf wurstLogoSpf;
    public final YesCheatSpf yesCheatSpf;
    public final ZoomSpf zoomSpf;
    
    public OtfManager() {
        this.features = new TreeMap<String, OtherFeature>((o1, o2) -> o1.compareToIgnoreCase(o2));
        this.bookHackSpf = new BookHackSpf();
        this.changelogSpf = new ChangelogSpf();
        this.cleanUpOtf = new CleanUpOtf();
        this.disableSpf = new DisableSpf();
        this.hackListSpf = new HackListSpf();
        this.lastServerOtf = new LastServerOtf();
        this.reconnectOtf = new ReconnectOtf();
        this.serverFinderSpf = new ServerFinderSpf();
        this.tabGuiSpf = new TabGuiSpf();
        this.targetSpf = new TargetSpf();
        this.wurstCapesOtf = new WurstCapesOtf();
        this.wurstLogoSpf = new WurstLogoSpf();
        this.yesCheatSpf = new YesCheatSpf();
        this.zoomSpf = new ZoomSpf();
        try {
            Field[] fields;
            for (int length = (fields = OtfManager.class.getFields()).length, i = 0; i < length; ++i) {
                final Field field = fields[i];
                if (field.getName().endsWith("Spf") || field.getName().endsWith("Otf")) {
                    final OtherFeature spf = (OtherFeature)field.get(this);
                    this.features.put(spf.getName(), spf);
                }
            }
        }
        catch (final Exception e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Initializing other Wurst features"));
        }
    }
    
    public OtherFeature getFeatureByName(final String name) {
        return this.features.get(name);
    }
    
    public Collection<OtherFeature> getAllFeatures() {
        return this.features.values();
    }
    
    public int countFeatures() {
        return this.features.size();
    }
}
