package com.microsoft.data.tools.tdslib.payloads.login7.auth;

/**
 * Active Directory Authentication Library (ADAL) workflow.
 */
public enum ADALWorkflow {

    /**
     * Username and password.
     */
    USER_PASS(0x01),

    /**
     * Integrated.
     */
    INTEGRATED(0x02);

    private final int value;

    ADALWorkflow(int value) {
        this.value = value;
    }

    /**
     * Gets the byte value of the workflow.
     * @return The workflow ID.
     */
    public int getValue() {
        return value;
    }

    /**
     * Helper to get Enum from value.
     */
    public static ADALWorkflow fromValue(int value) {
        for (ADALWorkflow w : values()) {
            if (w.value == value) {
                return w;
            }
        }
        throw new IllegalArgumentException("Unknown ADALWorkflow value: " + value);
    }
}