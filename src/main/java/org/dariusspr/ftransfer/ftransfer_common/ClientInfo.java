package org.dariusspr.ftransfer.ftransfer_common;

public class ClientInfo {
    private String name;
    private String ip, port;

    public ClientInfo() {
    }

    public ClientInfo(String name, String ip, String port) {
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void update(ClientInfo newInfo) {
        name = newInfo.name;
        ip = newInfo.ip;
        port = newInfo.port;
    }

    public static boolean isValid(ClientInfo clientInfo) {
        // TODO: add better validation
        return !(clientInfo.name.isEmpty() || clientInfo.ip.isEmpty() || clientInfo.port.isEmpty());
    }
}
