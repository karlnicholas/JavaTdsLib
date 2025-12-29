# Java TDS Client

A high-performance, pure Java implementation of the **Microsoft SQL Server TDS (Tabular Data Stream) Protocol**.

This library provides a low-level, asynchronous-ready driver for communicating directly with SQL Server without relying on JDBC or ODBC. It is built using **Java NIO (`SocketChannel`)** for efficient, non-blocking I/O operations.

## 🚀 Features

* **Pure Java NIO:** Built on `java.nio.channels.SocketChannel` for high-throughput networking.
* **Zero Dependencies:** Requires only the standard JDK (Java 8+).
* **Full Login Handshake:** Implements the complex TDS Login sequence:
    * **Pre-Login:** Capabilities negotiation and version handshakes.
    * **TLS/SSL Encapsulation:** Supports the unique TDS-over-TLS wrapping required for modern SQL Server encryption.
    * **Login7:** Handles authentication with full option flags.
* **Token Stream Parsing:** A robust parser for the server's response stream, supporting:
    * `LOGINACK` (Login Acknowledgement)
    * `ENVCHANGE` (Database context, Packet size, etc.)
    * `ERROR` / `INFO` messages
    * `DONE` / `DONEPROC` / `DONEINPROC` status tokens
* **Packet Fragmentation:** Automatically handles splitting messages into 4KB packets and reassembling them.

## 📋 Requirements

* **Java:** JDK 8 or higher.
* **SQL Server:** Compatible with SQL Server 2008 R2 through SQL Server 2022 (TDS Versions 7.1 - 7.4).

## 🛠️ Architecture

The library is layered to separate transport concerns from protocol logic:

```mermaid
graph TD
    UserCode[User Application] --> Client[TdsClient]
    
    subgraph "Core Library"
        Client --> TokenHandler[TokenStreamHandler]
        Client --> MsgHandler[MessageHandler]
        
        TokenHandler -- Parsed Tokens --> Client
        MsgHandler -- Raw Messages --> TokenHandler
        
        MsgHandler --> Connection[TdsConnection (NIO)]
    end
    
    subgraph "Network Layer"
        Connection --> |Encrypted| SSL[PreLoginTlsChannel]
        Connection --> |Raw Packets| Socket[TcpConnection]
    end
    
    Socket --> |TCP/IP| SQLServer[((SQL Server))]