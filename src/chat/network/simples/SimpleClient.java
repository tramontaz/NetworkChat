package chat.network.simples;

import java.io.*;
import java.net.Socket;

public class SimpleClient {
    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket("127.0.0.1", 8189);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("hi everyone!!!");
            String msg = in.readUTF();
            System.out.println("Got the message from server: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
