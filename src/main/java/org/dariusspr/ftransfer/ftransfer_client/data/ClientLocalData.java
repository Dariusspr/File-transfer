package org.dariusspr.ftransfer.ftransfer_client.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

import java.io.File;
import java.util.ArrayList;

public class ClientLocalData {
    private static final ClientLocalData localData = new ClientLocalData();

    private final ClientInfo info = new ClientInfo();
    private final ArrayList<ClientInfo> selectedReceivers;

    private final ObservableList<ClientInfo> availableClients = FXCollections.observableArrayList();
    private final ObservableList<File> selectedFiles;
    private final ObservableList<FileTransfer> allFileTransfers;

    private ClientLocalData() {
        this.selectedReceivers = new ArrayList<>();
        this.selectedFiles = FXCollections.observableArrayList();
        this.allFileTransfers = FXCollections.observableArrayList();
    }

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

    public ArrayList<ClientInfo> getSelectedReceivers() {
        return selectedReceivers;
    }

    public ObservableList<File> getSelectedFiles() {
        return selectedFiles;
    }

    public ObservableList<FileTransfer> getAllFileTransfers() {
        return allFileTransfers;
    }
}
