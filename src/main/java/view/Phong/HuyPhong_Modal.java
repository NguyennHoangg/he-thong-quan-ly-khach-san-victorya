package view.Phong;

import java.util.ArrayList;
import java.util.List;

import controller.ChiTietPhieuDatPhong_Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ChiTietPhieuDatPhong;
import model.Phong;
import view.CaLamViec_GUI;

public class HuyPhong_Modal {
    private ChiTietPhieuDatPhong_Controller ctpdp_ctrl = new ChiTietPhieuDatPhong_Controller();
    private Stage stage;
    private List<ChiTietPhieuDatPhong> dsPhongHuy = new ArrayList<>();
    private Button btnHuy, btnXacNhan;

    private double tongTien = 0.0;
    private double tongCoc = 0.0;
    private double tongHoan = 0.0;
    private List<Phong> dsPhongCapNhat = new ArrayList<>();

    private String lyDo;
    private CaLamViec_GUI caLamViecGUI;

    public HuyPhong_Modal(List<ChiTietPhieuDatPhong> dsPhongHuy, String lyDo) {
        this(dsPhongHuy, lyDo, null);
    }

    public HuyPhong_Modal(List<ChiTietPhieuDatPhong> dsPhongHuy, String lyDo, CaLamViec_GUI caLamViecGUI) {
        this.caLamViecGUI = caLamViecGUI;
        this.dsPhongHuy = dsPhongHuy;
        this.lyDo = lyDo;
        for (ChiTietPhieuDatPhong ct : dsPhongHuy) {
            dsPhongCapNhat.add(ct.getPhong());
        }
        VBox khung = new VBox(20);
        khung.setPadding(new Insets(20));
        khung.setAlignment(Pos.TOP_CENTER);
        khung.setStyle("-fx-background-color: #ffffff;");

        Label lblTieuDe = new Label("Phòng muốn hủy");
        lblTieuDe.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
        lblTieuDe.setAlignment(Pos.CENTER);

        TableView<ChiTietPhieuDatPhong> bangHuyPhong = taoBang();
        bangHuyPhong.setPrefHeight(350);

        VBox noiDung = noiDung();

        HBox khungNut = khungNut();

        khung.getChildren().addAll(lblTieuDe, bangHuyPhong, noiDung, khungNut);

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Phòng muốn hủy");

        Scene scene = new Scene(khung, 750, 700);
        scene.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/Label.css").toExternalForm());
        stage.setScene(scene);
    }

