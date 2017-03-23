package chat.network;

/**
 * Created by Администратор on 23.03.2017.
 */
public class ServerSocketThread extends Thread{

    public ServerSocketThread(String name){
        super(name);
        start();
    }

    @Override
    public void run() {
        System.out.println("Server is on!");
        while (!isInterrupted()){
            System.out.println("It's working thread: " + getName());
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("The Interrupt break sleep");
                break;
            }
        }
        System.out.println("Server is off");
    }
}
