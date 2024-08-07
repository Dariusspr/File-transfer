package org.dariusspr.ftransfer.ftransfer_common;

import java.io.Serializable;

public class ClientInfo implements Serializable {
    private String name;
    private String ip;
    private int port;

    public ClientInfo() {
    }

    public ClientInfo(ClientInfo info) {
        this.name = info.getName();
        this.ip = info.getIp();
        this.port = info.getPort();
    }

    public ClientInfo(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void update(ClientInfo newInfo) {
        name = newInfo.name;
        ip = newInfo.ip;
        port = newInfo.port;
    }

    public static boolean isValid(ClientInfo clientInfo) {
        return !(clientInfo.name.isEmpty() || clientInfo.ip.isEmpty() || idValidPort(clientInfo.port));
    }

    private static boolean idValidPort(int port) {
        return port < 0 || port > 65535;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
