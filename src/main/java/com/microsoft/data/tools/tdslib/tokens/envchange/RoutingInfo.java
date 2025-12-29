package com.microsoft.data.tools.tdslib.tokens.envchange;

public record RoutingInfo(byte protocol, int port, String server) {}