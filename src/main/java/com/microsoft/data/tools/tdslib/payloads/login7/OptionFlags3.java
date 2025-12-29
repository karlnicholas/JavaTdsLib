// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.login7;

public final class OptionFlags3 {
    public enum OptionChangePassword { No, Yes }

    private static final int OptionChangePasswordBitIndex = 0x01;
    private static final int OptionBinaryXmlBitIndex = 0x02;
    private static final int OptionSpawnUserInstanceBitIndex = 0x04;
    private static final int OptionUnkownCollationHandlingBitIndex = 0x08;
    private static final int OptionExtensionUsedBitIndex = 0x10;

    private byte value;

    public OptionFlags3(byte value) { this.value = value; }
    public OptionFlags3() { setChangePassword(OptionChangePassword.No); setBinaryXml(false); setSpawnUserInstance(false); setUnknownCollationHandling(true); setExtensionUsed(true); }

    public byte getValue() { return value; }

    public OptionChangePassword getChangePassword() { return (value & OptionChangePasswordBitIndex) == OptionChangePasswordBitIndex ? OptionChangePassword.Yes : OptionChangePassword.No; }
    public void setChangePassword(OptionChangePassword v) { if (v == OptionChangePassword.No) value &= (byte)(255 - OptionChangePasswordBitIndex); else value |= OptionChangePasswordBitIndex; }

    public boolean isBinaryXml() { return (value & OptionBinaryXmlBitIndex) == OptionBinaryXmlBitIndex; }
    public void setBinaryXml(boolean v) { if (v) value |= OptionBinaryXmlBitIndex; else value &= (byte)(255 - OptionBinaryXmlBitIndex); }

    public boolean isSpawnUserInstance() { return (value & OptionSpawnUserInstanceBitIndex) == OptionSpawnUserInstanceBitIndex; }
    public void setSpawnUserInstance(boolean v) { if (v) value |= OptionSpawnUserInstanceBitIndex; else value &= (byte)(255 - OptionSpawnUserInstanceBitIndex); }

    public boolean isUnknownCollationHandling() { return (value & OptionUnkownCollationHandlingBitIndex) == OptionUnkownCollationHandlingBitIndex; }
    public void setUnknownCollationHandling(boolean v) { if (v) value |= OptionUnkownCollationHandlingBitIndex; else value &= (byte)(255 - OptionUnkownCollationHandlingBitIndex); }

    public boolean isExtensionUsed() { return (value & OptionExtensionUsedBitIndex) == OptionExtensionUsedBitIndex; }
    public void setExtensionUsed(boolean v) { if (v) value |= OptionExtensionUsedBitIndex; else value &= (byte)(255 - OptionExtensionUsedBitIndex); }

    public byte toByte() { return value; }

    public static OptionFlags3 fromByte(byte b) { return new OptionFlags3(b); }

    @Override
    public String toString() {
        return String.format("OptionFlags3[0b%s(ChangePassword=%s, BinaryXml=%s, SpawnUserInstance=%s, UnknownCollationHandling=%s, ExtensionUsed=%s)]",
                String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0'), getChangePassword(), isBinaryXml(), isSpawnUserInstance(), isUnknownCollationHandling(), isExtensionUsed());
    }
}
