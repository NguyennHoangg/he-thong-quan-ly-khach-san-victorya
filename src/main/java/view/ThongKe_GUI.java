package view;

import controller.ThongKe_Controller;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.thongke.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javafx.scene.SnapshotParameters;
import javafx.stage.FileChooser;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import model.thongke.TableRowHoaDon;

/**
 * Trang Thống Kê - GUI với 7 tab thống kê
 * Sử dụng layout chung cho tất cả các tab:
 * - Filter thời gian (Ngày/Tuần/Tháng/Năm/Khoảng ngày)
 * - Hàng KPI (3-4 cards)
 * - Biểu đồ xu hướng + biểu đồ cơ cấu
 * - Bảng dữ liệu
 */
public class ThongKe_GUI extends BorderPane {

    private final ThongKe_Controller controller;
    private TimeFilter currentFilter;

    private static final java.time.format.DateTimeFormatter DATE_DISPLAY = java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/yyyy");
    private static final java.time.format.DateTimeFormatter DATE_FILE = java.time.format.DateTimeFormatter
            .ofPattern("yyyyMMdd");
    private static final String BTN_BLUE = "-fx-background-color:#3b82f6; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;";
    private static final String BTN_GREEN = "-fx-background-color:#22c55e; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;";

    // Style constants
    private static final String CARD_STYLE = "-fx-background-color: white; -fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);";
    private static final String KPI_VALUE_STYLE = "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;";
    private static final String KPI_LABEL_STYLE = "-fx-font-size: 13px; -fx-text-fill: #64748b;";
    private static final String KPI_SUB_STYLE = "-fx-font-size: 11px; -fx-text-fill: #94a3b8;";
    private static final String SECTION_TITLE_STYLE = "-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;";

    // Chart width control
    private static final double MIN_WIDTH_PER_CATEGORY = 70.0;
    private static final double MIN_CHART_WIDTH = 600.0;

