package chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by Администратор on 16.03.2017.
 */
public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler{

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 300;
    private static final String WINDOW_TITLE = "Network chat";

    private final JTextArea log = new JTextArea();
    private final JPanel upper_panel = new JPanel(new GridLayout(2, 3));
    private final JTextField fieldIpAddress = new JTextField("172.0.0.1");
    private final JTextField fieldPort = new JTextField("8189");
    private final JTextField fieldLogin = new JTextField("login");
    private final JPasswordField fieldPassword = new JPasswordField("Password");
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

        JScrollPane jScrollPaneLog = new JScrollPane(log);
        add(jScrollPaneLog, BorderLayout.CENTER);

        checkBoxAlwaysOnTop.addActionListener(this);
        btnLogin.addActionListener(this);

        upper_panel.add(fieldIpAddress);
        upper_panel.add(fieldPort);
        upper_panel.add(checkBoxAlwaysOnTop);
        upper_panel.add(fieldLogin);
        upper_panel.add(fieldPassword);
        upper_panel.add(btnLogin);
        add(upper_panel, BorderLayout.NORTH);

        bottomPanel.add(btnDisconnect, BorderLayout.WEST);
        bottomPanel.add(textFieldInputMessage, BorderLayout.CENTER);
        bottomPanel.add(btnSend, BorderLayout.EAST);
        btnSend.addActionListener(this);
        textFieldInputMessage.addActionListener(this);
        add(bottomPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane1JListUsers = new JScrollPane(jListUsers);
        scrollPane1JListUsers.setPreferredSize(new Dimension(150, 0));
        add(scrollPane1JListUsers, BorderLayout.EAST);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == checkBoxAlwaysOnTop ) setAlwaysOnTop(checkBoxAlwaysOnTop.isSelected());
        else if (src == btnLogin) System.out.println("Login pressed");
        else if (src == btnDisconnect) System.out.println("Disconnect pressed");
        else if (src == btnSend || src == textFieldInputMessage) sendMessage();
//        else if (src == btnLogin) throw new RuntimeException("Всё пропало!!!");
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
        log.append("Your message: " + textFieldInputMessage.getText() + "\n");
        try {
            writeToFile(file, textFieldInputMessage.getText()); //пишем содержание в фаил и сигнализируем если файла не будет
        }catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }
        textFieldInputMessage.setText("");
        textFieldInputMessage.grabFocus();
    }
}
