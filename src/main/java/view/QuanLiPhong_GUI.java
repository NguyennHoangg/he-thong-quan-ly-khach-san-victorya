package view;

import controller.QuanLiPhong_Controller;
import model.LoaiPhong;
import model.Phong;

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

import java.text.NumberFormat; // >>> format VND
import java.util.Locale; // >>> format VND
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class QuanLiPhong_GUI extends BorderPane {

    /* ========== Controller & state ========== */
    private final QuanLiPhong_Controller controller = new QuanLiPhong_Controller();
    private String selectedMaPhong = null; // row đang chọn (để update/delete)

    /* ========== Form nhập ========== */
    private final TextField tfSoPhong = new TextField();
    private final TextField tfTang = new TextField();
    private final ComboBox<LoaiPhong> cbLoaiPhong = new ComboBox<>();
    private final ComboBox<String> cbTrangThai = new ComboBox<>();
    private final TextField tfGia = new TextField();
    private final Button btnLuu = new Button("Lưu");
    private final Button btnXoa = new Button("Xóa");
    private final Button btnMoi = new Button("Mới");

    /* ========== Bộ lọc ========== */
    private final TextField tfSearchRow = new TextField();
    private final ComboBox<LoaiPhong> cbLoaiPhongFilter = new ComboBox<>();
    private final ComboBox<String> cbTrangThaiFilter = new ComboBox<>();
    private final ComboBox<String> cbTangFilter = new ComboBox<>();

    /* ========== Bảng ========== */
    private final TableView<Room> table = new TableView<>();
    private final ObservableList<Room> master = FXCollections.observableArrayList();

    public QuanLiPhong_GUI() {
        setPadding(new Insets(16));
        setCenter(buildCenter());
        hookEvents();
        loadRoomTypes();
        loadRooms();
    }

    private Node buildCenter() {
        VBox root = new VBox(12);
        root.getChildren().addAll(buildForm(), buildFilterBar(), buildTable());
        return root;
    }

    private Node buildForm() {
        tfSoPhong.setPromptText("Nhập số phòng (map DB: soPhong)");
        tfTang.setPromptText("Nhập tầng (ví dụ: 1)");
        tfGia.setPromptText("Giá loại phòng (VND)");
        tfGia.setEditable(false); // >>> giá phụ thuộc loại phòng, không cho sửa tay

        cbLoaiPhong.setConverter(new StringConverter<>() {
            @Override
            public String toString(LoaiPhong lp) {
                return lp == null ? "Loại phòng" : lp.getTenLoaiPhong();
            }

            @Override
            public LoaiPhong fromString(String s) {
                return null;
            }
        });

        cbTrangThai.setItems(FXCollections.observableArrayList("Trống", "Đã đặt"));
        cbTrangThai.setConverter(new StringConverter<>() {
            @Override
            public String toString(String s) {
                return s == null ? "Trạng thái" : s;
            }

            @Override
            public String fromString(String s) {
                return s;
            }
        });

        // ======= chỉnh size cho đều =======
        cbLoaiPhong.setPrefWidth(400);
        cbLoaiPhong.setMinWidth(400);
        cbLoaiPhong.setMaxWidth(400);

        cbTrangThai.setPrefWidth(400);
        cbTrangThai.setMinWidth(400);
        cbTrangThai.setMaxWidth(400);

        tfSoPhong.setPrefWidth(300);
        tfTang.setPrefWidth(120);
        tfGia.setPrefWidth(220);
        // ==================================

        btnLuu.setDefaultButton(true);
        btnLuu.setStyle("-fx-background-color:#0066ff; -fx-text-fill:white; -fx-background-radius:6;");
        btnXoa.setStyle("-fx-background-color:#f44336; -fx-text-fill:white; -fx-background-radius:6;");
        btnMoi.setStyle("-fx-background-color:#9e9e9e; -fx-text-fill:white; -fx-background-radius:6;");

        GridPane g = new GridPane();
        g.setHgap(16);
        g.setVgap(10);
        g.add(col("Số phòng", tfSoPhong), 0, 0);
        g.add(col("Tầng", tfTang), 1, 0);
        g.add(col("Loại phòng", cbLoaiPhong), 0, 1);
        g.add(col("Trạng thái", cbTrangThai), 1, 1);
        g.add(col("Giá loại phòng", tfGia), 0, 2); // >>> chỉ hiển thị

        HBox actions = new HBox(10, btnLuu, btnXoa, btnMoi);
        g.add(actions, 0, 3);
        GridPane.setMargin(actions, new Insets(4, 0, 0, 0));

        return g;
    }

    private VBox col(String label, Node control) {
        Label lb = new Label(label);
        VBox box = new VBox(6, lb, control);
        box.setPrefWidth(360);
        return box;
    }

    private Node buildFilterBar() {
        tfSearchRow.setPromptText("Tìm theo số phòng");
        cbLoaiPhongFilter.setPromptText("Loại phòng");
        cbLoaiPhongFilter.setConverter(new StringConverter<>() {
            @Override
            public String toString(LoaiPhong t) {
                return t == null ? "Loại phòng" : t.getTenLoaiPhong();
            }

            @Override
            public LoaiPhong fromString(String s) {
                return null;
            }
        });

        cbTrangThaiFilter.setPromptText("Trạng thái");
        cbTrangThaiFilter.setItems(FXCollections.observableArrayList("Trống", "Đã đặt"));
        cbTrangThaiFilter.setConverter(new StringConverter<>() {
            @Override
            public String toString(String s) {
                return s == null ? "Trạng thái" : s;
            }

            @Override
            public String fromString(String s) {
                return s;
            }
        });

        cbTangFilter.setPromptText("Tầng");
        cbTangFilter.getItems().addAll("Tất cả", "Tầng 0", "Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4", "Tầng 5");
        cbTangFilter.getSelectionModel().selectFirst();

        HBox bar = new HBox(10, tfSearchRow, cbLoaiPhongFilter, cbTrangThaiFilter, cbTangFilter);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(8, 0, 8, 0));
        return bar;
    }

    private Node buildTable() {
        TableColumn<Room, String> cSoPhong = new TableColumn<>("Phòng");
        cSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        cSoPhong.setPrefWidth(120);

        TableColumn<Room, String> cLoaiPhong = new TableColumn<>("Loại phòng");
        cLoaiPhong.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getLoaiPhong() != null ? cell.getValue().getLoaiPhong().getTenLoaiPhong() : ""));
        cLoaiPhong.setPrefWidth(180);

        TableColumn<Room, String> cTang = new TableColumn<>("Tầng");
        cTang.setCellValueFactory(new PropertyValueFactory<>("tang"));
        cTang.setPrefWidth(100);

        TableColumn<Room, String> cTrangThai = new TableColumn<>("Trạng thái");
        cTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        cTrangThai.setPrefWidth(120);
        cTrangThai.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label badge = new Label(status);
                badge.setStyle(badgeStyle(status));
                setGraphic(badge);
                setText(null);
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        TableColumn<Room, Number> cGia = new TableColumn<>("Giá (VND)");
        cGia.setCellValueFactory(new PropertyValueFactory<>("gia"));
        cGia.setPrefWidth(140);

        table.getColumns().addAll(cSoPhong, cLoaiPhong, cTang, cTrangThai, cGia);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(440);

        // Filter
        FilteredList<Room> filtered = new FilteredList<>(master);
        tfSearchRow.textProperty().addListener((obs, o, n) -> filtered.setPredicate(makePredicate()));
        cbLoaiPhongFilter.valueProperty().addListener((obs, o, n) -> filtered.setPredicate(makePredicate()));
        cbTrangThaiFilter.valueProperty().addListener((obs, o, n) -> filtered.setPredicate(makePredicate()));
        cbTangFilter.valueProperty().addListener((obs, o, n) -> filtered.setPredicate(makePredicate()));

        SortedList<Room> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        // Chọn dòng -> đổ lên form
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                selectedMaPhong = newV.getMaPhong();
                tfSoPhong.setText(newV.getSoPhong());
                tfTang.setText(newV.getTang().replaceAll("[^0-9-]", "")); // chỉ số tầng
                cbLoaiPhong.getSelectionModel().select(newV.getLoaiPhong());
                cbTrangThai.getSelectionModel().select(newV.getTrangThai());
                tfGia.setText(formatVnd(newV.getGia())); // >>> hiển thị đẹp
            }
        });

        return new VBox(new Separator(), table);
    }

    /* ====== Helpers ====== */
    private String badgeStyle(String st) {
        String base = "-fx-padding:2 8; -fx-background-radius:10; -fx-text-fill:white; -fx-font-size:12px;";
        String color = "#9e9e9e";
        if ("Trống".equalsIgnoreCase(st))
            color = "#4CAF50";
        else if ("Đã đặt".equalsIgnoreCase(st))
            color = "#f44336";
        return base + " -fx-background-color:" + color + ";";
    }

    private Predicate<Room> makePredicate() {
        final String q = safe(tfSearchRow.getText());
        final LoaiPhong loai = cbLoaiPhongFilter.getValue();
        final String st = cbTrangThaiFilter.getValue();
        final String tangSel = cbTangFilter.getValue();

        return r -> {
            boolean okQ = q.isBlank() || r.getSoPhong().toLowerCase().contains(q);
            boolean okLoai = (loai == null) || (r.getLoaiPhong() != null &&
                    loai.getMaLoaiPhong().equals(r.getLoaiPhong().getMaLoaiPhong()));
            boolean okSt = (st == null) || st.equalsIgnoreCase(r.getTrangThai());
            boolean okTang = (tangSel == null || tangSel.equals("Tất cả") || r.getTang().equalsIgnoreCase(tangSel));
            return okQ && okLoai && okSt && okTang;
        };
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    // >>> format VND dùng cho hiển thị (nhận Number: long/double đều được)
    private static String formatVnd(Number value) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        long v = value == null ? 0L : Math.round(value.doubleValue());
        return nf.format(v);
    }

    private void hookEvents() {
        tfSearchRow.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                table.requestFocus();
        });

        // >>> Khi chọn Loại phòng thì tự set giá lên field (không cho sửa tay)
        cbLoaiPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldLp, newLp) -> {
            if (newLp != null) {
                tfGia.setText(formatVnd(newLp.getGia())); // newLp.getGia() (double/BigDecimal tuỳ model)
            } else {
                tfGia.clear();
            }
        });

        btnLuu.setOnAction(e -> {
            try {
                String so = tfSoPhong.getText().trim();
                String tangStr = tfTang.getText().trim();
                LoaiPhong loai = cbLoaiPhong.getValue();
                String st = cbTrangThai.getValue();
                // >>> KHÔNG đọc giá từ tfGia nữa (giá phụ thuộc Loại phòng)
                // long gia = parseLongOrZero(tfGia.getText()); // <— XOÁ

                if (so.isEmpty() || tangStr.isEmpty() || loai == null || st == null) {
                    new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đủ Tên/Số phòng, Tầng, Loại phòng, Trạng thái.")
                            .showAndWait();
                    return;
                }

                int tang = parseTang(tangStr);

                if (selectedMaPhong == null) {
                    // THÊM
                    Phong p = new Phong(
                            null,
                            so,
                            new LoaiPhong(loai.getMaLoaiPhong(), loai.getTenLoaiPhong(), loai.getGia(),
                                    loai.getNgayTao()),
                            st,
                            tang);
                    String newId = controller.addRoom(p);
                    loadRooms();
                    selectRowById(newId);
                } else {
                    // SỬA
                    Phong p = new Phong(
                            selectedMaPhong,
                            so,
                            new LoaiPhong(loai.getMaLoaiPhong(), loai.getTenLoaiPhong(), loai.getGia(),
                                    loai.getNgayTao()),
                            st,
                            tang);
                    controller.updateRoom(p);
                    loadRooms();
                    selectRowById(selectedMaPhong);
                }
                clearForm();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi lưu: " + ex.getMessage()).showAndWait();
            }
        });

        btnXoa.setOnAction(e -> handleDelete());
        btnMoi.setOnAction(e -> clearForm());
    }

    private void clearForm() {
        selectedMaPhong = null;
        table.getSelectionModel().clearSelection();
        tfSoPhong.clear();
        tfTang.clear();
        tfGia.clear();
        cbLoaiPhong.getSelectionModel().clearSelection();
        cbTrangThai.getSelectionModel().clearSelection();
    }

    private long parseLongOrZero(String s) {
        try {
            return Long.parseLong(s.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0L;
        }
    }

    private int parseTang(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private void loadRoomTypes() {
        var types = FXCollections.observableArrayList(controller.getAllRoomTypes());
        cbLoaiPhong.setItems(types);
        cbLoaiPhongFilter.setItems(types);
    }

    private void loadRooms() {
        var rooms = controller.getAllRooms().stream().map(this::toRoom).collect(Collectors.toList());
        master.setAll(rooms);
    }

    private void selectRowById(String maPhong) {
        if (maPhong == null)
            return;
        for (Room r : table.getItems()) {
            if (maPhong.equals(r.getMaPhong())) {
                table.getSelectionModel().select(r);
                table.scrollTo(r);
                break;
            }
        }
    }

    private void handleDelete() {
        Room r = table.getSelectionModel().getSelectedItem();
        if (r == null) {
            new Alert(Alert.AlertType.WARNING, "Chọn 1 phòng để xóa.").showAndWait();
            return;
        }
        var conf = new Alert(Alert.AlertType.CONFIRMATION, "Xóa phòng " + r.getSoPhong() + "?", ButtonType.OK,
                ButtonType.CANCEL);
        conf.showAndWait();
        if (conf.getResult() == ButtonType.OK) {
            try {
                controller.deleteRoom(r.getMaPhong());
                loadRooms();
                clearForm();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Không thể xóa: " + ex.getMessage()).showAndWait();
            }
        }
    }

    private Room toRoom(Phong p) {
        String tg = "Tầng " + p.getTang();
        long giaVnd = Math.round(p.getLoaiPhong() != null ? p.getLoaiPhong().getGia() : 0); // >>> lấy giá từ loại phòng
        return new Room(p.getMaPhong(), p.getSoPhong(), p.getLoaiPhong(), tg, p.getTrangThai(), giaVnd);
    }

    /* ========== ViewModel hiển thị trên TableView ========== */
    public static class Room {
        private final StringProperty maPhong = new SimpleStringProperty();
        private final StringProperty soPhong = new SimpleStringProperty(); // map DB: tenPhong
        private final ObjectProperty<LoaiPhong> loaiPhong = new SimpleObjectProperty<>();
        private final StringProperty tang = new SimpleStringProperty();
        private final StringProperty trangThai = new SimpleStringProperty();
        private final LongProperty gia = new SimpleLongProperty();

        public Room(String id, String so, LoaiPhong lp, String tg, String st, long gia) {
            setMaPhong(id);
            setSoPhong(so);
            setLoaiPhong(lp);
            setTang(tg);
            setTrangThai(st);
            setGia(gia);
        }

        public String getMaPhong() {
            return maPhong.get();
        }

        public void setMaPhong(String v) {
            maPhong.set(v);
        }

        public String getSoPhong() {
            return soPhong.get();
        }

        public void setSoPhong(String v) {
            soPhong.set(v);
        }

        public LoaiPhong getLoaiPhong() {
            return loaiPhong.get();
        }

        public void setLoaiPhong(LoaiPhong v) {
            loaiPhong.set(v);
        }

        public String getTang() {
            return tang.get();
        }

        public void setTang(String v) {
            tang.set(v);
        }

        public String getTrangThai() {
            return trangThai.get();
        }

        public void setTrangThai(String v) {
            trangThai.set(v);
        }

        public long getGia() {
            return gia.get();
        }

        public void setGia(long v) {
            gia.set(v);
        }
    }
}
