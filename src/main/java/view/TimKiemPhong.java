package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.scene.control.ScrollPane;

public class TimKiemPhong extends BorderPane {

    private VBox timKiemBox;
    private Button tatCaPhong;
    private Button phongVip;
    private Button phongThuong;
    private Button[] filters;

    public TimKiemPhong() {
        init();
        stylePage(); // Apply custom styling
    }

    private void init() {
        this.getStylesheets().add(getClass().getResource("/css/TimKiemPhong.css").toExternalForm());

        timKiemBox = createTimKiemBox();

        this.setTop(timKiemBox);
        BorderPane.setAlignment(timKiemBox, Pos.TOP_LEFT);
        BorderPane.setMargin(timKiemBox, new Insets(15));
    }

    private VBox createTimKiemBox() {
        VBox box = new VBox();
        double w = 607;
        double h = 300;

        box.setPrefSize(w, h);
        // đảm bảo không bị kéo giãn
        box.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        box.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        box.getStyleClass().add("timKiemBox");
        box.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");

        HBox filterBtnGroup = createFilterButtons();
        HBox checkInOutBox = createCheckInOutBox();

        box.getChildren().addAll(filterBtnGroup, checkInOutBox);
        return box;
    }

    private HBox createFilterButtons() {
        HBox filterBtnGroup = new HBox();
        filterBtnGroup.setSpacing(10);
        filterBtnGroup.setAlignment(Pos.CENTER_LEFT);
        filterBtnGroup.setPrefWidth(600);
        filterBtnGroup.setPadding(new Insets(20));

        tatCaPhong = new Button("Tất cả phòng");
        tatCaPhong.getStyleClass().add("buttonfilter");
        phongVip = new Button("Vip");
        phongVip.getStyleClass().add("buttonfilter");
        phongThuong = new Button("Thường");
        phongThuong.getStyleClass().add("buttonfilter");

        // mặc định active
        tatCaPhong.getStyleClass().add("active");

        filters = new Button[] { tatCaPhong, phongVip, phongThuong };
        setupFilterBehavior(filters);

        filterBtnGroup.getChildren().addAll(tatCaPhong, phongVip, phongThuong);
        return filterBtnGroup;
    }

    private HBox createCheckInOutBox() {
        HBox checkInOutBox = new HBox(20);
        checkInOutBox.setAlignment(Pos.CENTER_LEFT);
        checkInOutBox.setPadding(new Insets(20));

        // ===================== CHECK IN =====================
        VBox checkIn = new VBox(6);
        Label lblCheckIn = new Label("Check in");
        lblCheckIn.setFont(Font.font("Segoe UI", 14));

        DatePicker checkInDatePicker = new DatePicker();
        checkInDatePicker.getStyleClass().add("date-picker-airbnb");
        checkInDatePicker.setPrefWidth(220);
        checkInDatePicker.setValue(LocalDate.now()); // Mặc định ngày trong mockup
        
        // StringConverter để hiển thị định dạng ngày ngắn gọn như mockup
        checkInDatePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd/MM/YYYY", java.util.Locale.forLanguageTag("vi-VN"));
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

        // Time picker cho check-in
        HBox checkInTimeBox = createTimePicker("14:00");
        
        checkIn.getChildren().addAll(lblCheckIn, checkInDatePicker, checkInTimeBox);

        // ===================== CHECK OUT =====================
        VBox checkOut = new VBox(6);
        Label lblCheckOut = new Label("Check out");
        lblCheckOut.setFont(Font.font("Segoe UI", 14));

        DatePicker checkOutDatePicker = new DatePicker();
        checkOutDatePicker.getStyleClass().add("date-picker-airbnb");
        checkOutDatePicker.setPrefWidth(220);
        checkOutDatePicker.setValue(LocalDate.now()); // Mặc định ngày trong mockup
        
        // StringConverter để hiển thị định dạng ngày ngắn gọn như mockup
        checkOutDatePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY", java.util.Locale.forLanguageTag("vi-VN"));
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

        // Time picker cho check-out
        HBox checkOutTimeBox = createTimePicker("16:00");

        checkOut.getChildren().addAll(lblCheckOut, checkOutDatePicker, checkOutTimeBox);

        // Button Tìm kiếm
        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefSize(100, 40);
        btnTimKiem.getStyleClass().add("button-search");

        checkInOutBox.getChildren().addAll(checkIn, checkOut, btnTimKiem);

        return checkInOutBox;
    }

    private HBox createTimePicker(String defaultTime) {
        HBox timePickerContainer = new HBox();
        timePickerContainer.setAlignment(Pos.CENTER_LEFT);
        timePickerContainer.setPadding(new Insets(5, 0, 0, 0));
        
        // Text field để hiển thị thời gian đã chọn (giống DatePicker)
        TextField timeDisplay = new TextField(defaultTime);
        timeDisplay.setPrefWidth(220); // Giống DatePicker width
        timeDisplay.setEditable(false);
        timeDisplay.setPrefHeight(20);
        timeDisplay.getStyleClass().add("date-picker-airbnb"); // Dùng style giống DatePicker
        
        // Popup để chọn thời gian (giống DatePicker popup)
        Popup timePopup = new Popup();
        timePopup.setAutoHide(true);
        
        // Content của popup
        VBox popupContent = new VBox(10);
        popupContent.setPadding(new Insets(15));
        popupContent.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #DDDDDD;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 3);"
        );
        
        // HBox chứa 2 cột: giờ và phút
        HBox timeSelectionBox = new HBox(10);
        timeSelectionBox.setAlignment(Pos.CENTER);
        
