// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.login7.auth;

public enum ADALWorkflow {
    UserPass(0x01), Integrated(0x02);
    private final int value;
    ADALWorkflow(int value) { this.value = value; }
    public int value() { return value; }
}
