package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.NhanVien;
import notification.service.OTPService;

public class NhapMaXacNhan_GUI extends BorderPane {
    private Hyperlink linkQuayLai;
    private Button btnXacNhan;
    private String otpCode;


    public NhapMaXacNhan_GUI(NhanVien nhanVien) {
        init(nhanVien);
    }

    private void init(NhanVien nhanVien) {
        this.setStyle("-fx-background-color:white;");
        this.setLeft(taoPaneTrai(nhanVien));
        this.setRight(taoImageView());
    }

    private VBox taoPaneTrai(NhanVien nhanVien) {
        VBox quenMatKhauBox = new VBox(20);
        quenMatKhauBox.setStyle("-fx-background-color:white;");
        quenMatKhauBox.setPrefWidth(600);
        quenMatKhauBox.setPrefHeight(600);
        quenMatKhauBox.setAlignment(Pos.TOP_LEFT);
        quenMatKhauBox.setPadding(new Insets(250, 40, 200, 200));

        // button quay lai
        linkQuayLai = new Hyperlink("< Quay lại");
        linkQuayLai.setBorder(Border.EMPTY);
        linkQuayLai.setOnAction(e -> {
            // Quay lại trang đăng nhập
            Scene scene = linkQuayLai.getScene();
            if (scene != null) {
                scene.setRoot(new QuenMatKhau_GUI());
            }
        });

        // Label quên mật khẩu
        Label lblXacNhan = new Label("Nhập mã xác nhận");
        lblXacNhan.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 40));

        // Label hướng dẫn nhập
        // Ẩn một phần email bằng dấu *
        String emailFormat = nhanVien.getEmail().replaceAll("(^.{3}).*(@.*$)", "$1******$2");
        Label lblHuongDan = new Label(
                "Chúng tôi đã gửi mã xác nhận về " + emailFormat +
                        " . Vui lòng nhập mã và chọn xác nhận để đặt lại mật khẩu (mã xác nhận có thời gian là 5 phút).");
        lblHuongDan.setFont(Font.font("System", javafx.scene.text.FontWeight.NORMAL, 16));
        lblHuongDan.setWrapText(true);
        lblHuongDan.setMaxWidth(500);

        // ô nhập CCCD
        HBox boxNhapMa = new HBox(20); // khoảng cách giữa các ô
        TextField ma1 = new TextField();
        TextField ma2 = new TextField();
        TextField ma3 = new TextField();
        TextField ma4 = new TextField();

        // Đặt kích thước và style cho các ô nhập mã
        TextField[] fields = { ma1, ma2, ma3, ma4 };
        for (int i = 0; i < fields.length; i++) {
            TextField field = fields[i];
            field.setPrefWidth(60);
            field.setPrefHeight(60);
            field.setAlignment(Pos.CENTER);
            field.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 24));
            field.setStyle(
                    "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #515DEF; -fx-border-width: 2;");

            final int idx = i;
            field.textProperty().addListener((obs, oldText, newText) -> {
                // Chỉ cho nhập 1 ký tự số
                if (!newText.matches("\\d")) {
                    field.setText(newText.replaceAll("[^\\d]", ""));
                }
                if (field.getText().length() > 1) {
                    field.setText(field.getText().substring(0, 1));
                }
                // Nếu nhập xong thì chuyển sang ô tiếp theo
                if (field.getText().length() == 1 && idx < fields.length - 1) {
                    fields[idx + 1].requestFocus();
                }
            });

            // Nếu nhấn Backspace khi ô đang trống thì chuyển về ô trước
            field.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case BACK_SPACE:
                        if (field.getText().isEmpty() && idx > 0) {
                            fields[idx - 1].requestFocus();
                        }
                        break;
                    default:
                        break;
                }
            });
        }

        boxNhapMa.setAlignment(Pos.CENTER_LEFT);
        boxNhapMa.getChildren().addAll(ma1, ma2, ma3, ma4);

        // button tiếp tục
        btnXacNhan = new Button("Xác nhận");
        btnXacNhan.setStyle("-fx-background-color: #515DEF; -fx-background-radius:4;");
        btnXacNhan.setPrefSize(600, 40);
        btnXacNhan.setOnAction(e -> {
            // Lấy mã OTP từ các ô nhập
            String otpCode = ma1.getText() + ma2.getText() + ma3.getText() + ma4.getText();
            long currentTime = System.currentTimeMillis();
            boolean result = OTPService.verifyOtp(nhanVien.getEmail(), otpCode, currentTime);
            if (!result) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi xác thực");
                alert.setHeaderText(null);
                alert.setContentText("Mã xác nhận không đúng hoặc đã hết hạn. Vui lòng thử lại.");
                alert.showAndWait();
                return;
            }
            Scene scene = btnXacNhan.getScene();
            if (scene != null) {
                scene.setRoot(new DoiMatKhau_GUI(nhanVien));
            }
        });

        quenMatKhauBox.getChildren().addAll(linkQuayLai, lblXacNhan, lblHuongDan, boxNhapMa, btnXacNhan);
        return quenMatKhauBox;
    }

    private BorderPane taoImageView() {
        Image image = new Image(getClass().getResourceAsStream("/img/forgot_password_2.png"));
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
}
