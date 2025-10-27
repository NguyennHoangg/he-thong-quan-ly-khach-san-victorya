package config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDatabase {

    // Load .env file
    private static final Dotenv dotenv = Dotenv.configure()
            .directory(".")
            .ignoreIfMissing()
            .load();

    // Read from .env
    private static final String DB_HOST = dotenv.get("DB_HOST", "localhost");
    private static final String DB_PORT = dotenv.get("DB_PORT", "14330");
    private static final String DB_NAME = dotenv.get("DB_NAME", "Victorya_Hotel_v4");
    private static final String DB_USER = dotenv.get("DB_USERNAME", "sa");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD", "sapassword");

    private static final String JDBC_URL = String.format(
            "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true",
            DB_HOST, DB_PORT, DB_NAME);
    private static final String USER = DB_USER;
    private static final String PASSWORD = DB_PASSWORD;

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

}
