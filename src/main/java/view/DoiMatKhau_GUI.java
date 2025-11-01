package view;

import controller.TaiKhoan_Controller;
import controller.User_Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.NhanVien;

public class DoiMatKhau_GUI extends BorderPane {
    // Nút quay lại trang trước
    private Hyperlink linkQuayLai;
    // Nút lưu mật khẩu mới
    private Button btnLuuMatKhau;

    // Controller xử lý tài khoản
    private TaiKhoan_Controller taiKhoan_Controller = new TaiKhoan_Controller();
    // Controller xử lý người dùng
    private User_Controller user_Controller = new User_Controller();

    /**
     * Khởi tạo giao diện đổi mật khẩu
     * 
     * @param nhanVien Đối tượng nhân viên cần đổi mật khẩu
     */
    public DoiMatKhau_GUI(NhanVien nhanVien) {
        init(nhanVien);
    }

    /**
     * Khởi tạo các thành phần giao diện
     * 
     * @param nhanVien Đối tượng nhân viên cần đổi mật khẩu
     */
    private void init(NhanVien nhanVien) {
        this.setStyle("-fx-background-color:white;");
        this.setLeft(taoBoxNhapMatKhau(nhanVien));
        this.setRight(taoImageView());
    }

    /**
     * Tạo khung nhập mật khẩu mới
     * 
     * @param nhanVien Đối tượng nhân viên cần đổi mật khẩu
     * @return VBox chứa các thành phần nhập mật khẩu
     */
    private VBox taoBoxNhapMatKhau(NhanVien nhanVien) {
        VBox passWordBox = new VBox();
        passWordBox.setPrefWidth(600);
        // Reduce height so both password fields fit in the visible area
        passWordBox.setPrefHeight(450);
        passWordBox.setAlignment(Pos.TOP_LEFT);
        // Reduce top/bottom padding so the confirm password field is visible
        passWordBox.setPadding(new Insets(250, 40, 40, 120));

        // Tạo nút quay lại, khi nhấn sẽ về trang Quên mật khẩu
        linkQuayLai = new Hyperlink("< Quay lại");
        linkQuayLai.setBorder(Border.EMPTY);
        linkQuayLai.setOnAction(e -> {
            // Quay lại trang Quên mật khẩu
            Scene scene = linkQuayLai.getScene();
            if (scene != null) {
                scene.setRoot(new NhapMaXacNhan_GUI(nhanVien));
            }
        });

        // Tiêu đề giao diện
        Label lblTieuDe = new Label("Đặt lại mật khẩu mới");
        lblTieuDe.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 40));

        // Hướng dẫn nhập mật khẩu
        Label lblHuongDan = new Label("Đặt lại mật khẩu");
        lblHuongDan.setWrapText(true);

        // Ô nhập mật khẩu mới
        // Mật khẩu mới (masked) và bản visible để hiển thị khi người dùng nhấn "mắt"
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("Nhập mật khẩu mới");
        newPassword.setMaxWidth(Double.MAX_VALUE);

        TextField newPasswordVisible = new TextField();
        newPasswordVisible.setPromptText("Nhập mật khẩu mới");
        newPasswordVisible.setVisible(false);
        newPasswordVisible.setManaged(false);
        newPasswordVisible.setMaxWidth(Double.MAX_VALUE);
        // Đồng bộ nội dung giữa 2 control
        newPasswordVisible.textProperty().bindBidirectional(newPassword.textProperty());

        // Nút "mắt" để hiển thị/ẩn mật khẩu mới
        Button toggleNew = new Button("👁");
        toggleNew.setStyle("-fx-background-color: black; -fx-font-size: 14;");
        toggleNew.setOnAction(e -> {
            boolean showing = newPasswordVisible.isVisible();
            newPasswordVisible.setVisible(!showing);
            newPasswordVisible.setManaged(!showing);
            newPassword.setVisible(showing);
            newPassword.setManaged(showing);
            toggleNew.setText(showing ? "👁" : "🙈");
        });

        // Wrap new password field in HBox with toggle button
        HBox newPasswordBox = new HBox(5);
        newPasswordBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(newPassword, Priority.ALWAYS);
        HBox.setHgrow(newPasswordVisible, Priority.ALWAYS);
        newPasswordBox.getChildren().addAll(newPassword, newPasswordVisible, toggleNew);

        // Ô xác nhận mật khẩu mới (masked) và bản visible
        PasswordField comfirmPassword = new PasswordField();
        comfirmPassword.setPromptText("Xác nhận mật khẩu mới");
        comfirmPassword.setMaxWidth(Double.MAX_VALUE);

        TextField comfirmPasswordVisible = new TextField();
        comfirmPasswordVisible.setPromptText("Xác nhận mật khẩu mới");
        comfirmPasswordVisible.setVisible(false);
        comfirmPasswordVisible.setManaged(false);
        comfirmPasswordVisible.setMaxWidth(Double.MAX_VALUE);
        comfirmPasswordVisible.textProperty().bindBidirectional(comfirmPassword.textProperty());

        // Nút "mắt" để hiển thị/ẩn mật khẩu xác nhận
        Button toggleConfirm = new Button("👁");
        toggleConfirm.setStyle("-fx-background-color: black; -fx-font-size: 14;");
        toggleConfirm.setOnAction(e -> {
            boolean showing = comfirmPasswordVisible.isVisible();
            comfirmPasswordVisible.setVisible(!showing);
            comfirmPasswordVisible.setManaged(!showing);
            comfirmPassword.setVisible(showing);
            comfirmPassword.setManaged(showing);
            toggleConfirm.setText(showing ? "👁" : "🙈");
        });

        // Wrap confirm password field in HBox with toggle button
        HBox confirmPasswordBox = new HBox(5);
        confirmPasswordBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(comfirmPassword, Priority.ALWAYS);
        HBox.setHgrow(comfirmPasswordVisible, Priority.ALWAYS);
        confirmPasswordBox.getChildren().addAll(comfirmPassword, comfirmPasswordVisible, toggleConfirm);

        // Khoảng cách giữa các phần tử trong VBox
        passWordBox.setSpacing(20);

        // Nút lưu mật khẩu mới
        btnLuuMatKhau = new Button("Lưu mật khẩu");
        btnLuuMatKhau.setStyle("-fx-background-color: #515DEF; -fx-background-radius:4;");
        btnLuuMatKhau.setPrefSize(580, 40);

        // Xử lý sự kiện khi nhấn nút lưu mật khẩu
        btnLuuMatKhau.setOnAction(e -> {
            // Lấy giá trị mật khẩu mới và xác nhận
            String newPasswordText = newPassword.getText().trim();
            String comfirmPasswoordText = comfirmPassword.getText().trim();

            // Kiểm tra mật khẩu xác nhận có khớp không
            boolean result = checkComfirmPassword(newPasswordText, comfirmPasswoordText);
            if (!result) {
                // Nếu không khớp, hiển thị thông báo lỗi
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Mật khẩu xác nhận không khớp!");
                alert.showAndWait();
                return;
            }
            // Hash mật khẩu mới trước khi lưu
            String passwordHash = user_Controller.HashPassWord(newPasswordText);
            // Cập nhật mật khẩu mới vào hệ thống
            boolean isSuccess = taiKhoan_Controller.capNhatMatKhau(nhanVien.getTaiKhoan().getTenDangNhap(),
                    passwordHash);
            if (!isSuccess) {
                // Nếu cập nhật thất bại, hiển thị thông báo lỗi
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Có lỗi xảy ra. Vui lòng thử lại sau ít phút.");
                alert.showAndWait();
            }
            // Nếu thành công, hiển thị thông báo thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText("Mật khẩu đã được thay đổi thành công!");
            alert.showAndWait();
            // Quay về trang đăng nhập
            Scene scene = btnLuuMatKhau.getScene();
            if (scene != null) {
                scene.setRoot(new TrangDangNhap().createLoginPane(scene.getWidth(),scene.getHeight()));
            }
        });

        // Thêm các thành phần vào khung nhập mật khẩu
        // Use the HBox wrappers instead of raw PasswordFields so both fields show with toggle buttons
        passWordBox.getChildren().addAll(linkQuayLai, lblTieuDe, lblHuongDan, newPasswordBox, confirmPasswordBox,
                btnLuuMatKhau);
        return passWordBox;
    }

    /**
     * Tạo hình ảnh minh họa bên phải giao diện
     * 
     * @return BorderPane chứa hình ảnh minh họa
     */
    private BorderPane taoImageView() {
        // Tạo hình ảnh minh họa bên phải giao diện
        Image image = new Image(getClass().getResourceAsStream("/img/forgot_password.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(614);
        imageView.setFitHeight(845);
        imageView.setPreserveRatio(true);
        // Thêm padding cho ImageView
        BorderPane imagePane = new BorderPane(imageView);
        imagePane.setPadding(new Insets(100, 300, 100, 100)); // chỉnh padding theo ý muốn
        imageView.setSmooth(true);
        return imagePane;
    }

    /**
     * Kiểm tra mật khẩu xác nhận có khớp không
     * 
     * @param password1 Mật khẩu mới nhập
     * @param password2 Mật khẩu xác nhận
     * @return true nếu hai mật khẩu giống nhau, false nếu khác
     */
    private boolean checkComfirmPassword(String password1, String password2) {
        return password1.equals(password2);
    }
}
