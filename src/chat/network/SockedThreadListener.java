package chat.network;

import java.net.Socket;

public interface SockedThreadListener {


    void onStartSockedThread(SockedThread sockedThread, Socket socket);
    void onStopSockedThread(SockedThread sockedThread, Socket socket);

    void onSockedIsReady(SockedThread sockedThread, Socket socket);
    void onReceiveString(SockedThread sockedThread, Socket socket, String value);
    void onException(SockedThread sockedThread, Socket socket, Exception e);



}
