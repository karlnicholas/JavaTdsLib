package com.microsoft.data.tools.tdslib.payloads.login7;

/**
 * Option flags 3.
 * Handles bit-masking for password changes, XML, and extension settings.
 */
public final class OptionFlags3 {

    // Bit Masks
    private static final int OPTION_CHANGE_PASSWORD_BIT_INDEX = 0x01;
    private static final int OPTION_BINARY_XML_BIT_INDEX = 0x02;
    private static final int OPTION_SPAWN_USER_INSTANCE_BIT_INDEX = 0x04;
    private static final int OPTION_UNKNOWN_COLLATION_HANDLING_BIT_INDEX = 0x08;
    private static final int OPTION_EXTENSION_USED_BIT_INDEX = 0x10;

    // Use int internally to avoid signed byte issues
    private int value;

    // --- Enums ---

    public enum OptionChangePassword {
        NO,
        YES
    }

    // --- Constructors ---

    /**
     * Create a new instance with default values.
     */
    public OptionFlags3() {
        this.value = 0;
        setChangePassword(OptionChangePassword.NO);
        setBinaryXml(false);
        setSpawnUserInstance(false);
        setUnknownCollationHandling(true);
        setExtensionUsed(true);
    }

    /**
     * Create a new instance from a raw byte value.
     */
    public OptionFlags3(byte value) {
        this.value = value & 0xFF;
    }

    // --- Properties ---

    public byte getValue() {
        return (byte) value;
    }

    public byte byteValue() {
        return (byte) value;
    }

    public OptionChangePassword getChangePassword() {
        if ((value & OPTION_CHANGE_PASSWORD_BIT_INDEX) == OPTION_CHANGE_PASSWORD_BIT_INDEX) {
            return OptionChangePassword.YES;
        }
        return OptionChangePassword.NO;
    }

    public void setChangePassword(OptionChangePassword changePassword) {
        if (changePassword == OptionChangePassword.NO) {
            value &= ~OPTION_CHANGE_PASSWORD_BIT_INDEX;
        } else {
            value |= OPTION_CHANGE_PASSWORD_BIT_INDEX;
        }
    }

    public boolean isBinaryXml() {
        return (value & OPTION_BINARY_XML_BIT_INDEX) == OPTION_BINARY_XML_BIT_INDEX;
    }

    public void setBinaryXml(boolean enabled) {
        if (enabled) {
            value |= OPTION_BINARY_XML_BIT_INDEX;
        } else {
            value &= ~OPTION_BINARY_XML_BIT_INDEX;
        }
    }

    public boolean isSpawnUserInstance() {
        return (value & OPTION_SPAWN_USER_INSTANCE_BIT_INDEX) == OPTION_SPAWN_USER_INSTANCE_BIT_INDEX;
    }

    public void setSpawnUserInstance(boolean enabled) {
        if (enabled) {
            value |= OPTION_SPAWN_USER_INSTANCE_BIT_INDEX;
        } else {
            value &= ~OPTION_SPAWN_USER_INSTANCE_BIT_INDEX;
        }
    }

    public boolean isUnknownCollationHandling() {
        return (value & OPTION_UNKNOWN_COLLATION_HANDLING_BIT_INDEX) == OPTION_UNKNOWN_COLLATION_HANDLING_BIT_INDEX;
    }

    public void setUnknownCollationHandling(boolean enabled) {
        if (enabled) {
            value |= OPTION_UNKNOWN_COLLATION_HANDLING_BIT_INDEX;
        } else {
            value &= ~OPTION_UNKNOWN_COLLATION_HANDLING_BIT_INDEX;
        }
    }

    public boolean isExtensionUsed() {
        return (value & OPTION_EXTENSION_USED_BIT_INDEX) == OPTION_EXTENSION_USED_BIT_INDEX;
    }

    public void setExtensionUsed(boolean enabled) {
        if (enabled) {
            value |= OPTION_EXTENSION_USED_BIT_INDEX;
        } else {
            value &= ~OPTION_EXTENSION_USED_BIT_INDEX;
        }
    }

    @Override
    public String toString() {
        String binary = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
        return "OptionFlags3[0b" + binary +
                "(ChangePassword=" + getChangePassword() +
                ", BinaryXml=" + isBinaryXml() +
                ", SpawnUserInstance=" + isSpawnUserInstance() +
                ", UnknownCollationHandling=" + isUnknownCollationHandling() +
                ", ExtensionUsed=" + isExtensionUsed() + ")]";
    }
}