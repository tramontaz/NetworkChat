package chat.server.core;

import java.sql.*;

public class SQLClient {

    private static Connection connection;
    private static Statement statement;

    synchronized static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db.sqlite");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    synchronized static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized static String getNick(String login, String password){
        String request = "SELECT nickname FROM users WHERE login = '" + login + "' AND password ='" + password + "'";
        try (ResultSet resultSet = statement.executeQuery(request)){
            if (resultSet.next()) return resultSet.getString(1);
            else return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
