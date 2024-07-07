package org.dariusspr.ftransfer.ftransfer_client.data;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FileTransfer {
    private FileTransferManager manager;
    private boolean isFile;
    private String fromTo;
    private String name;
    private String unit;
    private final SimpleObjectProperty<TransferState> state = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty progress = new SimpleDoubleProperty();
    private double size;

    public FileTransfer(FileTransferManager manager) {
        this.manager = manager;
    }

    public FileTransfer(boolean isFile, String fromTo, String name, TransferState state, float size, String unit, float progress) {
        this.isFile = isFile;
        this.fromTo = fromTo;
        this.name = name;
        this.state.set(state);
        this.size = size;
        this.unit = unit;
        this.progress.set(progress);
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getFromTo() {
        return fromTo;
    }

    public void setFromTo(String fromTo) {
        this.fromTo = fromTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransferState getState() {
        return state.get();
    }

    public void setState(TransferState state) {
        Platform.runLater(() -> this.state.set(state));
    }

    public double getSize() {
        return size;
    }

    public void setSize(long size) {
        BigDecimal bd = new BigDecimal(size);
        if (bd.compareTo(BigDecimal.valueOf(1024 * 1024 * 1024.0)) >= 0) { // GB
            BigDecimal sizeInGB = bd.divide(BigDecimal.valueOf(1024 * 1024 * 1024.0), 1, RoundingMode.HALF_UP);
            this.size = sizeInGB.doubleValue();
            unit = "GB";
        } else if (bd.compareTo(BigDecimal.valueOf(1024 * 1024.0)) >= 0) { // MB
            BigDecimal sizeInMB = bd.divide(BigDecimal.valueOf(1024 * 1024.0), 1, RoundingMode.HALF_UP);
            this.size = sizeInMB.doubleValue();
            unit = "MB";
        } else { // KB
            BigDecimal sizeInKB = bd.divide(BigDecimal.valueOf(1024.0), 1, RoundingMode.HALF_UP);
            this.size = sizeInKB.doubleValue();
            unit = "KB";
        }
    }

    public String getUnit() {
        return unit;
    }

    public double getProgress() {
        return progress.get();
    }

    public void setProgress(long progress, long total) {
        if (progress != 0 || total != 0) {
            BigDecimal bd = new BigDecimal(progress).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            Platform.runLater(() -> this.progress.set(bd.doubleValue()));

        } else {
            Platform.runLater(() -> this.progress.set(0));
        }
    }

    public SimpleObjectProperty<TransferState> stateProperty() {
        return state;
    }

    public SimpleDoubleProperty progressProperty() {
        return progress;
    }

    @Override
    public String toString() {
        return "FileTransfer{" +
                "isFile=" + isFile +
                ", fromTO=" + fromTo +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", size=" + size +
                ", unit= '" + unit + '\'' +
                ", progress=" + progress +
                '}';
    }

    public enum TransferState {
        PENDING,
        PAUSED,
        CANCELLED,
        SENT,
        RECEIVED,
        SENDING,
        RECEIVING,
        ERROR
    }

    public FileTransferManager getManager() {
        return manager;
    }
}

