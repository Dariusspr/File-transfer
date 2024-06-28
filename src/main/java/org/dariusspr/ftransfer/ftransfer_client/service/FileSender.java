package org.dariusspr.ftransfer.ftransfer_client.service;

import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

import java.io.File;
import java.net.Socket;

public class FileSender implements Runnable {
    private ClientInfo[] receivers;
    private Socket[] sockets;
    private final File rootFile;
    private long size;

    // TODO: open file byte stream
    /*
    TODO: recursively send files
    - Send meta data (is dir/file, name, size)
    - if file: send data by chunks(wait for positive reply before sending another chunk)
    - if dir: send meta data (is dir/file, name, size)
    -  ...
     */
    // TODO: later on: if directory, use multithreading to reduce some time
    // TODO: later on, implement pause/resume, cancel

    public FileSender(File rootFile) {
        this.rootFile = rootFile;
    }

    // TODO:  size

    @Override
    public void run() {

    }

    public void setReceivers(ClientInfo[] receivers) {
        this.receivers = receivers;
    }
}
