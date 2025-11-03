package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import controller.TaiKhoan_Controller;
import controller.User_Controller;
import model.NhanVien;
import model.TaiKhoan;
import java.time.format.DateTimeFormatter;
import javafx.util.StringConverter;

/**
 * Giao diện trang tài khoản
 * Hiển thị và cho phép cập nhật thông tin cá nhân và tài khoản
 */
public class TaiKhoan_GUI extends BorderPane {

    private TextField txtCCCD, txtTen, txtDiaChi, txtSoDienThoai, txtEmail;
    private DatePicker dpNgaySinh;
    private ComboBox<String> cmbGioiTinh;
    private final TaiKhoan_Controller controller = new TaiKhoan_Controller();
    private final User_Controller userController = new User_Controller();
    private NhanVien nhanVienHienTai;
    private TaiKhoan taiKhoanHienTai;
    private String tenDangNhapHienTai;

    public TaiKhoan_GUI() {
        this(null);
    }

    public TaiKhoan_GUI(String tenDangNhap) {
        this.tenDangNhapHienTai = tenDangNhap;
        initialize();
    }

    private void initialize() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");
        
        // Load CSS
        getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/css/Control.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/css/Label.css").toExternalForm());

        VBox container = new VBox(20);
        container.setMaxWidth(800);
        container.setAlignment(Pos.TOP_CENTER);

        container.getChildren().add(createMainCard());
        setCenter(container);
        
