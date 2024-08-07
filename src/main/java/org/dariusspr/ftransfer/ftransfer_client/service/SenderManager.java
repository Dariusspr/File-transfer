package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SenderManager {
    private static final SenderManager manager = new SenderManager();

    private static final int THREAD_POOL_SIZE = 5;
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final ArrayList<FileSender> senders = new ArrayList<>();

    private SenderManager() {}

    public static SenderManager get() {
        return manager;
    }

    public void addFile(File file) {
        FileSender fileSender = new FileSender(file, this);
        senders.add(fileSender);
    }

    public void sendAll(ArrayList<ClientInfo> receivers) {
        setReceivers(receivers);
        if (senders.isEmpty() || receivers.isEmpty()) {
            return;
        }

        ClientLocalData clientLocalData = ClientLocalData.getData();

        // Prepare
        senders.forEach(FileSender::initTransfer);
        senders.forEach(sender -> clientLocalData.getAllFileTransfers()
                        .add(sender.getTransfer()));

        for (FileSender fileSender : senders) {
            executor.submit(fileSender);
        }

        // Clear senders(and selected files) list after submitting it for processing
        senders.clear();
        clientLocalData.getSelectedFiles().clear();
    }

    public ArrayList<FileSender> getSenders() {
        return senders;
    }


    public void closeSender(FileSender sender) {
        sender.close();
        senders.remove(sender);
    }

    public void stop() {
        executor.shutdown();
    }

    private void setReceivers(ArrayList<ClientInfo> receivers) {
        for (FileSender sender : senders) {
            sender.setReceivers(receivers);
        }
    }

    public void deleteSender(FileSender fileSender) {
        ClientLocalData.getData().getAllFileTransfers().remove(fileSender.getTransfer());
    }
}
