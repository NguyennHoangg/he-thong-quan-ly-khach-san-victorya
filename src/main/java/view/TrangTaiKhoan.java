package view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Giao diện trang Tài Khoản (dạng thành phần) để chèn vào khu vực content.
 * - Thiết kế bám theo ảnh tham chiếu: phần tiêu đề trên cùng, form thông tin cá
 *   nhân dạng 2 cột, nút "Đổi mật khẩu" cạnh ô mật khẩu và nút "Lưu" ở giữa bên dưới.
 * - Lớp kế thừa BorderPane để dễ đặt vào center của container cha.
 */
public class TrangTaiKhoan extends BorderPane {

    // Khai báo các control để có thể lấy/đặt dữ liệu khi cần
    private TextField hoVaTenField;
    private TextField cccdField;
    private TextField diaChiField;
    private RadioButton gioiTinhNam;
    private RadioButton gioiTinhNu;
    private TextField ngaySinhField;
    private TextField taiKhoanField;
    private PasswordField matKhauField;
    private TextField emailField;
    private Button btnDoiMatKhau;
    private Button btnLuu;

    public TrangTaiKhoan() {
        // Padding bao quanh thẻ trắng
        setPadding(new Insets(24));

        // --- Phần tiêu đề người dùng (tên + email tóm tắt) ---
        VBox headerBox = new VBox(6);
        headerBox.setPadding(new Insets(0, 0, 16, 0));

        Label tenNguoiDung = new Label("Hoàng");
        tenNguoiDung.setStyle("-fx-font-size: 20px; -fx-font-weight: 600; -fx-text-fill: #1f2937;");

        Label emailTomTat = new Label("example@gmail.com");
        emailTomTat.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        headerBox.getChildren().addAll(tenNguoiDung, emailTomTat);
        setTop(headerBox);

        // --- Lưới form 2 cột: cột trái là nhãn, cột phải là input ---
        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(8, 0, 8, 0));

        // Hàm tạo nhãn chuẩn theo giao diện
        java.util.function.Function<String, Label> label = text -> {
            Label l = new Label(text);
            l.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
            return l;
        };

        // Ô nhập chuẩn: nền xám nhạt, bo góc 8
        java.util.function.Supplier<TextField> greyField = () -> {
            TextField tf = new TextField();
            tf.setPrefHeight(40);
            tf.setStyle("-fx-background-color: #eef2f7; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: transparent; -fx-padding: 0 12 0 12;");
            return tf;
        };

        // 1) Họ và tên
        hoVaTenField = greyField.get();
        hoVaTenField.setPromptText("Nguyễn Huy Hoàng");
        addRow(form, 0, label.apply("Họ và tên:"), hoVaTenField);

        // 2) CCCD
        cccdField = greyField.get();
        cccdField.setPromptText("0123456789");
        addRow(form, 1, label.apply("CCCD:"), cccdField);

        // 3) Địa chỉ
        diaChiField = greyField.get();
        diaChiField.setPromptText("TP.HCM");
        addRow(form, 2, label.apply("Địa Chỉ"), diaChiField);

        // 4) Giới tính: 2 RadioButton đặt ngang hàng
        HBox gioiTinhBox = new HBox(24);
        gioiTinhBox.setAlignment(Pos.CENTER_LEFT);
        ToggleGroup groupGioiTinh = new ToggleGroup();
        gioiTinhNam = new RadioButton("Nam");
        gioiTinhNu = new RadioButton("Nữ");
        gioiTinhNam.setToggleGroup(groupGioiTinh);
        gioiTinhNu.setToggleGroup(groupGioiTinh);
        // Mô phỏng "switch" nhỏ như ảnh bằng cách giữ radio mặc định và style tối giản
        gioiTinhNam.setStyle("-fx-text-fill: #374151;");
        gioiTinhNu.setStyle("-fx-text-fill: #374151;");
        gioiTinhBox.getChildren().addAll(gioiTinhNam, gioiTinhNu);
        addRow(form, 3, label.apply("Giới tính"), gioiTinhBox);

        // 5) Ngày sinh (đơn giản dùng TextField dd/MM/yyyy)
        ngaySinhField = greyField.get();
        ngaySinhField.setPromptText("27/08/2004");
        addRow(form, 4, label.apply("Ngày Sinh"), ngaySinhField);

        // 6) Tài khoản + 7) Mật khẩu + nút Đổi mật khẩu (cùng hàng)
        taiKhoanField = greyField.get();
        taiKhoanField.setPromptText("034571958");

        matKhauField = new PasswordField();
        matKhauField.setPrefHeight(40);
        matKhauField.setStyle("-fx-background-color: #eef2f7; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: transparent; -fx-padding: 0 12 0 12;");

        btnDoiMatKhau = new Button("Đổi mật khẩu");
        btnDoiMatKhau.setPrefHeight(40);
        btnDoiMatKhau.setStyle("-fx-background-color: #2F6FFF; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 0 16 0 16;");

        // HBox chứa: [Tài khoản] [Mật khẩu] [Đổi mật khẩu]
        HBox lineTaiKhoan = new HBox(12);
        lineTaiKhoan.setAlignment(Pos.CENTER_LEFT);
        // Ô tài khoản hẹp hơn 1 chút để chừa chỗ cho mật khẩu và nút
        taiKhoanField.setPrefWidth(240);
        matKhauField.setPrefWidth(240);
        lineTaiKhoan.getChildren().addAll(taiKhoanField, smallLabel("Mật Khẩu"), matKhauField, btnDoiMatKhau);
        addRow(form, 5, label.apply("Tài Khoản"), lineTaiKhoan);

        // 8) Email
        emailField = greyField.get();
        emailField.setPromptText("example@gmail.com");
        addRow(form, 6, label.apply("Email"), emailField);

        setCenter(form);

        // --- Nút Lưu đặt ở giữa phía dưới ---
        btnLuu = new Button("Lưu");
        btnLuu.setPrefHeight(44);
        btnLuu.setPrefWidth(140);
        btnLuu.setStyle("-fx-background-color: #2F6FFF; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;");

        HBox bottomBox = new HBox(btnLuu);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(24, 0, 8, 0));
        setBottom(bottomBox);
    }

    // Tạo nhãn phụ nhỏ màu xám nhạt (dùng trước ô mật khẩu cho giống ảnh)
    private Label smallLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
        return l;
    }

    // Thêm một hàng vào GridPane theo định dạng: [label] [node nhập liệu]
    private void addRow(GridPane grid, int rowIndex, Label leftLabel, javafx.scene.Node rightNode) {
        leftLabel.setAlignment(Pos.CENTER_LEFT);
        GridPane.setHalignment(leftLabel, HPos.LEFT);
        grid.add(leftLabel, 0, rowIndex);
        grid.add(rightNode, 1, rowIndex);

        // Đảm bảo cột phải (input) co giãn tốt, cột trái (nhãn) cố định vừa phải
        GridPane.setHgrow(rightNode, Priority.ALWAYS);
    }
}
