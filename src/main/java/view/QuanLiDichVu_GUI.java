package view;

import controller.DichVu_Controller;
import model.DichVu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.text.NumberFormat;
import java.util.Locale;

public class QuanLiDichVu_GUI extends BorderPane {
    private Button btnLuu = new Button("Thêm dịch vụ mới");
    private Button btnTimKiem = new Button("Tìm kiếm");

    private TextField tfTimKiem = new TextField();

    private TableView<DichVu> bangDichVu = new TableView<>();
    private DichVu_Controller dv_ctrl = new DichVu_Controller();
    private final ObservableList<DichVu> danhSachDichVuMaster = FXCollections.observableArrayList();

    public QuanLiDichVu_GUI() {
        setPadding(new Insets(20));

        HBox thanhCongCu = taoKhuVucTimKiem();

        VBox container = new VBox(16, xayDungKhuVucTieuDe(), thanhCongCu, taoBang());
        VBox.setVgrow(bangDichVu, Priority.ALWAYS); // Cho phép ScrollPane chiếm toàn
        // bộ chiều cao còn lại
        setCenter(container);
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
    }

    private VBox xayDungKhuVucTieuDe() {
        Label tieuDe = new Label("Danh sách dịch vụ");
        tieuDe.setStyle("-fx-font-size:26px; -fx-font-weight:800; -fx-text-fill:#111827;");
        VBox box = new VBox(tieuDe);
        box.setPadding(new Insets(4, 0, 8, 0));
        return box;
    }

    private HBox taoKhuVucTimKiem() {
        tfTimKiem.setPromptText("Tìm theo tên dịch vụ");
        tfTimKiem.getStyleClass().addAll("text-field");
        tfTimKiem.setPrefHeight(35);
        tfTimKiem.setPrefWidth(400);
        tfTimKiem.setPromptText("Nhập tên dịch vụ");
        btnTimKiem.getStyleClass().add("btn");
        btnLuu.getStyleClass().add("btn-luu");
        tfTimKiem.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                timKiemDichVu();
            }
        });

        btnTimKiem.setOnAction(e -> timKiemDichVu());
        btnLuu.setOnAction(e -> hienThiModal(null));

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

        thanhCongCu.getChildren().addAll(nhomTimKiem, khoangTrong, btnLuu);

        return thanhCongCu;
    }

    private TableView taoBang() {
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
            // chuyển tiền tệ
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
        danhSachDichVuMaster.addAll(dv_ctrl.getDsDichVu());
        bangDichVu.setItems(danhSachDichVuMaster);

        // Sự kiện chọn dòng
        bangDichVu.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null) {
                QuanLiDichVu_Modal dichVu_Modal = new QuanLiDichVu_Modal(moi);
                dichVu_Modal.hienThi();
                lamMoi();
            }
        });

        return bangDichVu;
    }

    private void hienThiModal(DichVu dvu) {
        QuanLiDichVu_Modal dichVu_Modal = new QuanLiDichVu_Modal(dvu);
        dichVu_Modal.hienThi();
        lamMoi();

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
                ObservableList<DichVu> danhSachTimKiem = FXCollections.observableArrayList(dichVuCanTim);
                bangDichVu.setItems(danhSachTimKiem);

            }
        }
    }

    private void lamMoi() {
        tfTimKiem.clear();
        danhSachDichVuMaster.setAll(dv_ctrl.getDsDichVu());
        bangDichVu.setItems(danhSachDichVuMaster);
        bangDichVu.getSelectionModel().clearSelection();

    }

    private void hienThiThongBao(String noiDung, Alert.AlertType loai) {
        Alert alert = new Alert(loai);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}