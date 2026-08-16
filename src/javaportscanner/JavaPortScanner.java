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
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JavaPortScanner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter IP address: ");
        String ipAddress = scanner.nextLine();
        if (!isValidTarget(ipAddress)) {
            System.out.println("Invalid target.");
            return;
        }
        System.out.println("Scanning IP: " + ipAddress);
        System.out.println("Enter starting port: ");
        int startingPort;
        int endingPort;
        try {
            startingPort = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid port number.");
            return;
        }
        System.out.println("Enter ending port: ");
        try {
            endingPort = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid port number.");
            return;
        }
        if (!isValidPortRange(startingPort, endingPort)) {
            System.out.println("Error: Enter a valid port range (1-65535)");
            return;
        }

        scanPorts(ipAddress, startingPort, endingPort);

    }

    public static void scanPorts(String ipAddress, int startingPort, int endingPort) {
        ArrayList<Integer> openPorts = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        System.out.println("Scanning " + ipAddress + " from port " + startingPort + " to " + endingPort + ".");
        for (int port = startingPort; port <= endingPort; port++) {
            int currentPort = port;
            executor.submit(() -> {
                PortStatus result = isPortOpen(ipAddress, currentPort);
                if (result == PortStatus.OPEN) {
                    synchronized (openPorts) {
                        openPorts.add(currentPort);
                    }
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        for (int port : openPorts) {
            System.out.println(port + " is Open - " + getServiceName(port));
        }
        if (openPorts.isEmpty()) {
            System.out.println("No open ports found.");
        } else {
            System.out.println("Total open ports: " + openPorts.size());
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

    public static boolean isValidTarget(String target) {
        try {
            InetAddress.getByName(target);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static boolean isValidPortRange(int startingPort, int endingPort) {
        if (startingPort >= 1 && startingPort <= 65535
                && endingPort >= 1 && endingPort <= 65535
                && endingPort >= startingPort) {
            return true;
        } else {
            return false;
        }
    }

    public static String getServiceName(int port) {
        switch (port) {
            case 21:
                return "FTP";
            case 22:
                return "SSH";
            case 23:
                return "Telnet";
            case 25:
                return "SMTP";
            case 53:
                return "DNS";
            case 80:
                return "HTTP";
            case 110:
                return "POP3";
            case 143:
                return "IMAP";
            case 443:
                return "HTTPS";
            case 3306:
                return "MySQL";
            case 5432:
                return "PostgreSQL";
            case 8080:
                return "HTTP-Alt";
            case 135:
                return "MS RPC";
            case 445:
                return "SMB";
            default:
                return "Unknown";
        }
    }
    
    enum PortStatus {
        OPEN,
        CLOSED,
        FILTERED,
        ERROR
    }
}
