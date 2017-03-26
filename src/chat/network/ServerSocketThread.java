package chat.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread{

    ServerSockedThreadListener eventListener;
    int port;
    private int timeout;

    public ServerSocketThread(String name, ServerSockedThreadListener eventListener, int port, int timeout){
        super(name);
        this.port = port;
        this.timeout = timeout;
        this.eventListener = eventListener;
        start();
    }

    @Override
    public void run() {
        eventListener.onStartServerThread(this);
        try(ServerSocket serverSocket = new ServerSocket(port)){
            serverSocket.setSoTimeout(timeout);
            eventListener.onCreateServerSocket(this, serverSocket);
            Socket socket;
            while (!isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    eventListener.onTimeOutSocket(this, serverSocket);
                    continue;
                }
                eventListener.onAcceptedSocked(this, socket);
            }
        }catch (IOException e){     // можно в интерфейсе сделать параметр onException и обрабатывать его
            throw new RuntimeException(e);
        }finally {
            eventListener.onStopServerThread(this);
        }

    }
}
