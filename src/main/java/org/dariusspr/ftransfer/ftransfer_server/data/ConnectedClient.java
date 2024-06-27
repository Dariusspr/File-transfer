package org.dariusspr.ftransfer.ftransfer_server.data;

import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

import java.io.*;
import java.net.Socket;

public class ConnectedClient {
    private ClientInfo clientInfo;
    private final Socket socket;
    private final OutputStream outputStream;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    public ConnectedClient(Socket socket) throws IOException {
        this.socket = socket;
        outputStream =  socket.getOutputStream();
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public boolean isDisconnected() {
        try {
            objectOutputStream.writeObject(0);
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    public void close() {
        try {
            objectInputStream.close();
            objectOutputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
