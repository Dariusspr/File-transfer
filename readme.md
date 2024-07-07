# fTransfer - File transfer app
## Overview
**fTransfer** is a java application created for transferring files over sockets.  
- Users type client's name and its port number;
- Client app connects to centralized server;
- Server pings clients to keep track of active clients;
- Clients are notified about list of currently active clients and their info(ip, port, name);
- Users can select multiple files(directories) for sending;
- Users can select multiple receivers from dropdown menu;
- Files are sent concurrently in peer to peer manner.
## Prerequisites
- JDK 21
- JavaFX 21.0.1

