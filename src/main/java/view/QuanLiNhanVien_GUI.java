package view;

import controller.NhanVien_Controller;
import model.NhanVien;
import model.TaiKhoan;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.time.LocalDate;

import java.util.List;

public class QuanLiNhanVien_GUI extends BorderPane {
    private TextField tfTenNhanVien = new TextField();
    private TextField tfEmail = new TextField();
    private TextField tfSoDienThoai = new TextField();
    private ComboBox<String> cmbGioiTinh = new ComboBox<>();
    private DatePicker dpNgaySinh = new DatePicker();
    private Button btnLuu = new Button("Lưu");
    private Button btnXoa = new Button("Xóa");
    private Button btnMoi = new Button("Làm mới");
    private Button btnTimKiem = new Button("Tìm kiếm");
    private ComboBox<String> cmbVaiTro = new ComboBox<>();

    private TextField tfTimKiem = new TextField();

    private TextField tfCCCD = new TextField();

    private TableView<NhanVien> bangNhanVien = new TableView<>();
    private NhanVien_Controller nv_ctrl = new NhanVien_Controller();

    public QuanLiNhanVien_GUI() {
        setPadding(new Insets(16));
        VBox container = new VBox(12);
        container.getChildren().addAll(taoFormNhapLieu(), taoBang());
        setCenter(container);
        container.getStylesheets().add(getClass().getResource("/css/Label.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Control.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());

    }

    private Node taoFormNhapLieu() {
        // --- Thiết lập TextField ---
        tfTenNhanVien.setPromptText("Nhập tên nhân viên");
        tfCCCD.setPromptText("Nhập căn cước công dân");
        tfEmail.setPromptText("Nhập email");
        tfSoDienThoai.setPromptText("Nhập số điện thoại");
        tfTimKiem.setPromptText("Tìm theo tên hoặc email");

        for (TextField tf : new TextField[] { tfTenNhanVien, tfCCCD, tfEmail, tfSoDienThoai, tfTimKiem }) {
            tf.getStyleClass().add("text-field");
        }

        // --- Label ---
        Label lblTen = new Label("Tên nhân viên");
        Label lblCCCD = new Label("CCCD");
        Label lblEmail = new Label("Email");
        Label lblSoDienThoai = new Label("Số điện thoại");
        Label lblGioiTinh = new Label("Giới tính");
        Label lblVaiTro = new Label("Vai trò");
        Label lblNgaySinh = new Label("Ngày sinh");

        // --- ComboBox & DatePicker ---
        cmbGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ"));
        cmbGioiTinh.setPromptText("Giới tính");
        cmbGioiTinh.getStyleClass().add("cmb");

        ObservableList<String> dsVaiTro = FXCollections.observableArrayList(nv_ctrl.getDsVaiTroNhanVien());
        cmbVaiTro.setItems(dsVaiTro);
        cmbVaiTro.setPromptText("Vai trò");
        cmbVaiTro.getStyleClass().add("cmb");

        dpNgaySinh.setPromptText("Chọn ngày sinh");
        dpNgaySinh.getStyleClass().add("date-picker");

        // --- Kích thước đồng nhất ---
        double ngang = 220;
        double doc = 35;

        for (TextField tf : new TextField[] { tfTenNhanVien, tfCCCD, tfEmail, tfSoDienThoai }) {
            tf.setPrefWidth(ngang);
            tf.setPrefHeight(doc);
        }

        cmbGioiTinh.setPrefWidth(ngang);
        cmbVaiTro.setPrefWidth(ngang);
        dpNgaySinh.setPrefWidth(ngang);

        tfTimKiem.setPrefWidth(400);
        tfTimKiem.setPrefHeight(35);
        tfTimKiem.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                xuLyTimKiem();
            }
        });

        // --- Nút ---
        btnLuu.getStyleClass().add("btn-luu");
        btnXoa.getStyleClass().add("btn-huy");
        btnMoi.getStyleClass().add("btn-lam-moi");
        btnTimKiem.getStyleClass().add("btn");

        btnLuu.setOnAction(e -> xuLyThem());
        btnXoa.setOnAction(e -> xuLyXoa());
        btnMoi.setOnAction(e -> lamMoi());
        btnTimKiem.setOnAction(e -> xuLyTimKiem());

        // --- GridPane (4 cột) ---
        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(12);

