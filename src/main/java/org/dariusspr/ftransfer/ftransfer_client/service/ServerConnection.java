package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;
import org.dariusspr.ftransfer.ftransfer_common.ServerInfo;

import java.io.*;
import java.net.Socket;

public class ServerConnection {
    private static final ServerConnection serverConnection = new ServerConnection();

    private boolean isRunning = false;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;

    private ServerConnection() {}

    public static ServerConnection get() {
        return serverConnection;
    }

    public boolean start() {
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }
        try {
            socket = new Socket(ServerInfo.defaultIp, ServerInfo.defaultPort);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            //TODO:
            e.printStackTrace();
            return false;
        }
        isRunning = true;
        return true;
    }


    public void stop() {
        if (!isRunning) {
            throw new IllegalStateException("Server is not running");
        }
        try {
            isRunning = false;

            objectOutputStream.close();
            socket.close();
        } catch (IOException e) {
            //TODO:
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

}

