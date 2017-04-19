package chat.server.core;

import chat.Cmd;
import chat.network.ServerSockedThreadListener;
import chat.network.ServerSocketThread;
import chat.network.SocketThread;
import chat.network.SocketThreadListener;


import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ChatServer implements ServerSockedThreadListener, SocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private final Vector<SocketThread> clients = new Vector<>();
    private final DateFormat dateFormat = new SimpleDateFormat("HH.mm.ss");

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
            if (client.reconnected()){
                sendBroadcastMessage(client.getNickname() + ": reconnected", true);

            } else {
                sendBroadcastMessage(client.getNickname() + ": disconnected", true);
            }
            sendBroadcastMessage(getAllUsersMsg(), false);
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
        sendBroadcastMessage(client.getNickname() + ": " + value, true);
    }

    private void handleNonAuthorizedMsg(ChatSocketThread newClient, String value){
        String[] arr = value.split(Cmd.DELIMITER);
        if (arr.length != 3 || !arr[0].equals(Cmd.AUTH)){
            newClient.sendMessage("Authorisation message format error.");
            newClient.close();
            return;
        }
        String nickname = SQLClient.getNick(arr[1], arr[2]);
        if (nickname == null){
            newClient.sendMessage("Incorrect login/password.");
            newClient.close();
            return;
        }
        ChatSocketThread client = findClientByNickname(nickname);
        if (client != null){
            client.sendMessage("You are already logged in.");
            client.setReconnected(true);
            client.close();
        }

        newClient.setNickname(nickname);
        newClient.setAuthorized(true);
        newClient.sendMessage(Cmd.NICKNAME + Cmd.DELIMITER + nickname);
        sendBroadcastMessage( nickname + " connected", true);
        sendBroadcastMessage(getAllUsersMsg(), false);

    }

    private ChatSocketThread findClientByNickname(String nickname){
        for (int i = 0; i <clients.size() ; i++) {
            ChatSocketThread client = (ChatSocketThread) clients.get(i);
            if(!client.authorized()) continue;
            if(nickname.equals(client.getNickname())) return client;
        }
        return null;
    }

    private String getAllUsersMsg(){
        StringBuilder sb = new StringBuilder(Cmd.USERS);
        for (int i = 0; i <clients.size() ; i++) {
            ChatSocketThread client = (ChatSocketThread) clients.get(i);
            if (!client.authorized()) continue;
            sb.append(Cmd.DELIMITER).append(client.getNickname());
        }
        return sb.toString();
    }

    private void sendBroadcastMessage(String msg, boolean addTime){
        if(addTime){
            msg = dateFormat.format(System.currentTimeMillis()) + ": " + msg;
        }
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
