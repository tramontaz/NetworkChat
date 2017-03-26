package chat.client;

import chat.Cmd;
import chat.network.SocketThread;
import chat.network.SocketThreadListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, SocketThreadListener {

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 300;
    private static final String WINDOW_TITLE = "Network chat";

    private final JTextArea log = new JTextArea();
    private final JPanel upperPanel = new JPanel(new GridLayout(2, 3));
    private final JTextField fieldIpAddress = new JTextField("127.0.0.1");
    private final JTextField fieldPort = new JTextField("8189");
    private final JTextField fieldLogin = new JTextField("user_1");
    private final JPasswordField fieldPassword = new JPasswordField("pass_1");
    private final JButton btnLogin = new JButton("Login");
    private final JCheckBox checkBoxAlwaysOnTop = new JCheckBox("Always on top");

    private final JPanel bottomPanel = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("Disconnect");
    private final JTextField textFieldInputMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");

    private final JList<String> jListUsers = new JList<>();

    File file = new File("chat.txt"); // Создаём фаил, куда будем записывать сообщения из чата

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });

    }

    private ClientGUI(){
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle(WINDOW_TITLE);
        log.setEnabled(false);
        log.setLineWrap(true);

        JScrollPane jScrollPaneLog = new JScrollPane(log);
        jScrollPaneLog.setAutoscrolls(true);
        add(jScrollPaneLog, BorderLayout.CENTER);

        checkBoxAlwaysOnTop.addActionListener(this);
        btnLogin.addActionListener(this);

        upperPanel.add(fieldIpAddress);
        upperPanel.add(fieldPort);
        upperPanel.add(checkBoxAlwaysOnTop);
        upperPanel.add(fieldLogin);
        upperPanel.add(fieldPassword);
        upperPanel.add(btnLogin);
        add(upperPanel, BorderLayout.NORTH);

        bottomPanel.add(btnDisconnect, BorderLayout.WEST);
        bottomPanel.add(textFieldInputMessage, BorderLayout.CENTER);
        bottomPanel.add(btnSend, BorderLayout.EAST);
        btnSend.addActionListener(this);
        btnDisconnect.addActionListener(this);
        textFieldInputMessage.addActionListener(this);
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setVisible(false);

        JScrollPane scrollPane1JListUsers = new JScrollPane(jListUsers);
        scrollPane1JListUsers.setPreferredSize(new Dimension(150, 0));
        add(scrollPane1JListUsers, BorderLayout.EAST);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == checkBoxAlwaysOnTop ) setAlwaysOnTop(checkBoxAlwaysOnTop.isSelected());
        else if (src == btnLogin) connect();
        else if (src == btnDisconnect) socketThread.close();
        else if (src == btnSend || src == textFieldInputMessage) sendMessage();
        else throw new RuntimeException("Неизвестный src = " + src);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        e.getStackTrace();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        String msg;
        if (stackTraceElements.length == 0) msg = "Пустой stackTraceElements";
        else msg = e.getClass().getCanonicalName() + ": " +  e.getMessage() + " \n" +
                stackTraceElements[0].toString();
        JOptionPane.showMessageDialog(null, msg, "Exception", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    public static void writeToFile(File file, String data) throws FileNotFoundException { // метод реализующий запись в фаил
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file, true));
            writer.println(data);
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendMessage(){
        String msg = textFieldInputMessage.getText();
        socketThread.sendMessage(msg);
        textFieldInputMessage.setText("");
        textFieldInputMessage.grabFocus();
    }

    private void connect(){
        try {
            Socket socket = new Socket(fieldIpAddress.getText(), Integer.parseInt(fieldPort.getText()));
            socketThread = new SocketThread("SocketThread", this, socket);
        } catch (IOException e) {
            log.append("Exception: " + e.getMessage() + "\n");
        }

    }

    private SocketThread socketThread;

    // События сокета в потоке сокета:
    @Override
    public void onStartSockedThread(SocketThread socketThread, Socket socket) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append("Socked started." + "\n");
            }
        });
    }

    @Override
    public void onStopSockedThread(SocketThread socketThread, Socket socket) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append("Connection lost.\n");
                upperPanel.setVisible(true);
                bottomPanel.setVisible(false);
            }
        });
    }

    @Override
    public void onSockedIsReady(SocketThread socketThread, Socket socket) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append("Connection established.\n");
                upperPanel.setVisible(false);
                bottomPanel.setVisible(true);
                String request = Cmd.AUTH + Cmd.DELIMITER + fieldLogin.getText() +
                        Cmd.DELIMITER + new String(fieldPassword.getPassword());
                socketThread.sendMessage(request);
            }
        });
    }

    @Override
    public void onReceiveString(SocketThread socketThread, Socket socket, String value) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(value + "\n");
            }
        });
    }

    @Override
    public void onException(SocketThread socketThread, Socket socket, Exception e) {
        e.printStackTrace();
    }
}
