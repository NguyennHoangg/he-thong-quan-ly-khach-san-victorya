package view.Phong;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import controller.Phong_Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import model.LoaiPhong;
import model.Phong;

/**
 * Giao diện đặt phòng khách sạn
 * Hiển thị danh sách phòng trống và cho phép chọn phòng để đặt
 */
public class DatPhong extends BorderPane {

    // Controllers
    private final Phong_Controller phong_Controller = new Phong_Controller();

    // Bộ lọc tìm kiếm
    private VBox timKiemBox;
    private Button tatCaPhong;
    private Button phongVip;
    private Button phongThuong;
    private Button[] filters;

    // Bảng hiển thị phòng
    private TableView<Phong> tableView;
    private TableView<Phong> rightTableView;
    private java.util.Map<String, javafx.beans.property.SimpleBooleanProperty> selectionMap = new java.util.HashMap<>();
    
    // List lưu chi tiết phiếu đặt phòng
    private java.util.List<model.ChiTietPhieuDatPhong> chiTietPhieuDatPhongList = new java.util.ArrayList<>();

    // Thông tin đặt phòng
    private DatePicker checkInDatePicker;
    private TextField checkInTimeField;
    private DatePicker checkOutDatePicker;
    private TextField checkOutTimeField;
    private Button btnTimKiem;
    private Label countBadgeLabel;

    /**
     * Constructor khởi tạo giao diện tìm kiếm phòng
     */
    public DatPhong() {
        init();
    }

    /**
     * Phương thức khởi tạo các thành phần giao diện chính
     */
    private void init() {

        // Tải file CSS từ resources
        this.getStylesheets().add(getClass().getResource("/css/DatPhong.css").toExternalForm());

        // Tạo các thành phần giao diện
        timKiemBox = createTimKiemBox();
        tableView = createTableView();

        // Tạo left container chứa timKiemBox và tableViewContainer
        VBox leftContainer = new VBox();
        leftContainer.setSpacing(10);
        leftContainer.setPadding(new Insets(15, 10, 15, 15));
        
        // Sử dụng hàm tableView() để tạo giao diện bảng
        HBox tableViewContainer = tableView();
        leftContainer.getChildren().addAll(timKiemBox, tableViewContainer);
        
        // Tạo right panel
        VBox rightPanel = createRightPanel();
        
        // Tạo main container chứa left và right
        HBox mainContainer = new HBox();
        mainContainer.setSpacing(0);
        mainContainer.getChildren().addAll(leftContainer, rightPanel);
        HBox.setHgrow(rightPanel, javafx.scene.layout.Priority.ALWAYS);
        
        this.setCenter(mainContainer);
        BorderPane.setAlignment(mainContainer, Pos.TOP_LEFT);
    }

    /**
     * Tạo hộp chứa các bộ lọc tìm kiếm
     * 
     * @return VBox chứa các thành phần tìm kiếm
     */
    private VBox createTimKiemBox() {

        VBox box = new VBox();
        double w = 650;
        double h = 300;

        // Thiết lập kích thước cố định
        box.setPrefSize(w, h);
        box.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        box.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // Áp dụng style CSS và hiệu ứng đổ bóng
        box.getStyleClass().add("timKiemBox");
        box.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");

        // Tạo các thành phần con
        HBox filterBtnGroup = createFilterButtons(); // Nhóm nút lọc
        HBox checkInOutBox = createCheckInOutBox(); // Hộp chọn ngày check-in/out

        box.getChildren().addAll(filterBtnGroup, checkInOutBox);
        return box;
    }

    /**
     * Tạo nhóm các nút lọc phòng theo loại
     * 
     * @return HBox chứa các nút lọc
     */
    private HBox createFilterButtons() {
        HBox filterBtnGroup = new HBox();
        filterBtnGroup.setSpacing(10);
        filterBtnGroup.setAlignment(Pos.CENTER_LEFT);
        filterBtnGroup.setPrefWidth(600);
        filterBtnGroup.setPadding(new Insets(20));

        // Tạo các nút lọc
        tatCaPhong = new Button("Tất cả phòng");
        tatCaPhong.getStyleClass().add("buttonfilter");
        phongVip = new Button("Vip");
        phongVip.getStyleClass().add("buttonfilter");
        phongThuong = new Button("Thường");
        phongThuong.getStyleClass().add("buttonfilter");

        // Đặt nút "Tất cả phòng" làm mặc định active
        tatCaPhong.getStyleClass().add("active");

        // Lưu các nút vào mảng và thiết lập hành vi
        filters = new Button[] { tatCaPhong, phongVip, phongThuong };
        setupFilterBehavior(filters);

        filterBtnGroup.getChildren().addAll(tatCaPhong, phongVip, phongThuong);
        return filterBtnGroup;
    }

