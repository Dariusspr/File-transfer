package org.dariusspr.ftransfer.ftransfer_client.service;

import javafx.application.Platform;
import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;
import org.dariusspr.ftransfer.ftransfer_client.data.FileTransfer;

import java.net.Socket;
import java.util.ArrayList;

public class ReceiverManager {
    private static final ReceiverManager manager = new ReceiverManager();
    private final ClientLocalData clientLocalData = ClientLocalData.getData();
    private final ArrayList<FileReceiver> activeReceivers = new ArrayList<>();

    private ReceiverManager() {}

    public static ReceiverManager get() {
        return manager;
    }

    public void addReceiver(Socket socket) {
        FileReceiver fileSender = new FileReceiver(socket, this);
        activeReceivers.add(fileSender);
    }


    public void closeReceiver(FileReceiver receiver) {
        receiver.close();
        activeReceivers.remove(receiver);
    }

    public void addTransfer(FileReceiver receiver) {
        Platform.runLater(() ->clientLocalData.getAllFileTransfers().add(receiver.getTransfer()));
    }

    public void closeAll() {
        for (FileReceiver receiver : activeReceivers) {
            receiver.close();
        }
        activeReceivers.clear();
    }

    public void deleteSender(FileReceiver fileReceiver) {
        ClientLocalData.getData().getAllFileTransfers().remove(fileReceiver.getTransfer());
    }
}
