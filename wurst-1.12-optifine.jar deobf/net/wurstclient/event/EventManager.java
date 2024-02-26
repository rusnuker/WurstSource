// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.event;

import java.util.Arrays;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.Collection;
import net.wurstclient.WurstClient;
import java.util.ArrayList;
import java.util.HashMap;

public final class EventManager
{
    private final HashMap<Class<? extends Listener>, ArrayList<? extends Listener>> listenerMap;
    
    public EventManager() {
        this.listenerMap = new HashMap<Class<? extends Listener>, ArrayList<? extends Listener>>();
    }
    
    public <L extends Listener, E extends Event<L>> void fire(final E event) {
        if (!WurstClient.INSTANCE.isEnabled()) {
            return;
        }
        try {
            final Class<L> type = event.getListenerType();
            final ArrayList<L> listeners = (ArrayList<L>)this.listenerMap.get(type);
            if (listeners == null || listeners.isEmpty()) {
                return;
            }
            final ArrayList<L> listeners2 = new ArrayList<L>((Collection<? extends L>)listeners);
            listeners2.removeIf(Objects::isNull);
            event.fire(listeners2);
        }
        catch (final Throwable e) {
            e.printStackTrace();
            final CrashReport report = CrashReport.makeCrashReport(e, "Firing Wurst event");
            final CrashReportCategory category = report.makeCategory("Affected event");
            category.setDetail("Event class", () -> event2.getClass().getName());
            throw new ReportedException(report);
        }
    }
    
    public <L extends Listener> void add(final Class<L> type, final L listener) {
        try {
            ArrayList<L> listeners = (ArrayList<L>)this.listenerMap.get(type);
            if (listeners == null) {
                listeners = new ArrayList<L>((Collection<? extends L>)Arrays.asList(listener));
                this.listenerMap.put(type, listeners);
                return;
            }
            listeners.add(listener);
        }
        catch (final Throwable e) {
            e.printStackTrace();
            final CrashReport report = CrashReport.makeCrashReport(e, "Adding Wurst event listener");
            final CrashReportCategory category = report.makeCategory("Affected listener");
            category.setDetail("Listener type", () -> clazz.getName());
            category.setDetail("Listener class", () -> listener2.getClass().getName());
            throw new ReportedException(report);
        }
    }
    
    public <L extends Listener> void remove(final Class<L> type, final L listener) {
        try {
            final ArrayList<L> listeners = (ArrayList<L>)this.listenerMap.get(type);
            if (listeners != null) {
                listeners.remove(listener);
            }
        }
        catch (final Throwable e) {
            e.printStackTrace();
            final CrashReport report = CrashReport.makeCrashReport(e, "Removing Wurst event listener");
            final CrashReportCategory category = report.makeCategory("Affected listener");
            category.setDetail("Listener type", () -> clazz.getName());
            category.setDetail("Listener class", () -> listener2.getClass().getName());
            throw new ReportedException(report);
        }
    }
}
