package view.Phong;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import controller.Phong_Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.util.converter.LocalTimeStringConverter;
import model.Phong;
import view.CaLamViec_GUI;

/**
 * Giao diện đặt phòng khách sạn
 * Hiển thị danh sách phòng trống và cho phép chọn phòng để đặt
 */
public class DatPhong extends BorderPane {

    // Controllers
    private final Phong_Controller phong_Controller = new Phong_Controller();

    // Bộ lọc tìm kiếm
    private VBox timKiemBox;

    // Lưu danh sách số phòng được gợi ý tối ưu (để hiển thị badge)
    private java.util.Set<String> phongGoiYToiUu = new java.util.HashSet<>();

    // Lưu danh sách các phòng đã chọn
    private java.util.List<Phong> selectedRooms = new java.util.ArrayList<>();

    // Thông tin đặt phòng
    private DatePicker checkInDatePicker;
    private TextField checkInTimeField;
    private DatePicker checkOutDatePicker;
    private TextField checkOutTimeField;
    private Button btnTimKiem;

    private final Image anhThuong = new Image(getClass().getResource("/img/Thuong.jpg").toExternalForm());
    private final Image anhVip = new Image(getClass().getResource("/img/VIP.jpg").toExternalForm());
    private final Image anhFamily = new Image(getClass().getResource("/img/Family.jpg").toExternalForm());

    // Ca làm việc
    private CaLamViec_GUI caLamViecGUI;

    /**
     * Constructor khởi tạo giao diện tìm kiếm phòng
     */
    public DatPhong() {
        init();
    }

    public DatPhong(CaLamViec_GUI caLamViecGUI) {
        this.caLamViecGUI = caLamViecGUI;
        init();
    }

    /**
     * Phương thức khởi tạo các thành phần giao diện chính
     */
    private void init() {

        // Tải file CSS từ resources
        this.getStylesheets().add(getClass().getResource("/css/DatPhong.css").toExternalForm());

        // Layout mới: Filter trên, 2 bảng nằm ngang dưới
        VBox mainLayout = new VBox(16);
        mainLayout.setFillWidth(true);

        // Tạo các thành phần giao diện (thứ tự quan trọng)
        timKiemBox = createTimKiemBox(); // Tạo filter trước (bao gồm btnTimKiem)

        // Phần filter ở trên
        VBox filterSection = new VBox();
        filterSection.getChildren().add(timKiemBox);

        // Phần hiển thị danh sách phòng (tạo sau khi có btnTimKiem)
        ListView<Phong> roomListView = createRoomListView();
        VBox.setVgrow(roomListView, javafx.scene.layout.Priority.ALWAYS);

        // Button đặt phòng ở cuối
        Button btnDatPhong = new Button("Đặt phòng");
        btnDatPhong.setPrefHeight(32);
        btnDatPhong.setMaxWidth(Double.MAX_VALUE);
        btnDatPhong.setStyle(
                "-fx-background-color: #16A34A;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;");

        btnDatPhong.setOnMouseEntered(e -> btnDatPhong.setStyle(
                "-fx-background-color: #15803D;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"));

        btnDatPhong.setOnMouseExited(e -> btnDatPhong.setStyle(
                "-fx-background-color: #16A34A;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"));

        btnDatPhong.setOnAction(e -> onDatPhongClicked());

        mainLayout.getChildren().addAll(filterSection, roomListView, btnDatPhong);

        this.setPadding(new Insets(16, 10, 22, 6));
        this.setCenter(mainLayout);
        // BorderPane.setAlignment(mainLayout, Pos.TOP_LEFT);
    }

    /**
     * Tạo hộp chứa các bộ lọc tìm kiếm
     * 
     * @return VBox chứa các thành phần tìm kiếm
     */
    private VBox createTimKiemBox() {

        VBox box = new VBox();

        // Thiết lập kích thước tự động
        box.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        box.setPadding(new Insets(0));

        // Áp dụng style CSS và hiệu ứng đổ bóng
        box.getStyleClass().add("timKiemBox");
        box.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0, 0, 2);");

        // Tạo 1 hàng ngang chứa tất cả: check-in, check-out, số phòng, số người lớn, số
        // trẻ em, button tìm kiếm
        HBox mainRow = createMainFilterRow();

