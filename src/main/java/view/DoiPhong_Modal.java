package view;

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

public class DoiPhong_Modal {

    private Stage stage;
    private String phongDaChon = null;
    private ScrollPane bangPhong;

    public DoiPhong_Modal() {
        khoiTaoGiaoDien();
    }

    // Khởi tạo giao diện modal
    private void khoiTaoGiaoDien() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Phòng muộn đổi sang");

        // Container chính
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle("-fx-background-color: white;");
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());

        // Tiêu đề
        Label tieuDe = new Label("Phòng muốn đổi sang");
        tieuDe.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Vùng chứa các bộ lọc
        HBox vungBoLoc1 = taoBangBoLoc1();
        HBox vungBoLoc2 = taoBangBoLoc2();

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
        container.getChildren().addAll(tieuDe, vungBoLoc1, vungBoLoc2, bangPhong, vungCacNut);

        Scene scene = new Scene(container, 750, 700);

        stage.setScene(scene);
    }

    // Tạo hàng bộ lọc thứ nhất
    private HBox taoBangBoLoc1() {
        HBox hang = new HBox(15);
        hang.setAlignment(Pos.CENTER);

        ComboBox<String> cbLoaiPhong = taoComboBox("Loại phòng");
        ComboBox<String> cbCheckIn = taoComboBox("Check-in");
        ComboBox<String> cbCheckOut = taoComboBox("Check-out");
        ComboBox<String> cbSoNguoi = taoComboBox("Số người");

        hang.getChildren().addAll(cbLoaiPhong, cbCheckIn, cbCheckOut, cbSoNguoi);
        return hang;
    }

    // Tạo hàng bộ lọc thứ hai
    private HBox taoBangBoLoc2() {
        HBox hang = new HBox(15);
        hang.setAlignment(Pos.CENTER);

        ComboBox<String> cbGiaTheoGio = taoComboBox("Giá theo giờ");
        ComboBox<String> cbGiaTheoNgay = taoComboBox("Giá theo ngày");
        ComboBox<String> cbTang = taoComboBox("Tầng");

        hang.getChildren().addAll(cbGiaTheoGio, cbGiaTheoNgay, cbTang);
        return hang;
    }

    // Tạo ComboBox với style
    private ComboBox<String> taoComboBox(String chuThich) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setPromptText(chuThich);
        cb.setPrefWidth(150);
        cb.setPrefHeight(40);
        cb.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #CCCCCC; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-font-size: 14px;");
        return cb;
    }

    // Tạo bảng danh sách phòng với TableView
    private ScrollPane taoBangPhong() {
        // Class lưu trữ dữ liệu phòng
        class PhongData {
            private String soPhong;
            private String loaiPhong;
            private String tang;
            private String giaTheoNgay;
            private String giaTheoTuan;

            public PhongData(String soPhong, String loaiPhong, String tang, String giaTheoNgay, String giaTheoTuan) {
                this.soPhong = soPhong;
                this.loaiPhong = loaiPhong;
                this.tang = tang;
                this.giaTheoNgay = giaTheoNgay;
                this.giaTheoTuan = giaTheoTuan;
            }

            public String getSoPhong() {
                return soPhong;
            }

            public String getLoaiPhong() {
                return loaiPhong;
            }

            public String getTang() {
                return tang;
            }

            public String getGiaTheoNgay() {
                return giaTheoNgay;
            }

            public String getGiaTheoTuan() {
                return giaTheoTuan;
            }
        }

        // Khởi tạo dữ liệu mẫu
        ObservableList<PhongData> data = FXCollections.observableArrayList(
                new PhongData("#001", "Thường", "Tầng - 1", "300.000VNĐ", "5.000.000VNĐ"),
                new PhongData("#003", "VIP", "Tầng -1", "700.000VNĐ", "8000.000VNĐ"),
                new PhongData("#005", "Vip", "Tầng -1", "350.000VNĐ", "5.000.000VNĐ"));

        TableView<PhongData> table = new TableView<>();
        table.setItems(data);

        // Tạo các cột
        TableColumn<PhongData, String> colSoPhong = new TableColumn<>("Số phòng");
        colSoPhong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSoPhong()));
        colSoPhong.setPrefWidth(120);

        TableColumn<PhongData, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLoaiPhong()));
        colLoaiPhong.setPrefWidth(120);

        TableColumn<PhongData, String> colTang = new TableColumn<>("Tầng");
        colTang.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTang()));
        colTang.setPrefWidth(120);

        TableColumn<PhongData, String> colGiaTheoNgay = new TableColumn<>("Giá theo ngày");
        colGiaTheoNgay.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGiaTheoNgay()));
        colGiaTheoNgay.setPrefWidth(150);

        TableColumn<PhongData, String> colGiaTheoTuan = new TableColumn<>("Giá theo ngày");
        colGiaTheoTuan.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGiaTheoTuan()));
        colGiaTheoTuan.setPrefWidth(150);

        table.getColumns().add(colSoPhong);
        table.getColumns().add(colLoaiPhong);
        table.getColumns().add(colTang);
        table.getColumns().add(colGiaTheoNgay);
        table.getColumns().add(colGiaTheoTuan);

        table.setPrefWidth(700);
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Tùy chỉnh style cho row
        table.setRowFactory(tv -> {
            TableRow<PhongData> row = new TableRow<PhongData>() {
                @Override
                protected void updateItem(PhongData item, boolean empty) {
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
                phongDaChon = newSelection.getSoPhong();
                System.out.println("Đã chọn phòng: " + phongDaChon);
            }
        });

        // Chọn dòng thứ 3 mặc định (phòng #005)
        table.getSelectionModel().select(2);

        // Load CSS nếu có
        try {
            String cssPath = "/css/Table.css";
            java.net.URL css = getClass().getResource(cssPath);
            if (css != null) {
                table.getStylesheets().add(css.toExternalForm());
            }
        } catch (Exception ex) {
            System.out.println("Không thể load CSS: " + ex.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: white;");

        return scrollPane;
    }

    // Xử lý khi nhấn Hủy
    private void xuLyHuy() {
        System.out.println("Đã hủy thao tác đổi phòng");
        phongDaChon = null;
        stage.close();
    }

    // Xử lý khi nhấn xác nhận
    private void xuLyXacNhan() {
        if (phongDaChon != null) {
            System.out.println("Xác nhận đổi sang phòng: " + phongDaChon);
            stage.close();
        } else {
            // Hiển thị thông báo chưa chọn phòng
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
    public String layPhongDaChon() {
        return phongDaChon;
    }
}