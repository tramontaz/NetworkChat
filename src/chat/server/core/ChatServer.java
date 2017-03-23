package chat.server.core;

import chat.network.ServerSocketThread;

public class ChatServer {
    private ServerSocketThread serverSocketThread;

    public void start(int port){
        if (serverSocketThread != null && serverSocketThread.isAlive()){
            System.out.println("Server already running");
            return;
        }
        serverSocketThread = new ServerSocketThread("ServerSocketThread");
    }

    public void stop(){
        if (serverSocketThread == null && !serverSocketThread.isAlive()){
            System.out.println("Server not running");
            return;
        }
        serverSocketThread.interrupt();
    }
}
