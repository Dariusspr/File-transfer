# fTransfer - File transfer app

## Overview

fTransfer is a Java application designed for transferring files over sockets. Key features include:

- Users can input the client's name and port number.
- The client application connects to a centralized server.
- The server pings clients to track active connections.
- Clients receive notifications about currently active clients, including their IP addresses, port numbers, and names.
- Users can select multiple files or directories to send.
- A dropdown menu allows users to choose multiple recipients.
- Files are transmitted concurrently using a peer-to-peer approach.

## Technologies

- Maven
- JDK 21
- JavaFX 21.0.1
