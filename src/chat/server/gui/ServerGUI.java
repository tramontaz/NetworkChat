package chat.server.gui;

import chat.server.core.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler{

    private static final int POS_X = 1150;
    private static final int POS_Y = 550;
    private static final int WIDTH = 200;
    private static final int HEIGHT = 100;
    private final JButton btnStart = new JButton("Start");
    private final JButton btnStop = new JButton("Stop");
    private final JPanel jPanelBtnPanel = new JPanel(new GridLayout(1, 2));

    private final ChatServer chatServer = new ChatServer();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerGUI();
            }
        });
    }

    ServerGUI(){
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        jPanelBtnPanel.add(btnStart);
        jPanelBtnPanel.add(btnStop);
        add(jPanelBtnPanel);
        btnStart.addActionListener(this);
        btnStop.addActionListener(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnStart) chatServer.start(8189);
        else if (src == btnStop) chatServer.stop();
        else throw new RuntimeException("Неизвестный источник: " + src);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.getStackTrace();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        String msg;
        if (stackTraceElements.length == 0) msg = "Пустой stackTraceElements";
        else msg = e.getClass().getCanonicalName() + ": " +  e.getMessage() + " \n" +
                stackTraceElements[0].toString();
        JOptionPane.showMessageDialog(null, msg, "Exception", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
