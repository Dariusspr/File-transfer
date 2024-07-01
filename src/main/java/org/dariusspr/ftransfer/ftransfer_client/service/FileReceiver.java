package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_client.io.FileIO;
import org.dariusspr.ftransfer.ftransfer_client.io.FileMetaData;

import java.io.*;
import java.net.Socket;

public class FileReceiver {
    private final ReceiverManager manager = ReceiverManager.get();
    private FileIO fileIO;
    private final Socket socket;
    private ObjectInputStream objectInputStream;
    private DataOutputStream dataOutputStream;

    private FileMetaData metaData;

    private  volatile boolean isFinished;
    private final Thread receiveThread = new Thread(this::receive);

    private void receive() {
        if (!prepareStreams()) {
            manager.closeReceiver(this);
            return;
        }

        while (!isFinished) {
            try {
                Object object = objectInputStream.readObject();

                if (handleReceivedObject(object)) {
                    dataOutputStream.writeByte(1);
                } else {
                    dataOutputStream.writeByte(0);
                }

            } catch (IOException e) {
                System.err.println("Failure while working with '" + fileIO.getFile() + "'");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        manager.closeReceiver(this);
    }

    private boolean handleReceivedObject(Object object) throws IOException {
        fileIO = new FileIO();

        switch (object) {
            case String readableMessage -> {
                if (readableMessage.startsWith("f:")) {
                    fileIO.setFile(readableMessage.substring(2));
                } else if (readableMessage.startsWith("d:")) {
                    FileIO.createDirectory(readableMessage.substring(2));
                } else if (readableMessage.equalsIgnoreCase("end")) {
                    fileIO.closeFile();
                    isFinished = true;
                }
                else {
                    System.err.println("Invalid readableMessage: '" + readableMessage + "'");
                }
            }
            case FileMetaData metadataMessage -> {
                metaData = metadataMessage;
            }
            case byte[] dataMessage -> {
                fileIO.append(dataMessage);
            }
            case null, default -> {
                System.err.println("Invalid passed object");
                return false;
            }
        }
        return true;
    }

    public FileReceiver(Socket socket) {
        this.socket = socket;
        isFinished = false;
        receiveThread.start();
    }

    private boolean prepareStreams() {
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        isFinished = false;
        receiveThread.interrupt();

        try {
            fileIO.closeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (objectInputStream != null) {
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO:
                e.printStackTrace();
            }
        }
    }
}
