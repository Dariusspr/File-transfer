package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_client.data.FileTransfer;
import org.dariusspr.ftransfer.ftransfer_client.data.FileTransferManager;
import org.dariusspr.ftransfer.ftransfer_client.io.FileOutput;
import org.dariusspr.ftransfer.ftransfer_client.io.FileMetaData;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileReceiver implements FileTransferManager {
    private final ReceiverManager manager = ReceiverManager.get();
    private final ReceiverManager receiverManager;
    private FileMetaData metaData;

    private final FileOutput fileOutput = new FileOutput();

    private final Socket socket;
    private ObjectInputStream objectInputStream;
    private DataOutputStream dataOutputStream;

    private final Thread receiveThread = new Thread(this::initReceive);
    private volatile boolean isFinished;
    private volatile boolean isCancelled;
    private volatile boolean isPaused;

    private final static int PROGRESS_UPDATE_FREQ = 1000; // MILLISECONDS

    private FileTransfer transfer;
    private long bytesReceived;
    private long bytesToReceive;

    public FileReceiver(Socket socket, ReceiverManager receiverManager) {
        this.socket = socket;
        isFinished = false;
        this.receiverManager = receiverManager;
        receiveThread.start();
    }

    private void initReceive() {
        if (!prepareStreams()) {
            manager.closeReceiver(this);
            return;
        }

        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            scheduler.scheduleAtFixedRate(
                    this::updateTransferProgress,
                    PROGRESS_UPDATE_FREQ, PROGRESS_UPDATE_FREQ,
                    TimeUnit.MILLISECONDS);
            receiving();
        }
        manager.closeReceiver(this);
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

    private void receiving() {
        while (!isFinished) {
            try {
                while (isPaused && !isCancelled) {
                    Thread.onSpinWait();
                }
                Object object = objectInputStream.readObject();

                if (isCancelled) {
                    dataOutputStream.writeByte(15);
                }
                if (handleReceivedObject(object)) { // Valid message
                    dataOutputStream.writeByte(1);
                } else { // Invalid message
                    dataOutputStream.writeByte(0);
                }

            } catch (IOException e) {
                System.err.println("Encountered an error while processing file '" + fileOutput.getFile() + "'");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean handleReceivedObject(Object object) throws IOException {
        switch (object) {
            case String readableMessage -> {
                handleReadableMessage(readableMessage);
            }
            case FileMetaData metadataMessage -> {
                metaData = metadataMessage;
                initTransfer();
            }
            case byte[] dataMessage -> {
                fileOutput.append(dataMessage);
                bytesReceived += dataMessage.length;
            }
            default -> {
                System.err.println("Invalid received object");
                return false;
            }
        }
        return true;
    }

    private void handleReadableMessage(String readableMessage) throws IOException {
        if (readableMessage.startsWith("f:")) {
            fileOutput.setFile(readableMessage.substring(2));
        } else if (readableMessage.startsWith("d:")) {
            FileOutput.createDirectory(readableMessage.substring(2));
        } else if (readableMessage.equalsIgnoreCase("end")) {
            fileOutput.closeFile();
            isFinished = true;
            bytesToReceive = bytesReceived;
            updateTransferProgress();
            transfer.setState(FileTransfer.TransferState.RECEIVED);
        } else if (readableMessage.equalsIgnoreCase("cancel")) {
            isCancelled = true;
            isFinished = true;
            transfer.setState(FileTransfer.TransferState.CANCELLED);
            updateTransferProgress();
        } else {
            System.err.println("Invalid readableMessage: '" + readableMessage + "'");
        }
    }

    private void initTransfer() {
        transfer = new FileTransfer(this);
        transfer.setState(FileTransfer.TransferState.RECEIVING);
        bytesToReceive = metaData.size();
        transfer.setSize(bytesToReceive);
        String root = metaData.fileTree().getFirst();
        transfer.setFile(!new File(root).isDirectory());
        transfer.setName(root);
        transfer.setFromTo("From " + metaData.sender());
        transfer.setProgress(0, bytesToReceive);

        receiverManager.addTransfer(this);
    }

    public void close() {
        isFinished = true;
        receiveThread.interrupt();

        try {
            fileOutput.closeFile();
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

    private void updateTransferProgress() {
        transfer.setProgress(bytesReceived, bytesToReceive);
    }


    public FileTransfer getTransfer() {
        return transfer;
    }


    @Override
    public void setPaused(boolean isPaused) {
        if (isPaused) {
            transfer.setState(FileTransfer.TransferState.PAUSED);
        } else {
            transfer.setState(FileTransfer.TransferState.RECEIVED);
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
        receiverManager.deleteSender(this);
    }
}
