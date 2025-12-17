package view.QuanLy;

import controller.QuanLiPhong_Controller;
import model.LoaiPhong;
import model.Phong;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Predicate;

public class QuanLiPhong_GUI extends BorderPane {

    private static final String ID_TAT_CA = "__ALL__";
    private static final String TXT_TAT_CA = "Tất cả";

    private final QuanLiPhong_Controller controller = new QuanLiPhong_Controller();
    private String maPhongDangChon = null;

    // Form nhập
    private final TextField tfSoPhong = new TextField();

    private final ComboBox<String> cbTang = new ComboBox<>();
    private final ComboBox<LoaiPhong> cbLoaiPhong = new ComboBox<>();
    private final ComboBox<String> cbTrangThai = new ComboBox<>();
    private final ComboBox<String> cbTinhTrang = new ComboBox<>();
    private final TextField tfGia = new TextField();

    // Hiển thị sức chứa theo loại phòng
    private final TextField tfNguoiLonToiDa = new TextField();
    private final TextField tfTreEmToiDa = new TextField();

    // Mô tả phòng
    private final TextArea taMoTa = new TextArea();

    private final Button btnLuu = new Button("Lưu");
    private final Button btnXoa = new Button("Xóa đã chọn");
    private final Button btnMoi = new Button("Tải lại");

    // Bộ lọc
    private final TextField tfTimKiem = new TextField();
    private final ComboBox<LoaiPhong> cbLocLoaiPhong = new ComboBox<>();
    private final ComboBox<String> cbLocTrangThai = new ComboBox<>();
    private final ComboBox<String> cbLocTang = new ComboBox<>();
    private final ComboBox<String> cbLocTinhTrang = new ComboBox<>();

    // Bảng
    private final TableView<Phong> bang = new TableView<>();
    private final ObservableList<Phong> duLieuGoc = FXCollections.observableArrayList();

    public QuanLiPhong_GUI() {
        setPadding(new Insets(16, 24, 24, 24));
        setCenter(taoNoiDungChinh());
        ganSuKien();
        taiDanhSachLoaiPhong();
        taiDanhSachPhong();
        // đưa form về trạng thái "thêm mới": Trống + Tốt, khoá 2 combo
        lamMoiForm();
    }

    private Node taoNoiDungChinh() {
        VBox goc = new VBox(14);
        goc.getChildren().addAll(taoFormNhap(), taoThanhLoc(), taoBang());
        VBox.setVgrow(goc.getChildren().get(2), Priority.ALWAYS);
        return goc;
    }

