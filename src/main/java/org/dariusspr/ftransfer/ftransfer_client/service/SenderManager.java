package org.dariusspr.ftransfer.ftransfer_client.service;

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
        FileSender fileSender = new FileSender(file);
        senders.add(fileSender);
    }

    public void sendAll(ArrayList<ClientInfo> receivers) {
        setReceivers(receivers);
        for (FileSender fileSender : senders) {
            executor.submit(fileSender);
        }
        senders.clear();
    }

    public ArrayList<FileSender> getSenders() {
        return senders;
    }


    public void closeSender(FileSender sender) {
        sender.close();
        senders.remove(sender);
    }

    public void stop() {
        // TODO: handle better(save current states?)
        executor.shutdown();
    }

    private void setReceivers(ArrayList<ClientInfo> receivers) {
        for (FileSender sender : senders) {
            sender.setReceivers(receivers);
        }
    }
}