    private VBox noiDung() {
        for (ChiTietPhieuDatPhong ct : dsPhongHuy) {
            double gia = ct.getPhong().getLoaiPhong().getGia();
            String tenLoaiDatPhong = ct.getLoaiDatPhong().getMaLoaiDatPhong();
            double thoiGianThue = ct.getSoGioLuuTru();
            double tienHoan = ctpdp_ctrl.tinhTienHoan(ct);
            tongHoan += tienHoan;
            double thanhTien = ctpdp_ctrl.tinhThanhTien(gia, tenLoaiDatPhong, thoiGianThue);
            double tienCoc = ctpdp_ctrl.tinhTienCoc(thanhTien);

            tongTien += thanhTien;
            tongCoc += tienCoc;
        }
        VBox khung = new VBox(10);
        khung.setAlignment(Pos.BOTTOM_RIGHT);
        khung.setPadding(new Insets(20, 40, 20, 40));
        khung.setStyle("-fx-background-color: #ffffff;");

        Label lblTongTien = new Label("Tổng tiền:");
        Label lblTongCoc = new Label("Tổng tiền cọc:");
        Label lblTongTienHoan = new Label("Tổng tiền hoàn:");

        Label lblTongTienGiaTri = new Label(String.format("%,.0f VND", tongTien));
        Label lblTongCocGiaTri = new Label(String.format("%,.0f VND", tongCoc));
        Label lblTongHoanGiaTri = new Label(String.format("%,.0f VND", tongHoan));

        lblTongTienGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTongCocGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTongHoanGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));

        Region r1 = new Region();
        Region r2 = new Region();
        Region r3 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);
        HBox.setHgrow(r2, Priority.ALWAYS);
        HBox.setHgrow(r3, Priority.ALWAYS);

        HBox tongTienBox = new HBox(lblTongTien, r1, lblTongTienGiaTri);
        HBox tongCocBox = new HBox(lblTongCoc, r2, lblTongCocGiaTri);
        HBox tongHoanBox = new HBox(lblTongTienHoan, r3, lblTongHoanGiaTri);

        tongTienBox.setAlignment(Pos.CENTER_RIGHT);
        tongCocBox.setAlignment(Pos.CENTER_RIGHT);
        tongHoanBox.setAlignment(Pos.CENTER_RIGHT);

        khung.getChildren().addAll(tongTienBox, tongCocBox, tongHoanBox);
        return khung;
    }

    private HBox khungNut() {
        HBox khung = new HBox(20);
        khung.setAlignment(Pos.CENTER);
        khung.setPadding(new Insets(20, 0, 10, 0));

        btnHuy = new Button("Hủy");
        btnHuy.getStyleClass().add("btn-huy");
        btnHuy.setPrefWidth(290);
        btnHuy.setPrefHeight(50);
        btnHuy.setOnAction(e -> stage.close());

        btnXacNhan = new Button("Xác nhận");
        btnXacNhan.getStyleClass().add("btn");
        btnXacNhan.setPrefWidth(290);
        btnXacNhan.setPrefHeight(50);
        btnXacNhan.setOnAction(e -> {
            if (ctpdp_ctrl.themHuyPhong(dsPhongHuy, lyDo)) {
                // Cập nhật tổng chi trong ca làm việc
                if (tongHoan > 0 && caLamViecGUI != null && caLamViecGUI.hasOpenShift()) {
                    caLamViecGUI.capNhatTongChi(tongHoan);
                }
                
                thongBao("Thông báo", "Hủy phòng thành công", AlertType.INFORMATION);
                stage.close();
            } else {
                thongBao("Thông báo", "Thất bại", AlertType.ERROR);
            }

        });

        khung.getChildren().addAll(btnHuy, btnXacNhan);
        return khung;
    }

    private TableView<ChiTietPhieuDatPhong> taoBang() {
        TableView<ChiTietPhieuDatPhong> tableHuyPhong = new TableView<>();
        tableHuyPhong.getStyleClass().add("table");
        tableHuyPhong.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ChiTietPhieuDatPhong, String> colSoPhong = new TableColumn<>("Số phòng");
        colSoPhong.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhong().getSoPhong()));

        TableColumn<ChiTietPhieuDatPhong, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getPhong().getLoaiPhong().getTenLoaiPhong()));

        TableColumn<ChiTietPhieuDatPhong, String> colTongTien = new TableColumn<>("Tổng tiền");
        colTongTien.setCellValueFactory(data -> {
            double gia = data.getValue().getPhong().getLoaiPhong().getGia();
            String tenLoaiDatPhong = data.getValue().getLoaiDatPhong().getMaLoaiDatPhong();
            double thoiGianThue = data.getValue().getSoGioLuuTru();

            double thanhTien = ctpdp_ctrl.tinhThanhTien(gia, tenLoaiDatPhong, thoiGianThue);
            return new SimpleStringProperty(String.format("%,.0f VND", thanhTien));
        });

        TableColumn<ChiTietPhieuDatPhong, String> colTienCoc = new TableColumn<>("Tiền cọc");
        colTienCoc.setCellValueFactory(data -> {
            double gia = data.getValue().getPhong().getLoaiPhong().getGia();
            String tenLoaiDatPhong = data.getValue().getLoaiDatPhong().getMaLoaiDatPhong();
            double thoiGianThue = data.getValue().getSoGioLuuTru();

            double thanhTien = ctpdp_ctrl.tinhThanhTien(gia, tenLoaiDatPhong, thoiGianThue);
            double tienCoc = ctpdp_ctrl.tinhTienCoc(thanhTien);
            return new SimpleStringProperty(String.format("%,.0f VND", tienCoc));
        });

        TableColumn<ChiTietPhieuDatPhong, String> colTienHoanTra = new TableColumn<>("Tiền hoàn trả");
        colTienHoanTra.setCellValueFactory(data -> {
            double tienHoan = ctpdp_ctrl.tinhTienHoan(data.getValue());
            return new SimpleStringProperty(String.format("%,.0f VND", tienHoan));
        });

        tableHuyPhong.getColumns().add(colSoPhong);
        tableHuyPhong.getColumns().add(colLoaiPhong);
        tableHuyPhong.getColumns().add(colTongTien);
        tableHuyPhong.getColumns().add(colTienCoc);
        tableHuyPhong.getColumns().add(colTienHoanTra);
        tableHuyPhong.setItems(FXCollections.observableArrayList(dsPhongHuy));

        return tableHuyPhong;
    }

    public void hienThi() {
        stage.showAndWait();
    }

    private void thongBao(String tieuDe, String noiDung, AlertType alertType) {
        Alert thongBao = new Alert(alertType);
        thongBao.setTitle(tieuDe);
        thongBao.setHeaderText(null);
        thongBao.setContentText(noiDung);
        thongBao.showAndWait();
    }
}