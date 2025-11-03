package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import controller.NhanVien_Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.NhanVien;
import model.TaiKhoan;

public class QuanLiNhanVien_Modal {

    private final Stage cuaSo = new Stage();
    private final NhanVien_Controller nVien_Controller = new NhanVien_Controller();

    private TextField tfMaNhanVien = new TextField();
    private TextField tfTenNhanVien = new TextField();
    private TextField tfCCCD = new TextField();
    private TextField tfSoDienThoai = new TextField();
    private TextField tfEmail = new TextField();
    private ComboBox<String> cmbGioiTinh = new ComboBox<>();
    private DatePicker dpNgaySinh = new DatePicker();
    private ComboBox<String> cmbVaiTro = new ComboBox<>();
    private TextField tfDiaChi = new TextField();
    private Button btnLuu = new Button("Lưu");
    private Button btnXoa = new Button("Xóa");
    private Button btnMoi = new Button("Làm mới");
    private DateTimeFormatter dinhDang = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QuanLiNhanVien_Modal(NhanVien nv) {
        cuaSo.initModality(Modality.APPLICATION_MODAL);
        cuaSo.setTitle("Thông tin nhân viên");

        VBox khungChinh = new VBox(25);
        khungChinh.setPadding(new Insets(20));
        khungChinh.setAlignment(Pos.TOP_CENTER);

        khungChinh.getStylesheets().add(getClass().getResource("/css/Label.css").toExternalForm());
        khungChinh.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        khungChinh.getStylesheets().add(getClass().getResource("/css/Control.css").toExternalForm());

        Label tieuDe = new Label("Thông tin nhân viên");
        tieuDe.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
        VBox.setMargin(tieuDe, new Insets(10, 0, 0, 0));

        Label lblMaNV = new Label("Mã nhân viên:");
        Label lblTen = new Label("Tên:");
        Label lblCCCC = new Label("CCCD:");
        Label lblSoDienThoai = new Label("Số điện thoại:");
        Label lblEmail = new Label("Email:");
        Label lblGioiTinh = new Label("Giới tính:");
        Label lblNgaySinh = new Label("Ngày sinh:");
        Label lblVaiTro = new Label("Vai trò:");
        Label lblDiaChi = new Label("Địa chỉ:");

        dpNgaySinh.setPromptText("dd/MM/yyyy");
        dpNgaySinh.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dinhDang.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(string, dinhDang);
                } catch (DateTimeParseException e) {
                    // Không crash nếu nhập sai
                    System.err.println("Sai định dạng ngày, yêu cầu dd/MM/yyyy");
                    return null;
                }
            }
        });
        dpNgaySinh.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                try {
                    LocalDate parsedDate = LocalDate.parse(newText, dinhDang);
                    dpNgaySinh.setValue(parsedDate);
                } catch (DateTimeParseException e) {
                    // Bỏ qua nếu sai định dạng
                }
            }
        });

        ObservableList<String> dsVaiTro = FXCollections.observableArrayList(nVien_Controller.getDsVaiTroNhanVien());
        cmbVaiTro.setPromptText("Vai trò");
        cmbVaiTro.setItems(dsVaiTro);
        cmbVaiTro.getStyleClass().add("cmb");

        cmbGioiTinh.setPromptText("Giới tính");
        cmbGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ"));
        cmbGioiTinh.getStyleClass().add("cmb");

        GridPane luoiNhapLieu = new GridPane();
        luoiNhapLieu.setHgap(20); // Khoảng cách giữa các ô
        luoiNhapLieu.setVgap(18);
        luoiNhapLieu.setAlignment(Pos.CENTER);

        ColumnConstraints cotLabel = new ColumnConstraints(100); // chiều rộng cột
        cotLabel.setHalignment(HPos.RIGHT); // thẳng hàng bên phải giống excel

        ColumnConstraints cotNhap = new ColumnConstraints(400);
        luoiNhapLieu.getColumnConstraints().addAll(cotLabel, cotNhap);

        double chieuRong = 400;
        double chieuCao = 40;

        tfMaNhanVien.setStyle("-fx-opacity: 0.85; -fx-background-color: #f5f5f5;");
        tfMaNhanVien.setEditable(false);
        tfMaNhanVien.setFocusTraversable(false);

        TextField[] cacTF = { tfMaNhanVien, tfTenNhanVien, tfCCCD, tfSoDienThoai, tfEmail, tfDiaChi };
        for (TextField tf : cacTF) {
            tf.setPrefWidth(chieuRong);
            tf.setPrefHeight(chieuCao);
        }

        cmbGioiTinh.setPrefSize(chieuRong, chieuCao);
        dpNgaySinh.setPrefSize(chieuRong, chieuCao);
        cmbVaiTro.setPrefSize(chieuRong, chieuCao);

        HBox hangNut = new HBox(25);
        hangNut.setAlignment(Pos.BASELINE_LEFT);
        btnLuu.getStyleClass().add("btn-luu");
        btnLuu.setPrefWidth(80);
        btnXoa.getStyleClass().add("btn-huy");
        btnXoa.setPrefWidth(64);
        btnMoi.getStyleClass().add("btn-lam-moi");

        btnLuu.setOnAction(e -> xuLyLuu());
        btnXoa.setOnAction(e -> xuLyXoa());
        btnMoi.setOnAction(e -> lamMoi());

        hangNut.getChildren().addAll(btnLuu, btnXoa, btnMoi);

        luoiNhapLieu.add(lblMaNV, 0, 0);
        luoiNhapLieu.add(tfMaNhanVien, 1, 0);
        luoiNhapLieu.add(lblTen, 0, 1);
        luoiNhapLieu.add(tfTenNhanVien, 1, 1);
        luoiNhapLieu.add(lblCCCC, 0, 2);
        luoiNhapLieu.add(tfCCCD, 1, 2);
        luoiNhapLieu.add(lblSoDienThoai, 0, 3);
        luoiNhapLieu.add(tfSoDienThoai, 1, 3);
        luoiNhapLieu.add(lblEmail, 0, 4);
        luoiNhapLieu.add(tfEmail, 1, 4);
        luoiNhapLieu.add(lblGioiTinh, 0, 5);
        luoiNhapLieu.add(cmbGioiTinh, 1, 5);
        luoiNhapLieu.add(lblNgaySinh, 0, 6);
        luoiNhapLieu.add(dpNgaySinh, 1, 6);
        luoiNhapLieu.add(lblVaiTro, 0, 7);
        luoiNhapLieu.add(cmbVaiTro, 1, 7);
        luoiNhapLieu.add(lblDiaChi, 0, 8);
        luoiNhapLieu.add(tfDiaChi, 1, 8);
        luoiNhapLieu.add(hangNut, 1, 9);

        duLieu(nv);

        khungChinh.getChildren().addAll(tieuDe, luoiNhapLieu);

        double chieuRongManHinh = Screen.getPrimary().getVisualBounds().getWidth() * 0.37;
        double chieuCaoManHinh = Screen.getPrimary().getVisualBounds().getHeight() * 0.7;

        Scene canh = new Scene(khungChinh, chieuRongManHinh, chieuCaoManHinh);
        cuaSo.setScene(canh);
    }

    public void hienThi() {
        cuaSo.showAndWait();
    }

    public void lamMoi() {
        tfTenNhanVien.clear();
        tfEmail.clear();
        tfCCCD.clear();
        tfSoDienThoai.clear();

        cmbGioiTinh.getSelectionModel().clearSelection();
        cmbVaiTro.getSelectionModel().clearSelection();

        dpNgaySinh.setValue(null);
    }

    private void duLieu(NhanVien nv) {
        if (nv == null)
            return;

        tfMaNhanVien.setText(nv.getMaNhanVien());
        tfTenNhanVien.setText(nv.getTenNhanVien());
        tfEmail.setText(nv.getEmail());
        tfCCCD.setText(nv.getCCCD());
        tfSoDienThoai.setText(nv.getSoDienThoai());
        tfDiaChi.setText(nv.getDiaChi());
        dpNgaySinh.setValue(nv.getNgaySinh());

        cmbGioiTinh.getSelectionModel().select(nv.getGioiTinh() ? "Nam" : "Nữ");

        String vaiTro = nv.getTaiKhoan().getVaiTro();
        if (vaiTro.equalsIgnoreCase("admin"))
            cmbVaiTro.getSelectionModel().select("Quản lý");
        else
            cmbVaiTro.getSelectionModel().select("Nhân viên");
    }

    private NhanVien layDuLieu() {
        String maNV = tfMaNhanVien.getText().trim();
        String tenNV = tfTenNhanVien.getText().trim();
        LocalDate ngaySinh = dpNgaySinh.getValue();
        String gioiTinh = cmbGioiTinh.getSelectionModel().getSelectedItem();
        String email = tfEmail.getText().trim();
        String soDienThoai = tfSoDienThoai.getText().trim();
        String vaiTroHienThi = cmbVaiTro.getSelectionModel().getSelectedItem();
        String cccd = tfCCCD.getText().trim();
        String diaChi = tfDiaChi.getText().trim();

        if (gioiTinh == null || vaiTroHienThi == null || vaiTroHienThi.equalsIgnoreCase("Tất cả")) {
            hienThiThongBao("Cảnh báo", "Vui lòng chọn giới tính và vai trò", AlertType.ERROR);
            return null;
        }
        boolean gioiTinhValue = gioiTinh.equals("Nam");

        String vaiTroDB = vaiTroHienThi.equals("Quản lý") ? "admin" : "employee";

        TaiKhoan tk = new TaiKhoan(soDienThoai, vaiTroDB);

        NhanVien nv;

        if (maNV.isEmpty())
            nv = new NhanVien(tenNV, tk, gioiTinhValue, ngaySinh, email, soDienThoai, cccd, diaChi);
        else
            nv = new NhanVien(maNV, cccd, tenNV, tk, gioiTinhValue, ngaySinh, email, soDienThoai, LocalDate.now(),
                    "Đang làm việc", diaChi);

        StringBuilder loi = new StringBuilder();
        if (!nVien_Controller.kiemTra(nv, loi)) {
            hienThiThongBao("Cảnh báo", loi.toString(), AlertType.ERROR);
            return null;
        }

        return nv;
    }

    public void xuLyLuu() {
        NhanVien nv = layDuLieu();
        if (nv == null)
            return;

        StringBuilder loi = new StringBuilder();

        if (nVien_Controller.luu(nv, loi)) {
            hienThiThongBao("Thông báo", loi.toString(), AlertType.INFORMATION);
            cuaSo.close();
        } else {
            hienThiThongBao("Cảnh báo", loi.toString(), AlertType.ERROR);
        }
    }

    public void xuLyXoa() {
        NhanVien nvMoi = layDuLieu();
        StringBuilder loiNhan = new StringBuilder();
        if (nVien_Controller.xoaNhanVien(nvMoi, loiNhan)) {
            hienThiThongBao("Thông báo", loiNhan.toString(), AlertType.INFORMATION);
            cuaSo.close();
        } else {
            hienThiThongBao("Cảnh báo", loiNhan.toString(), AlertType.ERROR);
        }
    }

    public void hienThiThongBao(String tieuDe, String noiDung, AlertType loaiThongBao) {
        Alert thongBao = new Alert(loaiThongBao);
        thongBao.setContentText(noiDung);
        thongBao.setTitle(tieuDe);
        thongBao.setHeaderText(null);
        thongBao.showAndWait();
    }
}
