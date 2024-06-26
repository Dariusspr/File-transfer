package org.dariusspr.ftransfer.ftransfer_server;

public class Launcher {
    public static void main(String[] args) {
        // TODO: ability to control(set port, start, close, restart) server though cli
        Service server = Service.get();
        server.start();
    }
}
