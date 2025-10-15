package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import controller.Phong_Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import model.LoaiPhong;
import model.Phong;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;

/**
 * Lớp giao diện tìm kiếm phòng
 * Kế thừa từ BorderPane để tạo layout chính
 */
public class TimKiemPhong extends BorderPane {

    // Các thành phần giao diện chính
    private VBox timKiemBox; // Hộp chứa các bộ lọc tìm kiếm
    private Button tatCaPhong; // Nút lọc tất cả phòng
    private Button phongVip; // Nút lọc phòng VIP
    private Button phongThuong; // Nút lọc phòng thường
    private Button[] filters; // Mảng chứa tất cả các nút lọc
    private Phong_Controller phong_Controller = new Phong_Controller();

    /**
     * Constructor khởi tạo giao diện tìm kiếm phòng
     */
    public TimKiemPhong() {
        init();
        stylePage(); // Áp dụng CSS tùy chỉnh
    }

    /**
     * Phương thức khởi tạo các thành phần giao diện chính
     */
    private void init() {

        // Tải file CSS từ resources
        this.getStylesheets().add(getClass().getResource("/css/TimKiemPhong.css").toExternalForm());

        // Tạo các thành phần giao diện
        timKiemBox = createTimKiemBox();
        tableView();

        // Thiết lập layout chính
        this.setTop(timKiemBox); // Đặt hộp tìm kiếm ở phía trên
        this.setCenter(tableView()); // Đặt bảng dữ liệu ở giữa

        // Thiết lập căn chỉnh và margin
        BorderPane.setAlignment(timKiemBox, Pos.TOP_LEFT);
        BorderPane.setMargin(timKiemBox, new Insets(15));
        BorderPane.setAlignment(tableView(), Pos.BOTTOM_CENTER);
    }

    /**
     * Tạo hộp chứa các bộ lọc tìm kiếm
     * 
     * @return VBox chứa các thành phần tìm kiếm
     */
    private VBox createTimKiemBox() {
        VBox box = new VBox();
        double w = 607;
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

        // ===================== PHẦN CHECK IN =====================
        VBox checkIn = new VBox(6);
        Label lblCheckIn = new Label("Check in");
        lblCheckIn.setFont(Font.font("Segoe UI", 14));

        // Tạo DatePicker cho check-in
        DatePicker checkInDatePicker = new DatePicker();
        checkInDatePicker.getStyleClass().add("date-picker-airbnb");
        checkInDatePicker.setPrefWidth(220);
        checkInDatePicker.setValue(LocalDate.now()); // Mặc định là ngày hiện tại

        // StringConverter để hiển thị định dạng ngày theo locale Việt Nam
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

        checkIn.getChildren().addAll(lblCheckIn, checkInDatePicker, checkInTimeBox);

        // ===================== PHẦN CHECK OUT =====================
        VBox checkOut = new VBox(6);
        Label lblCheckOut = new Label("Check out");
        lblCheckOut.setFont(Font.font("Segoe UI", 14));

        // Tạo DatePicker cho check-out
        DatePicker checkOutDatePicker = new DatePicker();
        checkOutDatePicker.getStyleClass().add("date-picker-airbnb");
        checkOutDatePicker.setPrefWidth(220);
        checkOutDatePicker.setValue(LocalDate.now()); // Mặc định là ngày hiện tại

        // StringConverter để hiển thị định dạng ngày theo locale Việt Nam
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
        HBox checkOutTimeBox = createTimePicker("00:00");

        checkOut.getChildren().addAll(lblCheckOut, checkOutDatePicker, checkOutTimeBox);

        // Nút Tìm kiếm
        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefSize(100, 40);
        btnTimKiem.getStyleClass().add("button-search");

        checkInOutBox.getChildren().addAll(checkIn, checkOut, btnTimKiem);

        return checkInOutBox;
    }

