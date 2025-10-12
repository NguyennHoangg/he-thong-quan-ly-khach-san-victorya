package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

public class ConnectDatabase {

    private static final String JDBC_URL = "jdbc:sqlserver://localhost:1433;databaseName=Victorya_Hotel;encrypt=true;trustServerCertificate=true";
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

    public static String HashPassWord(String matKhau) {
        String matKhauHash = BCrypt.hashpw(matKhau, BCrypt.gensalt());
        return matKhauHash; // Trả về mật khẩu đã mã hóa
    }

}
