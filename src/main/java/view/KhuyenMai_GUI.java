package view;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Predicate;

public class KhuyenMai_GUI extends BorderPane {

    // ===== Header search =====
    private final TextField tfSearchHeader = new TextField();

    // ===== Form =====
    private final TextField tfTen = new TextField();
    private final TextField tfSoTien = new TextField();
    private final ComboBox<RoomType> cbLoaiPhong = new ComboBox<>();
    private final ComboBox<Status> cbTrangThai = new ComboBox<>();
    private final DatePicker dpNgayBatDau = new DatePicker();
    private final DatePicker dpNgayKetThuc = new DatePicker();
    private final Button btnLuu = new Button("Lưu");

    // ===== Filter bar =====
    private final TextField tfSearchFilter = new TextField();
    private final ComboBox<RoomType> cbFilterLoaiPhong = new ComboBox<>();
    private final ComboBox<Status> cbFilterTrangThai = new ComboBox<>();
    private final DatePicker dpFilterNgayBD = new DatePicker();
    private final DatePicker dpFilterNgayKT = new DatePicker();

    // ===== Table =====
    private final TableView<Promotion> table = new TableView<>();
    private final ObservableList<Promotion> masterData = FXCollections.observableArrayList();
    private final FilteredList<Promotion> filtered = new FilteredList<>(masterData, p -> true);
    private final SortedList<Promotion> sorted = new SortedList<>(filtered);

    private final DateTimeFormatter dmy = DateTimeFormatter.ofPattern("d/M/yy");

    public KhuyenMai_GUI() {
        setPadding(new Insets(16, 24, 24, 24));
        setTop(buildTop());
        setCenter(buildCenter());
        initCombos();
        initTable();
        initActions();
        seedSampleData();
    }

