package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_client.data.FileTransfer;
import org.dariusspr.ftransfer.ftransfer_client.data.FileTransferManager;
import org.dariusspr.ftransfer.ftransfer_client.io.FileInput;
import org.dariusspr.ftransfer.ftransfer_client.io.FileMetaData;
import org.dariusspr.ftransfer.ftransfer_client.io.FileProcessor;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileSender implements Runnable, FileTransferManager {
    private ArrayList<ClientInfo> receivers;

    private final ArrayList<Socket> sockets = new ArrayList<>();
    private final ArrayList<DataInputStream> dataInputStreams = new ArrayList<>();
    private final ArrayList<ObjectOutputStream> objectOutputStreams = new ArrayList<>();

    private final FileProcessor fileProcessor;
    private final SenderManager senderManager;

    private final File rootFile;
    private final static int PROGRESS_UPDATE_FREQ = 300; // MILLISECONDS
    private final FileTransfer transfer;
    private  long bytesSent;
    private final long bytesToSend;

    private volatile boolean isPaused;
    private volatile boolean isCancelled;

    public FileSender(File rootFile, SenderManager senderManager) {
        this.rootFile = rootFile;
        fileProcessor = new FileProcessor(rootFile);
        this.senderManager = senderManager;
        this.transfer = new FileTransfer(this);
        bytesToSend = fileProcessor.getMetaData().size();
    }

    @Override
    public void run() {
        if (!prepareSockets()) {
            return;
        }
        prepareStreams();

        FileMetaData metaData = fileProcessor.getMetaData();
        sendAll(metaData);

        transfer.setState(FileTransfer.TransferState.SENDING);
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            scheduler.scheduleAtFixedRate(
                    this::updateTransferProgress,
                    PROGRESS_UPDATE_FREQ, PROGRESS_UPDATE_FREQ,
                    TimeUnit.MILLISECONDS);
            sendingFiles();
        }

        sendAll("end");
        bytesSent = bytesToSend;
        updateTransferProgress();
        transfer.setState(FileTransfer.TransferState.SENT);
        senderManager.closeSender(this);
    }

    private void sendingFiles() {
        FileMetaData metaData = fileProcessor.getMetaData();
        ArrayList<String> filePaths = metaData.fileTree();
        Path absoluteParent = fileProcessor.getLocalParentPath();
        for (String path : filePaths) {
            Path localPath = absoluteParent.resolve(path);

            boolean isDirectory = localPath.toFile().isDirectory();
            sendInitialFileInfo(path, isDirectory);
            if (isDirectory) {
                continue;
            }

            try (final FileInput fileInput = new FileInput()) {
                fileInput.setFile(localPath);
                byte[] chunks;
                while ((chunks = fileInput.readChunks()) != null) {
                    while(isPaused && !isCancelled) {
                        Thread.onSpinWait();
                    }
                    if (isCancelled) {
                        cancelSending();
                        return;
                    }
                    sendAll(chunks);
                    bytesSent += chunks.length;

                }
            } catch (FileNotFoundException e) {
                System.err.println(localPath + " was not found");
            } catch (Exception e) {
                System.err.println("Encountered an error while processing '" + path + "'");
            }
        }
    }

    private void sendInitialFileInfo(String path, boolean isDirectory) {
        if (isDirectory) {
            sendAll("d:" + path);
        } else {
            sendAll("f:" + path);
        }
    }

    private void cancelSending() {
        sendAll("cancel");
        transfer.setState(FileTransfer.TransferState.CANCELLED);
        updateTransferProgress();
        senderManager.closeSender(this);
        System.out.println("Not closed");
    }

    private void updateTransferProgress() {
        transfer.setProgress(bytesSent, bytesToSend);
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
                if (status == 15) { // Cancel
                    isCancelled = true;
                }
                else if (status != 1) {
                    System.err.println("Invalid response");
                    handleSocketError(index, iterator);
                }

            } catch (IOException e) {
                System.err.println("Encountered an error while sending object");
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
                System.err.println("Failed to connect to socket " + client.getName());
            }
        }
        return true;
    }

    private void prepareStreams() {
        for (Socket socket : sockets) {
            try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStreams.add(dataInputStream);
            objectOutputStreams.add(objectOutputStream);
            } catch (IOException e) {
                System.err.println("Failed to create streams for" + socket);
            }
        }
    }

    public void close() {
        for (int index = 0; index < sockets.size(); index++) {
            closeSocket(index);
        }
    }
    private void closeSocket(int index) {
        if (index < 0 || index >= sockets.size()) {
            return;
        }

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

    public void setReceivers(ArrayList<ClientInfo> receivers) {
        this.receivers = receivers;
    }

    public void initTransfer() {
        transfer.setFile(!rootFile.isDirectory());
        transfer.setName(rootFile.getName());
        transfer.setState(FileTransfer.TransferState.PENDING);
        transfer.setSize(bytesToSend);
        transfer.setProgress(0, bytesToSend);
        StringBuilder stringBuilder = new StringBuilder("To ");
        for (int i  = 0; i < receivers.size(); i++) {
            stringBuilder.append(receivers.get(i).getName());
            if (i != receivers.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        transfer.setFromTo(stringBuilder.toString());
    }

    public FileTransfer getTransfer() {
        return transfer;
    }

    @Override
    public void setPaused(boolean isPaused) {
        if (isPaused) {
            transfer.setState(FileTransfer.TransferState.PAUSED);
        } else {
            transfer.setState(FileTransfer.TransferState.SENDING);
        }
        this.isPaused = isPaused;
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }

    @Override
    public void delete() {
        isCancelled = true;
        senderManager.deleteSender(this);
    }
}
