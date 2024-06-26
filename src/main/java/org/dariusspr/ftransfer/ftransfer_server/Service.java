package org.dariusspr.ftransfer.ftransfer_server;

import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;
import org.dariusspr.ftransfer.ftransfer_server.data.ConnectedClient;
import org.dariusspr.ftransfer.ftransfer_server.data.ServerData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Service{
    private static final Service server = new Service();
    private final ServerData serverData = ServerData.getData();
    private volatile boolean isRunning = false;
    private final Thread listenerThread = new Thread(this::listen);
    private ServerSocket serverSocket;
    private ClientsManager clientsManager = ClientsManager.get();

    private Service() { }

    public static Service get() {
        return server;
    }
    public void start() {
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }

        try {
            serverSocket = new ServerSocket(serverData.getPort());
        } catch (IOException e) {
            // TODO: exception reporting
            e.printStackTrace();
        }

        clientsManager.start();
        isRunning = true;
        listenerThread.start();
    }

    public void listen() {
        while(isRunning) {
            try {
                Socket socket = serverSocket.accept();
                new Thread (() -> handleClientRegistration(socket)).start();
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    // TODO: exception reporting
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleClientRegistration(Socket socket) {
        try {
            ConnectedClient connectedClient = new ConnectedClient(socket);
            Object receivedObj = connectedClient.getObjectInputStream().readObject();
            if (receivedObj instanceof ClientInfo clientInfo) {
                connectedClient.update(clientInfo);
                clientsManager.addClient(connectedClient);
            } else {
                throw new ClassNotFoundException();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // TODO: improve
        }
    }

    public void stop() {
        if (!isRunning) {
            throw new IllegalStateException("Server is not running");
        }

        isRunning = false;
        clientsManager.stop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO: exception reporting
            e.printStackTrace();
        }
    }
}
