package com.microsoft.data.tools.tdslib.io.connection.tcp;

import com.microsoft.data.tools.tdslib.io.connection.ConnectionOptions;
import java.net.InetSocketAddress;

/**
 * Connection options for Tcp connection.
 */
public class TcpConnectionOptions extends ConnectionOptions {

    /**
     * Local endpoint to use for the socket.
     * If null, the OS assigns one.
     */
    private InetSocketAddress localEndpoint;

    /**
     * Connect timeout in milliseconds.
     * -1 indicates default OS timeout.
     */
    private int connectTimeout = -1;

    /**
     * Receive timeout in milliseconds.
     * 0 indicates infinite. Default is 5000 (5 seconds).
     */
    private int receiveTimeout = 5000;

    /**
     * Send timeout in milliseconds.
     * 0 indicates infinite. Default is 5000 (5 seconds).
     */
    private int sendTimeout = 5000;

    /**
     * Optional hostname to be used for TLS server certificate name validation.
     */
    private String tlsCertificateHostname;

    // Java typically uses TrustManagers rather than a simple callback delegate.
    // Keeping this object placeholder to match C# architecture intent.
    private Object remoteCertificateValidationCallback;

    public InetSocketAddress getLocalEndpoint() {
        return localEndpoint;
    }

    public void setLocalEndpoint(InetSocketAddress localEndpoint) {
        this.localEndpoint = localEndpoint;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public int getSendTimeout() {
        return sendTimeout;
    }

    public void setSendTimeout(int sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    public String getTlsCertificateHostname() {
        return tlsCertificateHostname;
    }

    public void setTlsCertificateHostname(String tlsCertificateHostname) {
        this.tlsCertificateHostname = tlsCertificateHostname;
    }

    public Object getRemoteCertificateValidationCallback() {
        return remoteCertificateValidationCallback;
    }

    public void setRemoteCertificateValidationCallback(Object remoteCertificateValidationCallback) {
        this.remoteCertificateValidationCallback = remoteCertificateValidationCallback;
    }
}