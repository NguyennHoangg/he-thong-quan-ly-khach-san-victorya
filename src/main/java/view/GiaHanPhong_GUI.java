package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Giao diện gia hạn phòng - phiên bản đơn giản
 */
public class GiaHanPhong_GUI extends BorderPane {

    private TextField txtSoDienThoai;
    private TableView<RoomExtensionRow> tablePhongGiaHan;
    private VBox containerChonThoiGian;

    public GiaHanPhong_GUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f0f2f5;");
        getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());

        HBox mainContainer = new HBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);

        VBox leftPanel = taoVungTrai();
        VBox rightPanel = taoVungPhai();

        mainContainer.getChildren().addAll(leftPanel, rightPanel);
        setCenter(mainContainer);
    }

    private VBox taoVungTrai() {
        VBox container = new VBox(20);
        
        Label lblTieuDe = new Label("Chọn phòng gia hạn");
        lblTieuDe.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(15));
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        txtSoDienThoai = new TextField();
        txtSoDienThoai.setPromptText("083205000981");
        txtSoDienThoai.setPrefWidth(400);
        txtSoDienThoai.setPrefHeight(35);

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefHeight(35);
        btnTimKiem.getStyleClass().add("btn");

        searchBox.getChildren().addAll(txtSoDienThoai, btnTimKiem);
        
        VBox tableBox = taoBangPhong();

        container.getChildren().addAll(lblTieuDe, searchBox, tableBox);
        return container;
    }

    private VBox taoBangPhong() {
        tablePhongGiaHan = new TableView<>();
        tablePhongGiaHan.setPrefWidth(540);
        tablePhongGiaHan.setPrefHeight(380);
        tablePhongGiaHan.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        TableColumn<RoomExtensionRow, String> colSoPhong = new TableColumn<>("Số phòng");
        colSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        colSoPhong.setPrefWidth(90);

        TableColumn<RoomExtensionRow, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));
        colLoaiPhong.setPrefWidth(120);

        TableColumn<RoomExtensionRow, String> colTang = new TableColumn<>("Tầng");
        colTang.setCellValueFactory(new PropertyValueFactory<>("tang"));
        colTang.setPrefWidth(100);

        TableColumn<RoomExtensionRow, String> colGiaTheoNgay = new TableColumn<>("Giá theo ngày");
        colGiaTheoNgay.setCellValueFactory(new PropertyValueFactory<>("giaTheoNgay"));
        colGiaTheoNgay.setPrefWidth(120);

        TableColumn<RoomExtensionRow, String> colGiaTheoGio = new TableColumn<>("Giá theo giờ");
        colGiaTheoGio.setCellValueFactory(new PropertyValueFactory<>("giaTheoGio"));
        colGiaTheoGio.setPrefWidth(110);

        tablePhongGiaHan.getColumns().add(colSoPhong);
        tablePhongGiaHan.getColumns().add(colLoaiPhong);
        tablePhongGiaHan.getColumns().add(colTang);
        tablePhongGiaHan.getColumns().add(colGiaTheoNgay);
        tablePhongGiaHan.getColumns().add(colGiaTheoGio);
        loadSampleData();

        VBox container = new VBox(tablePhongGiaHan);
        return container;
    }

    private VBox taoVungPhai() {
        VBox container = new VBox(20);
        
        Label lblTieuDe = new Label("Chọn thời gian gia hạn");
        lblTieuDe.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        containerChonThoiGian = new VBox(0);
        containerChonThoiGian.setPadding(new Insets(15));
        containerChonThoiGian.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        containerChonThoiGian.setPrefWidth(560);
        containerChonThoiGian.setPrefHeight(380);

        HBox headerRow = taoHeaderChonThoiGian();
        containerChonThoiGian.getChildren().add(headerRow);
        themPhongMau();

        Button btnGiaHan = new Button("Gia hạn ngay");
        btnGiaHan.setPrefWidth(560);
        btnGiaHan.setPrefHeight(45);
        btnGiaHan.getStyleClass().add("btn");
        btnGiaHan.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 16px;");

        container.getChildren().addAll(lblTieuDe, containerChonThoiGian, btnGiaHan);
        return container;
    }

    private HBox taoHeaderChonThoiGian() {
        HBox header = new HBox(0);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        Label lblSoPhong = new Label("Số phòng");
        lblSoPhong.setPrefWidth(100);
        lblSoPhong.setStyle("-fx-font-weight: bold; -fx-padding: 0 10 0 0;");

        Label lblNgayTraPhong = new Label("Ngày trả phòng");
        lblNgayTraPhong.setPrefWidth(180);
        lblNgayTraPhong.setStyle("-fx-font-weight: bold; -fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblGiaHanDen = new Label("Gia hạn đến");
        lblGiaHanDen.setPrefWidth(180);
        lblGiaHanDen.setStyle("-fx-font-weight: bold; -fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblThoiGianHan = new Label("Thời gian hạn");
        lblThoiGianHan.setPrefWidth(100);
        lblThoiGianHan.setStyle("-fx-font-weight: bold; -fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        header.getChildren().addAll(lblSoPhong, lblNgayTraPhong, lblGiaHanDen, lblThoiGianHan);
        return header;
    }

    private HBox taoDongPhongGiaHan(String soPhong, String ngayTraPhong, String ngayGiaHan, String thoiGianHan) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-border-color: transparent transparent #ddd transparent; -fx-border-width: 0 0 1 0;");

        Label lblSoPhong = new Label(soPhong);
        lblSoPhong.setPrefWidth(100);
        lblSoPhong.setStyle("-fx-font-weight: bold; -fx-padding: 0 10 0 0;");

        Label lblNgayTra = new Label(ngayTraPhong);
        lblNgayTra.setPrefWidth(180);
        lblNgayTra.setStyle("-fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblThoiGian = new Label(thoiGianHan);
        lblThoiGian.setPrefWidth(100);
        lblThoiGian.setAlignment(Pos.CENTER);
        lblThoiGian.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981; -fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Button btnChonGiaHan = new Button(ngayGiaHan);
        btnChonGiaHan.setPrefWidth(165);
        btnChonGiaHan.setPrefHeight(32);
        btnChonGiaHan.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 5;");
        btnChonGiaHan.setOnAction(e -> hienThiChonGiaHan(btnChonGiaHan, lblThoiGian));
        
        VBox vboxGiaHan = new VBox(btnChonGiaHan);
        vboxGiaHan.setPrefWidth(180);
        vboxGiaHan.setAlignment(Pos.CENTER_LEFT);
        vboxGiaHan.setStyle("-fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        row.getChildren().addAll(lblSoPhong, lblNgayTra, vboxGiaHan, lblThoiGian);
        return row;
    }
    
    private void hienThiChonGiaHan(Button btnTarget, Label lblThoiGian) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Chọn thời gian gia hạn");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");
        root.setAlignment(Pos.CENTER);
        
        Label title = new Label("Chọn thời gian gia hạn");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ToggleGroup group = new ToggleGroup();
        RadioButton rbNgay = new RadioButton("Theo ngày");
        RadioButton rbGio = new RadioButton("Theo giờ");
        rbNgay.setToggleGroup(group);
        rbGio.setToggleGroup(group);
        rbNgay.setSelected(true);
        
        HBox loaiBox = new HBox(20, rbNgay, rbGio);
        loaiBox.setAlignment(Pos.CENTER);
        
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(250);
        
        ComboBox<String> cboGio = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            cboGio.getItems().add(String.format("%02d:00", i));
        }
        cboGio.setValue("14:00");
        cboGio.setVisible(false);
        
        rbNgay.setOnAction(e -> cboGio.setVisible(false));
        rbGio.setOnAction(e -> cboGio.setVisible(true));
        
        Button btnXacNhan = new Button("Xác nhận");
        btnXacNhan.getStyleClass().add("btn");
        btnXacNhan.setPrefWidth(120);
        btnXacNhan.setOnAction(e -> {
            // Cập nhật button và label (logic đơn giản)
            btnTarget.setText("01/10/2025");
            lblThoiGian.setText("1 ngày");
            dialog.close();
        });
        
        root.getChildren().addAll(title, loaiBox, datePicker, cboGio, btnXacNhan);
        dialog.setScene(new javafx.scene.Scene(root, 350, 280));
        dialog.showAndWait();
    }

    private void loadSampleData() {
        ObservableList<RoomExtensionRow> data = FXCollections.observableArrayList(
            new RoomExtensionRow("#001", "Double bed", "Floor - 1", "300.000VND", "5.000.000VND"),
            new RoomExtensionRow("#002", "Single bed", "Floor - 2", "400.000VND", "7.000.000VND"),
            new RoomExtensionRow("#003", "VIP", "Floor - 1", "700.000VND", "8.600.000VND"),
            new RoomExtensionRow("#005", "Single bed", "Floor - 1", "350.000VND", "5.000.000VND")
        );
        tablePhongGiaHan.setItems(data);
    }

    private void themPhongMau() {
        containerChonThoiGian.getChildren().addAll(
            taoDongPhongGiaHan("#002", "12:00 30/09/2025", "15:00 30/09/2025", "3 giờ"),
            taoDongPhongGiaHan("#005", "30/09/2025", "01/10/2025", "1 ngày")
        );
    }

    // Model cho dữ liệu bảng
    public static class RoomExtensionRow {
        private final String soPhong, loaiPhong, tang, giaTheoNgay, giaTheoGio;

        public RoomExtensionRow(String soPhong, String loaiPhong, String tang, String giaTheoNgay, String giaTheoGio) {
            this.soPhong = soPhong;
            this.loaiPhong = loaiPhong;
            this.tang = tang;
            this.giaTheoNgay = giaTheoNgay;
            this.giaTheoGio = giaTheoGio;
        }

        public String getSoPhong() { return soPhong; }
        public String getLoaiPhong() { return loaiPhong; }
        public String getTang() { return tang; }
        public String getGiaTheoNgay() { return giaTheoNgay; }
        public String getGiaTheoGio() { return giaTheoGio; }
    }
}
