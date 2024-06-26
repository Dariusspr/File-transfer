package org.dariusspr.ftransfer.ftransfer_server;

import org.dariusspr.ftransfer.ftransfer_server.data.ConnectedClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientsManager {
    private  static final ClientsManager manager = new ClientsManager();
    private final ArrayList<ConnectedClient> connectedClients = new ArrayList<>();

    private volatile boolean isRunning = false;
    private volatile boolean isAdded, isRemoved;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long CONNECTION_CHECKING_FREQ = 500; // in ms


    public static ClientsManager get() {
        return manager;
    }

    public void start() {
        if (isRunning) {
            throw new IllegalStateException("clients Manager is already running");
        }

        isRunning = true;
        scheduler.scheduleAtFixedRate(this::checkClients, CONNECTION_CHECKING_FREQ, CONNECTION_CHECKING_FREQ, TimeUnit.MICROSECONDS);
    }


    public void addClient(ConnectedClient client) {
        connectedClients.add(client);
        isAdded = true;
    }

    public void removeClient(ConnectedClient client) {
        client.close();
        connectedClients.remove(client);
        isRemoved = true;
    }

    public void removeClient(Iterator<ConnectedClient> iterator, ConnectedClient client) {
        client.close();
        iterator.remove();
        isRemoved = true;
    }


    private ClientsManager() {
    }

    public void checkClients() {
        if (isRunning && !connectedClients.isEmpty()) {

            Iterator<ConnectedClient> iterator = connectedClients.iterator();
            while(iterator.hasNext()) {
                ConnectedClient client = iterator.next();
                if (client.isDisconnected()) {
                    removeClient(iterator, client);
                }
            }

            if (isRemoved || isAdded) {
                System.out.println("notify clients");
                // TODO: alert other clients about change in list
                isRemoved = false;
                isAdded = false;
            }
        }
    }

    private void closeClients() {
        for (ConnectedClient connectedClient : connectedClients) {
            connectedClient.close();
        }
    }

    public void stop() {
        if (!isRunning) {
            throw new IllegalStateException("clients Manager is not running");
        }

        isRunning = false;
        closeClients();
        scheduler.shutdown();
    }
}
