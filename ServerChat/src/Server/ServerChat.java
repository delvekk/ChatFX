package Server;

import gui.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;

public class ServerChat {
    private static HashSet<String> allUsersName = new HashSet<>();
    private static HashSet<PrintWriter> allWriters = new HashSet<>();
    private static ServerSocket serverSocket;

    public static void starServer() {
        InetAddress ipAddress = null;
        int port = Controller.getPort();
        try {
            ipAddress = InetAddress.getByName(Controller.getServerAddress());
        } catch (UnknownHostException e) {
        }
        try {
            serverSocket = new ServerSocket(port, 50, ipAddress);
            while (true) {
                new ServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
        }
    }

    public static ServerSocket getServerSocket() {
        return serverSocket;
    }


    private static class ServerThread extends Thread {
        private String userName;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private Boolean connectionStatus = false;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);


                out.println("CONNECTING");
                userName = in.readLine();
                synchronized (allUsersName) {
                    if (!allUsersName.contains(userName)) {
                        if (userName != null) {
                            allUsersName.add(userName);
                            connectionStatus = true;

                        }
                    }
                }
                if (!connectionStatus) {
                    out.println("ERROR");
                }
                if (connectionStatus) {
                    out.println("ADDED");
                    out.println(allUsersName.size());
                    allUsersName.forEach(name -> out.println(name));
                    allWriters.add(out);
                    allWriters.forEach(printWriter -> printWriter.println(userName + " JOINED"));
                    while (true) {
                        String text = in.readLine();
                        if (serverSocket.isClosed()) {
                            allWriters.forEach(printWriter -> printWriter.println("SERVER CLOSED"));
                            break;
                        }
                        if (text.equals("exit")) {
                            allWriters.forEach(printWriter -> printWriter.println(userName+ " DISCONNECTED"));
                            break;
                        }
                        for (PrintWriter writer : allWriters) {
                            if (writer.equals(out)) {
                                writer.println("Me: " + text);
                            } else {
                                writer.println(userName + ": " + text);
                            }
                        }
                    }
                }
            } catch (IOException e) {
            } finally {
                if (userName != null && connectionStatus) {
                    allUsersName.remove(userName);
                }
                if (out != null && connectionStatus) {
                    allWriters.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
