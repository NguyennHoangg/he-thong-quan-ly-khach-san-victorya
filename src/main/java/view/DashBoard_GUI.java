package view;

import controller.ThongKeDashboard_Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * DASHBOARD GUI
 * - Cách 1: Mỗi lần vào trang Dashboard => tạo mới DashBoard_GUI => tự động load dữ liệu mới từ DB.
 * - Dữ liệu lấy từ ThongKeDashboard_Controller (controller sẽ query DB).
 */
public class DashBoard_GUI extends BorderPane {

    // Controller thống kê (mỗi lần new DashBoard_GUI -> controller new -> query dữ liệu thật)
    private final ThongKeDashboard_Controller controller = new ThongKeDashboard_Controller();

    /**
     * Constructor
     * - Mỗi lần bạn new DashBoard_GUI() là coi như "vào trang Dashboard"
     * - Tại đây build giao diện + lấy dữ liệu thật từ DB.
     */
    public DashBoard_GUI() {

        // Áp style + padding
        getStyleClass().add("dashboard-content");
        setPadding(new Insets(24));
        getStylesheets().add(getClass().getResource("/css/Dashboard.css").toExternalForm());

        // Container chính
        VBox noiDung = new VBox(24);
        noiDung.setFillWidth(true);
        noiDung.setMaxWidth(Double.MAX_VALUE);

        // Grid 3 cột
        GridPane grid = taoGrid3Cot(20, 20);

        // Hàng 1
        grid.add(taoCardPhongTrong(), 0, 0);
        grid.add(taoCardTiLeHuyPhong(), 1, 0);
        grid.add(taoCardDatPhongThangNay(), 2, 0);

        // Hàng 2
        Node cardBar = taoCardTiLeDatPhong_Bar();
        grid.add(cardBar, 0, 1, 2, 1);         // span 2 cột
        grid.add(taoCardKhachHang(), 2, 1);

        // Hàng 3
        Node cardRevenue = taoCardDoanhThu_Line();
        grid.add(cardRevenue, 0, 2, 2, 1);     // span 2 cột
        grid.add(taoCardViecHomNay(), 2, 2);

        // Set center
        noiDung.getChildren().add(grid);
        setCenter(noiDung);
    }

    // =========================================================
    // GRID / CARD BASE
    // =========================================================

