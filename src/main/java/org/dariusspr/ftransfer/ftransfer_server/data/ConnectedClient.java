package org.dariusspr.ftransfer.ftransfer_server.data;

import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

import java.net.Socket;

public class ConnectedClient extends ClientInfo {
    private final Socket socket;
    public ConnectedClient(ClientInfo info, Socket socket) {
        super(info);
        this.socket = socket;
    }
    public Socket getSocket() {
        return socket;
    }
}