        // Hàng 1: 4 cột
        formGrid.add(taoKhuVucLabel(lblTen, tfTenNhanVien), 0, 0);
        formGrid.add(taoKhuVucLabel(lblCCCD, tfCCCD), 1, 0);
        formGrid.add(taoKhuVucLabel(lblEmail, tfEmail), 2, 0);
        formGrid.add(taoKhuVucLabel(lblSoDienThoai, tfSoDienThoai), 3, 0);

        // Hàng 2: 3 cột
        formGrid.add(taoKhuVucLabel(lblGioiTinh, cmbGioiTinh), 0, 1);
        formGrid.add(taoKhuVucLabel(lblVaiTro, cmbVaiTro), 1, 1);
        formGrid.add(taoKhuVucLabel(lblNgaySinh, dpNgaySinh), 2, 1);

        // Hàng 3: nút
        HBox khuVucNut = new HBox(10, btnLuu, btnXoa, btnMoi);
        khuVucNut.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(khuVucNut, 0, 2, 4, 1);
        GridPane.setMargin(khuVucNut, new Insets(10, 0, 0, 0));

        // Hàng 4: tìm kiếm
        HBox khuVucTimKiem = new HBox(10, tfTimKiem, btnTimKiem);
        khuVucTimKiem.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(khuVucTimKiem, 0, 3, 4, 1);
        GridPane.setMargin(khuVucTimKiem, new Insets(10, 0, 0, 0));

