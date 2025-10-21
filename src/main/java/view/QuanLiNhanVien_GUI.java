package view;

import controller.NhanVien_Controller;
import model.NhanVien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.List;

public class QuanLiNhanVien_GUI extends BorderPane {
    private String maNhanVienDangChon = null; // ID nhân viên đang chọn

    private TextField tfTenNhanVien = new TextField();
    private TextField tfEmail = new TextField();
    private TextField tfSoDienThoai = new TextField();
    private ComboBox<String> cbGioiTinh = new ComboBox<>();
    private DatePicker dpNgaySinh = new DatePicker();
    private DatePicker dpNgayBatDau = new DatePicker();
    private Button btnLuu = new Button("Lưu");
    private Button btnXoa = new Button("Xóa");
    private Button btnMoi = new Button("Mới");
    private Button btnTimKiem = new Button("Tìm kiếm");

    private TextField tfTimKiem = new TextField();
    private ComboBox<String> cbGioiTinhFilter = new ComboBox<>();

    private TableView<NhanVien> bangNhanVien = new TableView<>();
    private NhanVien_Controller nv_ctrl = new NhanVien_Controller();
    private List<NhanVien> dsNV = nv_ctrl.getDsNhanVien();
    private ObservableList<NhanVien> danhSachMaster = FXCollections.observableArrayList(dsNV);

    public QuanLiNhanVien_GUI() {
        setPadding(new Insets(16));
        VBox container = new VBox(12);
        container.getChildren().addAll(taoFormNhapLieu(), taoBang());
        setCenter(container);
        container.getStylesheets().add(getClass().getResource("/css/Label.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Control.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());

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
        Label lblNgayBatDau = new Label("Ngày bắt đầu");

        cbGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ"));
        cbGioiTinh.setConverter(new StringConverter<>() {
            @Override
            public String toString(String s) {
                return s == null ? "Giới tính" : s;
            }

            @Override
            public String fromString(String s) {
                return s;
            }
        });

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

        cbGioiTinh.setPrefWidth(400);
        cbGioiTinh.setPrefHeight(30);
        cbGioiTinh.getStyleClass().add("cmb");
        dpNgaySinh.setPrefWidth(300);
        dpNgaySinh.getStyleClass().add("date-picker");
        dpNgayBatDau.setPrefWidth(300);
        dpNgayBatDau.getStyleClass().add("date-picker");

        // Cấu hình nút
        btnLuu.setDefaultButton(true);
        btnLuu.getStyleClass().add("btn-luu");
        btnXoa.getStyleClass().add("btn-huy");
        btnMoi.getStyleClass().add("btn-lam-moi");
        btnTimKiem.getStyleClass().add("btn");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(16);
        formGrid.setVgap(10);
        formGrid.add(taoKhuVucLabel(lblTen, tfTenNhanVien), 0, 0);
        formGrid.add(taoKhuVucLabel(lblEmail, tfEmail), 1, 0);
        formGrid.add(taoKhuVucLabel(lblSoDienThoai, tfSoDienThoai), 0, 1);
        formGrid.add(taoKhuVucLabel(lblGioiTinh, cbGioiTinh), 1, 1);
        formGrid.add(taoKhuVucLabel(lblNgaySinh, dpNgaySinh), 0, 2);
        formGrid.add(taoKhuVucLabel(lblNgayBatDau, dpNgayBatDau), 1, 2);

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
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
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
                cbGioiTinh.getSelectionModel().select(gioiTinh);

                if (moi.getNgaySinh() != null) {
                    try {
                        dpNgaySinh.setValue(moi.getNgaySinh());
                    } catch (Exception e) {
                        dpNgaySinh.setValue(null);
                    }
                }

                if (moi.getNgayBatDau() != null) {
                    try {
                        dpNgayBatDau.setValue(moi.getNgayBatDau());
                    } catch (Exception e) {
                        dpNgayBatDau.setValue(null);
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
}