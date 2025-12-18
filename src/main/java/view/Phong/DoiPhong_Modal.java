package view.Phong;

import java.util.function.Function;

import controller.LoaiPhong_Controller;
import controller.Phong_Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Phong;

public class DoiPhong_Modal {

    private final Stage cuaSo = new Stage();
    private final Phong_Controller phongCtrl = new Phong_Controller();
    private final LoaiPhong_Controller loaiPhongCtrl = new LoaiPhong_Controller();

    private TableView<Phong> bangPhong;
    private ComboBox<String> cmbLoaiPhong;
    private ComboBox<Integer> cmbTang;

    private String maPhongDaChon;
    private Phong phongDaChon;

    public DoiPhong_Modal() {
        cuaSo.initModality(Modality.APPLICATION_MODAL);
        cuaSo.setTitle("Chọn phòng muốn đổi sang");

        VBox khungChinh = new VBox(15);
        khungChinh.setPadding(new Insets(20));
        khungChinh.setAlignment(Pos.CENTER);
        khungChinh.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        khungChinh.getStylesheets().add(getClass().getResource("/css/Control.css").toExternalForm());
        khungChinh.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());

        Label tieuDe = new Label("Phòng muốn đổi sang");
        tieuDe.setStyle("-fx-font-size: 23px; -fx-font-weight: bold;");

        HBox vungBoLoc = new HBox(10);
        vungBoLoc.setAlignment(Pos.CENTER);

        cmbLoaiPhong = new ComboBox<>(FXCollections.observableArrayList(loaiPhongCtrl.getDsTenLoaiPhong()));
        cmbLoaiPhong.getStyleClass().add("cmb");
        cmbLoaiPhong.setPromptText("Loại phòng");
        cmbLoaiPhong.setOnAction(e -> locPhong());

        cmbTang = new ComboBox<>(FXCollections.observableArrayList(phongCtrl.getDsTang()));
        cmbTang.getStyleClass().add("cmb");
        cmbTang.setPromptText("Tầng");
        cmbTang.setOnAction(e -> locPhong());

        vungBoLoc.getChildren().addAll(cmbLoaiPhong, cmbTang);

        // Bảng phòng
        bangPhong = new TableView<>();
        bangPhong.getStyleClass().add("table-view");
        bangPhong.setPlaceholder(new Label("Không có dữ liệu"));
        bangPhong.getColumns().addAll(
                taoCot("Số phòng", p -> p.getSoPhong()),
                taoCot("Loại phòng", p -> p.getLoaiPhong().getTenLoaiPhong()),
                taoCot("Tầng", p -> String.valueOf(p.getTang())),
                taoCot("Giá", p -> String.format("%,.0f VND", p.getLoaiPhong().getGia())));
        bangPhong.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bangPhong.setItems(FXCollections.observableArrayList(phongCtrl.getDsPhongTheoTrangThai("Trống")));

        bangPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                maPhongDaChon = newVal.getSoPhong();
        });

        // Các nút
        HBox vungNut = new HBox(10);
        vungNut.setAlignment(Pos.CENTER);

        Button btnHuy = new Button("Hủy");
        btnHuy.getStyleClass().add("btn-huy");
        btnHuy.setOnAction(e -> cuaSo.close());

        Button btnXacNhan = new Button("Xác nhận");
        btnXacNhan.getStyleClass().add("btn");
        btnXacNhan.setOnAction(e -> xuLyXacNhan());

        vungNut.getChildren().addAll(btnHuy, btnXacNhan);

        khungChinh.getChildren().addAll(tieuDe, vungBoLoc, bangPhong, vungNut);

        cuaSo.setScene(new Scene(khungChinh, 600, 500));
    }

    private TableColumn<Phong, String> taoCot(String tenCot, Function<Phong, String> hamLayDuLieu) {
        TableColumn<Phong, String> cot = new TableColumn<>(tenCot);
        cot.setCellValueFactory(data -> new SimpleStringProperty(hamLayDuLieu.apply(data.getValue())));
        return cot;
    }

    private void locPhong() {
        String loai = cmbLoaiPhong.getValue();
        Integer tang = cmbTang.getValue();

        if (loai == null)
            loai = "Tất cả";
        if (tang == null)
            tang = 0;

        bangPhong.setItems(FXCollections.observableArrayList(
                phongCtrl.locPhong("Trống", loai, tang)));
    }

    private void xuLyXacNhan() {
        if (maPhongDaChon == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn phòng trước khi xác nhận!").showAndWait();
            return;
        }
        phongDaChon = phongCtrl.getPhongTheoSoPhong(maPhongDaChon);
        cuaSo.close();
    }

    public void hienThi() {
        cuaSo.showAndWait();
    }

    public Phong getPhongDaChon() {
        return phongDaChon;
    }

    public void lamMoiModal() {
        bangPhong.setItems(FXCollections.observableArrayList(
                phongCtrl.getDsPhongTheoTrangThai("Trống")));
        bangPhong.refresh();
    }
}