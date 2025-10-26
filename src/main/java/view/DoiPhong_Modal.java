package view;

import java.util.List;
import java.util.Optional;

import controller.LoaiPhong_Controller;
import controller.Phong_Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.LoaiPhong;
import model.Phong;

public class DoiPhong_Modal {

    private Stage stage;
    private String maPhongDaChon;
    private ScrollPane bangPhong;
    private Phong_Controller p_ctrl = new Phong_Controller();
    private LoaiPhong_Controller lp_ctrl = new LoaiPhong_Controller();
    private Phong phongDaChon;

    public DoiPhong_Modal() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Phòng muốn đổi sang");
        // Container chính
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle("-fx-background-color: white;");
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());

        // Tiêu đề
        Label tieuDe = new Label("Phòng muốn đổi sang");
        tieuDe.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Vùng chứa các bộ lọc
        HBox vungBoLoc1 = taoBangBoLoc1();

        // Bảng danh sách phòng
        bangPhong = taoBangPhong();

        // Vùng chứa các nút
        HBox vungCacNut = new HBox(15);
        vungCacNut.setAlignment(Pos.CENTER);
        vungCacNut.setPrefWidth(600);

        // Nút Hủy
        Button nutHuy = new Button("Hủy");
        nutHuy.setPrefWidth(290);
        nutHuy.setPrefHeight(50);
        nutHuy.getStyleClass().add("btn-huy");
        nutHuy.setOnAction(e -> xuLyHuy());

        // Nút xác nhận
        Button nutXacNhan = new Button("Xác nhận");
        nutXacNhan.setPrefWidth(290);
        nutXacNhan.setPrefHeight(50);
        nutXacNhan.getStyleClass().add("btn");
        nutXacNhan.setOnAction(e -> xuLyXacNhan());

        vungCacNut.getChildren().addAll(nutHuy, nutXacNhan);

        // Thêm các thành phần vào container
        container.getChildren().addAll(tieuDe, vungBoLoc1, bangPhong, vungCacNut);

        Scene scene = new Scene(container, 750, 700);

        stage.setScene(scene);
    }

    // Tạo bảng danh sách phòng với TableView
    private ScrollPane taoBangPhong() {
        TableView<Phong> table = new TableView<>();
        TableColumn<Phong, String> colSoPhong = new TableColumn<>("Số phòng");
        colSoPhong.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getSoPhong()));

        TableColumn<Phong, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getLoaiPhong().getTenLoaiPhong()));

        TableColumn<Phong, String> colTang = new TableColumn<>("Tầng");
        colTang.setCellValueFactory(
                data -> new SimpleStringProperty(String.valueOf(data.getValue().getTang())));

        TableColumn<Phong, String> colGia = new TableColumn<>("Giá");
        colGia.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("%,.0f VND", data.getValue().getLoaiPhong().getGia())));

        // ======== Dữ liệu ========
        List<Phong> dsPhongDaDat = p_ctrl.getDsPhongTheoTrangThai("Trống");
        ObservableList<Phong> data = FXCollections.observableArrayList(dsPhongDaDat);
        table.setItems(data);

        table.getColumns().add(colSoPhong);
        table.getColumns().add(colLoaiPhong);
        table.getColumns().add(colTang);
        table.getColumns().add(colGia);

        table.setPrefWidth(700);
        table.setPrefHeight(200);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Tùy chỉnh style cho row
        table.setRowFactory(tv -> {
            TableRow<Phong> row = new TableRow<Phong>() {
                @Override
                protected void updateItem(Phong item, boolean empty) {
                    super.updateItem(item, empty);
                    capNhatStyleDong();
                }

                private void capNhatStyleDong() {
                    if (isEmpty()) {
                        setStyle("");
                    } else if (isSelected()) {
                        setStyle("-fx-background-color: #E8E9FF; -fx-text-fill: black;");
                    } else {
                        setStyle("-fx-background-color: white; -fx-text-fill: black;");
                    }
                }
            };

            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (!row.isEmpty()) {
                    if (isNowSelected) {
                        row.setStyle("-fx-background-color: #E8E9FF; -fx-text-fill: black;");
                    } else {
                        row.setStyle("-fx-background-color: white; -fx-text-fill: black;");
                    }
                }
            });

            return row;
        });

        // Xử lý sự kiện chọn dòng
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                maPhongDaChon = newSelection.getSoPhong();
            }
        });

        // Load CSS
        table.getStyleClass().addAll("table");

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: white;");

        return scrollPane;
    }

    // Tạo hàng bộ lọc thứ nhất
    private HBox taoBangBoLoc1() {
        HBox hang = new HBox(15);
        hang.setAlignment(Pos.CENTER);
        List<String> dsTenLoaiPhong = lp_ctrl.getDsTenLoaiPhong();
        List<String> dsTang = p_ctrl.getDsTang();

        ObservableList<String> dsLoaiPhong = FXCollections.observableArrayList(dsTenLoaiPhong);
        ObservableList<String> dsSoTang = FXCollections.observableArrayList(dsTang);

        ComboBox<String> cmbLoaiPhong = taoComboBox("Loại phòng");
        ComboBox<String> cmbTang = taoComboBox("Tầng");

        cmbLoaiPhong.setItems(dsLoaiPhong);
        cmbTang.setItems(dsSoTang);

        hang.getChildren().addAll(cmbLoaiPhong, cmbTang);
        return hang;
    }

    // Tạo ComboBox với style
    private ComboBox<String> taoComboBox(String chuThich) {
        ComboBox<String> cmb = new ComboBox<>();

        cmb.setPromptText(chuThich);
        cmb.setPrefWidth(150);
        cmb.setPrefHeight(40);
        cmb.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #CCCCCC; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-font-size: 14px;");
        return cmb;
    }

    // Xử lý khi nhấn Hủy
    private void xuLyHuy() {
        maPhongDaChon = null;
        phongDaChon = null;
        stage.close();
    }

    // Xử lý khi nhấn xác nhận
    private void xuLyXacNhan() {

        if (maPhongDaChon != null) {
            Alert canhBao = new Alert(Alert.AlertType.CONFIRMATION);
            canhBao.setTitle("Xác nhận");
            canhBao.setHeaderText(null);
            canhBao.setContentText("Bạn có chắc chắn đổi sang phòng " + maPhongDaChon + " không ?");

            Optional<ButtonType> xacNhan = canhBao.showAndWait();
            if (xacNhan.isPresent() && xacNhan.get().equals(ButtonType.YES)) {
                System.out.println("madachon: " + maPhongDaChon);
                chonPhong();
            }
            stage.close();
        } else {
            Alert canhBao = new Alert(Alert.AlertType.WARNING);
            canhBao.setTitle("Cảnh báo");
            canhBao.setHeaderText(null);
            canhBao.setContentText("Vui lòng chọn phòng trước khi xác nhận!");
            canhBao.showAndWait();
        }
    }

    // Hiển thị modal
    public void hienThi() {
        stage.showAndWait();
    }

    // Lấy phòng đã chọn
    public Phong chonPhong() {
        phongDaChon = p_ctrl.getPhongTheoSoPhong(maPhongDaChon);
        if (phongDaChon == null) {
            return null;
        }
        return phongDaChon;
    }

}