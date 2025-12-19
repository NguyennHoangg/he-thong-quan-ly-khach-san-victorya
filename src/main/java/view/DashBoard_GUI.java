package view;

import controller.ThongKeDashboard_Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class DashBoard_GUI extends BorderPane {

    private final ThongKeDashboard_Controller controller = new ThongKeDashboard_Controller();

    public DashBoard_GUI() {
        getStyleClass().add("dashboard-content");
        setPadding(new Insets(24));
        getStylesheets().add(getClass().getResource("/css/Dashboard.css").toExternalForm());

        VBox root = new VBox(24);
        GridPane grid = taoGrid3Cot(20, 20);

        // Row 1
        grid.add(taoCardPhongTrong(), 0, 0);
        grid.add(taoCardTiLeHuyPhong(), 1, 0);
        grid.add(taoCardDatPhongThangNay(), 2, 0);

        // Row 2
        grid.add(taoCardTiLeDatPhong_Bar(), 0, 1, 2, 1);
        grid.add(taoCardKhachHang(), 2, 1);

        // Row 3
        grid.add(taoCardDoanhThu_Line(), 0, 2, 2, 1);
        grid.add(taoCardViecHomNay(), 2, 2);

        root.getChildren().add(grid);
        setCenter(root);
    }
    // GRID + CARD BASE
    private GridPane taoGrid3Cot(double hgap, double vgap) {
        GridPane grid = new GridPane();
        grid.setHgap(hgap);
        grid.setVgap(vgap);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }
        return grid;
    }

    private VBox taoCardBase(double padding) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(padding));
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }
    // TEXT HELPER
    private Label tieuDeNho(String s) {
        Label lb = new Label(s);
        lb.setStyle("-fx-font-size:12px; -fx-text-fill:#64748b;");
        return lb;
    }

    private Label tieuDeLon(String s) {
        Label lb = new Label(s);
        lb.setStyle("-fx-font-size:16px; -fx-font-weight:600; -fx-text-fill:#1e293b;");
        return lb;
    }

    private Label giaTriLon(String s) {
        Label lb = new Label(s);
        lb.setStyle("-fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:#1e293b;");
        return lb;
    }
    // TOOLTIP "CHẮC ĂN" (show theo chuột, không phụ thuộc Tooltip.install)
    private void ganTooltipThuCong(Node node, Tooltip tip) {
        node.setOnMouseMoved(e -> {
            tip.setAutoHide(true);
            if (!tip.isShowing()) {
                tip.show(node, e.getScreenX() + 12, e.getScreenY() + 12);
            } else {
                tip.setX(e.getScreenX() + 12);
                tip.setY(e.getScreenY() + 12);
            }
        });
        node.setOnMouseExited(e -> tip.hide());
    }
    // CARD: PHÒNG TRỐNG
    private VBox taoCardPhongTrong() {
        int tong = controller.getTongSoPhong();
        int trong = controller.getSoPhongTrong();
        double rate = tong == 0 ? 0 : (double) trong / tong;

        VBox card = taoCardBase(12);

        ProgressBar bar = new ProgressBar(rate);
        bar.setPrefHeight(8);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent:#3b82f6;");

        card.getChildren().addAll(
                tieuDeNho("Số phòng trống"),
                giaTriLon(String.valueOf(trong)),
                bar,
                new Label(trong + " / " + tong + " phòng"),
                new Label(String.format("Công suất: %.0f%%", rate * 100))
        );
        return card;
    }

    // CARD: TỶ LỆ HỦY PHÒNG
    private VBox taoCardTiLeHuyPhong() {
        double now = controller.getTiLeHuyPhongThangNay() * 100;
        double last = controller.getTiLeHuyPhongThangTruoc() * 100;
        double diff = now - last;

        Label diffLb = new Label((diff >= 0 ? "Tăng " : "Giảm ") + String.format("%.1f điểm %%", Math.abs(diff)));
        diffLb.setStyle("-fx-font-size:11px; -fx-text-fill:" + (diff >= 0 ? "#ef4444;" : "#16a34a;"));

        VBox card = taoCardBase(12);
        card.getChildren().addAll(
                tieuDeNho("Tỷ lệ hủy phòng (tháng này)"),
                giaTriLon(String.format("%.0f%%", now)),
                diffLb,
                new Label(String.format("Tháng trước: %.0f%%", last))
        );
        return card;
    }
    // CARD: ĐẶT PHÒNG THÁNG NÀY
    private VBox taoCardDatPhongThangNay() {
        int total = controller.getTongDatPhongThangNay();
        double change = controller.getPhanTramThayDoiThangNaySoVoiThangTruoc();

        Label percent = new Label((change >= 0 ? "↑ " : "↓ ") + String.format("%.2f%%", Math.abs(change)));
        percent.setStyle("-fx-font-size:11px; -fx-text-fill:" + (change >= 0 ? "#16a34a;" : "#ef4444;"));

        VBox card = taoCardBase(12);
        card.getChildren().addAll(
                tieuDeNho("Đặt phòng tháng này"),
                giaTriLon(String.valueOf(total)),
                percent
        );
        return card;
    }
    // BAR CHART: TỶ LỆ ĐẶT PHÒNG (hover chắc hiện)
    private VBox taoCardTiLeDatPhong_Bar() {
        VBox card = taoCardBase(20);
        card.getChildren().add(tieuDeLon("Tỷ lệ đặt phòng theo tháng"));

        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis(0, 100, 10);

        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setLegendVisible(false);
        chart.setPrefHeight(260);
        chart.setAnimated(false);

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        List<ThongKeDashboard_Controller.BookingRatePoint> points = controller.getBookingRateByMonth();

        for (ThongKeDashboard_Controller.BookingRatePoint p : points) {
            s.getData().add(new XYChart.Data<>(p.month, p.percent));
        }

        chart.getData().add(s);
        card.getChildren().add(chart);

        //  gắn tooltip sau khi node bar được create
        chart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) return;

            Platform.runLater(() -> {
                chart.applyCss();
                chart.layout();

                for (int i = 0; i < s.getData().size(); i++) {
                    XYChart.Data<String, Number> d = s.getData().get(i);
                    Node bar = d.getNode();
                    if (bar == null) continue;

                    ThongKeDashboard_Controller.BookingRatePoint p = points.get(i);
                    Tooltip tip = new Tooltip(p.month + ": " + p.percent + "%");
                    ganTooltipThuCong(bar, tip);

                    bar.setPickOnBounds(true);
                }
            });
        });

        return card;
    }
    // LINE CHART: DOANH THU (hover chắc hiện)
    private VBox taoCardDoanhThu_Line() {
        VBox card = taoCardBase(20);
        card.getChildren().add(tieuDeLon("Doanh thu"));

        NumberAxis x = new NumberAxis(1, 12, 1);
        NumberAxis y = new NumberAxis();

        LineChart<Number, Number> chart = new LineChart<>(x, y);
        chart.setLegendVisible(false);
        chart.setCreateSymbols(true);
        chart.setPrefHeight(260);
        chart.setAnimated(false);

        XYChart.Series<Number, Number> s = new XYChart.Series<>();
        List<ThongKeDashboard_Controller.RevenuePoint> points = controller.getRevenuePoints();

        for (ThongKeDashboard_Controller.RevenuePoint p : points) {
            s.getData().add(new XYChart.Data<>(p.index, p.value));
        }

        chart.getData().add(s);
        card.getChildren().add(chart);

        chart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) return;

            Platform.runLater(() -> {
                chart.applyCss();
                chart.layout();

                for (int i = 0; i < s.getData().size(); i++) {
                    XYChart.Data<Number, Number> d = s.getData().get(i);
                    Node symbol = d.getNode();
                    if (symbol == null) continue;

                    ThongKeDashboard_Controller.RevenuePoint p = points.get(i);
                    Tooltip tip = new Tooltip("Tháng " + p.index + "\nDoanh thu: " + String.format("%,.0f", p.value) + " VND");
                    ganTooltipThuCong(symbol, tip);

                    symbol.setPickOnBounds(true);
                }
            });
        });

        return card;
    }
    // CARD: VIỆC HÔM NAY
    private VBox taoCardViecHomNay() {
        VBox card = taoCardBase(16);
        card.getChildren().add(tieuDeLon("Việc hôm nay"));

        card.getChildren().addAll(
                dong("Check-in hôm nay", controller.getSoCheckInHomNay()),
                dong("Check-out hôm nay", controller.getSoCheckOutHomNay()),
                dong("Phòng sắp trả (24h)", controller.getSoPhongSapTraTrong24h()),
                dong("Hóa đơn chờ thanh toán", controller.getSoHoaDonChoThanhToan())
        );
        return card;
    }

    private HBox dong(String ten, int v) {
        Label l = new Label("• " + ten);
        l.setStyle("-fx-font-size:12px; -fx-text-fill:#64748b;");

        Label r = new Label(String.valueOf(v));
        r.setStyle("-fx-font-size:12px; -fx-font-weight:600; -fx-text-fill:#1e293b;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        HBox row = new HBox(10, l, sp, r);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
    // CARD: KHÁCH HÀNG
    private VBox taoCardKhachHang() {
        int tongKhach = controller.getTongSoKhachHang();
        int khachMoi = controller.getSoKhachHangMoiThangNay();
        double percentMoi = tongKhach == 0 ? 0d : (double) khachMoi / tongKhach;

        VBox card = taoCardBase(16);
        card.setPrefHeight(130);

        Label title = tieuDeNho("Khách hàng");

        HBox row = new HBox(24);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(4);
        Label lTitle = new Label("Tổng khách hàng");
        lTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        Label lValue = new Label(String.valueOf(tongKhach));
        lValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label lSub = new Label("Tích lũy trong hệ thống");
        lSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
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
}