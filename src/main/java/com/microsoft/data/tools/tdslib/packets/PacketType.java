package com.microsoft.data.tools.tdslib.packets;

/**
 * Type of TDS Packet.
 */
public enum PacketType {

    /**
     * Unknown packet type.
     */
    UNKNOWN(0x00),

    /**
     * SQL batch.
     */
    SQL_BATCH(0x01),

    /**
     * RPC.
     */
    RPC_REQUEST(0x03),

    /**
     * Tabular result.
     */
    TABULAR_RESULT(0x04),

    /**
     * Attention signal.
     */
    ATTENTION(0x06),

    /**
     * Bulk load data.
     */
    BULK_LOAD(0x07),

    /**
     * Federated Authentication Token.
     */
    FED_AUTH_TOKEN(0x08),

    /**
     * Transaction manager request.
     */
    TRANSACTION_MANAGER(0x0E),

    /**
     * TDS7 Login.
     */
    LOGIN7(0x10),

    /**
     * SSPI (Security Support Provider Interface).
     */
    SSPI(0x11),

    /**
     * Pre-Login.
     */
    PRE_LOGIN(0x12);

    private final byte value;

    PacketType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup method to get Enum from byte value.
     */
    public static PacketType fromValue(byte value) {
        for (PacketType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}