    /**
     * Tạo hộp chọn ngày check-in và check-out
     * 
     * @return HBox chứa các thành phần chọn ngày và thời gian
     */
    private HBox createCheckInOutBox() {
        HBox checkInOutBox = new HBox(20);
        checkInOutBox.setAlignment(Pos.CENTER_LEFT);
        checkInOutBox.setPadding(new Insets(20));

        // Check-in section
        VBox checkIn = new VBox(6);
        Label lblCheckIn = new Label("Check in");
        lblCheckIn.setFont(Font.font("Segoe UI", 14));

        checkInDatePicker = new DatePicker();
        checkInDatePicker.getStyleClass().add("date-picker-airbnb");
        checkInDatePicker.setPrefWidth(220);
        checkInDatePicker.setValue(LocalDate.now());

        checkInDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #c3bfbfff;");
                }
            }
        });
        checkInDatePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd/MM/YYYY",
                    java.util.Locale.forLanguageTag("vi-VN"));

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, formatter)
                        : null;
            }
        });

        // Tạo time picker cho giờ check-in
        HBox checkInTimeBox = createTimePicker("00:00");
        checkInTimeField = (TextField) checkInTimeBox.getChildren().get(0);

        checkIn.getChildren().addAll(lblCheckIn, checkInDatePicker, checkInTimeBox);

        // Check-out section
        VBox checkOut = new VBox(6);
        Label lblCheckOut = new Label("Check out");
        lblCheckOut.setFont(Font.font("Segoe UI", 14));

        checkOutDatePicker = new DatePicker();
        checkOutDatePicker.getStyleClass().add("date-picker-airbnb");
        checkOutDatePicker.setPrefWidth(220);
        checkOutDatePicker.setValue(LocalDate.now());

        checkOutDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #c2c0c1ff;");
                }
            }
        });
        checkOutDatePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY",
                    java.util.Locale.forLanguageTag("vi-VN"));

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, formatter)
                        : null;
            }
        });

        // Tạo time picker cho giờ check-out
        HBox checkOutTimeBox = createTimePicker("12:00");
        checkOutTimeField = (TextField) checkOutTimeBox.getChildren().get(0);

        checkOut.getChildren().addAll(lblCheckOut, checkOutDatePicker, checkOutTimeBox);

        // Đảm bảo check-out luôn sau check-in
        checkInDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && checkOutDatePicker.getValue() != null) {
                if (checkOutDatePicker.getValue().isBefore(newVal)) {
                    checkOutDatePicker.setValue(newVal.plusDays(1));
                }
            }
            updateCheckOutDatePickerConstraints();
        });

        checkOutDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && checkInDatePicker.getValue() != null) {
                if (newVal.isBefore(checkInDatePicker.getValue())) {
                    checkOutDatePicker.setValue(checkInDatePicker.getValue().plusDays(1));
                }
            }
        });

        btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefSize(100, 40);
        btnTimKiem.getStyleClass().add("button-search");

        checkInOutBox.getChildren().addAll(checkIn, checkOut, btnTimKiem);

        return checkInOutBox;
    }

    private void updateCheckOutDatePickerConstraints() {
        checkOutDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate checkInDate = checkInDatePicker.getValue();
                LocalDate minDate = (checkInDate != null) ? checkInDate : LocalDate.now();
                if (date.isBefore(minDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #c2c0c1ff;");
                }
            }
        });
    }

    private HBox createTimePicker(String defaultTime) {
        HBox timePickerContainer = new HBox();
        timePickerContainer.setAlignment(Pos.CENTER_LEFT);
        timePickerContainer.setPadding(new Insets(5, 0, 0, 0));

        TextField timeDisplay = new TextField(defaultTime);
        timeDisplay.setPrefWidth(220);
        timeDisplay.setEditable(false);
        timeDisplay.setPrefHeight(20);
        timeDisplay.getStyleClass().add("date-picker-airbnb");

        Popup timePopup = new Popup();
        timePopup.setAutoHide(true);
        VBox popupContent = new VBox(10);
        popupContent.setPadding(new Insets(15));
        popupContent.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #DDDDDD;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 3);");

        HBox timeSelectionBox = new HBox(10);
        timeSelectionBox.setAlignment(Pos.CENTER);

        // Hour column
        VBox hourBox = new VBox(5);
        Label hourLabel = new Label("Giờ");
        hourLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        VBox hourButtonBox = new VBox(2);
        hourButtonBox.setPrefWidth(60);
        hourButtonBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 4;");

        ScrollPane hourScrollPane = new ScrollPane(hourButtonBox);
        hourScrollPane.setPrefHeight(100);
        hourScrollPane.setPrefWidth(60);
        hourScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        hourScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        hourScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Tạo mảng nút giờ và xử lý giá trị mặc định
        Button[] hourButtons = new Button[24];
        String[] hourTimeParts = defaultTime.split(":");
        String defaultHour = hourTimeParts.length > 0 ? hourTimeParts[0] : "14";

        // Chuẩn hóa giờ mặc định trong khoảng 0-23
        try {
            int dh = Integer.parseInt(defaultHour);
            if (dh < 0 || dh > 23)
                defaultHour = "14";
            else
                defaultHour = String.format("%02d", dh);
        } catch (Exception ex) {
            defaultHour = "14";
        }

        // Tạo các nút giờ từ 0-23
        for (int hour = 0; hour <= 23; hour++) {
            String hourStr = String.format("%02d", hour);
            Button hourBtn = new Button(hourStr);
            hourBtn.setPrefWidth(50);
            hourBtn.setPrefHeight(20);
            hourBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");

            // Highlight nút giờ mặc định
            if (hourStr.equals(defaultHour)) {
                hourBtn.setStyle(
                        "-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
            }

            hourButtons[hour] = hourBtn;
            hourButtonBox.getChildren().add(hourBtn);
        }

        hourBox.getChildren().addAll(hourLabel, hourScrollPane);

        // ===================== CỘT PHÚT (00-59) =====================
        VBox minuteBox = new VBox(5);
        Label minuteLabel = new Label("Phút");
        minuteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        // VBox chứa các nút phút thay vì ListView
        VBox minuteButtonBox = new VBox(2);
        minuteButtonBox.setPrefWidth(60);
        minuteButtonBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 4;");

        // ScrollPane để giới hạn chiều cao hiển thị 5 dòng (100px)
        ScrollPane minuteScrollPane = new ScrollPane(minuteButtonBox);
        minuteScrollPane.setPrefHeight(100); // 5 dòng x 20px mỗi dòng
        minuteScrollPane.setPrefWidth(60);
        minuteScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        minuteScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Ẩn thanh cuộn
        minuteScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Tạo mảng nút phút và xử lý giá trị mặc định
        Button[] minuteButtons = new Button[4];
        String defaultMinute = hourTimeParts.length > 1 ? hourTimeParts[1] : "00";

        // Chuẩn hóa phút mặc định trong các giá trị hợp lệ: 00, 15, 30, 45
        String[] allowedMinutes = { "00", "15", "30", "45" };
        boolean found = false;
        for (String m : allowedMinutes) {
            if (m.equals(defaultMinute)) {
                found = true;
                break;
            }
        }
        if (!found)
            defaultMinute = "00";

        // Tạo các nút phút: 00, 15, 30, 45
        for (int i = 0; i < allowedMinutes.length; i++) {
            String minuteStr = allowedMinutes[i];
            Button minuteBtn = new Button(minuteStr);
            minuteBtn.setPrefWidth(50);
            minuteBtn.setPrefHeight(20);
            minuteBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");

            // Highlight nút phút mặc định
            if (minuteStr.equals(defaultMinute)) {
                minuteBtn.setStyle(
                        "-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
            }

            minuteButtons[i] = minuteBtn;
            minuteButtonBox.getChildren().add(minuteBtn);
        }

        minuteBox.getChildren().addAll(minuteLabel, minuteScrollPane);

        timeSelectionBox.getChildren().addAll(hourBox, minuteBox);

        // Biến để theo dõi giá trị đã chọn
        String[] selectedHour = { defaultHour };
        String[] selectedMinute = { defaultMinute };

        // Xử lý sự kiện click nút giờ
        for (int i = 0; i < hourButtons.length; i++) {
            Button btn = hourButtons[i];
            btn.setOnAction(e -> {
                // Reset tất cả nút giờ về trạng thái bình thường
                for (Button hourBtn : hourButtons) {
                    hourBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");
                }
                // Highlight nút được chọn
                btn.setStyle(
                        "-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
                selectedHour[0] = btn.getText();
            });
        }

        // Xử lý sự kiện click nút phút
        for (int i = 0; i < minuteButtons.length; i++) {
            Button btn = minuteButtons[i];
            btn.setOnAction(e -> {
                // Reset tất cả nút phút về trạng thái bình thường
                for (Button minuteBtn : minuteButtons) {
                    minuteBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");
                }
                // Highlight nút được chọn
                btn.setStyle(
                        "-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
                selectedMinute[0] = btn.getText();
            });
        }

        // Nút OK để xác nhận lựa chọn
        Button okButton = new Button("Chọn");
        okButton.setPrefWidth(100);
        okButton.setStyle(
                "-fx-background-color: #1366D9;;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;");

        // Xử lý sự kiện click nút OK
        okButton.setOnAction(e -> {
            if (selectedHour[0] != null && selectedMinute[0] != null) {
                String newTime = selectedHour[0] + ":" + selectedMinute[0];
                timeDisplay.setText(newTime);
                timePopup.hide();
            }
        });

        popupContent.getChildren().addAll(timeSelectionBox, okButton);
        timePopup.getContent().add(popupContent);

        // Hiển thị popup khi click vào TextField (tương tự DatePicker)
        timeDisplay.setOnMouseClicked(e -> {
            if (!timePopup.isShowing()) {
                timePopup.show(timeDisplay,
                        timeDisplay.localToScreen(timeDisplay.getBoundsInLocal()).getMinX(),
                        timeDisplay.localToScreen(timeDisplay.getBoundsInLocal()).getMaxY() + 5);
            }
        });

        timePickerContainer.getChildren().add(timeDisplay);

        return timePickerContainer;
    }


    /**
     * Tạo và cấu hình thành phần giao diện lọc ngang cho chức năng tìm kiếm phòng.
     * Phương thức này xây dựng giao diện lọc hoàn chỉnh bao gồm trường tìm kiếm và
     * các menu thả xuống để lọc phòng theo nhiều tiêu chí khác nhau.
     * 
     * Giao diện lọc bao gồm:
     * - Trường văn bản để tìm kiếm theo số phòng hoặc số CCCD
     * - Nút menu thả xuống để lọc theo loại phòng (Vip, Thường)
     * - Nút menu thả xuống để lọc theo trạng thái phòng (Có sẵn, Đã đặt, Đang ở)
     * - Nút menu thả xuống để lọc theo tầng (Tầng 1-5)
     * kích thước, khoảng cách và padding phù hợp cho bố cục giao diện tối ưu.
     * 
     * @return HBox chứa toàn bộ giao diện lọc với trường tìm kiếm và các nút lọc
     *         thả xuống
     */
    private HBox filterView() {
        HBox filterGroup = new HBox();
        filterGroup.setPrefHeight(60);
        filterGroup.setMaxHeight(60);
        filterGroup.setMinHeight(60);
        filterGroup.setPrefWidth(600);
        filterGroup.setMaxWidth(600);
        filterGroup.setMinWidth(600);
        filterGroup.getStyleClass().add("filter-view");

        // Thanh tìm kiếm
        TextField search = new TextField();
        search.setPrefSize(300, 34);
        search.getStyleClass().add("search");
        search.setPromptText("Nhập số phòng hoặc CCCD");

        // Xử lý tìm kiếm khi nhập text
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTableData(newValue, null, null, null);
        });

        // Loại phòng
        Button btnLoaiPhong = new Button("Loại phòng");
        btnLoaiPhong.setPrefSize(120, 34);
        btnLoaiPhong.getStyleClass().add("buttonfilter");
        ContextMenu loaiPhongMenu = new ContextMenu();
        loaiPhongMenu.setStyle("-fx-border-radius: 8; -fx-background-radius: 8;");
        MenuItem tatCaLoai = new MenuItem("Tất cả");
        MenuItem phongVipItem = new MenuItem("VIP");
        MenuItem phongThuongItem = new MenuItem("Thường");
        loaiPhongMenu.getItems().addAll(tatCaLoai, phongVipItem, phongThuongItem);

        btnLoaiPhong.setOnAction(e -> {
            loaiPhongMenu.show(btnLoaiPhong, javafx.geometry.Side.BOTTOM, 0, 0);
        });
        tatCaLoai.setOnAction(e -> {
            btnLoaiPhong.setText("Loại phòng");
            filterTableData(search.getText(), null, null, null);
        });
        phongVipItem.setOnAction(e -> {
            btnLoaiPhong.setText("VIP");
            filterTableData(search.getText(), "VIP", null, null);
        });
        phongThuongItem.setOnAction(e -> {
            btnLoaiPhong.setText("Thường");
            filterTableData(search.getText(), "Thường", null, null);
        });

        // Trạng thái
        Button btnTrangThai = new Button("Trạng thái");
        btnTrangThai.setPrefSize(120, 34);
        btnTrangThai.getStyleClass().add("buttonfilter");
        ContextMenu trangThaiMenu = new ContextMenu();
        trangThaiMenu.setStyle("-fx-border-radius: 8; -fx-background-radius: 8;");
        MenuItem tatCaTrangThai = new MenuItem("Tất cả");
        MenuItem coSan = new MenuItem("Trống");
        MenuItem daDat = new MenuItem("Đã đặt");
        MenuItem dangO = new MenuItem("Đang ở");
        trangThaiMenu.getItems().addAll(tatCaTrangThai, coSan, daDat, dangO);
        btnTrangThai.setOnAction(e -> {
            trangThaiMenu.show(btnTrangThai, javafx.geometry.Side.BOTTOM, 0, 0);
        });

        MenuItem[] trangThaiItems = { tatCaTrangThai, coSan, daDat, dangO };
        String[] trangThaiTexts = { "Trạng thái", "Trống", "Đã đặt", "Đang ở" };
        String[] trangThaiFilters = { null, "Trống", "Đã đặt", "Đang ở" };

        for (int i = 0; i < trangThaiItems.length; i++) {
            final int idx = i;
            trangThaiItems[i].setOnAction(e -> {
                btnTrangThai.setText(trangThaiTexts[idx]);
                filterTableData(search.getText(), null, trangThaiFilters[idx], null);
            });
        }

        // Tầng
        Button btnTang = new Button("Tầng");
        btnTang.setPrefSize(100, 34);
        btnTang.getStyleClass().add("buttonfilter");
        ContextMenu tangMenu = new ContextMenu();
        tangMenu.setStyle("-fx-border-radius: 8; -fx-background-radius: 8;");
        MenuItem tatCaTang = new MenuItem("Tất cả");
        MenuItem tang1 = new MenuItem("Tầng 1");
        MenuItem tang2 = new MenuItem("Tầng 2");
        MenuItem tang3 = new MenuItem("Tầng 3");
        MenuItem tang4 = new MenuItem("Tầng 4");
        MenuItem tang5 = new MenuItem("Tầng 5");
        MenuItem tang6 = new MenuItem("Tầng 6");
        MenuItem tang7 = new MenuItem("Tầng 7");
        MenuItem tang8 = new MenuItem("Tầng 8");
        tangMenu.getItems().addAll(tatCaTang, tang1, tang2, tang3, tang4, tang5, tang6, tang7, tang8);
        btnTang.setOnAction(e -> {
            tangMenu.show(btnTang, javafx.geometry.Side.BOTTOM, 0, 0);
        });
        tatCaTang.setOnAction(e -> {
            btnTang.setText("Tầng");
            filterTableData(search.getText(), null, null, null);
        });

        MenuItem[] tangItems = { tang1, tang2, tang3, tang4, tang5, tang6, tang7, tang8 };
        for (int i = 0; i < tangItems.length; i++) {
            final int tangNumber = i + 1;
            tangItems[i].setOnAction(e -> {
                btnTang.setText("Tầng " + tangNumber);
                filterTableData(search.getText(), null, null, tangNumber);
            });
        }

        filterGroup.getChildren().addAll(search, btnLoaiPhong, btnTrangThai, btnTang);
        filterGroup.setPadding(new Insets(12, 6, 12, 6));
        filterGroup.setSpacing(20);

        return filterGroup;
    }

    /**
     * Lọc dữ liệu bảng theo các tiêu chí
     * 
     * @param searchText Từ khóa tìm kiếm (số phòng hoặc CCCD)
     * @param loaiPhong  Loại phòng (VIP, Thường, null = tất cả)
     * @param trangThai  Trạng thái (Trống, Đã đặt, Đang ở, null = tất cả)
     * @param tang       Tầng (1-5, null = tất cả)
     */
    private void filterTableData(String searchText, String loaiPhong, String trangThai, Integer tang) {
        List<Phong> allRooms = phong_Controller.getDsachPhong_TrangTimKiem();
        List<Phong> filteredRooms = new java.util.ArrayList<>();

        for (Phong p : allRooms) {
            boolean matches = true;

            // Lọc theo từ khóa tìm kiếm
            if (searchText != null && !searchText.trim().isEmpty()) {
                String keyword = searchText.toLowerCase();
                boolean matchRoom = p.getSoPhong() != null && p.getSoPhong().toLowerCase().contains(keyword);
                if (!matchRoom) {
                    matches = false;
                }
            }

            // Lọc theo loại phòng
            if (loaiPhong != null && p.getLoaiPhong() != null) {
                if (!p.getLoaiPhong().getTenLoaiPhong().equals(loaiPhong)) {
                    matches = false;
                }
            }

            // Lọc theo trạng thái
            if (trangThai != null) {
                if (!p.getTrangThai().equals(trangThai)) {
                    matches = false;
                }
            }

            // Lọc theo tầng
            if (tang != null) {
                if (p.getTang() != tang) {
                    matches = false;
                }
            }

            if (matches) {
                filteredRooms.add(p);
            }
        }

        ObservableList<Phong> observableList = FXCollections.observableArrayList(filteredRooms);
        tableView.setItems(observableList);
        tableView.refresh();
    }

    /**
     * Tạo container chứa TableView
     * 
     * @return VBox chứa bảng dữ liệu phòng
     */
    private HBox tableView() {
        HBox container = new HBox();
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(10, 24, 10, 24));
        container.setSpacing(24);

        // Make the container take the available center height of this BorderPane
        // (subtract the top search box height). This lets children bind to the
        // container height so the right panel can stretch top-to-bottom.
        container.prefHeightProperty().bind(this.heightProperty().subtract(timKiemBox.heightProperty()).subtract(30));

        // Left column: filters + main table
        VBox left = new VBox();
        left.setAlignment(Pos.TOP_LEFT);
        // Make left responsive: occupy ~15% of the center container width
        left.prefWidthProperty().bind(container.widthProperty().multiply(0.15));
        left.setMaxWidth(Double.MAX_VALUE);
        left.setSpacing(5);
        left.getChildren().addAll(filterView(), tableView);
        // Allow left column to expand vertically so the TableView inside can grow.
        left.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tableView, javafx.scene.layout.Priority.ALWAYS);

        container.getChildren().addAll(left);
        return container;
    }

    /**
     * Create the right-side panel containing a simple TableView
     * (summary/selection).
     */
    private VBox createRightPanel() {
        VBox right = new VBox();
        right.setAlignment(Pos.TOP_LEFT);
        right.setSpacing(12);
        right.setPadding(new Insets(15, 15, 15, 10)); // Cách trái 10px, phải 15px
        right.getStyleClass().add("right-panel");
        
        // Không set width cố định, để nó tự động fill
        right.setMaxWidth(Double.MAX_VALUE);
        right.setFillWidth(true);

        // Title row with count badge
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setSpacing(8);

        Label title = new Label("Danh sách chọn");
        title.setFont(Font.font("Segoe UI", 14));
        title.getStyleClass().add("panel-title");

        countBadgeLabel = new Label("0");
        countBadgeLabel.getStyleClass().add("count-badge");

        titleRow.getChildren().addAll(title, countBadgeLabel);

        // Header cho bảng
        HBox tableHeader = new HBox(14);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 16, 12, 16));
        tableHeader.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E6EAF2; -fx-border-width: 0 0 1 0;");
        
        // Placeholder cho icon check (32px + spacing)
        Region iconSpace = new Region();
        iconSpace.setPrefWidth(46);
        
        Label headerPhong = new Label("Phòng");
        headerPhong.setPrefWidth(60);
        headerPhong.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        Label headerCheckIn = new Label("Check-in");
        headerCheckIn.setPrefWidth(150);
        headerCheckIn.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        Label headerCheckOut = new Label("Check-out");
        headerCheckOut.setPrefWidth(150);
        headerCheckOut.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label headerDuration = new Label("Thời gian");
        headerDuration.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        tableHeader.getChildren().addAll(iconSpace, headerPhong, headerCheckIn, headerCheckOut, spacer, headerDuration);
        
        // Tạo TableView cho danh sách phòng đã chọn
        TableView<Phong> rightTableView = new TableView<>();
        rightTableView.getStyleClass().add("custom-table");
        rightTableView.setMaxWidth(Double.MAX_VALUE);
        rightTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Ẩn header của table và set background trắng
        rightTableView.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-border-color: #E6EAF2;" +
            "-fx-border-width: 1;"
        );
        
        // CSS để ẩn hoàn toàn header
        rightTableView.lookup(".column-header-background");
        rightTableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                javafx.scene.Node header = rightTableView.lookup(".column-header-background");
                if (header != null) {
                    header.setVisible(false);
                    header.setManaged(false);
                }
            }
        });
        
        // Custom row style - tất cả row đều màu trắng, không xen kẽ
        rightTableView.setRowFactory(tv -> {
            TableRow<Phong> row = new TableRow<>();
            
            // Set style mặc định cho tất cả row
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.setStyle("");
                } else {
                    row.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-border-color: transparent transparent #E6EAF2 transparent;" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 0;"
                    );
                }
            });
            
            // Hover effect
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle(
                        "-fx-background-color: #F8F9FA;" +
                        "-fx-border-color: transparent transparent #E6EAF2 transparent;" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 0;"
                    );
                }
            });
            
            row.setOnMouseExited(e -> {
                if (!row.isEmpty()) {
                    row.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-border-color: transparent transparent #E6EAF2 transparent;" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 0;"
                    );
                }
            });
            
            return row;
        });
        
        // Chỉ có 1 cột duy nhất để tự custom toàn bộ row
        TableColumn<Phong, Phong> colRow = new TableColumn<>();
        colRow.prefWidthProperty().bind(rightTableView.widthProperty());
        colRow.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        
        colRow.setCellFactory(col -> new TableCell<Phong, Phong>() {
            @Override
            protected void updateItem(Phong p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setGraphic(null);
                } else {
                    HBox row = new HBox(14);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(12, 16, 12, 16));
                    
                    // Icon check màu xanh
                    Label checkIcon = new Label("✓");
                    checkIcon.setPrefSize(32, 32);
                    checkIcon.setMinSize(32, 32);
                    checkIcon.setMaxSize(32, 32);
                    checkIcon.setAlignment(Pos.CENTER);
                    checkIcon.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; " +
                                     "-fx-background-radius: 16; -fx-font-weight: bold; -fx-font-size: 16px;");
                    
                    // Số phòng - khớp với header (60px)
                    Label roomLabel = new Label("#" + p.getSoPhong());
                    roomLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0F1724;");
                    roomLabel.setPrefWidth(60);
                    roomLabel.setMinWidth(60);
                    roomLabel.setMaxWidth(60);
                    
                    // Check-in box - khớp với header (150px)
                    VBox checkInBox = new VBox(4);
                    checkInBox.setPrefWidth(150);
                    checkInBox.setMinWidth(150);
                    checkInBox.setMaxWidth(150);
                    checkInBox.setPadding(new Insets(10, 14, 10, 14));
                    checkInBox.setStyle("-fx-background-color: #DDE7F2; -fx-background-radius: 10;");
                    
                    Label ciTitle = new Label("📅  Check-in");
                    ciTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #0F1724; -fx-font-weight: 600;");
                    
                    String ciText = formatSelectedDate(checkInDatePicker, checkInTimeField);
                    Label ciDate = new Label(ciText);
                    ciDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
                    
                    checkInBox.getChildren().addAll(ciTitle, ciDate);
                    
                    // Check-out box - khớp với header (150px)
                    VBox checkOutBox = new VBox(4);
                    checkOutBox.setPrefWidth(150);
                    checkOutBox.setMinWidth(150);
                    checkOutBox.setMaxWidth(150);
                    checkOutBox.setPadding(new Insets(10, 14, 10, 14));
                    checkOutBox.setStyle("-fx-background-color: #A7E6C3; -fx-background-radius: 10;");
                    
                    Label coTitle = new Label("📅  Check-out");
                    coTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #0F1724; -fx-font-weight: 600;");
                    
                    String coText = formatSelectedDate(checkOutDatePicker, checkOutTimeField);
                    Label coDate = new Label(coText);
                    coDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
                    
                    checkOutBox.getChildren().addAll(coTitle, coDate);
                    
                    // Spacer
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    
                    // Thời gian
                    String duration = computeDurationText(checkInDatePicker, checkInTimeField, checkOutDatePicker, checkOutTimeField);
                    Label durationLabel = new Label(duration);
                    durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B; -fx-font-weight: 600;");
                    
                    row.getChildren().addAll(checkIcon, roomLabel, checkInBox, checkOutBox, spacer, durationLabel);
                    setGraphic(row);
                }
            }
        });
        
        rightTableView.getColumns().add(colRow);
        
        // Thiết lập placeholder
        Label placeholder = new Label("Chưa chọn phòng nào");
        placeholder.setStyle("-fx-text-fill: #999999; -fx-font-size: 14px;");
        rightTableView.setPlaceholder(placeholder);
        
        // Cho TableView chiếm hết không gian còn lại
        VBox.setVgrow(rightTableView, javafx.scene.layout.Priority.ALWAYS);
        
        // Lưu reference để update sau
        this.rightTableView = rightTableView;

        // Confirm button - chiều rộng bằng rightPanel
        Button confirm = new Button("Xác nhận");
        confirm.getStyleClass().add("button-search");
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setPrefHeight(52);
        
        // Xử lý sự kiện khi click nút Xác nhận
        confirm.setOnAction(e -> {
            if (this.rightTableView.getItems().isEmpty()) {
                showAlert("Chưa chọn phòng", "Vui lòng chưa có phòng nào được chọn!");
                return;
            }
            
            // Mở modal đặt phòng với danh sách phòng đã có trong rightTableView
            openDatPhongModal();
        });

        right.getChildren().addAll(titleRow, tableHeader, rightTableView, confirm);
        return right;
    }
    
    /**
     * Format LocalDate và time string thành chuỗi dd/MM/yyyy HH:mm
     */
    private String formatDateTime(java.time.LocalDate date, String time) {
        if (date == null || time == null || time.isEmpty()) {
            return "";
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateFormatter.format(date) + " " + time;
    }
    
    /**
     * Parse LocalDate và time string thành LocalDateTime
     */
    private java.time.LocalDateTime parseDateTime(java.time.LocalDate date, String time) {
        if (date == null || time == null || time.isEmpty()) {
            time = "00:00";
        }
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return java.time.LocalDateTime.of(date, java.time.LocalTime.of(hour, minute));
    }
    
    /**
     * Mở modal đặt phòng
     */
    private void openDatPhongModal() {
        try {
            // Tạo Stage mới cho modal
            javafx.stage.Stage modalStage = new javafx.stage.Stage();
            modalStage.setTitle("Đặt phòng");
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            // Tạo DatPhong_Modal_GUI với danh sách chi tiết phiếu đặt phòng và callback để reload data
            view.Phong.DatPhong_Modal_GUI modalContent = new view.Phong.DatPhong_Modal_GUI(
                chiTietPhieuDatPhongList,
                () -> {
                    // Reload bảng phòng sau khi đặt phòng thành công
                    loadDataSauKhiTimKiem(tableView);
                    // Reset danh sách chi tiết phiếu đặt phòng
                    chiTietPhieuDatPhongList.clear();
                    // Xóa bảng bên phải (rightTableView)
                    rightTableView.getItems().clear();
                    // Reset selection map
                    selectionMap.clear();
                    // Cập nhật badge count
                    updateCountBadge();
                }
            );
            
            // Tạo Scene với kích thước lớn hơn
            javafx.scene.Scene scene = new javafx.scene.Scene(modalContent, 1500, 900);
            modalStage.setScene(scene);
            modalStage.setResizable(true);
            modalStage.setMinWidth(1400);
            modalStage.setMinHeight(850);
            
            // Hiển thị modal
            modalStage.showAndWait();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Lỗi", "Không thể mở form đặt phòng: " + ex.getMessage());
        }
    }
    
    /**
     * Lấy danh sách chi tiết phiếu đặt phòng (để lưu lên CSDL)
     */
    public java.util.List<model.ChiTietPhieuDatPhong> getChiTietPhieuDatPhongList() {
        return chiTietPhieuDatPhongList;
    }
    
    /**
     * Hiển thị alert dialog
     */
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.WARNING
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Tạo TableView hiển thị danh sách phòng
     * 
     * @return TableView đã được cấu hình
     */
    private TableView<Phong> createTableView() {
        // Luôn khởi tạo mới TableView để tránh null
        tableView = new TableView<>();
        setupTableViewProperties(tableView); // Thiết lập thuộc tính cơ bản
        setupTableColumns(tableView); // Thiết lập các cột
        // Gắn sự kiện tìm kiếm
        btnTimKiem.setOnAction(e -> loadDataSauKhiTimKiem(tableView));
        return tableView;
    }

    /**
     * Thiết lập các thuộc tính cơ bản cho TableView
     * 
     * @param tableView TableView cần thiết lập thuộc tính
     */
    private void setupTableViewProperties(TableView<Phong> tableView) {
        tableView.getStyleClass().add("custom-table");
        // Allow editing so CheckBoxTableCell can toggle values on click
        tableView.setEditable(true);

        // Thiết lập border và style
        tableView.setBorder(new Border(
                new BorderStroke(
                        Color.web("#E6EAF2"),
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(8),
                        new BorderWidths(1))));

        // Allow rows to compute height automatically so service text can wrap
        tableView.setFixedCellSize(-1);
        tableView.setStyle("""
                    -fx-background-color: white;
                    -fx-background-radius: 8;
                    -fx-border-radius: 8;
                """);

        // Make rows have white background and a bottom border
        tableView.setRowFactory(tv -> {
            TableRow<Phong> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.setStyle("");
                } else {
                    // white background + subtle bottom border
                    row.setStyle(
                            "-fx-background-color: white; -fx-border-color: transparent transparent #E6EAF2 transparent; -fx-border-width: 0 0 1 0;");
                }
            });
            return row;
        });

        // Align table width with search/filter box width (600)
        tableView.setPrefWidth(600);
        tableView.setMaxWidth(600);
        tableView.setMinWidth(600);

        int soDong = 8;
        double baselineRowHeight = 54; // baseline for preferred height when empty
        double chieuCao = soDong * baselineRowHeight + 34; // 34px header
        tableView.setPrefHeight(chieuCao);
        tableView.setMinHeight(200);
        tableView.setMaxHeight(chieuCao + 300); // allow some expansion when rows wrap

        // Thiết lập placeholder khi không có dữ liệu
        Label placeholder = new Label("Không có dữ liệu");
        placeholder.setStyle(
                "-fx-background-color: white; -fx-text-fill: #666666; -fx-padding: 16; -fx-alignment: center;");
        placeholder.setMaxWidth(Double.MAX_VALUE);
        tableView.setPlaceholder(placeholder);
    }

    /**
     * Thiết lập tất cả các cột cho TableView
     * 
     * @param tableView TableView cần thiết lập các cột
     */
    @SuppressWarnings("unchecked")
    private void setupTableColumns(TableView<Phong> tableView) {
        // Tạo các cột
        TableColumn<Phong, Boolean> checkCol = createCheckboxColumn();
        TableColumn<Phong, String> soPhongCol = createSoPhongColumn();
        TableColumn<Phong, String> loaiPhongCol = createLoaiPhongColumn();
        TableColumn<Phong, String> tangCol = createTangColumn();
        TableColumn<Phong, String> trangThaiCol = createTrangThaiColumn();

        // Thêm tất cả cột vào bảng (loại bỏ cột "Dịch Vụ" theo yêu cầu)
        tableView.getColumns().addAll(checkCol, soPhongCol, loaiPhongCol, tangCol, trangThaiCol);

        // Đồng bộ selectionMap khi danh sách items thay đổi
        setupSelectionMapListener(tableView);
    }

    /**
     * Tạo cột số phòng
     * 
     * @return TableColumn hiển thị số phòng
     */
    private TableColumn<Phong, String> createSoPhongColumn() {
        Label header = createColumnHeader("Số Phòng", Pos.CENTER_LEFT);

        TableColumn<Phong, String> column = new TableColumn<>();
        column.setGraphic(header);
        column.setPrefWidth(100);
        column.getStyleClass().add("table-header");
        column.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        column.setStyle("-fx-alignment: CENTER-LEFT;");
        // Custom cell: wrap text and show tooltip. Only show services text when
        // present;
        // VIP rooms will have "Tất cả dịch vụ" from the value factory.
        column.setCellFactory(col -> new TableCell<Phong, String>() {
            private final Label lbl = new Label();
            {
                lbl.setWrapText(true);
                lbl.getStyleClass().add("cell-content");
                lbl.setMaxWidth(column.getPrefWidth() - 20);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.trim().isEmpty()) {
                    setText(null);
                    setGraphic(null);
                } else {
                    lbl.setText(item);
                    lbl.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    Tooltip.install(lbl, new Tooltip(item));
                    setGraphic(lbl);
                }
            }
        });

        return column;
    }

    /**
     * Tạo cột loại phòng
     * 
     * @return TableColumn hiển thị loại phòng
     */
    private TableColumn<Phong, String> createLoaiPhongColumn() {
        Label header = createColumnHeader("Loại Phòng", Pos.CENTER_LEFT);

        TableColumn<Phong, String> column = new TableColumn<>();
        column.setGraphic(header);
        column.setPrefWidth(120);
        column.getStyleClass().add("table-header");
        column.setCellValueFactory(cellData -> {
            LoaiPhong lp = cellData.getValue().getLoaiPhong();
            return new SimpleStringProperty(lp != null ? lp.getTenLoaiPhong() : "");
        });
        column.setStyle("-fx-alignment: CENTER-LEFT;");
        column.setCellFactory(col -> createStringCell(Pos.CENTER_LEFT));

        return column;
    }

    /**
     * Tạo cột tầng (dựa trên ký tự đầu của số phòng)
     * 
     * @return TableColumn hiển thị tầng
     */
    private TableColumn<Phong, String> createTangColumn() {
        Label header = createColumnHeader("Tầng", Pos.CENTER);

        TableColumn<Phong, String> column = new TableColumn<>();
        column.setGraphic(header);
        column.setPrefWidth(80);
        column.getStyleClass().add("table-header");
        column.setCellValueFactory(cellData -> {
            String soPhong = cellData.getValue().getSoPhong();
            if (soPhong != null && !soPhong.isEmpty()) {
                String tang = "Tầng " + soPhong.charAt(0);
                return new SimpleStringProperty(tang);
            }
            return new SimpleStringProperty("");
        });
        column.setStyle("-fx-alignment: CENTER;");
        column.setCellFactory(col -> createStringCell(Pos.CENTER));

        return column;
    }

   
    /**
     * Tạo header cho cột với căn lề tùy chỉnh
     * 
     * @param text      Văn bản hiển thị
     * @param alignment Kiểu căn lề
     * @return Label làm header
     */
    private Label createColumnHeader(String text, Pos alignment) {
        Label header = new Label(text);
        header.getStyleClass().add("table-header-label");
        header.setFont(Font.font("Segoe UI", 12));
        header.setPrefHeight(34);
        header.setPadding(new Insets(4, 0, 4, 0));
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(alignment);
        return header;
    }

    /**
     * Tạo cell hiển thị chuỗi với căn lề tùy chỉnh
     * 
     * @param alignment Kiểu căn lề
     * @return TableCell hiển thị chuỗi
     */
    private TableCell<Phong, String> createStringCell(Pos alignment) {
        return new TableCell<Phong, String>() {
            {
                setAlignment(alignment);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().add("cell-content");
                } else {
                    setText(item);
                    getStyleClass().add("cell-content");
                }
            }
        };
    }

    /**
     * Tạo cột checkbox với chức năng chọn tất cả
     * 
     * @param selectionMap Map lưu trữ trạng thái chọn của từng dòng
     * @return TableColumn chứa checkbox
     */
    private TableColumn<Phong, Boolean> createCheckboxColumn() {
        // Header checkbox
        CheckBox headerCheck = new CheckBox();
        headerCheck.setSelected(false);
        headerCheck.setIndeterminate(false);
        HBox headerCheckWrap = new HBox(headerCheck);
        headerCheckWrap.setAlignment(Pos.CENTER);
        headerCheckWrap.setPrefHeight(34);
        headerCheckWrap.setPadding(new Insets(4, 0, 4, 0));
        headerCheckWrap.getStyleClass().add("table-header-label");

        TableColumn<Phong, Boolean> checkCol = new TableColumn<>();
        checkCol.setGraphic(headerCheckWrap);
        checkCol.setPrefWidth(110);
        checkCol.getStyleClass().add("table-header");
        checkCol.setStyle("-fx-alignment: CENTER;");
        checkCol.setEditable(true);

        // Provide a Boolean property per row backed by the class-level selectionMap
        // keyed by maPhong
        checkCol.setCellValueFactory(cellData -> {
            Phong p = cellData.getValue();
            if (p == null || p.getMaPhong() == null)
                return new javafx.beans.property.SimpleBooleanProperty(false);
            String key = p.getMaPhong();
            javafx.beans.property.SimpleBooleanProperty prop = selectionMap.get(key);
            if (prop == null) {
                prop = new javafx.beans.property.SimpleBooleanProperty(false);
                selectionMap.put(key, prop);
                // Update header state when prop changes
                prop.addListener((obs, oldV, newV) -> {
                    boolean allTrue = true;
                    boolean allFalse = true;
                    for (javafx.beans.property.SimpleBooleanProperty pp : selectionMap.values()) {
                        if (pp.get())
                            allFalse = false;
                        else
                            allTrue = false;
                    }
                    if (allTrue) {
                        headerCheck.setIndeterminate(false);
                        headerCheck.setSelected(true);
                    } else if (allFalse) {
                        headerCheck.setIndeterminate(false);
                        headerCheck.setSelected(false);
                    } else {
                        headerCheck.setIndeterminate(true);
                    }

                    // Sync right panel cards when selection changes
                    if (newV != null) {
                        if (newV) {
                            addSelectedRoomCard(p);
                        } else {
                            removeSelectedRoomCard(key);
                        }
                        updateCountBadge();
                    }
                });
            }
            return prop;
        });

        // Use built-in CheckBoxTableCell for robustness
        checkCol.setCellFactory(CheckBoxTableCell.forTableColumn(checkCol));

        // header action: set all
        headerCheck.setOnAction(evt -> {
            boolean target = headerCheck.isSelected();
            for (javafx.beans.property.SimpleBooleanProperty p : selectionMap.values()) {
                p.set(target);
            }
            headerCheck.setIndeterminate(false);
        });

        return checkCol;
    }

    /**
     * Tạo cột trạng thái với label màu tương ứng
     * 
     * @return TableColumn hiển thị trạng thái phòng với màu sắc phân biệt
     */
    private TableColumn<Phong, String> createTrangThaiColumn() {
        Label header = createColumnHeader("Trạng thái", Pos.CENTER);

        TableColumn<Phong, String> column = new TableColumn<>();
        column.setGraphic(header);
        column.setPrefWidth(120);
        column.getStyleClass().add("table-header");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrangThai()));
        column.setStyle("-fx-alignment: CENTER;");

        column.setCellFactory(col -> new TableCell<Phong, String>() {
            {
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Tạo label với màu nền tương ứng trạng thái
                    Label statusLabel = new Label(item);
                    statusLabel.setPrefWidth(80);
                    statusLabel.setAlignment(Pos.CENTER);
                    statusLabel.setStyle(
                            "-fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

                    // Thiết lập màu sắc theo trạng thái
                    switch (item.toLowerCase()) {
                        case "có sẵn":
                        case "trống":
                            statusLabel.setStyle(
                                    statusLabel.getStyle() + "-fx-background-color: #E8F1FD; -fx-text-fill: #448DF2;");
                            break;
                        case "đã đặt":
                            statusLabel.setStyle(
                                    statusLabel.getStyle() + "-fx-background-color: #FEECEB; -fx-text-fill: #F36960;");
                            break;
                        case "đang ở":
                            statusLabel.setStyle(
                                    statusLabel.getStyle() + "-fx-background-color: #E7F8F0; -fx-text-fill: #41C588;");
                            break;
                        default:
                            statusLabel.setStyle(
                                    statusLabel.getStyle() + "-fx-background-color: #F3F4F6; -fx-text-fill: #374151;");
                    }

                    setText(null);
                    setGraphic(statusLabel);
                    getStyleClass().add("cell-content");
                }
            }
        });

        return column;
    }

    /**
     * Thiết lập listener để đồng bộ selectionMap khi danh sách items thay đổi
     * 
     * @param tableView    TableView cần thiết lập listener
     * @param selectionMap Map lưu trữ trạng thái chọn
     */
    private void setupSelectionMapListener(TableView<Phong> tableView) {
        tableView.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends Phong> c) -> {
            while (c.next()) {
                // Khi có items bị xóa
                if (c.wasRemoved()) {
                    for (Phong p : c.getRemoved()) {
                        if (p != null && p.getMaPhong() != null) {
                            // remove selection state and right panel card
                            selectionMap.remove(p.getMaPhong());
                            removeSelectedRoomCard(p.getMaPhong());
                        }
                    }
                    updateCountBadge();
                }
                
            }
        });
    }
    /**
     * Add a room to the right table view for a selected room.
     */
    private void addSelectedRoomCard(Phong p) {
        if (p == null || p.getMaPhong() == null || rightTableView == null)
            return;
        String key = p.getMaPhong();
        
        // Kiểm tra xem phòng đã có trong list chưa
        boolean exists = chiTietPhieuDatPhongList.stream()
                .anyMatch(item -> item.getPhong().getMaPhong().equals(key));
        
        if (!exists) {
            // Thêm phòng vào rightTableView để hiển thị
            rightTableView.getItems().add(p);
            
            // Parse thời gian check-in và check-out
            java.time.LocalDateTime checkIn = parseDateTime(checkInDatePicker.getValue(), checkInTimeField.getText());
            java.time.LocalDateTime checkOut = parseDateTime(checkOutDatePicker.getValue(), checkOutTimeField.getText());
            
            // Tính số giờ lưu trú
            int soGioLuuTru = (int) java.time.Duration.between(checkIn, checkOut).toHours();
            
            // Lấy danh sách dịch vụ cho phòng VIP
            java.util.List<model.DichVu> dsachDichVu = new java.util.ArrayList<>();
            if (p.getLoaiPhong() != null && p.getLoaiPhong().getTenLoaiPhong() != null 
                && p.getLoaiPhong().getTenLoaiPhong().equalsIgnoreCase("VIP")) {
                // Phòng VIP: thêm tất cả dịch vụ
                java.util.List<model.DichVu> allDichVu = p.getLoaiPhong().getDsachDichVu();
                if (allDichVu != null && !allDichVu.isEmpty()) {
                    dsachDichVu.addAll(allDichVu);
                }
            }
            
            // Tạo ChiTietPhieuDatPhong (chưa có PhieuDatPhong và LoaiDatPhong, sẽ set sau)
            model.ChiTietPhieuDatPhong chiTiet = new model.ChiTietPhieuDatPhong(
                null, // PhieuDatPhong - sẽ set sau khi tạo phiếu
                null, // LoaiDatPhong - sẽ set sau
                dsachDichVu, // Danh sách dịch vụ - VIP có tất cả, thường rỗng
                soGioLuuTru,
                checkIn,
                checkOut,
                p,
                1 // Số người mặc định = 1
            );
            
            chiTietPhieuDatPhongList.add(chiTiet);
        }
        updateCountBadge();
    }

    private void removeSelectedRoomCard(String maPhong) {
        if (maPhong == null || rightTableView == null)
            return;
        
        // Xóa phòng khỏi table
        rightTableView.getItems().removeIf(phong -> phong.getMaPhong().equals(maPhong));
        
        // Xóa phòng khỏi chiTietPhieuDatPhongList
        chiTietPhieuDatPhongList.removeIf(item -> item.getPhong().getMaPhong().equals(maPhong));
        
        updateCountBadge();
    }

    private void updateCountBadge() {
        if (countBadgeLabel != null && rightTableView != null) {
            countBadgeLabel.setText(String.valueOf(rightTableView.getItems().size()));
        }
    }

    private String formatSelectedDate(DatePicker dp, TextField timeField) {
        if (dp == null || dp.getValue() == null)
            return "";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = dp.getValue().format(f);
        String time = (timeField != null && timeField.getText() != null && !timeField.getText().isEmpty())
                ? (" " + timeField.getText())
                : "";
        return date + time;
    }

    private String computeDurationText(DatePicker ciDp, TextField ciTime, DatePicker coDp, TextField coTime) {
        try {
            if (ciDp == null || ciDp.getValue() == null || coDp == null || coDp.getValue() == null)
                return "";
            java.time.LocalDate ciDate = ciDp.getValue();
            java.time.LocalDate coDate = coDp.getValue();
            java.time.LocalTime ciT = java.time.LocalTime
                    .parse((ciTime != null && !ciTime.getText().isEmpty()) ? ciTime.getText() : "00:00");
            java.time.LocalTime coT = java.time.LocalTime
                    .parse((coTime != null && !coTime.getText().isEmpty()) ? coTime.getText() : "00:00");
            java.time.LocalDateTime lci = java.time.LocalDateTime.of(ciDate, ciT);
            java.time.LocalDateTime lco = java.time.LocalDateTime.of(coDate, coT);
            long days = java.time.temporal.ChronoUnit.DAYS.between(lci, lco);
            if (days > 0)
                return days + " ngày";
            long hours = java.time.temporal.ChronoUnit.HOURS.between(lci, lco);
            return hours + " giờ";
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Tải và hiển thị dữ liệu phòng TRỐNG lên TableView sau khi thực hiện tìm kiếm.
     * 
     * Logic mới:
     * - Chỉ hiển thị phòng trống trong khoảng thời gian check-in/check-out
     * - Lọc theo loại phòng nếu có chọn (VIP/Thường)
     * - Trạng thái tất cả phòng được set = "Trống"
     *
     * @param tableView TableView hiển thị danh sách phòng sau khi tìm kiếm.
     */
    private void loadDataSauKhiTimKiem(TableView<Phong> tableView) {
        tableView.getItems().clear();

        // Lấy thời gian check-in và check-out
        String[] checkinCheckout = getCheckinCheckoutSqlDatetime(checkInDatePicker, checkInTimeField,
                checkOutDatePicker, checkOutTimeField);

        // Xác định loại phòng đã chọn
        String loaiPhong = null;
        if (phongVip.getStyleClass().contains("active")) {
            loaiPhong = "VIP";
        } else if (phongThuong.getStyleClass().contains("active")) {
            loaiPhong = "Thường";
        }

        // Gọi controller để lấy danh sách phòng TRỐNG theo thời gian
        List<Phong> dsPhongTrong = phong_Controller.getDsachPhongTrongTheoThoiGian(
            loaiPhong, 
            checkinCheckout[0],
            checkinCheckout[1]
        );

        // Hiển thị danh sách phòng trống
        ObservableList<Phong> observableList = FXCollections.observableArrayList(dsPhongTrong);
        tableView.setItems(observableList);
        tableView.refresh();
    }

    /**
     * Thiết lập hành vi cho các nút lọc (chỉ cho phép chọn một nút tại một thời
     * điểm)
     * 
     * @param filters Mảng các nút lọc
     */
    private void setupFilterBehavior(Button[] filters) {
        for (int i = 0; i < filters.length; i++) {
            Button btn = filters[i];
            btn.setOnAction(evt -> {
                // Bỏ active cho tất cả nút
                for (Button f : filters) {
                    f.getStyleClass().remove("active");
                }
                // Thêm active cho nút được click
                if (!btn.getStyleClass().contains("active")) {
                    btn.getStyleClass().add("active");
                }
            });
        }
    }

    /**
     * Lấy thời gian check-in, check-out và format kiểu DATETIME SQL Server
     * 
     * @param checkInDatePicker  DatePicker check-in
     * @param checkInTimeField   TextField giờ check-in (HH:mm)
     * @param checkOutDatePicker DatePicker check-out
     * @param checkOutTimeField  TextField giờ check-out (HH:mm)
     * @return Mảng [checkin, checkout] kiểu String DATETIME SQL Server
     */
    public String[] getCheckinCheckoutSqlDatetime(DatePicker checkInDatePicker, TextField checkInTimeField,
            DatePicker checkOutDatePicker, TextField checkOutTimeField) {
        LocalDate checkInDate = checkInDatePicker.getValue();
        String checkInTimeStr = checkInTimeField.getText();
        LocalDate checkOutDate = checkOutDatePicker.getValue();
        String checkOutTimeStr = checkOutTimeField.getText();

        // Ghép lại thành LocalDateTime
        java.time.LocalDateTime checkInDateTime = java.time.LocalDateTime.parse(
                checkInDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T" + checkInTimeStr);
        java.time.LocalDateTime checkOutDateTime = java.time.LocalDateTime.parse(
                checkOutDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T" + checkOutTimeStr);

        // Format sang kiểu DATETIME SQL Server
        String checkInSql = checkInDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String checkOutSql = checkOutDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new String[] { checkInSql, checkOutSql };
    }

}
