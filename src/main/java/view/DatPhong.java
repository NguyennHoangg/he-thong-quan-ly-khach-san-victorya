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
import javafx.scene.control.cell.CheckBoxTableCell;
// unused imports removed
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
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;

/**
 * Lớp giao diện tìm kiếm phòng
 * Kế thừa từ BorderPane để tạo layout chính
 */
public class DatPhong extends BorderPane {

    // Các thành phần giao diện chính
    private VBox timKiemBox; // Hộp chứa các bộ lọc tìm kiếm
    private Button tatCaPhong; // Nút lọc tất cả phòng
    private Button phongVip; // Nút lọc phòng VIP
    private Button phongThuong; // Nút lọc phòng thường
    private Button[] filters; // Mảng chứa tất cả các nút lọc

    private Phong_Controller phong_Controller = new Phong_Controller();
    private TableView<Phong> tableView;
    // Selection map keyed by maPhong so checkbox state is stable across reloads
    private java.util.Map<String, javafx.beans.property.SimpleBooleanProperty> selectionMap = new java.util.HashMap<>();

    // Right panel components for selected rooms
    private VBox rightList; // container that will hold selected-room cards
    private java.util.Map<String, javafx.scene.Node> rightCardMap = new java.util.HashMap<>();
    private Label countBadgeLabel;
    private Label emptyStateLabel;

    // Các thành phần tìm kiếm thời gian và loại phòng
    private DatePicker checkInDatePicker;
    private TextField checkInTimeField;
    private DatePicker checkOutDatePicker;
    private TextField checkOutTimeField;
    private Button btnTimKiem;

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

        // Sử dụng hàm tableView() để tạo giao diện bảng và panel bên cạnh
        HBox tableViewContainer = tableView();
        this.setTop(timKiemBox); // Đặt hộp tìm kiếm ở phía trên
        this.setCenter(tableViewContainer); // Đặt HBox chứa 2 bảng ở giữa

