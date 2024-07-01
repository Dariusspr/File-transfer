package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverServer {
    private static final ReceiverServer server = new ReceiverServer();

    private final ReceiverManager receiverManager = ReceiverManager.get();

    private ServerSocket serverSocket;
    private final ClientLocalData clientLocalData = ClientLocalData.getData();

    private volatile boolean isRunning = false;
    private final Thread listenerThread = new Thread(this::listen);

    private ReceiverServer() {
    }

    public static ReceiverServer get() {
        return server;
    }

    public void start() {
        if (isRunning) {
            throw new IllegalStateException("Receiver server is already running");
        }

        try {
            serverSocket = new ServerSocket(clientLocalData.getInfo().getPort());
        } catch (IOException e) {
            // TODO: exception reporting
            e.printStackTrace();
        }

        isRunning = true;
        listenerThread.start();
    }

    public void listen() {
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                receiverManager.addReceiver(socket);
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    // TODO: exception reporting
                    e.printStackTrace();
                }
            }
        }
    }



    public void stop() {
        if (!isRunning) {
            throw new IllegalStateException("Receiver server  is not running");
        }

        isRunning = false;
        receiverManager.closeAll();
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO: exception reporting
            e.printStackTrace();
        }
    }
}
