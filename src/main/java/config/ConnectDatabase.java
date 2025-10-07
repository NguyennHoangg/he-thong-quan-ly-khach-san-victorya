package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class ConnectDatabase {

     private static final Dotenv DOTENV = Dotenv.load();

 

    

    private static final String DB_PORT = DOTENV.get("DB_PORT");
    private static final String JDBC_URL = "jdbc:sqlserver://localhost:" + DB_PORT + ";databaseName=Victorya_Hotel;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "sapassword";
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        ConnectDatabase conn = new ConnectDatabase();
        if(conn.getConnection() != null){
            System.out.println("ok");
        }
    }
}