        // Thiết lập căn chỉnh và margin
        BorderPane.setAlignment(timKiemBox, Pos.TOP_LEFT);
        BorderPane.setMargin(timKiemBox, new Insets(15));
        BorderPane.setAlignment(tableViewContainer, Pos.TOP_LEFT);
    }

    /**
     * Tạo hộp chứa các bộ lọc tìm kiếm
     * 
     * @return VBox chứa các thành phần tìm kiếm
     */
    private VBox createTimKiemBox() {

        VBox box = new VBox();
        double w = 600;
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
        checkInDatePicker = new DatePicker();
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
        checkInTimeField = (TextField) checkInTimeBox.getChildren().get(0);

        checkIn.getChildren().addAll(lblCheckIn, checkInDatePicker, checkInTimeBox);

        // ===================== PHẦN CHECK OUT =====================
        VBox checkOut = new VBox(6);
        Label lblCheckOut = new Label("Check out");
        lblCheckOut.setFont(Font.font("Segoe UI", 14));

        // Tạo DatePicker cho check-out
        checkOutDatePicker = new DatePicker();
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
        checkOutTimeField = (TextField) checkOutTimeBox.getChildren().get(0);

        checkOut.getChildren().addAll(lblCheckOut, checkOutDatePicker, checkOutTimeBox);

        // Nút Tìm kiếm
        btnTimKiem = new Button("Tìm kiếm");
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

    // private HBox taoBoxThanhToanTienCoc(){}

    // private BorderPane modalThongTinChiTiet(){}

    // private TableView<DichVu> taoBangDichVu(){};

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

        // Right column: additional panel with a simpler table
        VBox right = createRightPanel();
        // Let the right panel grow to fill remaining space and take ~85% width
        HBox.setHgrow(right, javafx.scene.layout.Priority.ALWAYS);
        right.prefWidthProperty().bind(container.widthProperty().multiply(0.85));
        right.setMaxWidth(860);
        // nicer padding and top alignment
        right.setPadding(new Insets(24));
        right.setAlignment(Pos.TOP_CENTER);

        // Make right panel fill the vertical space of the container so it stretches
        // from top to bottom inside the center area.
        right.setMaxHeight(800);
        right.prefHeightProperty().bind(container.heightProperty().subtract(160));
        right.setAlignment(Pos.TOP_LEFT);

        // Ensure both columns can grow vertically within the container
        VBox.setVgrow(right, javafx.scene.layout.Priority.ALWAYS);
        right.setAlignment(Pos.TOP_CENTER);
        container.getChildren().addAll(left, right);
        return container;
    }

    /**
     * Create the right-side panel containing a simple TableView
     * (summary/selection).
     */
    private VBox createRightPanel() {
        VBox right = new VBox();
        right.setAlignment(Pos.TOP_LEFT);
        right.setPrefWidth(860);
        right.setMaxWidth(860);
        right.setSpacing(12);
        right.getStyleClass().add("right-panel");

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

        // Scrollable list of selected-room cards
        rightList = new VBox(12);
        rightList.setAlignment(Pos.TOP_CENTER);
        rightList.setPadding(new Insets(12));

        emptyStateLabel = new Label("Chưa chọn phòng nào");
        emptyStateLabel.getStyleClass().add("empty-state");
        rightList.getChildren().add(emptyStateLabel);

        ScrollPane scroll = new ScrollPane(rightList);
        scroll.setFitToWidth(true);
        // Let the ScrollPane viewport height adapt to the right panel height so the
        // list fills top-to-bottom and the confirm button stays at the bottom.
        scroll.prefViewportHeightProperty().bind(right.heightProperty().subtract(160));
        scroll.getStyleClass().add("table-card");
        // Make scroll width bind to right container width so it expands
        scroll.prefWidthProperty().bind(right.widthProperty().subtract(32));
        // Let scroll take available vertical space so confirm button sits at bottom
        javafx.scene.layout.VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);

        // Add a spacer so the scroll area grows and the confirm button is pinned to
        // the bottom of the right panel (full-width).
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, javafx.scene.layout.Priority.ALWAYS);

        // Confirm button
        Button confirm = new Button("Xác nhận");
        confirm.getStyleClass().add("button-search");
        confirm.prefWidthProperty().bind(right.widthProperty().subtract(40));
        confirm.setPrefHeight(52);

        right.getChildren().addAll(titleRow, scroll, bottomSpacer, confirm);
        return right;
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
        loadData(tableView); // Tải dữ liệu mẫu
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
     * Tạo cột dịch vụ (hiện tại hiển thị dữ liệu cố định)
     * 
     * @return TableColumn hiển thị các dịch vụ của phòng
     */
    private TableColumn<Phong, String> createDichVuColumn() {
        // (removed) services column — intentionally left blank because column was
        // removed from the table. If needed later, reintroduce here.
        return null;
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
                // Note: when items are added we don't pre-create props; they'll be created by
                // the cellValueFactory when needed
            }
        });
    }

    /**
     * Tải dữ liệu vào TableView
     * 
     * @param tableView TableView cần tải dữ liệu
     */
    private void loadData(TableView<Phong> tableView) {
        List<Phong> dsachPhong = phong_Controller.getDsachPhong_TrangTimKiem();
        ObservableList<Phong> observableList = FXCollections.observableArrayList(dsachPhong);
        tableView.setItems(observableList);
    }

    /**
     * Add a visual card to the right panel for a selected room.
     */
    private void addSelectedRoomCard(Phong p) {
        if (p == null || p.getMaPhong() == null)
            return;
        String key = p.getMaPhong();
        if (rightCardMap.containsKey(key))
            return; // already present

        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setStyle(
                "-fx-background-color: white; -fx-border-color: transparent transparent #E6EAF2 transparent; -fx-border-width: 0 0 1 0; -fx-background-radius: 10;");

        // Left check indicator
        Label checkDot = new Label("\u2713");
        checkDot.setPrefSize(40, 40);
        checkDot.setAlignment(Pos.CENTER);
        checkDot.setStyle(
                "-fx-background-color: #16A34A; -fx-text-fill: white; -fx-background-radius: 999; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Room label
        Label roomLbl = new Label("#" + p.getSoPhong());
        roomLbl.setFont(Font.font("Segoe UI", 15));
        roomLbl.setStyle("-fx-text-fill: #0F1724; -fx-font-weight: 700;");

        // Check-in box
        VBox checkInBox = new VBox(6);
        checkInBox.setPadding(new Insets(12));
        checkInBox.setStyle("-fx-background-color: #EEF6FF; -fx-background-radius: 14;");
        Label ciTitle = new Label("\uD83D\uDCC5  Check-in");
        ciTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #0F1724; -fx-font-weight: 700;");
        String ciText = formatSelectedDate(checkInDatePicker, checkInTimeField);
        Label ciDate = new Label(ciText);
        ciDate.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");
        checkInBox.getChildren().addAll(ciTitle, ciDate);

        // Check-out box
        VBox checkOutBox = new VBox(6);
        checkOutBox.setPadding(new Insets(12));
        checkOutBox.setStyle("-fx-background-color: #CFF3E0; -fx-background-radius: 14;");
        Label coTitle = new Label("\uD83D\uDCC5  Check-out");
        coTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #0F1724; -fx-font-weight: 700;");
        String coText = formatSelectedDate(checkOutDatePicker, checkOutTimeField);
        Label coDate = new Label(coText);
        coDate.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");
        checkOutBox.getChildren().addAll(coTitle, coDate);

        HBox mid = new HBox(14, checkInBox, checkOutBox);
        mid.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Duration: compute days/hours between selected checkin/out
        String duration = computeDurationText(checkInDatePicker, checkInTimeField, checkOutDatePicker,
                checkOutTimeField);
        Label durationLbl = new Label(duration);
        durationLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B; -fx-font-weight: 600;");

        card.getChildren().addAll(checkDot, roomLbl, mid, spacer, durationLbl);

        // remove empty-state placeholder if present
        if (emptyStateLabel != null && rightList.getChildren().contains(emptyStateLabel)) {
            rightList.getChildren().remove(emptyStateLabel);
        }
        rightList.getChildren().add(card);
        rightCardMap.put(key, card);
        updateCountBadge();
    }

    private void removeSelectedRoomCard(String maPhong) {
        if (maPhong == null)
            return;
        javafx.scene.Node n = rightCardMap.remove(maPhong);
        if (n != null && rightList.getChildren().contains(n)) {
            rightList.getChildren().remove(n);
        }
        // if no cards left, show empty-state
        if (rightCardMap.isEmpty() && emptyStateLabel != null && !rightList.getChildren().contains(emptyStateLabel)) {
            rightList.getChildren().add(emptyStateLabel);
        }
        updateCountBadge();
    }

    private void updateCountBadge() {
        if (countBadgeLabel != null) {
            countBadgeLabel.setText(String.valueOf(rightCardMap.size()));
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
     * Tải và hiển thị dữ liệu phòng lên TableView sau khi thực hiện tìm kiếm.
     * 
     * Phương thức này sẽ:
     * - Xóa dữ liệu cũ trên TableView.
     * - Lấy thông tin ngày giờ nhận/trả phòng từ các trường giao diện.
     * - Xác định loại phòng được chọn (VIP hoặc Thường).
     * - Lấy danh sách tất cả các phòng và danh sách phòng đã được đặt trong khoảng
     * thời gian tìm kiếm.
     * - Lọc danh sách phòng theo loại phòng (nếu có chọn).
     * - Xác định trạng thái từng phòng ("Đã đặt" hoặc "Trống") dựa trên danh sách
     * phòng đã đặt.
     * - Hiển thị danh sách phòng phù hợp lên TableView.
     *
     * @param tableView TableView hiển thị danh sách phòng sau khi tìm kiếm.
     */
    private void loadDataSauKhiTimKiem(TableView<Phong> tableView) {
        tableView.getItems().clear();

        String[] checkinCheckout = getCheckinCheckoutSqlDatetime(checkInDatePicker, checkInTimeField,
                checkOutDatePicker, checkOutTimeField);

        String loaiPhong = null;
        if (phongVip.getStyleClass().contains("active")) {
            loaiPhong = "VIP";
        } else if (phongThuong.getStyleClass().contains("active")) {
            loaiPhong = "Thường";
        }

        // Lấy tất cả phòng
        List<Phong> tatCaPhong = phong_Controller.getDsachPhong_TrangTimKiem();
        // Lấy danh sách phòng đã đặt trong khoảng thời gian
        List<Phong> phongDaDat = phong_Controller.getDsachPhongTheoThoiGian(loaiPhong, checkinCheckout[0],
                checkinCheckout[1]);

        java.util.Set<String> maPhongDaDatSet = new java.util.HashSet<>();
        if (phongDaDat != null) {
            for (Phong p : phongDaDat) {
                maPhongDaDatSet.add(p.getMaPhong());
            }
        }

        List<Phong> dsHienThi = new java.util.ArrayList<>();
        for (Phong p : tatCaPhong) {
            // Lọc theo loại phòng nếu đã chọn
            if (loaiPhong != null && !p.getLoaiPhong().getTenLoaiPhong().equals(loaiPhong)) {
                continue;
            }

            // Xác định trạng thái phòng theo khoảng thời gian
            String trangThai = maPhongDaDatSet.contains(p.getMaPhong()) ? "Đã đặt" : "Trống";
            Phong clone = new Phong(p.getMaPhong(), p.getSoPhong(), p.getLoaiPhong(), trangThai, p.getTang());
            dsHienThi.add(clone);
        }

        ObservableList<Phong> observableList = FXCollections.observableArrayList(dsHienThi);
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
