package view.QuanLy;

import controller.NhanVien_Controller;
import model.NhanVien;
import view.QuanLiNhanVien_Modal;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;

public class QuanLiNhanVien_GUI extends BorderPane {
    private TextField tfTimKiem = new TextField();
    private Button btnTimKiem = new Button("Tìm kiếm");
    private Button btnLuu = new Button("Thêm nhân viên");
    private Button btnXoa = new Button("Xóa nhân viên");
    private NhanVien nhanVienDaChon = null;

    private TableView<NhanVien> bangNhanVien = new TableView<>();
    private NhanVien_Controller nv_ctrl = new NhanVien_Controller();

    public QuanLiNhanVien_GUI() {
        setPadding(new Insets(16));

        HBox thanhCongCu = taoKhuVucTimKiem();

        VBox container = new VBox(16, xayDungKhuVucTieuDe(), thanhCongCu, taoBang());
        VBox.setVgrow(bangNhanVien, Priority.ALWAYS); // Cho phép ScrollPane chiếm toàn
        // bộ chiều cao còn lại
        setCenter(container);
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
    }

    private VBox xayDungKhuVucTieuDe() {
        Label tieuDe = new Label("Danh sách nhân viên");
        tieuDe.setStyle("-fx-font-size:26px; -fx-font-weight:800; -fx-text-fill:#111827;");
        VBox box = new VBox(tieuDe);
        box.setPadding(new Insets(4, 0, 8, 0));
        return box;
    }

