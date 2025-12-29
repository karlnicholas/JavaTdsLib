package com.microsoft.data.tools.tdslib.payloads.prelogin;

import java.util.Objects;

public class SqlVersion {
    private byte major;
    private byte minor;
    private byte patch;
    private byte trivial;
    private int subBuild; // ushort in C# -> int in Java

    public SqlVersion() {}

    public SqlVersion(byte major, byte minor, byte patch, byte trivial, int subBuild) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.trivial = trivial;
        this.subBuild = subBuild;
    }

    // Getters and Setters omitted for brevity, assume standard generation

    @Override
    public String toString() {
        return String.format("SqlVersion[%d.%d.%d.%d %d]",
                major, minor, patch, trivial, subBuild);
    }

    // Equals/HashCode standard impl
}