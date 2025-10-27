package view;

import javafx.application.Application;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;


public class DashBoard_GUI extends BorderPane {
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
        
        // Configure column constraints for equal width
        for (int i = 0; i < 3; i++) {
            javafx.scene.layout.ColumnConstraints col = new javafx.scene.layout.ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(javafx.scene.layout.Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }

        // Card: Khách đang lưu trú
        VBox cardGuest = new VBox(4);
        cardGuest.getStyleClass().add("card");
        cardGuest.setPadding(new Insets(12));
        cardGuest.setMaxWidth(Double.MAX_VALUE);
        cardGuest.setPrefHeight(110);
        cardGuest.setMaxHeight(110);
        Label guestTitle = new Label("Khách đang lưu trú");
        guestTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        Label guestValue = new Label("48");
        guestValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        ProgressBar guestProgress = new ProgressBar(0.32);
        guestProgress.setPrefWidth(Double.MAX_VALUE);
        guestProgress.setMaxWidth(Double.MAX_VALUE);
        guestProgress.setPrefHeight(8);
        guestProgress.setStyle("-fx-accent: #3b82f6;");
        Label guestSub = new Label("48 / 150");
        guestSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
        Label guestStatus = new Label("Đang sử dụng");
        guestStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        cardGuest.getChildren().addAll(guestTitle, guestValue, guestProgress, guestSub, guestStatus);
        grid.add(cardGuest, 0, 0);

        // Card: Tỷ lệ hủy phòng
        VBox cardCancel = new VBox(4);
        cardCancel.getStyleClass().add("card");
        cardCancel.setPadding(new Insets(12));
        cardCancel.setMaxWidth(Double.MAX_VALUE);
        cardCancel.setPrefHeight(110);
        cardCancel.setMaxHeight(110);
        
        HBox cancelTop = new HBox(12);
        cancelTop.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        VBox cancelInfo = new VBox(2);
        Label cancelTitle = new Label("Tỷ lệ hủy phòng");
        cancelTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        Label cancelValue = new Label("65%");
        cancelValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        cancelInfo.getChildren().addAll(cancelTitle, cancelValue);
        
        // Mini donut chart for cancel rate
        PieChart cancelDonut = new PieChart();
        cancelDonut.getData().add(new PieChart.Data("Goal", 65));
        cancelDonut.getData().add(new PieChart.Data("Rest", 35));
        cancelDonut.setLegendVisible(false);
        cancelDonut.setLabelsVisible(false);
        cancelDonut.setPrefSize(50, 50);
        cancelDonut.setMaxSize(50, 50);
        cancelDonut.setStyle("-fx-start-angle: 90;");

        
        
        cancelTop.getChildren().addAll(cancelInfo);
        
        Label cancelGoal = new Label("Goal");
        cancelGoal.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        Label cancelWeek = new Label("70% trong tuần này");
        cancelWeek.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");
        Label cancelMonth = new Label("52% trong tháng trước");
        cancelMonth.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");
        cardCancel.getChildren().addAll(cancelTop, cancelGoal, cancelWeek, cancelMonth);
        grid.add(cardCancel, 1, 0);

        // Card: Đặt phòng 30 ngày
        VBox cardBooking = new VBox(4);
        cardBooking.getStyleClass().add("card");
        cardBooking.setPadding(new Insets(12));
        cardBooking.setMaxWidth(Double.MAX_VALUE);
        cardBooking.setPrefHeight(110);
        cardBooking.setMaxHeight(110);
        Label bookingTitle = new Label("Đặt phòng trong 30 ngày");
        bookingTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        Label bookingValue = new Label("352");
        bookingValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        HBox bookingRow = new HBox(8);
        Label bookingSub = new Label("30 ngày qua");
        bookingSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        Label bookingPercent = new Label("↓ 36.24%");
        bookingPercent.setStyle("-fx-font-size: 11px; -fx-text-fill: #ef4444;");
        bookingRow.getChildren().addAll(bookingSub, bookingPercent);
        
        // Mini area chart for booking trend
        NumberAxis bookingXAxis = new NumberAxis(1, 3, 1);
        bookingXAxis.setVisible(false);
        NumberAxis bookingYAxis = new NumberAxis();
        bookingYAxis.setVisible(false);
        LineChart<Number, Number> bookingMiniChart = new LineChart<>(bookingXAxis, bookingYAxis);
        bookingMiniChart.setLegendVisible(false);
        bookingMiniChart.setPrefHeight(25);
        bookingMiniChart.setMaxHeight(25);
        bookingMiniChart.setCreateSymbols(false);
        bookingMiniChart.setStyle("-fx-padding: 0;");
        XYChart.Series<Number, Number> bookingMiniSeries = new XYChart.Series<>();
        bookingMiniSeries.getData().add(new XYChart.Data<>(1, 120));
        bookingMiniSeries.getData().add(new XYChart.Data<>(2, 90));
        bookingMiniSeries.getData().add(new XYChart.Data<>(3, 142));
        bookingMiniChart.getData().add(bookingMiniSeries);
        
        cardBooking.getChildren().addAll(bookingTitle, bookingValue, bookingRow);
        grid.add(cardBooking, 2, 0);

        // Card: Thống kê tỷ lệ đặt phòng (Bar chart)
        VBox cardBar = new VBox(12);
        cardBar.getStyleClass().add("card");
        cardBar.setPadding(new Insets(20));
        cardBar.setMaxWidth(Double.MAX_VALUE);
        Label barTitle = new Label("Thống kê tỷ lệ đặt phòng");
        barTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");
        CategoryAxis barXAxis = new CategoryAxis();
        barXAxis.setTickLabelFill(javafx.scene.paint.Color.web("#64748b"));
        NumberAxis barYAxis = new NumberAxis();
        barYAxis.setTickLabelFill(javafx.scene.paint.Color.web("#64748b"));
        BarChart<String, Number> barChart = new BarChart<>(barXAxis, barYAxis);
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(250);
        barChart.setMaxWidth(Double.MAX_VALUE);
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.getData().add(new XYChart.Data<>("May", 70));
        barSeries.getData().add(new XYChart.Data<>("Jun", 55));
        barSeries.getData().add(new XYChart.Data<>("Jul", 80));
        barSeries.getData().add(new XYChart.Data<>("Aug", 60));
        barSeries.getData().add(new XYChart.Data<>("Sep", 90));
        barSeries.getData().add(new XYChart.Data<>("Oct", 85));
        barSeries.getData().add(new XYChart.Data<>("Nov", 88));
        barSeries.getData().add(new XYChart.Data<>("Dec", 92));
        barSeries.getData().add(new XYChart.Data<>("Jan", 95));
        barSeries.getData().add(new XYChart.Data<>("Feb", 97));
        barChart.getData().add(barSeries);
        cardBar.getChildren().addAll(barTitle, barChart);
        grid.add(cardBar, 0, 1, 2, 1);

        // Card: Khách hàng mới (Line chart)
        VBox cardLine = new VBox(12);
        cardLine.getStyleClass().add("card");
        cardLine.setPadding(new Insets(20));
        cardLine.setMaxWidth(Double.MAX_VALUE);
        Label lineTitle = new Label("Khách hàng mới");
        lineTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");
        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelFill(javafx.scene.paint.Color.web("#64748b"));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFill(javafx.scene.paint.Color.web("#64748b"));
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(true);
        lineChart.setPrefHeight(250);
        lineChart.setMaxWidth(Double.MAX_VALUE);
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        series1.setName("Tháng này");
        series1.getData().add(new XYChart.Data<>(0.6, 12));
        series1.getData().add(new XYChart.Data<>(1.0, 22));
        series1.getData().add(new XYChart.Data<>(1.5, 28));
        series1.getData().add(new XYChart.Data<>(2.0, 35));
        series1.getData().add(new XYChart.Data<>(2.5, 30));
        series1.getData().add(new XYChart.Data<>(3.0, 42));
        series1.getData().add(new XYChart.Data<>(4.0, 55));
        series1.getData().add(new XYChart.Data<>(4.5, 60));
        series1.getData().add(new XYChart.Data<>(5.0, 72));
        series1.getData().add(new XYChart.Data<>(6.0, 82));
        series1.getData().add(new XYChart.Data<>(6.5, 88));
        series1.getData().add(new XYChart.Data<>(7.0, 95));
        series1.getData().add(new XYChart.Data<>(7.5, 105));
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.setName("Tháng 1");
        series2.getData().add(new XYChart.Data<>(0.6, 8));
        series2.getData().add(new XYChart.Data<>(1.0, 18));
        series2.getData().add(new XYChart.Data<>(1.5, 25));
        series2.getData().add(new XYChart.Data<>(2.0, 32));
        series2.getData().add(new XYChart.Data<>(2.5, 38));
        series2.getData().add(new XYChart.Data<>(3.0, 45));
        series2.getData().add(new XYChart.Data<>(4.0, 48));
        series2.getData().add(new XYChart.Data<>(4.5, 55));
        series2.getData().add(new XYChart.Data<>(5.0, 68));
        series2.getData().add(new XYChart.Data<>(6.0, 78));
        series2.getData().add(new XYChart.Data<>(6.5, 85));
        series2.getData().add(new XYChart.Data<>(7.0, 92));
        series2.getData().add(new XYChart.Data<>(7.5, 98));
        lineChart.getData().addAll(series1, series2);
        cardLine.getChildren().addAll(lineTitle, lineChart);
        grid.add(cardLine, 2, 1);

        // Card: Doanh thu (Line chart)
        VBox cardRevenue = new VBox(12);
        cardRevenue.getStyleClass().add("card");
        cardRevenue.setPadding(new Insets(20));
        cardRevenue.setMaxWidth(Double.MAX_VALUE);
        Label revenueTitle = new Label("Doanh thu");
        revenueTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");
        NumberAxis revXAxis = new NumberAxis();
        revXAxis.setTickLabelFill(javafx.scene.paint.Color.web("#64748b"));
        NumberAxis revYAxis = new NumberAxis();
        revYAxis.setTickLabelFill(javafx.scene.paint.Color.web("#64748b"));
        LineChart<Number, Number> revenueChart = new LineChart<>(revXAxis, revYAxis);
        revenueChart.setLegendVisible(false);
        revenueChart.setPrefHeight(250);
        revenueChart.setMaxWidth(Double.MAX_VALUE);
        XYChart.Series<Number, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.getData().add(new XYChart.Data<>(0.5, 22));
        revenueSeries.getData().add(new XYChart.Data<>(1.0, 28));
        revenueSeries.getData().add(new XYChart.Data<>(1.5, 32));
        revenueSeries.getData().add(new XYChart.Data<>(2.0, 45));
        revenueSeries.getData().add(new XYChart.Data<>(2.5, 35));
        revenueSeries.getData().add(new XYChart.Data<>(3.0, 52));
        revenueSeries.getData().add(new XYChart.Data<>(3.5, 70));
        revenueSeries.getData().add(new XYChart.Data<>(4.0, 48));
        revenueSeries.getData().add(new XYChart.Data<>(4.5, 62));
        revenueSeries.getData().add(new XYChart.Data<>(5.0, 55));
        revenueSeries.getData().add(new XYChart.Data<>(5.5, 78));
        revenueSeries.getData().add(new XYChart.Data<>(6.0, 65));
        revenueSeries.getData().add(new XYChart.Data<>(6.5, 72));
        revenueSeries.getData().add(new XYChart.Data<>(7.0, 68));
        revenueChart.getData().add(revenueSeries);
        cardRevenue.getChildren().addAll(revenueTitle, revenueChart);
        grid.add(cardRevenue, 0, 2, 2, 1);

        // Card: Sử dụng dịch vụ (Pie chart)
        VBox cardPie = new VBox(12);
        cardPie.getStyleClass().add("card");
        cardPie.setPadding(new Insets(20));
        cardPie.setMaxWidth(Double.MAX_VALUE);
        Label pieTitle = new Label("Sử dụng dịch vụ");
        pieTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");
        PieChart pieChart = new PieChart();
        pieChart.getData().add(new PieChart.Data("Ăn uống", 40));
        pieChart.getData().add(new PieChart.Data("Giặt ủi", 32));
        pieChart.getData().add(new PieChart.Data("Khác", 28));
        pieChart.setLegendVisible(true);
        pieChart.setPrefHeight(250);
        pieChart.setMaxWidth(Double.MAX_VALUE);
        cardPie.getChildren().addAll(pieTitle, pieChart);
        grid.add(cardPie, 2, 2);

        content.getChildren().add(grid);
        this.setCenter(content);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
