// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features;

import net.wurstclient.features.special_features.YesCheatSpf;
import net.minecraft.crash.CrashReportCategory;
import net.wurstclient.files.ConfigFiles;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import net.wurstclient.gui.UIRenderer;
import net.wurstclient.features.mods.other.NavigatorMod;
import java.util.Iterator;
import net.wurstclient.settings.Setting;
import java.util.Collection;
import java.util.Arrays;
import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;
import java.lang.annotation.Annotation;

public abstract class Hack extends Feature
{
    private final String name;
    private final String description;
    private final Bypasses bypasses;
    private final boolean stateSaved;
    private boolean enabled;
    private boolean blocked;
    private boolean active;
    private long currentMS;
    protected long lastMS;
    
    public Hack(final String name, final String description) {
        this.bypasses = this.getClass().getAnnotation(Bypasses.class);
        this.stateSaved = !this.getClass().isAnnotationPresent(DontSaveState.class);
        this.currentMS = 0L;
        this.lastMS = -1L;
        this.name = name;
        this.description = description;
    }
    
    @Override
    public final ArrayList<PossibleKeybind> getPossibleKeybinds() {
        final String dotT = ".t " + this.name.toLowerCase();
        final ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<PossibleKeybind>(Arrays.asList(new PossibleKeybind(dotT, "Toggle " + this.name), new PossibleKeybind(String.valueOf(dotT) + " on", "Enable " + this.name), new PossibleKeybind(String.valueOf(dotT) + " off", "Disable " + this.name)));
        for (final Setting setting : this.getSettings()) {
            possibleKeybinds.addAll(setting.getPossibleKeybinds(this.name));
        }
        return possibleKeybinds;
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[0];
    }
    
    @Override
    public final boolean isEnabled() {
        return this.enabled;
    }
    
    public final boolean isActive() {
        return this.active;
    }
    
    public final void setEnabled(final boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        this.active = (enabled && !this.blocked);
        if (!(this instanceof NavigatorMod)) {
            UIRenderer.modList.updateState(this);
        }
        if (this.blocked && enabled) {
            return;
        }
        try {
            this.onToggle();
            if (enabled) {
                this.onEnable();
            }
            else {
                this.onDisable();
            }
        }
        catch (final Throwable e) {
            final CrashReport report = CrashReport.makeCrashReport(e, "Toggling Wurst mod");
            final CrashReportCategory category = report.makeCategory("Affected mod");
            category.setDetail("Mod name", () -> this.name);
            category.setDetail("Attempted action", () -> b ? "Enable" : "Disable");
            throw new ReportedException(report);
        }
        if (this.stateSaved) {
            ConfigFiles.MODS.save();
        }
    }
    
    public final void enableOnStartup() {
        this.enabled = true;
        this.active = (this.enabled && !this.blocked);
        try {
            this.onToggle();
            this.onEnable();
        }
        catch (final Throwable e) {
            final CrashReport report = CrashReport.makeCrashReport(e, "Toggling Wurst mod");
            final CrashReportCategory category = report.makeCategory("Affected mod");
            category.setDetail("Mod name", () -> this.name);
            category.setDetail("Attempted action", () -> "Enable on startup");
            throw new ReportedException(report);
        }
    }
    
    public final void toggle() {
        this.setEnabled(!this.isEnabled());
    }
    
    @Override
    public boolean isBlocked() {
        return this.blocked;
    }
    
    public void setBlocked(final boolean blocked) {
        this.blocked = blocked;
        this.active = (this.enabled && !blocked);
        if (!(this instanceof NavigatorMod)) {
            UIRenderer.modList.updateState(this);
        }
        if (this.enabled) {
            try {
                this.onToggle();
                if (blocked) {
                    this.onDisable();
                }
                else {
                    this.onEnable();
                }
            }
            catch (final Throwable e) {
                final CrashReport report = CrashReport.makeCrashReport(e, "Toggling Wurst mod");
                final CrashReportCategory category = report.makeCategory("Affected mod");
                category.setDetail("Mod name", () -> this.name);
                category.setDetail("Attempted action", () -> b ? "Block" : "Unblock");
                throw new ReportedException(report);
            }
        }
    }
    
    public final void updateMS() {
        this.currentMS = System.currentTimeMillis();
    }
    
    public final void updateLastMS() {
        this.lastMS = System.currentTimeMillis();
    }
    
    public final boolean hasTimePassedM(final long MS) {
        return this.currentMS >= this.lastMS + MS;
    }
    
    public final boolean hasTimePassedS(final float speed) {
        return this.currentMS >= this.lastMS + (long)(1000.0f / speed);
    }
    
    public void onToggle() {
    }
    
    public void onEnable() {
    }
    
    public void onDisable() {
    }
    
    public void initSettings() {
    }
    
    public void onYesCheatUpdate(final YesCheatSpf.Profile profile) {
    }
    
    @Override
    public final String getName() {
        return this.name;
    }
    
    public String getRenderName() {
        return this.name;
    }
    
    @Override
    public final String getType() {
        return "Hack";
    }
    
    @Override
    public final String getDescription() {
        return this.description;
    }
    
    public final boolean isStateSaved() {
        return this.stateSaved;
    }
    
    @Override
    public final String getPrimaryAction() {
        return this.enabled ? "Disable" : "Enable";
    }
    
    @Override
    public final void doPrimaryAction() {
        this.toggle();
    }
    
    public final Bypasses getBypasses() {
        return this.bypasses;
    }
}
