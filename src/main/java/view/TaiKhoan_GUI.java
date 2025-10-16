package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

/**
 * Trang tài khoản - thiết kế hiện đại và gọn gàng
 */
public class TaiKhoan_GUI extends BorderPane {

    private TextField txtHoTen, txtCCCD, txtDiaChi, txtNgaySinh, txtTaiKhoan, txtEmail;
    private PasswordField txtMatKhau;
    private RadioButton rbNam, rbNu;

    public TaiKhoan_GUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        VBox container = new VBox(20);
        container.setMaxWidth(950);
        container.setAlignment(Pos.TOP_CENTER);

        container.getChildren().addAll(createHeader(), createMainCard());
        setCenter(container);
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        Circle avatar = new Circle(35);
        avatar.setFill(Color.web("#3b82f6"));
        avatar.setStroke(Color.web("#60a5fa"));
        avatar.setStrokeWidth(2.5);

        VBox info = new VBox(3);
        Label name = new Label("Nguyễn Huy Hoàng");
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label email = new Label("hoang@example.com");
        email.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        info.getChildren().addAll(name, email);

        header.getChildren().addAll(avatar, info);
        return header;
    }

    private VBox createMainCard() {
        VBox card = new VBox(18);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        // Section 1: Thông tin cá nhân
        Label title1 = new Label("📋 Thông tin cá nhân");
        title1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox row1 = new HBox(12);
        txtHoTen = createField("Nguyễn Huy Hoàng");
        txtCCCD = createField("0123456789");
        row1.getChildren().addAll(createFieldGroup("Họ và tên", txtHoTen), createFieldGroup("CCCD", txtCCCD));

        HBox row2 = new HBox(12);
        txtDiaChi = createField("TP. Hồ Chí Minh");
        txtNgaySinh = createField("27/08/2004");
        row2.getChildren().addAll(createFieldGroup("Địa chỉ", txtDiaChi), createFieldGroup("Ngày sinh", txtNgaySinh));

        HBox row3 = new HBox(12);
        VBox genderGroup = new VBox(6);
        Label genderLabel = new Label("Giới tính");
        genderLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        HBox genderBox = new HBox(15);
        ToggleGroup group = new ToggleGroup();
        rbNam = new RadioButton("Nam");
        rbNu = new RadioButton("Nữ");
        rbNam.setToggleGroup(group);
        rbNu.setToggleGroup(group);
        rbNam.setSelected(true);
        genderBox.getChildren().addAll(rbNam, rbNu);
        genderGroup.getChildren().addAll(genderLabel, genderBox);
        
        VBox spacer = new VBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row3.getChildren().addAll(genderGroup, spacer);

        // Separator
        javafx.scene.shape.Line line = new javafx.scene.shape.Line();
        line.setStroke(Color.web("#e5e7eb"));
        line.setStrokeWidth(1);
        line.setStartX(0);
        line.setEndX(900);

        // Section 2: Thông tin tài khoản
        Label title2 = new Label("🔐 Thông tin tài khoản");
        title2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox row4 = new HBox(12);
        txtTaiKhoan = createField("hoang123");
        txtEmail = createField("hoang@example.com");
        row4.getChildren().addAll(createFieldGroup("Tên tài khoản", txtTaiKhoan), createFieldGroup("Email", txtEmail));

        HBox row5 = new HBox(10);
        VBox passGroup = createFieldGroup("Mật khẩu", null);
        txtMatKhau = new PasswordField();
        txtMatKhau.setPromptText("••••••••");
        txtMatKhau.setPrefHeight(38);
        txtMatKhau.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 12;");
        passGroup.getChildren().add(txtMatKhau);
        HBox.setHgrow(passGroup, Priority.ALWAYS);

        Button btnChange = new Button("🔑 Đổi mật khẩu");
        btnChange.setPrefHeight(38);
        btnChange.setPrefWidth(140);
        btnChange.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;");
        
        row5.getChildren().addAll(passGroup, btnChange);
        row5.setAlignment(Pos.BOTTOM_LEFT);

        Button btnSave = new Button("💾 Lưu thay đổi");
        btnSave.setPrefWidth(180);
        btnSave.setPrefHeight(40);
        btnSave.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8;");

        HBox saveBox = new HBox(btnSave);
        saveBox.setAlignment(Pos.CENTER);
        saveBox.setPadding(new Insets(5, 0, 0, 0));

        card.getChildren().addAll(title1, row1, row2, row3, line, title2, row4, row5, saveBox);
        return card;
    }

    private TextField createField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        field.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 12;");
        return field;
    }

    private VBox createFieldGroup(String label, TextField field) {
        VBox group = new VBox(6);
        HBox.setHgrow(group, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");

        if (field != null) {
            group.getChildren().addAll(lbl, field);
        } else {
            group.getChildren().add(lbl);
        }
        return group;
    }
}
