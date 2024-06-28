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

    private final ArrayList<FileSender> undecidedSenders = new ArrayList<>();
    private final ArrayList<FileSender> inQueueSenders = new ArrayList<>();

    private SenderManager() {}

    public static SenderManager get() {
        return manager;
    }

    public void addSender(File file) {
        FileSender fileSender = new FileSender(file);
        undecidedSenders.add(fileSender);
    }

    public void sendAll(ClientInfo ...receivers) {
        setReceivers(receivers);
        for (FileSender fileSender : undecidedSenders) {
            executor.submit(fileSender);
        }
        inQueueSenders.addAll(undecidedSenders);
        undecidedSenders.clear();
    }

    public ArrayList<FileSender> getUndecidedSenders() {
        return undecidedSenders;
    }

    public ArrayList<FileSender> getInQueueSenders() {
        return inQueueSenders;
    }

    public void closeSender(FileSender sender) {
        inQueueSenders.remove(sender);
    }

    public void stop() {
        // TODO: handle better(save current states?)
        executor.shutdown();
    }

    private void setReceivers(ClientInfo ...receivers) {
        for (FileSender sender : undecidedSenders) {
            sender.setReceivers(receivers);
        }
    }
}
