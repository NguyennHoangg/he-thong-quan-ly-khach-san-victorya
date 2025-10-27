package view;

import controller.NhanVien_Controller;
import model.NhanVien;
import model.TaiKhoan;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class QuanLiNhanVien_GUI extends BorderPane {
    private String maNhanVienDangChon = null; // ID nhân viên đang chọn

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
    private ComboBox<String> cmbGioiTinhFilter = new ComboBox<>();

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
        tfTenNhanVien.setPromptText("Nhập tên nhân viên");
        tfTenNhanVien.getStyleClass().add("text-field");
        tfEmail.setPromptText("Nhập email");
        tfEmail.getStyleClass().add("text-field");
        tfSoDienThoai.setPromptText("Nhập số điện thoại");
        tfSoDienThoai.getStyleClass().add("text-field");
        tfTimKiem.setPromptText("Tìm theo tên hoặc email");
        Label lblTen = new Label("Tên nhân viên");
        Label lblEmail = new Label("Email");
        Label lblSoDienThoai = new Label("Số điện thoại");
        Label lblGioiTinh = new Label("Giới tính");
        Label lblNgaySinh = new Label("Ngày sinh");
        Label lblVaiTro = new Label("Vai trò");

        cmbGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ"));
        cmbGioiTinh.setPromptText("Giới tính");

        cmbVaiTro.setPromptText("Vai trò");
        ObservableList<String> dsVaiTro = FXCollections.observableArrayList(nv_ctrl.getDsVaiTroNhanVien());
        cmbVaiTro.setItems(dsVaiTro);

        // Cấu hình kích thước
        tfTenNhanVien.setPrefWidth(300);
        tfTenNhanVien.setPrefHeight(40);
        tfEmail.setPrefWidth(300);
        tfEmail.setPrefHeight(40);
        tfSoDienThoai.setPrefWidth(300);
        tfSoDienThoai.setPrefHeight(40);
        tfTimKiem.setPrefWidth(300);
        tfTimKiem.setPrefHeight(40);
        tfTimKiem.getStyleClass().add("text-field");

        cmbGioiTinh.setPrefWidth(400);
        cmbGioiTinh.setPrefHeight(30);
        cmbGioiTinh.getStyleClass().add("cmb");
        cmbVaiTro.setPrefWidth(400);
        cmbVaiTro.setPrefHeight(30);
        cmbVaiTro.getStyleClass().add("cmb");
        dpNgaySinh.setPrefWidth(400);
        dpNgaySinh.getStyleClass().add("date-picker");

        // Cấu hình nút
        btnLuu.setDefaultButton(true);
        btnLuu.getStyleClass().add("btn-luu");
        btnXoa.getStyleClass().add("btn-huy");
        btnMoi.getStyleClass().add("btn-lam-moi");
        btnTimKiem.getStyleClass().add("btn");

        btnLuu.setOnAction(e -> xuLyThem());
        btnMoi.setOnAction(e -> lamMoi());

        GridPane formGrid = new GridPane();
        formGrid.setHgap(16);
        formGrid.setVgap(10);
        formGrid.add(taoKhuVucLabel(lblTen, tfTenNhanVien), 0, 0);
        formGrid.add(taoKhuVucLabel(lblEmail, tfEmail), 1, 0);
        formGrid.add(taoKhuVucLabel(lblSoDienThoai, tfSoDienThoai), 0, 1);
        formGrid.add(taoKhuVucLabel(lblGioiTinh, cmbGioiTinh), 0, 2);
        formGrid.add(taoKhuVucLabel(lblNgaySinh, dpNgaySinh), 1, 1);
        formGrid.add(taoKhuVucLabel(lblVaiTro, cmbVaiTro), 1, 2);

        HBox khuVucNut = new HBox(10, btnLuu, btnXoa, btnMoi);
        formGrid.add(khuVucNut, 0, 3, 2, 1); // hàng 3, chiếm 2 cột
        GridPane.setMargin(khuVucNut, new Insets(10, 0, 0, 0));

        HBox khuVucTimKiem = new HBox(10, tfTimKiem, btnTimKiem);
        formGrid.add(khuVucTimKiem, 0, 4, 2, 1); // hàng 4, chiếm 2 cột
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

        bangNhanVien.getColumns().add(colMaNV);
        bangNhanVien.getColumns().add(colTenNV);
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
                maNhanVienDangChon = moi.getMaNhanVien();
                tfTenNhanVien.setText(moi.getTenNhanVien());
                tfEmail.setText(moi.getEmail());
                tfSoDienThoai.setText(moi.getSoDienThoai());
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
        TaiKhoan tk = new TaiKhoan(soDienThoai, vaiTro);
        boolean gioiTinhValue = "Nam".equals(gioiTinh);
        NhanVien nv = new NhanVien(tenNV, tk, gioiTinhValue, ngaySinh, email, soDienThoai);
        if (nv_ctrl.themNhanVien(nv)) {
            Alert thongBao = new Alert(Alert.AlertType.INFORMATION);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText("Thêm nhân viên thành công!");
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
            lamMoi();

        } else {
            Alert thongBao = new Alert(Alert.AlertType.ERROR);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText("Thêm nhân viên Thất bại!");
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
        }

    }

    private void lamMoi() {
        tfTenNhanVien.clear();
        tfEmail.clear();
        tfSoDienThoai.clear();

        cmbGioiTinh.getSelectionModel().clearSelection();
        cmbVaiTro.getSelectionModel().clearSelection();

        dpNgaySinh.setValue(null);

        bangNhanVien.getSelectionModel().clearSelection();

        tfTenNhanVien.requestFocus();
        bangNhanVien.getItems().clear();

        List<NhanVien> dsNV = nv_ctrl.getDsNhanVien();

        ObservableList<NhanVien> danhSachMoi = FXCollections.observableArrayList(dsNV);
        bangNhanVien.setItems(danhSachMoi);

        System.out.println("Bảng nhân viên đã được làm mới!");
    }
}