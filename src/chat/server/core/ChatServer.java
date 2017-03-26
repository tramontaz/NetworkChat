package chat.server.core;

import chat.Cmd;
import chat.network.ServerSockedThreadListener;
import chat.network.ServerSocketThread;
import chat.network.SocketThread;
import chat.network.SocketThreadListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer implements ServerSockedThreadListener, SocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private final Vector<SocketThread> clients = new Vector<>();

    public void start(int port){
        if (serverSocketThread != null && serverSocketThread.isAlive()){
            System.out.println("Server already running");
            return;
        }
        serverSocketThread = new ServerSocketThread("ServerSocketThread", this, 8189, 3000);
        SQLClient.connect();
    }

    public void stop(){
        if (serverSocketThread == null && !serverSocketThread.isAlive()){
            System.out.println("Server not running");
            return;
        }
        serverSocketThread.interrupt();
        SQLClient.disconnect();
    }


    //Набор событий Server socket thread:
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
        new ChatSocketThread(threadName, this, socket);
    }

    @Override
    public void onTimeOutSocket(ServerSocketThread thread, ServerSocket serverSocket) {

    }

    private synchronized void putLog(Thread thread, String msg){
        System.out.println(thread.getName() + ": " + msg);
    }

    // набор событий SocketTread'ов в соответствующих потоках:
    @Override
    public synchronized void onStartSockedThread(SocketThread socketThread, Socket socket) {
        putLog(socketThread, "started");
    }

    @Override
    public synchronized void onStopSockedThread(SocketThread socketThread, Socket socket) {
        putLog(socketThread, "stopped");
        ChatSocketThread client = (ChatSocketThread) socketThread;
        clients.remove(client);
        if (client.authorized()){
            sendBroadcastMessage(client.getNickname() + ": disconnected", true);
        }
    }

    @Override
    public synchronized void onSockedIsReady(SocketThread socketThread, Socket socket) {
        putLog(socketThread, "onSockedIsReady");
        clients.add(socketThread);
    }

    @Override
    public synchronized void onReceiveString(SocketThread socketThread, Socket socket, String value) {
        ChatSocketThread client = (ChatSocketThread) socketThread;
        if (!client.authorized()){
            handleNonAuthorizedMsg(client, value);
            return;
        }
        sendBroadcastMessage(value, true);
    }

    private void handleNonAuthorizedMsg(ChatSocketThread client, String value){
        String[] arr = value.split(Cmd.DELIMITER);
        if (arr.length != 3 || !arr[0].equals(Cmd.AUTH)){
            client.sendMessage("Authorisation message format error.");
            client.close();
            return;
        }
        String nickname = SQLClient.getNick(arr[1], arr[2]);
        if (nickname == null){
            client.sendMessage("Incorrect login/password.");
            client.close();
            return;
        }
        client.setNickname(nickname);
        client.setAuthorized(true);
        sendBroadcastMessage(nickname + " connected", true);

    }

    private void sendBroadcastMessage(String msg, boolean addTime){
        for (int i = 0; i <clients.size() ; i++) {
            ChatSocketThread client = (ChatSocketThread) clients.get(i);
            client.sendMessage(msg);
        }

    }

    @Override
    public synchronized void onException(SocketThread socketThread, Socket socket, Exception e) {
        e.printStackTrace();
    }
}
