package org.dariusspr.ftransfer.ftransfer_client.data;


import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FileTransfer {

    private boolean isFile;
    private String fromTo;
    private String name;
    private TransferState state;
    private double size;
    private String unit;
    private double progress;

    public FileTransfer() {}

    public FileTransfer(boolean isFile, String fromTo, String name, TransferState state, float size, String unit, float progress) {
        this.isFile = isFile;
        this.fromTo = fromTo;
        this.name = name;
        this.state = state;
        this.size = size;
        this.unit = unit;
        this.progress = progress;
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
        return state;
    }

    public void setState(TransferState state) {
        this.state = state;
    }

    public double getSize() {
        return size;
    }

    public void setSize(long size) {
        BigDecimal bd = new BigDecimal(size);
        if (bd.compareTo(BigDecimal.valueOf(1024 * 1024 * 1024.0)) >= 0) {
            BigDecimal sizeInGB = bd.divide(BigDecimal.valueOf(1024 * 1024 * 1024.0), 1, RoundingMode.HALF_UP);
            this.size = sizeInGB.doubleValue();
            unit = "GB";
        } else if (bd.compareTo(BigDecimal.valueOf(1024 * 1024.0)) >= 0) {
            BigDecimal sizeInMB = bd.divide(BigDecimal.valueOf(1024 * 1024.0), 1, RoundingMode.HALF_UP);
            this.size = sizeInMB.doubleValue();
            unit = "MB";
        } else {
            BigDecimal sizeInKB = bd.divide(BigDecimal.valueOf(1024.0), 1, RoundingMode.HALF_UP);
            this.size = sizeInKB.doubleValue();
            unit = "KB";
        }
    }

    public String getUnit() {
        return unit;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        BigDecimal bd = new BigDecimal(progress);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        this.progress = bd.doubleValue();
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
        SENT,
        RECEIVED,
        SENDING,
        RECEIVING,
        ERROR
    }
}

