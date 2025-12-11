package model.thongke;

import java.time.LocalDate;

/**
 * Model cho dữ liệu chuỗi thời gian (biểu đồ xu hướng)
 */
public class TimeSeriesPoint {
    private LocalDate date;
    private String label; // Nhãn hiển thị (có thể là ngày, tháng, năm hoặc giờ)
    private double value;
    private int count; // Số lượng (nếu cần)
    private Integer hour; // Giờ trong ngày (0-23) khi cần hiển thị theo giờ

    public TimeSeriesPoint() {
    }

    public TimeSeriesPoint(LocalDate date, double value) {
        this.date = date;
        this.label = date != null ? date.toString() : "";
        this.value = value;
    }

    public TimeSeriesPoint(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public TimeSeriesPoint(LocalDate date, String label, double value, int count) {
        this.date = date;
        this.label = label;
        this.value = value;
        this.count = count;
    }

    public TimeSeriesPoint(LocalDate date, String label, double value, int count, Integer hour) {
        this.date = date;
        this.label = label;
        this.value = value;
        this.count = count;
        this.hour = hour;
    }

    public TimeSeriesPoint(Integer hour, String label, double value) {
        this.hour = hour;
        this.label = label;
        this.value = value;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }
}
