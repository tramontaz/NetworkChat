package chat.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SockedThread extends Thread {

    private Socket socket;
    private SockedThreadListener eventListener;
    DataOutputStream out;

    public SockedThread(String name, SockedThreadListener eventListener, Socket socket){
        super(name);
        this.socket = socket;
        this.eventListener = eventListener;
        start();
    }

    @Override
    public void run() {
        try{
            eventListener.onStartSockedThread(this, socket);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            eventListener.onSockedIsReady(this, socket);
            while (!isInterrupted()) {
                String msg = in.readUTF();
                eventListener.onReceiveString(this, socket, msg);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            eventListener.onStopSockedThread(this, socket);
        }

    }

    public synchronized boolean sendMessage(String message){
        try {
            out.writeUTF(message);
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            close();
            return false;
        }

    }

    public synchronized void close(){
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
