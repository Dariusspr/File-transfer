package org.dariusspr.ftransfer.ftransfer_server.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.dariusspr.ftransfer.ftransfer_common.ServerInfo.defaultPort;

public class ServerData {
    private static final ServerData localData = new ServerData();

    private final ObservableList<ConnectedClient> connectedClients = FXCollections.observableArrayList();

    private int port = defaultPort;

    private ServerData() {}

    public static ServerData getData() {
        return localData;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public int getPort() {
        return port;
    }

    public ObservableList<ConnectedClient> getConnectedClients() {
        return connectedClients;
    }
}
