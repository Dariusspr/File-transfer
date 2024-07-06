package org.dariusspr.ftransfer.ftransfer_client.data;

public interface FileTransferManager {
    public void setPaused(boolean isPaused);
    public void cancel();
    public void delete();

}
