package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controller.TaiKhoan_Controller;
import model.NhanVien;
import model.TaiKhoan;
import utils.Util;
import view.TrangDangNhap;


public class TaiKhoan_GUI extends BorderPane {

    private TextField txtHoTen, txtCCCD, txtTaiKhoan, txtEmail, txtDiaChi, txtSoDienThoai, txtNgaySinh, txtGioiTinh;
    private DatePicker dpNgaySinh;
    private PasswordField txtMatKhau;
    private RadioButton rbNam, rbNu;
    private final TaiKhoan_Controller controller = new TaiKhoan_Controller();
    private NhanVien nhanVienHienTai;
    private TaiKhoan taiKhoanHienTai;
    private Label lblHeaderName;
    private Label lblHeaderEmail;

    public TaiKhoan_GUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        VBox container = new VBox(20);
        container.setMaxWidth(950);
        container.setAlignment(Pos.TOP_CENTER);

        container.getChildren().addAll(createHeader(), createMainCard());
        setCenter(container);
        
        taiDuLieuMacDinh();
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        VBox info = new VBox(3);
        lblHeaderName = new Label("Đang tải...");
        lblHeaderName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        lblHeaderEmail = new Label("Đang tải...");
        lblHeaderEmail.setStyle("-fx-font-size: 14px; -fx-text-fill: #2563eb; -fx-font-weight: 700;");
        info.getChildren().addAll(lblHeaderName, lblHeaderEmail);

