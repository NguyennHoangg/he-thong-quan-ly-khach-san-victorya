package model.thongke;

/**
 * Model đại diện cho 1 KPI (Key Performance Indicator)
 */
public class KpiItem {
    private String label; // Tiêu đề KPI
    private String value; // Giá trị hiển thị
    private String subLabel; // Mô tả phụ
    private String trend; // "up", "down", "neutral"
    private double trendValue; // % thay đổi

    public KpiItem() {
    }

    public KpiItem(String label, String value, String subLabel) {
        this.label = label;
        this.value = value;
        this.subLabel = subLabel;
        this.trend = "neutral";
        this.trendValue = 0;
    }

    public KpiItem(String label, String value, String subLabel, String trend, double trendValue) {
        this.label = label;
        this.value = value;
        this.subLabel = subLabel;
        this.trend = trend;
        this.trendValue = trendValue;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSubLabel() {
        return subLabel;
    }

    public void setSubLabel(String subLabel) {
        this.subLabel = subLabel;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public double getTrendValue() {
        return trendValue;
    }

    public void setTrendValue(double trendValue) {
        this.trendValue = trendValue;
    }
}
