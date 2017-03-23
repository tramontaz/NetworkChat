package chat.network;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Администратор on 23.03.2017.
 */
public interface ServerSockedThreadListener {

    void onStartServerThread(ServerSocketThread thread);
    void onStopServerThread(ServerSocketThread thread);

    void onCreateServerSocket(ServerSocketThread thread, ServerSocket serverSocket);
    void onAcceptedSocked(ServerSocketThread thread, Socket socket);
    void onTimeOutSocket(ServerSocketThread thread, ServerSocket serverSocket);

}
