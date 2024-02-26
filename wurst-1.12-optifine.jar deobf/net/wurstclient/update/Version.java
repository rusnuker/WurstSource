// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.update;

import java.util.regex.Pattern;

public final class Version implements Comparable<Version>
{
    private static final Pattern SYNTAX;
    private final int major;
    private final int minor;
    private final int patch;
    private final int preRelease;
    
    static {
        SYNTAX = Pattern.compile("^[0-9]+\\.[0-9]+(?:\\.[0-9]+)?(?:pre[0-9]+)?$");
    }
    
    public Version(final String version) {
        if (!Version.SYNTAX.asPredicate().test(version)) {
            this.major = -1;
            this.minor = -1;
            this.patch = -1;
            this.preRelease = Integer.MAX_VALUE;
            return;
        }
        final int indexOfPre = version.indexOf("pre");
        String[] parts;
        if (indexOfPre == -1) {
            this.preRelease = Integer.MAX_VALUE;
            parts = version.split("\\.");
        }
        else {
            this.preRelease = Integer.parseInt(version.substring(indexOfPre + 3));
            parts = version.substring(0, indexOfPre).split("\\.");
        }
        this.major = Integer.parseInt(parts[0]);
        this.minor = Integer.parseInt(parts[1]);
        if (parts.length == 3) {
            this.patch = Integer.parseInt(parts[2]);
        }
        else {
            this.patch = 0;
        }
    }
    
    @Override
    public int hashCode() {
        return this.major << 24 | this.minor << 16 | this.patch << 8 | this.preRelease;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) || (obj instanceof Version && this.compareTo((Version)obj) == 0);
    }
    
    @Override
    public int compareTo(final Version o) {
        if (this.major != o.major) {
            return Integer.compare(this.major, o.major);
        }
        if (this.minor != o.minor) {
            return Integer.compare(this.minor, o.minor);
        }
        if (this.patch != o.patch) {
            return Integer.compare(this.patch, o.patch);
        }
        if (this.preRelease != o.preRelease) {
            return Integer.compare(this.preRelease, o.preRelease);
        }
        return 0;
    }
    
    public boolean shouldUpdateTo(final Version other) {
        return this.isInvalid() || other.isInvalid() || this.compareTo(other) < 0;
    }
    
    public boolean isLowerThan(final Version other) {
        return this.compareTo(other) < 0;
    }
    
    public boolean isLowerThan(final String other) {
        return this.isLowerThan(new Version(other));
    }
    
    public boolean isHigherThan(final Version other) {
        return this.compareTo(other) > 0;
    }
    
    public boolean isHigherThan(final String other) {
        return this.isHigherThan(new Version(other));
    }
    
    @Override
    public String toString() {
        if (this.isInvalid()) {
            return "(invalid version)";
        }
        String s = String.valueOf(this.major) + "." + this.minor;
        if (this.patch > 0) {
            s = String.valueOf(s) + "." + this.patch;
        }
        if (this.isPreRelease()) {
            s = String.valueOf(s) + "pre" + this.preRelease;
        }
        return s;
    }
    
    public boolean isInvalid() {
        return this.major == -1 && this.minor == -1 && this.patch == -1;
    }
    
    public boolean isPreRelease() {
        return this.preRelease != Integer.MAX_VALUE;
    }
    
    public String getChangelogLink() {
        String version = String.valueOf(this.major) + "-" + this.minor;
        if (this.patch != 0) {
            version = String.valueOf(version) + "-" + this.patch;
        }
        if (this.isPreRelease()) {
            version = String.valueOf(version) + "pre" + this.preRelease;
        }
        return "https://www.wurstclient.net/updates/wurst-" + version + "/";
    }
}
