package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class About_GUI extends Stage {

    public About_GUI() {
        initializeUI();
    }

    private void initializeUI() {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Giới thiệu - Victorya Hotel");
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");
        root.setPadding(new Insets(30));
        
        // Top - Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 20, 0));
        
        Label title = new Label("VICTORYA HOTEL");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#667eea"));
        
        Label subtitle = new Label("Hệ thống quản lý khách sạn - Phiên bản 1.0.0");
        subtitle.setFont(Font.font("System", 16));
        subtitle.setTextFill(Color.web("#64748b"));
        
        header.getChildren().addAll(title, subtitle);
        
        // Center - Content
        HBox content = new HBox(40);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20, 0, 20, 0));
        
        // Left column - Features
        VBox leftColumn = new VBox(10);
        leftColumn.setPrefWidth(350);
        
        Label featuresTitle = new Label("TÍNH NĂNG");
        featuresTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        featuresTitle.setTextFill(Color.web("#1e293b"));
        
        VBox featuresList = new VBox(8);
        featuresList.getChildren().addAll(
            createInfoLabel("• Quản lý đặt phòng và nhận phòng"),
            createInfoLabel("• Quản lý ca làm việc"),
            createInfoLabel("• Thanh toán (Tiền mặt, MoMo)"),
            createInfoLabel("• Quản lý khách hàng và nhân viên"),
            createInfoLabel("• Báo cáo thống kê"),
            createInfoLabel("• Quản lý khuyến mãi")
        );
        
        leftColumn.getChildren().addAll(featuresTitle, featuresList);
        
        // Right column - Info
        VBox rightColumn = new VBox(10);
        rightColumn.setPrefWidth(350);
        
        Label teamTitle = new Label("ĐỘI NGŨ");
        teamTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        teamTitle.setTextFill(Color.web("#1e293b"));
        
        VBox teamList = new VBox(8);
        teamList.getChildren().addAll(
            createInfoLabel("Nguyễn Huy Hoàng - Team Leader"),
            createInfoLabel("Đinh Tấn Khiêm - "),
            createInfoLabel("Lê Thanh Tùng - Time Keeper"),
            createInfoLabel("Nguyễn Văn Trường - Database Designer")
        );
        
        Label contactTitle = new Label("LIÊN HỆ");
        contactTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        contactTitle.setTextFill(Color.web("#1e293b"));
        contactTitle.setPadding(new Insets(20, 0, 0, 0));
        
        VBox contactList = new VBox(8);
        contactList.getChildren().addAll(
            createInfoLabel("Email: contact@victorya-hotel.com"),
            createInfoLabel("Phone: +84 123 456 789"),
            createInfoLabel("Website: www.victorya-hotel.com")
        );
        
        rightColumn.getChildren().addAll(teamTitle, teamList, contactTitle, contactList);
        
        content.getChildren().addAll(leftColumn, rightColumn);
        
        // Bottom - Button
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));
        
        Button closeBtn = new Button("Đóng");
        closeBtn.setPrefWidth(120);
        closeBtn.setPrefHeight(35);
        closeBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> this.close());
        
        footer.getChildren().add(closeBtn);
        
        root.setTop(header);
        root.setCenter(content);
        root.setBottom(footer);
        
        Scene scene = new Scene(root, 900, 550);
        this.setScene(scene);
    }
    
    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", 14));
        label.setTextFill(Color.web("#475569"));
        return label;
    }
    
    public static void showDialog() {
        About_GUI about = new About_GUI();
        about.showAndWait();
    }
    
    public void display() {
        this.showAndWait();
    }
}
