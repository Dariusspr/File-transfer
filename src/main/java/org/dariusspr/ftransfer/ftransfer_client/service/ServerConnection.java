package org.dariusspr.ftransfer.ftransfer_client.service;

import javafx.application.Application;
import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;
import org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;
import org.dariusspr.ftransfer.ftransfer_common.ServerInfo;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServerConnection {
    private static final ServerConnection serverConnection = new ServerConnection();
    private boolean isRunning = false;

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final Thread listenThread = new Thread(this::listenForUpdates);

    private final ClientLocalData clientLocalData = ClientLocalData.getData();

    private ServerConnection() {}

    public static ServerConnection get() {
        return serverConnection;
    }

    public boolean start() {
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }
        try {
            initConnection();
            register();
            listenThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        isRunning = true;
        return true;
    }

    private void initConnection() throws IOException {
        socket = new Socket(ServerInfo.getIp(), ServerInfo.getPort());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    private void register() throws IOException {
        objectOutputStream.writeObject(clientLocalData.getInfo());
        objectOutputStream.flush();
    }

    private void listenForUpdates() {
        while (isRunning) {
            try {
                Object object = objectInputStream.readObject();
                handleClientInfo(object);
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    e.printStackTrace();
                    ClientApplication.close();
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleClientInfo(Object object) {
        if (object instanceof ArrayList<?> arrayList) {
            if (!arrayList.isEmpty() && arrayList.getFirst() instanceof ClientInfo) {
                @SuppressWarnings("unchecked") ArrayList<ClientInfo> newAvailableList = (ArrayList<ClientInfo>) arrayList;
                newAvailableList.removeIf(client -> client.toString().equals(clientLocalData.getInfo().toString()));
                clientLocalData.getAvailableClients().setAll(newAvailableList);
            }
        }
    }

    public void stop() {
        if (!isRunning) {
            throw new IllegalStateException("Server is not running");
        }
        try {
            isRunning = false;
            objectInputStream.close();
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

    public static String getLocalIp() {
        InetAddress localHost;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        String ipAddress = localHost.getHostAddress();
        return ipAddress;
    }
}

