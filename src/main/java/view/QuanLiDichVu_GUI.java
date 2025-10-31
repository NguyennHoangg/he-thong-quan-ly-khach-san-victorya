package view;

import controller.DichVu_Controller;
import model.DichVu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class QuanLiDichVu_GUI extends BorderPane {

    private TextField tfTenDichVu = new TextField();
    private TextField tfGia = new TextField();
    private TextField tfMoTa = new TextField();
    private Button btnLuu = new Button("Lưu");
    private Button btnXoa = new Button("Xóa");
    private Button btnMoi = new Button("Làm mới");
    private Button btnTimKiem = new Button("Tìm kiếm");

    private TextField tfTimKiem = new TextField();

    private TableView<DichVu> bangDichVu = new TableView<>();
    private DichVu_Controller dv_ctrl = new DichVu_Controller();

    private ComboBox<String> cmbDonViTinh = new ComboBox<>();
    private DichVu dichVuDaChon;

    public QuanLiDichVu_GUI() {
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
        tfTenDichVu.setPromptText("Nhập tên dịch vụ");
        tfGia.setPromptText("Nhập giá");
        tfTimKiem.setPromptText("Tìm theo tên hoặc mã dịch vụ");
        tfMoTa.setPromptText("Nhập mô tả dịch vụ");

        Label lblTenDichVu = new Label("Dịch vụ");
        Label lblGia = new Label("Giá");
        Label lblDonViTinh = new Label("Đơn vị tính");
        Label lblMoTa = new Label("Mô tả");

        ObservableList dsTenDichVu = FXCollections.observableArrayList(dv_ctrl.getDsDonViTinh());
        cmbDonViTinh.setItems(dsTenDichVu);
        cmbDonViTinh.setPromptText("Đơn vị tính");
        cmbDonViTinh.getStyleClass().addAll("cmb");

        double ngang = 400;
        double doc = 40;
        for (TextField tf : new TextField[] { tfTenDichVu, tfGia, tfTimKiem, tfMoTa }) {
            tf.setPrefWidth(ngang);
            tf.setPrefHeight(doc);
            tf.getStyleClass().add("text-field");

        }

        cmbDonViTinh.setPrefWidth(300);
        cmbDonViTinh.setPrefHeight(40);

        // Cấu hình nút
        btnLuu.getStyleClass().add("btn-luu");
        btnXoa.getStyleClass().add("btn-huy");
        btnMoi.getStyleClass().add("btn-lam-moi");
        btnTimKiem.getStyleClass().add("btn");

        btnLuu.setOnAction(e -> themDichVu());
        btnTimKiem.setOnAction(e -> timKiemDichVu());
        tfTimKiem.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                timKiemDichVu();
            }
        });
        btnXoa.setOnAction(e -> xoaDichVu());

        GridPane formGrid = new GridPane();
        formGrid.setHgap(16);
        formGrid.setVgap(10);
        formGrid.add(taoKhuVucLabel(lblTenDichVu, tfTenDichVu), 0, 0);
        formGrid.add(taoKhuVucLabel(lblGia, tfGia), 1, 0);
        formGrid.add(taoKhuVucLabel(lblDonViTinh, cmbDonViTinh), 0, 1);
        formGrid.add(taoKhuVucLabel(lblMoTa, tfMoTa), 1, 1);

        HBox khuVucNut = new HBox(10, btnLuu, btnXoa, btnMoi);
        btnMoi.setOnAction(e -> lamMoi());
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
                dichVuDaChon = moi;
                tfTenDichVu.setText(moi.getTenDichVu());
                tfGia.setText(String.valueOf(moi.getGia()));
                cmbDonViTinh.setValue(moi.getDonViTinh());
                tfMoTa.setText(moi.getMoTa());
            }
        });

        ScrollPane scrollPane = new ScrollPane(bangDichVu);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: white;");
        return scrollPane;
    }

    private void themDichVu() {
        String tenDichVu = tfTenDichVu.getText().trim();
        double gia;

        try {
            gia = Double.parseDouble(tfGia.getText().trim());
        } catch (NumberFormatException e) {
            hienThiThongBao("Giá phải là một số hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        String donViTinh = cmbDonViTinh.getSelectionModel().getSelectedItem();
        if (donViTinh == null || donViTinh.isEmpty()) {
            hienThiThongBao("Vui lòng chọn đơn vị tính!", Alert.AlertType.ERROR);
            return;
        }

        String moTa = tfMoTa.getText();

        StringBuilder tinNhan = new StringBuilder();
        boolean hopLe = dv_ctrl.kiemTraDauVao(tenDichVu, donViTinh, gia, tinNhan);

        if (!hopLe) {
            hienThiThongBao(tinNhan.toString(), AlertType.ERROR);
            return;
        }

        DichVu dvuMoi = new DichVu(tenDichVu, gia, moTa, donViTinh);
        if (dv_ctrl.themDichVu(dvuMoi, tinNhan)) {
            hienThiThongBao(tinNhan.toString(), AlertType.INFORMATION);
            lamMoi();
        } else {
            hienThiThongBao(tinNhan.toString(), AlertType.ERROR);
        }
    }

    private void xoaDichVu() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xóa dịch vụ");
        confirm.setContentText("Bạn có chắc chắn muốn xóa dịch vụ này?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (dv_ctrl.xoaDichVu(dichVuDaChon.getMaDichVu())) {
                hienThiThongBao("Xóa dịch vụ thành công!", Alert.AlertType.INFORMATION);
                lamMoi();
            } else {
                hienThiThongBao("Xóa dịch vụ thất bại!", Alert.AlertType.ERROR);
            }
        }
    }

    private void timKiemDichVu() {
        String tuKhoa = tfTimKiem.getText().trim();
        if (tuKhoa.isEmpty()) {
            hienThiThongBao("Vui lòng nhập tên dịch vụ cần tìm!", Alert.AlertType.ERROR);
            lamMoi();
            return;
        } else {
            DichVu dichVuCanTim = dv_ctrl.timDichVu(tuKhoa);
            if (dichVuCanTim == null) {
                hienThiThongBao("Không tìm thấy tên dịch vụ!", Alert.AlertType.ERROR);
                lamMoi();
                return;
            } else {
                // List<DichVu> ketQua = dv_ctrl.timKiemDichVu(tuKhoa);
                ObservableList<DichVu> danhSachTimKiem = FXCollections.observableArrayList(dichVuCanTim);
                bangDichVu.setItems(danhSachTimKiem);

            }
        }
    }

    private void lamMoi() {
        dichVuDaChon = null;
        tfTenDichVu.clear();
        tfGia.clear();
        tfMoTa.clear();
        tfTimKiem.clear();
        tfTenDichVu.requestFocus();

        cmbDonViTinh.getSelectionModel().clearSelection();

        bangDichVu.getSelectionModel().clearSelection();
        List<DichVu> dsDV = dv_ctrl.getDsDichVu();
        ObservableList<DichVu> danhSachMaster = FXCollections.observableArrayList(dsDV);
        bangDichVu.setItems(danhSachMaster);

    }

    private void hienThiThongBao(String noiDung, Alert.AlertType loai) {
        Alert alert = new Alert(loai);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}