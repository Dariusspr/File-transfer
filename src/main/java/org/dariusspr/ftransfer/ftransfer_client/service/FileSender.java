package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_client.io.FileMetaData;
import org.dariusspr.ftransfer.ftransfer_client.io.FileProcessor;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class FileSender implements Runnable {
    private ClientInfo[] receivers;

    private final ArrayList<Socket> sockets = new ArrayList<>();
    private final ArrayList<DataInputStream> dataInputStreams = new ArrayList<>();
    private final ArrayList<ObjectOutputStream> objectOutputStreams = new ArrayList<>();
    private static final int CHUNK_SIZE = 65536; // 64 KB

    private final FileProcessor fileProcessor;

    // TODO: later on: if directory, use multithreading to reduce some time
    // TODO: later on: implement pause/resume, cancel

    public FileSender(File rootFile) {
        fileProcessor = new FileProcessor(rootFile);
    }

    @Override
    public void run() {
        if (!prepareSockets()) {
            return;
        };
        prepareStreams();

        FileMetaData metaData = fileProcessor.getMetaData();
        ArrayList<String> filePaths = metaData.fileTree();
        sendAll(metaData);

        Path absoluteParent = fileProcessor.getLocalParentPath();
        for (String path : filePaths) {
            Path localPath = absoluteParent.resolve(path);

            if (localPath.toFile().isDirectory()) {
                sendAll("d:" + path);
                continue;
            } else {
                sendAll("f:" + path);
            }

            try (FileInputStream fileIn = new FileInputStream(localPath.toFile())){
                byte[] chunks = new byte[CHUNK_SIZE];
                int bytesRead;
                while ((bytesRead = fileIn.read(chunks)) != -1) {
                    sendAll(Arrays.copyOf(chunks, bytesRead));
                }

            } catch (FileNotFoundException e) {
                // TODO:
                System.err.println(localPath + " was not found");
            } catch (IOException e) {
                // TODO:
                System.err.println("Failed to read " + localPath);
            }
        }

        sendAll("end");

        closeAll();
    }

    private void sendAll(Object object){

        Iterator<ObjectOutputStream> iterator = objectOutputStreams.iterator();
        while (iterator.hasNext()) {
            ObjectOutputStream stream = iterator.next();
            int index = objectOutputStreams.indexOf(stream);

            try {

                stream.writeObject(object);
                stream.flush();

                byte status = dataInputStreams.get(index).readByte();
                if (status != 1) {
                    // TODO:
                    System.err.println("Response error");
                    handleSocketError(index, iterator);
                }

            } catch (IOException e) {
                // TODO:
                System.err.println("Output error");
                handleSocketError(index, iterator);
            }
        }
    }

    private void handleSocketError(int index, Iterator<? extends Closeable> iterator) {
        closeSocket(index);

        iterator.remove();
        if (index < dataInputStreams.size()) {
            dataInputStreams.remove(index);
        }
        if (index < sockets.size()) {
            sockets.remove(index);
        }
    }


    private boolean prepareSockets() {
        if (receivers == null)
            return false;

        for (ClientInfo client : receivers) {
            try {
                Socket socket = new Socket(client.getIp(), client.getPort());
                sockets.add(socket);
            } catch (IOException e) {
                // TODO:
                System.err.println("Failed to connect to socket " + client.getName());
            }
        }
        return true;
    }

    private void prepareStreams() {
        for (Socket socket : sockets) {
            try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStreams.add(dataInputStream);
            objectOutputStreams.add(objectOutputStream);
            } catch (IOException e) {
                // TODO:
                System.err.println("Failed to create streams for" + socket);

            }
        }
    }

    private void closeAll() {
        for (int index = 0; index < sockets.size(); index++) {
            closeSocket(index);
        }
    }
    private void closeSocket(int index) {
        if (index < 0 || index >= sockets.size()) {
            return;
        }

        // TODO: handle exceptions

        if (dataInputStreams.get(index) != null) {
            try {
                dataInputStreams.get(index).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (objectOutputStreams.get(index) != null) {
            try {
                objectOutputStreams.get(index).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (sockets.get(index) != null) {
            try {
                sockets.get(index).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setReceivers(ClientInfo[] receivers) {
        this.receivers = receivers;
    }

}
