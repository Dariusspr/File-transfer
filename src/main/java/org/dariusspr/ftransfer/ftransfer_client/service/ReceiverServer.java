package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverServer {
    private static final ReceiverServer server = new ReceiverServer();
    private final ClientLocalData clientLocalData = ClientLocalData.getData();
    private ServerSocket serverSocket;

    private volatile boolean isRunning = false;
    private final Thread listenerThread = new Thread(this::listen);

    private ReceiverServer() {
    }

    public static ReceiverServer get() {
        return server;
    }

    public void start() {
        if (isRunning) {
            throw new IllegalStateException("Receiver manager is already running");
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
                // TODO: create file receiver
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
            throw new IllegalStateException("Receiver manager  is not running");
        }

        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO: exception reporting
            e.printStackTrace();
        }
    }
}
