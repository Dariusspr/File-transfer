package org.dariusspr.ftransfer.ftransfer_server;

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

        isRunning = true;
        listenerThread.start();
    }

    public void listen() {
        while(isRunning) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Connected");
                // TODO: prepare connections
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
            throw new IllegalStateException("Server is not running");
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO: exception reporting
            e.printStackTrace();
        }

        isRunning = false;
    }
}
