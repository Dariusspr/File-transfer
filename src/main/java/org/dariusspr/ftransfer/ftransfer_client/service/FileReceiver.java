package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_client.io.FileMetaData;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReceiver {
    ReceiverManager manager = ReceiverManager.get();
    private final Socket socket;
    private ObjectInputStream objectInputStream;
    private DataOutputStream dataOutputStream;

    private FileMetaData metaData;
    private String currentFile;
    private Path localPathTmp;
    private FileOutputStream fileOutputStream;
    private final static String FILE_SAVE_PATH = "/saved/";
    private final static String FILE_WORKING_MARKER = "_tmp";

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
                System.err.println("Failure while working with '" + currentFile + "'");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        manager.closeReceiver(this);
    }

    private boolean handleReceivedObject(Object object) throws IOException {
        switch (object) {
            case String readableMessage -> {
                if (readableMessage.startsWith("f:")) {
                    setCurrentFile(readableMessage.substring(2));
                } else if (readableMessage.startsWith("d:")) {
                    createDirectory(readableMessage.substring(2));
                } else if (readableMessage.equalsIgnoreCase("end")) {
                    closeCurrentFile();
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
                save(dataMessage);
            }
            case null, default -> {
                System.err.println("Invalid passed object");
                return false;
            }
        }
        return true;
    }

    private void save(byte[] dataMessage) throws IOException {
        fileOutputStream.write(dataMessage);
    }

    private void createDirectory(String directory) throws IOException {
        Path localPath = Paths.get(FILE_SAVE_PATH + directory);
        Files.createDirectories(localPath);
    }

    private void setCurrentFile(String file) throws IOException {
        if (fileOutputStream != null) {
            closeCurrentFile();
        }

        currentFile = file;
        localPathTmp = Paths.get(FILE_SAVE_PATH + file + FILE_WORKING_MARKER);

        fileOutputStream = new FileOutputStream(localPathTmp.toFile());
    }

    private void closeCurrentFile() throws IOException {
        if (fileOutputStream == null) {
            return;
        }
        if (localPathTmp == null || currentFile == null) {
            fileOutputStream.close();
            return;
        }

        Path localPath = Paths.get(FILE_SAVE_PATH + currentFile);
        Files.move(localPathTmp, localPathTmp.resolveSibling(localPath));

        fileOutputStream.close();
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
            closeCurrentFile();
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