    private HBox taoKhuVucTimKiem() {
        tfTimKiem.setPromptText("Tìm theo tên hoặc email");
        tfTimKiem.getStyleClass().addAll("text-field");
        tfTimKiem.setPrefHeight(35);
        tfTimKiem.setPrefWidth(400);
        tfTimKiem.setPromptText("Nhập CCCD hoặc Tên để tìm kiếm");
        tfTimKiem.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                xuLyTimKiem();
            }
        });
        btnTimKiem.getStyleClass().add("btn");
        btnLuu.getStyleClass().add("btn-luu");
        btnXoa.getStyleClass().add("btn-huy");

        btnTimKiem.setOnAction(e -> xuLyTimKiem());
        btnLuu.setOnAction(e -> hienThiModal(null));
        btnXoa.setOnAction(e -> xoaNhanVien(nhanVienDaChon));

        // Nhóm tìm kiếm (trái)
        HBox nhomTimKiem = new HBox(8, tfTimKiem, btnTimKiem);
        nhomTimKiem.setAlignment(Pos.CENTER_LEFT);

        // Thanh công cụ chính
        HBox thanhCongCu = new HBox();
        thanhCongCu.setAlignment(Pos.CENTER_LEFT);
        thanhCongCu.setSpacing(10);
        thanhCongCu.setPadding(new Insets(4, 0, 8, 0));

        // Giãn khoảng trống giữa 2 nhóm (trái-phải)
        Region khoangTrong = new Region();
        HBox.setHgrow(khoangTrong, Priority.ALWAYS);

        thanhCongCu.getChildren().addAll(nhomTimKiem, khoangTrong, btnXoa, btnLuu);

        return thanhCongCu;
    }

    private TableView<NhanVien> taoBang() {
        TableColumn<NhanVien, String> colMaNV = new TableColumn<>("Mã NV");
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colMaNV.setPrefWidth(100);

        TableColumn<NhanVien, String> colTenNV = new TableColumn<>("Tên nhân viên");
        colTenNV.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        colTenNV.setPrefWidth(180);

        TableColumn<NhanVien, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        TableColumn<NhanVien, String> colSoDienThoai = new TableColumn<>("Số điện thoại");
        colSoDienThoai.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colSoDienThoai.setPrefWidth(150);

        TableColumn<NhanVien, String> colGioiTinh = new TableColumn<>("Giới tính");
        colGioiTinh.setCellValueFactory(cellData -> {
            boolean gioiTinh = cellData.getValue().getGioiTinh();
            String text = gioiTinh ? "Nam" : "Nữ";
            return new SimpleStringProperty(text);
        });
        colGioiTinh.setPrefWidth(100);

        TableColumn<NhanVien, String> colNgayBatDau = new TableColumn<>("Ngày bắt đầu");
        colNgayBatDau.setCellValueFactory(new PropertyValueFactory<>("ngayBatDau"));
        colNgayBatDau.setPrefWidth(130);

        TableColumn<NhanVien, String> colCCCD = new TableColumn<>("CCCD");
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("CCCD"));
        colCCCD.setPrefWidth(130);

        bangNhanVien.getColumns().addAll(colMaNV, colTenNV, colCCCD, colEmail, colSoDienThoai, colGioiTinh,
                colNgayBatDau);
        bangNhanVien.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bangNhanVien.setPrefHeight(Region.USE_COMPUTED_SIZE);

        List<NhanVien> dsNV = nv_ctrl.getDsNhanVien();
        ObservableList<NhanVien> danhSachMaster = FXCollections.observableArrayList(dsNV);
        bangNhanVien.setItems(danhSachMaster);

        // Sự kiện chọn dòng
        bangNhanVien.setRowFactory(tv -> {
            TableRow<NhanVien> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    NhanVien item = row.getItem();
                    QuanLiNhanVien_Modal modal = new QuanLiNhanVien_Modal(item);
                    modal.hienThi();
                    lamMoi();
                } else if (e.getClickCount() == 1 && !row.isEmpty()) {
                    NhanVien item = row.getItem();
                    nhanVienDaChon = item;
                }
            });
            return row;
        });

        return bangNhanVien;
    }

    public void xuLyTimKiem() {
        String tuKhoa = tfTimKiem.getText();
        if (tuKhoa.isEmpty()) {
            hienThiThongBao("Thông báo", "Vui lòng nhập tên hoặc CCCD của nhân viên cần tìm!", AlertType.INFORMATION);
            lamMoi();
            return;
        }
        List<NhanVien> dsTimDuoc = nv_ctrl.timNhanVien(tuKhoa);
        if (!dsTimDuoc.isEmpty()) {
            bangNhanVien.setItems(FXCollections.observableArrayList(dsTimDuoc));
        } else {
            hienThiThongBao("Thông báo", "Không tìm thấy nhân viên", AlertType.INFORMATION);
            lamMoi();
        }
    }

    private void lamMoi() {
        nhanVienDaChon = null;
        tfTimKiem.clear();
        bangNhanVien.getSelectionModel().clearSelection();
        bangNhanVien.setItems(FXCollections.observableArrayList(nv_ctrl.getDsNhanVien()));
    }

    public void hienThiModal(NhanVien nv) {
        QuanLiNhanVien_Modal nhanVien_Modal = new QuanLiNhanVien_Modal(nv);
        nhanVien_Modal.hienThi();
        lamMoi();
    }

    public void xoaNhanVien(NhanVien nv) {
        if (nv == null) {
            hienThiThongBao("Thông báo", "Vui lòng chọn nhân viên cần xóa", AlertType.ERROR);
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Bạn có chắc muốn xóa nhân viên này?");
        confirm.setContentText(nv.getTenNhanVien());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            StringBuilder loiNhan = new StringBuilder();
            if (nv_ctrl.xoaNhanVien(nv, loiNhan)) {
                hienThiThongBao("Thông báo", loiNhan.toString(), AlertType.INFORMATION);
                lamMoi();

            } else {
                hienThiThongBao("Xóa nhân viên thât bại", loiNhan.toString(), AlertType.ERROR);
            }
        }

    }

    public void hienThiThongBao(String tieuDe, String noiDung, AlertType loaiThongBao) {
        Alert thongBao = new Alert(loaiThongBao);
        thongBao.setTitle(tieuDe);
        thongBao.setContentText(noiDung);
        thongBao.setHeaderText(null);
        thongBao.showAndWait();
    }
}
