package com.microsoft.data.tools.tdslib.tokens.envchange;

import java.util.HashMap;
import java.util.Map;

/**
 * Environment change token sub type.
 */
public enum EnvChangeTokenSubType {
    DATABASE(1),
    LANGUAGE(2),
    CHARACTER_SET(3),
    PACKET_SIZE(4),
    UNICODE_DATA_SORTING_LOCAL_ID(5),
    UNICODE_DATA_SORTING_COMPARISON_FLAGS(6),
    SQL_COLLATION(7),
    BEGIN_TRANSACTION(8),
    COMMIT_TRANSACTION(9),
    ROLLBACK_TRANSACTION(10),
    ENLIST_DTC_TRANSACTION(11),
    DEFECT_TRANSACTION(12),
    DATABASE_MIRRORING_PARTNER(13),
    PROMOTE_TRANSACTION(15),
    TRANSACTION_MANAGER_ADDRESS(16),
    TRANSACTION_ENDED(17),
    RESET_CONNECTION(18),
    USER_INSTANCE_NAME(19),
    ROUTING(20);

    private final int value;
    private static final Map<Integer, EnvChangeTokenSubType> map = new HashMap<>();

    static {
        for (EnvChangeTokenSubType type : values()) {
            map.put(type.value, type);
        }
    }

    EnvChangeTokenSubType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EnvChangeTokenSubType fromValue(int value) {
        return map.get(value);
    }
}