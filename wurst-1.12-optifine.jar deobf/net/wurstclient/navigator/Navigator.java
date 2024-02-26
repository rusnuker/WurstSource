// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.navigator;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.Iterator;
import java.util.function.Function;
import java.util.Comparator;
import java.util.Collection;
import net.wurstclient.WurstClient;
import java.util.HashMap;
import net.wurstclient.features.Feature;
import java.util.ArrayList;

public final class Navigator
{
    private final ArrayList<Feature> navigatorList;
    private final HashMap<String, Long> preferences;
    
    public Navigator() {
        this.navigatorList = new ArrayList<Feature>();
        this.preferences = new HashMap<String, Long>();
        this.navigatorList.addAll(WurstClient.INSTANCE.hax.getAll());
        this.navigatorList.addAll(WurstClient.INSTANCE.commands.getAllCommands());
        this.navigatorList.addAll(WurstClient.INSTANCE.special.getAllFeatures());
    }
    
    public void copyNavigatorList(final ArrayList<Feature> list) {
        if (!list.equals(this.navigatorList)) {
            list.clear();
            list.addAll(this.navigatorList);
        }
    }
    
    public void getSearchResults(final ArrayList<Feature> list, final String query) {
        list.clear();
        for (final Feature mod : this.navigatorList) {
            if (mod.getName().toLowerCase().contains(query) || mod.getSearchTags().toLowerCase().contains(query) || mod.getDescription().toLowerCase().contains(query)) {
                list.add(mod);
            }
        }
        final Comparator<String> c = (o1, o2) -> {
            final int index1 = o1.toLowerCase().indexOf(s);
            final int index2 = o2.toLowerCase().indexOf(s);
            if (index1 == index2) {
                return 0;
            }
            else if (index1 == -1) {
                return 1;
            }
            else if (index2 == -1) {
                return -1;
            }
            else {
                return index1 - index2;
            }
        };
        list.sort(Comparator.comparing((Function<? super Object, ?>)Feature::getName, (Comparator<? super Object>)c).thenComparing((Function<? super Object, ?>)Feature::getSearchTags, (Comparator<? super Object>)c).thenComparing((Function<? super Object, ?>)Feature::getDescription, (Comparator<? super Object>)c));
    }
    
    public long getPreference(final String feature) {
        Long preference = this.preferences.get(feature);
        if (preference == null) {
            preference = 0L;
        }
        return preference;
    }
    
    public void addPreference(final String feature) {
        Long preference = this.preferences.get(feature);
        if (preference == null) {
            preference = 0L;
        }
        ++preference;
        this.preferences.put(feature, preference);
    }
    
    public void setPreference(final String feature, final long preference) {
        this.preferences.put(feature, preference);
    }
    
    public void forEach(final Consumer<Feature> action) {
        this.navigatorList.forEach(action);
    }
    
    public Iterator<Feature> iterator() {
        return this.navigatorList.iterator();
    }
    
    public List<Feature> getList() {
        return Collections.unmodifiableList((List<? extends Feature>)this.navigatorList);
    }
    
    public void sortFeatures() {
        this.navigatorList.sort(Comparator.comparingLong(f -> this.getPreference(f.getName())).reversed());
    }
    
    public int countAllFeatures() {
        return this.navigatorList.size();
    }
}
