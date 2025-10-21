package view;

import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Splash Screen hiển thị trong khi ứng dụng đang loading
 */
public class SplashScreen extends Preloader {
    private Stage splashStage;
    private ProgressBar progressBar;
    private Label statusLabel;

    @Override
    public void start(Stage stage) throws Exception {
        this.splashStage = stage;
        
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white; -fx-padding: 40;");
        
        // Logo
        try {
            Image logo = new Image(getClass().getResourceAsStream("/img/Logo.png"));
            ImageView logoView = new ImageView(logo);
            logoView.setFitWidth(300);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
            root.getChildren().add(logoView);
        } catch (Exception e) {
            // Nếu không load được logo, hiển thị text
            Label titleLabel = new Label("Victorya Hotel");
            titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            root.getChildren().add(titleLabel);
        }
        
        // Status label
        statusLabel = new Label("Đang khởi động ứng dụng...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #3498db;");
        
        Label versionLabel = new Label("Version 1.0.0");
        versionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");
        
        root.getChildren().addAll(progressBar, statusLabel, versionLabel);
        
        Scene scene = new Scene(root, 600, 450);
        scene.setFill(Color.TRANSPARENT);
        
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            splashStage.hide();
        }
    }

    @Override
    public void handleProgressNotification(ProgressNotification info) {
        progressBar.setProgress(info.getProgress());
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            double progress = ((ProgressNotification) info).getProgress();
            progressBar.setProgress(progress);
            
            // Cập nhật status message theo progress (tổng 6s)
            if (progress <= 0.1) {
                statusLabel.setText("Đang khởi động hệ thống... (1/6)");
            } else if (progress <= 0.2) {
                statusLabel.setText("Đang tải cấu hình màn hình... (2/6)");
            } else if (progress <= 0.3) {
                statusLabel.setText("Đang khởi tạo môi trường... (2/6)");
            } else if (progress <= 0.5) {
                statusLabel.setText("Đang tải biểu tượng giao diện... (3/6)");
            } else if (progress <= 0.6) {
                statusLabel.setText("Đang chuẩn bị tải màn hình... (3/6)");
            } else if (progress <= 0.7) {
                statusLabel.setText("⚡ Đang tải màn hình Tìm kiếm phòng... (4/6)");
            } else if (progress <= 0.75) {
                statusLabel.setText("⚡ Đang tải màn hình Đặt phòng... (4/6)");
            } else if (progress <= 0.8) {
                statusLabel.setText("⚡ Đang tải màn hình Đổi phòng... (5/6)");
            } else if (progress <= 0.85) {
                statusLabel.setText("⚡ Đang tải màn hình Hủy phòng... (5/6)");
            } else if (progress <= 0.9) {
                statusLabel.setText("⚡ Đang tải màn hình Gia hạn phòng... (5/6)");
            } else if (progress <= 0.95) {
                statusLabel.setText("Đang hoàn thiện giao diện... (6/6)");
            } else {
                statusLabel.setText("✅ Hoàn tất! Chào mừng bạn đến với Victorya Hotel");
            }
        } else if (info instanceof StateChangeNotification) {
            handleStateChangeNotification((StateChangeNotification) info);
        }
    }
}
