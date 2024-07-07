package org.dariusspr.ftransfer.ftransfer_client.data;

public interface FileTransferManager {
    void setPaused(boolean isPaused);

    void cancel();

    void delete();

}