    private Node taoFormNhap() {
        // TextField cơ bản
        tfSoPhong.setPromptText("Nhập tên/số phòng");
        tfGia.setPromptText("Giá loại phòng (VND)");
        tfGia.setEditable(false);

        // Tầng (ComboBox)
        cbTang.setPromptText("Tầng");
        cbTang.getItems().setAll(
                "Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4",
                "Tầng 5", "Tầng 6", "Tầng 7", "Tầng 8");
        cbTang.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText("Tầng");
                else
                    setText(item);
            }
        });// chưa có gì thì set là tânggf

        // Sức chứa (read-only)
        tfNguoiLonToiDa.setPromptText("Tự động theo loại phòng");
        tfNguoiLonToiDa.setEditable(false);
        tfTreEmToiDa.setPromptText("Tự động theo loại phòng");
        tfTreEmToiDa.setEditable(false);

        // Mô tả phòng
        taMoTa.setPromptText("Mô tả phòng (view, gần thang máy, có bồn tắm, giường extra, ...)");
        taMoTa.setWrapText(true);// tự động xuống dòng khi dài

        taMoTa.setPrefRowCount(3);

        // Loại phòng
        cbLoaiPhong.setConverter(new StringConverter<LoaiPhong>() {// chuyển về string để hiển thị
            @Override
            public String toString(LoaiPhong lp) {
                return lp == null ? "Loại phòng" : lp.getTenLoaiPhong();
            }

            @Override
            public LoaiPhong fromString(String s) {
                return null;
            }
        });

        // Trạng thái
        cbTrangThai.setItems(FXCollections.observableArrayList("Trống", "Đã đặt", "Đang ở"));
        cbTrangThai.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String s) {
                return s == null ? "Trạng thái" : s;
            }

            @Override
            public String fromString(String s) {
                return s;
            }
        });

        // Tình trạng
        cbTinhTrang.setItems(FXCollections.observableArrayList("Tốt", "Cần sửa chữa", "Đang sửa chữa"));
        cbTinhTrang.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String s) {
                return s == null ? "Tình trạng" : s;
            }

            @Override
            public String fromString(String s) {
                return s;
            }
        });

        // Nút
        btnLuu.setStyle(
                "-fx-background-color:#155EEB; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 18;");
        btnXoa.setStyle(
                "-fx-background-color:#f44336; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 18;");
        btnMoi.setStyle(
                "-fx-background-color:#9e9e9e; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 18;");

        btnLuu.textProperty().bind(
                Bindings.when(bang.getSelectionModel().selectedItemProperty().isNull())
                        .then("Thêm mới")
                        .otherwise("Cập nhật"));
        btnXoa.disableProperty().bind(Bindings.isEmpty(bang.getSelectionModel().getSelectedItems()));

        GridPane g = new GridPane();
        g.setHgap(18);
        g.setVgap(10);
        g.setPadding(new Insets(8, 0, 10, 0));

        // Cho 2 cột ăn đủ chiều ngang
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        c1.setHgrow(Priority.ALWAYS);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        c2.setHgrow(Priority.ALWAYS);
        g.getColumnConstraints().addAll(c1, c2);

        int row = 0;

        g.add(cot("Số phòng", tfSoPhong), 0, row);
        g.add(cot("Tầng", cbTang), 1, row);
        row++;

        g.add(cot("Loại phòng", cbLoaiPhong), 0, row);
        g.add(cot("Trạng thái", cbTrangThai), 1, row);
        row++;

        g.add(cot("Giá loại phòng", tfGia), 0, row);
        g.add(cot("Tình trạng", cbTinhTrang), 1, row);
        row++;

        g.add(cot("Người lớn tối đa", tfNguoiLonToiDa), 0, row);
        g.add(cot("Trẻ em tối đa", tfTreEmToiDa), 1, row);
        row++;

        // Mô tả – span 2 cột -> đỡ trống bên phải
        g.add(cot("Mô tả phòng", taMoTa), 0, row, 2, 1);
        row++;

        HBox nutHanhDong = new HBox(10, btnLuu, btnXoa, btnMoi);
        nutHanhDong.setAlignment(Pos.CENTER_LEFT);
        g.add(nutHanhDong, 0, row, 2, 1);
        GridPane.setMargin(nutHanhDong, new Insets(4, 0, 0, 0));

        return g;
    }

    private VBox cot(String nhan, Node dieuKhien) {
        Label lb = new Label(nhan);
        lb.setStyle("-fx-text-fill:#374151; -fx-font-size:12px;");
        if (dieuKhien instanceof Region r) {
            r.setMaxWidth(Double.MAX_VALUE);
        }
        VBox hop = new VBox(6, lb, dieuKhien);
        hop.setFillWidth(true);
        return hop;
    }

    private Node taoThanhLoc() {
        tfTimKiem.setPromptText("Tìm theo số phòng");
        tfTimKiem.setPrefWidth(180);

        // Loại phòng
        cbLocLoaiPhong.setPromptText("Loại phòng");
        cbLocLoaiPhong.setConverter(new StringConverter<LoaiPhong>() {
            @Override
            public String toString(LoaiPhong t) {
                return (t == null || t.getTenLoaiPhong() == null) ? "Loại phòng" : t.getTenLoaiPhong();
            }

            @Override
            public LoaiPhong fromString(String s) {
                return null;
            }
        });
        cbLocLoaiPhong.setButtonCell(new ListCell<LoaiPhong>() {
            @Override
            protected void updateItem(LoaiPhong item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getTenLoaiPhong() == null
                        || TXT_TAT_CA.equals(item.getTenLoaiPhong())) {
                    setText("Loại phòng");
                } else {
                    setText(item.getTenLoaiPhong());
                }
            }
        });

        // Trạng thái
        cbLocTrangThai.setPromptText("Trạng thái");
        cbLocTrangThai.setItems(FXCollections.observableArrayList(TXT_TAT_CA, "Trống", "Đã đặt", "Đang ở"));
        cbLocTrangThai.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || TXT_TAT_CA.equals(item))
                    setText("Trạng thái");
                else
                    setText(item);
            }
        });
        cbLocTrangThai.getSelectionModel().selectFirst();

        // Tầng
        cbLocTang.setPromptText("Tầng");
        cbLocTang.getItems().setAll(
                "Tất cả",
                "Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4",
                "Tầng 5", "Tầng 6", "Tầng 7", "Tầng 8");
        cbLocTang.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || TXT_TAT_CA.equals(item))
                    setText("Tầng");
                else
                    setText(item);
            }
        });
        cbLocTang.getSelectionModel().selectFirst();

        // Tình trạng
        cbLocTinhTrang.setPromptText("Tình trạng");
        cbLocTinhTrang.setItems(FXCollections.observableArrayList(TXT_TAT_CA, "Tốt", "Cần sửa chữa"));
        cbLocTinhTrang.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || TXT_TAT_CA.equals(item))
                    setText("Tình trạng");
                else
                    setText(item);
            }
        });
        cbLocTinhTrang.getSelectionModel().selectFirst();

        HBox thanh = new HBox(10, tfTimKiem, cbLocLoaiPhong, cbLocTrangThai, cbLocTinhTrang, cbLocTang);
        thanh.setAlignment(Pos.CENTER_LEFT);
        thanh.setPadding(new Insets(6, 0, 8, 0));
        return thanh;
    }

    private Node taoBang() {
        bang.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Phòng
        TableColumn<Phong, String> cotSoPhong = new TableColumn<>("Phòng");
        cotSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        cotSoPhong.setPrefWidth(120);

        // Loại phòng
        TableColumn<Phong, String> cotLoaiPhong = new TableColumn<>("Loại phòng");
        cotLoaiPhong.setCellValueFactory((CellDataFeatures<Phong, String> c) -> {
            LoaiPhong lp = c.getValue().getLoaiPhong();
            String ten = (lp != null) ? lp.getTenLoaiPhong() : "";
            return new ReadOnlyStringWrapper(ten);
        });
        cotLoaiPhong.setPrefWidth(180);

        // Tầng
        TableColumn<Phong, String> cotTang = new TableColumn<>("Tầng");
        cotTang.setCellValueFactory(c -> new ReadOnlyStringWrapper("Tầng " + c.getValue().getTang()));
        cotTang.setPrefWidth(90);

        // Giá
        TableColumn<Phong, Number> cotGia = new TableColumn<>("Giá (VND)");
        cotGia.setCellValueFactory(c -> {
            LoaiPhong lp = c.getValue().getLoaiPhong();
            double gia = (lp == null) ? 0d : lp.getGia();
            return new ReadOnlyDoubleWrapper(gia);
        });
        cotGia.setPrefWidth(140);
        cotGia.setCellFactory(col -> new TableCell<Phong, Number>() {
            @Override
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null)
                    setText(null);
                else
                    setText(dinhDangVND(v));
                setStyle("-fx-alignment: CENTER-RIGHT;");
            }
        });

        // Người lớn tối đa
        TableColumn<Phong, Number> cotNguoiLon = new TableColumn<>("Người lớn tối đa");
        cotNguoiLon.setCellValueFactory(c -> {
            LoaiPhong lp = c.getValue().getLoaiPhong();
            int v = (lp == null) ? 0 : lp.getSoNguoiLonToiDa();
            return new ReadOnlyIntegerWrapper(v);
        });
        cotNguoiLon.setPrefWidth(130);
        cotNguoiLon.setStyle("-fx-alignment: CENTER;");

        // Trẻ em tối đa
        TableColumn<Phong, Number> cotTreEm = new TableColumn<>("Trẻ em tối đa");
        cotTreEm.setCellValueFactory(c -> {
            LoaiPhong lp = c.getValue().getLoaiPhong();
            int v = (lp == null) ? 0 : lp.getSoTreEmToiDa();
            return new ReadOnlyIntegerWrapper(v);
        });
        cotTreEm.setPrefWidth(120);
        cotTreEm.setStyle("-fx-alignment: CENTER;");

        // Tổng số người
        TableColumn<Phong, Number> cotTongNguoi = new TableColumn<>("Tối đa (người)");
        cotTongNguoi.setCellValueFactory(c -> {
            LoaiPhong lp = c.getValue().getLoaiPhong();
            int tong = 0;
            if (lp != null) {
                tong = lp.getSoNguoiLonToiDa() + lp.getSoTreEmToiDa();
            }
            return new ReadOnlyIntegerWrapper(tong);
        });
        cotTongNguoi.setPrefWidth(130);
        cotTongNguoi.setStyle("-fx-alignment: CENTER;");

        // Trạng thái đặt
        TableColumn<Phong, String> cotTrangThai = new TableColumn<>("Trạng thái");
        cotTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        cotTrangThai.setPrefWidth(140);
        cotTrangThai.setCellFactory(tc -> new TableCell<Phong, String>() {
            @Override
            protected void updateItem(String st, boolean empty) {
                super.updateItem(st, empty);
                if (empty || st == null) {
                    setGraphic(null);
                } else {
                    Color mau;
                    String hienThi;
                    if (st.equalsIgnoreCase("Trống")) {
                        mau = Color.web("#10b981");
                        hienThi = "Trống";
                    } else if (st.equalsIgnoreCase("Đã đặt")) {
                        mau = Color.web("#ef4444");
                        hienThi = "Đã đặt";
                    } else {
                        mau = Color.web("#6b7280");
                        hienThi = st;
                    }
                    setGraphic(vienChip(hienThi, mau));
                }
            }
        });

        // Tình trạng vật lý
        TableColumn<Phong, String> cotTinhTrang = new TableColumn<>("Tình trạng");
        cotTinhTrang.setCellValueFactory(new PropertyValueFactory<>("tinhTrang"));
        cotTinhTrang.setPrefWidth(140);
        cotTinhTrang.setCellFactory(col -> new TableCell<Phong, String>() {
            @Override
            protected void updateItem(String tt, boolean empty) {
                super.updateItem(tt, empty);
                if (empty || tt == null) {
                    setGraphic(null);
                } else {
                    Color mau;
                    if (tt.equalsIgnoreCase("Tốt"))
                        mau = Color.web("#10b981");
                    else if (tt.equalsIgnoreCase("Cần sửa chữa"))
                        mau = Color.web("#f59e0b");
                    else
                        mau = Color.web("#6b7280");
                    setGraphic(vienChip(tt, mau));
                }
            }
        });

        bang.getColumns().setAll(
                cotSoPhong,
                cotLoaiPhong,
                cotTang,
                cotGia,
                cotNguoiLon,
                cotTreEm,
                cotTongNguoi,
                cotTrangThai,
                cotTinhTrang);

        bang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bang.setPrefHeight(440);

        // Lọc & sắp xếp
        final FilteredList<Phong> loc = new FilteredList<>(duLieuGoc);

        tfTimKiem.textProperty().addListener((o, oldVal, newVal) -> loc.setPredicate(taoDieuKienLoc()));
        cbLocLoaiPhong.valueProperty().addListener((o, ov, nv) -> loc.setPredicate(taoDieuKienLoc()));
        cbLocTrangThai.valueProperty().addListener((o, ov, nv) -> loc.setPredicate(taoDieuKienLoc()));
        cbLocTang.valueProperty().addListener((o, ov, nv) -> loc.setPredicate(taoDieuKienLoc()));
        cbLocTinhTrang.valueProperty().addListener((o, ov, nv) -> loc.setPredicate(taoDieuKienLoc()));

        SortedList<Phong> sapXep = new SortedList<>(loc);
        sapXep.comparatorProperty().bind(bang.comparatorProperty());
        bang.setItems(sapXep);

        // Chọn dòng -> đổ form (chế độ sửa)
        bang.getSelectionModel().selectedItemProperty().addListener((o, cu, moi) -> {
            if (moi != null) {
                maPhongDangChon = moi.getMaPhong();
                tfSoPhong.setText(moi.getSoPhong());
                cbTang.setValue("Tầng " + moi.getTang());
                cbLoaiPhong.getSelectionModel().select(moi.getLoaiPhong());
                cbTrangThai.getSelectionModel().select(moi.getTrangThai());
                cbTinhTrang.getSelectionModel().select(moi.getTinhTrang());

                LoaiPhong lp = moi.getLoaiPhong();
                if (lp != null) {
                    tfGia.setText(dinhDangVND(lp.getGia()));
                    tfNguoiLonToiDa.setText(String.valueOf(lp.getSoNguoiLonToiDa()));
                    tfTreEmToiDa.setText(String.valueOf(lp.getSoTreEmToiDa()));
                } else {
                    tfGia.clear();
                    tfNguoiLonToiDa.clear();
                    tfTreEmToiDa.clear();
                }

                taMoTa.setText(moi.getMoTa());

                // khi sửa thì cho phép chỉnh trạng thái & tình trạng
                cbTrangThai.setDisable(false);
                cbTinhTrang.setDisable(false);
            }
        });

        return new VBox(new Separator(), bang);
    }

    private Predicate<Phong> taoDieuKienLoc() {
        final String tuKhoa = layChuoi(tfTimKiem.getText());
        final LoaiPhong loai = cbLocLoaiPhong.getValue();
        final String tt = cbLocTrangThai.getValue();
        final String tang = cbLocTang.getValue();
        final String tinhTrang = cbLocTinhTrang.getValue();

        return r -> {
            boolean hopTuKhoa = tuKhoa.isBlank()
                    || (r.getSoPhong() != null && r.getSoPhong().toLowerCase().contains(tuKhoa));

            boolean hopLoai = (loai == null)
                    || ID_TAT_CA.equals(loai.getMaLoaiPhong())
                    || (r.getLoaiPhong() != null
                            && loai.getMaLoaiPhong().equals(r.getLoaiPhong().getMaLoaiPhong()));

            boolean hopTT = (tt == null) || TXT_TAT_CA.equals(tt)
                    || (r.getTrangThai() != null && tt.equalsIgnoreCase(r.getTrangThai()));

            String tangHienThi = "Tầng " + r.getTang();
            boolean hopTang = (tang == null) || TXT_TAT_CA.equals(tang)
                    || tangHienThi.equalsIgnoreCase(tang);

            boolean hopTinhTrang = (tinhTrang == null) || TXT_TAT_CA.equals(tinhTrang)
                    || (r.getTinhTrang() != null && tinhTrang.equalsIgnoreCase(r.getTinhTrang()));

            return hopTuKhoa && hopLoai && hopTT && hopTang && hopTinhTrang;
        };
    }

    private static String layChuoi(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private static String dinhDangVND(Number v) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        return nf.format(v == null ? 0 : v.doubleValue());
    }

    public static StackPane vienChip(String text, Color color) {
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

    private void ganSuKien() {
        tfTimKiem.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                bang.requestFocus();
        });

        // Chọn loại phòng -> cập nhật giá & sức chứa
        cbLoaiPhong.getSelectionModel().selectedItemProperty().addListener((o, cu, moi) -> {
            if (moi != null) {
                tfGia.setText(dinhDangVND(moi.getGia()));
                tfNguoiLonToiDa.setText(String.valueOf(moi.getSoNguoiLonToiDa()));
                tfTreEmToiDa.setText(String.valueOf(moi.getSoTreEmToiDa()));
            } else {
                tfGia.clear();
                tfNguoiLonToiDa.clear();
                tfTreEmToiDa.clear();
            }
        });

        btnLuu.setOnAction(e -> luuPhong());
        btnXoa.setOnAction(e -> xoaNhieuPhong());

        btnMoi.setOnAction(e -> {
            try {
                taiDanhSachPhong();
                bang.getSelectionModel().clearSelection();
                hienThongBao("Đã tải lại", "Danh sách phòng đã được tải lại từ CSDL.");
            } finally {
                lamMoiForm();
            }
        });
    }

    private void luuPhong() {
        try {
            String soPhong = tfSoPhong.getText().trim();
            String tangDisplay = cbTang.getValue(); // ví dụ: "Tầng 3"
            LoaiPhong loai = cbLoaiPhong.getValue();

            String tt;
            String ttinh;
            if (maPhongDangChon == null) {
                tt = "Trống";
                ttinh = "Tốt";
            } else {
                tt = cbTrangThai.getValue();
                ttinh = cbTinhTrang.getValue();
            }

            if (soPhong.isEmpty() || tangDisplay == null || loai == null || tt == null || ttinh == null) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin.").showAndWait();
                return;
            }

            // Lấy số tầng (chỉ số) từ "Tầng x"
            String tangDigits = tangDisplay.replaceAll("[^0-9]", "");
            if (tangDigits.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Giá trị tầng không hợp lệ.").showAndWait();
                return;
            }
            int tang = Integer.parseInt(tangDigits);

            // Kiểm tra số phòng hợp lệ
            String soPhongHopLe = kiemTraSoPhongHopLe(soPhong, tang);
            if (soPhongHopLe == null)
                return;

            Phong p = new Phong(
                    maPhongDangChon, // null => thêm; khác null => sửa
                    soPhongHopLe,
                    loai,
                    tt,
                    tang);
            p.setTinhTrang(ttinh);
            p.setMoTa(taMoTa.getText());

            if (maPhongDangChon == null) {
                String newId = controller.addRoom(p);
                taiDanhSachPhong();
                chonDongTheoMa(newId);
                hienThongBao("Đã thêm mới", "Thêm phòng " + soPhongHopLe + " thành công.");
            } else {
                controller.updateRoom(p);
                taiDanhSachPhong();
                chonDongTheoMa(maPhongDangChon);
                hienThongBao("Đã cập nhật", "Cập nhật phòng " + soPhongHopLe + " thành công.");
            }
            lamMoiForm();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi lưu: " + ex.getMessage()).showAndWait();
        }
    }

    private String kiemTraSoPhongHopLe(String soPhong, int tang) {
        if (soPhong == null || soPhong.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập số phòng.").showAndWait();
            return null;
        }

        String soDigits = soPhong.replaceAll("\\D+", "");
        if (soDigits.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Số phòng không hợp lệ (phải là số).").showAndWait();
            return null;
        }

        String prefixTang = String.valueOf(tang);
        if (!soDigits.startsWith(prefixTang)) {
            new Alert(Alert.AlertType.WARNING,
                    "Số phòng phải bắt đầu bằng số tầng.\n" +
                            "Ví dụ: Tầng " + tang + " thì số phòng nên dạng \"" + prefixTang + "xx\".\n" +
                            "Bạn nhập: \"" + soPhong + "\"")
                    .showAndWait();
            return null;
        }
        return soDigits;
    }

    private void lamMoiForm() {
        maPhongDangChon = null;
        bang.getSelectionModel().clearSelection();
        tfSoPhong.clear();
        cbTang.getSelectionModel().clearSelection();
        tfGia.clear();
        tfNguoiLonToiDa.clear();
        tfTreEmToiDa.clear();
        taMoTa.clear();
        cbLoaiPhong.getSelectionModel().clearSelection();

        // Mặc định khi thêm mới
        cbTrangThai.setValue("Trống");
        cbTinhTrang.setValue("Tốt");

        // Khoá combo khi đang ở chế độ thêm mới
        cbTrangThai.setDisable(true);
        cbTinhTrang.setDisable(true);
    }

    private void taiDanhSachLoaiPhong() {
        ObservableList<LoaiPhong> ds = FXCollections.observableArrayList(controller.getAllRoomTypes());
        cbLoaiPhong.setItems(ds);

        ObservableList<LoaiPhong> dsLoc = FXCollections.observableArrayList();
        dsLoc.add(new LoaiPhong(ID_TAT_CA, TXT_TAT_CA, 0d, (LocalDate) null));
        dsLoc.addAll(ds);
        cbLocLoaiPhong.setItems(dsLoc);
        cbLocLoaiPhong.getSelectionModel().selectFirst();
    }

    private void taiDanhSachPhong() {
        duLieuGoc.setAll(controller.getAllRooms());
    }

    private void chonDongTheoMa(String maPhong) {
        if (maPhong == null)
            return;
        for (Phong p : bang.getItems()) {
            if (maPhong.equals(p.getMaPhong())) {
                bang.getSelectionModel().select(p);
                bang.scrollTo(p);
                break;
            }
        }
    }

    private void hienThongBao(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void xoaNhieuPhong() {
        ObservableList<Phong> chon = bang.getSelectionModel().getSelectedItems();
        if (chon == null || chon.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Hãy chọn ít nhất 1 phòng để xoá.").showAndWait();
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "Xoá " + chon.size() + " phòng đã chọn?", ButtonType.OK, ButtonType.CANCEL);
        conf.setHeaderText("Xác nhận xoá");
        conf.showAndWait();
        if (conf.getResult() != ButtonType.OK)
            return;

        int daXoa = 0;
        java.util.List<Phong> banSao = new java.util.ArrayList<>(chon);
        for (Phong p : banSao) {
            try {
                controller.deleteRoom(p.getMaPhong());
                daXoa++;
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Không thể xoá phòng " + p.getSoPhong() + ": " + ex.getMessage()).showAndWait();
            }
        }
        taiDanhSachPhong();
        lamMoiForm();
        new Alert(Alert.AlertType.INFORMATION, "Đã xoá " + daXoa + " phòng.").showAndWait();
    }
}
