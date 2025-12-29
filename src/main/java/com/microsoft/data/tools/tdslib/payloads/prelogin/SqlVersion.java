// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.prelogin;

import java.util.Objects;

public class SqlVersion {
    private int major;
    private int minor;
    private int patch;
    private int trivial;
    private int subBuild;

    public int getMajor() { return major; }
    public void setMajor(int major) { this.major = major; }
    public int getMinor() { return minor; }
    public void setMinor(int minor) { this.minor = minor; }
    public int getPatch() { return patch; }
    public void setPatch(int patch) { this.patch = patch; }
    public int getTrivial() { return trivial; }
    public void setTrivial(int trivial) { this.trivial = trivial; }
    public int getSubBuild() { return subBuild; }
    public void setSubBuild(int subBuild) { this.subBuild = subBuild; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SqlVersion that = (SqlVersion) obj;
        return major == that.major && minor == that.minor && patch == that.patch && trivial == that.trivial && subBuild == that.subBuild;
    }

    @Override
    public int hashCode() { return Objects.hash(major, minor, patch, trivial, subBuild); }

    @Override
    public String toString() { return String.format("SqlVersion[%d.%d.%d.%d %d]", major, minor, patch, trivial, subBuild); }
}
