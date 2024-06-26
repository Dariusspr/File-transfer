package org.dariusspr.ftransfer.ftransfer_server.data;

import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectedClient extends ClientInfo {
    private final Socket socket;
    private final OutputStream outputStream;
    private final ObjectInputStream objectInputStream;
    public ConnectedClient(Socket socket) throws IOException {
        this.socket = socket;
        outputStream =  socket.getOutputStream();
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public boolean isDisconnected() {
        try {
            outputStream.write(0);
            outputStream.flush();
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    public void close() {
        try {
            objectInputStream.close();
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
}