        return formGrid;
    }

    private VBox taoKhuVucLabel(Label lbl, Node control) {
        lbl.getStyleClass().add("label");
        VBox box = new VBox(6, lbl, control);
        box.setPrefWidth(360);
        return box;
    }

    private ScrollPane taoBang() {

        // Cột Mã nhân viên
        TableColumn<NhanVien, String> colMaNV = new TableColumn<>("Mã NV");
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colMaNV.setPrefWidth(100);

        // Cột Tên nhân viên
        TableColumn<NhanVien, String> colTenNV = new TableColumn<>("Tên nhân viên");
        colTenNV.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        colTenNV.setPrefWidth(180);

        // Cột Email
        TableColumn<NhanVien, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        // Cột Số điện thoại
        TableColumn<NhanVien, String> colSoDienThoai = new TableColumn<>("Số điện thoại");
        colSoDienThoai.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colSoDienThoai.setPrefWidth(150);

        // Cột Giới tính
        TableColumn<NhanVien, String> colGioiTinh = new TableColumn<>("Giới tính");
        colGioiTinh.setCellValueFactory(cellData -> {
            boolean gioiTinh = cellData.getValue().getGioiTinh();
            String text = gioiTinh ? "Nam" : "Nữ";
            return new SimpleStringProperty(text);
        });
        colGioiTinh.setPrefWidth(100);

        // Cột Ngày bắt đầu
        TableColumn<NhanVien, String> colNgayBatDau = new TableColumn<>("Ngày bắt đầu");
        colNgayBatDau.setCellValueFactory(new PropertyValueFactory<>("ngayBatDau"));
        colNgayBatDau.setPrefWidth(130);

        TableColumn<NhanVien, String> colCCCD = new TableColumn<>("CCCD");
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("CCCD"));
        colCCCD.setPrefWidth(130);

        bangNhanVien.getColumns().add(colMaNV);
        bangNhanVien.getColumns().add(colTenNV);
        bangNhanVien.getColumns().add(colCCCD);
        bangNhanVien.getColumns().add(colEmail);
        bangNhanVien.getColumns().add(colSoDienThoai);
        bangNhanVien.getColumns().add(colGioiTinh);
        bangNhanVien.getColumns().add(colNgayBatDau);
        bangNhanVien.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bangNhanVien.setPrefHeight(440);
        bangNhanVien.getStyleClass().add("table");

        List<NhanVien> dsNV = nv_ctrl.getDsNhanVien();
        ObservableList<NhanVien> danhSachMaster = FXCollections.observableArrayList(dsNV);

        bangNhanVien.setItems(danhSachMaster);

        // Sự kiện chọn dòng
        bangNhanVien.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null) {
                tfTenNhanVien.setText(moi.getTenNhanVien());
                tfEmail.setText(moi.getEmail());
                tfSoDienThoai.setText(moi.getSoDienThoai());
                tfCCCD.setText(moi.getCCCD());
                String gioiTinh = "";
                if (moi.getGioiTinh()) {
                    gioiTinh = "Nam";
                } else {
                    gioiTinh = "Nữ";
                }
                cmbGioiTinh.getSelectionModel().select(gioiTinh);

                if (moi.getNgaySinh() != null) {
                    try {
                        dpNgaySinh.setValue(moi.getNgaySinh());
                    } catch (Exception e) {
                        dpNgaySinh.setValue(null);
                    }
                }

                String vaiTro = moi.getTaiKhoan().getVaiTro();
                cmbVaiTro.getSelectionModel().select(vaiTro);

            }
        });
        ScrollPane scrollPane = new ScrollPane(bangNhanVien);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: white;");
        return scrollPane;
    }

    public void xuLyThem() {
        String tenNV = tfTenNhanVien.getText();
        LocalDate ngaySinh = dpNgaySinh.getValue();
        String gioiTinh = cmbGioiTinh.getSelectionModel().getSelectedItem();
        String email = tfEmail.getText();
        String soDienThoai = tfSoDienThoai.getText();
        String vaiTro = cmbVaiTro.getSelectionModel().getSelectedItem();
        String cccd = tfCCCD.getText();

        if (gioiTinh == null || vaiTro == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn giới tính và vai trò!");
            alert.showAndWait();
            return;
        }

        boolean gioiTinhValue = gioiTinh.equals("Nam");

        TaiKhoan tk = new TaiKhoan(soDienThoai, vaiTro);
        NhanVien nv = new NhanVien(tenNV, tk, gioiTinhValue, ngaySinh, email, soDienThoai, cccd);

        StringBuilder loiNhan = new StringBuilder();
        boolean hopLe = nv_ctrl.kiemTra(nv, loiNhan);
        if (!hopLe) {
            Alert canhBao = new Alert(Alert.AlertType.ERROR);
            canhBao.setTitle("Cảnh báo");
            canhBao.setHeaderText(null);
            canhBao.setContentText(loiNhan.toString());
            canhBao.showAndWait();
            return;
        }
        if (nv_ctrl.themNhanVien(nv, loiNhan)) {
            Alert thongBao = new Alert(Alert.AlertType.INFORMATION);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText(loiNhan.toString());
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
            lamMoi(); // Làm mới giao diện sau khi thêm thành công
        } else {
            Alert thongBao = new Alert(Alert.AlertType.ERROR);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText(loiNhan.toString());
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
        }
    }

    private void lamMoi() {
        tfTenNhanVien.clear();
        tfEmail.clear();
        tfCCCD.clear();
        tfSoDienThoai.clear();

        cmbGioiTinh.getSelectionModel().clearSelection();
        cmbVaiTro.getSelectionModel().clearSelection();

        dpNgaySinh.setValue(null);

        bangNhanVien.getSelectionModel().clearSelection();

        tfTenNhanVien.requestFocus();

        List<NhanVien> dsNV = nv_ctrl.getDsNhanVien();
        bangNhanVien.setItems(FXCollections.observableArrayList(dsNV));
    }

    public void xuLyTimKiem() {
        String tuKhoa = tfTimKiem.getText();
        List<NhanVien> dsTimDuoc = nv_ctrl.timNhanVien(tuKhoa);
        if (dsTimDuoc.size() != 0) {
            ObservableList<NhanVien> dsNV = FXCollections.observableArrayList(dsTimDuoc);
            bangNhanVien.setItems(dsNV);
        } else {
            Alert thongBao = new Alert(Alert.AlertType.ERROR);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText("Không tìm thấy nhân viên");
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
            lamMoi();

        }
    }

    public void xuLyXoa() {
        String tenNV = tfTenNhanVien.getText();
        LocalDate ngaySinh = dpNgaySinh.getValue();
        String gioiTinh = cmbGioiTinh.getSelectionModel().getSelectedItem();
        String email = tfEmail.getText();
        String soDienThoai = tfSoDienThoai.getText();
        String vaiTro = cmbVaiTro.getSelectionModel().getSelectedItem();
        String cccd = tfCCCD.getText();
        TaiKhoan tk = new TaiKhoan(soDienThoai, vaiTro);
        boolean gioiTinhValue = "Nam".equals(gioiTinh);
        NhanVien nv = new NhanVien(tenNV, tk, gioiTinhValue, ngaySinh, email, soDienThoai, cccd);
        if (nv_ctrl.xoaNhanVien(nv)) {
            lamMoi();
        }
        return;

    }
}