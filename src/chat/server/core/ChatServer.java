package chat.server.core;

import chat.network.ServerSockedThreadListener;
import chat.network.ServerSocketThread;
import chat.network.SockedThread;
import chat.network.SockedThreadListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer implements ServerSockedThreadListener, SockedThreadListener{
    private ServerSocketThread serverSocketThread;

    public void start(int port){
        if (serverSocketThread != null && serverSocketThread.isAlive()){
            System.out.println("Server already running");
            return;
        }
        serverSocketThread = new ServerSocketThread("ServerSocketThread", this, 8189, 3000);
    }

    public void stop(){
        if (serverSocketThread == null && !serverSocketThread.isAlive()){
            System.out.println("Server not running");
            return;
        }
        serverSocketThread.interrupt();
    }

    @Override
    public void onStartServerThread(ServerSocketThread thread) {
        putLog(thread, "Thread is started");
    }

    @Override
    public void onStopServerThread(ServerSocketThread thread) {
        putLog(thread, "thread is stopped");
    }

    @Override
    public void onCreateServerSocket(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog(thread, "onCreateServerSocket");
    }

    @Override
    public void onAcceptedSocked(ServerSocketThread thread, Socket socket) {
        putLog(thread, "Client connected: " + socket);   // .toString() добавляется автоматически при сложении со строкой
        String threadName = "Socket name: " + socket.getInetAddress() + socket.getPort();
        new SockedThread(threadName, this, socket);
    }

    @Override
    public void onTimeOutSocket(ServerSocketThread thread, ServerSocket serverSocket) {

    }

    void putLog(Thread thread, String msg){
        System.out.println(thread.getName() + ": " + msg);
    }

    // события SocketTread'ов в соответствующих потоках:
    @Override
    public synchronized void onStartSockedThread(SockedThread sockedThread, Socket socket) {
        putLog(sockedThread, "started");
    }

    @Override
    public synchronized void onStopSockedThread(SockedThread sockedThread, Socket socket) {
        putLog(sockedThread, "stopped");
    }

    @Override
    public synchronized void onSockedIsReady(SockedThread sockedThread, Socket socket) {
        putLog(sockedThread, "onSockedIsReady");
    }

    @Override
    public synchronized void onReceiveString(SockedThread sockedThread, Socket socket, String value) {
        sockedThread.sendMessage(value);
    }
}