        box.getChildren().add(mainRow);
        return box;
    }

    /**
     * Tạo hàng filter chính chứa check-in, check-out, số phòng, số người lớn, số
     * trẻ em, button tìm kiếm
     */
    private HBox createMainFilterRow() {
        HBox mainRow = new HBox(5);
        mainRow.setAlignment(Pos.CENTER_LEFT);
        mainRow.setPadding(new Insets(3, 5, 3, 3));
        mainRow.getStyleClass().add("filter-section");

        // Check-in section
        VBox checkIn = new VBox(2);
        checkIn.getStyleClass().add("filter-item");
        Label lblCheckIn = new Label("Check in");
        lblCheckIn.getStyleClass().add("filter-label");
        lblCheckIn.setStyle("-fx-font-size: 10px; -fx-padding: 0 0 0 0;");

        checkInDatePicker = new DatePicker();
        checkInDatePicker.getStyleClass().add("date-picker-airbnb");
        checkInDatePicker.setPrefWidth(115);
        checkInDatePicker.setMinWidth(100);
        checkInDatePicker.setMaxWidth(130);
        checkInDatePicker.setPrefHeight(30);
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

        // Thời gian check-in mặc định là giờ hiện tại + 2 tiếng
        LocalTime time = LocalTime.now().plusHours(2);
        // Làm tròn phút về 00, 15, 30, hoặc 45
        int minute = time.getMinute();
        if (minute < 15) minute = 15;
        else if (minute < 30) minute = 30;
        else if (minute < 45) minute = 45;
        else {
            minute = 0;
            time = time.plusHours(1);
        }
        time = time.withMinute(minute).withSecond(0).withNano(0);
        
        HBox checkInTimeBox = createTimePicker(String.format("%02d:%02d", time.getHour(), time.getMinute()), true);
        checkInTimeField = (TextField) checkInTimeBox.getChildren().get(0);
        checkIn.getChildren().addAll(lblCheckIn, checkInDatePicker, checkInTimeBox);

        // Check-out section
        VBox checkOut = new VBox(4);
        checkOut.getStyleClass().add("filter-item");
        Label lblCheckOut = new Label("Check out");
        lblCheckOut.getStyleClass().add("filter-label");
        lblCheckOut.setStyle("-fx-font-size: 11px; -fx-padding: 0 0 0 0;");

        checkOutDatePicker = new DatePicker();
        checkOutDatePicker.getStyleClass().add("date-picker-airbnb");
        checkOutDatePicker.setPrefWidth(130);
        checkOutDatePicker.setMinWidth(110);
        checkOutDatePicker.setMaxWidth(150);
        checkOutDatePicker.setPrefHeight(32);
        checkOutDatePicker.setValue(LocalDate.now().plusDays(1));

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

        HBox checkOutTimeBox = createTimePicker("12:00", false);
        checkOutTimeField = (TextField) checkOutTimeBox.getChildren().get(0);
        checkOut.getChildren().addAll(lblCheckOut, checkOutDatePicker, checkOutTimeBox);


        // Filter Loại phòng
        VBox filterLoaiPhong = new VBox(2);
        filterLoaiPhong.getStyleClass().add("filter-item");
        Label lblLoaiPhong = new Label("Loại phòng");
        lblLoaiPhong.getStyleClass().add("filter-label");
        lblLoaiPhong.setStyle("-fx-font-size: 10px; -fx-padding: 0 0 2 0;");

        ComboBox<String> loaiPhongComboBox = new ComboBox<>();
        loaiPhongComboBox.getItems().addAll("Tất cả", "Phòng Đơn", "Phòng đôi", "Phòng gia đình");
        loaiPhongComboBox.setValue("Tất cả");
        loaiPhongComboBox.setPrefWidth(95);
        loaiPhongComboBox.setMinWidth(85);
        loaiPhongComboBox.setMaxWidth(115);
        loaiPhongComboBox.setPrefHeight(30);
        loaiPhongComboBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;");
        loaiPhongComboBox.setId("loaiPhongComboBox");

        filterLoaiPhong.getChildren().addAll(lblLoaiPhong, loaiPhongComboBox);

        // Filter Số người lớn
        VBox filterSoNguoiLon = new VBox(2);
        filterSoNguoiLon.getStyleClass().add("filter-item");
        Label lblSoNguoiLon = new Label("Người lớn");
        lblSoNguoiLon.getStyleClass().add("filter-label");
        lblSoNguoiLon.setStyle("-fx-font-size: 10px; -fx-padding: 0 0 2 0;");

        HBox soNguoiLonControl = createNumberControl(2, 1, 10);
        Label lblSoNguoiLonValue = (Label) soNguoiLonControl.getChildren().get(1);
        lblSoNguoiLonValue.setId("soNguoiLonValue");

        filterSoNguoiLon.getChildren().addAll(lblSoNguoiLon, soNguoiLonControl);

        // Filter Số trẻ em
        VBox filterSoTreEm = new VBox(2);
        filterSoTreEm.getStyleClass().add("filter-item");
        Label lblSoTreEm = new Label("Trẻ em");
        lblSoTreEm.getStyleClass().add("filter-label");
        lblSoTreEm.setStyle("-fx-font-size: 10px; -fx-padding: 0 0 2 0;");

        HBox soTreEmControl = createNumberControl(0, 0, 10);
        Label lblSoTreEmValue = (Label) soTreEmControl.getChildren().get(1);
        lblSoTreEmValue.setId("soTreEmValue");

        filterSoTreEm.getChildren().addAll(lblSoTreEm, soTreEmControl);

        // Button tìm kiếm
        btnTimKiem = new Button("Tìm");
        btnTimKiem.setPrefSize(65, 30);
        btnTimKiem.setMinWidth(55);
        btnTimKiem.getStyleClass().add("button-search");
        // setOnAction sẽ được gán trong createRoomListView()

        mainRow.getChildren().addAll(checkIn, checkOut, filterLoaiPhong, filterSoNguoiLon, filterSoTreEm, btnTimKiem);
        return mainRow;
    }

    /**
     * Tạo control số với button + và -
     * 
     * @param defaultValue Giá trị mặc định
     * @param minValue     Giá trị tối thiểu
     * @param maxValue     Giá trị tối đa
     * @return HBox chứa button -, label giá trị, button +
     */
    private HBox createNumberControl(int defaultValue, int minValue, int maxValue) {
        HBox control = new HBox(5);
        control.getStyleClass().add("number-control");
        control.setAlignment(Pos.CENTER);
        control.setPrefWidth(80);
        control.setMaxWidth(100);
        control.setPrefHeight(30);

        // Button giảm (-)
        Button btnMinus = new Button("−");
        btnMinus.getStyleClass().add("number-control-btn");

        // Label hiển thị giá trị
        Label valueLabel = new Label(String.valueOf(defaultValue));
        valueLabel.getStyleClass().add("number-control-value");
        HBox.setHgrow(valueLabel, javafx.scene.layout.Priority.ALWAYS);

        // Button tăng (+)
        Button btnPlus = new Button("+");
        btnPlus.getStyleClass().add("number-control-btn");

        // Xử lý sự kiện
        btnMinus.setOnAction(e -> {
            int currentValue = Integer.parseInt(valueLabel.getText());
            if (currentValue > minValue) {
                valueLabel.setText(String.valueOf(currentValue - 1));
            }
        });

        btnPlus.setOnAction(e -> {
            int currentValue = Integer.parseInt(valueLabel.getText());
            if (currentValue < maxValue) {
                valueLabel.setText(String.valueOf(currentValue + 1));
            }
        });

        control.getChildren().addAll(btnMinus, valueLabel, btnPlus);
        return control;
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

    private HBox createTimePicker(String defaultTime, boolean restrictPastTime) {
        HBox timePickerContainer = new HBox();
        timePickerContainer.setAlignment(Pos.CENTER_LEFT);
        timePickerContainer.setPadding(new Insets(3, 0, 0, 0));

        TextField timeDisplay = new TextField(defaultTime);
        timeDisplay.setPrefWidth(130);
        timeDisplay.setMinWidth(110);
        timeDisplay.setMaxWidth(150);
        timeDisplay.setEditable(false);
        timeDisplay.setPrefHeight(28);
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

        // Lấy giờ hiện tại để kiểm tra nếu cần restrict past time
        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();
        LocalDate selectedDate = checkInDatePicker.getValue();
        boolean isToday = selectedDate != null && selectedDate.equals(LocalDate.now());
        
        // Tạo các nút giờ từ 0-23
        for (int hour = 0; hour <= 23; hour++) {
            String hourStr = String.format("%02d", hour);
            Button hourBtn = new Button(hourStr);
            hourBtn.setPrefWidth(50);
            hourBtn.setPrefHeight(20);
            
            // Disable nút giờ nếu là check-in, ngày hôm nay, và giờ đã qua
            boolean shouldDisable = restrictPastTime && isToday && hour < currentHour;
            
            if (shouldDisable) {
                hourBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #9e9e9e; -fx-border-width: 0; -fx-font-size: 11px;");
                hourBtn.setDisable(true);
            } else {
                hourBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");
                
                // Highlight nút giờ mặc định
                if (hourStr.equals(defaultHour)) {
                    hourBtn.setStyle(
                            "-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
                }
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
     * Lấy thời gian check-in, check-out và format kiểu DATETIME SQL Server
     * 
     * @param checkInDatePicker  DatePicker check-in
     * @param checkInTimeField   TextField giờ check-in (HH:mm)
     * @param checkOutDatePicker DatePicker check-out
     * @param checkOutTimeField  TextField giờ check-out (HH:mm)
     * @return Mảng [checkin, checkout] kiểu String DATETIME SQL Server
     */
    private String[] getCheckinCheckoutSqlDatetime(DatePicker checkInDatePicker, TextField checkInTimeField,
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

    /**
     * Tạo ListView hiển thị danh sách phòng dạng card
     */
    private ListView<Phong> createRoomListView() {
        ListView<Phong> listView = new ListView<>();
        listView.setPrefHeight(USE_COMPUTED_SIZE);
        listView.setStyle("-fx-background-color: #F9FAFB; -fx-border-width: 0;");

        // Thiết lập placeholder khi chưa có dữ liệu
        VBox placeholderBox = new VBox(15);
        placeholderBox.setAlignment(Pos.CENTER);
        placeholderBox.setPadding(new Insets(50));

      
       

        Label placeholderText = new Label("Vui lòng chọn ngày check-in, check-out và nhấn 'Tìm kiếm'");
        placeholderText.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748B;");

        Label placeholderSubtext = new Label("Hệ thống sẽ hiển thị các phòng trống phù hợp với yêu cầu của bạn");
        placeholderSubtext.setStyle("-fx-font-size: 14px; -fx-text-fill: #94A3B8;");

        placeholderBox.getChildren().addAll(placeholderText, placeholderSubtext);
        listView.setPlaceholder(placeholderBox);

        // Thiết lập CellFactory để hiển thị card
        listView.setCellFactory(param -> new ListCell<Phong>() {
            @Override
            protected void updateItem(Phong phong, boolean empty) {
                super.updateItem(phong, empty);
                if (empty || phong == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Kiểm tra xem phòng này có nằm trong tổ hợp gợi ý tối ưu không
                    boolean isTopRecommendation = phongGoiYToiUu.contains(phong.getSoPhong());
                    BorderPane card = createRoomCard(phong, isTopRecommendation);
                    setGraphic(card);
                    setStyle("-fx-background-color: transparent; -fx-padding: 10 0;");
                }
            }
        });

        // Gắn sự kiện tìm kiếm
        btnTimKiem.setOnAction(e -> loadDataToListView(listView));

        return listView;
    }

    /**
     * Load dữ liệu phòng vào ListView
     */
    private void loadDataToListView(ListView<Phong> listView) {
        listView.getItems().clear();
        selectedRooms.clear();

        // Validate
        if (checkInDatePicker.getValue() == null || checkOutDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn ngày check-in và check-out!");
            alert.showAndWait();
            return;
        }

        String checkInTime = checkInTimeField.getText().trim();
        String checkOutTime = checkOutTimeField.getText().trim();
        if (checkInTime.isEmpty() || checkOutTime.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng nhập giờ check-in và check-out!");
            alert.showAndWait();
            return;
        }

        // Kiểm tra thời gian hợp lệ
        try {
            LocalDate checkInDate = checkInDatePicker.getValue();
            LocalDate checkOutDate = checkOutDatePicker.getValue();
            LocalTime checkInTimeValue = LocalTime.parse(checkInTime);
            LocalTime checkOutTimeValue = LocalTime.parse(checkOutTime);
            java.time.LocalDateTime checkInDateTime = java.time.LocalDateTime.of(checkInDate, checkInTimeValue);
            java.time.LocalDateTime checkOutDateTime = java.time.LocalDateTime.of(checkOutDate, checkOutTimeValue);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            
            // Kiểm tra check-in không được trong quá khứ
            if (checkInDateTime.isBefore(now)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Thời gian check-in không được là thời gian trong quá khứ!");
                alert.showAndWait();
                return;
            }
            
            // Kiểm tra check-out không được trong quá khứ
            if (checkOutDateTime.isBefore(now)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Thời gian check-out không được là thời gian trong quá khứ!");
                alert.showAndWait();
                return;
            }
            
            // Kiểm tra check-out phải sau check-in
            if (checkOutDateTime.isBefore(checkInDateTime) || checkOutDateTime.isEqual(checkInDateTime)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Thời gian check-out phải sau thời gian check-in!");
                alert.showAndWait();
                return;
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Định dạng giờ không hợp lệ! Vui lòng nhập theo định dạng HH:mm");
            alert.showAndWait();
            return;
        }

        try {
            // Lấy thời gian check-in và check-out
            String[] checkinCheckout = getCheckinCheckoutSqlDatetime(checkInDatePicker, checkInTimeField,
                    checkOutDatePicker, checkOutTimeField);

            // Lấy loại phòng từ ComboBox
            ComboBox<String> loaiPhongComboBox = (ComboBox<String>) this.lookup("#loaiPhongComboBox");
            String selectedLoaiPhong = loaiPhongComboBox.getValue();
            String tenLoaiPhong = (selectedLoaiPhong != null && !selectedLoaiPhong.equals("Tất cả")) ? selectedLoaiPhong
                    : null;

            // Lấy số người lớn và trẻ em từ filter
            Label lblSoNguoiLon = (Label) this.lookup("#soNguoiLonValue");
            Label lblSoTreEm = (Label) this.lookup("#soTreEmValue");

            int soNguoiLon = lblSoNguoiLon != null ? Integer.parseInt(lblSoNguoiLon.getText()) : 2;
            int soTreEm = lblSoTreEm != null ? Integer.parseInt(lblSoTreEm.getText()) : 0;

            // Gọi controller để lấy danh sách phòng GỢI Ý theo thời gian và số người
            java.util.List<Phong> dsPhongGoiY = phong_Controller.goiYPhongPhuHop(
                    checkinCheckout[0], // Thời gian check-in
                    checkinCheckout[1], // Thời gian check-out
                    tenLoaiPhong, // Loại phòng
                    soNguoiLon, // Số người lớn
                    soTreEm // Số trẻ em
            );

            // Hiển thị danh sách phòng được sắp xếp theo độ phù hợp
            if (dsPhongGoiY != null && !dsPhongGoiY.isEmpty()) {
                // Tìm tổ hợp phòng tối ưu
                java.util.List<model.Phong> toHopPhong = phong_Controller.timToHopPhongToiUu(dsPhongGoiY, soNguoiLon,
                        soTreEm);

                // Sắp xếp: Phòng gợi ý lên đầu, sau đó các phòng còn lại
                java.util.List<model.Phong> dsPhongHienThi = new java.util.ArrayList<>();
                java.util.Set<String> maPhongDaChon = new java.util.HashSet<>();

                // Thêm phòng gợi ý vào đầu
                if (toHopPhong != null && !toHopPhong.isEmpty()) {
                    dsPhongHienThi.addAll(toHopPhong);
                    for (model.Phong p : toHopPhong) {
                        maPhongDaChon.add(p.getMaPhong());
                    }
                }

                // Thêm các phòng còn lại
                for (model.Phong p : dsPhongGoiY) {
                    if (!maPhongDaChon.contains(p.getMaPhong())) {
                        dsPhongHienThi.add(p);
                    }
                }

                // Hiển thị danh sách (phòng gợi ý đầu tiên)
                listView.getItems().addAll(dsPhongHienThi);

                // Hiển thị gợi ý tổ hợp phòng cụ thể (cho nhân viên lễ tân)
                if (toHopPhong != null && !toHopPhong.isEmpty()) {
                    StringBuilder goiY = new StringBuilder();
                    goiY.append("Gợi ý tổ hợp phòng phù hợp nhất: ").append(toHopPhong.size()).append(" phòng\n\n");

                    int tongGia = 0;
                    for (int i = 0; i < toHopPhong.size(); i++) {
                        model.Phong p = toHopPhong.get(i);
                        int gia = (int) p.getLoaiPhong().getGia();
                        tongGia += gia;

                        goiY.append((i + 1)).append(". Phòng ").append(p.getSoPhong())
                                .append(" - ").append(p.getLoaiPhong().getTenLoaiPhong())
                                .append("\n   • Sức chứa: ").append(p.getLoaiPhong().getSoNguoiLonToiDa())
                                .append(" người lớn, ")
                                .append(p.getLoaiPhong().getSoTreEmToiDa()).append(" trẻ em")
                                .append("\n   • Giá: ").append(String.format("%,d", gia)).append(" VNĐ/đêm\n\n");
                    }

                    goiY.append("Tổng giá: ").append(String.format("%,d", tongGia)).append(" VNĐ/đêm");
                    goiY.append("\n\nCác phòng này được đánh dấu NÊN CHỌN trong danh sách.");
                    goiY.append("\n\nLưu ý: Danh sách phòng đã được sắp xếp theo sức chứa (cao → thấp) để bạn dễ tìm.");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Gợi ý cho Lễ tân");
                    alert.setHeaderText("Yêu cầu: " + soNguoiLon + " người lớn + " + soTreEm + " trẻ em");
                    alert.setContentText(goiY.toString());
                    alert.showAndWait();

                    // Lưu danh sách phòng gợi ý để hiển thị badge
                    phongGoiYToiUu = new java.util.HashSet<>();
                    for (model.Phong p : toHopPhong) {
                        phongGoiYToiUu.add(p.getSoPhong());
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Không tìm thấy phòng trống phù hợp với yêu cầu của bạn.\nVui lòng thử thay đổi thời gian hoặc tiêu chí tìm kiếm.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Định dạng thời gian không hợp lệ! Vui lòng nhập giờ theo định dạng HH:mm (ví dụ: 14:00)");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Tạo card hiển thị thông tin phòng - Giao diện dành cho nhân viên lễ tân
     * 
     * @param phong              Phòng cần hiển thị
     * @param isInRecommendation Có nằm trong tổ hợp phòng gợi ý không
     */
    private BorderPane createRoomCard(Phong phong, boolean isInRecommendation) {
        BorderPane card = new BorderPane();
        card.setPadding(new Insets(15));

        // Nếu nằm trong tổ hợp gợi ý, thêm border màu xanh
        String borderStyle = isInRecommendation
                ? "-fx-border-color: #16A34A; -fx-border-width: 2;"
                : "-fx-border-color: #E5E7EB; -fx-border-width: 1;";

        String shadowStyle = isInRecommendation
                ? "-fx-effect: dropshadow(gaussian, rgba(22,163,74,0.2), 12, 0, 0, 3);"
                : "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);";

        card.setStyle(
                "-fx-background-color: white;" +
                        borderStyle +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        shadowStyle);

        // LEFT: Ảnh phòng
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(320, 240);
        imageContainer.setMinSize(300, 225);
        imageContainer.setMaxSize(360, 270);

        // Hiển thị ảnh phòng theo loại
        javafx.scene.image.ImageView roomImageView = new javafx.scene.image.ImageView();
        roomImageView.setFitWidth(320);
        roomImageView.setFitHeight(240);
        roomImageView.setPreserveRatio(false);
        roomImageView.setSmooth(true);
        
        // Chọn ảnh theo loại phòng
        String tenLoaiPhong = phong.getLoaiPhong().getTenLoaiPhong();
        if (tenLoaiPhong != null) {
            if (tenLoaiPhong.contains("Phòng đơn")) {
                roomImageView.setImage(anhThuong);
            } else if (tenLoaiPhong.contains("Phòng đôi")) {
                roomImageView.setImage(anhVip);
            } else if (tenLoaiPhong.contains("Phòng gia đình")) {
                roomImageView.setImage(anhFamily);
            } else {
                roomImageView.setImage(anhThuong); // Mặc định
            }
        } else {
            roomImageView.setImage(anhThuong);
        }
        
        // Bo góc cho ảnh
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(320, 240);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        roomImageView.setClip(clip);

        // Badge "Gợi ý" cho phòng trong tổ hợp
        if (isInRecommendation) {
            Label recommendBadge = new Label("NÊN CHỌN");
            recommendBadge.setPadding(new Insets(6, 12, 6, 12));
            recommendBadge.setStyle(
                    "-fx-background-color: #16A34A;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 20;");
            StackPane.setAlignment(recommendBadge, Pos.TOP_LEFT);
            StackPane.setMargin(recommendBadge, new Insets(8));
            imageContainer.getChildren().add(recommendBadge);
        }

        imageContainer.getChildren().add(roomImageView);

        // CENTER: Thông tin phòng
        VBox infoBox = new VBox(12);
        infoBox.setPadding(new Insets(0, 15, 0, 15));
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Tên phòng
        Label roomName = new Label(phong.getLoaiPhong().getTenLoaiPhong() + " #" + phong.getSoPhong());
        roomName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        roomName.setWrapText(true);
        roomName.setMaxWidth(USE_PREF_SIZE);

        // Sức chứa phòng
        HBox capacityBox = new HBox(15);
        capacityBox.setAlignment(Pos.CENTER_LEFT);

        Label adultsCapacity = new Label(phong.getLoaiPhong().getSoNguoiLonToiDa() + " người lớn");
        adultsCapacity.setStyle(
                "-fx-background-color: #DBEAFE; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-size: 12px; -fx-text-fill: #1E40AF;");

        Label childrenCapacity = new Label(phong.getLoaiPhong().getSoTreEmToiDa() + " trẻ em");
        childrenCapacity.setStyle(
                "-fx-background-color: #FCE7F3; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-size: 12px; -fx-text-fill: #BE185D;");

        capacityBox.getChildren().addAll(adultsCapacity, childrenCapacity);

        // Tiện ích
        HBox amenitiesBox = new HBox(15);
        amenitiesBox.setAlignment(Pos.CENTER_LEFT);

        Label freeWifi = new Label("WiFi miễn phí");
        freeWifi.setStyle(
                "-fx-background-color: #F1F5F9; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-size: 12px;");

        Label roomService = new Label("Dịch vụ phòng");
        roomService.setStyle(
                "-fx-background-color: #F1F5F9; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-size: 12px;");

        amenitiesBox.getChildren().addAll(freeWifi, roomService);

        infoBox.getChildren().addAll(roomName, capacityBox, amenitiesBox);

        // RIGHT: Giá và nút xem phòng
        VBox priceBox = new VBox(10);
        priceBox.setAlignment(Pos.TOP_RIGHT);
        priceBox.setPadding(new Insets(0, 5, 0, 15));
        priceBox.setMinWidth(160);
        priceBox.setPrefWidth(200);

        Label nightlyLabel = new Label("Giá");
        nightlyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B; -fx-alignment: center-right;");
        nightlyLabel.setMaxWidth(Double.MAX_VALUE);
        nightlyLabel.setAlignment(Pos.CENTER_RIGHT);

        // Giá hiện tại
        Label currentPrice = new Label(String.format("%,.0f đ", phong.getLoaiPhong().getGia()) + "/giờ");
        currentPrice.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #DC2626;" +
                        "-fx-alignment: center-right;");
        currentPrice.setMaxWidth(Double.MAX_VALUE);
        currentPrice.setAlignment(Pos.CENTER_RIGHT);
        currentPrice.setWrapText(true);

        Label taxNote = new Label("Giá chưa bao gồm thuế và phí");
        taxNote.setStyle("-fx-font-size: 10px; -fx-text-fill: #9CA3AF; -fx-alignment: center-right;");
        taxNote.setMaxWidth(Double.MAX_VALUE);
        taxNote.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // CheckBox để chọn nhiều phòng
        CheckBox selectCheckBox = new CheckBox("Chọn");
        selectCheckBox.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Xử lý sự kiện chọn phòng
        selectCheckBox.setOnAction(e -> {
            if (selectCheckBox.isSelected()) {
                if (!selectedRooms.contains(phong)) {
                    selectedRooms.add(phong);
                }
            } else {
                selectedRooms.remove(phong);
            }
        });

        // Container cho CheckBox
        HBox checkBoxContainer = new HBox(selectCheckBox);
        checkBoxContainer.setAlignment(Pos.CENTER);
        checkBoxContainer.setPadding(new Insets(10));

        priceBox.getChildren().addAll(nightlyLabel, currentPrice, taxNote, spacer, checkBoxContainer);

        card.setLeft(imageContainer);
        card.setCenter(infoBox);
        card.setRight(priceBox);
        BorderPane.setMargin(imageContainer, new Insets(0, 15, 0, 0));

        return card;
    }

    /**
     * Xử lý sự kiện khi nhấn button Đặt phòng
     * Add tất cả phòng đã chọn vào modal
     */
    private void onDatPhongClicked() {
        // Validate
        if (selectedRooms.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn ít nhất một phòng!");
            alert.showAndWait();
            return;
        }

        // Validate thời gian
        if (checkInDatePicker.getValue() == null || checkOutDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn ngày check-in và check-out!");
            alert.showAndWait();
            return;
        }

        String checkInTime = checkInTimeField.getText().trim();
        String checkOutTime = checkOutTimeField.getText().trim();
        if (checkInTime.isEmpty() || checkOutTime.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng nhập giờ check-in và check-out!");
            alert.showAndWait();
            return;
        }

        try {
            // Parse thời gian check-in và check-out
            String[] timeParts = checkInTime.split(":");
            int checkInHour = Integer.parseInt(timeParts[0]);
            int checkInMinute = timeParts.length > 1 ? Integer.parseInt(timeParts[1]) : 0;
            java.time.LocalDateTime checkInDateTime = java.time.LocalDateTime.of(
                    checkInDatePicker.getValue(),
                    java.time.LocalTime.of(checkInHour, checkInMinute));

            String[] outTimeParts = checkOutTime.split(":");
            int checkOutHour = Integer.parseInt(outTimeParts[0]);
            int checkOutMinute = outTimeParts.length > 1 ? Integer.parseInt(outTimeParts[1]) : 0;
            java.time.LocalDateTime checkOutDateTime = java.time.LocalDateTime.of(
                    checkOutDatePicker.getValue(),
                    java.time.LocalTime.of(checkOutHour, checkOutMinute));

            // Tính số giờ lưu trú
            long hours = java.time.Duration.between(checkInDateTime, checkOutDateTime).toHours();

            // Tạo danh sách ChiTietPhieuDatPhong cho tất cả phòng đã chọn
            java.util.List<model.ChiTietPhieuDatPhong> dsChiTiet = new java.util.ArrayList<>();

            for (Phong phong : selectedRooms) {
                model.ChiTietPhieuDatPhong chiTiet = new model.ChiTietPhieuDatPhong(
                        null, // PhieuDatPhong - sẽ set sau
                        new model.LoaiDatPhong("LDP01"), // LoaiDatPhong - Online
                        new java.util.ArrayList<>(), // DichVu list
                        (int) hours, // Số giờ lưu trú
                        checkInDateTime, // Thời gian nhận phòng
                        checkOutDateTime, // Thời gian trả phòng
                        phong, // Phòng
                        2 // Số người (mặc định)
                );
                dsChiTiet.add(chiTiet);
            }

            // Mở modal đặt phòng với tất cả phòng đã chọn
            openDatPhongModalWithRooms(dsChiTiet);

        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể đặt phòng: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Mở modal đặt phòng với nhiều phòng đã chọn
     */
    private void openDatPhongModalWithRooms(java.util.List<model.ChiTietPhieuDatPhong> dsChiTiet) {
        try {
            // Tạo Stage mới cho modal
            javafx.stage.Stage modalStage = new javafx.stage.Stage();
            modalStage.setTitle("Đặt phòng - " + dsChiTiet.size() + " phòng");
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // Tạo DatPhong_Modal_GUI với callback
            view.Phong.DatPhong_Modal_GUI modalContent = new view.Phong.DatPhong_Modal_GUI(
                    dsChiTiet,
                    caLamViecGUI, // Truyền ca làm việc
                    () -> {
                        // Callback sau khi đặt phòng thành công
                        modalStage.close();
                        // Clear selected rooms
                        selectedRooms.clear();
                    });

            // Tạo Scene
            javafx.scene.Scene scene = new javafx.scene.Scene(modalContent, 1275, 765);
            modalStage.setScene(scene);
            modalStage.setResizable(true);
            modalStage.setMinWidth(1190);
            modalStage.setMinHeight(723);

            // Hiển thị modal
            modalStage.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể mở form đặt phòng: " + ex.getMessage());
            alert.showAndWait();
        }
    }

}
