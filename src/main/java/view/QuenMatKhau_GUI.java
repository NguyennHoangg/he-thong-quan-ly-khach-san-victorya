package view;

import controller.User_Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.NhanVien;
import notification.model.Email;
import notification.service.EmailService;

public class QuenMatKhau_GUI extends BorderPane {
    private Email email = new Email();
    private EmailService emailService = new EmailService(email);
    private User_Controller user = new User_Controller();

    private Hyperlink linkQuayLai;
    private Button btnTiepTuc;
    private TextField txtCCCD;

    public QuenMatKhau_GUI(){
        init();
    }

    private void init() {
        this.setStyle("-fx-background-color:white");
        this.setLeft(taoPaneTrai());
        this.setRight(taoImageView());
    }

    private VBox taoPaneTrai(){
    VBox quenMatKhauBox = new VBox(20);
    quenMatKhauBox.setStyle("-fx-background-color:white;");
    quenMatKhauBox.setPrefWidth(600);
    quenMatKhauBox.setPrefHeight(600);
    quenMatKhauBox.setAlignment(Pos.TOP_LEFT);
    quenMatKhauBox.setPadding(new Insets(250, 40, 200, 200));

    //button quay lai
    linkQuayLai = new Hyperlink("< Quay lại");
    linkQuayLai.setBorder(Border.EMPTY);
    linkQuayLai.setOnAction(e -> {
        // Quay lại trang đăng nhập
        Scene scene = linkQuayLai.getScene();
        if (scene != null) {
            scene.setRoot(TrangDangNhap.createLoginPane(scene.getWidth(), scene.getHeight()));
        }
    });

    //Label quên mật khẩu
    Label lblQuenMatKhau = new Label("Quên mật khẩu?");
    lblQuenMatKhau.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 40));

    //Label hướng dẫn nhập
    Label lblHuongDan = new Label("Vui lòng nhập CCCD của bạn.");
    lblHuongDan.setFont(Font.font("System", javafx.scene.text.FontWeight.NORMAL, 16));

    //ô nhập CCCD
    txtCCCD = new TextField();
    txtCCCD.setPromptText("Nhập CCCD của bạn");
    txtCCCD.setStyle("-fx-background-radius: 4; -fx-border-radius:4;");

    // ProgressIndicator loading cho button
    ProgressIndicator loading = new ProgressIndicator(-1);
    loading.setPrefSize(32, 32);
    loading.setStyle("-fx-progress-color: #0A1E7A;"); // xanh đậm hơn
    loading.setVisible(false);

    btnTiepTuc = new Button("Tiếp tục");
    btnTiepTuc.setStyle("-fx-background-color: #515DEF; -fx-background-radius:4;");
    btnTiepTuc.setPrefSize(600, 40);
    btnTiepTuc.setGraphic(null);
    btnTiepTuc.setOnAction(e -> {
        btnTiepTuc.setDisable(true);
        btnTiepTuc.setText("");
        loading.setProgress(-1); // luôn indeterminate
        loading.setVisible(true);
        btnTiepTuc.setGraphic(loading);
        String cccd = txtCCCD.getText().trim();
        new Thread(() -> {
            try {
                NhanVien nv = user.getEmailTheoCCCD(cccd);
                javafx.application.Platform.runLater(() -> {
                    if(nv == null){
                        loading.setVisible(false);
                        btnTiepTuc.setDisable(false);
                        btnTiepTuc.setText("Tiếp tục");
                        btnTiepTuc.setGraphic(null);
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText(null);
                        alert.setContentText("Vui lòng kiểm tra lại CCCD bạn đã nhập.");
                        alert.showAndWait();
                        return;
                    }
                    String email = nv.getEmail();
                    if(email == null || email.isBlank()){
                        loading.setVisible(false);
                        btnTiepTuc.setDisable(false);
                        btnTiepTuc.setText("Tiếp tục");
                        btnTiepTuc.setGraphic(null);
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText(null);
                        alert.setContentText("Không tìm thấy email cho nhân viên này. Vui lòng liên hệ quản trị viên.");
                        alert.showAndWait();
                        return;
                    }
                    // Gửi OTP trong thread phụ
                    new Thread(() -> {
                        boolean result = emailService.sendOtpEmail(email);
                        javafx.application.Platform.runLater(() -> {
                            loading.setVisible(false);
                            btnTiepTuc.setDisable(false);
                            btnTiepTuc.setText("Tiếp tục");
                            btnTiepTuc.setGraphic(null);
                            if(!result){
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Lỗi");
                                alert.setHeaderText(null);
                                alert.setContentText("Có lỗi xảy ra. Vui lòng quay lại sau ít phút.");
                                alert.showAndWait();
                                return;
                            }
                            Scene scene = btnTiepTuc.getScene();
                            if (scene != null) {
                                scene.setRoot(new NhapMaXacNhan_GUI(nv));
                            }
                        });
                    }).start();
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    loading.setVisible(false);
                    btnTiepTuc.setDisable(false);
                    btnTiepTuc.setText("Tiếp tục");
                    btnTiepTuc.setGraphic(null);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText("Có lỗi xảy ra: " + ex.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
    });

    quenMatKhauBox.getChildren().addAll(linkQuayLai, lblQuenMatKhau, lblHuongDan, txtCCCD, btnTiepTuc);
    return quenMatKhauBox;
    }

    private BorderPane taoImageView() {
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

    
}
