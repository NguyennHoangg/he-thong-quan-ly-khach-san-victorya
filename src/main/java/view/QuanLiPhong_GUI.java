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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.Objects;
import java.util.function.Predicate;

public class QuanLiPhong_GUI extends BorderPane {

    // ===== Form nhập =====
    private final TextField tfSoPhong = new TextField();
    private final TextField tfTang = new TextField();
    private final ComboBox<RoomType> cbLoaiPhong = new ComboBox<>();
    private final ComboBox<Status> cbTrangThai = new ComboBox<>();
    private final TextField tfGia = new TextField();
    private final Button btnLuu = new Button("Lưu");

    // bộ lọc
    private final TextField tfSearchRow = new TextField();
    private final ComboBox<RoomType> cbLoaiPhongFilter = new ComboBox<>();
    private final ComboBox<Status> cbTrangThaiFilter = new ComboBox<>();
    private final ComboBox<String> cbTangFilter = new ComboBox<>();

    // Bảng
    private final TableView<Room> table = new TableView<>();
    private final ObservableList<Room> master = FXCollections.observableArrayList();

    public QuanLiPhong_GUI() {
        setPadding(new Insets(16));
        setCenter(buildCenter());
        seedData();
        hookEvents();

    }

    private Node buildCenter() {
        VBox root = new VBox(12);
        root.getChildren().addAll(buildForm(), buildFilterBar(), buildTable());
        return root;
    }

    private Node buildForm() {
        tfSoPhong.setPromptText("Nhập số phòng");
        tfTang.setPromptText("Nhập tầng (ví dụ: Tầng -1)");
        tfGia.setPromptText("VND");

        cbLoaiPhong.setItems(FXCollections.observableArrayList(RoomType.values()));

        cbLoaiPhong.setConverter(new StringConverter<>() {
            @Override public String toString(RoomType t){ return t==null? "Loại phòng" : t.label; }
            @Override public RoomType fromString(String s){ return null; }
        });
        cbTrangThai.setItems(FXCollections.observableArrayList(Status.values()));
        cbTrangThai.setMaxWidth(350);
        cbLoaiPhong.setMaxWidth(350);
        cbTrangThai.setConverter(new StringConverter<>() {

            @Override public String toString(Status s){
                return s==null? "Trạng thái" : s.label; }
            @Override public Status fromString(String s){
                return null; }
        });

        // một ít style inline cơ bản cho nút/ô nhập
        btnLuu.setDefaultButton(true);
        btnLuu.setStyle("-fx-background-color:#0066ff; -fx-text-fill:white; -fx-background-radius:6;");
        for (TextField tf : new TextField[]{tfSoPhong, tfTang, tfGia}) {
            tf.setStyle("-fx-background-color:#f9f9f9; -fx-border-color:#dcdcdc; -fx-border-radius:4; -fx-background-radius:4;");
        }
        cbLoaiPhong.setStyle("-fx-background-color:#f9f9f9; -fx-border-color:#dcdcdc; -fx-border-radius:4; -fx-background-radius:4;");
        cbTrangThai.setStyle("-fx-background-color:#f9f9f9; -fx-border-color:#dcdcdc; -fx-border-radius:4; -fx-background-radius:4;-fx-arc-width: 300");

        GridPane g = new GridPane();
        g.setHgap(16); g.setVgap(10);
        g.add(col("Số phòng", tfSoPhong), 0, 0);
        g.add(col("Tầng", tfTang), 1, 0);
        g.add(col("Loại phòng", cbLoaiPhong), 0, 1);
        g.add(col("Trạng thái", cbTrangThai), 1, 1);
        g.add(col("Giá", tfGia), 0, 2);
        g.add(btnLuu, 0, 3);
        GridPane.setMargin(btnLuu, new Insets(4,0,0,0));
        return g;
    }

    private VBox col(String label, Node control){
        Label lb = new Label(label);
        VBox box = new VBox(6, lb, control);
        box.setPrefWidth(360);
        return box;
    }

