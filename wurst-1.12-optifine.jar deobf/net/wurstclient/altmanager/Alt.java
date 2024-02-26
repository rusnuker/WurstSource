// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager;

import java.util.Comparator;
import java.util.Objects;

public final class Alt implements Comparable<Alt>
{
    private String email;
    private String name;
    private String password;
    private boolean cracked;
    private boolean unchecked;
    private boolean starred;
    
    public Alt(final String email, final String password, final String name) {
        this(email, password, name, false);
    }
    
    public Alt(final String email, final String password, final String name, final boolean starred) {
        this.email = Objects.requireNonNull(email);
        this.starred = starred;
        if (password == null || password.isEmpty()) {
            this.cracked = true;
            this.unchecked = false;
            this.name = email;
            this.password = null;
        }
        else {
            this.cracked = false;
            this.unchecked = (name == null || name.isEmpty());
            this.name = (this.unchecked ? "" : name);
            this.password = password;
        }
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getNameOrEmail() {
        return this.unchecked ? this.email : this.name;
    }
    
    public String getPassword() {
        if (this.password == null || this.password.isEmpty()) {
            this.cracked = true;
            return "";
        }
        return this.password;
    }
    
    public boolean isCracked() {
        return this.cracked;
    }
    
    public boolean isStarred() {
        return this.starred;
    }
    
    public void setStarred(final boolean starred) {
        this.starred = starred;
    }
    
    public boolean isUnchecked() {
        return this.unchecked;
    }
    
    public void setChecked(final String name) {
        this.name = name;
        this.unchecked = false;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Alt)) {
            return false;
        }
        final Alt other = (Alt)obj;
        return this.email.equals(other.email) && this.cracked == other.cracked;
    }
    
    @Override
    public int hashCode() {
        return this.email.hashCode() << 1 | (this.cracked ? 1 : 0);
    }
    
    @Override
    public int compareTo(final Alt o) {
        Comparator<Alt> c = Comparator.comparing(a -> !a.starred);
        c = c.thenComparing(a -> a.cracked);
        c = c.thenComparing(a -> a.email.toLowerCase());
        return c.compare(this, o);
    }
}
