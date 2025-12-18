package model.thongke;

/**
 * Model cho dữ liệu phân nhóm (biểu đồ cơ cấu/pie chart)
 */
public class GroupSeriesPoint {
    private String groupName; // Tên nhóm
    private double value;
    private int count; // Số lượng
    private double percentage; // Phần trăm

    public GroupSeriesPoint() {
    }

    public GroupSeriesPoint(String groupName, double value) {
        this.groupName = groupName;
        this.value = value;
    }

    public GroupSeriesPoint(String groupName, double value, int count) {
        this.groupName = groupName;
        this.value = value;
        this.count = count;
    }

    public GroupSeriesPoint(String groupName, double value, int count, double percentage) {
        this.groupName = groupName;
        this.value = value;
        this.count = count;
        this.percentage = percentage;
    }

    // Getters and Setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
