package com.microsoft.data.tools.tdslib.payloads.login7.auth;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// You will need to import your ADALWorkflow enum once created
// import com.microsoft.data.tools.tdslib.payloads.login7.auth.ADALWorkflow;

/**
 * Active Directory Authentication Library Federate authentication.
 */
public final class ADALFedAuth extends FedAuth {

    private final boolean echo;
    private final ADALWorkflow workflow;

    /**
     * Creates a new instance of this class.
     *
     * @param workflow Workflow.
     * @param echo     Echo.
     */
    public ADALFedAuth(ADALWorkflow workflow, boolean echo) {
        this.workflow = workflow;
        this.echo = echo;
    }

    /**
     * Constructor overload for default echo=false
     */
    public ADALFedAuth(ADALWorkflow workflow) {
        this(workflow, false);
    }

    public boolean isEcho() {
        return echo;
    }

    public ADALWorkflow getWorkflow() {
        return workflow;
    }

    @Override
    public ByteBuffer getBuffer() {
        // Total Size Calculation:
        // 1 byte  (FeatureId)
        // 4 bytes (Length of data = 2)
        // 1 byte  (Options)
        // 1 byte  (Workflow)
        // Total = 7 bytes
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // 1. Feature Id
        buffer.put(FEATURE_ID);

        // 2. Length of the following data (2 bytes)
        // Written as a 32-bit Little Endian Integer
        buffer.putInt(2);

        // 3. Options
        // Combine the Library ID and the Echo flag
        byte options = (byte) (LIBRARY_ADAL | (echo ? FED_AUTH_ECHO_YES : FED_AUTH_ECHO_NO));
        buffer.put(options);

        // 4. Workflow
        // Assuming ADALWorkflow is an enum where we can get the byte value
        // You might need to adjust .getValue() based on the actual Enum implementation
        if (workflow != null) {
            // If ADALWorkflow is a standard Java Enum, use ordinal() or a custom field
            buffer.put((byte) workflow.getValue());
        } else {
            buffer.put((byte) 0);
        }

        buffer.flip(); // Prepare buffer for reading/writing to socket
        return buffer;
    }
}