package view;

import controller.DichVu_Controller;
import model.DichVu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;


import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class QuanLiDichVu_GUI extends BorderPane {
    private String maDichVuDangChon = null; // ID dịch vụ đang chọn

    private TextField tfTenDichVu = new TextField();
    private TextField tfGia = new TextField();
    private TextField tfDonViTinh = new TextField();
    private TextArea taMoTa = new TextArea();
    private Button btnLuu = new Button("Lưu");
    private Button btnXoa = new Button("Xóa");
    private Button btnMoi = new Button("Mới");
    private Button btnTimKiem = new Button("Tìm kiếm");

    private TextField tfTimKiem = new TextField();

    private TableView<DichVu> bangDichVu = new TableView<>();
    private DichVu_Controller dv_ctrl = new DichVu_Controller();

    public QuanLiDichVu_GUI() {
        setPadding(new Insets(16));
        VBox container = new VBox(12);
        container.getChildren().addAll(taoFormNhapLieu(), taoBang());
        setCenter(container);
        container.getStylesheets().add(getClass().getResource("/css/Label.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Control.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());

        // Gán sự kiện cho các nút
        // ganSuKien();
    }

    private Node taoFormNhapLieu() {
        tfTenDichVu.setPromptText("Nhập tên dịch vụ");
        tfTenDichVu.getStyleClass().add("text-field");
        tfGia.setPromptText("Nhập giá");
        tfGia.getStyleClass().add("text-field");
        tfDonViTinh.setPromptText("Nhập đơn vị tính");
        tfDonViTinh.getStyleClass().add("text-field");
        tfTimKiem.setPromptText("Tìm theo tên hoặc mã dịch vụ");

        taMoTa.setPromptText("Nhập mô tả dịch vụ");
        taMoTa.getStyleClass().add("text-area");
        taMoTa.setWrapText(true);
        taMoTa.setPrefRowCount(3);

        Label lblTenDichVu = new Label("Tên dịch vụ");
        Label lblGia = new Label("Giá");
        Label lblDonViTinh = new Label("Đơn vị tính");
        Label lblMoTa = new Label("Mô tả");

        // Cấu hình kích thước
        tfTenDichVu.setPrefWidth(300);
        tfTenDichVu.setPrefHeight(40);
        tfGia.setPrefWidth(300);
        tfGia.setPrefHeight(40);
        tfDonViTinh.setPrefWidth(300);
        tfDonViTinh.setPrefHeight(40);
        tfTimKiem.setPrefWidth(300);
        tfTimKiem.setPrefHeight(40);
        tfTimKiem.getStyleClass().add("text-field");

        taMoTa.setPrefWidth(300);
        taMoTa.setPrefHeight(80);

        // Cấu hình nút
        btnLuu.setDefaultButton(true);
        btnLuu.getStyleClass().add("btn-luu");
        btnXoa.getStyleClass().add("btn-huy");
        btnMoi.getStyleClass().add("btn-lam-moi");
        btnTimKiem.getStyleClass().add("btn");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(16);
        formGrid.setVgap(10);
        formGrid.add(taoKhuVucLabel(lblTenDichVu, tfTenDichVu), 0, 0);
        formGrid.add(taoKhuVucLabel(lblGia, tfGia), 1, 0);
        formGrid.add(taoKhuVucLabel(lblDonViTinh, tfDonViTinh), 0, 1);
        formGrid.add(taoKhuVucLabel(lblMoTa, taMoTa), 1, 1);

        HBox khuVucNut = new HBox(10, btnLuu, btnXoa, btnMoi);
        formGrid.add(khuVucNut, 0, 2, 2, 1); // hàng 2, chiếm 2 cột
        GridPane.setMargin(khuVucNut, new Insets(10, 0, 0, 0));

        HBox khuVucTimKiem = new HBox(10, tfTimKiem, btnTimKiem);
        formGrid.add(khuVucTimKiem, 0, 3, 2, 1); // hàng 3, chiếm 2 cột
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
        // Cột Mã dịch vụ
        TableColumn<DichVu, String> colMaDV = new TableColumn<>("Mã DV");
        colMaDV.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));
        colMaDV.setPrefWidth(100);

        // Cột Tên dịch vụ
        TableColumn<DichVu, String> colTenDV = new TableColumn<>("Tên dịch vụ");
        colTenDV.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        colTenDV.setPrefWidth(200);

        // Cột Giá
        TableColumn<DichVu, Double> colGia = new TableColumn<>("Giá");
        colGia.setCellValueFactory(cellData -> {
            // Convert float to Double for the cell
            return new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getGia()).asObject();
        });
        colGia.setPrefWidth(120);
        // Format giá tiền
        colGia.setCellFactory(column -> new TableCell<DichVu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
                    setText(formatter.format(item));
                }
            }
        });

        // Cột Đơn vị tính
        TableColumn<DichVu, String> colDonViTinh = new TableColumn<>("Đơn vị tính");
        colDonViTinh.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));
        colDonViTinh.setPrefWidth(120);

        // Cột Mô tả
        TableColumn<DichVu, String> colMoTa = new TableColumn<>("Mô tả");
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        colMoTa.setPrefWidth(300);

        bangDichVu.getColumns().add(colMaDV);
        bangDichVu.getColumns().add(colTenDV);
        bangDichVu.getColumns().add(colGia);
        bangDichVu.getColumns().add(colDonViTinh);
        bangDichVu.getColumns().add(colMoTa);
        bangDichVu.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bangDichVu.setPrefHeight(440);
        bangDichVu.getStyleClass().add("table-view");

        // Load dữ liệu
        List<DichVu> dsDV = dv_ctrl.getDsDichVu();
        ObservableList<DichVu> danhSachMaster = FXCollections.observableArrayList(dsDV);
        bangDichVu.setItems(danhSachMaster);

        // Sự kiện chọn dòng
        bangDichVu.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null) {
                maDichVuDangChon = moi.getMaDichVu();
                tfTenDichVu.setText(moi.getTenDichVu());
                tfGia.setText(String.valueOf(moi.getGia()));
                tfDonViTinh.setText(moi.getDonViTinh());
                taMoTa.setText(moi.getMoTa());
            }
        });

        ScrollPane scrollPane = new ScrollPane(bangDichVu);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: white;");
        return scrollPane;
    }

    private void ganSuKien() {
        // Nút Lưu
        btnLuu.setOnAction(e -> {
            if (kiemTraDuLieu()) {
                if (maDichVuDangChon == null) {
                    // Thêm mới
                    themDichVu();
                } else {
                    // Cập nhật
                    capNhatDichVu();
                }
            }
        });

        // Nút Xóa
        btnXoa.setOnAction(e -> {
            if (maDichVuDangChon != null) {
                xoaDichVu();
            } else {
                hienThiThongBao("Vui lòng chọn dịch vụ cần xóa!", Alert.AlertType.WARNING);
            }
        });

        // Nút Mới
        btnMoi.setOnAction(e -> {
            lamMoi();
        });

        // Nút Tìm kiếm
        btnTimKiem.setOnAction(e -> {
            timKiemDichVu();
        });
    }

    private boolean kiemTraDuLieu() {
        if (tfTenDichVu.getText().trim().isEmpty()) {
            hienThiThongBao("Vui lòng nhập tên dịch vụ!", Alert.AlertType.WARNING);
            tfTenDichVu.requestFocus();
            return false;
        }

        if (tfGia.getText().trim().isEmpty()) {
            hienThiThongBao("Vui lòng nhập giá!", Alert.AlertType.WARNING);
            tfGia.requestFocus();
            return false;
        }

        try {
            float gia = Float.parseFloat(tfGia.getText().trim());
            if (gia <= 0) {
                hienThiThongBao("Giá phải lớn hơn 0!", Alert.AlertType.WARNING);
                tfGia.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            hienThiThongBao("Giá không hợp lệ!", Alert.AlertType.WARNING);
            tfGia.requestFocus();
            return false;
        }

        if (tfDonViTinh.getText().trim().isEmpty()) {
            hienThiThongBao("Vui lòng nhập đơn vị tính!", Alert.AlertType.WARNING);
            tfDonViTinh.requestFocus();
            return false;
        }

        return true;
    }

    private void themDichVu() {
        // String tenDichVu = tfTenDichVu.getText().trim();
        // float gia = Float.parseFloat(tfGia.getText().trim());
        // String donViTinh = tfDonViTinh.getText().trim();
        // String moTa = taMoTa.getText().trim();

        // // Tạo mã dịch vụ tự động (bạn có thể thay đổi logic này)
        // String maDV = "DV" + String.format("%03d", bangDichVu.getItems().size() + 1);

        // DichVu dichVu = new DichVu(maDV, tenDichVu, gia, moTa, donViTinh);

        // if (dv_ctrl.themDichVu(dichVu)) {
        // hienThiThongBao("Thêm dịch vụ thành công!", Alert.AlertType.INFORMATION);
        // loadDuLieu();
        // lamMoi();
        // } else {
        // hienThiThongBao("Thêm dịch vụ thất bại!", Alert.AlertType.ERROR);
        // }
    }

    private void capNhatDichVu() {
        // String tenDichVu = tfTenDichVu.getText().trim();
        // float gia = Float.parseFloat(tfGia.getText().trim());
        // String donViTinh = tfDonViTinh.getText().trim();
        // String moTa = taMoTa.getText().trim();

        // DichVu dichVu = new DichVu(maDichVuDangChon, tenDichVu, gia, moTa,
        // donViTinh);

        // if (dv_ctrl.capNhatDichVu(dichVu)) {
        // hienThiThongBao("Cập nhật dịch vụ thành công!", Alert.AlertType.INFORMATION);
        // loadDuLieu();
        // lamMoi();
        // } else {
        // hienThiThongBao("Cập nhật dịch vụ thất bại!", Alert.AlertType.ERROR);
        // }
    }

    private void xoaDichVu() {
        // Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        // confirm.setTitle("Xác nhận");
        // confirm.setHeaderText("Xóa dịch vụ");
        // confirm.setContentText("Bạn có chắc chắn muốn xóa dịch vụ này?");

        // if (confirm.showAndWait().get() == ButtonType.OK) {
        // if (dv_ctrl.xoaDichVu(maDichVuDangChon)) {
        // hienThiThongBao("Xóa dịch vụ thành công!", Alert.AlertType.INFORMATION);
        // loadDuLieu();
        // lamMoi();
        // } else {
        // hienThiThongBao("Xóa dịch vụ thất bại!", Alert.AlertType.ERROR);
        // }
        // }
    }

    private void timKiemDichVu() {
        // String tuKhoa = tfTimKiem.getText().trim();
        // if (tuKhoa.isEmpty()) {
        // loadDuLieu();
        // } else {
        // List<DichVu> ketQua = dv_ctrl.timKiemDichVu(tuKhoa);
        // ObservableList<DichVu> danhSachTimKiem =
        // FXCollections.observableArrayList(ketQua);
        // bangDichVu.setItems(danhSachTimKiem);
        // }
    }

    private void lamMoi() {
        maDichVuDangChon = null;
        tfTenDichVu.clear();
        tfGia.clear();
        tfDonViTinh.clear();
        taMoTa.clear();
        tfTimKiem.clear();
        bangDichVu.getSelectionModel().clearSelection();
        tfTenDichVu.requestFocus();
    }

    private void hienThiThongBao(String noiDung, Alert.AlertType loai) {
        Alert alert = new Alert(loai);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}