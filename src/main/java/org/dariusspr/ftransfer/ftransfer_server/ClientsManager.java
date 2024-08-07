package org.dariusspr.ftransfer.ftransfer_server;

import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;
import org.dariusspr.ftransfer.ftransfer_server.data.ConnectedClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientsManager {
    private static final ClientsManager manager = new ClientsManager();
    private final ArrayList<ConnectedClient> connectedClients = new ArrayList<>();

    private volatile boolean isRunning = false;
    private volatile boolean isAdded, isRemoved;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long CONNECTION_CHECKING_FREQ = 1000; // MILLISECONDS

    private ClientsManager() {
    }

    public static ClientsManager get() {
        return manager;
    }

    public void start() {
        if (isRunning) {
            throw new IllegalStateException("clients Manager is already running");
        }
        isRunning = true;
        scheduler.scheduleAtFixedRate(this::checkAllClients, CONNECTION_CHECKING_FREQ, CONNECTION_CHECKING_FREQ, TimeUnit.MILLISECONDS);
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

    // Ensure that the list of connected clients is up-to-date and notifies clients about changes.
    public void checkAllClients() {
        if (isRunning && !connectedClients.isEmpty()) {
            pingAndRemoveDisconnected();
            if (isRemoved || isAdded) {
                notifyClients();
                isRemoved = false;
                isAdded = false;
            }
        }
    }

    private void pingAndRemoveDisconnected() {
        Iterator<ConnectedClient> iterator = connectedClients.iterator();
        while (iterator.hasNext()) {
            ConnectedClient client = iterator.next();
            if (client.isDisconnected()) {
                removeClient(iterator, client);
            }
        }
    }

    private void notifyClients() {

        ArrayList<ClientInfo> infoList = new ArrayList<>();
        Iterator<ConnectedClient> iterator = connectedClients.iterator();
        while (iterator.hasNext()) {
            ConnectedClient client = iterator.next();
            infoList.add(client.getClientInfo());
        }

        iterator = connectedClients.iterator();
        while (iterator.hasNext()) {
            ConnectedClient client = iterator.next();
            try {
                client.getObjectOutputStream().writeObject(infoList);
            } catch (IOException e) {
                e.printStackTrace();
                removeClient(client);
                // TODO;
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
