package model.thongke;

import java.time.LocalDate;

/**
 * Model cho bộ lọc thời gian
 */
public class TimeFilter {
    public enum TimeMode {
        DAY, // Ngày
        WEEK, // Tuần
        MONTH, // Tháng
        YEAR, // Năm
        CUSTOM // Khoảng ngày tùy chọn
    }

    private TimeMode mode;
    private LocalDate fromDate;
    private LocalDate toDate;

    public TimeFilter() {
        this.mode = TimeMode.MONTH;
        this.toDate = LocalDate.now();
        this.fromDate = toDate.minusMonths(1);
    }

    public TimeFilter(TimeMode mode) {
        this.mode = mode;
        this.toDate = LocalDate.now();
        calculateFromDate();
    }

    public TimeFilter(LocalDate fromDate, LocalDate toDate) {
        this.mode = TimeMode.CUSTOM;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    private void calculateFromDate() {
        switch (mode) {
            case DAY -> fromDate = toDate;
            case WEEK -> fromDate = toDate.minusWeeks(1);
            case MONTH -> fromDate = toDate.minusMonths(1);
            case YEAR -> fromDate = toDate.minusYears(1);
            default -> fromDate = toDate.minusMonths(1);
        }
    }

    // Static factory methods
    public static TimeFilter today() {
        TimeFilter tf = new TimeFilter();
        tf.mode = TimeMode.DAY;
        tf.toDate = LocalDate.now();
        tf.fromDate = tf.toDate;
        return tf;
    }

    public static TimeFilter thisWeek() {
        TimeFilter tf = new TimeFilter();
        tf.mode = TimeMode.WEEK;
        tf.toDate = LocalDate.now();
        tf.fromDate = tf.toDate.minusWeeks(1);
        return tf;
    }

    public static TimeFilter thisMonth() {
        TimeFilter tf = new TimeFilter();
        tf.mode = TimeMode.MONTH;
        tf.toDate = LocalDate.now();
        tf.fromDate = tf.toDate.withDayOfMonth(1);
        return tf;
    }

    public static TimeFilter thisYear() {
        TimeFilter tf = new TimeFilter();
        tf.mode = TimeMode.YEAR;
        tf.toDate = LocalDate.now();
        tf.fromDate = tf.toDate.withDayOfYear(1);
        return tf;
    }

    public static TimeFilter custom(LocalDate from, LocalDate to) {
        return new TimeFilter(from, to);
    }

    // Getters and Setters
    public TimeMode getMode() {
        return mode;
    }

    public void setMode(TimeMode mode) {
        this.mode = mode;
        calculateFromDate();
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
