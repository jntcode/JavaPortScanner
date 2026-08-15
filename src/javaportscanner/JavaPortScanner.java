package javaportscanner;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;


public class JavaPortScanner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter IP address: ");
        String ipAddress = scanner.nextLine();
        try {
            InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            System.out.println("Invalid IP address.");
            return;
        }

        System.out.println("Scanning IP: " + ipAddress);
        System.out.println("Enter starting port: ");

        int startingPort;
        int endingPort;

        try {
            startingPort = Integer.valueOf(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid port number.");
            return;

        }
        System.out.println("Enter ending port: ");

        try {
            endingPort = Integer.valueOf(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid port number.");
            return;
        }
        if (endingPort < startingPort
                || startingPort < 1
                || startingPort > 65535
                || endingPort < 1
                || endingPort > 65535) {
            System.out.println("Error: Enter a valid port range (1-65535)");
            return;
        }

        scanPorts(ipAddress, startingPort, endingPort);

    }

    public static void scanPorts(String ipAddress, int startingPort, int endingPort) {
        for (int port = startingPort; port <= endingPort; port++) {
            PortStatus result = isPortOpen(ipAddress, port);
            System.out.println(port + ": " + result);

        }
    }

    public static PortStatus isPortOpen(String ipAddress, int port) {
        try (Socket socket = new Socket()) {

            SocketAddress address = new InetSocketAddress(ipAddress, port);
            socket.connect(address, 1000);

            return PortStatus.OPEN;

        } catch (ConnectException e) {

            return PortStatus.CLOSED;

        } catch (SocketTimeoutException e) {

            return PortStatus.FILTERED;

        } catch (IOException e) {

            return PortStatus.ERROR;
        }

    }

    enum PortStatus {
        OPEN,
        CLOSED,
        FILTERED,
        ERROR
    }

}