    /**
     * Tạo cột tiền tệ (dùng chung cho tất cả bảng thống kê).
     */
    private <T> TableColumn<T, Double> taoCotTienTe(String tieuDe, String tenThuocTinh) {
        TableColumn<T, Double> col = new TableColumn<>(tieuDe);
        col.setCellValueFactory(new PropertyValueFactory<>(tenThuocTinh));
        col.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : ThongKe_Controller.dinhDangTien(item));
            }
        });
        return col;
    }

    /**
     * Tạo cột phần trăm (hiển thị dạng x.x%).
     */
    private <T> TableColumn<T, Double> taoCotPhanTram(String tieuDe, String tenThuocTinh) {
        TableColumn<T, Double> col = new TableColumn<>(tieuDe);
        col.setCellValueFactory(new PropertyValueFactory<>(tenThuocTinh));
        col.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.1f%%", item));
            }
        });
        return col;
    }

    public ThongKe_GUI() {
        this.controller = new ThongKe_Controller();
        this.currentFilter = TimeFilter.thisMonth();
        initUI();
    }

    private void initUI() {
        this.setStyle("-fx-background-color: #f1f5f9;");
        this.setPadding(new Insets(0));

        // Main TabPane
        TabPane tabPane = new TabPane();
        tabPane.setTabMinWidth(140);
        tabPane.setTabMaxWidth(180);
        tabPane.setTabMinHeight(36);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("thongke-tabpane");

        // Add 7 tabs
        tabPane.getTabs().addAll(
                createTabDoanhThu(),
                createTabDatPhong(),
                createTabPhong(),
                createTabKhachHang(),
                createTabNhanVien(),
                createTabHoaDon(),
                createTabKhuyenMai(),
                createTabDichVu());

        this.setCenter(tabPane);

        // Apply custom styles
        applyStyles();
    }

    private void applyStyles() {
        this.setStyle("-fx-background-color: #f1f5f9;");
    }

    // ============================================================
    // COMMON COMPONENTS
    // ============================================================

    private String getTimeLabelDisplay() {
        if (currentFilter == null)
            return "";
        LocalDate from = currentFilter.getFromDate();
        LocalDate to = currentFilter.getToDate();
        if (from == null || to == null)
            return "";
        if (from.equals(to)) {
            return from.format(DATE_DISPLAY);
        }
        return from.format(DATE_DISPLAY) + " - " + to.format(DATE_DISPLAY);
    }

    private String getTimeSuffix() {
        if (currentFilter == null)
            return "";
        LocalDate from = currentFilter.getFromDate();
        LocalDate to = currentFilter.getToDate();
        if (from == null || to == null)
            return "";
        if (from.equals(to)) {
            return "-" + from.format(DATE_FILE);
        }
        return "-" + from.format(DATE_FILE) + "-" + to.format(DATE_FILE);
    }

    private void saveNodeAsPng(String defaultName, javafx.scene.Node node) {
        try {
            SnapshotParameters params = new SnapshotParameters();
            javafx.scene.image.WritableImage wi = node.snapshot(params, null);
            BufferedImage bi = SwingFXUtils.fromFXImage(wi, null);
            if (bi == null)
                return;

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Lưu biểu đồ PNG");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
            chooser.setInitialFileName(defaultName + ".png");
            java.io.File outFile = chooser.showSaveDialog(node.getScene().getWindow());
            if (outFile == null)
                return;
            ImageIO.write(bi, "png", outFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lưu PNG lỗi: " + ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void saveTableAsXlsx(String defaultName, TableView<?> table) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Data");
            int rowIdx = 0;
            // header
            Row header = sheet.createRow(rowIdx++);
            for (int c = 0; c < table.getColumns().size(); c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(table.getColumns().get(c).getText());
            }
            // rows
            for (Object item : table.getItems()) {
                Row r = sheet.createRow(rowIdx++);
                for (int c = 0; c < table.getColumns().size(); c++) {
                    Object cellVal = table.getColumns().get(c).getCellData(table.getItems().indexOf(item));
                    Cell cell = r.createCell(c);
                    if (cellVal instanceof Number num) {
                        cell.setCellValue(num.doubleValue());
                    } else {
                        cell.setCellValue(cellVal == null ? "" : cellVal.toString());
                    }
                }
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Lưu bảng Excel");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
            chooser.setInitialFileName(defaultName + ".xlsx");
            java.io.File outFile = chooser.showSaveDialog(table.getScene().getWindow());
            if (outFile == null)
                return;

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outFile)) {
                wb.write(fos);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lưu Excel lỗi: " + ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    /**
     * Tạo filter thời gian chung
     */
    private HBox createTimeFilter(Runnable onFilterChange) {
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(15, 20, 15, 20));
        filterBox.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label lblFilter = new Label("Lọc theo:");
        lblFilter.setStyle("-fx-font-weight: 600; -fx-text-fill: #1e293b;");

        ToggleGroup timeGroup = new ToggleGroup();

        RadioButton rbDay = new RadioButton("Hôm nay");
        RadioButton rbWeek = new RadioButton("Tuần này");
        RadioButton rbMonth = new RadioButton("Tháng này");
        RadioButton rbYear = new RadioButton("Năm nay");
        RadioButton rbCustom = new RadioButton("Tùy chọn");

        rbDay.setToggleGroup(timeGroup);
        rbWeek.setToggleGroup(timeGroup);
        rbMonth.setToggleGroup(timeGroup);
        rbYear.setToggleGroup(timeGroup);
        rbCustom.setToggleGroup(timeGroup);
        rbMonth.setSelected(true);

        // Date pickers for custom range
        DatePicker dpFrom = new DatePicker(currentFilter.getFromDate());
        DatePicker dpTo = new DatePicker(currentFilter.getToDate());
        dpFrom.setPrefWidth(130);
        dpTo.setPrefWidth(130);
        dpFrom.setDisable(true);
        dpTo.setDisable(true);

        Label lblTo = new Label("đến");
        lblTo.setStyle("-fx-text-fill: #64748b;");

        Button btnApply = new Button("Áp dụng");
        btnApply.setStyle(
                "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        btnApply.setDisable(true);

        // Event handlers
        timeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == rbDay) {
                currentFilter = TimeFilter.today();
                dpFrom.setDisable(true);
                dpTo.setDisable(true);
                btnApply.setDisable(true);
                onFilterChange.run();
            } else if (newVal == rbWeek) {
                currentFilter = TimeFilter.thisWeek();
                dpFrom.setDisable(true);
                dpTo.setDisable(true);
                btnApply.setDisable(true);
                onFilterChange.run();
            } else if (newVal == rbMonth) {
                currentFilter = TimeFilter.thisMonth();
                dpFrom.setDisable(true);
                dpTo.setDisable(true);
                btnApply.setDisable(true);
                onFilterChange.run();
            } else if (newVal == rbYear) {
                currentFilter = TimeFilter.thisYear();
                dpFrom.setDisable(true);
                dpTo.setDisable(true);
                btnApply.setDisable(true);
                onFilterChange.run();
            } else if (newVal == rbCustom) {
                dpFrom.setDisable(false);
                dpTo.setDisable(false);
                btnApply.setDisable(false);
            }
        });

        btnApply.setOnAction(e -> {
            if (dpFrom.getValue() != null && dpTo.getValue() != null) {
                currentFilter = TimeFilter.custom(dpFrom.getValue(), dpTo.getValue());
                onFilterChange.run();
            }
        });

        filterBox.getChildren().addAll(
                lblFilter, rbDay, rbWeek, rbMonth, rbYear, rbCustom,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                dpFrom, lblTo, dpTo, btnApply);

        return filterBox;
    }

    /**
     * Tạo KPI card
     */
    private VBox createKpiCard(KpiItem kpi) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(16));
        card.setStyle(CARD_STYLE);
        card.setMinWidth(180);
        card.setMaxWidth(250);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lblLabel = new Label(kpi.getLabel());
        lblLabel.setStyle(KPI_LABEL_STYLE);

        Label lblValue = new Label(kpi.getValue());
        lblValue.setStyle(KPI_VALUE_STYLE);

        Label lblSub = new Label(kpi.getSubLabel());
        lblSub.setStyle(KPI_SUB_STYLE);

        card.getChildren().addAll(lblLabel, lblValue, lblSub);
        return card;
    }

    /**
     * Tạo hàng KPI
     */
    private HBox createKpiRow(List<KpiItem> kpis) {
        HBox row = new HBox(15);
        row.setPadding(new Insets(0, 20, 0, 20));
        row.setAlignment(Pos.CENTER_LEFT);

        for (KpiItem kpi : kpis) {
            row.getChildren().add(createKpiCard(kpi));
        }

        return row;
    }

    // ============================================================
    // CHART DATA GROUPING & SCROLLABLE HELPERS
    // ============================================================

    private static final int MAX_CHART_COLUMNS = 12; // Tối đa 12 cột trên biểu đồ

    private boolean isSingleDay(TimeFilter tf) {
        if (tf == null)
            return false;
        LocalDate from = tf.getFromDate();
        LocalDate to = tf.getToDate();
        if (from == null || to == null)
            return false;
        long daysBetween = ChronoUnit.DAYS.between(from, to);
        return from.equals(to) || daysBetween == 0 || tf.getMode() == TimeFilter.TimeMode.DAY;
    }

    /**
     * Chuẩn hóa dữ liệu theo giờ cho 1 ngày, lấp đầy đủ 24 giờ.
     */
    private List<TimeSeriesPoint> buildHourlySeries(List<TimeSeriesPoint> data, LocalDate day) {
        java.util.Map<Integer, Double> map = new java.util.HashMap<>();
        if (data != null) {
            for (TimeSeriesPoint p : data) {
                if (p.getHour() != null) {
                    map.merge(p.getHour(), p.getValue(), Double::sum);
                }
            }
        }
        List<TimeSeriesPoint> result = new java.util.ArrayList<>(24);
        for (int h = 0; h < 24; h++) {
            double value = map.getOrDefault(h, 0.0);
            String label = String.format("%02d:00", h);
            result.add(new TimeSeriesPoint(day, label, value, 1, h));
        }
        return result;
    }

    /**
     * Gom nhóm dữ liệu theo giờ thành tối đa MAX_CHART_COLUMNS cột.
     */
    private List<TimeSeriesPoint> groupHourlySeries(List<TimeSeriesPoint> data, LocalDate day) {
        // Chuẩn hóa trước để luôn có 24 giờ đầy đủ
        List<TimeSeriesPoint> fullHours = buildHourlySeries(data, day);
        int totalHours = 24;
        int interval = (int) Math.ceil((double) totalHours / MAX_CHART_COLUMNS);
        if (interval < 1)
            interval = 1;

        List<TimeSeriesPoint> grouped = new java.util.ArrayList<>();
        for (int start = 0; start < totalHours; start += interval) {
            int end = Math.min(23, start + interval - 1);
            double sum = 0;
            for (int h = start; h <= end; h++) {
                // fullHours đã được sắp xếp theo giờ từ 0 -> 23
                TimeSeriesPoint p = fullHours.get(h);
                sum += p.getValue();
            }
            String label = (interval == 1)
                    ? String.format("%02dh", start)
                    : String.format("%02d-%02dh", start, end);
            grouped.add(new TimeSeriesPoint(day, label, sum, interval, start));
        }
        return grouped;
    }

    /**
     * Gom nhóm dữ liệu time series theo khoảng thời gian để tối đa
     * MAX_CHART_COLUMNS cột
     * VD: 30 ngày -> gom mỗi 3 ngày -> 10 cột
     */
    /**
     * Gom nhóm dữ liệu time series thành tối đa 12 cột, lấp đầy khoảng trống (giá
     * trị 0)
     * dựa trên from/to của currentFilter để không bỏ sót ngày nào.
     */
    private List<TimeSeriesPoint> groupTimeSeriesData(List<TimeSeriesPoint> data, TimeFilter tf) {
        if (data == null)
            data = List.of();

        // Trường hợp lọc theo ngày (1 ngày duy nhất): hiển thị theo giờ, gom nhóm tối
        // đa 12 cột
        if (tf != null && isSingleDay(tf)) {
            LocalDate from = tf.getFromDate();
            if (from != null) {
                return groupHourlySeries(data, from);
            }
        }

        // Xác định khoảng thời gian
        LocalDate fromDate = tf != null ? tf.getFromDate() : null;
        LocalDate toDate = tf != null ? tf.getToDate() : null;
        if (fromDate == null || toDate == null) {
            if (data.isEmpty())
                return data;
            LocalDate min = data.stream().map(TimeSeriesPoint::getDate).filter(d -> d != null).min(LocalDate::compareTo)
                    .orElse(null);
            LocalDate max = data.stream().map(TimeSeriesPoint::getDate).filter(d -> d != null).max(LocalDate::compareTo)
                    .orElse(null);
            fromDate = fromDate == null ? min : fromDate;
            toDate = toDate == null ? max : toDate;
        }
        if (fromDate == null || toDate == null)
            return data;

        long days = ChronoUnit.DAYS.between(fromDate, toDate) + 1;

        // Nếu chỉ có 1 ngày, trả về single day ngay lập tức
        if (days <= 1) {
            final LocalDate targetDate = fromDate; // Final để dùng trong lambda
            double sum = data.stream()
                    .filter(p -> p.getDate() != null && p.getDate().equals(targetDate))
                    .mapToDouble(TimeSeriesPoint::getValue)
                    .sum();
            String label = targetDate.getDayOfMonth() + "/" + targetDate.getMonthValue();
            return List.of(new TimeSeriesPoint(targetDate, label, sum, 1));
        }

        // Tiếp tục xử lý nhiều ngày
        LocalDate from = fromDate;
        LocalDate to = toDate;

        int interval = (int) Math.ceil((double) days / MAX_CHART_COLUMNS);
        if (interval < 1)
            interval = 1;

        // Map ngày -> giá trị
        java.util.Map<LocalDate, Double> map = new java.util.HashMap<>();
        for (TimeSeriesPoint p : data) {
            if (p.getDate() != null) {
                map.merge(p.getDate(), p.getValue(), Double::sum);
            }
        }

        List<TimeSeriesPoint> grouped = new java.util.ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            LocalDate bucketEnd = current.plusDays(interval - 1);
            if (bucketEnd.isAfter(to))
                bucketEnd = to;

            double sum = 0;
            LocalDate walker = current;
            while (!walker.isAfter(bucketEnd)) {
                sum += map.getOrDefault(walker, 0.0);
                walker = walker.plusDays(1);
            }

            String label;
            if (current.equals(bucketEnd)) {
                label = current.getDayOfMonth() + "/" + current.getMonthValue();
            } else if (current.getMonthValue() == bucketEnd.getMonthValue()) {
                label = current.getDayOfMonth() + "-" + bucketEnd.getDayOfMonth() + "/" + current.getMonthValue();
            } else {
                label = current.getDayOfMonth() + "/" + current.getMonthValue() + "-" +
                        bucketEnd.getDayOfMonth() + "/" + bucketEnd.getMonthValue();
            }

            grouped.add(new TimeSeriesPoint(current, label, sum, interval));
            current = bucketEnd.plusDays(1);
        }

        return grouped;
    }

    /**
     * Tạo Bar Chart cho dữ liệu theo thời gian (khởi tạo trống, gán dữ liệu sau)
     */
    private BarChart<String, Number> createTimeBarChart(String title) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#64748b"));
        yAxis.setTickLabelFill(Color.web("#64748b"));

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        chart.setPrefHeight(300);
        chart.setMinHeight(300);
        chart.setStyle("-fx-background-color: transparent;");

        xAxis.setStartMargin(10);
        xAxis.setEndMargin(10);
        chart.setCategoryGap(10);
        chart.setBarGap(2);
        xAxis.setTickLabelRotation(-45);

        return chart;
    }

    /**
     * Gán dữ liệu cho BarChart thời gian, giữ nguyên thứ tự và nhãn gốc.
     */
    private void updateTimeBarChart(BarChart<String, Number> chart, List<TimeSeriesPoint> data) {
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        // Reset thật sạch trục X và dữ liệu cũ trước khi gán mới
        xAxis.getCategories().clear();
        chart.getData().clear();

        List<String> categories = new java.util.ArrayList<>();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Xử lý dữ liệu
        if (data != null && !data.isEmpty()) {
            for (TimeSeriesPoint point : data) {
                String label = point.getLabel() != null && !point.getLabel().isEmpty()
                        ? point.getLabel()
                        : (point.getDate() != null
                                ? point.getDate().getDayOfMonth() + "/" + point.getDate().getMonthValue()
                                : "");
                if (label.isEmpty()) {
                    continue; // Bỏ qua điểm không có label
                }
                // Đảm bảo giá trị >= 0 (kể cả 0) để cột luôn hiển thị
                double value = Math.max(0, point.getValue());
                series.getData().add(new XYChart.Data<>(label, value));
                categories.add(label);
            }
        }

        // Nếu không có dữ liệu và đang filter theo ngày, tạo 1 cột với giá trị 0
        if (categories.isEmpty() && currentFilter != null) {
            LocalDate from = currentFilter.getFromDate();
            LocalDate to = currentFilter.getToDate();
            boolean isSingleDay = (currentFilter.getMode() == TimeFilter.TimeMode.DAY) ||
                    (from != null && to != null && from.equals(to));

            if (isSingleDay && from != null) {
                String label = from.getDayOfMonth() + "/" + from.getMonthValue();
                series.getData().add(new XYChart.Data<>(label, 0));
                categories.add(label);
            }
        }

        // Đảm bảo categories không rỗng
        if (categories.isEmpty()) {
            categories.add("Không có dữ liệu");
            series.getData().add(new XYChart.Data<>("Không có dữ liệu", 0));
        }

        // Clear và set dữ liệu mới
        chart.getData().add(series);
        xAxis.setCategories(FXCollections.observableArrayList(categories));

        // Đảm bảo width tối thiểu cho biểu đồ (đặc biệt quan trọng khi chỉ có 1 cột)
        int categoryCount = Math.max(1, categories.size());
        double width = Math.max(MIN_CHART_WIDTH, categoryCount * MIN_WIDTH_PER_CATEGORY);
        chart.setMinWidth(width);
        chart.setPrefWidth(width);

        // Đảm bảo bar gap phù hợp khi chỉ có 1 cột
        if (categoryCount == 1) {
            chart.setCategoryGap(50); // Tăng gap để cột không quá nhỏ
        } else {
            chart.setCategoryGap(10);
        }

        // Reset và cập nhật Y-axis để tự động scale lại khi dữ liệu thay đổi
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        if (series.getData().isEmpty()) {
            // Không có dữ liệu
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(1);
            yAxis.setTickUnit(0.2);
            yAxis.setAutoRanging(false);
        } else {
            // Tính toán min/max từ dữ liệu mới
            double minValue = series.getData().stream()
                    .mapToDouble(d -> d.getYValue().doubleValue())
                    .min()
                    .orElse(0);
            double maxValue = series.getData().stream()
                    .mapToDouble(d -> d.getYValue().doubleValue())
                    .max()
                    .orElse(0);

            // Nếu tất cả giá trị đều = 0, set upper bound = 1 để đảm bảo cột vẫn hiển thị
            if (maxValue == 0) {
                yAxis.setLowerBound(0);
                yAxis.setUpperBound(1);
                yAxis.setTickUnit(0.2);
                yAxis.setAutoRanging(false);
            } else {
                // Tự động scale Y-axis với padding 10% ở trên
                double padding = maxValue * 0.1;
                yAxis.setLowerBound(Math.max(0, minValue - padding));
                yAxis.setUpperBound(maxValue + padding);

                // Tính tick unit phù hợp (chia thành khoảng 5-10 mức)
                double range = (maxValue + padding) - Math.max(0, minValue - padding);
                double tickUnit = range / 10;
                // Làm tròn tick unit để đẹp hơn
                double magnitude = Math.pow(10, Math.floor(Math.log10(tickUnit)));
                tickUnit = Math.ceil(tickUnit / magnitude) * magnitude;
                yAxis.setTickUnit(tickUnit);

                yAxis.setAutoRanging(false);
            }
        }
    }

    /**
     * Tạo Bar Chart đơn giản (khởi tạo trống, gán dữ liệu sau)
     */
    private BarChart<String, Number> createBarChart(String title) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#64748b"));
        yAxis.setTickLabelFill(Color.web("#64748b"));

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        chart.setPrefHeight(300);
        chart.setMinHeight(300);
        chart.setStyle("-fx-background-color: transparent;");

        xAxis.setStartMargin(10);
        xAxis.setEndMargin(10);
        chart.setCategoryGap(10);
        chart.setBarGap(2);
        xAxis.setTickLabelRotation(-45);

        return chart;
    }

    /**
     * Gán dữ liệu cho BarChart nhóm, giữ nguyên thứ tự và nhãn gốc.
     */
    private void updateGroupBarChart(BarChart<String, Number> chart, List<GroupSeriesPoint> data) {
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        // Reset thật sạch trục X và dữ liệu cũ trước khi gán mới
        xAxis.getCategories().clear();
        chart.getData().clear();

        List<String> categories = new java.util.ArrayList<>();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (GroupSeriesPoint point : data) {
            String label = point.getGroupName();
            series.getData().add(new XYChart.Data<>(label, point.getValue()));
            categories.add(label);
        }

        chart.getData().setAll(series);
        xAxis.setCategories(FXCollections.observableArrayList(categories));

        double width = Math.max(MIN_CHART_WIDTH, categories.size() * MIN_WIDTH_PER_CATEGORY);
        chart.setMinWidth(width);
        chart.setPrefWidth(width);

        // Reset và cập nhật Y-axis để tự động scale lại khi dữ liệu thay đổi
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        if (series.getData().isEmpty()) {
            // Không có dữ liệu
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(1);
            yAxis.setTickUnit(0.2);
            yAxis.setAutoRanging(false);
        } else {
            // Tính toán min/max từ dữ liệu mới
            double minValue = series.getData().stream()
                    .mapToDouble(d -> d.getYValue().doubleValue())
                    .min()
                    .orElse(0);
            double maxValue = series.getData().stream()
                    .mapToDouble(d -> d.getYValue().doubleValue())
                    .max()
                    .orElse(0);

            // Nếu tất cả giá trị đều = 0, set upper bound = 1 để đảm bảo cột vẫn hiển thị
            if (maxValue == 0) {
                yAxis.setLowerBound(0);
                yAxis.setUpperBound(1);
                yAxis.setTickUnit(0.2);
                yAxis.setAutoRanging(false);
            } else {
                // Tự động scale Y-axis với padding 10% ở trên
                double padding = maxValue * 0.1;
                yAxis.setLowerBound(Math.max(0, minValue - padding));
                yAxis.setUpperBound(maxValue + padding);

                // Tính tick unit phù hợp (chia thành khoảng 5-10 mức)
                double range = (maxValue + padding) - Math.max(0, minValue - padding);
                double tickUnit = range / 10;
                // Làm tròn tick unit để đẹp hơn
                double magnitude = Math.pow(10, Math.floor(Math.log10(tickUnit)));
                tickUnit = Math.ceil(tickUnit / magnitude) * magnitude;
                yAxis.setTickUnit(tickUnit);

                yAxis.setAutoRanging(false);
            }
        }
    }

    /**
     * Tạo Pie Chart
     */
    private PieChart createPieChart(String title, List<GroupSeriesPoint> data) {
        PieChart chart = new PieChart();
        chart.setTitle(title);
        chart.setPrefHeight(280);
        chart.setLabelsVisible(true);
        chart.setStyle("-fx-background-color: transparent;");

        double total = data.stream().mapToDouble(GroupSeriesPoint::getValue).sum();
        for (GroupSeriesPoint point : data) {
            double percent = total > 0 ? (point.getValue() / total * 100) : 0;
            String label = String.format("%s (%.1f%%)", point.getGroupName(), percent);
            chart.getData().add(new PieChart.Data(label, point.getValue()));
        }

        return chart;
    }

    /**
     * Tạo container cho chart (không scroll)
     */
    private VBox createChartContainer(String title, javafx.scene.Node chart, String fileBaseName) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(16));
        container.setStyle(CARD_STYLE);
        HBox.setHgrow(container, Priority.ALWAYS);

        Label lblTitle = new Label(title);
        lblTitle.setStyle(SECTION_TITLE_STYLE);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().add(lblTitle);

        if (fileBaseName != null) {
            Button btnSave = new Button("Lưu PNG");
            btnSave.setStyle(BTN_BLUE);
            btnSave.setOnAction(e -> saveNodeAsPng(fileBaseName + getTimeSuffix(), chart));
            header.getChildren().add(btnSave);
        }

        container.getChildren().addAll(header, chart);
        return container;
    }

    // ============================================================
    // TAB 1: DOANH THU
    // ============================================================

    private Tab createTabDoanhThu() {
        Tab tab = new Tab("📊 Doanh thu");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f1f5f9;");

        // Components references for update
        HBox kpiRow = new HBox();
        BarChart<String, Number> timeChart = createTimeBarChart("");
        BarChart<String, Number> barChart = createBarChart("");
        PieChart pieRevenue = createPieChart("", List.of());
        TableView<TableRowDoanhThu> table = createTableDoanhThu();

        // Update function
        Runnable updateData = () -> {
            // Update KPIs
            List<KpiItem> kpis = controller.getKpiDoanhThu(currentFilter);
            kpiRow.getChildren().clear();
            kpiRow.getChildren().addAll(createKpiRow(kpis).getChildren());

            // RESET & cập nhật biểu đồ doanh thu theo thời gian thật sạch
            // Xóa toàn bộ series cũ trước khi vẽ mới
            timeChart.getData().clear();

            // Gom nhóm lại hoàn toàn theo filter hiện tại
            boolean singleDay = isSingleDay(currentFilter);
            List<TimeSeriesPoint> rawData = singleDay
                    ? controller.getChartDoanhThuTheoGio(currentFilter)
                    : controller.getChartDoanhThuTheoNgay(currentFilter);
            List<TimeSeriesPoint> groupedData = singleDay
                    ? groupHourlySeries(rawData, currentFilter.getFromDate())
                    : groupTimeSeriesData(rawData, currentFilter);
            updateTimeBarChart(timeChart, groupedData);

            // Update bar chart (top 5 nhân viên)
            List<GroupSeriesPoint> barData = controller.getTop5DoanhThuTheoNhanVien(currentFilter);
            updateGroupBarChart(barChart, barData);

            // Update revenue composition pie chart
            pieRevenue.getData().clear();
            List<GroupSeriesPoint> revMix = controller.getChartDoanhThuPhongVsDichVu(currentFilter);
            double totalRev = revMix.stream().mapToDouble(GroupSeriesPoint::getValue).sum();
            for (GroupSeriesPoint p : revMix) {
                double percent = totalRev > 0 ? (p.getValue() / totalRev * 100) : 0;
                pieRevenue.getData().add(new PieChart.Data(
                        String.format("%s (%.0f%%)", p.getGroupName(), percent), p.getValue()));
            }

            // Update table
            table.setItems(FXCollections.observableArrayList(controller.getTableDoanhThu(currentFilter)));
        };

        // Time filter
        HBox filterBox = createTimeFilter(updateData);
        // KPI row setup
        kpiRow.setSpacing(15);
        kpiRow.setPadding(new Insets(0, 20, 0, 20));

        // Chart setup
        timeChart.setTitle("Doanh thu theo thời gian");
        timeChart.setLegendVisible(false);
        barChart.setTitle("Top 5 nhân viên");
        barChart.setLegendVisible(false);
        pieRevenue.setTitle("Cơ cấu doanh thu");

        VBox timeContainer = createChartContainer("Doanh thu theo thời gian", timeChart,
                "doanh-thu-theo-thoi-gian");
        VBox barContainer = createChartContainer("Top 5 nhân viên theo doanh thu", barChart,
                "top-5-nhan-vien");
        VBox pieContainer = createChartContainer("Cơ cấu doanh thu (phòng vs dịch vụ)", pieRevenue,
                "co-cau-doanh-thu");
        timeContainer.setMaxWidth(Double.MAX_VALUE);
        barContainer.setMaxWidth(Double.MAX_VALUE);
        pieContainer.setMaxWidth(Double.MAX_VALUE);

        // Hàng 1: Biểu đồ doanh thu theo thời gian (full width)
        HBox timeRow = new HBox(timeContainer);
        timeRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(timeContainer, Priority.ALWAYS);

        // Hàng 2: Biểu đồ doanh thu theo nhân viên và cơ cấu doanh thu (50/50)
        HBox chartRow = new HBox(15);
        chartRow.setPadding(new Insets(0, 20, 0, 20));
        barContainer.prefWidthProperty().bind(chartRow.widthProperty().subtract(55).divide(2));
        pieContainer.prefWidthProperty().bind(chartRow.widthProperty().subtract(55).divide(2));
        chartRow.getChildren().addAll(barContainer, pieContainer);

        // Table container
        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setStyle(CARD_STYLE);
        HBox tableHeader = new HBox(10);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        Label tableTitle = new Label("Bảng doanh thu theo ngày (" + getTimeLabelDisplay() + ")");
        tableTitle.setStyle(SECTION_TITLE_STYLE);
        Button btnXlsx = new Button("Lưu Excel");
        btnXlsx.setStyle(BTN_GREEN);
        btnXlsx.setOnAction(e -> saveTableAsXlsx("bang-doanh-thu" + getTimeSuffix(), table));
        tableHeader.getChildren().addAll(tableTitle, btnXlsx);
        tableContainer.getChildren().addAll(tableHeader, table);
        HBox tableRow = new HBox(tableContainer);
        tableRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox actionsRow = new HBox(10, filterBox);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(actionsRow, kpiRow, timeRow, chartRow, tableRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        tab.setContent(scrollPane);

        // Initial load
        updateData.run();

        return tab;
    }

    private TableView<TableRowDoanhThu> createTableDoanhThu() {
        TableView<TableRowDoanhThu> table = new TableView<>();
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRowDoanhThu, LocalDate> colNgay = new TableColumn<>("Ngày");
        colNgay.setCellValueFactory(new PropertyValueFactory<>("ngay"));

        TableColumn<TableRowDoanhThu, Integer> colSoHD = new TableColumn<>("Số HĐ");
        colSoHD.setCellValueFactory(new PropertyValueFactory<>("soHoaDon"));

        TableColumn<TableRowDoanhThu, Double> colPhong = taoCotTienTe("Tổng doanh thu phòng", "doanhThuPhong");
        TableColumn<TableRowDoanhThu, Double> colDv = taoCotTienTe("Tổng doanh thu dịch vụ", "doanhThuDichVu");
        TableColumn<TableRowDoanhThu, Double> colTong = taoCotTienTe("Doanh thu tổng", "doanhThuTong");

        table.getColumns().addAll(colNgay, colSoHD, colPhong, colDv, colTong);
        return table;
    }

    // ============================================================
    // TAB 2: ĐẶT PHÒNG
    // ============================================================

    private Tab createTabDatPhong() {
        Tab tab = new Tab("🛏️ Đặt phòng");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f1f5f9;");

        HBox kpiRow = new HBox();
        BarChart<String, Number> timeChart = createTimeBarChart("");
        PieChart pieChart = createPieChart("", List.of());
        TableView<TableRowDatPhong> table = createTableDatPhong();

        Runnable updateData = () -> {
            List<KpiItem> kpis = controller.getKpiDatPhong(currentFilter);
            kpiRow.getChildren().clear();
            kpiRow.getChildren().addAll(createKpiRow(kpis).getChildren());

            // Gom nhóm dữ liệu nếu > 12 ngày
            boolean singleDay = isSingleDay(currentFilter);
            List<TimeSeriesPoint> rawData = singleDay
                    ? controller.getChartPhieuTheoGio(currentFilter)
                    : controller.getChartPhieuTheoNgay(currentFilter);
            List<TimeSeriesPoint> groupedData = singleDay
                    ? groupHourlySeries(rawData, currentFilter.getFromDate())
                    : groupTimeSeriesData(rawData, currentFilter);
            updateTimeBarChart(timeChart, groupedData);

            List<GroupSeriesPoint> pieData = controller.getChartTheoLoaiDatPhong(currentFilter);
            pieChart.getData().clear();
            double total = pieData.stream().mapToDouble(GroupSeriesPoint::getValue).sum();
            for (GroupSeriesPoint p : pieData) {
                double percent = total > 0 ? (p.getValue() / total * 100) : 0;
                pieChart.getData().add(new PieChart.Data(
                        String.format("%s (%.0f%%)", p.getGroupName(), percent), p.getValue()));
            }

            table.setItems(FXCollections.observableArrayList(controller.getTableDatPhong(currentFilter)));
        };

        HBox filterBox = createTimeFilter(updateData);
        kpiRow.setSpacing(15);
        kpiRow.setPadding(new Insets(0, 20, 0, 20));

        timeChart.setTitle("Số phiếu đặt theo thời gian");
        pieChart.setTitle("Loại đặt phòng");

        HBox chartRow = new HBox(15);
        chartRow.setPadding(new Insets(0, 20, 0, 20));
        VBox timeContainer = createChartContainer("Số lượng đặt phòng theo thời gian", timeChart,
                "dat-phong-theo-thoi-gian");
        VBox pieContainer = createChartContainer("Phân bổ theo loại đặt phòng", pieChart,
                "loai-dat-phong");
        timeContainer.prefWidthProperty().bind(chartRow.widthProperty().subtract(55).divide(2));
        pieContainer.prefWidthProperty().bind(chartRow.widthProperty().subtract(55).divide(2));
        timeContainer.setMaxWidth(Double.MAX_VALUE);
        pieContainer.setMaxWidth(Double.MAX_VALUE);
        chartRow.getChildren().addAll(timeContainer, pieContainer);

        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setStyle(CARD_STYLE);
        HBox tableHeader = new HBox(10);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        Label tblTitle = new Label("Danh sách phiếu đặt phòng (" + getTimeLabelDisplay() + ")");
        tblTitle.setStyle(SECTION_TITLE_STYLE);
        Button tblXlsx = new Button("Lưu Excel");
        tblXlsx.setStyle(BTN_GREEN);
        tblXlsx.setOnAction(e -> saveTableAsXlsx("phieu-dat-phong" + getTimeSuffix(), table));
        tableHeader.getChildren().addAll(tblTitle, tblXlsx);
        tableContainer.getChildren().addAll(tableHeader, table);
        HBox tableRow = new HBox(tableContainer);
        tableRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox actionsRow = new HBox(10, filterBox);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(actionsRow, kpiRow, chartRow, tableRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        tab.setContent(scrollPane);

        updateData.run();
        return tab;
    }

    private TableView<TableRowDatPhong> createTableDatPhong() {
        TableView<TableRowDatPhong> table = new TableView<>();
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRowDatPhong, String> colMa = new TableColumn<>("Mã phiếu");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maPhieuDatPhong"));

        TableColumn<TableRowDatPhong, LocalDate> colNgay = new TableColumn<>("Ngày tạo");
        colNgay.setCellValueFactory(new PropertyValueFactory<>("ngayTao"));

        TableColumn<TableRowDatPhong, String> colKhach = new TableColumn<>("Khách hàng");
        colKhach.setCellValueFactory(new PropertyValueFactory<>("tenKhach"));

        TableColumn<TableRowDatPhong, String> colTT = new TableColumn<>("Trạng thái");
        colTT.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        TableColumn<TableRowDatPhong, Double> colCoc = taoCotTienTe("Tiền cọc", "tienDatCoc");

        table.getColumns().addAll(colMa, colNgay, colKhach, colTT, colCoc);
        return table;
    }

    // ============================================================
    // TAB 3: PHÒNG
    // ============================================================

    private Tab createTabPhong() {
        Tab tab = new Tab("🏠 Phòng");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f1f5f9;");

        HBox kpiRow = new HBox();
        PieChart pieChart = createPieChart("", List.of());
        BarChart<String, Number> barChart = createBarChart("");
        TableView<TableRowPhong> table = createTablePhong();

        Runnable updateData = () -> {
            List<KpiItem> kpis = controller.getKpiPhong(currentFilter);
            kpiRow.getChildren().clear();
            kpiRow.getChildren().addAll(createKpiRow(kpis).getChildren());

            List<GroupSeriesPoint> pieData = controller.getChartPhongTheoTrangThai();
            pieChart.getData().clear();
            double total = pieData.stream().mapToDouble(GroupSeriesPoint::getValue).sum();
            for (GroupSeriesPoint p : pieData) {
                double percent = total > 0 ? (p.getValue() / total * 100) : 0;
                pieChart.getData().add(new PieChart.Data(
                        String.format("%s (%.0f%%)", p.getGroupName(), percent), p.getValue()));
            }

            List<GroupSeriesPoint> barData = controller.getChartPhongTheoLoai();
            updateGroupBarChart(barChart, barData);

            table.setItems(FXCollections.observableArrayList(controller.getTablePhong(currentFilter)));
        };

        HBox filterBox = createTimeFilter(updateData);
        kpiRow.setSpacing(15);
        kpiRow.setPadding(new Insets(0, 20, 0, 20));

        pieChart.setTitle("Trạng thái phòng");
        barChart.setTitle("Phòng theo loại");

        HBox chartRow = new HBox(15);
        chartRow.setPadding(new Insets(0, 20, 0, 20));
        VBox pieContainer = createChartContainer("Trạng thái phòng hiện tại", pieChart,
                "trang-thai-phong");
        VBox barContainer = createChartContainer("Phân bố theo loại phòng", barChart,
                "phong-theo-loai");
        pieContainer.prefWidthProperty().bind(chartRow.widthProperty().subtract(55).divide(2));
        barContainer.prefWidthProperty().bind(chartRow.widthProperty().subtract(55).divide(2));
        pieContainer.setMaxWidth(Double.MAX_VALUE);
        barContainer.setMaxWidth(Double.MAX_VALUE);
        chartRow.getChildren().addAll(pieContainer, barContainer);

        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setStyle(CARD_STYLE);
        HBox tableHeader = new HBox(10);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        Label tblTitle = new Label("Danh sách phòng (" + getTimeLabelDisplay() + ")");
        tblTitle.setStyle(SECTION_TITLE_STYLE);
        Button tblXlsx = new Button("Lưu Excel");
        tblXlsx.setStyle(BTN_GREEN);
        tblXlsx.setOnAction(e -> saveTableAsXlsx("danh-sach-phong" + getTimeSuffix(), table));
        tableHeader.getChildren().addAll(tblTitle, tblXlsx);
        tableContainer.getChildren().addAll(tableHeader, table);
        HBox tableRow = new HBox(tableContainer);
        tableRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox actionsRow = new HBox(10, filterBox);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(actionsRow, kpiRow, chartRow, tableRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        tab.setContent(scrollPane);

        updateData.run();
        return tab;
    }

    private TableView<TableRowPhong> createTablePhong() {
        TableView<TableRowPhong> table = new TableView<>();
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRowPhong, String> colMa = new TableColumn<>("Mã phòng");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maPhong"));

        TableColumn<TableRowPhong, String> colSo = new TableColumn<>("Số phòng");
        colSo.setCellValueFactory(new PropertyValueFactory<>("soPhong"));

        TableColumn<TableRowPhong, String> colLoai = new TableColumn<>("Loại phòng");
        colLoai.setCellValueFactory(new PropertyValueFactory<>("tenLoaiPhong"));

        TableColumn<TableRowPhong, Integer> colTang = new TableColumn<>("Tầng");
        colTang.setCellValueFactory(new PropertyValueFactory<>("tang"));

        TableColumn<TableRowPhong, String> colTT = new TableColumn<>("Trạng thái");
        colTT.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        TableColumn<TableRowPhong, String> colTinhTrang = new TableColumn<>("Tình trạng");
        colTinhTrang.setCellValueFactory(new PropertyValueFactory<>("tinhTrang"));

        TableColumn<TableRowPhong, Integer> colLuotDat = new TableColumn<>("Lượt đặt");
        colLuotDat.setCellValueFactory(new PropertyValueFactory<>("luotDat"));

        TableColumn<TableRowPhong, Double> colDoanhThu = taoCotTienTe("Doanh thu phòng", "doanhThuPhong");

        TableColumn<TableRowPhong, Integer> colLuotHuy = new TableColumn<>("Lượt hủy");
        colLuotHuy.setCellValueFactory(new PropertyValueFactory<>("luotHuy"));

        // Bảng chỉ hiển thị lượt đặt, lượt hủy và doanh thu phòng (bỏ công suất & tỷ lệ
        // hủy để đơn giản cho việc học)
        table.getColumns().addAll(colMa, colSo, colLoai, colTang, colTT, colTinhTrang,
                colLuotDat, colLuotHuy, colDoanhThu);
        return table;
    }

    // ============================================================
    // TAB 4: KHÁCH HÀNG
    // ============================================================

    private Tab createTabKhachHang() {
        Tab tab = new Tab("👥 Khách hàng");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f1f5f9;");

        HBox kpiRow = new HBox();
        BarChart<String, Number> timeChart = createTimeBarChart("");
        TableView<TableRowKhachHang> table = createTableKhachHang();

        Runnable updateData = () -> {
            List<KpiItem> kpis = controller.getKpiKhachHang(currentFilter);
            kpiRow.getChildren().clear();
            kpiRow.getChildren().addAll(createKpiRow(kpis).getChildren());

            // Gom nhóm dữ liệu nếu > 12 ngày
            boolean singleDay = isSingleDay(currentFilter);
            List<TimeSeriesPoint> rawData = singleDay
                    ? controller.getChartKhachMoiTheoGio(currentFilter)
                    : controller.getChartKhachMoiTheoNgay(currentFilter);
            List<TimeSeriesPoint> groupedData = singleDay
                    ? groupHourlySeries(rawData, currentFilter.getFromDate())
                    : groupTimeSeriesData(rawData, currentFilter);
            updateTimeBarChart(timeChart, groupedData);

            table.setItems(FXCollections.observableArrayList(
                    controller.getTableTopKhachHang(currentFilter, 10)));
        };

        HBox filterBox = createTimeFilter(updateData);
        kpiRow.setSpacing(15);
        kpiRow.setPadding(new Insets(0, 20, 0, 20));

        timeChart.setTitle("Khách hàng mới theo thời gian");

        HBox chartRow = new HBox(15);
        chartRow.setPadding(new Insets(0, 20, 0, 20));
        VBox chartContainer = createChartContainer("Số khách hàng mới", timeChart,
                "khach-hang-moi");
        HBox.setHgrow(chartContainer, Priority.ALWAYS);
        chartRow.getChildren().add(chartContainer);

        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setStyle(CARD_STYLE);
        HBox tableHeader = new HBox(10);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        Label tblTitle = new Label("Top 10 khách hàng theo doanh thu (" + getTimeLabelDisplay() + ")");
        tblTitle.setStyle(SECTION_TITLE_STYLE);
        Button tblXlsx = new Button("Lưu Excel");
        tblXlsx.setStyle(BTN_GREEN);
        tblXlsx.setOnAction(e -> saveTableAsXlsx("top-khach-hang" + getTimeSuffix(), table));
        tableHeader.getChildren().addAll(tblTitle, tblXlsx);
        tableContainer.getChildren().addAll(tableHeader, table);
        HBox tableRow = new HBox(tableContainer);
        tableRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox actionsRow = new HBox(10, filterBox);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(actionsRow, kpiRow, chartRow, tableRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        tab.setContent(scrollPane);

        updateData.run();
        return tab;
    }

    private TableView<TableRowKhachHang> createTableKhachHang() {
        TableView<TableRowKhachHang> table = new TableView<>();
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRowKhachHang, String> colMa = new TableColumn<>("Mã KH");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));

        TableColumn<TableRowKhachHang, String> colTen = new TableColumn<>("Họ tên");
        colTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));

        TableColumn<TableRowKhachHang, Integer> colSoHD = new TableColumn<>("Số HĐ");
        colSoHD.setCellValueFactory(new PropertyValueFactory<>("soHoaDon"));

        TableColumn<TableRowKhachHang, Double> colDoanhThu = taoCotTienTe("Tổng doanh thu", "tongDoanhThu");

        table.getColumns().addAll(colMa, colTen, colSoHD, colDoanhThu);
        return table;
    }

    // ============================================================
    // TAB 5: NHÂN VIÊN
    // ============================================================

    private Tab createTabNhanVien() {
        Tab tab = new Tab("👤 Nhân viên");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f1f5f9;");

        HBox kpiRow = new HBox();
        BarChart<String, Number> barChart1 = createBarChart("");
        BarChart<String, Number> barChart2 = createBarChart("");
        TableView<TableRowNhanVien> table = createTableNhanVien();

        Runnable updateData = () -> {
            List<KpiItem> kpis = controller.getKpiNhanVien(currentFilter);
            kpiRow.getChildren().clear();
            kpiRow.getChildren().addAll(createKpiRow(kpis).getChildren());

            List<GroupSeriesPoint> barData1 = controller.getChartDoanhThuNhanVien(currentFilter);
            updateGroupBarChart(barChart1, barData1);

            List<GroupSeriesPoint> barData2 = controller.getChartCaTheoNhanVien(currentFilter);
            updateGroupBarChart(barChart2, barData2);

            table.setItems(FXCollections.observableArrayList(controller.getTableNhanVien(currentFilter)));
        };

        HBox filterBox = createTimeFilter(updateData);
        kpiRow.setSpacing(15);
        kpiRow.setPadding(new Insets(0, 20, 0, 20));

        barChart1.setTitle("Doanh thu theo nhân viên");
        barChart2.setTitle("Số ca theo nhân viên");

        VBox chartColumn = new VBox(15);
        chartColumn.setPadding(new Insets(0, 20, 0, 20));
        VBox barContainer1 = createChartContainer("Doanh thu theo nhân viên", barChart1,
                "doanh-thu-nhan-vien");
        VBox barContainer2 = createChartContainer("Số ca làm việc", barChart2,
                "so-ca-nhan-vien");
        barContainer1.setMaxWidth(Double.MAX_VALUE);
        barContainer2.setMaxWidth(Double.MAX_VALUE);
        chartColumn.getChildren().addAll(barContainer1, barContainer2);

        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setStyle(CARD_STYLE);
        HBox tableHeader = new HBox(10);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        Label tblTitle = new Label("Hiệu suất nhân viên (" + getTimeLabelDisplay() + ")");
        tblTitle.setStyle(SECTION_TITLE_STYLE);
        Button tblXlsx = new Button("Lưu Excel");
        tblXlsx.setStyle(BTN_GREEN);
        tblXlsx.setOnAction(e -> saveTableAsXlsx("nhan-vien" + getTimeSuffix(), table));
        tableHeader.getChildren().addAll(tblTitle, tblXlsx);
        tableContainer.getChildren().addAll(tableHeader, table);
        HBox tableRow = new HBox(tableContainer);
        tableRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox actionsRow = new HBox(10, filterBox);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(actionsRow, kpiRow, chartColumn, tableRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        tab.setContent(scrollPane);

        updateData.run();
        return tab;
    }

    private TableView<TableRowNhanVien> createTableNhanVien() {
        TableView<TableRowNhanVien> table = new TableView<>();
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRowNhanVien, String> colMa = new TableColumn<>("Mã NV");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));

        TableColumn<TableRowNhanVien, String> colTen = new TableColumn<>("Tên NV");
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));

        TableColumn<TableRowNhanVien, Integer> colCa = new TableColumn<>("Số ca");
        colCa.setCellValueFactory(new PropertyValueFactory<>("soCa"));

        TableColumn<TableRowNhanVien, Integer> colHD = new TableColumn<>("Số HĐ");
        colHD.setCellValueFactory(new PropertyValueFactory<>("soHoaDon"));

        TableColumn<TableRowNhanVien, Double> colDoanhThu = taoCotTienTe("Doanh thu", "tongDoanhThu");

        table.getColumns().addAll(colMa, colTen, colCa, colHD, colDoanhThu);
        return table;
    }

    // ============================================================
    // TAB 6: HÓA ĐƠN
    // ============================================================

    private Tab createTabHoaDon() {
        Tab tab = new Tab("🧾 Hóa đơn");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f1f5f9;");

        HBox kpiRow = new HBox();
        BarChart<String, Number> timeChart = createTimeBarChart("");
        PieChart pieChart = createPieChart("", List.of());
        TableView<TableRowHoaDon> table = createTableHoaDon();

        Runnable updateData = () -> {
            List<KpiItem> kpis = controller.getKpiHoaDon(currentFilter);
            kpiRow.getChildren().clear();
            kpiRow.getChildren().addAll(createKpiRow(kpis).getChildren());

            boolean singleDay = isSingleDay(currentFilter);
            List<TimeSeriesPoint> rawData = singleDay
                    ? controller.getChartHoaDonTheoNgay(currentFilter) // same data, grouped hourly fallback handled
                    : controller.getChartHoaDonTheoNgay(currentFilter);
            List<TimeSeriesPoint> groupedData = singleDay
                    ? groupHourlySeries(rawData, currentFilter.getFromDate())
                    : groupTimeSeriesData(rawData, currentFilter);
            updateTimeBarChart(timeChart, groupedData);

            List<GroupSeriesPoint> pieData = controller.getChartHoaDonTheoTrangThai(currentFilter);
            pieChart.getData().clear();
            double total = pieData.stream().mapToDouble(GroupSeriesPoint::getValue).sum();
            for (GroupSeriesPoint p : pieData) {
                double percent = total > 0 ? (p.getValue() / total * 100) : 0;
                pieChart.getData().add(new PieChart.Data(
                        String.format("%s (%.0f%%)", p.getGroupName(), percent), p.getValue()));
            }

            table.setItems(FXCollections.observableArrayList(controller.getTableHoaDon(currentFilter)));
        };

        HBox filterBox = createTimeFilter(updateData);
        kpiRow.setSpacing(15);
        kpiRow.setPadding(new Insets(0, 20, 0, 20));

        timeChart.setTitle("Số hóa đơn theo thời gian");
        pieChart.setTitle("Trạng thái hóa đơn");

        VBox chartCol = new VBox(15);
        chartCol.setPadding(new Insets(0, 20, 0, 20));
        VBox timeContainer = createChartContainer("Số hóa đơn theo thời gian", timeChart,
                "hoa-don-theo-thoi-gian");
        VBox pieContainer = createChartContainer("Phân bố trạng thái hóa đơn", pieChart,
                "hoa-don-trang-thai");
        timeContainer.setMaxWidth(Double.MAX_VALUE);
        pieContainer.setMaxWidth(Double.MAX_VALUE);
        chartCol.getChildren().addAll(timeContainer, pieContainer);

        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setStyle(CARD_STYLE);
        HBox tableHeader = new HBox(10);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        Label tblTitle = new Label("Danh sách hóa đơn (" + getTimeLabelDisplay() + ")");
        tblTitle.setStyle(SECTION_TITLE_STYLE);
        Button tblXlsx = new Button("Lưu Excel");
        tblXlsx.setStyle(BTN_GREEN);
        tblXlsx.setOnAction(e -> saveTableAsXlsx("hoa-don" + getTimeSuffix(), table));
        tableHeader.getChildren().addAll(tblTitle, tblXlsx);
        tableContainer.getChildren().addAll(tableHeader, table);
        HBox tableRow = new HBox(tableContainer);
        tableRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox actionsRow = new HBox(10, filterBox);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(actionsRow, kpiRow, chartCol, tableRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        tab.setContent(scrollPane);

        updateData.run();
        return tab;
    }

    private TableView<TableRowHoaDon> createTableHoaDon() {
        TableView<TableRowHoaDon> table = new TableView<>();
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRowHoaDon, String> colMa = new TableColumn<>("Mã HĐ");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));

        TableColumn<TableRowHoaDon, LocalDate> colNgay = new TableColumn<>("Ngày tạo");
        colNgay.setCellValueFactory(new PropertyValueFactory<>("ngayTao"));

        TableColumn<TableRowHoaDon, String> colTT = new TableColumn<>("Trạng thái");
        colTT.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        TableColumn<TableRowHoaDon, Double> colTien = taoCotTienTe("Tổng tiền", "tongTien");

        table.getColumns().addAll(colMa, colNgay, colTT, colTien);
        return table;
    }

    // ============================================================
    // TAB 7: KHUYẾN MÃI
    // ============================================================

    private Tab createTabKhuyenMai() {
        Tab tab = new Tab("🎁 Khuyến mãi");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f1f5f9;");

        HBox kpiRow = new HBox();
        TableView<TableRowKhuyenMai> table = createTableKhuyenMai();
        BarChart<String, Number> barChart = createBarChart("");

        Runnable updateData = () -> {
            List<KpiItem> kpis = controller.getKpiKhuyenMai(currentFilter);
            kpiRow.getChildren().clear();
            kpiRow.getChildren().addAll(createKpiRow(kpis).getChildren());

            List<TableRowKhuyenMai> tableData = controller.getTableKhuyenMai(currentFilter);
            table.setItems(FXCollections.observableArrayList(tableData));

            List<GroupSeriesPoint> barData = tableData.stream()
                    .map(r -> new GroupSeriesPoint(r.getTenKhuyenMai(), r.getDoanhThu()))
                    .toList();
            updateGroupBarChart(barChart, barData);
        };

        HBox filterBox = createTimeFilter(updateData);
        kpiRow.setSpacing(15);
        kpiRow.setPadding(new Insets(0, 20, 0, 20));

        barChart.setTitle("Doanh thu theo khuyến mãi");

        HBox chartRow = new HBox(15);
        chartRow.setPadding(new Insets(0, 20, 0, 20));
        VBox chartContainer = createChartContainer("Doanh thu theo mã khuyến mãi", barChart,
                "doanh-thu-khuyen-mai");
        HBox.setHgrow(chartContainer, Priority.ALWAYS);
        chartRow.getChildren().add(chartContainer);

        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setStyle(CARD_STYLE);
        HBox tableHeader = new HBox(10);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        Label tblTitle = new Label("Thống kê khuyến mãi (" + getTimeLabelDisplay() + ")");
        tblTitle.setStyle(SECTION_TITLE_STYLE);
        Button tblXlsx = new Button("Lưu Excel");
        tblXlsx.setStyle(BTN_GREEN);
        tblXlsx.setOnAction(e -> saveTableAsXlsx("khuyen-mai" + getTimeSuffix(), table));
        tableHeader.getChildren().addAll(tblTitle, tblXlsx);
        tableContainer.getChildren().addAll(tableHeader, table);
        HBox tableRow = new HBox(tableContainer);
        tableRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox actionsRow = new HBox(10, filterBox);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(actionsRow, kpiRow, chartRow, tableRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        tab.setContent(scrollPane);

        updateData.run();
        return tab;
    }

    private TableView<TableRowKhuyenMai> createTableKhuyenMai() {
        TableView<TableRowKhuyenMai> table = new TableView<>();
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRowKhuyenMai, String> colMa = new TableColumn<>("Mã KM");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maKhuyenMai"));

        TableColumn<TableRowKhuyenMai, String> colTen = new TableColumn<>("Tên KM");
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenKhuyenMai"));

        TableColumn<TableRowKhuyenMai, Integer> colSoHD = new TableColumn<>("Số HĐ");
        colSoHD.setCellValueFactory(new PropertyValueFactory<>("soHoaDon"));

        TableColumn<TableRowKhuyenMai, Double> colDoanhThu = taoCotTienTe("Doanh thu", "doanhThu");

        table.getColumns().addAll(colMa, colTen, colSoHD, colDoanhThu);
        return table;
    }

    // ============================================================
    // TAB 7: DỊCH VỤ
    // ============================================================

    private Tab createTabDichVu() {
        Tab tab = new Tab("🍽️ Dịch vụ");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f1f5f9;");

        HBox kpiRow = new HBox();
        BarChart<String, Number> timeChart = createTimeBarChart("");
        BarChart<String, Number> barChart = createBarChart("");
        PieChart pieUsage = createPieChart("", List.of());
        TableView<TableRowDichVu> table = createTableDichVu();

        Runnable updateData = () -> {
            List<KpiItem> kpis = controller.getKpiDichVu(currentFilter);
            kpiRow.getChildren().clear();
            kpiRow.getChildren().addAll(createKpiRow(kpis).getChildren());

            // Gom nhóm dữ liệu nếu > 12 ngày
            boolean singleDay = isSingleDay(currentFilter);
            List<TimeSeriesPoint> rawData = singleDay
                    ? controller.getChartDichVuTheoGio(currentFilter)
                    : controller.getChartDichVuTheoNgay(currentFilter);
            List<TimeSeriesPoint> groupedData = singleDay
                    ? groupHourlySeries(rawData, currentFilter.getFromDate())
                    : groupTimeSeriesData(rawData, currentFilter);
            updateTimeBarChart(timeChart, groupedData);

            List<GroupSeriesPoint> barData = controller.getChartDoanhThuDichVu(currentFilter);
            updateGroupBarChart(barChart, barData);

            // Pie chart: % lượt sử dụng dịch vụ
            pieUsage.getData().clear();
            List<GroupSeriesPoint> usageData = controller.getChartLuotSuDungDichVu(currentFilter);
            double totalUsage = usageData.stream().mapToDouble(GroupSeriesPoint::getValue).sum();
            for (GroupSeriesPoint p : usageData) {
                double percent = totalUsage > 0 ? (p.getValue() / totalUsage * 100) : 0;
                pieUsage.getData().add(new PieChart.Data(
                        String.format("%s (%.0f%%)", p.getGroupName(), percent), p.getValue()));
            }

            table.setItems(FXCollections.observableArrayList(controller.getTableDichVu(currentFilter)));
        };

        HBox filterBox = createTimeFilter(updateData);
        kpiRow.setSpacing(15);
        kpiRow.setPadding(new Insets(0, 20, 0, 20));

        timeChart.setTitle("Doanh thu dịch vụ theo thời gian");
        barChart.setTitle("Doanh thu theo loại dịch vụ");
        pieUsage.setTitle("Tỷ lệ lượt sử dụng dịch vụ");

        VBox timeContainer = createChartContainer("Doanh thu dịch vụ theo thời gian", timeChart,
                "doanh-thu-dich-vu");
        VBox barContainer = createChartContainer("Doanh thu theo loại dịch vụ", barChart,
                "doanh-thu-theo-loai-dv");
        VBox pieContainer = createChartContainer("Tỷ lệ lượt sử dụng dịch vụ", pieUsage,
                "ty-le-su-dung-dv");
        timeContainer.setMaxWidth(Double.MAX_VALUE);
        barContainer.setMaxWidth(Double.MAX_VALUE);
        pieContainer.setMaxWidth(Double.MAX_VALUE);

        HBox timeRow = new HBox(timeContainer);
        timeRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(timeContainer, Priority.ALWAYS);

        HBox barRow = new HBox(barContainer);
        barRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(barContainer, Priority.ALWAYS);

        HBox pieRow = new HBox(pieContainer);
        pieRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(pieContainer, Priority.ALWAYS);

        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setStyle(CARD_STYLE);
        HBox tableHeader = new HBox(10);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        Label tblTitle = new Label("Thống kê dịch vụ (" + getTimeLabelDisplay() + ")");
        tblTitle.setStyle(SECTION_TITLE_STYLE);
        Button tblXlsx = new Button("Lưu Excel");
        tblXlsx.setStyle(BTN_GREEN);
        tblXlsx.setOnAction(e -> saveTableAsXlsx("dich-vu" + getTimeSuffix(), table));
        tableHeader.getChildren().addAll(tblTitle, tblXlsx);
        tableContainer.getChildren().addAll(tableHeader, table);
        HBox tableRow = new HBox(tableContainer);
        tableRow.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox actionsRow = new HBox(10, filterBox);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(actionsRow, kpiRow, timeRow, barRow, pieRow, tableRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        tab.setContent(scrollPane);

        updateData.run();
        return tab;
    }

    private TableView<TableRowDichVu> createTableDichVu() {
        TableView<TableRowDichVu> table = new TableView<>();
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRowDichVu, String> colMa = new TableColumn<>("Mã DV");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));

        TableColumn<TableRowDichVu, String> colTen = new TableColumn<>("Tên DV");
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));

        TableColumn<TableRowDichVu, Integer> colSL = new TableColumn<>("Số lượng");
        colSL.setCellValueFactory(new PropertyValueFactory<>("tongSoLuong"));

        TableColumn<TableRowDichVu, Double> colDoanhThu = taoCotTienTe("Doanh thu", "tongDoanhThu");

        table.getColumns().addAll(colMa, colTen, colSL, colDoanhThu);
        return table;
    }
}