        // Cột giờ (1-24)
        VBox hourBox = new VBox(5);
        Label hourLabel = new Label("Giờ");
        hourLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        // VBox chứa buttons cho giờ thay vì ListView
        VBox hourButtonBox = new VBox(2);
        hourButtonBox.setPrefWidth(60);
        hourButtonBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 4;");
        
        // ScrollPane để giới hạn chiều cao popup hiển thị 5 hàng (100px)
        ScrollPane hourScrollPane = new ScrollPane(hourButtonBox);
        hourScrollPane.setPrefHeight(100); // 5 hàng x 20px mỗi hàng
        hourScrollPane.setPrefWidth(60);
        hourScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        hourScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Ẩn thanh scroll
        hourScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        Button[] hourButtons = new Button[24];
        String[] hourTimeParts = defaultTime.split(":");
        String defaultHour = hourTimeParts.length > 0 ? hourTimeParts[0] : "14";
        
        // Tạo buttons cho giờ từ 1-24
        for (int hour = 1; hour <= 24; hour++) {
            String hourStr = String.format("%02d", hour);
            Button hourBtn = new Button(hourStr);
            hourBtn.setPrefWidth(50);
            hourBtn.setPrefHeight(20);
            hourBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");
            
            if (hourStr.equals(defaultHour)) {
                hourBtn.setStyle("-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
            }
            
            hourButtons[hour-1] = hourBtn;
            hourButtonBox.getChildren().add(hourBtn);
        }
        
        hourBox.getChildren().addAll(hourLabel, hourScrollPane);
        
        // Cột phút (00-59)
        VBox minuteBox = new VBox(5);
        Label minuteLabel = new Label("Phút");
        minuteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        // VBox chứa buttons cho phút thay vì ListView
        VBox minuteButtonBox = new VBox(2);
        minuteButtonBox.setPrefWidth(60);
        minuteButtonBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 4;");
        
        // ScrollPane để giới hạn chiều cao popup hiển thị 5 hàng (100px)
        ScrollPane minuteScrollPane = new ScrollPane(minuteButtonBox);
        minuteScrollPane.setPrefHeight(100); // 5 hàng x 20px mỗi hàng
        minuteScrollPane.setPrefWidth(60);
        minuteScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        minuteScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Ẩn thanh scroll
        minuteScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        Button[] minuteButtons = new Button[60];
        String defaultMinute = hourTimeParts.length > 1 ? hourTimeParts[1] : "00";
        
        // Tạo buttons cho phút từ 00-59
        for (int minute = 0; minute < 60; minute++) {
            String minuteStr = String.format("%02d", minute);
            Button minuteBtn = new Button(minuteStr);
            minuteBtn.setPrefWidth(50);
            minuteBtn.setPrefHeight(20);
            minuteBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");
            
            if (minuteStr.equals(defaultMinute)) {
                minuteBtn.setStyle("-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
            }
            
            minuteButtons[minute] = minuteBtn;
            minuteButtonBox.getChildren().add(minuteBtn);
        }
        
        minuteBox.getChildren().addAll(minuteLabel, minuteScrollPane);
        
        timeSelectionBox.getChildren().addAll(hourBox, minuteBox);
        
        // Variables để track selected values
        String[] selectedHour = {defaultHour};
        String[] selectedMinute = {defaultMinute};
        
        // Handle hour button clicks
        for (int i = 0; i < hourButtons.length; i++) {
            Button btn = hourButtons[i];
            btn.setOnAction(e -> {
                // Reset all hour buttons
                for (Button hourBtn : hourButtons) {
                    hourBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");
                }
                // Highlight selected button
                btn.setStyle("-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
                selectedHour[0] = btn.getText();
            });
        }
        
        // Handle minute button clicks  
        for (int i = 0; i < minuteButtons.length; i++) {
            Button btn = minuteButtons[i];
            btn.setOnAction(e -> {
                // Reset all minute buttons
                for (Button minuteBtn : minuteButtons) {
                    minuteBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 11px;");
                }
                // Highlight selected button
                btn.setStyle("-fx-background-color: #1366D9; -fx-text-fill: white; -fx-border-width: 0; -fx-font-size: 11px;");
                selectedMinute[0] = btn.getText();
            });
        }
        
        // Button OK để confirm selection
        Button okButton = new Button("Chọn");
        okButton.setPrefWidth(100);
        okButton.setStyle(
            "-fx-background-color: #1366D9;;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        
        // Handle OK button click
        okButton.setOnAction(e -> {
            if (selectedHour[0] != null && selectedMinute[0] != null) {
                String newTime = selectedHour[0] + ":" + selectedMinute[0];
                timeDisplay.setText(newTime);
                timePopup.hide();
            }
        });
        
        popupContent.getChildren().addAll(timeSelectionBox, okButton);
        timePopup.getContent().add(popupContent);
        
        // Show popup khi click vào TextField (giống DatePicker)
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

    private void stylePage() {
        String css = """
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

        // Apply CSS to scene khi component được add vào scene
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add("data:text/css," + css.replace("\n", "%0A"));
            }
        });
    }

    private void setupFilterBehavior(Button[] filters) {
        for (int i = 0; i < filters.length; i++) {
            Button btn = filters[i];
            btn.setOnAction(evt -> {
                for (Button f : filters) {
                    f.getStyleClass().remove("active");
                }
                if (!btn.getStyleClass().contains("active")) {
                    btn.getStyleClass().add("active");
                }
            });
        }
    }
}
