package view;

import controller.NhanVien_Controller;
import dao.CaLamViecNhanVien_DAO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.CaLamViecNhanVien;
import model.NhanVien;
import utils.CaLamViecManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CaLamViec_GUI extends BorderPane {
    
    private TabPane tabPane;
    private Tab tabBangDieuKhien;
    private Tab tabLichSu;
    
    // Dashboard components
    private VBox dashboardContainer;
    private VBox currentShiftPanel;
    private VBox noShiftPanel;
    
    private Label lblNhanVien;
    private Label lblGioBatDau;
    private Label lblTienDauCa;
    private Label lblTongThu;
    private Label lblTongChi;
    private Label lblTienDuKien;
    private Label lblLoiNhuanRong;
    private VBox transactionListContainer;
    
    private Button btnMoCa;
    private Button btnKetCa;

    
    // History components
    private VBox historyContainer;
    
    // Data
    private CaLamViecNhanVien currentShift;
    private NhanVien currentEmployee;
    private CaLamViecNhanVien_DAO caLamViecDAO;
    private NhanVien_Controller nhanVienController;
    
    private DecimalFormat currencyFormat = new DecimalFormat("#,### đ");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd 'tháng' MM, yyyy");
    
    public CaLamViec_GUI() {
        initializeData();
        initializeUI();
        setupEventHandlers();
        // Load data after UI is fully initialized
        loadCurrentShift();
    }
    
    private void initializeData() {
        caLamViecDAO = new CaLamViecNhanVien_DAO();
        nhanVienController = new NhanVien_Controller();
        
        // Get current employee from session
        String currentUsername = CaLamViecManager.getInstance().getCurrentUser();
        if (currentUsername != null) {
            currentEmployee = nhanVienController.timNhanVienTheoTenDangNhap(currentUsername);
        }
    }
    
    private void initializeUI() {
        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #f5f5f5;");
        
        // Header
        HBox header = createHeader();
        
        // Tab pane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Tab 1: Bảng điều khiển
        tabBangDieuKhien = new Tab("Bảng Điều Khiển");
        dashboardContainer = new VBox(20);
        dashboardContainer.setPadding(new Insets(20));
        ScrollPane scrollPane1 = new ScrollPane(dashboardContainer);
        scrollPane1.setFitToWidth(true);
        scrollPane1.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        tabBangDieuKhien.setContent(scrollPane1);
        
        // Tab 2: Lịch sử ca làm
        tabLichSu = new Tab("Lịch Sử Ca Làm");
        historyContainer = new VBox(15);
        historyContainer.setPadding(new Insets(20));
        ScrollPane scrollPane2 = new ScrollPane(historyContainer);
        scrollPane2.setFitToWidth(true);
        scrollPane2.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        tabLichSu.setContent(scrollPane2);
        
        tabPane.getTabs().addAll(tabBangDieuKhien, tabLichSu);
        
        mainContainer.getChildren().addAll(header, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        this.setCenter(mainContainer);
        
        // Load tab content
        loadDashboard();
        loadHistory();
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));
        
        Label title = new Label("Quản Lý Ca Làm Việc");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#333333"));
        
        Label dateLabel = new Label("Ngày " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateLabel.setFont(Font.font("System", 16));
        dateLabel.setTextFill(Color.web("#666666"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(title, spacer, dateLabel);
        return header;
    }
    
    private void loadDashboard() {
        dashboardContainer.getChildren().clear();
        
        if (currentShift != null && "Đang mở".equals(currentShift.getTrangThai())) {
            showCurrentShiftPanel();
        } else {
            showNoShiftPanel();
        }
    }
    
    private void showNoShiftPanel() {
        noShiftPanel = new VBox(30);
        noShiftPanel.setAlignment(Pos.CENTER);
        noShiftPanel.setPadding(new Insets(100));
        noShiftPanel.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        
        
        Label messageLabel = new Label("Chưa Mở Ca");
        messageLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        messageLabel.setTextFill(Color.web("#333333"));
        
        Label subMessageLabel = new Label("Vui lòng mở ca để bắt đầu làm việc");
        subMessageLabel.setFont(Font.font("System", 16));
        subMessageLabel.setTextFill(Color.web("#999999"));
        
        btnMoCa = new Button("Mở Ca");
        btnMoCa.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 50; " +
                "-fx-background-radius: 8; -fx-cursor: hand;");
        btnMoCa.setOnMouseEntered(e -> btnMoCa.setStyle(btnMoCa.getStyle() + "-fx-background-color: #1976D2;"));
        btnMoCa.setOnMouseExited(e -> btnMoCa.setStyle(btnMoCa.getStyle().replace("-fx-background-color: #1976D2;", "-fx-background-color: #2196F3;")));
        
        noShiftPanel.getChildren().addAll(messageLabel, subMessageLabel, btnMoCa);
        dashboardContainer.getChildren().add(noShiftPanel);
    }
    
    private void showCurrentShiftPanel() {
        currentShiftPanel = new VBox(20);
        currentShiftPanel.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30;");
        
        // Header with shift info
        HBox shiftHeader = createShiftHeader();
        
        // Statistics cards
        HBox statsCards = createStatsCards();
        
        // Profit section
        HBox profitSection = createProfitSection();
        
        
        // Transactions section
        VBox transactionsSection = createTransactionsSection();
        
        currentShiftPanel.getChildren().addAll(
                shiftHeader,
                statsCards,
                profitSection,
                transactionsSection
        );
        
        dashboardContainer.getChildren().add(currentShiftPanel);
    }
    
    private HBox createShiftHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));
        header.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 2 0;");
        
        VBox infoBox = new VBox(8);
        
        Label titleLabel = new Label("Ca Làm Việc Hiện Tại");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#333333"));
        
        lblNhanVien = new Label("Nhân viên: " + (currentEmployee != null ? currentEmployee.getTenNhanVien() : "N/A"));
        lblNhanVien.setFont(Font.font("System", 14));
        lblNhanVien.setTextFill(Color.web("#666666"));
        
        infoBox.getChildren().addAll(titleLabel, lblNhanVien);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        VBox timeBox = new VBox(5);
        timeBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label timeLabel = new Label("Giờ bắt đầu");
        timeLabel.setFont(Font.font("System", 12));
        timeLabel.setTextFill(Color.web("#999999"));
        
        lblGioBatDau = new Label(currentShift != null ? timeFormat.format(currentShift.getNgay()) : "08:00");
        lblGioBatDau.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblGioBatDau.setTextFill(Color.web("#2196F3"));
        
        timeBox.getChildren().addAll(timeLabel, lblGioBatDau);
        
        btnKetCa = new Button("Kết Ca");
        btnKetCa.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 25; " +
                "-fx-background-radius: 8; -fx-cursor: hand;");
        
        header.getChildren().addAll(infoBox, spacer, timeBox, btnKetCa);
        return header;
    }
    
    private HBox createStatsCards() {
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER);
        
        // Card 1: Tiền đầu ca
        VBox card1 = createStatCard("Tiền Đầu Ca", "0", "#2196F3");
        lblTienDauCa = (Label) ((VBox) card1.getChildren().get(1)).getChildren().get(0);
        
        // Card 2: Tổng thu
        VBox card2 = createStatCard("Tổng Thu", "0", "#4CAF50");
        lblTongThu = (Label) ((VBox) card2.getChildren().get(1)).getChildren().get(0);
        
        // Card 3: Tổng chi
        VBox card3 = createStatCard("Tổng Chi", "0", "#f44336");
        lblTongChi = (Label) ((VBox) card3.getChildren().get(1)).getChildren().get(0);
        
        // Card 4: Tiền dự kiến
        VBox card4 = createStatCard("Tiền Dự Kiến", "0", "#9C27B0");
        lblTienDuKien = (Label) ((VBox) card4.getChildren().get(1)).getChildren().get(0);
        
        statsBox.getChildren().addAll(card1, card2, card3, card4);
        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);
        
        return statsBox;
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "15; -fx-background-radius: 10;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", 13));
        titleLabel.setTextFill(Color.web(color));
        
        VBox valueBox = new VBox();
        valueBox.setAlignment(Pos.CENTER_LEFT);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.web("#333333"));
        
        valueBox.getChildren().add(valueLabel);
        card.getChildren().addAll(titleLabel, valueBox);
        
        return card;
    }
    
    private HBox createProfitSection() {
        HBox profitBox = new HBox(15);
        profitBox.setAlignment(Pos.CENTER_LEFT);
        profitBox.setPadding(new Insets(15, 0, 0, 0));
        
        Label label = new Label("Lợi nhuận ròng:");
        label.setFont(Font.font("System", 14));
        label.setTextFill(Color.web("#666666"));
        
        lblLoiNhuanRong = new Label("0 đ");
        lblLoiNhuanRong.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblLoiNhuanRong.setTextFill(Color.web("#4CAF50"));
        
        profitBox.getChildren().addAll(label, lblLoiNhuanRong);
        return profitBox;
    }
    
    
    private VBox createTransactionsSection() {
        VBox section = new VBox(15);
        
        Label title = new Label("Giao Dịch Trong Ca");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#333333"));
        
        transactionListContainer = new VBox(10);
        
        section.getChildren().addAll(title, transactionListContainer);
        return section;
    }
    
    private void loadHistory() {
        historyContainer.getChildren().clear();
        
        if (currentEmployee == null) {
            Label noDataLabel = new Label("Không có dữ liệu");
            noDataLabel.setFont(Font.font("System", 16));
            noDataLabel.setTextFill(Color.web("#999999"));
            historyContainer.getChildren().add(noDataLabel);
            historyContainer.setAlignment(Pos.CENTER);
            return;
        }
        
        List<CaLamViecNhanVien> historyList = caLamViecDAO.getAllCaLamViecByNhanVien(currentEmployee.getMaNhanVien());
        
        if (historyList == null || historyList.isEmpty()) {
            Label noDataLabel = new Label("Chưa có lịch sử ca làm việc");
            noDataLabel.setFont(Font.font("System", 16));
            noDataLabel.setTextFill(Color.web("#999999"));
            historyContainer.getChildren().add(noDataLabel);
            historyContainer.setAlignment(Pos.CENTER);
            return;
        }
        
        for (CaLamViecNhanVien shift : historyList) {
            if (!"Đã hoàn thành".equals(shift.getTrangThai())) continue;
            
            VBox shiftCard = createHistoryShiftCard(shift);
            historyContainer.getChildren().add(shiftCard);
        }
    }
    
    private VBox createHistoryShiftCard(CaLamViecNhanVien shift) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 10; -fx-padding: 20;");
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label employeeLabel = new Label(currentEmployee != null ? currentEmployee.getTenNhanVien() : "N/A");
        employeeLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        employeeLabel.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label profitLabel = new Label("Lợi nhuận ròng");
        profitLabel.setFont(Font.font("System", 12));
        profitLabel.setTextFill(Color.web("#FFFFFF"));
        profitLabel.setStyle("-fx-opacity: 0.9;");
        
        header.getChildren().addAll(employeeLabel, spacer, profitLabel);
        
        // Date and time info
        HBox dateTimeBox = new HBox(20);
        dateTimeBox.setAlignment(Pos.CENTER_LEFT);
        
        Label dateIcon = new Label("Thời gian");
        dateIcon.setFont(Font.font(14));
        
        Label dateLabel = new Label(shift.getNgay() != null ? 
                LocalDate.parse(dateFormat.format(shift.getNgay()), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        .format(dateFormatter) : "N/A");
        dateLabel.setFont(Font.font("System", 14));
        dateLabel.setTextFill(Color.WHITE);
        
       
        String startTime = shift.getNgay() != null ? timeFormat.format(shift.getNgay()) : "08:00";
        Double ketCa = shift.getTienKetCa();
        String endTime = ketCa != null && ketCa > 0 ? "16:30" : "16:00";
        Label timeLabel = new Label(startTime + " - " + endTime + " (8h 0m)");
        timeLabel.setFont(Font.font("System", 14));
        timeLabel.setTextFill(Color.WHITE);
        
        dateTimeBox.getChildren().addAll(dateIcon, dateLabel, timeLabel);
        
        // Profit amount
        double profit = shift.getTongThu() - shift.getTongChi();
        Label profitAmount = new Label(currencyFormat.format(profit));
        profitAmount.setFont(Font.font("System", FontWeight.BOLD, 24));
        profitAmount.setTextFill(Color.WHITE);
        profitAmount.setAlignment(Pos.CENTER_RIGHT);
        profitAmount.setMaxWidth(Double.MAX_VALUE);
        
        // Stats row
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        
        VBox stat1 = createHistoryStat("Tiền đầu ca", currencyFormat.format(shift.getTienMoCa()));
        VBox stat2 = createHistoryStat("Tổng thu", currencyFormat.format(shift.getTongThu()));
        VBox stat3 = createHistoryStat("Tổng chi", currencyFormat.format(shift.getTongChi()));
        Double tienKetCa = shift.getTienKetCa();
        VBox stat4 = createHistoryStat("Tiền cuối ca", currencyFormat.format(tienKetCa != null ? tienKetCa : 0));
        VBox stat5 = createHistoryStat("Chênh lệch", "0 đ");
        
        statsRow.getChildren().addAll(stat1, stat2, stat3, stat4, stat5);
        
        card.getChildren().addAll(header, dateTimeBox, profitAmount, statsRow);
        
        return card;
    }
    
    private VBox createHistoryStat(String label, String value) {
        VBox stat = new VBox(5);
        
        Label labelText = new Label(label);
        labelText.setFont(Font.font("System", 11));
        labelText.setTextFill(Color.web("#FFFFFF"));
        labelText.setStyle("-fx-opacity: 0.8;");
        
        Label valueText = new Label(value);
        valueText.setFont(Font.font("System", FontWeight.BOLD, 13));
        valueText.setTextFill(Color.WHITE);
        
        stat.getChildren().addAll(labelText, valueText);
        return stat;
    }
    
    private void loadCurrentShift() {
        if (currentEmployee == null) return;
        
        currentShift = caLamViecDAO.getCaLamViecDangMoByNhanVien(currentEmployee.getMaNhanVien());
        
        if (currentShift != null && currentShift.getTrangThai().equalsIgnoreCase("Đang mở")) {
            // Reload dashboard to show current shift panel
            loadDashboard();
            updateShiftData();
            setupEventHandlers();
        }
    }
    
    private void updateShiftData() {
        if (currentShift == null) return;
        
        Platform.runLater(() -> {
            // Check if labels are initialized
            if (lblTienDauCa == null || lblTongThu == null || lblTongChi == null || 
                lblTienDuKien == null || lblLoiNhuanRong == null) {
                return;
            }
            
            lblTienDauCa.setText(currencyFormat.format(currentShift.getTienMoCa()));
            lblTongThu.setText(currencyFormat.format(currentShift.getTongThu()));
            lblTongChi.setText(currencyFormat.format(currentShift.getTongChi()));
            
            double tienDuKien = currentShift.getTienMoCa() + currentShift.getTongThu() - currentShift.getTongChi();
            lblTienDuKien.setText(currencyFormat.format(tienDuKien));
            
            double loiNhuan = currentShift.getTongThu() - currentShift.getTongChi();
            lblLoiNhuanRong.setText(currencyFormat.format(loiNhuan));
            
            if (loiNhuan >= 0) {
                lblLoiNhuanRong.setTextFill(Color.web("#4CAF50"));
            } else {
                lblLoiNhuanRong.setTextFill(Color.web("#f44336"));
            }
        });
    }
    
    private void setupEventHandlers() {
        if (btnMoCa != null) {
            btnMoCa.setOnAction(e -> handleMoCa());
        }
        
        if (btnKetCa != null) {
            btnKetCa.setOnAction(e -> handleKetCa());
        }
        
        
        // Tab change listener
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabLichSu) {
                loadHistory();
            }
        });
    }
    
    private void handleMoCa() {
        if (currentEmployee == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin nhân viên", Alert.AlertType.ERROR);
            return;
        }
        
        // Check if there's already an open shift
        CaLamViecNhanVien existingShift = caLamViecDAO.getCaLamViecDangMoByNhanVien(currentEmployee.getMaNhanVien());
        if (existingShift != null) {
            showAlert("Lỗi", "Bạn đã có ca làm việc đang mở.\nVui lòng kết ca hiện tại trước khi mở ca mới.", Alert.AlertType.ERROR);
            // Reload to show the existing shift
            currentShift = existingShift;
            loadDashboard();
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog("500000");
        dialog.setTitle("Mở Ca");
        dialog.setHeaderText("Nhập số tiền đầu ca");
        dialog.setContentText("Số tiền:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            try {
                double tienMoCa = Double.parseDouble(amount.replace(",", "").trim());
                
                if (tienMoCa < 0) {
                    showAlert("Lỗi", "Số tiền không được âm", Alert.AlertType.ERROR);
                    return;
                }
                
                // Create new shift
                boolean success = caLamViecDAO.moCaLamViec(currentEmployee.getMaNhanVien(), tienMoCa);
                
                if (success) {
                    showAlert("Thành công", 
                        String.format("Đã mở ca làm việc thành công!\nTiền đầu ca: %s", 
                        currencyFormat.format(tienMoCa)), 
                        Alert.AlertType.INFORMATION);
                    
                    // Reload current shift from database
                    currentShift = caLamViecDAO.getCaLamViecDangMoByNhanVien(currentEmployee.getMaNhanVien());
                    
                    // Clear old UI and reload dashboard
                    dashboardContainer.getChildren().clear();
                    loadDashboard();
                    
                    // Re-setup event handlers for new buttons
                    setupEventHandlers();
                    
                    // Update shift data
                    updateShiftData();
                } else {
                    showAlert("Lỗi", 
                        "Không thể mở ca làm việc.\n" +
                        "Có thể do:\n" +
                        "1. Bạn đã có ca đang mở\n" +
                        "2. Lỗi kết nối cơ sở dữ liệu\n" +
                        "3. Lỗi hệ thống\n\n" +
                        "Vui lòng xem console để biết thêm chi tiết.", 
                        Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Số tiền không hợp lệ.\nVui lòng nhập số.", Alert.AlertType.ERROR);
            }
        });
    }
    
    private void handleKetCa() {
        if (currentShift == null) {
            showAlert("Lỗi", "Không có ca làm việc đang mở", Alert.AlertType.ERROR);
            return;
        }
        
        // Refresh current shift data from database to get latest values
        String maCaLamViec = currentShift.getMaCaLamViec();
        currentShift = caLamViecDAO.getCaLamViecByMa(maCaLamViec);
        
        if (currentShift == null) {
            showAlert("Lỗi", "Không tìm thấy ca làm việc trong hệ thống", Alert.AlertType.ERROR);
            loadCurrentShift();
            loadDashboard();
            return;
        }
        
        // Verify current shift is still open
        if (!"Đang mở".equals(currentShift.getTrangThai())) {
            showAlert("Lỗi", "Ca làm việc này đã được kết. Vui lòng tải lại trang.", Alert.AlertType.ERROR);
            currentShift = null;
            loadDashboard();
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận");
        confirmAlert.setHeaderText("Bạn có chắc muốn kết ca?");
        
        // Show detailed confirmation message
        double tienDuKien = currentShift.getTienMoCa() + currentShift.getTongThu() - currentShift.getTongChi();
        String confirmMessage = String.format(
            "Thông tin ca làm việc:\n" +
            "- Tiền đầu ca: %s\n" +
            "- Tổng thu: %s\n" +
            "- Tổng chi: %s\n" +
            "- Tiền cuối ca dự kiến: %s\n\n" +
            "Sau khi kết ca, bạn sẽ không thể chỉnh sửa thông tin ca làm việc này.",
            currencyFormat.format(currentShift.getTienMoCa()),
            currencyFormat.format(currentShift.getTongThu()),
            currencyFormat.format(currentShift.getTongChi()),
            currencyFormat.format(tienDuKien)
        );
        confirmAlert.setContentText(confirmMessage);
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = caLamViecDAO.ketCaLamViec(maCaLamViec, tienDuKien);
            
            if (success) {
                showAlert("Thành công", 
                    String.format("Đã kết ca làm việc thành công!\nTiền cuối ca: %s", 
                    currencyFormat.format(tienDuKien)), 
                    Alert.AlertType.INFORMATION);
                
                // Clear current shift
                currentShift = null;
                
                // Clear old UI and reload dashboard to show "No Shift" panel
                dashboardContainer.getChildren().clear();
                loadDashboard();
                
                // Re-setup event handlers for new buttons (Mở Ca button)
                setupEventHandlers();
                
                // Reload history tab
                loadHistory();
            } else {
                showAlert("Lỗi", 
                    "Không thể kết ca làm việc. Vui lòng kiểm tra:\n" +
                    "1. Ca làm việc vẫn đang mở\n" +
                    "2. Kết nối cơ sở dữ liệu\n" +
                    "3. Xem console để biết thêm chi tiết", 
                    Alert.AlertType.ERROR);
                // Reload to get fresh data
                loadCurrentShift();
                loadDashboard();
            }
        }
    }
    
    
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // ============= PUBLIC METHODS ĐỂ CÁC GUI CON GỌI =============
    
    /**
     * Cập nhật tổng thu vào ca hiện tại
     * @param soTien Số tiền cần thêm vào tổng thu
     * @return true nếu cập nhật thành công
     */
    public boolean capNhatTongThu(double soTien) {
        if (currentShift == null || !"Đang mở".equals(currentShift.getTrangThai())) {
            System.err.println("Không có ca đang mở để cập nhật tổng thu");
            showAlert("Lỗi", "Không có ca làm việc đang mở!", Alert.AlertType.ERROR);
            return false;
        }
        
        if (soTien < 0) {
            System.err.println("Số tiền không được âm: " + soTien);
            return false;
        }
        
        double tongThuMoi = currentShift.getTongThu() + soTien;
        
        // Check for overflow before updating
        if (tongThuMoi > 9999999999999999.99) {
            System.err.println("ERROR: Tổng thu vượt quá giới hạn!");
            showAlert("Lỗi", 
                "Tổng thu vượt quá giới hạn cho phép!\n" +
                "Vui lòng kết ca trước khi tiếp tục.", 
                Alert.AlertType.ERROR);
            return false;
        }
        
        currentShift.setTongThu(tongThuMoi);
        
        boolean success = caLamViecDAO.capNhatCaLamViec(currentShift);
        if (success) {
            System.out.println("✓ Đã cập nhật tổng thu: +" + currencyFormat.format(soTien) + " → " + currencyFormat.format(tongThuMoi));
            Platform.runLater(() -> updateShiftData());
        } else {
            System.err.println("✗ Không thể cập nhật tổng thu vào database");
            showAlert("Lỗi", 
                "Không thể cập nhật tổng thu!\n" +
                "Vui lòng kiểm tra kết nối database.", 
                Alert.AlertType.ERROR);
            // Rollback
            currentShift.setTongThu(currentShift.getTongThu() - soTien);
        }
        return success;
    }
    
    /**
     * Cập nhật tổng chi vào ca hiện tại
     * @param soTien Số tiền cần thêm vào tổng chi
     * @return true nếu cập nhật thành công
     */
    public boolean capNhatTongChi(double soTien) {
        if (currentShift == null || !"Đang mở".equals(currentShift.getTrangThai())) {
            System.err.println("Không có ca đang mở để cập nhật tổng chi");
            showAlert("Lỗi", "Không có ca làm việc đang mở!", Alert.AlertType.ERROR);
            return false;
        }
        
        if (soTien < 0) {
            System.err.println("Số tiền không được âm: " + soTien);
            return false;
        }
        
        double tongChiMoi = currentShift.getTongChi() + soTien;
        
        // Check for overflow before updating
        if (tongChiMoi > 9999999999999999.99) {
            System.err.println("ERROR: Tổng chi vượt quá giới hạn!");
            showAlert("Lỗi", 
                "Tổng chi vượt quá giới hạn cho phép!\n" +
                "Vui lòng kết ca trước khi tiếp tục.", 
                Alert.AlertType.ERROR);
            return false;
        }
        
        currentShift.setTongChi(tongChiMoi);
        
        boolean success = caLamViecDAO.capNhatCaLamViec(currentShift);
        if (success) {
            System.out.println("✓ Đã cập nhật tổng chi: +" + currencyFormat.format(soTien) + " → " + currencyFormat.format(tongChiMoi));
            Platform.runLater(() -> updateShiftData());
        } else {
            System.err.println("✗ Không thể cập nhật tổng chi vào database");
            showAlert("Lỗi", 
                "Không thể cập nhật tổng chi!\n" +
                "Vui lòng kiểm tra kết nối database.", 
                Alert.AlertType.ERROR);
            // Rollback
            currentShift.setTongChi(currentShift.getTongChi() - soTien);
        }
        return success;
    }
    
    /**
     * Kiểm tra có ca đang mở không
     * @return true nếu có ca đang mở
     */
    public boolean hasOpenShift() {
        return currentShift != null && "Đang mở".equals(currentShift.getTrangThai());
    }
    
    /**
     * Làm mới dữ liệu ca từ database
     */
    public void refresh() {
        loadCurrentShift();
        loadDashboard();
    }
    
    /**
     * Lấy thông tin ca hiện tại
     * @return Ca làm việc hiện tại hoặc null nếu không có
     */
    public CaLamViecNhanVien getCurrentShift() {
        return currentShift;
    }
}
