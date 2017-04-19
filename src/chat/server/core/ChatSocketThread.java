package chat.server.core;

import chat.network.SocketThread;
import chat.network.SocketThreadListener;

import java.net.Socket;

class ChatSocketThread extends SocketThread {

    private String nickname;
    private boolean authorized;
    private boolean reconnected;

    ChatSocketThread(String name, SocketThreadListener eventListener, Socket socket) {
        super(name, eventListener, socket);
    }

    boolean authorized() {return authorized;}

    void setAuthorized(boolean authorized){this.authorized = authorized;}

    String getNickname(){return nickname;}

    void setNickname(String nickname){this.nickname = nickname;}

    void setReconnected(boolean reconnected){
        this.reconnected = reconnected;
    }

    boolean reconnected(){
        return reconnected;
    }
}
