package chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });

    }

    private ClientGUI(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle(WINDOW_TITLE);
        log.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(log);
        add(scrollPane, BorderLayout.CENTER);

        checkBoxAlwaysOnTop.addActionListener(this);
        btnLogin.addActionListener(this);

        Thread.setDefaultUncaughtExceptionHandler(this);

        upper_panel.add(fieldIpAddress);
        upper_panel.add(fieldPort);
        upper_panel.add(checkBoxAlwaysOnTop);
        upper_panel.add(fieldLogin);
        upper_panel.add(fieldPassword);
        upper_panel.add(btnLogin);
        add(upper_panel, BorderLayout.NORTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == checkBoxAlwaysOnTop ) setAlwaysOnTop(checkBoxAlwaysOnTop.isSelected());
//        else if (src == btnLogin) System.out.println("Login pressed");
        else if (src == btnLogin) throw new RuntimeException("Всё пропало!!!");
        else throw new RuntimeException("Неизвестный src = " + src);

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
