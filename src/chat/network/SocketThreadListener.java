package chat.network;

import java.net.Socket;

public interface SocketThreadListener {


    void onStartSockedThread(SocketThread socketThread, Socket socket);
    void onStopSockedThread(SocketThread socketThread, Socket socket);

    void onSockedIsReady(SocketThread socketThread, Socket socket);
    void onReceiveString(SocketThread socketThread, Socket socket, String value);
    void onException(SocketThread socketThread, Socket socket, Exception e);



}