    /**
     * Tạo GridPane 3 cột (mỗi cột ~ 33.33%)
     */
    private GridPane taoGrid3Cot(double hgap, double vgap) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("dashboard-grid");
        grid.setHgap(hgap);
        grid.setVgap(vgap);
        grid.setMaxWidth(Double.MAX_VALUE);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }
        return grid;
    }

    /**
     * Base card dùng chung
     */
    private VBox taoCardBase(double padding) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(padding));
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    // =========================================================
    // HELPER TEXT
    // =========================================================

    private Label taoTieuDeNho(String text) {
        Label lb = new Label(text);
        lb.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        return lb;
    }

    private Label taoTieuDeLon(String text) {
        Label lb = new Label(text);
        lb.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");
        return lb;
    }

    private Label taoGiaTriLon(String text) {
        Label lb = new Label(text);
        lb.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        return lb;
    }

    private Label taoChuPhu(String text) {
        Label lb = new Label(text);
        lb.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        return lb;
    }

    private Label taoText12(String text) {
        Label lb = new Label(text);
        lb.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        return lb;
    }

    // =========================================================
    // CARD 1: PHÒNG TRỐNG
    // =========================================================

    /**
     * Card hiển thị số phòng trống + progress
     * - Dữ liệu thật từ DB: controller.getTongSoPhong(), controller.getSoPhongTrong()
     */
    private VBox taoCardPhongTrong() {
        int tongPhong = controller.getTongSoPhong();
        int soPhongTrong = controller.getSoPhongTrong();
        double tiLePhongTrong = tongPhong == 0 ? 0 : (double) soPhongTrong / tongPhong;

        VBox card = taoCardBase(12);
        card.setPrefHeight(110);
        card.setMaxHeight(110);

        Label title = taoTieuDeNho("Số phòng trống");
        Label value = taoGiaTriLon(String.valueOf(soPhongTrong));

        ProgressBar progress = new ProgressBar(tiLePhongTrong);
        progress.setPrefWidth(Double.MAX_VALUE);
        progress.setMaxWidth(Double.MAX_VALUE);
        progress.setPrefHeight(8);
        progress.setStyle("-fx-accent: #3b82f6;");

        Label sub1 = taoChuPhu(soPhongTrong + " / " + tongPhong + " phòng trống");
        Label sub2 = taoText12("Công suất hiện tại: " + String.format("%.0f%% phòng trống", tiLePhongTrong * 100));

        card.getChildren().addAll(title, value, progress, sub1, sub2);
        return card;
    }

    // =========================================================
    // CARD 2: TỶ LỆ HỦY
    // =========================================================

    /**
     * Card hiển thị tỷ lệ hủy phòng tháng này + so sánh tháng trước
     * - Dữ liệu thật từ DB: controller.getTiLeHuyPhongThangNay(), controller.getTiLeHuyPhongThangTruoc()
     */
    private VBox taoCardTiLeHuyPhong() {
        double tiLeHuyThangNay = controller.getTiLeHuyPhongThangNay();     // 0..1
        double tiLeHuyThangTruoc = controller.getTiLeHuyPhongThangTruoc(); // 0..1
        double chenhlech = (tiLeHuyThangNay - tiLeHuyThangTruoc) * 100.0;

        VBox card = taoCardBase(12);
        card.setPrefHeight(110);
        card.setMaxHeight(110);

        Label title = taoTieuDeNho("Tỷ lệ hủy phòng (tháng này)");
        Label value = taoGiaTriLon(String.format("%.0f%%", tiLeHuyThangNay * 100));

        Label line1 = new Label("Tháng này: " + String.format("%.0f%%", tiLeHuyThangNay * 100));
        line1.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");

        Label line2 = new Label((chenhlech >= 0 ? "Tăng " : "Giảm ")
                + String.format("%.1f điểm %% so với tháng trước", Math.abs(chenhlech)));
        line2.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (chenhlech >= 0 ? "#ef4444;" : "#16a34a;"));

        card.getChildren().addAll(title, value, line1, line2);
        return card;
    }

    // =========================================================
    // CARD 3: ĐẶT PHÒNG THÁNG NÀY
    // =========================================================

    /**
     * Card hiển thị số đặt phòng tháng này + % thay đổi
     * - Dữ liệu thật từ DB: controller.getTongDatPhongThangNay(), controller.getPhanTramThayDoiThangNaySoVoiThangTruoc()
     */
    private VBox taoCardDatPhongThangNay() {
        int tongDatPhongThangNay = controller.getTongDatPhongThangNay();
        double changePercent = controller.getPhanTramThayDoiThangNaySoVoiThangTruoc();

        VBox card = taoCardBase(12);
        card.setPrefHeight(110);
        card.setMaxHeight(110);

        Label title = taoTieuDeNho("Đặt phòng tháng này");
        Label value = taoGiaTriLon(String.valueOf(tongDatPhongThangNay));

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Label sub = new Label("So với tháng trước");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

        Label percent = new Label((changePercent >= 0 ? "↑ " : "↓ ") + String.format("%.2f%%", Math.abs(changePercent)));
        percent.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (changePercent >= 0 ? "#16a34a;" : "#ef4444;"));

        row.getChildren().addAll(sub, percent);
        card.getChildren().addAll(title, value, row);
        return card;
    }

    // =========================================================
    // BAR CHART: TỶ LỆ ĐẶT PHÒNG
    // =========================================================

    /**
     * Biểu đồ cột tỷ lệ đặt phòng theo tháng
     * - Dữ liệu thật từ DB: controller.getBookingRateByMonth()
     */
    private VBox taoCardTiLeDatPhong_Bar() {
        VBox card = taoCardBase(20);
        Label title = taoTieuDeLon("Thống kê tỷ lệ đặt phòng");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelFill(Color.web("#64748b"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFill(Color.web("#64748b"));

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setPrefHeight(250);
        chart.setMaxWidth(Double.MAX_VALUE);

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        for (ThongKeDashboard_Controller.BookingRatePoint p : controller.getBookingRateByMonth()) {
            s.getData().add(new XYChart.Data<>(p.month, p.percent));
        }
        chart.getData().add(s);

        card.getChildren().addAll(title, chart);
        return card;
    }

    // =========================================================
    // CARD: KHÁCH HÀNG
    // =========================================================

    /**
     * Card hiển thị tổng khách hàng + khách mới tháng này
     * - Dữ liệu thật từ DB: controller.getTongSoKhachHang(), controller.getSoKhachHangMoiThangNay()
     */
    private VBox taoCardKhachHang() {
        int tongKhach = controller.getTongSoKhachHang();
        int khachMoi = controller.getSoKhachHangMoiThangNay();
        double percentMoi = tongKhach == 0 ? 0d : (double) khachMoi / tongKhach;

        VBox card = taoCardBase(16);
        card.setPrefHeight(130);

        Label title = taoTieuDeNho("Khách hàng");

        HBox row = new HBox(24);
        row.setFillHeight(true);

        VBox left = new VBox(4);
        Label lTitle = new Label("Tổng khách hàng");
        lTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        Label lValue = new Label(String.valueOf(tongKhach));
        lValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label lSub = new Label("Tích lũy trong hệ thống");
        lSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #cbd5f5;");
        left.getChildren().addAll(lTitle, lValue, lSub);

        VBox right = new VBox(4);
        Label rTitle = new Label("Khách mới tháng này");
        rTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        Label rValue = new Label(String.valueOf(khachMoi));
        rValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        Label badge = new Label(String.format("Chiếm %.1f%% tổng khách", percentMoi * 100));
        badge.setStyle(
                "-fx-font-size: 10px;" +
                        "-fx-text-fill: #166534;" +
                        "-fx-background-color: #bbf7d0;" +
                        "-fx-padding: 3 8 3 8;" +
                        "-fx-background-radius: 999;"
        );

        right.getChildren().addAll(rTitle, rValue, badge);

        row.getChildren().addAll(left, right);
        card.getChildren().addAll(title, row);
        return card;
    }

    // =========================================================
    // LINE CHART: DOANH THU
    // =========================================================

    /**
     * Biểu đồ đường doanh thu
     * - Dữ liệu thật từ DB: controller.getRevenuePoints()
     */
    private VBox taoCardDoanhThu_Line() {
        VBox card = taoCardBase(20);
        Label title = taoTieuDeLon("Doanh thu");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#64748b"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFill(Color.web("#64748b"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setPrefHeight(250);
        chart.setMaxWidth(Double.MAX_VALUE);

        XYChart.Series<Number, Number> s = new XYChart.Series<>();
        for (ThongKeDashboard_Controller.RevenuePoint p : controller.getRevenuePoints()) {
            s.getData().add(new XYChart.Data<>(p.index, p.value));
        }
        chart.getData().add(s);

        card.getChildren().addAll(title, chart);
        return card;
    }

    // =========================================================
    // CARD: VIỆC HÔM NAY
    // =========================================================

    /**
     * Card hiển thị các số liệu công việc trong ngày
     * - Dữ liệu thật từ DB:
     *   + controller.getSoCheckInHomNay()
     *   + controller.getSoCheckOutHomNay()
     *   + controller.getSoPhongSapTraTrong24h()
     *   + controller.getSoHoaDonChoThanhToan()
     */
    private VBox taoCardViecHomNay() {
        VBox card = taoCardBase(16);
        card.setPrefHeight(250);

        Label title = taoTieuDeLon("Việc hôm nay");

        int checkIn = controller.getSoCheckInHomNay();
        int checkOut = controller.getSoCheckOutHomNay();
        int phongSapTra = controller.getSoPhongSapTraTrong24h();
        int hoaDonCho = controller.getSoHoaDonChoThanhToan();

        VBox list = new VBox(8);
        list.getChildren().addAll(
                taoDongThongKe("Check-in hôm nay", String.valueOf(checkIn)),
                taoDongThongKe("Check-out hôm nay", String.valueOf(checkOut)),
                taoDongThongKe("Phòng sắp trả (24h)", String.valueOf(phongSapTra)),
                taoDongThongKe("Hóa đơn chờ thanh toán", String.valueOf(hoaDonCho))
        );

        Label tip = new Label("Gợi ý: bấm vào từng mục để điều hướng sang màn liên quan.");
        tip.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8;");

        card.getChildren().addAll(title, list, tip);
        return card;
    }

    /**
     * Tạo 1 dòng thống kê dạng bullet
     */
    private HBox taoDongThongKe(String ten, String giaTri) {
        Label left = new Label("• " + ten);
        left.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Label right = new Label(giaTri);
        right.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, left, spacer, right);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // =========================================================
    // DEMO CHẠY THỬ
    // =========================================================
    public static void main(String[] args) {
        Application.launch(DemoApp.class, args);
    }

    public static class DemoApp extends Application {
        @Override
        public void start(Stage stage) {
            stage.setTitle("Dashboard Demo");
            stage.setScene(new Scene(new DashBoard_GUI(), 1366, 768));
            stage.show();
        }
    }
}
