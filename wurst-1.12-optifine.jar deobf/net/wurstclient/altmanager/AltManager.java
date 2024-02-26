// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Iterator;
import java.nio.file.Path;
import java.util.ArrayList;

public final class AltManager
{
    private final AltsFile altsFile;
    private final ArrayList<Alt> alts;
    private int numPremium;
    private int numCracked;
    
    public AltManager(final Path altsFile) {
        this.alts = new ArrayList<Alt>();
        (this.altsFile = new AltsFile(altsFile)).load(this);
    }
    
    public boolean contains(final String name) {
        for (final Alt alt : this.alts) {
            if (alt.getNameOrEmail().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void add(final String email, final String password, final boolean starred) {
        this.add(new Alt(email, password, null, starred));
    }
    
    public void add(final Alt alt) {
        this.alts.add(alt);
        this.sortAlts();
        this.altsFile.save(this);
    }
    
    public void addAll(final Collection<Alt> c) {
        this.alts.addAll(c);
        this.sortAlts();
        this.altsFile.save(this);
    }
    
    public void edit(final Alt alt, final String newEmail, final String newPassword) {
        this.remove(alt);
        this.add(new Alt(newEmail, newPassword, null, alt.isStarred()));
    }
    
    public void setChecked(final int index, final String name) {
        final Alt alt = this.alts.get(index);
        if (alt.isUnchecked()) {
            ++this.numPremium;
        }
        alt.setChecked(name);
        this.altsFile.save(this);
    }
    
    public void setStarred(final int index, final boolean starred) {
        this.alts.get(index).setStarred(starred);
        this.sortAlts();
        this.altsFile.save(this);
    }
    
    public void remove(final int index) {
        final Alt alt = this.alts.get(index);
        if (alt.isCracked()) {
            --this.numCracked;
        }
        else if (!alt.isUnchecked()) {
            --this.numPremium;
        }
        this.alts.remove(index);
        this.altsFile.save(this);
    }
    
    private void remove(final Alt alt) {
        if (this.alts.remove(alt)) {
            if (alt.isCracked()) {
                --this.numCracked;
            }
            else if (!alt.isUnchecked()) {
                --this.numPremium;
            }
        }
        this.altsFile.save(this);
    }
    
    private void sortAlts() {
        final ArrayList<Alt> newAlts = this.alts.stream().distinct().sorted().collect((Collector<? super Object, ?, ArrayList<Alt>>)Collectors.toCollection(() -> new ArrayList()));
        this.alts.clear();
        this.alts.addAll(newAlts);
        this.numCracked = (int)this.alts.stream().filter(Alt::isCracked).count();
        this.numPremium = (int)this.alts.stream().filter(alt -> !alt.isCracked()).filter(alt -> !alt.isUnchecked()).count();
    }
    
    public List<Alt> getList() {
        return Collections.unmodifiableList((List<? extends Alt>)this.alts);
    }
    
    public int getNumPremium() {
        return this.numPremium;
    }
    
    public int getNumCracked() {
        return this.numCracked;
    }
}