        taiDuLieuMacDinh();
    }

    private VBox createMainCard() {
        VBox card = new VBox(18);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        Label title1 = new Label("📋 Thông tin cá nhân");
        title1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox row0 = new HBox(12);
        row0.setFillHeight(false);
        txtTen = createTextField("Họ và tên");
        txtTen.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validateTen();
            }
        });
        
        cmbGioiTinh = new ComboBox<>();
        cmbGioiTinh.getItems().addAll("Nam", "Nữ");
        cmbGioiTinh.setPrefHeight(38);
        cmbGioiTinh.getStyleClass().add("cmb");
        
        VBox tenGroup = createFieldGroup("Họ và tên", txtTen);
        VBox gioiTinhGroup = createFieldGroup("Giới tính", cmbGioiTinh);
        HBox.setHgrow(tenGroup, Priority.ALWAYS);
        HBox.setHgrow(gioiTinhGroup, Priority.ALWAYS);
        row0.getChildren().addAll(tenGroup, gioiTinhGroup);

        HBox row1 = new HBox(12);
        row1.setFillHeight(false);
        txtCCCD = createTextField("CCCD");
        txtCCCD.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validateCCCD();
            }
        });
        
        dpNgaySinh = new DatePicker();
        dpNgaySinh.setPrefHeight(38);
        dpNgaySinh.getStyleClass().add("date-picker");
        dpNgaySinh.setOnAction(e -> validateNgaySinh());
        
        // Set format dd-MM-yyyy cho DatePicker
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dpNgaySinh.setConverter(new StringConverter<java.time.LocalDate>() {
            @Override
            public String toString(java.time.LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                }
                return "";
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return java.time.LocalDate.parse(string, dateFormatter);
                    } catch (Exception e) {
                        return null;
                    }
                }
                return null;
            }
        });
        
        VBox cccdGroup = createFieldGroup("CCCD", txtCCCD);
        VBox ngaySinhGroup = createFieldGroup("Ngày sinh", dpNgaySinh);
        HBox.setHgrow(cccdGroup, Priority.ALWAYS);
        HBox.setHgrow(ngaySinhGroup, Priority.ALWAYS);
        // Đảm bảo row1 có cùng width với row0
        cccdGroup.prefWidthProperty().bind(tenGroup.widthProperty());
        ngaySinhGroup.prefWidthProperty().bind(gioiTinhGroup.widthProperty());
        row1.getChildren().addAll(cccdGroup, ngaySinhGroup);

        HBox row2 = new HBox(12);
        txtDiaChi = createTextField("Địa chỉ");
        txtDiaChi.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validateDiaChi();
            }
        });
        VBox diaChiGroup = createFieldGroup("Địa chỉ", txtDiaChi);
        HBox.setHgrow(diaChiGroup, Priority.ALWAYS);
        row2.getChildren().add(diaChiGroup);

        javafx.scene.shape.Line line = new javafx.scene.shape.Line();
        line.setStroke(Color.web("#e5e7eb"));
        line.setStrokeWidth(1);
        line.setStartX(0);
        line.setEndX(750);

        Label title2 = new Label("🔐 Thông tin tài khoản");
        title2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox row3 = createRowWithChangeButton("Số điện thoại", txtSoDienThoai = createTextField("Số điện thoại"), 
            "Đổi", e -> hienThiModalDoiSoDienThoai());
        HBox row4 = createRowWithChangeButton("Email", txtEmail = createTextField("Email"), 
            "Đổi", e -> hienThiModalDoiEmail());
        HBox row5 = createPasswordRow();

        // Button lưu thay đổi và làm mới
        Button btnLuuThayDoi = createButton("💾 Lưu thay đổi", "btn-luu");
        btnLuuThayDoi.setPrefWidth(180);
        btnLuuThayDoi.setPrefHeight(40);
        btnLuuThayDoi.setOnAction(e -> luuThayDoi());

        Button btnLamMoi = createButton("🔄 Làm mới", "btn-small");
        btnLamMoi.setPrefWidth(110);
        btnLamMoi.setPrefHeight(40);
        btnLamMoi.setOnAction(e -> taiDuLieuMacDinh());

        HBox saveBox = new HBox(10);
        saveBox.setAlignment(Pos.CENTER);
        saveBox.setPadding(new Insets(10, 0, 0, 0));
        saveBox.getChildren().addAll(btnLuuThayDoi, btnLamMoi);

        card.getChildren().addAll(title1, row0, row1, row2, line, title2, row3, row4, row5, saveBox);
        return card;
    }

    private HBox createRowWithChangeButton(String label, TextField field, String buttonText, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        field.setDisable(true);
        Button btn = createButton(buttonText, "btn");
        btn.setOnAction(handler);
        VBox fieldGroup = createFieldGroup(label, field);
        HBox.setHgrow(fieldGroup, Priority.ALWAYS);
        HBox row = new HBox(10);
        row.getChildren().addAll(fieldGroup, btn);
        row.setAlignment(Pos.BOTTOM_LEFT);
        return row;
    }

    private HBox createPasswordRow() {
        PasswordField txtMatKhau = new PasswordField();
        txtMatKhau.setText("••••••••");
        txtMatKhau.setDisable(true);
        txtMatKhau.setPrefHeight(38);
        txtMatKhau.getStyleClass().add("text-field");
        
        Button btnDoiMatKhau = createButton("Đổi", "btn");
        btnDoiMatKhau.setOnAction(e -> hienThiModalDoiMatKhau());
        
        VBox passGroup = createFieldGroup("Mật khẩu", null);
        passGroup.getChildren().add(txtMatKhau);
        HBox.setHgrow(passGroup, Priority.ALWAYS);
        
        HBox row = new HBox(10);
        row.getChildren().addAll(passGroup, btnDoiMatKhau);
        row.setAlignment(Pos.BOTTOM_LEFT);
        return row;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        field.getStyleClass().add("text-field");
        return field;
    }

    private Button createButton(String text, String styleClass) {
        Button btn = new Button(text);
        btn.setPrefHeight(38);
        btn.setPrefWidth(80);
        btn.getStyleClass().add(styleClass);
        return btn;
    }

    private Label createFieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        return lbl;
    }

    private VBox createFieldGroup(String label, javafx.scene.Node field) {
        VBox group = new VBox(6);
        HBox.setHgrow(group, Priority.ALWAYS);
        group.getChildren().add(createFieldLabel(label));
        if (field != null) {
            group.getChildren().add(field);
        }
        return group;
    }
    
    private void taiDuLieuMacDinh() {
        try {
            if (tenDangNhapHienTai == null || tenDangNhapHienTai.isEmpty()) {
                tenDangNhapHienTai = "admin"; // Fallback, nên lấy từ session
            }
            
        taiKhoanHienTai = controller.layThongTinTaiKhoan(tenDangNhapHienTai);
        nhanVienHienTai = controller.layThongTinNhanVien(tenDangNhapHienTai);
        
            if (taiKhoanHienTai == null || nhanVienHienTai == null) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Không tìm thấy thông tin tài khoản. Vui lòng đăng nhập lại.");
                return;
            }
            
        hienThiDuLieuLenForm();
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Dữ liệu không hợp lệ: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi tải dữ liệu: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        }
    }
    
    private void hienThiDuLieuLenForm() {
        try {
            if (nhanVienHienTai == null) {
                return;
            }
            
            txtTen.setText(nhanVienHienTai.getTenNhanVien() != null ? nhanVienHienTai.getTenNhanVien() : "");
            txtCCCD.setText(nhanVienHienTai.getCCCD() != null ? nhanVienHienTai.getCCCD() : "");
            dpNgaySinh.setValue(nhanVienHienTai.getNgaySinh());
            txtDiaChi.setText(nhanVienHienTai.getDiaChi() != null ? nhanVienHienTai.getDiaChi() : "");
            txtSoDienThoai.setText(nhanVienHienTai.getSoDienThoai() != null ? nhanVienHienTai.getSoDienThoai() : "");
            txtEmail.setText(nhanVienHienTai.getEmail() != null ? nhanVienHienTai.getEmail() : "");
            cmbGioiTinh.setValue(nhanVienHienTai.isGioiTinh() ? "Nam" : "Nữ");
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Dữ liệu hiển thị không hợp lệ: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi hiển thị dữ liệu: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        }
    }
    
    /**
     * Xử lý khi bấm nút "Lưu thay đổi"
     */
    private void luuThayDoi() {
        try {
            if (nhanVienHienTai == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin nhân viên");
                return;
            }

            // Validate dữ liệu
            if (!validateDuLieu()) {
                return;
            }

            // Cập nhật thông tin
            String tenMoi = txtTen.getText().trim();
            String cccdMoi = txtCCCD.getText().trim();
            java.time.LocalDate ngaySinhMoi = dpNgaySinh.getValue();
            boolean gioiTinhMoi = cmbGioiTinh.getValue() != null && cmbGioiTinh.getValue().equals("Nam");
            String diaChiMoi = txtDiaChi.getText().trim();

            nhanVienHienTai.setTenNhanVien(tenMoi);
            nhanVienHienTai.setCCCD(cccdMoi);
            nhanVienHienTai.setNgaySinh(ngaySinhMoi);
            nhanVienHienTai.setGioiTinh(gioiTinhMoi);
            nhanVienHienTai.setDiaChi(diaChiMoi.isEmpty() ? null : diaChiMoi);

            // Gọi controller cập nhật
            boolean thanhCong = controller.capNhatThongTinCaNhan(nhanVienHienTai);
            
            if (thanhCong) {
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thông tin thành công!");
                // Reload dữ liệu từ database để cập nhật form
                taiDuLieuMacDinh();
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Cập nhật thông tin thất bại! Vui lòng thử lại.");
            }
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Dữ liệu không hợp lệ: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi lưu thay đổi: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        }
    }

    /**
     * Validate họ tên khi focus out - chỉ chứa chữ cái và khoảng trắng
     */
    private void validateTen() {
        try {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) {
                resetFieldStyle(txtTen);
                return; // Cho phép rỗng, sẽ validate khi lưu
            }

            if (!ten.matches("^[\\p{L}\\s]+$")) {
                setErrorStyle(txtTen);
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Họ và tên chỉ được chứa chữ cái và khoảng trắng");
                txtTen.requestFocus();
            } else {
                resetFieldStyle(txtTen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Validate CCCD khi focus out - chỉ chứa số và đúng 12 chữ số
     */
    private void validateCCCD() {
        try {
            String cccd = txtCCCD.getText().trim();
            if (cccd.isEmpty()) {
                resetFieldStyle(txtCCCD);
                return; // Cho phép rỗng, sẽ validate khi lưu
            }

            if (!cccd.matches("^[0-9]{12}$")) {
                setErrorStyle(txtCCCD);
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "CCCD phải gồm đúng 12 chữ số");
                txtCCCD.requestFocus();
            } else {
                resetFieldStyle(txtCCCD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validate ngày sinh - tuổi phải đủ 18
     */
    private void validateNgaySinh() {
        try {
            if (dpNgaySinh.getValue() == null) {
                resetDatePickerStyle();
                return;
            }

            java.time.LocalDate ngaySinh = dpNgaySinh.getValue();
            java.time.LocalDate homNay = java.time.LocalDate.now();
            long tuoi = java.time.temporal.ChronoUnit.YEARS.between(ngaySinh, homNay);

            if (tuoi < 18) {
                dpNgaySinh.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2;");
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Tuổi phải đủ 18 tuổi trở lên");
                dpNgaySinh.requestFocus();
            } else {
                resetDatePickerStyle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validate địa chỉ khi focus out - độ dài tối đa 200 ký tự
     */
    private void validateDiaChi() {
        try {
            String diaChi = txtDiaChi.getText().trim();
            if (diaChi.isEmpty()) {
                resetFieldStyle(txtDiaChi);
                return;
            }

            if (diaChi.length() > 200) {
                setErrorStyle(txtDiaChi);
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Địa chỉ không được vượt quá 200 ký tự");
                txtDiaChi.requestFocus();
            } else {
                resetFieldStyle(txtDiaChi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set style lỗi cho TextField (viền đỏ)
     */
    private void setErrorStyle(TextField field) {
        field.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2;");
    }

    /**
     * Reset style về mặc định cho TextField
     */
    private void resetFieldStyle(TextField field) {
        field.setStyle(null); // Reset về CSS class mặc định
    }

    /**
     * Reset style về mặc định cho DatePicker
     */
    private void resetDatePickerStyle() {
        dpNgaySinh.setStyle(null); // Reset về CSS class mặc định
    }

    /**
     * Validate dữ liệu trước khi lưu
     */
    private boolean validateDuLieu() {
        try {
            String tenMoi = txtTen.getText().trim();
            if (tenMoi.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập họ và tên");
                txtTen.requestFocus();
                return false;
            }

            if (!tenMoi.matches("^[\\p{L}\\s]+$")) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Họ và tên chỉ được chứa chữ cái và khoảng trắng");
                txtTen.requestFocus();
                return false;
            }

            if (tenMoi.length() > 100) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Họ và tên không được vượt quá 100 ký tự");
                txtTen.requestFocus();
                return false;
            }

            String cccd = txtCCCD.getText().trim();
            if (cccd.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập CCCD");
                txtCCCD.requestFocus();
                return false;
            }

            if (!cccd.matches("^[0-9]{12}$")) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "CCCD phải gồm đúng 12 chữ số");
                txtCCCD.requestFocus();
                return false;
            }

            if (dpNgaySinh.getValue() == null) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn ngày sinh");
                dpNgaySinh.requestFocus();
                return false;
            }

            // Kiểm tra tuổi đủ 18
            java.time.LocalDate ngaySinh = dpNgaySinh.getValue();
            java.time.LocalDate homNay = java.time.LocalDate.now();
            long tuoi = java.time.temporal.ChronoUnit.YEARS.between(ngaySinh, homNay);
            if (tuoi < 18) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Tuổi phải đủ 18 tuổi trở lên");
                dpNgaySinh.requestFocus();
                return false;
            }

            if (cmbGioiTinh.getValue() == null || cmbGioiTinh.getValue().isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn giới tính");
                cmbGioiTinh.requestFocus();
                return false;
            }

            String diaChiMoi = txtDiaChi.getText().trim();
            if (diaChiMoi.length() > 200) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Địa chỉ không được vượt quá 200 ký tự");
                txtDiaChi.requestFocus();
                return false;
            }

            return true;
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi validate dữ liệu: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
            return false;
        }
    }

    private void hienThiModalDoiSoDienThoai() {
        PasswordField txtMatKhauCu = new PasswordField();
        txtMatKhauCu.setPrefHeight(38);
        txtMatKhauCu.getStyleClass().add("password-field");
        
        TextField txtSoDienThoaiMoi = new TextField();
        txtSoDienThoaiMoi.setPrefHeight(38);
        txtSoDienThoaiMoi.getStyleClass().add("text-field");
        
        Stage[] modalRef = new Stage[1];
        modalRef[0] = createModal("Đổi số điện thoại", 
            txtMatKhauCu, txtSoDienThoaiMoi,
            () -> xuLyDoiSoDienThoai(modalRef[0], txtMatKhauCu, txtSoDienThoaiMoi));
        modalRef[0].showAndWait();
    }

    private void xuLyDoiSoDienThoai(Stage modal, PasswordField txtMatKhauCu, TextField txtSoDienThoaiMoi) {
        if (!kiemTraThongTinCoBan(modal)) return;

        String matKhauCu = txtMatKhauCu.getText().trim();
        String soDienThoaiMoi = txtSoDienThoaiMoi.getText().trim();

        if (matKhauCu.isEmpty() || soDienThoaiMoi.isEmpty()) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!soDienThoaiMoi.matches("^[0-9]{10,11}$")) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Số điện thoại phải gồm 10-11 chữ số");
            return;
        }

        if (!kiemTraMatKhau(txtMatKhauCu)) return;

        boolean thanhCong = controller.capNhatSoDienThoai(nhanVienHienTai.getMaNhanVien(), soDienThoaiMoi);
        if (thanhCong) {
            nhanVienHienTai.setSoDienThoai(soDienThoaiMoi);
            txtSoDienThoai.setText(soDienThoaiMoi);
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đổi số điện thoại thành công!");
            modal.close();
        } else {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Đổi số điện thoại thất bại! Vui lòng thử lại.");
        }
    }

    private void hienThiModalDoiEmail() {
        PasswordField txtMatKhauCu = new PasswordField();
        txtMatKhauCu.setPrefHeight(38);
        txtMatKhauCu.getStyleClass().add("password-field");
        
        TextField txtEmailMoi = new TextField();
        txtEmailMoi.setPrefHeight(38);
        txtEmailMoi.getStyleClass().add("text-field");
        
        Stage[] modalRef = new Stage[1];
        modalRef[0] = createModal("Đổi email", 
            txtMatKhauCu, txtEmailMoi,
            () -> xuLyDoiEmail(modalRef[0], txtMatKhauCu, txtEmailMoi));
        modalRef[0].showAndWait();
    }

    private void xuLyDoiEmail(Stage modal, PasswordField txtMatKhauCu, TextField txtEmailMoi) {
        if (!kiemTraThongTinCoBan(modal)) return;

        String matKhauCu = txtMatKhauCu.getText().trim();
        String emailMoi = txtEmailMoi.getText().trim();

        if (matKhauCu.isEmpty() || emailMoi.isEmpty()) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!emailMoi.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Email không hợp lệ");
            return;
        }

        if (!kiemTraMatKhau(txtMatKhauCu)) return;

        boolean thanhCong = controller.capNhatEmail(nhanVienHienTai.getMaNhanVien(), emailMoi);
        if (thanhCong) {
            nhanVienHienTai.setEmail(emailMoi);
            txtEmail.setText(emailMoi);
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đổi email thành công!");
            modal.close();
        } else {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Đổi email thất bại! Vui lòng thử lại.");
        }
    }

    private void hienThiModalDoiMatKhau() {
        PasswordField txtMatKhauCu = new PasswordField();
        txtMatKhauCu.setPrefHeight(38);
        txtMatKhauCu.getStyleClass().add("password-field");
        
        PasswordField txtMatKhauMoi = new PasswordField();
        txtMatKhauMoi.setPrefHeight(38);
        txtMatKhauMoi.getStyleClass().add("password-field");
        
        PasswordField txtXacNhanMatKhau = new PasswordField();
        txtXacNhanMatKhau.setPrefHeight(38);
        txtXacNhanMatKhau.getStyleClass().add("password-field");
        
        Stage[] modalRef = new Stage[1];
        modalRef[0] = createModal3Fields("Đổi mật khẩu", 
            txtMatKhauCu, txtMatKhauMoi, txtXacNhanMatKhau,
            () -> xuLyDoiMatKhau(modalRef[0], txtMatKhauCu, txtMatKhauMoi, txtXacNhanMatKhau));
        modalRef[0].showAndWait();
    }

    private Label createModalLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        return lbl;
    }

    private Stage createModal(String title, javafx.scene.Node field1, javafx.scene.Node field2, Runnable onConfirm) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.setTitle(title);

        VBox modalContent = new VBox(15);
        modalContent.setPadding(new Insets(25));
        modalContent.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        modalContent.setPrefWidth(450);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label lblField1 = createModalLabel("Mật khẩu hiện tại:");
        Label lblField2 = createModalLabel(field2 instanceof PasswordField ? "Mật khẩu mới:" : 
            (field2 instanceof TextField && title.contains("số điện thoại") ? "Số điện thoại mới:" : "Email mới:"));

        HBox buttonBox = createModalButtonBox(modal, onConfirm);

        modalContent.getChildren().addAll(titleLabel, lblField1, field1, lblField2, field2, buttonBox);
        modal.setScene(new javafx.scene.Scene(modalContent));
        return modal;
    }

    private Stage createModal3Fields(String title, PasswordField field1, PasswordField field2, PasswordField field3, Runnable onConfirm) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.setTitle(title);

        VBox modalContent = new VBox(15);
        modalContent.setPadding(new Insets(25));
        modalContent.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        modalContent.setPrefWidth(450);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox buttonBox = createModalButtonBox(modal, onConfirm);

        modalContent.getChildren().addAll(titleLabel, 
            createModalLabel("Mật khẩu hiện tại:"), field1,
            createModalLabel("Mật khẩu mới:"), field2,
            createModalLabel("Xác nhận mật khẩu mới:"), field3,
            buttonBox);
        modal.setScene(new javafx.scene.Scene(modalContent));
        return modal;
    }

    private HBox createModalButtonBox(Stage modal, Runnable onConfirm) {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnHuy = createButton("Hủy", "btn-huy");
        btnHuy.setPrefWidth(100);
        btnHuy.setOnAction(e -> modal.close());

        Button btnXacNhan = createButton("Xác nhận", "btn");
        btnXacNhan.setPrefWidth(100);
        btnXacNhan.setOnAction(e -> onConfirm.run());

        buttonBox.getChildren().addAll(btnHuy, btnXacNhan);
        return buttonBox;
    }

    private void xuLyDoiMatKhau(Stage modal, PasswordField txtMatKhauCu, PasswordField txtMatKhauMoi, PasswordField txtXacNhanMatKhau) {
        if (taiKhoanHienTai == null) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin tài khoản");
            modal.close();
            return;
        }

        String matKhauCu = txtMatKhauCu.getText().trim();
        String matKhauMoi = txtMatKhauMoi.getText().trim();
        String xacNhanMatKhau = txtXacNhanMatKhau.getText().trim();

        if (matKhauCu.isEmpty() || matKhauMoi.isEmpty() || xacNhanMatKhau.isEmpty()) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Mật khẩu mới và xác nhận mật khẩu không khớp");
            return;
        }

        if (matKhauMoi.length() < 6 || matKhauMoi.length() > 50) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                matKhauMoi.length() < 6 ? "Mật khẩu mới phải có ít nhất 6 ký tự" : "Mật khẩu mới không được vượt quá 50 ký tự");
            return;
        }

        if (taiKhoanHienTai.getTenDangNhap() == null || taiKhoanHienTai.getTenDangNhap().isEmpty()) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Tên đăng nhập không hợp lệ");
            return;
        }

        if (!kiemTraMatKhau(txtMatKhauCu)) return;

        String matKhauHash = userController.HashPassWord(matKhauMoi);
        if (matKhauHash == null || matKhauHash.isEmpty()) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể hash mật khẩu. Vui lòng thử lại.");
            return;
        }

        boolean thanhCong = controller.capNhatMatKhau(taiKhoanHienTai.getTenDangNhap(), matKhauHash);
        if (thanhCong) {
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công!");
            modal.close();
        } else {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Đổi mật khẩu thất bại! Vui lòng thử lại.");
        }
    }

    private boolean kiemTraThongTinCoBan(Stage modal) {
        if (nhanVienHienTai == null || taiKhoanHienTai == null) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin nhân viên");
            modal.close();
            return false;
        }
        if (nhanVienHienTai.getMaNhanVien() == null || nhanVienHienTai.getMaNhanVien().isEmpty()) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Mã nhân viên không hợp lệ");
            return false;
        }
        return true;
    }

    private boolean kiemTraMatKhau(PasswordField txtMatKhauCu) {
        String matKhauCu = txtMatKhauCu.getText().trim();
        boolean matKhauDung = controller.kiemTraMatKhauCu(taiKhoanHienTai.getTenDangNhap(), matKhauCu);
        if (!matKhauDung) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu hiện tại không đúng");
            txtMatKhauCu.clear();
            txtMatKhauCu.requestFocus();
            return false;
        }
        return true;
    }

    private void hienThiThongBao(Alert.AlertType loai, String tieuDe, String noiDung) {
        try {
            Alert alert = new Alert(loai);
            alert.setTitle(tieuDe);
            alert.setHeaderText(null);
            alert.setContentText(noiDung);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Không thể hiển thị thông báo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