    private Node buildTop() {
        // Header search
        tfSearchHeader.setPromptText("Search for rooms and offers");
        tfSearchHeader.setPrefWidth(420);
        tfSearchHeader.setStyle(
                "-fx-background-color:#f7fafc; -fx-background-radius:8; -fx-border-radius:8; -fx-padding:8 12;");

        HBox header = new HBox(wrapWithIcon("\uD83D\uDD0D", tfSearchHeader));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 12, 0));

        // Form
        GridPane form = new GridPane();
        form.setHgap(24);
        form.setVgap(12);
        form.setPadding(new Insets(12, 0, 0, 0));

        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c1.setPrefWidth(340);
        c2.setPrefWidth(340);
        form.getColumnConstraints().addAll(c1, c2);

        int r = 0;
        form.add(label("Tên khuyến mãi"), 0, r);
        styleInput(tfTen, "Nhập tên khuyến mãi");
        form.add(tfTen, 0, ++r);

        form.add(label("Số tiền áp dụng"), 1, 0);
        styleInput(tfSoTien, "Nhập số tiền áp dụng");
        form.add(tfSoTien, 1, 1);

        form.add(label("Loại phòng áp dụng"), 0, ++r);
        styleCombo(cbLoaiPhong, "Loại phòng");
        form.add(cbLoaiPhong, 0, ++r);

        form.add(label("Trạng thái"), 1, 2);
        styleCombo(cbTrangThai, "Trạng thái");
        form.add(cbTrangThai, 1, 3);

        form.add(label("Ngày bắt đầu"), 0, ++r);
        styleDate(dpNgayBatDau, "");
        form.add(dpNgayBatDau, 0, ++r);

        form.add(label("Ngày kết thúc"), 1, 4);
        styleDate(dpNgayKetThuc, "");
        form.add(dpNgayKetThuc, 1, 5);

        btnLuu.setStyle(
                "-fx-background-color:#155EEB; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 16;-fx-opacity: 1;");
        GridPane.setMargin(btnLuu, new Insets(0, 0, 20, 0)); // bottom = 20px
        form.add(btnLuu, 0, 6);
        VBox top = new VBox(header, form);
        top.setSpacing(16);
        return top;
    }

    private Node buildCenter() {
        tfSearchFilter.setPromptText("Tên");
        tfSearchFilter.setPrefWidth(230);
        styleComboPill(cbFilterLoaiPhong, "Loại phòng");
        styleComboPill(cbFilterTrangThai, "Trạng thái");
        styleDatePill(dpFilterNgayBD, "Ngày bắt đầu");
        styleDatePill(dpFilterNgayKT, "Ngày kết thúc");

        HBox filters = new HBox(
                pill(wrapWithIcon("\uD83D\uDD0D", tfSearchFilter)),
                pill(cbFilterLoaiPhong),
                pill(cbFilterTrangThai),
                pill(wrapWithIcon("📅", dpFilterNgayBD)),
                pill(wrapWithIcon("📅", dpFilterNgayKT)));
        filters.setSpacing(10);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(14));
        filters.setStyle(
                "-fx-background-color:#f7fafc; -fx-background-radius:10; -fx-border-color:#e8edf3; -fx-border-radius:10;");

        VBox center = new VBox(filters, table);
        center.setSpacing(10);
        VBox.setVgrow(table, Priority.ALWAYS);
        return center;
    }

    private void initCombos() {
        cbLoaiPhong.setItems(FXCollections.observableArrayList(RoomType.values()));
        cbTrangThai.setItems(FXCollections.observableArrayList(Status.values()));
        cbFilterLoaiPhong.setItems(FXCollections.observableArrayList(RoomType.values()));
        cbFilterTrangThai.setItems(FXCollections.observableArrayList(Status.values()));

        cbLoaiPhong.setConverter(RoomType.converter());
        cbTrangThai.setConverter(Status.converter());
        cbFilterLoaiPhong.setConverter(RoomType.converter());
        cbFilterTrangThai.setConverter(Status.converter());
    }

    private void initTable() {
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Promotion, String> colMa = new TableColumn<>("Mã khuyến mãi");
        colMa.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Promotion, String> colTen = new TableColumn<>("Tên");
        colTen.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Promotion, Integer> colSoTien = new TableColumn<>("Số tiền áp dụng");
        colSoTien.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Promotion, String> colNgayKT = new TableColumn<>("Ngày kết thúc");
        colNgayKT.setCellValueFactory(cell -> new ReadOnlyStringWrapper(formatDate(cell.getValue().getEndDate())));

        TableColumn<Promotion, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getRoomType().label()));

        TableColumn<Promotion, Status> colTrangThai = new TableColumn<>("Trạng thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTrangThai.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Status st, boolean empty) {
                super.updateItem(st, empty);
                if (empty || st == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(chip(st.display(), st.color()));
            }
        });

        table.getColumns().add(colMa);
        table.getColumns().add(colTen);
        table.getColumns().add(colNgayKT);
        table.getColumns().add(colLoaiPhong);
        table.getColumns().add(colTrangThai);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
    }

    private void initActions() {
        btnLuu.disableProperty().bind(
                tfTen.textProperty().isEmpty()
                        .or(tfSoTien.textProperty().isEmpty())
                        .or(cbLoaiPhong.valueProperty().isNull())
                        .or(cbTrangThai.valueProperty().isNull()));

        btnLuu.setOnAction(e -> {
            try {
                int amount = Integer.parseInt(tfSoTien.getText().trim());
                String code = "#" + String.format("%04d", masterData.size() + 5644);
                Promotion p = new Promotion(code, tfTen.getText(), amount,
                        dpNgayBatDau.getValue(), dpNgayKetThuc.getValue(),
                        cbLoaiPhong.getValue(), cbTrangThai.getValue());
                masterData.add(0, p);
                clearForm();
            } catch (Exception ex) {
                alert("Số tiền không hợp lệ", "Vui lòng nhập số nguyên.");
            }
        });

        tfSearchFilter.textProperty().addListener((obs, o, n) -> applyFilters());
        cbFilterLoaiPhong.valueProperty().addListener((obs, o, n) -> applyFilters());
        cbFilterTrangThai.valueProperty().addListener((obs, o, n) -> applyFilters());
        dpFilterNgayBD.valueProperty().addListener((obs, o, n) -> applyFilters());
        dpFilterNgayKT.valueProperty().addListener((obs, o, n) -> applyFilters());
    }

    private void applyFilters() {
        String kw = tfSearchFilter.getText() == null ? "" : tfSearchFilter.getText().trim().toLowerCase();
        RoomType type = cbFilterLoaiPhong.getValue();
        Status st = cbFilterTrangThai.getValue();
        LocalDate from = dpFilterNgayBD.getValue();
        LocalDate to = dpFilterNgayKT.getValue();

        Predicate<Promotion> predicate = p -> {
            boolean okKw = kw.isEmpty() || p.getCode().toLowerCase().contains(kw)
                    || p.getName().toLowerCase().contains(kw);
            boolean okType = type == null || p.getRoomType() == type;
            boolean okSt = st == null || p.getStatus() == st;
            boolean okDate = true;
            if (from != null && p.getStartDate() != null)
                okDate &= !p.getStartDate().isBefore(from);
            if (to != null && p.getEndDate() != null)
                okDate &= !p.getEndDate().isAfter(to);
            return okKw && okType && okSt && okDate;
        };
        filtered.setPredicate(predicate);
    }

    private void seedSampleData() {
        masterData.addAll(
                new Promotion("#5644", "Family deal", 10,
                        LocalDate.of(2023, 3, 2), LocalDate.of(2023, 3, 21),
                        RoomType.VIP, Status.NOT_STARTED),
                new Promotion("#6112", "Christmas deal", 12,
                        LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 25),
                        RoomType.THUONG, Status.ENDED),
                new Promotion("#6141", "Family deal", 15,
                        LocalDate.of(2023, 3, 5), null,
                        RoomType.THUONG, Status.NOT_STARTED),
                new Promotion("#6535", "Black Friday", 10,
                        LocalDate.of(2023, 4, 15), LocalDate.of(2023, 5, 1),
                        RoomType.VIP, Status.ACTIVE));
    }

    // ===== Styles =====
    private static void styleInput(TextField tf, String prompt) {
        tf.setPromptText(prompt);
        tf.setPrefWidth(340);
        tf.setStyle(
                "-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:8 12;");
    }

    private static <T> void styleCombo(ComboBox<T> cb, String prompt) {
        cb.setPromptText(prompt);
        cb.setPrefWidth(340);
        cb.setStyle(
                "-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:4 10;");
    }

    private static void styleDate(DatePicker dp, String prompt) {
        dp.setPromptText(prompt);
        dp.setEditable(false);
        dp.setPrefWidth(340);
        dp.setStyle(
                "-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:6 10;");
    }

    private static <T> void styleComboPill(ComboBox<T> cb, String prompt) {
        cb.setPromptText(prompt);
        cb.setPrefWidth(140);
        cb.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");
    }

    private static void styleDatePill(DatePicker dp, String prompt) {
        dp.setPromptText(prompt);
        dp.setEditable(false);
        dp.setPrefWidth(140);
        dp.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");
    }

    private static Node pill(Node inner) {
        HBox box = new HBox(inner);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle(
                "-fx-background-color:white; -fx-border-color:#e6e9ee; -fx-background-radius:20; -fx-border-radius:20;");
        return box;
    }

    private static Label label(String text) {
        Label lb = new Label(text);
        lb.setStyle("-fx-font-size:12px; -fx-text-fill:#5b667a;");
        return lb;
    }

    private static HBox wrapWithIcon(String icon, Node node) {
        Label lb = new Label(icon);
        lb.setStyle("-fx-opacity:0.8;");
        HBox h = new HBox(lb, node);
        h.setSpacing(6);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private static StackPane chip(String text, Color color) {
        Label lb = new Label(text);
        lb.setPadding(new Insets(3, 8, 3, 8));
        lb.setStyle("-fx-font-size:12px;");
        lb.setTextFill(color);
        StackPane pill = new StackPane(lb);
        pill.setBackground(new Background(new BackgroundFill(
                Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.12),
                new CornerRadii(999), Insets.EMPTY)));
        return pill;
    }

    private void clearForm() {
        tfTen.clear();
        tfSoTien.clear();
        cbLoaiPhong.setValue(null);
        cbTrangThai.setValue(null);
        dpNgayBatDau.setValue(null);
        dpNgayKetThuc.setValue(null);
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String formatDate(LocalDate d) {
        return d == null ? "-" : d.format(dmy);
    }

    // ===== Models =====
    public enum RoomType {
        VIP("VIP"), THUONG("Thường");

        private final String label;

        RoomType(String l) {
            label = l;
        }

        public String label() {
            return label;
        }

        public static StringConverter<RoomType> converter() {
            return new StringConverter<>() {
                @Override
                public String toString(RoomType rt) {
                    return rt == null ? "" : rt.label();
                }

                @Override
                public RoomType fromString(String s) {
                    for (RoomType r : values())
                        if (Objects.equals(r.label(), s))
                            return r;
                    return null;
                }
            };
        }
    }

    public enum Status {
        NOT_STARTED("Chưa bắt đầu", Color.web("#3b82f6")),
        ENDED("Kết thúc", Color.web("#ef4444")),
        ACTIVE("Đang hoạt động", Color.web("#10b981"));

        private final String display;
        private final Color color;

        Status(String d, Color c) {
            display = d;
            color = c;
        }

        public String display() {
            return display;
        }

        public Color color() {
            return color;
        }

        public static StringConverter<Status> converter() {
            return new StringConverter<>() {
                @Override
                public String toString(Status s) {
                    return s == null ? "" : s.display();
                }

                @Override
                public Status fromString(String s) {
                    for (Status st : values())
                        if (Objects.equals(st.display(), s))
                            return st;
                    return null;
                }
            };
        }
    }

    public static class Promotion {
        private final StringProperty code = new SimpleStringProperty();
        private final StringProperty name = new SimpleStringProperty();
        private final IntegerProperty amount = new SimpleIntegerProperty();
        private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
        private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
        private final ObjectProperty<RoomType> roomType = new SimpleObjectProperty<>();
        private final ObjectProperty<Status> status = new SimpleObjectProperty<>();

        public Promotion(String c, String n, int a, LocalDate s, LocalDate e, RoomType r, Status st) {
            code.set(c);
            name.set(n);
            amount.set(a);
            startDate.set(s);
            endDate.set(e);
            roomType.set(r);
            status.set(st);
        }

        public String getCode() {
            return code.get();
        }

        public String getName() {
            return name.get();
        }

        public int getAmount() {
            return amount.get();
        }

        public LocalDate getStartDate() {
            return startDate.get();
        }

        public LocalDate getEndDate() {
            return endDate.get();
        }

        public RoomType getRoomType() {
            return roomType.get();
        }

        public Status getStatus() {
            return status.get();
        }
    }
}
