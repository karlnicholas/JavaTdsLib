package com.microsoft.data.tools.tdslib.tokens.featureextack;

import java.util.HashMap;
import java.util.Map;

/**
 * Feature identifier.
 */
public enum FeatureId {

    /**
     * Session recovery.
     */
    SESSION_RECOVERY(0x01),

    /**
     * Federated authentication.
     */
    FED_AUTH(0x02),

    /**
     * Column encryption.
     */
    COLUMN_ENCRYPTION(0x04),

    /**
     * Global transactions.
     */
    GLOBAL_TRANSACTIONS(0x05),

    /**
     * Azure SQL Support.
     */
    AZURE_SQL_SUPPORT(0x08),

    /**
     * Feature terminator.
     */
    TERMINATOR(0xFF);

    private final int value;
    private static final Map<Integer, FeatureId> map = new HashMap<>();

    static {
        for (FeatureId id : values()) {
            map.put(id.value, id);
        }
    }

    FeatureId(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FeatureId fromValue(int value) {
        return map.get(value);
    }
}