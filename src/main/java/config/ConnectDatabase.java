package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt; // <-- import BCrypt

public final class ConnectDatabase {

    private static final String JDBC_URL = "jdbc:sqlserver://localhost:1433;databaseName=Victorya_Hotel;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "sapassword";

    private ConnectDatabase() {
    } // ngăn khởi tạo

    public static ConnectDatabase instance = new ConnectDatabase();

    public static Connection getConnection() {
        try {

            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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

    // Giữ tên cũ để không lỗi các chỗ đã gọi
    public static String HashPassWord(String matKhau) {
        return BCrypt.hashpw(matKhau, BCrypt.gensalt());
    }

    // Tên chuẩn camelCase
    public static String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}
