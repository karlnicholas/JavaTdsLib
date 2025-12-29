package com.microsoft.data.tools.tdslib.io.connection.tcp;

import com.microsoft.data.tools.tdslib.io.connection.ConnectionOptions;
import java.net.InetSocketAddress;

/**
 * Connection options for Tcp connection.
 */
public class TcpConnectionOptions extends ConnectionOptions {

    private InetSocketAddress localEndpoint;

    // Timeouts in milliseconds (defaults matched to C# TimeSpan values)
    private int connectTimeout = -1; // Default OS timeout
    private int receiveTimeout = 5000;
    private int sendTimeout = 5000;

    private String tlsCertificateHostname;

    // Placeholder for SSL TrustManager logic (replaces C# RemoteCertificateValidationCallback)
    private Object remoteCertificateValidationCallback;

    public InetSocketAddress getLocalEndpoint() { return localEndpoint; }
    public void setLocalEndpoint(InetSocketAddress localEndpoint) { this.localEndpoint = localEndpoint; }

    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }

    public int getReceiveTimeout() { return receiveTimeout; }
    public void setReceiveTimeout(int receiveTimeout) { this.receiveTimeout = receiveTimeout; }

    public int getSendTimeout() { return sendTimeout; }
    public void setSendTimeout(int sendTimeout) { this.sendTimeout = sendTimeout; }

    public String getTlsCertificateHostname() { return tlsCertificateHostname; }
    public void setTlsCertificateHostname(String tlsCertificateHostname) { this.tlsCertificateHostname = tlsCertificateHostname; }

    public Object getRemoteCertificateValidationCallback() { return remoteCertificateValidationCallback; }
    public void setRemoteCertificateValidationCallback(Object callback) { this.remoteCertificateValidationCallback = callback; }
}