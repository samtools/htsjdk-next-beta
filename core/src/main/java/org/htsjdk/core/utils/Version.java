package org.htsjdk.core.utils;

public class Version {

    private final String formatString = "%d.%d.%d";

    private final int majorVersion;
    private final int minorVersion;
    private final int patchVersion;

    public Version(final int major, final int minor, final int patch) {
        this.majorVersion = major;
        this.minorVersion = minor;
        this.patchVersion = patch;
    }

    public Version(final String versionString) {
        ParamUtils.nonNull(versionString);
        final String[] parts = versionString.split(".", 0);
        if (parts.length != 3) {
            throw new IllegalArgumentException(String.format("Can parse version string: '%s'", versionString));
        }
        try {
            majorVersion = Integer.parseInt(parts[0]);
            minorVersion = Integer.parseInt(parts[1]);
            patchVersion = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Can parse version string: '%s'", versionString));
        }

    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;

        Version version = (Version) o;

        if (getMajorVersion() != version.getMajorVersion()) return false;
        if (getMinorVersion() != version.getMinorVersion()) return false;
        return getPatchVersion() == version.getPatchVersion();
    }

    @Override
    public int hashCode() {
        int result = getMajorVersion();
        result = 31 * result + getMinorVersion();
        result = 31 * result + getPatchVersion();
        return result;
    }

    @Override
    public String toString() {
        return String.format(formatString, getMajorVersion(), getMinorVersion(), getPatchVersion());
    }
}
