package org.dariusspr.ftransfer.ftransfer_common;

import java.util.regex.Pattern;

public class ServerInfo {
    public static final String defaultIp = "localhost";
    public static final int defaultPort = 9000;

    private static String serverIp = null;
    private static int serverPort = -1;


    public static String getIp() {
        return serverIp == null ? defaultIp :serverIp;
    }

    public static int getPort() {
        return serverPort == -1 ? defaultPort :serverPort;
    }
    private static final Pattern IP_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    public static boolean isValid(String ip, int port) {
        return isValidIP(ip) && isValidPort(port);
    }

    private static boolean isValidIP(String ip) {
        return IP_PATTERN.matcher(ip).matches() || ip.equals("localhost");
    }

    private static boolean isValidPort(int port) {
        try {
            return port >= 1 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void setIp(String ip) {
        serverIp = ip;
    }

    public static void setPort(int port) {
        serverPort = port;
    }
}