    /**
     * Tạo time picker tùy chỉnh với popup chọn giờ và phút
     * 
     * @param defaultTime Thời gian mặc định hiển thị
     * @return HBox chứa time picker
     */
    private HBox createTimePicker(String defaultTime) {
        HBox timePickerContainer = new HBox();
        timePickerContainer.setAlignment(Pos.CENTER_LEFT);
        timePickerContainer.setPadding(new Insets(5, 0, 0, 0));

        // TextField hiển thị thời gian đã chọn (tương tự DatePicker)
        TextField timeDisplay = new TextField(defaultTime);
        timeDisplay.setPrefWidth(220); // Cùng kích thước với DatePicker
        timeDisplay.setEditable(false);
        timeDisplay.setPrefHeight(20);
        timeDisplay.getStyleClass().add("date-picker-airbnb"); // Dùng style giống DatePicker

        // Popup chứa các tùy chọn thời gian (tương tự DatePicker popup)
        Popup timePopup = new Popup();
        timePopup.setAutoHide(true);

        // Nội dung của popup
        VBox popupContent = new VBox(10);
        popupContent.setPadding(new Insets(15));
        popupContent.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #DDDDDD;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 3);");

        // HBox chứa 2 cột: giờ và phút
        HBox timeSelectionBox = new HBox(10);
        timeSelectionBox.setAlignment(Pos.CENTER);

        // ===================== CỘT GIỜ (0-23) =====================
        VBox hourBox = new VBox(5);
        Label hourLabel = new Label("Giờ");
        hourLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        // VBox chứa các nút giờ thay vì ListView
        VBox hourButtonBox = new VBox(2);
        hourButtonBox.setPrefWidth(60);
        hourButtonBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 4;");

        // ScrollPane để giới hạn chiều cao hiển thị 5 dòng (100px)
        ScrollPane hourScrollPane = new ScrollPane(hourButtonBox);
        hourScrollPane.setPrefHeight(100); // 5 dòng x 20px mỗi dòng
        hourScrollPane.setPrefWidth(60);
        hourScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        hourScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Ẩn thanh cuộn
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
        Button[] minuteButtons = new Button[60];
        String defaultMinute = hourTimeParts.length > 1 ? hourTimeParts[1] : "00";

        // Chuẩn hóa phút mặc định trong khoảng 00-59
        try {
            int dm = Integer.parseInt(defaultMinute);
            if (dm < 0 || dm > 59)
                defaultMinute = "00";
            else
                defaultMinute = String.format("%02d", dm);
        } catch (Exception ex) {
            defaultMinute = "00";
        }

        // Tạo các nút phút từ 00-59
        for (int minute = 0; minute < 60; minute++) {
            String minuteStr = String.format("%02d", minute);
            Button minuteBtn = new Button(minuteStr);
            minuteBtn.setPrefWidth(50);
            minuteBtn.setPrefHeight(20);
            minuteBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");

            // Highlight nút phút mặc định
            if (minuteStr.equals(defaultMinute)) {
                minuteBtn.setStyle(
                        "-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
            }

            minuteButtons[minute] = minuteBtn;
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
     * Áp dụng CSS tùy chỉnh cho trang
     */
    private void stylePage() {
        String css = """
                    /* CSS cho DatePicker kiểu Airbnb */
                    .date-picker-airbnb {
                        -fx-background-color: #ffffff;
                        -fx-background-radius: 8;
                        -fx-border-radius: 8;
                        -fx-border-color: #DDDDDD;
                        -fx-border-width: 1;
                        -fx-padding: 12 14;
                        -fx-font-size: 14px;
                        -fx-font-family: "Segoe UI", -apple-system, sans-serif;
                        -fx-text-fill: #222222;
                        -fx-pref-height: 25;
                    }

                    .date-picker-airbnb:hover {
                        -fx-border-color: #B0B0B0;
                        -fx-cursor: hand;
                    }

                    .date-picker-airbnb:focused {
                        -fx-border-color: #1366D9;;
                        -fx-border-width: 2;
                        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 6, 0.0, 0, 2);
                        -fx-background-color: #ffffff;
                    }

                    .date-picker-airbnb .text-field {
                        -fx-background-color: transparent;
                        -fx-border-width: 0;
                        -fx-padding: 0;
                        -fx-text-fill: #222222;
                        -fx-font-family: "Segoe UI", -apple-system, sans-serif;
                        -fx-font-size: 14px;
                        -fx-font-weight: normal;
                    }

                    .date-picker-airbnb .text-field:focused {
                        -fx-background-color: transparent;
                        -fx-border-width: 0;
                    }

                    .date-picker-airbnb .arrow-button {
                        -fx-background-color: transparent;
                        -fx-border-color: transparent;
                        -fx-padding: 0 8 0 0;
                    }

                    .date-picker-airbnb .arrow-button .arrow {
                        -fx-background-color: #717171;
                        -fx-shape: "M7 10l5 5 5-5z";
                        -fx-scale-shape: true;
                        -fx-pref-width: 8;
                        -fx-pref-height: 5;
                    }

                    .spinner {
                        -fx-background-color: #ffffff;
                        -fx-border-color: #DDDDDD;
                        -fx-border-radius: 8;
                        -fx-background-radius: 8;
                        -fx-font-family: "Segoe UI", sans-serif;
                        -fx-font-size: 13px;
                        -fx-pref-height: 36;
                    }

                    .spinner:hover {
                        -fx-border-color: #B0B0B0;
                    }

                    .spinner:focused {
                        -fx-border-color: #1366D9;;
                        -fx-border-width: 2;
                        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 6, 0.0, 0, 2);
                    }
                """;

        // Áp dụng CSS vào scene khi component được thêm vào scene
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add("data:text/css," + css.replace("\n", "%0A"));
            }
        });
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
     * 
     * Tất cả các thành phần đều được tùy chỉnh với các class CSS và cấu hình với 
     * kích thước, khoảng cách và padding phù hợp cho bố cục giao diện tối ưu.
     * 
     * @return HBox chứa toàn bộ giao diện lọc với trường tìm kiếm và các nút lọc thả xuống
     */
    private HBox filterView() {
        HBox filterGroup = new HBox();
        filterGroup.setPrefHeight(60);
        filterGroup.setMaxHeight(60);
        filterGroup.setMinHeight(60);
        filterGroup.setPrefWidth(USE_COMPUTED_SIZE);
        filterGroup.setMaxWidth(USE_COMPUTED_SIZE);
        filterGroup.setMinWidth(USE_COMPUTED_SIZE);
        filterGroup.getStyleClass().add("filter-view");

        TextField search = new TextField();
        search.setPrefSize(300, 34);
        search.getStyleClass().add("search");
        search.setPromptText("Nhập số phòng hoặc CCCD");

        Button btnLoaiPhong = new Button("Loại phòng");
        btnLoaiPhong.setPrefSize(100, 34);
        ContextMenu loaiPhongMenu = new ContextMenu();
        loaiPhongMenu.setStyle("-fx-boerder-radius: 8; -fx-background-radius: 8;");
        MenuItem phongVipItem = new MenuItem("Vip");
        MenuItem phongThuong = new MenuItem("Thường");
        loaiPhongMenu.getItems().addAll(phongVipItem, phongThuong);

        btnLoaiPhong.setOnAction(e -> {
            loaiPhongMenu.show(btnLoaiPhong, javafx.geometry.Side.BOTTOM, 10, 10);
        });

        Button btnTrangThai = new Button("Trạng thái");
        btnTrangThai.setPrefSize(100, 34);
        ContextMenu trangThaiMenu = new ContextMenu();
        trangThaiMenu.setStyle("-fx-boerder-radius: 8; -fx-background-radius: 8;");
        MenuItem coSan = new MenuItem("Có sẵn");
        MenuItem daDat = new MenuItem("Đã đặt");
        MenuItem dangO = new MenuItem("Đang ở");
        trangThaiMenu.getItems().addAll(coSan, daDat, dangO);

        btnTrangThai.setOnAction(e -> {
            trangThaiMenu.show(btnTrangThai, javafx.geometry.Side.BOTTOM, 10, 10);
        });

        Button btnTang = new Button("Tầng");
        btnTang.setPrefSize(100, 34);
        ContextMenu tangMenu = new ContextMenu();
        trangThaiMenu.setStyle("-fx-boerder-radius: 8; -fx-background-radius: 8;");
        MenuItem tang1 = new MenuItem("Tầng 1");
        MenuItem tang2 = new MenuItem("Tầng 2");
        MenuItem tang3 = new MenuItem("Tầng 3");
        MenuItem tang4 = new MenuItem("Tầng 4");
        MenuItem tang5 = new MenuItem("Tầng 5");
        tangMenu.getItems().addAll(tang1, tang2, tang3, tang4, tang5);

        btnTang.setOnAction(e -> {
            tangMenu.show(btnTang, javafx.geometry.Side.BOTTOM, 10, 10);
        });

        btnLoaiPhong.getStyleClass().add("buttonfilter");
        btnTrangThai.getStyleClass().add("buttonfilter");
        btnTang.getStyleClass().add("buttonfilter");
        filterGroup.getChildren().addAll(search, btnLoaiPhong, btnTrangThai, btnTang);
        filterGroup.setPadding(new Insets(12, 6, 12, 6));
        filterGroup.setSpacing(30);

        return filterGroup;
    }

    /**
     * Tạo container chứa TableView
     * 
     * @return VBox chứa bảng dữ liệu phòng
     */
    private VBox tableView() {
        VBox table = new VBox();
        table.setAlignment(Pos.CENTER);
        table.setPrefSize(USE_COMPUTED_SIZE, 800);

        TableView<Phong> tableVV = createTableView();
        table.setSpacing(5);
        table.getChildren().addAll(filterView(), tableVV);
        return table;
    }

    /**
     * Tạo TableView hiển thị danh sách phòng
     * 
     * @return TableView đã được cấu hình
     */
    private TableView<Phong> createTableView() {
        TableView<Phong> tableView = new TableView<>();
        setupTableViewProperties(tableView); // Thiết lập thuộc tính cơ bản
        setupTableColumns(tableView); // Thiết lập các cột
        loadData(tableView); // Tải dữ liệu mẫu
        return tableView;
    }

    /**
     * Thiết lập các thuộc tính cơ bản cho TableView
     * 
     * @param tableView TableView cần thiết lập thuộc tính
     */
    private void setupTableViewProperties(TableView<Phong> tableView) {
        tableView.getStyleClass().add("custom-table");
        tableView.setEditable(false);

        // Thiết lập border và style
        tableView.setBorder(new Border(
                new BorderStroke(
                        Color.web("#E6EAF2"),
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(8),
                        new BorderWidths(1))));

        tableView.setFixedCellSize(60); // Chiều cao cố định cho mỗi dòng
        tableView.setStyle("""
                    -fx-background-color: white;
                    -fx-background-radius: 8;
                    -fx-border-radius: 8;
                """);

        tableView.setPrefWidth(USE_COMPUTED_SIZE);
        tableView.setMaxWidth(Double.MAX_VALUE);

        // Thiết lập chiều cao để hiển thị đúng 7 dòng
        double visibleRows = 7;
        double headerHeight = 34;
        double heightForRows = tableView.getFixedCellSize() * visibleRows + headerHeight;
        tableView.setPrefHeight(479);
        tableView.setMinHeight(479);
        tableView.setMaxHeight(479);

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
        // Map lưu trữ trạng thái chọn của từng dòng
        final java.util.Map<Phong, javafx.beans.property.SimpleBooleanProperty> selectionMap = new java.util.HashMap<>();

        // Tạo các cột
        TableColumn<Phong, Boolean> checkCol = createCheckboxColumn(selectionMap);
        TableColumn<Phong, String> soPhongCol = createSoPhongColumn();
        TableColumn<Phong, String> loaiPhongCol = createLoaiPhongColumn();
        TableColumn<Phong, String> tangCol = createTangColumn();
        TableColumn<Phong, String> dichVuCol = createDichVuColumn();
        TableColumn<Phong, String> trangThaiCol = createTrangThaiColumn();
        TableColumn<Phong, Void> tuyChonCol = createTuyChonColumn();

        // Thêm tất cả cột vào bảng
        tableView.getColumns().addAll(checkCol, soPhongCol, loaiPhongCol, tangCol, dichVuCol, trangThaiCol, tuyChonCol);

        // Đồng bộ selectionMap khi danh sách items thay đổi
        setupSelectionMapListener(tableView, selectionMap);
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
        column.setPrefWidth(170);
        column.getStyleClass().add("table-header");
        column.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        column.setStyle("-fx-alignment: CENTER-LEFT;");
        column.setCellFactory(col -> createStringCell(Pos.CENTER_LEFT));

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
        column.setPrefWidth(150);
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
        column.setPrefWidth(150);
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
     * Tạo cột dịch vụ (hiện tại hiển thị dữ liệu cố định)
     * 
     * @return TableColumn hiển thị các dịch vụ của phòng
     */
    private TableColumn<Phong, String> createDichVuColumn() {
        Label header = createColumnHeader("Dịch Vụ", Pos.CENTER_LEFT);

        TableColumn<Phong, String> column = new TableColumn<>();
        column.setGraphic(header);
        column.setPrefWidth(581);
        column.getStyleClass().add("table-header");
        column.setStyle("-fx-alignment: CENTER-LEFT;");
        // Extract dịch vụ names from the Phong -> LoaiPhong -> dsachDichVu and join them
        column.setCellValueFactory(cellData -> {
            Phong p = cellData.getValue();
            if (p == null || p.getLoaiPhong() == null || p.getLoaiPhong().getDsachDichVu() == null)
                return new SimpleStringProperty("");
            java.util.List<?> services = p.getLoaiPhong().getDsachDichVu();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < services.size(); i++) {
                Object dv = services.get(i);
                try {
                    // DichVu has getTenDichVu(); fall back to toString()
                    java.lang.reflect.Method m = dv.getClass().getMethod("getTenDichVu");
                    Object name = m.invoke(dv);
                    if (name != null) sb.append(name.toString());
                } catch (Exception ex) {
                    if (dv != null) sb.append(dv.toString());
                }
                if (i < services.size() - 1) sb.append(", ");
            }
            return new SimpleStringProperty(sb.toString());
        });
        column.setCellFactory(col -> createStringCell(Pos.CENTER_LEFT));

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
    private TableColumn<Phong, Boolean> createCheckboxColumn(
            java.util.Map<Phong, javafx.beans.property.SimpleBooleanProperty> selectionMap) {
        // Tạo header checkbox
        CheckBox headerCheck = new CheckBox();
        HBox headerCheckWrap = new HBox(headerCheck);
        headerCheckWrap.setAlignment(Pos.CENTER);
        headerCheckWrap.setPrefHeight(34);
        headerCheckWrap.setPadding(new Insets(4, 0, 4, 0));
        headerCheckWrap.getStyleClass().add("table-header-label");

        TableColumn<Phong, Boolean> checkCol = new TableColumn<>();
        checkCol.setGraphic(headerCheckWrap);
        checkCol.setPrefWidth(80);
        checkCol.getStyleClass().add("table-header");
        checkCol.setStyle("-fx-alignment: CENTER;");

        // Hàm cập nhật trạng thái header checkbox (toàn bộ/một phần/không có gì được
        // chọn)
        Runnable updateHeaderState = () -> {
            if (selectionMap.isEmpty()) {
                headerCheck.setSelected(false);
                headerCheck.setIndeterminate(false);
                return;
            }
            boolean allTrue = true;
            boolean allFalse = true;
            for (javafx.beans.property.SimpleBooleanProperty p : selectionMap.values()) {
                if (p.get())
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
        };

        // Xử lý sự kiện khi click header checkbox (chọn tất cả hoặc bỏ chọn tất cả)
        headerCheck.setOnAction(evt -> {
            boolean target = headerCheck.isSelected();
            for (javafx.beans.property.SimpleBooleanProperty p : selectionMap.values()) {
                p.set(target);
            }
            headerCheck.setIndeterminate(false);
        });

        // Thiết lập cell factory cho checkbox trong từng dòng
        checkCol.setCellFactory(col -> new TableCell<Phong, Boolean>() {
            private final CheckBox rowCheck = new CheckBox();
            {
                rowCheck.setAlignment(Pos.CENTER);
                rowCheck.setOnAction(e -> {
                    Phong item = getTableRow() == null ? null : getTableRow().getItem();
                    if (item != null) {
                        javafx.beans.property.SimpleBooleanProperty prop = selectionMap.get(item);
                        if (prop != null)
                            prop.set(rowCheck.isSelected());
                    }
                });
                setAlignment(Pos.CENTER);
            }

            /**
             * Gắn listener để đồng bộ trạng thái checkbox với property
             */
            private void attachListener(Phong item) {
                if (item == null)
                    return;
                javafx.beans.property.SimpleBooleanProperty prop = selectionMap.get(item);
                if (prop == null) {
                    prop = new javafx.beans.property.SimpleBooleanProperty(false);
                    selectionMap.put(item, prop);
                    prop.addListener((obs, oldV, newV) -> updateHeaderState.run());
                }
                rowCheck.selectedProperty().unbind();
                rowCheck.selectedProperty().bindBidirectional(prop);
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Phong rowItem = getTableRow().getItem();
                    attachListener(rowItem);
                    setGraphic(rowCheck);
                }
            }
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
        column.setPrefWidth(150);
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
     * Tạo cột tùy chọn với menu context chứa các hành động
     * 
     * @return TableColumn chứa button tùy chọn với menu
     */
    private TableColumn<Phong, Void> createTuyChonColumn() {
        Label header = createColumnHeader("Tùy chọn", Pos.CENTER);

        TableColumn<Phong, Void> column = new TableColumn<>();
        column.setGraphic(header);
        column.setPrefWidth(140);
        column.getStyleClass().add("table-header");
        column.setStyle("-fx-alignment: CENTER;");

        column.setCellFactory(col -> new TableCell<Phong, Void>() {
            private final Button moreBtn = new Button("⋯");
            {
                moreBtn.setPrefSize(30, 28);
                moreBtn.setFocusTraversable(false);
                setAlignment(Pos.CENTER);

                // Xử lý sự kiện click nút tùy chọn
                moreBtn.setOnAction(e -> {
                    Phong p = getTableRow() == null ? null : getTableRow().getItem();
                    javafx.scene.control.ContextMenu menu = new javafx.scene.control.ContextMenu();
                    javafx.scene.control.MenuItem miDetail = new javafx.scene.control.MenuItem("Chi tiết");
                    javafx.scene.control.MenuItem miEdit = new javafx.scene.control.MenuItem("Sửa");
                    javafx.scene.control.MenuItem miDelete = new javafx.scene.control.MenuItem("Xóa");

                    // TODO: Thêm logic xử lý cho từng hành động
                    miDetail.setOnAction(a -> {
                        // TODO: Hiển thị chi tiết của phòng p
                    });
                    miEdit.setOnAction(a -> {
                        // TODO: Chỉnh sửa phòng p
                    });
                    miDelete.setOnAction(a -> {
                        // TODO: Xóa phòng p
                    });

                    menu.getItems().addAll(miDetail, miEdit, miDelete);
                    menu.show(moreBtn, javafx.geometry.Side.BOTTOM, 0, 0);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    getStyleClass().add("cell-content");
                } else {
                    setGraphic(moreBtn);
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
    private void setupSelectionMapListener(TableView<Phong> tableView,
            java.util.Map<Phong, javafx.beans.property.SimpleBooleanProperty> selectionMap) {
        tableView.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends Phong> c) -> {
            while (c.next()) {
                // Khi có items mới được thêm
                if (c.wasAdded()) {
                    for (Phong p : c.getAddedSubList()) {
                        if (!selectionMap.containsKey(p)) {
                            javafx.beans.property.SimpleBooleanProperty prop = new javafx.beans.property.SimpleBooleanProperty(
                                    false);
                            selectionMap.put(p, prop);
                        }
                    }
                }
                // Khi có items bị xóa
                if (c.wasRemoved()) {
                    for (Phong p : c.getRemoved()) {
                        selectionMap.remove(p);
                    }
                }
            }
        });
    }

    /**
     * Tải dữ liệu vào TableView để test giao diện
     * 
     * @param tableView TableView cần tải dữ liệu
     */
    private void loadData(TableView<Phong> tableView) {
        List<Phong> dsachPhong = phong_Controller.getDsachPhong_TrangTimKiem();
        ObservableList<Phong> observableList = FXCollections.observableArrayList(dsachPhong);
        tableView.setItems(observableList);
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
}