    private Node buildFilterBar() {
        tfSearchRow.setPromptText("Số phòng");
        cbLoaiPhongFilter.setPromptText("Loại phòng");
        cbLoaiPhongFilter.setItems(FXCollections.observableArrayList(RoomType.values()));
        cbLoaiPhongFilter.setConverter(new StringConverter<>() {
            @Override public String toString(RoomType t){ return t==null? "Loại phòng" : t.label; }
            @Override public RoomType fromString(String s){ return null; }
        });

        cbTrangThaiFilter.setPromptText("Trạng thái");


        cbTrangThaiFilter.setItems(FXCollections.observableArrayList(Status.values()));
        cbTrangThaiFilter.setConverter(new StringConverter<>() {
            @Override public String toString(Status s){ return s==null? "Trạng thái" : s.label; }
            @Override public Status fromString(String s){ return null; }
        });

        cbTangFilter.setPromptText("Tầng");
        cbTangFilter.getItems().addAll("Tất cả", "Tầng -1", "Tầng -2", "Floor -1", "Floor -2");
        cbTangFilter.getSelectionModel().selectFirst();

        // style nhẹ cho filter bar
        tfSearchRow.setStyle("-fx-background-color:#ffffff; -fx-border-color:#e6e6e6; -fx-border-radius:18; -fx-background-radius:18; -fx-padding:6 12;");
        for (ComboBox<?> cb : new ComboBox[]{cbLoaiPhongFilter, cbTrangThaiFilter, cbTangFilter}) {
            cb.setStyle("-fx-background-color:#ffffff; -fx-border-color:#e6e6e6; -fx-border-radius:18; -fx-background-radius:18; -fx-padding:2 8;");
        }

        HBox bar = new HBox(10, tfSearchRow, cbLoaiPhongFilter, cbTrangThaiFilter, cbTangFilter);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(8, 0, 8, 0));
        return bar;
    }

    private Node buildTable() {
        TableColumn<Room, String> cSoPhong = new TableColumn<>("Số phòng");
        cSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        cSoPhong.setPrefWidth(100);

        TableColumn<Room, String> cLoaiPhong = new TableColumn<>("Loại phòng");
        cLoaiPhong.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLoaiPhong().label));
        cLoaiPhong.setPrefWidth(140);

        TableColumn<Room, String> cTang = new TableColumn<>("Tầng");
        cTang.setCellValueFactory(new PropertyValueFactory<>("tang"));
        cTang.setPrefWidth(100);

        TableColumn<Room, String> cDichVu = new TableColumn<>("Dịch vụ");
        cDichVu.setCellValueFactory(new PropertyValueFactory<>("dichVu"));
        cDichVu.setPrefWidth(320);

        TableColumn<Room, Status> cTrangThai = new TableColumn<>("Trạng thái");
        cTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        cTrangThai.setPrefWidth(120);
        cTrangThai.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(status.label);
                badge.setStyle(badgeStyle(status));
                setGraphic(badge);
                setText(null);
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        table.getColumns().addAll(cSoPhong, cLoaiPhong, cTang, cDichVu, cTrangThai);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(420);

        // Filter
        FilteredList<Room> filtered = new FilteredList<>(master);
        tfSearchRow.textProperty().addListener((obs, o, n) -> filtered.setPredicate(makePredicate()));
        cbLoaiPhongFilter.valueProperty().addListener((obs,o,n) -> filtered.setPredicate(makePredicate()));
        cbTrangThaiFilter.valueProperty().addListener((obs,o,n) -> filtered.setPredicate(makePredicate()));
        cbTangFilter.valueProperty().addListener((obs,o,n) -> filtered.setPredicate(makePredicate()));

        SortedList<Room> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        return new VBox(new Separator(), table);
    }

    // Style badge inline theo trạng thái
    private String badgeStyle(Status st){
        String base = "-fx-padding:2 8 2 8; -fx-background-radius:10; -fx-text-fill:white; -fx-font-size:12px;";
        String color;
        switch (st){
            case AVAILABLE -> color = "#4CAF50"; // xanh lá
            case BOOKED    -> color = "#f44336"; // đỏ
            case RESERVED  -> color = "#2196F3"; // xanh dương
            case WAITLIST  -> color = "#ff9800"; // cam
            default        -> color = "#9e9e9e";
        }
        return base + " -fx-background-color:" + color + ";";
    }

    private Predicate<Room> makePredicate() {
        final String q = safe(tfSearchRow.getText());
        final RoomType loai = cbLoaiPhongFilter.getValue();
        final Status st = cbTrangThaiFilter.getValue();
        final String tangSel = cbTangFilter.getValue();

        return r -> {
            boolean okQ = q.isBlank() || r.getSoPhong().toLowerCase().contains(q);
            boolean okLoai = (loai == null) || r.getLoaiPhong() == loai;
            boolean okSt = (st == null) || r.getTrangThai() == st;
            boolean okTang = (tangSel == null || tangSel.equals("Tất cả") || r.getTang().equalsIgnoreCase(tangSel));
            return okQ && okLoai && okSt && okTang;
        };
    }

    private static String safe(String s){ return s==null? "" : s.trim().toLowerCase(); }

    private void seedData() {
        master.addAll(
                new Room("#001", RoomType.VIP, "Tầng -1", "AC, shower, Double bed, towel bathtub, TV", Status.AVAILABLE, 950_000),
                new Room("#002", RoomType.THƯỜNG, "Tầng -2", "AC, shower, Double bed, towel bathtub, TV", Status.BOOKED, 550_000),
                new Room("#003", RoomType.VIP, "Floor -1", "AC, shower, Double bed, towel bathtub, TV", Status.BOOKED, 980_000),
                new Room("#004", RoomType.VIP, "Floor -1", "AC, shower, Double bed, towel bathtub, TV", Status.RESERVED, 1_000_000),
                new Room("#005", RoomType.SINGLE, "Floor -1", "AC, shower, Double bed, towel bathtub, TV", Status.RESERVED, 400_000),
                new Room("#006", RoomType.DOUBLE, "Floor -2", "AC, shower, Double bed, towel bathtub, TV", Status.WAITLIST, 650_000)
        );
    }

    private void hookEvents() {
        tfSearchRow.setOnKeyPressed(e -> { if (e.getCode()== KeyCode.ENTER) table.requestFocus(); });

        btnLuu.setOnAction(e -> {
            String so = tfSoPhong.getText().trim();
            String tang = tfTang.getText().trim();
            RoomType loai = cbLoaiPhong.getValue();
            Status st = cbTrangThai.getValue();
            long gia = parseLongOrZero(tfGia.getText());

            if (so.isEmpty() || tang.isEmpty() || loai == null || st == null) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đủ Số phòng, Tầng, Loại phòng, Trạng thái.").showAndWait();
                return;
            }

            Room exist = master.stream().filter(r -> Objects.equals(r.getSoPhong(), so)).findFirst().orElse(null);
            if (exist == null) {
                master.add(new Room(so, loai, tang, "AC, shower, Double bed, towel bathtub, TV", st, gia));
            } else {
                exist.setLoaiPhong(loai);
                exist.setTang(tang);
                exist.setTrangThai(st);
                exist.setGia(gia);
            }
            table.refresh();
            clearForm();
        });
    }

    private void clearForm(){
        tfSoPhong.clear(); tfTang.clear(); cbLoaiPhong.getSelectionModel().clearSelection();
        cbTrangThai.getSelectionModel().clearSelection(); tfGia.clear();
    }

    private long parseLongOrZero(String s){
        try { return Long.parseLong(s.replaceAll("[^0-9]","")); } catch (Exception e){ return 0L; }
    }

    // Không dùng CSS ngoài
    private void applyStyle() {
        // no-op: giữ lại để dễ bật CSS nếu sau này bạn muốn
        // System.out.println("Không dùng CSS ngoài");
    }

    /* ================= Model & Enums ================= */

    public enum RoomType {
        VIP("VIP"), THƯỜNG("Thường"), SINGLE("Single bed"), DOUBLE("Double bed");
        public final String label; RoomType(String l){ this.label = l; }
    }
    public enum Status {
        AVAILABLE("Available"), BOOKED("Booked"), RESERVED("Reserved"), WAITLIST("Waitlist");
        public final String label; Status(String l){ this.label = l; }
    }

    public static class Room {
        private final StringProperty soPhong = new SimpleStringProperty();
        private final ObjectProperty<RoomType> loaiPhong = new SimpleObjectProperty<>();
        private final StringProperty tang = new SimpleStringProperty();
        private final StringProperty dichVu = new SimpleStringProperty();
        private final ObjectProperty<Status> trangThai = new SimpleObjectProperty<>();
        private final LongProperty gia = new SimpleLongProperty();

        public Room(String so, RoomType lp, String tg, String dv, Status st, long gia){
            setSoPhong(so); setLoaiPhong(lp); setTang(tg); setDichVu(dv); setTrangThai(st); setGia(gia);
        }

        public String getSoPhong() { return soPhong.get(); }
        public void setSoPhong(String v) { soPhong.set(v); }
        public RoomType getLoaiPhong() { return loaiPhong.get(); }
        public void setLoaiPhong(RoomType v) { loaiPhong.set(v); }
        public String getTang() { return tang.get(); }
        public void setTang(String v) { tang.set(v); }
        public String getDichVu() { return dichVu.get(); }
        public void setDichVu(String v) { dichVu.set(v); }
        public Status getTrangThai() { return trangThai.get(); }
        public void setTrangThai(Status v) { trangThai.set(v); }
        public long getGia() { return gia.get(); }
        public void setGia(long v) { gia.set(v); }
    }
}
