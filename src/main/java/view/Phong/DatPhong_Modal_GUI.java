package view.Phong;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Spinner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import model.Phong;
import model.DichVu;
import model.ChiTietPhieuDatPhong;
import model.KhachHang;
import model.PhieuDatPhong;
import model.LoaiDatPhong;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

import controller.DichVu_Controller;
import controller.KhachHang_Controller;
import controller.PhieuDatPhong_Controller;

public class DatPhong_Modal_GUI extends BorderPane{
    
    private TableView<PhongDatModel> roomTable;
    private ObservableList<PhongDatModel> roomData = FXCollections.observableArrayList();
    
    private List<ChiTietPhieuDatPhong> chiTietPhieuDatPhongList;
    
    // Callback để reload data ở trang gốc sau khi đặt phòng thành công
    private Runnable onSuccessCallback;
    
    // Lưu các TextField để autofill
    private TextField cccdField;
    private TextField hoTenField;
    private TextField sdtField;
    private TextField emailField;
    
    /**
     * Constructor nhận danh sách chi tiết phiếu đặt phòng từ trang DatPhong
     * @param chiTietPhieuDatPhongList Danh sách chi tiết phiếu đặt phòng
     * @param onSuccessCallback Callback để gọi sau khi đặt phòng thành công
     */
    public DatPhong_Modal_GUI(List<ChiTietPhieuDatPhong> chiTietPhieuDatPhongList, Runnable onSuccessCallback){
        this.chiTietPhieuDatPhongList = chiTietPhieuDatPhongList;
        this.onSuccessCallback = onSuccessCallback;
        init();
    }

    private void init() {
        VBox mainContainer = mainContainer();
        this.setCenter(mainContainer);
        
        // Load chi tiết phiếu đặt phòng vào bảng
        loadChiTietPhieuDatPhong();
        
        // Cập nhật tổng kết
        javafx.application.Platform.runLater(() -> updateSummary());
    }
    
    /**
     * Load danh sách chi tiết phiếu đặt phòng vào bảng
     */
    private void loadChiTietPhieuDatPhong() {
        if (chiTietPhieuDatPhongList != null && !chiTietPhieuDatPhongList.isEmpty()) {
            for (ChiTietPhieuDatPhong chiTiet : chiTietPhieuDatPhongList) {
                addChiTietPhieuDatPhong(chiTiet);
            }
        }
    }

