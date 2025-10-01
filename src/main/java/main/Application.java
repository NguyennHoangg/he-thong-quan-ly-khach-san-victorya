package main;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load FXML file nếu có
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            // Parent root = loader.load();
            
            // Tạm thời tạo scene đơn giản
            javafx.scene.control.Label label = new javafx.scene.control.Label("Hệ Thống Quản Lý Khách Sạn Victorya");
            javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(label);
            root.setAlignment(javafx.geometry.Pos.CENTER);
            root.setSpacing(20);
            root.setPadding(new javafx.geometry.Insets(50));
            
            Scene scene = new Scene(root, 800, 600);
            
            primaryStage.setTitle("Victorya Hotel Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
