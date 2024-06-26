package org.dariusspr.ftransfer.ftransfer_server.data;

import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalTime;

public class ConnectedClient extends ClientInfo {
    private final Socket socket;
    private final OutputStream outStream;
    private ObjectInputStream objectInStream;
    public ConnectedClient(ClientInfo info, Socket socket) throws IOException {
        super(info);
        this.socket = socket;
        outStream =  socket.getOutputStream();
    }
    public Socket getSocket() {
        return socket;
    }

    public boolean isDisconnected() {
        try {
            System.out.println(LocalTime.now());
            System.out.println(socket.isConnected() + " "  + socket.isBound() + " " + socket.isClosed());
            outStream.write(0);
            outStream.flush();
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    public void close() {
        try {
            objectInStream.close();
            outStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        objectInStream = objectInputStream;
    }
}
