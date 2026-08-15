package javaportscanner;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.InetAddress;

public class JavaPortScanner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter IP address: ");
        String ipAddress = scanner.nextLine();
        try {
            InetAddress.getByName(ipAddress);
        } catch (UnknownHostException) {
            System.out.println("Invalid IP address.");
            return;
        }
        System.out.println("Scanning IP: " + ipAddress);
        System.out.println("Enter starting port: ");
        int startingPort = Integer.valueOf(scanner.nextLine());
        System.out.println("Enter ending port: ");
        int endingPort = Integer.valueOf(scanner.nextLine());
        if (endingPort < startingPort || startingPort < 1 || endingPort > 65535) {
            System.out.println("Error: Enter a valid port range (1-65535)");
            return;

        }
        for (int i = startingPort; i <= endingPort; i++) {
            Socket socket = new Socket();

            try (SocketAddress address = new InetSocketAddress(ipAddress, i)) {
                

                socket.connect(address, 1000);
                System.out.println("Port " + i + ": OPEN");

            } catch (ConnectException) {
                System.out.println("Port " + i + ": CLOSED");
            } catch (SocketTimeoutException) {
                System.out.println("Port " + i + ": FILTERED");

            

            }

        }

    }

}
