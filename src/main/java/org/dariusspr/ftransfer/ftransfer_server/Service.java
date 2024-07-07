package org.dariusspr.ftransfer.ftransfer_server;

import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;
import org.dariusspr.ftransfer.ftransfer_common.ServerInfo;
import org.dariusspr.ftransfer.ftransfer_server.data.ConnectedClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Service {
    private static final Service server = new Service();
    private volatile boolean isRunning = false;
    private final Thread listenerThread = new Thread(this::listenForConnections);
    private ServerSocket serverSocket;
    private final ClientsManager clientsManager = ClientsManager.get();

    private Service() {
    }

    public static Service get() {
        return server;
    }

    public void start() {
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }

        try {
            serverSocket = new ServerSocket(ServerInfo.getPort());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        clientsManager.start();
        isRunning = true;
        listenerThread.start();
    }

    private void handleClientRegistration(Socket socket) {
        try {
            ConnectedClient connectedClient = new ConnectedClient(socket);
            Object receivedObj = connectedClient.getObjectInputStream().readObject();
            if (receivedObj instanceof ClientInfo clientInfo) {
                connectedClient.setClientInfo(clientInfo);
                clientsManager.addClient(connectedClient);
            } else {
                throw new ClassNotFoundException();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void listenForConnections() {
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClientRegistration(socket)).start();
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
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
            e.printStackTrace();
        }
    }
}
