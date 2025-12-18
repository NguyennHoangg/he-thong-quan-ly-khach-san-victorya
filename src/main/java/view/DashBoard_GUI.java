package view;

import controller.ThongKeDashboard_Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;

public class DashBoard_GUI extends BorderPane {

    private final ThongKeDashboard_Controller controller = new ThongKeDashboard_Controller();

    public DashBoard_GUI() {
        this.getStyleClass().add("dashboard-content");
        this.setPadding(new Insets(24));
        this.getStylesheets().add(getClass().getResource("/css/Dashboard.css").toExternalForm());

        VBox content = new VBox(24);
        content.setFillWidth(true);
        content.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dashboard-grid");
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setMaxWidth(Double.MAX_VALUE);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }

        // ===================== CARD 1: SỐ PHÒNG TRỐNG =====================
        int tongPhong = controller.getTongSoPhong();
        int soPhongTrong = controller.getSoPhongTrong();
        double tiLePhongTrong = tongPhong == 0 ? 0 : (double) soPhongTrong / tongPhong;

        VBox cardPhongTrong = new VBox(4);
        cardPhongTrong.getStyleClass().add("card");
        cardPhongTrong.setPadding(new Insets(12));
        cardPhongTrong.setMaxWidth(Double.MAX_VALUE);
        cardPhongTrong.setPrefHeight(110);
        cardPhongTrong.setMaxHeight(110);

        Label phongTitle = new Label("Số phòng trống");
        phongTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Label phongValue = new Label(String.valueOf(soPhongTrong));
        phongValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        ProgressBar phongProgress = new ProgressBar(tiLePhongTrong);
        phongProgress.setPrefWidth(Double.MAX_VALUE);
        phongProgress.setMaxWidth(Double.MAX_VALUE);
        phongProgress.setPrefHeight(8);
        phongProgress.setStyle("-fx-accent: #3b82f6;");

        Label phongSub = new Label(soPhongTrong + " / " + tongPhong + " phòng trống");
        phongSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        Label phongStatus = new Label("Công suất hiện tại: " +
                String.format("%.0f%% phòng trống", tiLePhongTrong * 100));
        phongStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        cardPhongTrong.getChildren().addAll(phongTitle, phongValue, phongProgress, phongSub, phongStatus);
        grid.add(cardPhongTrong, 0, 0);

        // ===================== CARD 2: TỶ LỆ HỦY PHÒNG (THEO THÁNG) =====================
        VBox cardCancel = new VBox(4);
        cardCancel.getStyleClass().add("card");
        cardCancel.setPadding(new Insets(12));
        cardCancel.setMaxWidth(Double.MAX_VALUE);
        cardCancel.setPrefHeight(110);
        cardCancel.setMaxHeight(110);

// dùng theo THÁNG
        double tiLeHuyThangNay = controller.getTiLeHuyPhongThangNay();        // 0.0 – 1.0
        double tiLeHuyThangTruoc = controller.getTiLeHuyPhongThangTruoc();    // 0.0 – 1.0
        double chenhlech = (tiLeHuyThangNay - tiLeHuyThangTruoc) * 100.0;

        HBox cancelTop = new HBox(12);
        cancelTop.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        VBox cancelInfo = new VBox(2);

        Label cancelTitle = new Label("Tỷ lệ hủy phòng (tháng này)");
        cancelTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Label cancelValue = new Label(String.format("%.0f%%", tiLeHuyThangNay * 100));
        cancelValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        cancelInfo.getChildren().addAll(cancelTitle, cancelValue);
        cancelTop.getChildren().addAll(cancelInfo);

// dòng mô tả chi tiết
        Label cancelMonthNow = new Label("Tháng này: " + String.format("%.0f%%", tiLeHuyThangNay * 100));
        cancelMonthNow.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");

// Chỉ hiển thị so với tháng trước, không hiển thị riêng tháng trước trung bình nữa
        Label cancelDiff = new Label(
                (chenhlech >= 0 ? "Tăng " : "Giảm ") +
                        String.format("%.1f điểm %% so với tháng trước", Math.abs(chenhlech))
        );
        cancelDiff.setStyle("-fx-font-size: 10px; -fx-text-fill: " +
                (chenhlech >= 0 ? "#ef4444;" : "#16a34a;"));

        cardCancel.getChildren().addAll(cancelTop, cancelMonthNow, cancelDiff);
        grid.add(cardCancel, 1, 0);

        // ===================== CARD 3: ĐẶT PHÒNG THÁNG NÀY =====================
        VBox cardBooking = new VBox(4);
        cardBooking.getStyleClass().add("card");
        cardBooking.setPadding(new Insets(12));
        cardBooking.setMaxWidth(Double.MAX_VALUE);
        cardBooking.setPrefHeight(110);
        cardBooking.setMaxHeight(110);

        int tongDatPhongThangNay = controller.getTongDatPhongThangNay();
        double changePercent = controller.getPhanTramThayDoiThangNaySoVoiThangTruoc(); // có thể âm

        Label bookingTitle = new Label("Đặt phòng tháng này");
        bookingTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Label bookingValue = new Label(String.valueOf(tongDatPhongThangNay));
        bookingValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox bookingRow = new HBox(8);
        Label bookingSub = new Label("So với tháng trước");
        bookingSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

        Label bookingPercent = new Label(
                (changePercent >= 0 ? "↑ " : "↓ ") +
                        String.format("%.2f%%", Math.abs(changePercent)));
        bookingPercent.setStyle("-fx-font-size: 11px; -fx-text-fill: " +
                (changePercent >= 0 ? "#16a34a;" : "#ef4444;"));

