package org.dariusspr.ftransfer.ftransfer_client.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

public class ClientLocalData {
    private static final ClientLocalData localData = new ClientLocalData();

    private final ClientInfo info = new ClientInfo();
    private final ObservableList<ClientInfo> availableClients = FXCollections.observableArrayList();
    private ClientLocalData() {}

    public static ClientLocalData getData() {
        return localData;
    }

    public void updateClientInfo(ClientInfo newInfo) {
        info.update(newInfo);
    }

    public ClientInfo getInfo() {
        return info;
    }

    public ObservableList<ClientInfo> getAvailableClients() {
        return availableClients;
    }

}
