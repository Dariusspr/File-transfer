package org.dariusspr.ftransfer.ftransfer_client.service;

import java.net.Socket;
import java.util.ArrayList;

public class ReceiverManager {
    private static final ReceiverManager manager = new ReceiverManager();

    private final ArrayList<FileReceiver> activeReceivers = new ArrayList<>();

    private ReceiverManager() {}

    public static ReceiverManager get() {
        return manager;
    }

    public void addReceiver(Socket socket) {
        FileReceiver fileSender = new FileReceiver(socket);
        activeReceivers.add(fileSender);
    }


    public void closeReceiver(FileReceiver receiver) {
        receiver.close();
        activeReceivers.remove(receiver);
    }

    public void closeAll() {
        for (FileReceiver receiver : activeReceivers) {
            receiver.close();
        }
        activeReceivers.clear();
    }
}