        bookingRow.getChildren().addAll(bookingSub, bookingPercent);
        cardBooking.getChildren().addAll(bookingTitle, bookingValue, bookingRow);
        grid.add(cardBooking, 2, 0);

        // ===================== BAR CHART: TỶ LỆ ĐẶT PHÒNG =====================
        VBox cardBar = new VBox(12);
        cardBar.getStyleClass().add("card");
        cardBar.setPadding(new Insets(20));
        cardBar.setMaxWidth(Double.MAX_VALUE);

        Label barTitle = new Label("Thống kê tỷ lệ đặt phòng");
        barTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        CategoryAxis barXAxis = new CategoryAxis();
        barXAxis.setTickLabelFill(Color.web("#64748b"));
        NumberAxis barYAxis = new NumberAxis();
        barYAxis.setTickLabelFill(Color.web("#64748b"));

        BarChart<String, Number> barChart = new BarChart<>(barXAxis, barYAxis);
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(250);
        barChart.setMaxWidth(Double.MAX_VALUE);

        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        for (ThongKeDashboard_Controller.BookingRatePoint p : controller.getBookingRateByMonth()) {
            barSeries.getData().add(new XYChart.Data<>(p.month, p.percent));
        }
        barChart.getData().add(barSeries);

        cardBar.getChildren().addAll(barTitle, barChart);
        grid.add(cardBar, 0, 1, 2, 1);
// ===================== CARD: KHÁCH HÀNG (2 CỘT) =====================
        VBox cardKhachHang = new VBox(10);
        cardKhachHang.getStyleClass().add("card");
        cardKhachHang.setPadding(new Insets(16));
        cardKhachHang.setMaxWidth(Double.MAX_VALUE);
        cardKhachHang.setPrefHeight(130);

        Label khTitle = new Label("Khách hàng");
        khTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

// Lấy dữ liệu
        int tongKhach = controller.getTongSoKhachHang();
        int khachMoi = controller.getSoKhachHangMoiThangNay();
        double percentMoi = tongKhach == 0 ? 0d : (double) khachMoi / tongKhach;

// Hàng chứa 2 cột
        HBox row = new HBox(24);
        row.setFillHeight(true);

// ===== CỘT TRÁI: TỔNG KHÁCH HÀNG =====
        VBox colLeft = new VBox(4);
        Label lblTongTitle = new Label("Tổng khách hàng");
        lblTongTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Label lblTongValue = new Label(String.valueOf(tongKhach));
        lblTongValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label lblTongSub = new Label("Tích lũy trong hệ thống");
        lblTongSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #cbd5f5;");

        colLeft.getChildren().addAll(lblTongTitle, lblTongValue, lblTongSub);

// ===== CỘT PHẢI: KHÁCH MỚI THÁNG NÀY =====
        VBox colRight = new VBox(4);
        Label lblMoiTitle = new Label("Khách mới tháng này");
        lblMoiTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Label lblMoiValue = new Label(String.valueOf(khachMoi));
        lblMoiValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

// Badge % khách mới
        Label lblMoiBadge = new Label(
                String.format("Chiếm %.1f%% tổng khách", percentMoi * 100)
        );
        lblMoiBadge.setStyle(
                "-fx-font-size: 10px;" +
                        "-fx-text-fill: #166534;" +
                        "-fx-background-color: #bbf7d0;" +   // xanh lá nhạt
                        "-fx-padding: 3 8 3 8;" +
                        "-fx-background-radius: 999;"
        );

        colRight.getChildren().addAll(lblMoiTitle, lblMoiValue, lblMoiBadge);

// Thêm 2 cột vào hàng
        row.getChildren().addAll(colLeft, colRight);

// Thêm vào card
        cardKhachHang.getChildren().addAll(khTitle, row);

// Đặt card vào grid (vị trí cũ của card khách hàng)
        grid.add(cardKhachHang, 2, 1);

        // ===================== LINE CHART: DOANH THU =====================
        VBox cardRevenue = new VBox(12);
        cardRevenue.getStyleClass().add("card");
        cardRevenue.setPadding(new Insets(20));
        cardRevenue.setMaxWidth(Double.MAX_VALUE);

        Label revenueTitle = new Label("Doanh thu");
        revenueTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        NumberAxis revXAxis = new NumberAxis();
        revXAxis.setTickLabelFill(Color.web("#64748b"));
        NumberAxis revYAxis = new NumberAxis();
        revYAxis.setTickLabelFill(Color.web("#64748b"));

        LineChart<Number, Number> revenueChart = new LineChart<>(revXAxis, revYAxis);
        revenueChart.setLegendVisible(false);
        revenueChart.setPrefHeight(250);
        revenueChart.setMaxWidth(Double.MAX_VALUE);

        XYChart.Series<Number, Number> revenueSeries = new XYChart.Series<>();
        for (ThongKeDashboard_Controller.RevenuePoint p : controller.getRevenuePoints()) {
            revenueSeries.getData().add(new XYChart.Data<>(p.index, p.value));
        }
        revenueChart.getData().add(revenueSeries);

        cardRevenue.getChildren().addAll(revenueTitle, revenueChart);
        grid.add(cardRevenue, 0, 2, 2, 1);

        // ===================== HOÀN TẤT =====================
        content.getChildren().add(grid);
        this.setCenter(content);
    }

    // Chạy test riêng Dashboard
    public static void main(String[] args) {
        Application.launch(DemoApp.class, args);
    }

    public static class DemoApp extends Application {
        @Override
        public void start(Stage stage) {
            stage.setTitle("Dashboard Demo");
            stage.setScene(new javafx.scene.Scene(new DashBoard_GUI(), 1366, 768));
            stage.show();
        }
    }
}