    private VBox mainContainer(){
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #F5F7FA;");
        
        // Header
        Label mainTitle = new Label("THÔNG TIN ĐẶT PHÒNG");
        mainTitle.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 24));
        mainTitle.setStyle("-fx-text-fill: #2C3E50;");
        
        // Thông tin khách hàng
        VBox userInfo = createUserInfo();
        
        // Bảng thông tin phòng
        VBox roomInfo = createRoomInfoTable();
        
        // Button actions
        HBox actionButtons = createActionButtons();
        
        main.getChildren().addAll(mainTitle, userInfo, roomInfo, actionButtons);
        
        return main;
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button cancelBtn = new Button("Hủy");
        cancelBtn.setPrefSize(120, 40);
        cancelBtn.setStyle(
            "-fx-background-color: #95a5a6;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 6;"
        );
        
        cancelBtn.setOnMouseEntered(e -> 
            cancelBtn.setStyle(
                "-fx-background-color: #7f8c8d;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 6;"
            )
        );
        
        cancelBtn.setOnMouseExited(e -> 
            cancelBtn.setStyle(
                "-fx-background-color: #95a5a6;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 6;"
            )
        );
        
        // Xử lý khi click Hủy
        cancelBtn.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) this.getScene().getWindow();
            stage.close();
        });
        
        Button confirmBtn = new Button("Xác nhận đặt phòng");
        confirmBtn.setPrefSize(180, 40);
        confirmBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 6;"
        );
        
        confirmBtn.setOnMouseEntered(e -> 
            confirmBtn.setStyle(
                "-fx-background-color: #2980b9;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 6;"
            )
        );
        
        confirmBtn.setOnMouseExited(e -> 
            confirmBtn.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 6;"
            )
        );
        
        // Xử lý khi click Xác nhận đặt phòng
        confirmBtn.setOnAction(e -> xacNhanDatPhong());
        
        buttonBox.getChildren().addAll(cancelBtn, confirmBtn);
        
        return buttonBox;
    }
    
    private VBox createRoomInfoTable() {
        VBox container = new VBox(15);
        container.setStyle(
            "-fx-padding: 25;" +
            "-fx-background-color: white;" +
            "-fx-border-color: #D5DBDB;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Header
        Label headerLabel = new Label("📋 DANH SÁCH PHÒNG ĐÃ CHỌN");
        headerLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        headerLabel.setStyle("-fx-text-fill: #34495E;");
        
        // Tạo TableView
        roomTable = new TableView<>();
        roomTable.setPrefHeight(280);
        roomTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        roomTable.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #E8EEF2;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 13px;"
        );
        
        // Cột Số phòng
        TableColumn<PhongDatModel, String> soPhongCol = new TableColumn<>("Số Phòng");
        soPhongCol.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        soPhongCol.setPrefWidth(90);
        soPhongCol.setStyle("-fx-alignment: CENTER;");
        
        // Cột Loại phòng
        TableColumn<PhongDatModel, String> loaiPhongCol = new TableColumn<>("Loại Phòng");
        loaiPhongCol.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));
        loaiPhongCol.setPrefWidth(100);
        loaiPhongCol.setStyle("-fx-alignment: CENTER;");
        
        // Cột Check-in
        TableColumn<PhongDatModel, String> checkInCol = new TableColumn<>("Check-in");
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkInCol.setPrefWidth(130);
        checkInCol.setStyle("-fx-alignment: CENTER;");
        
        // Cột Check-out
        TableColumn<PhongDatModel, String> checkOutCol = new TableColumn<>("Check-out");
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        checkOutCol.setPrefWidth(130);
        checkOutCol.setStyle("-fx-alignment: CENTER;");
        
        // Cột Số giờ lưu trú
        TableColumn<PhongDatModel, String> soGioCol = new TableColumn<>("Số giờ");
        soGioCol.setCellValueFactory(new PropertyValueFactory<>("soGioLuuTru"));
        soGioCol.setPrefWidth(70);
        soGioCol.setStyle("-fx-alignment: CENTER;");
        
        // Cột Dịch vụ
        TableColumn<PhongDatModel, String> dichVuCol = new TableColumn<>("Dịch vụ");
        dichVuCol.setCellValueFactory(new PropertyValueFactory<>("dichVu"));
        dichVuCol.setPrefWidth(200);
        dichVuCol.setCellFactory(col -> new TableCell<PhongDatModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(item);
                    label.setWrapText(true);
                    label.setMaxWidth(190);
                    setGraphic(label);
                }
            }
        });
        
        // Cột Tổng tiền
        TableColumn<PhongDatModel, String> tongTienCol = new TableColumn<>("Tổng tiền");
        tongTienCol.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        tongTienCol.setPrefWidth(120);
        tongTienCol.setCellFactory(col -> new TableCell<PhongDatModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                }
            }
        });
        
        // Cột Chọn dịch vụ (Button)
        TableColumn<PhongDatModel, Void> actionCol = new TableColumn<>("Chọn DV");
        actionCol.setPrefWidth(90);
        actionCol.setCellFactory(col -> new TableCell<PhongDatModel, Void>() {
            private final Button chonDichVuBtn = new Button("Chọn DV");
            
            {
                chonDichVuBtn.setStyle(
                    "-fx-background-color: #4CAF50;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 11px;" +
                    "-fx-padding: 5 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-border-radius: 4;" +
                    "-fx-background-radius: 4;"
                );
                
                chonDichVuBtn.setOnMouseEntered(e -> 
                    chonDichVuBtn.setStyle(
                        "-fx-background-color: #45a049;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 5 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;"
                    )
                );
                
                chonDichVuBtn.setOnMouseExited(e -> 
                    chonDichVuBtn.setStyle(
                        "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 5 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;"
                    )
                );
                
                chonDichVuBtn.setOnAction(event -> {
                    PhongDatModel phongDat = getTableView().getItems().get(getIndex());
                    openChonDichVuDialog(phongDat);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(chonDichVuBtn);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        
        roomTable.getColumns().addAll(soPhongCol, loaiPhongCol, checkInCol, checkOutCol, soGioCol, dichVuCol, tongTienCol, actionCol);
        roomTable.setItems(roomData);
        
        // Tổng kết tiền
        HBox summaryBox = createSummaryBox();
        
        container.getChildren().addAll(headerLabel, roomTable, summaryBox);
        
        return container;
    }
    
    /**
     * Tạo box tổng kết tiền
     */
    private HBox createSummaryBox() {
        HBox summaryBox = new HBox(30);
        summaryBox.setAlignment(Pos.CENTER_RIGHT);
        summaryBox.setPadding(new Insets(15, 0, 0, 0));
        summaryBox.setStyle(
            "-fx-background-color: #F8F9FA;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 6;"
        );
        
        VBox summaryContent = new VBox(8);
        summaryContent.setAlignment(Pos.CENTER_RIGHT);
        
        // Tổng tiền
        HBox tongTienBox = new HBox(10);
        tongTienBox.setAlignment(Pos.CENTER_RIGHT);
        Label tongTienLabel = new Label("Tổng tiền:");
        tongTienLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 15));
        tongTienLabel.setStyle("-fx-text-fill: #2C3E50;");
        
        Label tongTienValue = new Label("0 VNĐ");
        tongTienValue.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 15));
        tongTienValue.setStyle("-fx-text-fill: #E74C3C;");
        tongTienValue.setId("tongTienValue");
        
        tongTienBox.getChildren().addAll(tongTienLabel, tongTienValue);
        
        // Tiền cọc (30%)
        HBox tienCocBox = new HBox(10);
        tienCocBox.setAlignment(Pos.CENTER_RIGHT);
        Label tienCocLabel = new Label("Tiền cọc (30%):");
        tienCocLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.SEMI_BOLD, 14));
        tienCocLabel.setStyle("-fx-text-fill: #34495E;");
        
        Label tienCocValue = new Label("0 VNĐ");
        tienCocValue.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
        tienCocValue.setStyle("-fx-text-fill: #27AE60;");
        tienCocValue.setId("tienCocValue");
        
        tienCocBox.getChildren().addAll(tienCocLabel, tienCocValue);
        
        summaryContent.getChildren().addAll(tongTienBox, tienCocBox);
        summaryBox.getChildren().add(summaryContent);
        
        // Listener để tự động cập nhật khi có thay đổi trong roomData
        roomData.addListener((javafx.collections.ListChangeListener.Change<? extends PhongDatModel> c) -> {
            updateSummary();
        });
        
        return summaryBox;
    }
    
    /**
     * Cập nhật tổng kết tiền
     */
    private void updateSummary() {
        double tongTien = 0;
        
        for (PhongDatModel model : roomData) {
            // Parse tổng tiền từ string (loại bỏ " VNĐ" và dấu phân cách)
            String tongTienStr = model.getTongTien().replace(" VNĐ", "").replace(",", "");
            try {
                tongTien += Double.parseDouble(tongTienStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        double tienCoc = tongTien * 0.3;
        
        // Tìm labels và cập nhật
        Label tongTienValue = (Label) this.lookup("#tongTienValue");
        Label tienCocValue = (Label) this.lookup("#tienCocValue");
        
        if (tongTienValue != null) {
            tongTienValue.setText(String.format("%,.0f VNĐ", tongTien));
        }
        
        if (tienCocValue != null) {
            tienCocValue.setText(String.format("%,.0f VNĐ", tienCoc));
        }
    }
    
    private void openChonDichVuDialog(PhongDatModel phongDat) {
        try {
            // Tạo Stage mới cho dialog
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Chọn dịch vụ - Phòng " + phongDat.getSoPhong());
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            // Tạo BorderPane cho dialog
            ChonDichVu_Dialog dialogContent = new ChonDichVu_Dialog(phongDat);
            
            // Tạo Scene
            javafx.scene.Scene scene = new javafx.scene.Scene(dialogContent, 800, 600);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            
            // Hiển thị dialog
            dialogStage.showAndWait();
            
            // Cập nhật tổng kết sau khi dialog đóng
            updateSummary();
            roomTable.refresh(); // Refresh bảng để hiển thị thay đổi
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Thêm chi tiết phiếu đặt phòng vào bảng
     */
    private void addChiTietPhieuDatPhong(ChiTietPhieuDatPhong chiTiet) {
        Phong phong = chiTiet.getPhong();
        
        // Format thời gian
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String checkIn = chiTiet.getThoiGianNhanPhong().format(formatter);
        String checkOut = chiTiet.getThoiGianTraPhong().format(formatter);
        
        // Tính số giờ lưu trú
        String soGio = calculateSoGioLuuTruFromHours(chiTiet.getSoGioLuuTru());
        
        // Lấy loại phòng
        String loaiPhong = phong.getLoaiPhong().getTenLoaiPhong();
        
        // Tính tổng tiền phòng
        double tongTienPhong = chiTiet.tinhThanhTien();
        String tongTienStr = String.format("%,.0f VNĐ", tongTienPhong);
        
        // Lấy danh sách dịch vụ
        String dichVuStr = "";
        List<DichVu> dsachDichVu = chiTiet.getDsachDichVu();
        
        // Kiểm tra nếu là phòng VIP
        boolean isVIP = loaiPhong != null && loaiPhong.equalsIgnoreCase("VIP");
        
        if (dsachDichVu != null && !dsachDichVu.isEmpty()) {
            if (isVIP) {
                // Phòng VIP: chỉ hiển thị "Tất cả dịch vụ"
                dichVuStr = "Tất cả dịch vụ";
            } else {
                // Phòng thường: liệt kê từng dịch vụ
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < dsachDichVu.size(); i++) {
                    sb.append(dsachDichVu.get(i).getTenDichVu());
                    if (i < dsachDichVu.size() - 1) sb.append(", ");
                }
                dichVuStr = sb.toString();
            }
        } else {
            dichVuStr = "Chưa chọn";
        }
        
        PhongDatModel model = new PhongDatModel(
            phong.getSoPhong(),
            loaiPhong,
            checkIn,
            checkOut,
            soGio,
            dichVuStr,
            tongTienStr,
            phong,
            chiTiet
        );
        
        roomData.add(model);
    }
    
    private String calculateSoGioLuuTruFromHours(int hours) {
        if (hours < 24) {
            return hours + " giờ";
        } else {
            int days = hours / 24;
            int remainingHours = hours % 24;
            if (remainingHours > 0) {
                return days + " ngày " + remainingHours + "h";
            } else {
                return days + " ngày";
            }
        }
    }
    
   
    
    private VBox createUserInfo(){
        VBox userInfoContainer = new VBox(20);
        userInfoContainer.setStyle(
            "-fx-padding: 25;" +
            "-fx-background-color: white;" +
            "-fx-border-color: #D5DBDB;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Header
        Label headerLabel = new Label("👤 THÔNG TIN KHÁCH HÀNG");
        headerLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        headerLabel.setStyle("-fx-text-fill: #34495E;");
        
        // Main grid - 2 cột
        HBox mainRow = new HBox(40);
        mainRow.setAlignment(Pos.TOP_LEFT);
        
        // Cột trái
        VBox leftColumn = new VBox(20);
        leftColumn.setPrefWidth(550);
        
        // CCCD field
        VBox cccdBox = createFormField("CCCD:", "Nhập số căn cước công dân", true);
        cccdField = (TextField) cccdBox.getChildren().get(1);
        setupCCCDAutocomplete();
        
        // Họ tên field
        VBox hoTenBox = createFormField("Họ và tên:", "Nhập họ và tên đầy đủ", true);
        hoTenField = (TextField) hoTenBox.getChildren().get(1);
        
        // Số điện thoại field
        VBox sdtBox = createFormField("Số điện thoại:", "Nhập số điện thoại", true);
        sdtField = (TextField) sdtBox.getChildren().get(1);
        
        leftColumn.getChildren().addAll(cccdBox, hoTenBox, sdtBox);
        
        // Cột phải
        VBox rightColumn = new VBox(20);
        rightColumn.setPrefWidth(550);
        
        // Email field
        VBox emailBox = createFormField("Email:", "Nhập địa chỉ email", false);
        emailField = (TextField) emailBox.getChildren().get(1);
        
        // Địa chỉ field
        VBox diaChiBox = createFormField("Địa chỉ:", "Nhập địa chỉ chi tiết", false);
        TextField diaChiField = (TextField) diaChiBox.getChildren().get(1);
        
        // Ghi chú field
        VBox ghiChuBox = createFormField("Ghi chú:", "Nhập ghi chú (nếu có)", false);
        TextField ghiChuField = (TextField) ghiChuBox.getChildren().get(1);
        
        rightColumn.getChildren().addAll(emailBox, diaChiBox, ghiChuBox);
        
        mainRow.getChildren().addAll(leftColumn, rightColumn);
        
        userInfoContainer.getChildren().addAll(headerLabel, mainRow);
        
        return userInfoContainer;
    }
    
    /**
     * Tạo form field với label và text field
     */
    private VBox createFormField(String labelText, String promptText, boolean required) {
        VBox fieldBox = new VBox(8);
        
        // Label container
        HBox labelContainer = new HBox(5);
        labelContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.SEMI_BOLD, 14));
        label.setStyle("-fx-text-fill: #2C3E50;");
        
        labelContainer.getChildren().add(label);
        
        if (required) {
            Label requiredLabel = new Label("*");
            requiredLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 14px; -fx-font-weight: bold;");
            labelContainer.getChildren().add(requiredLabel);
        }
        
        // Text field
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefHeight(40);
        textField.setStyle(
            "-fx-background-color: #F8F9FA;" +
            "-fx-border-color: #D5DBDB;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: 13px;"
        );
        
        // Focus effect
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #3498db;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 5;" +
                    "-fx-background-radius: 5;" +
                    "-fx-padding: 8 12;" +
                    "-fx-font-size: 13px;"
                );
            } else {
                textField.setStyle(
                    "-fx-background-color: #F8F9FA;" +
                    "-fx-border-color: #D5DBDB;" +
                    "-fx-border-radius: 5;" +
                    "-fx-background-radius: 5;" +
                    "-fx-padding: 8 12;" +
                    "-fx-font-size: 13px;"
                );
            }
        });
        
        fieldBox.getChildren().addAll(labelContainer, textField);
        
        return fieldBox;
    }
    
    /**
     * Setup autocomplete cho CCCD field
     */
    private void setupCCCDAutocomplete() {
        javafx.scene.control.ContextMenu suggestionMenu = new javafx.scene.control.ContextMenu();
        
        cccdField.textProperty().addListener((obs, oldVal, newVal) -> {
            suggestionMenu.hide();
            
            // Bắt đầu autocomplete khi nhập ít nhất 1 ký tự để gợi ý sớm
            if (newVal != null && !newVal.trim().isEmpty()) {
                // Tìm kiếm khách hàng có CCCD bắt đầu bằng chuỗi nhập vào
                List<KhachHang> khachHangList = timKiemKhachHangTheoCCCD(newVal.trim());
                
                if (!khachHangList.isEmpty()) {
                    suggestionMenu.getItems().clear();
                    
                    // Giới hạn số lượng gợi ý hiển thị (tối đa 10)
                    int maxSuggestions = Math.min(khachHangList.size(), 10);
                    
                    for (int i = 0; i < maxSuggestions; i++) {
                        KhachHang kh = khachHangList.get(i);
                        // Hiển thị CCCD - Họ tên - SĐT để dễ nhận biết
                        String displayText = kh.getCCCD() + " - " + kh.getTenKhachHang() + " - " + kh.getSoDienThoai();
                        javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem(displayText);
                        
                        // Style cho menu item
                        item.setStyle("-fx-font-size: 13px; -fx-padding: 8 12;");
                        
                        item.setOnAction(e -> {
                            // Autofill thông tin khách hàng
                            cccdField.setText(kh.getCCCD());
                            hoTenField.setText(kh.getTenKhachHang());
                            sdtField.setText(kh.getSoDienThoai());
                            emailField.setText(kh.getEmail() != null ? kh.getEmail() : "");
                            suggestionMenu.hide();
                        });
                        
                        suggestionMenu.getItems().add(item);
                    }
                    
                    // Hiển thị suggestion menu bên dưới TextField
                    if (!suggestionMenu.isShowing()) {
                        suggestionMenu.show(cccdField, javafx.geometry.Side.BOTTOM, 0, 0);
                    }
                }
            }
        });
        
        // Ẩn menu khi focus ra ngoài
        cccdField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                suggestionMenu.hide();
            }
        });
    }
    
    
    
    private List<KhachHang> timKiemKhachHangTheoCCCD(String cccdPrefix) {
        // Sử dụng phương thức startsWith để tìm khách hàng có CCCD bắt đầu bằng chuỗi nhập vào
        List<KhachHang> dsachKH = KhachHang_Controller.timKhachHangTheoCCCDStartsWith(cccdPrefix);
        return dsachKH;
    }
    
    /**
     * Xử lý xác nhận đặt phòng
     */
    private void xacNhanDatPhong() {
        // 1. Validate thông tin khách hàng
        String cccd = cccdField.getText().trim();
        String hoTen = hoTenField.getText().trim();
        String sdt = sdtField.getText().trim();
        String email = emailField.getText().trim();
        
        if (cccd.isEmpty() || hoTen.isEmpty() || sdt.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin bắt buộc (CCCD, Họ tên, SĐT)!");
            return;
        }
        
        // 2. Validate có phòng được chọn không
        if (roomData.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn ít nhất một phòng!");
            return;
        }
        
        try {
            // 3. Kiểm tra hoặc tạo khách hàng qua Controller
            KhachHang_Controller khController = new KhachHang_Controller();
            KhachHang khachHang = KhachHang_Controller.timKhachHangTheoCCCD(cccd);
            
            if (khachHang == null) {
                // Tạo khách hàng mới - sử dụng Controller để tạo mã
                KhachHang khachHangMoi = new KhachHang();
                khachHangMoi.setCCCD(cccd);
                khachHangMoi.setTenKhachHang(hoTen);
                khachHangMoi.setSoDienThoai(sdt);
                khachHangMoi.setEmail(email);
                khachHangMoi.setNgayTao(java.time.LocalDate.now());
                
                StringBuilder loiNhan = new StringBuilder();
                boolean themKHThanhCong = khController.themKhachHang(khachHangMoi, loiNhan);
                if (!themKHThanhCong) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm khách hàng mới!\n" + loiNhan.toString());
                    return;
                }
                
                // Lấy lại khách hàng vừa tạo để có mã
                khachHang = KhachHang_Controller.timKhachHangTheoCCCD(cccd);
            }
            
            // 4. Tạo phiếu đặt phòng
            String maPhieu = PhieuDatPhong_Controller.generateMaPhieuDatPhong(java.time.LocalDate.now());
            
            // Cập nhật PhieuDatPhong cho các ChiTietPhieuDatPhong
            PhieuDatPhong phieuDatPhong = new PhieuDatPhong();
            phieuDatPhong.setNgayTao(java.time.LocalDate.now());
            phieuDatPhong.setKhachHang(khachHang);
            phieuDatPhong.setTrangThai("Đã đặt");
            
            // Lấy danh sách chi tiết từ các PhongDatModel
            List<ChiTietPhieuDatPhong> dsChiTiet = new ArrayList<>();
            for (PhongDatModel model : roomData) {
                ChiTietPhieuDatPhong chiTiet = model.getChiTietPhieuDatPhong();
                
                // Tạo PhieuDatPhong với mã mới
                PhieuDatPhong pdp = new PhieuDatPhong(maPhieu);
                chiTiet.setPhieuDatPhong(pdp);
                
                // Set LoaiDatPhong mặc định (LDP01: Online, LDP02: Offline)
                if (chiTiet.getLoaiDatPhong() == null) {
                    chiTiet.setLoaiDatPhong(new LoaiDatPhong("LDP01")); // Mặc định là Online
                }
                
                dsChiTiet.add(chiTiet);
            }
            
            // Tính tiền cọc (30% tổng tiền)
            long tongTien = tinhTongTien();
            long tienCoc = (long) (tongTien * 0.3);
            
            PhieuDatPhong phieuFinal = new PhieuDatPhong(maPhieu, khachHang, java.time.LocalDate.now(), dsChiTiet, "Đã đặt", tienCoc);
            
            // 5. Lưu vào database qua Controller
            boolean success = PhieuDatPhong_Controller.themPhieuDatPhong(phieuFinal);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                    "Đặt phòng thành công!\n" +
                    "Mã phiếu: " + maPhieu + "\n" +
                    "Khách hàng: " + hoTen + "\n" +
                    "Tổng tiền: " + String.format("%,d VNĐ", tongTien).replace(",", ".") + "\n" +
                    "Tiền cọc: " + String.format("%,d VNĐ", tienCoc).replace(",", "."));
                
                // Reset dữ liệu form
                resetForm();
                
                // Gọi callback để reload data ở trang gốc
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                
                // Đóng cửa sổ
                javafx.stage.Stage stage = (javafx.stage.Stage) this.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo phiếu đặt phòng. Vui lòng thử lại!");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi: " + ex.getMessage());
        }
    }
    
    /**
     * Tính tổng tiền từ tất cả các phòng đã chọn
     */
    private long tinhTongTien() {
        double tongTien = 0;
        for (PhongDatModel model : roomData) {
            tongTien += model.getChiTietPhieuDatPhong().tinhThanhTien();
        }
        return (long) tongTien;
    }

    /**
     * Reset form sau khi đặt phòng thành công
     */
    private void resetForm() {
        // Clear dữ liệu phòng
        roomData.clear();
        
        // Clear thông tin khách hàng
        cccdField.clear();
        hoTenField.clear();
        sdtField.clear();
        emailField.clear();
        
        // Cập nhật lại tổng tiền
        Label tongTienValue = (Label) this.lookup("#tongTienValue");
        Label tienCocValue = (Label) this.lookup("#tienCocValue");
        if (tongTienValue != null) {
            tongTienValue.setText("0 VNĐ");
        }
        if (tienCocValue != null) {
            tienCocValue.setText("0 VNĐ");
        }
    }

    /**
     * Hiển thị alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



    /**
     * Wrapper class để lưu dịch vụ kèm số lượng
     */
    public static class DichVuWithQuantity {
        private DichVu dichVu;
        private javafx.beans.property.IntegerProperty soLuong = new javafx.beans.property.SimpleIntegerProperty(1);
        private javafx.beans.property.BooleanProperty selected = new javafx.beans.property.SimpleBooleanProperty(false);
        
        public DichVuWithQuantity(DichVu dichVu) {
            this.dichVu = dichVu;
        }
        
        public DichVu getDichVu() { return dichVu; }
        public int getSoLuong() { return soLuong.get(); }
        public void setSoLuong(int value) { soLuong.set(value); }
        public javafx.beans.property.IntegerProperty soLuongProperty() { return soLuong; }
        
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean value) { selected.set(value); }
        public javafx.beans.property.BooleanProperty selectedProperty() { return selected; }
        
        public String getMaDichVu() { return dichVu.getMaDichVu(); }
        public String getTenDichVu() { return dichVu.getTenDichVu(); }
        public double getGia() { return dichVu.getGia(); }
        public double getThanhTien() { return dichVu.getGia() * soLuong.get(); }
    }

    /**
     * Inner class BorderPane dialog để chọn dịch vụ
     */
    public static class ChonDichVu_Dialog extends BorderPane {
        private PhongDatModel phongDatModel;
        private ObservableList<DichVuWithQuantity> availableDichVu = FXCollections.observableArrayList();
        
        public ChonDichVu_Dialog(PhongDatModel phongDat) {
            this.phongDatModel = phongDat;
            
            // Load dịch vụ đã chọn trước đó (nếu có)
            List<DichVu> selectedDichVuList = phongDat.getSelectedDichVu();
            
            // Load tất cả dịch vụ từ database
            loadAllDichVuFromDatabase(selectedDichVuList);
            
            init();
        }
        
        /**
         * Load tất cả dịch vụ từ database và đánh dấu những dịch vụ đã chọn
         */
        private void loadAllDichVuFromDatabase(List<DichVu> selectedList) {
            try {
                // Lấy tất cả dịch vụ từ controller
                DichVu_Controller dichVuController = new DichVu_Controller();
                List<DichVu> dsachDichVu = dichVuController.getDsDichVu();
                
                for (DichVu dv : dsachDichVu) {
                    DichVuWithQuantity dvwq = new DichVuWithQuantity(dv);
                    
                    // Nếu dịch vụ này đã được chọn trước đó, đánh dấu selected = true
                    if (selectedList != null && selectedList.stream().anyMatch(s -> s.getMaDichVu().equals(dv.getMaDichVu()))) {
                        dvwq.setSelected(true);
                    }
                    
                    availableDichVu.add(dvwq);
                }    
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Không thể tải danh sách dịch vụ: " + e.getMessage());
                alert.showAndWait();
            }
        }

        private void init() {
            this.setStyle("-fx-background-color: #F5F7FA;");
            
            VBox mainContainer = new VBox(20);
            mainContainer.setPadding(new Insets(30));
            
            // Header
            Label headerLabel = new Label("CHỌN DỊCH VỤ");
            headerLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 20));
            headerLabel.setStyle("-fx-text-fill: #2C3E50;");
            
            // Danh sách dịch vụ với checkbox
            VBox dichVuContainer = createDichVuList();
            
            // Action buttons
            HBox actionButtons = createActionButtons();
            
            mainContainer.getChildren().addAll(headerLabel, dichVuContainer, actionButtons);
            this.setCenter(mainContainer);
        }
        
        private VBox createDichVuList() {
            VBox container = new VBox(15);
            container.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 20;" +
                "-fx-background-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
            );
            
            Label titleLabel = new Label("Danh sách dịch vụ:");
            titleLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
            titleLabel.setStyle("-fx-text-fill: #34495E;");
            
            // Tạo TableView
            TableView<DichVuWithQuantity> tableView = new TableView<>();
            tableView.setEditable(true);
            tableView.setPrefHeight(350);
            tableView.setStyle("-fx-background-color: white;");
            tableView.setItems(availableDichVu);
            
            // Cột Checkbox
            TableColumn<DichVuWithQuantity, Boolean> checkCol = new TableColumn<>("Chọn");
            checkCol.setPrefWidth(70);
            checkCol.setCellFactory(column -> new javafx.scene.control.cell.CheckBoxTableCell<>());
            checkCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
            
            // Cột Mã dịch vụ
            TableColumn<DichVuWithQuantity, String> maDvCol = new TableColumn<>("Mã DV");
            maDvCol.setPrefWidth(100);
            maDvCol.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));
            
            // Cột Tên dịch vụ
            TableColumn<DichVuWithQuantity, String> tenDvCol = new TableColumn<>("Tên dịch vụ");
            tenDvCol.setPrefWidth(250);
            tenDvCol.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
            
            // Cột Số lượng
            TableColumn<DichVuWithQuantity, Integer> soLuongCol = new TableColumn<>("SL");
            soLuongCol.setPrefWidth(80);
            soLuongCol.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty().asObject());
            soLuongCol.setCellFactory(column -> new TableCell<DichVuWithQuantity, Integer>() {
                private Spinner<Integer> spinner;
                
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        DichVuWithQuantity dvwq = getTableRow().getItem();
                        
                        if (spinner == null) {
                            spinner = new Spinner<>(1, 99, dvwq.getSoLuong());
                            spinner.setEditable(true);
                            spinner.setPrefWidth(70);
                            spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                                if (newVal != null) {
                                    dvwq.setSoLuong(newVal);
                                }
                            });
                        } else {
                            spinner.getValueFactory().setValue(dvwq.getSoLuong());
                        }
                        
                        setGraphic(spinner);
                    }
                }
            });
            
            // Cột Giá
            TableColumn<DichVuWithQuantity, Double> giaCol = new TableColumn<>("Đơn giá");
            giaCol.setPrefWidth(120);
            giaCol.setCellValueFactory(new PropertyValueFactory<>("gia"));
            giaCol.setCellFactory(column -> new TableCell<DichVuWithQuantity, Double>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText(String.format("%,.0f", price));
                    }
                }
            });
            
            // Cột Thành tiền
            TableColumn<DichVuWithQuantity, Double> thanhTienCol = new TableColumn<>("Thành tiền");
            thanhTienCol.setPrefWidth(130);
            thanhTienCol.setCellValueFactory(cellData -> {
                DichVuWithQuantity dvwq = cellData.getValue();
                javafx.beans.property.DoubleProperty thanhTien = new javafx.beans.property.SimpleDoubleProperty(dvwq.getThanhTien());
                // Listen to soLuong changes
                dvwq.soLuongProperty().addListener((obs, oldVal, newVal) -> {
                    thanhTien.set(dvwq.getThanhTien());
                });
                return thanhTien.asObject();
            });
            thanhTienCol.setCellFactory(column -> new TableCell<DichVuWithQuantity, Double>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText(String.format("%,.0f VNĐ", price));
                    }
                }
            });
            
            // Thêm các cột vào table
            tableView.getColumns().addAll(checkCol, maDvCol, tenDvCol, soLuongCol, giaCol, thanhTienCol);
            
            // Placeholder khi không có dữ liệu
            Label placeholder = new Label("Không có dịch vụ nào");
            placeholder.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            tableView.setPlaceholder(placeholder);
            
            container.getChildren().addAll(titleLabel, tableView);
            
            return container;
        }

        private HBox createActionButtons() {
            HBox buttonBox = new HBox(15);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            
            Button cancelBtn = new Button("Hủy");
            cancelBtn.setPrefSize(100, 35);
            cancelBtn.setStyle(
                "-fx-background-color: #95a5a6;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 5;"
            );
            
            cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(
                "-fx-background-color: #7f8c8d;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 5;"
            ));
            
            cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(
                "-fx-background-color: #95a5a6;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 5;"
            ));
            
            cancelBtn.setOnAction(e -> {
                javafx.stage.Stage stage = (javafx.stage.Stage) this.getScene().getWindow();
                stage.close();
            });
            
            Button confirmBtn = new Button("Xác nhận");
            confirmBtn.setPrefSize(120, 35);
            confirmBtn.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 5;"
            );
            
            confirmBtn.setOnMouseEntered(e -> confirmBtn.setStyle(
                "-fx-background-color: #2980b9;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 5;"
            ));
            
            confirmBtn.setOnMouseExited(e -> confirmBtn.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 5;"
            ));
            
            confirmBtn.setOnAction(e -> {
                // Lọc các dịch vụ đã được chọn
                List<DichVu> selectedDichVuList = availableDichVu.stream()
                    .filter(DichVuWithQuantity::isSelected)
                    .map(DichVuWithQuantity::getDichVu)
                    .collect(java.util.stream.Collectors.toList());
                
                // Tạo Map để lưu số lượng
                java.util.Map<String, Integer> quantityMap = new java.util.HashMap<>();
                availableDichVu.stream()
                    .filter(DichVuWithQuantity::isSelected)
                    .forEach(dvwq -> quantityMap.put(dvwq.getMaDichVu(), dvwq.getSoLuong()));
                
                // Cập nhật dịch vụ vào PhongDatModel
                phongDatModel.setSelectedDichVu(selectedDichVuList);
                
                // Cập nhật hiển thị dịch vụ với số lượng (vd: "Bia*2, Nước*1")
                String dichVuDisplay = availableDichVu.stream()
                    .filter(DichVuWithQuantity::isSelected)
                    .map(dvwq -> {
                        if (dvwq.getSoLuong() > 1) {
                            return dvwq.getTenDichVu() + "*" + dvwq.getSoLuong();
                        } else {
                            return dvwq.getTenDichVu();
                        }
                    })
                    .collect(java.util.stream.Collectors.joining(", "));
                
                phongDatModel.setDichVu(dichVuDisplay.isEmpty() ? "Không có" : dichVuDisplay);
                
                // Tính lại tổng tiền: giá phòng + tổng (giá dịch vụ * số lượng)
                ChiTietPhieuDatPhong ctpdp = phongDatModel.getChiTietPhieuDatPhong();
                
                // Tính giá phòng (không bao gồm dịch vụ)
                double giaPhong = ctpdp.getSoGioLuuTru() * ctpdp.getPhong().getLoaiPhong().getGia();
                
                // Tính tổng tiền dịch vụ với số lượng
                double tongTienDichVu = availableDichVu.stream()
                    .filter(DichVuWithQuantity::isSelected)
                    .mapToDouble(DichVuWithQuantity::getThanhTien)
                    .sum();
                
                double tongTienMoi = giaPhong + tongTienDichVu;
                phongDatModel.setTongTien(String.format("%,.0f VNĐ", tongTienMoi));
                
                // Đóng dialog
                javafx.stage.Stage stage = (javafx.stage.Stage) this.getScene().getWindow();
                stage.close();
            });
            
            buttonBox.getChildren().addAll(cancelBtn, confirmBtn);
            
            return buttonBox;
        }
    }
    
    // Inner class để lưu thông tin phòng đặt
    public static class PhongDatModel {
        private String soPhong;
        private String loaiPhong;
        private String checkIn;
        private String checkOut;
        private String soGioLuuTru;
        private String dichVu;
        private String tongTien;
        private Phong phong;
        private List<DichVu> selectedDichVu;
        private ChiTietPhieuDatPhong chiTietPhieuDatPhong;
        
        public PhongDatModel(String soPhong, String loaiPhong, String checkIn, String checkOut, 
                           String soGioLuuTru, String dichVu, String tongTien, Phong phong, 
                           ChiTietPhieuDatPhong chiTietPhieuDatPhong) {
            this.soPhong = soPhong;
            this.loaiPhong = loaiPhong;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.soGioLuuTru = soGioLuuTru;
            this.dichVu = dichVu;
            this.tongTien = tongTien;
            this.phong = phong;
            this.chiTietPhieuDatPhong = chiTietPhieuDatPhong;
            this.selectedDichVu = chiTietPhieuDatPhong.getDsachDichVu() != null 
                ? new java.util.ArrayList<>(chiTietPhieuDatPhong.getDsachDichVu()) 
                : new java.util.ArrayList<>();
        }
        
        // Getters
        public String getSoPhong() { return soPhong; }
        public String getLoaiPhong() { return loaiPhong; }
        public String getCheckIn() { return checkIn; }
        public String getCheckOut() { return checkOut; }
        public String getSoGioLuuTru() { return soGioLuuTru; }
        public String getDichVu() { return dichVu; }
        public String getTongTien() { return tongTien; }
        public Phong getPhong() { return phong; }
        public List<DichVu> getSelectedDichVu() { return selectedDichVu; }
        public ChiTietPhieuDatPhong getChiTietPhieuDatPhong() { return chiTietPhieuDatPhong; }
        
        // Setters
        public void setSoPhong(String soPhong) { this.soPhong = soPhong; }
        public void setLoaiPhong(String loaiPhong) { this.loaiPhong = loaiPhong; }
        public void setCheckIn(String checkIn) { this.checkIn = checkIn; }
        public void setCheckOut(String checkOut) { this.checkOut = checkOut; }
        public void setSoGioLuuTru(String soGioLuuTru) { this.soGioLuuTru = soGioLuuTru; }
        public void setDichVu(String dichVu) { this.dichVu = dichVu; }
        public void setTongTien(String tongTien) { this.tongTien = tongTien; }
        public void setPhong(Phong phong) { this.phong = phong; }
        public void setSelectedDichVu(List<DichVu> selectedDichVu) { 
            this.selectedDichVu = selectedDichVu;
            
            // Cập nhật vào ChiTietPhieuDatPhong
            if (chiTietPhieuDatPhong != null) {
                chiTietPhieuDatPhong.setDsachDichVu(selectedDichVu);
                
                // Cập nhật tổng tiền
                double tongTienMoi = chiTietPhieuDatPhong.tinhThanhTien();
                this.tongTien = String.format("%,.0f VNĐ", tongTienMoi);
            }
            
            // Cập nhật chuỗi hiển thị dịch vụ
            if (selectedDichVu != null && !selectedDichVu.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < selectedDichVu.size(); i++) {
                    sb.append(selectedDichVu.get(i).getTenDichVu());
                    if (i < selectedDichVu.size() - 1) sb.append(", ");
                }
                this.dichVu = sb.toString();
            } else {
                this.dichVu = "Chưa chọn";
            }
        }
    }
    
    /**
     * Class wrapper để lưu thông tin phòng kèm thời gian check-in/out
     */
    public static class PhongWithDateTime {
        private Phong phong;
        private String checkIn;
        private String checkOut;
        
        public PhongWithDateTime(Phong phong, String checkIn, String checkOut) {
            this.phong = phong;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }
        
        public Phong getPhong() { return phong; }
        public String getCheckIn() { return checkIn; }
        public String getCheckOut() { return checkOut; }
        
        public void setPhong(Phong phong) { this.phong = phong; }
        public void setCheckIn(String checkIn) { this.checkIn = checkIn; }
        public void setCheckOut(String checkOut) { this.checkOut = checkOut; }
    }


}