        header.getChildren().add(info);
        return header;
    }

    private VBox createMainCard() {
        VBox card = new VBox(18);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        Label title1 = new Label("📋 Thông tin cá nhân");
        title1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // PERSONAL INFO
        // Row 1: CCCD + Ngày sinh
        HBox row1 = new HBox(12);
        txtCCCD = createField("012345678912");
        txtCCCD.setEditable(false);
        txtNgaySinh = createField("01/01/2000");
        txtNgaySinh.setEditable(false);
        row1.getChildren().addAll(
            createFieldGroup("CCCD", txtCCCD),
            createFieldGroup("Ngày sinh", txtNgaySinh)
        );
        // Row 2: Giới tính + Địa chỉ
        HBox row2 = new HBox(12);
        txtGioiTinh = createField("Nam/Nữ");
        txtGioiTinh.setEditable(false);
        txtDiaChi = createField("123 Đường Abc, Quận 1, TP.HCM");
        txtDiaChi.setEditable(true);
        row2.getChildren().addAll(
            createFieldGroup("Giới tính", txtGioiTinh),
            createFieldGroup("Địa chỉ", txtDiaChi)
        );

        javafx.scene.shape.Line line = new javafx.scene.shape.Line();
        line.setStroke(Color.web("#e5e7eb"));
        line.setStrokeWidth(1);
        line.setStartX(0);
        line.setEndX(900);

        Label title2 = new Label("🔐 Thông tin tài khoản");
        title2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // TÀI KHOẢN
        HBox row4 = new HBox(12);
        // Tên tài khoản: bỏ khỏi UI (vẫn khởi tạo để tránh null khi set text)
        txtTaiKhoan = createField("user123");
        txtTaiKhoan.setEditable(false);

        VBox emailGroup = new VBox(6);
        HBox.setHgrow(emailGroup, Priority.ALWAYS);
        Label lblEmail = new Label("Email");
        lblEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        HBox emailBox = new HBox(5);
        txtEmail = createField("user@example.com");
        txtEmail.setEditable(false);
        HBox.setHgrow(txtEmail, Priority.ALWAYS);
        Button btnDoiEmail = new Button("Đổi");
        btnDoiEmail.setPrefHeight(38);
        btnDoiEmail.setPrefWidth(60);
        btnDoiEmail.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-font-size: 11px;");
        btnDoiEmail.setOnAction(e -> hienThiModalDoiEmail());
        emailBox.getChildren().addAll(txtEmail, btnDoiEmail);
        emailGroup.getChildren().addAll(lblEmail, emailBox);
        // Chỉ thêm emailGroup vào row4
        row4.getChildren().addAll(emailGroup);

        HBox row5 = new HBox(12);
        // SỐ ĐIỆN THOẠI (ở phần tài khoản)
        VBox sdtGroup = new VBox(6);
        HBox.setHgrow(sdtGroup, Priority.ALWAYS);
        Label lblSDT = new Label("Số điện thoại");
        lblSDT.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        HBox sdtBox = new HBox(5);
        txtSoDienThoai = createField("0901234567");
        txtSoDienThoai.setEditable(false);
        HBox.setHgrow(txtSoDienThoai, Priority.ALWAYS);
        Button btnDoiSDT = new Button("Đổi");
        btnDoiSDT.setPrefHeight(38);
        btnDoiSDT.setPrefWidth(60);
        btnDoiSDT.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-font-size: 11px;");
        btnDoiSDT.setOnAction(e -> hienThiModalDoiSoDienThoai());
        sdtBox.getChildren().addAll(txtSoDienThoai, btnDoiSDT);
        sdtGroup.getChildren().addAll(lblSDT, sdtBox);
        row5.getChildren().addAll(sdtGroup);

        HBox row6 = new HBox(10);
        VBox passGroup = createFieldGroup("Mật khẩu", null);
        txtMatKhau = new PasswordField();
        txtMatKhau.setPromptText("••••••••");
        txtMatKhau.setPrefHeight(38);
        txtMatKhau.setEditable(false);
        passGroup.getChildren().add(txtMatKhau);
        HBox.setHgrow(passGroup, Priority.ALWAYS);
        Button btnChange = new Button("Đổi mật khẩu");
        btnChange.setPrefHeight(38);
        btnChange.setPrefWidth(140);
        btnChange.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;");
        btnChange.setOnAction(e -> hienThiModalDoiMatKhau());
        row6.getChildren().addAll(passGroup, btnChange);
        row6.setAlignment(Pos.BOTTOM_LEFT);

        Button btnSave = new Button("💾 Lưu thay đổi");
        btnSave.setPrefWidth(180);
        btnSave.setPrefHeight(40);
        btnSave.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnSave.setOnAction(e -> luuThayDoi());
        HBox saveBox = new HBox(btnSave);
        saveBox.setAlignment(Pos.CENTER);
        saveBox.setPadding(new Insets(5, 0, 0, 0));

        card.getChildren().addAll(title1, row1, row2, line, title2, row4, row5, row6, saveBox);
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
    
    private void taiDuLieuMacDinh() {
        String tenDangNhapHienTai = TrangDangNhap.getCurrentUsername();
        if (tenDangNhapHienTai == null || tenDangNhapHienTai.isBlank()) {
            // Fallback: giữ nguyên hiển thị mặc định
            lblHeaderName.setText("Không tìm thấy thông tin");
            lblHeaderEmail.setText("");
            return;
        }
        taiKhoanHienTai = controller.layThongTinTaiKhoan(tenDangNhapHienTai);
        nhanVienHienTai = controller.layThongTinNhanVien(tenDangNhapHienTai);
        hienThiDuLieuLenForm();
    }
    
    private void hienThiDuLieuLenForm() {
        if (nhanVienHienTai != null) {
            lblHeaderName.setText(nhanVienHienTai.getTenNhanVien());
            // Hiển thị chức vụ (vai trò) nổi bật ở header
            if (taiKhoanHienTai != null) {
                lblHeaderEmail.setText(taiKhoanHienTai.getVaiTro());
            } else {
                lblHeaderEmail.setText("");
            }
            txtNgaySinh.setText(nhanVienHienTai.getNgaySinh() != null ? String.valueOf(nhanVienHienTai.getNgaySinh()) : "");
            txtGioiTinh.setText(nhanVienHienTai.isGioiTinh() ? "Nam" : "Nữ");
            txtEmail.setText(nhanVienHienTai.getEmail());
            txtCCCD.setText(nhanVienHienTai.getCCCD());
            if (nhanVienHienTai.getSoDienThoai() != null) {
                txtSoDienThoai.setText(nhanVienHienTai.getSoDienThoai());
            }
            txtDiaChi.setText(nhanVienHienTai.getDiaChi());
        } else {
            lblHeaderName.setText("Không tìm thấy thông tin");
            lblHeaderEmail.setText("");
        }
        if (taiKhoanHienTai != null) {
            // vẫn set để đồng bộ, dù không hiển thị trong UI
            txtTaiKhoan.setText(taiKhoanHienTai.getTenDangNhap());
            txtMatKhau.setText("••••••••");
        }
    }
    
    private void luuThayDoi() {
        if (nhanVienHienTai == null) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không có thông tin nhân viên để cập nhật");
            return;
        }
        nhanVienHienTai.setDiaChi(txtDiaChi.getText());
        boolean thanhCong = controller.capNhatThongTinCaNhan(nhanVienHienTai);
        if (thanhCong) {
            lblHeaderName.setText(nhanVienHienTai.getTenNhanVien());
            lblHeaderEmail.setText(nhanVienHienTai.getEmail() != null ? nhanVienHienTai.getEmail() : "");
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thông tin cá nhân thành công!");
        } else {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Cập nhật thông tin thất bại!");
        }
    }
    
    private void hienThiModalDoiMatKhau() {
        if (taiKhoanHienTai == null) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin tài khoản");
            return;
        }
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Đổi mật khẩu");
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white;");
        Label title = new Label("Đổi mật khẩu");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        VBox passCurrentGroup = new VBox(6);
        Label lbl0 = new Label("Mật khẩu hiện tại");
        lbl0.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        PasswordField txtMatKhauHienTai = new PasswordField();
        txtMatKhauHienTai.setPromptText("Nhập mật khẩu hiện tại");
        txtMatKhauHienTai.setPrefHeight(38);
        txtMatKhauHienTai.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 12;");
        passCurrentGroup.getChildren().addAll(lbl0, txtMatKhauHienTai);

        VBox passGroup1 = new VBox(6);
        Label lbl1 = new Label("Mật khẩu mới");
        lbl1.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        PasswordField txtMatKhauMoi = new PasswordField();
        txtMatKhauMoi.setPromptText("Nhập mật khẩu mới");
        txtMatKhauMoi.setPrefHeight(38);
        txtMatKhauMoi.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 12;");
        passGroup1.getChildren().addAll(lbl1, txtMatKhauMoi);

        VBox passGroup2 = new VBox(6);
        Label lbl2 = new Label("Xác nhận mật khẩu mới");
        lbl2.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        PasswordField txtXacNhanMatKhau = new PasswordField();
        txtXacNhanMatKhau.setPromptText("Nhập lại mật khẩu mới");
        txtXacNhanMatKhau.setPrefHeight(38);
        txtXacNhanMatKhau.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 12;");
        passGroup2.getChildren().addAll(lbl2, txtXacNhanMatKhau);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);
        Button btnHuy = new Button("Hủy");
        btnHuy.setPrefWidth(100);
        btnHuy.setPrefHeight(38);
        btnHuy.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #1e293b; -fx-background-radius: 8;");
        btnHuy.setOnAction(e -> modal.close());
        Button btnXacNhan = new Button("Xác nhận");
        btnXacNhan.setPrefWidth(100);
        btnXacNhan.setPrefHeight(38);
        btnXacNhan.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;");
        btnXacNhan.setOnAction(e -> {
            String matKhauCu = txtMatKhauHienTai.getText();
            String matKhauMoi = txtMatKhauMoi.getText();
            String xacNhan = txtXacNhanMatKhau.getText();
            if (matKhauCu.isEmpty() || matKhauMoi.isEmpty() || xacNhan.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin");
                return;
            }
            if (!Util.checkPassword(matKhauCu, taiKhoanHienTai.getMatKhau())) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu hiện tại không đúng!");
                return;
            }
            if (!matKhauMoi.equals(xacNhan)) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu xác nhận không khớp!");
                return;
            }
            String matKhauHash = Util.hashPassword(matKhauMoi);
            boolean thanhCong = controller.capNhatMatKhau(taiKhoanHienTai.getTenDangNhap(), matKhauHash);
            if (thanhCong) {
                taiKhoanHienTai.setMatKhau(matKhauHash);
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công!");
                modal.close();
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Đổi mật khẩu thất bại!");
            }
        });
        btnBox.getChildren().addAll(btnHuy, btnXacNhan);
        root.getChildren().addAll(title, passCurrentGroup, passGroup1, passGroup2, btnBox);
        Scene scene = new Scene(root, 400, 330);
        modal.setScene(scene);
        modal.showAndWait();
    }
    
    private void hienThiModalDoiSoDienThoai() {
        if (nhanVienHienTai == null || taiKhoanHienTai == null) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin nhân viên");
            return;
        }
        
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Đổi số điện thoại");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white;");
        
        Label title = new Label("Đổi số điện thoại");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        VBox passGroup = new VBox(6);
        Label lblPass = new Label("Mật khẩu hiện tại");
        lblPass.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        PasswordField txtMatKhauXacNhan = new PasswordField();
        txtMatKhauXacNhan.setPromptText("Nhập mật khẩu để xác nhận");
        txtMatKhauXacNhan.setPrefHeight(38);
        txtMatKhauXacNhan.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 12;");
        passGroup.getChildren().addAll(lblPass, txtMatKhauXacNhan);
        
        VBox sdtGroup = new VBox(6);
        Label lblSDT = new Label("Số điện thoại mới");
        lblSDT.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        TextField txtSDTMoi = new TextField();
        txtSDTMoi.setPromptText("Nhập số điện thoại mới");
        txtSDTMoi.setPrefHeight(38);
        txtSDTMoi.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 12;");
        sdtGroup.getChildren().addAll(lblSDT, txtSDTMoi);
        
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);
        
        Button btnHuy = new Button("Hủy");
        btnHuy.setPrefWidth(100);
        btnHuy.setPrefHeight(38);
        btnHuy.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #1e293b; -fx-background-radius: 8;");
        btnHuy.setOnAction(e -> modal.close());
        
        Button btnXacNhan = new Button("Xác nhận");
        btnXacNhan.setPrefWidth(100);
        btnXacNhan.setPrefHeight(38);
        btnXacNhan.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;");
        btnXacNhan.setOnAction(e -> {
            String matKhau = txtMatKhauXacNhan.getText();
            String sdtMoi = txtSDTMoi.getText().trim();
            
            if (matKhau.isEmpty() || sdtMoi.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin");
                return;
            }
            
            if (!Util.checkPassword(matKhau, taiKhoanHienTai.getMatKhau())) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không đúng!");
                return;
            }
            
            nhanVienHienTai.setSoDienThoai(sdtMoi);
            boolean thanhCong = controller.capNhatThongTinCaNhan(nhanVienHienTai);
            
            if (thanhCong) {
                txtSoDienThoai.setText(sdtMoi);
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đổi số điện thoại thành công!");
                modal.close();
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Đổi số điện thoại thất bại!");
            }
        });
        
        btnBox.getChildren().addAll(btnHuy, btnXacNhan);
        root.getChildren().addAll(title, passGroup, sdtGroup, btnBox);
        
        Scene scene = new Scene(root, 400, 280);
        modal.setScene(scene);
        modal.showAndWait();
    }
    
    private void hienThiModalDoiEmail() {
        if (nhanVienHienTai == null || taiKhoanHienTai == null) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin nhân viên");
            return;
        }
        
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Đổi email");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white;");
        
        Label title = new Label("Đổi email");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        VBox passGroup = new VBox(6);
        Label lblPass = new Label("Mật khẩu hiện tại");
        lblPass.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        PasswordField txtMatKhauXacNhan = new PasswordField();
        txtMatKhauXacNhan.setPromptText("Nhập mật khẩu để xác nhận");
        txtMatKhauXacNhan.setPrefHeight(38);
        txtMatKhauXacNhan.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 12;");
        passGroup.getChildren().addAll(lblPass, txtMatKhauXacNhan);
        
        VBox emailGroup = new VBox(6);
        Label lblEmail = new Label("Email mới");
        lblEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        TextField txtEmailMoi = new TextField();
        txtEmailMoi.setPromptText("Nhập email mới");
        txtEmailMoi.setPrefHeight(38);
        txtEmailMoi.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 12;");
        emailGroup.getChildren().addAll(lblEmail, txtEmailMoi);
        
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);
        
        Button btnHuy = new Button("Hủy");
        btnHuy.setPrefWidth(100);
        btnHuy.setPrefHeight(38);
        btnHuy.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #1e293b; -fx-background-radius: 8;");
        btnHuy.setOnAction(e -> modal.close());
        
        Button btnXacNhan = new Button("Xác nhận");
        btnXacNhan.setPrefWidth(100);
        btnXacNhan.setPrefHeight(38);
        btnXacNhan.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;");
        btnXacNhan.setOnAction(e -> {
            String matKhau = txtMatKhauXacNhan.getText();
            String emailMoi = txtEmailMoi.getText().trim();
            
            if (matKhau.isEmpty() || emailMoi.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin");
                return;
            }
            
            if (!Util.checkPassword(matKhau, taiKhoanHienTai.getMatKhau())) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không đúng!");
                return;
            }
            
            nhanVienHienTai.setEmail(emailMoi);
            boolean thanhCong = controller.capNhatThongTinCaNhan(nhanVienHienTai);
        
        if (thanhCong) {
                txtEmail.setText(emailMoi);
                lblHeaderEmail.setText(emailMoi);
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đổi email thành công!");
                modal.close();
        } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Đổi email thất bại!");
            }
        });
        
        btnBox.getChildren().addAll(btnHuy, btnXacNhan);
        root.getChildren().addAll(title, passGroup, emailGroup, btnBox);
        
        Scene scene = new Scene(root, 400, 280);
        modal.setScene(scene);
        modal.showAndWait();
    }
    
    private void hienThiThongBao(Alert.AlertType loai, String tieuDe, String noiDung) {
        Alert alert = new Alert(loai);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}
