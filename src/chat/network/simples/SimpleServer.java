package chat.network.simples;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            try (Socket socket = serverSocket.accept()) {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String msg = in.readUTF();
                System.out.println("Got the message from client: " + msg);
                out.writeUTF(msg);
            } catch (IOException e){
                e.printStackTrace();
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
