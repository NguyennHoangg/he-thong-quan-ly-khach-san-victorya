package view;

import dao.HoaDon_DAO;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.HoaDon;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class QuanLiHoaDon_GUI extends BorderPane {

    private final TextField tfTim = new TextField();
    private final ComboBox<TrangThaiHD> cbLocTrangThai = new ComboBox<>();
    private final DatePicker dpTuNgay = new DatePicker();
    private final DatePicker dpDenNgay = new DatePicker();
    private final Button btnTaiLai = new Button("Tải lại");

    private final TableView<HoaDon> bang = new TableView<>();
    private final ObservableList<HoaDon> duLieuGoc = FXCollections.observableArrayList();
    private final FilteredList<HoaDon> duLieuLoc = new FilteredList<>(duLieuGoc, p -> true);
    private final SortedList<HoaDon> duLieuSapXep = new SortedList<>(duLieuLoc);

    private final DateTimeFormatter dinhDangDMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat dinhDangTien = NumberFormat.getInstance(new Locale("vi", "VN"));

    public QuanLiHoaDon_GUI() {
        setPadding(new Insets(16, 24, 24, 24));
        setTop(xayDungKhuVucTieuDe());
        setCenter(xayDungKhuVucNoiDung());
        khoiTaoBang();
        khoiTaoSuKien();
        taiDuLieu();
    }

    private Node xayDungKhuVucTieuDe() {
        Label tieuDe = new Label("Danh sách hóa đơn");
        tieuDe.setStyle("-fx-font-size:26px; -fx-font-weight:800; -fx-text-fill:#111827;");
        VBox box = new VBox(tieuDe);
        box.setPadding(new Insets(4, 0, 8, 0));
        return box;
    }

    private Node xayDungKhuVucNoiDung() {
        tfTim.setPromptText("Mã hóa đơn / Mã khách hàng");
        tfTim.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:4 6;");
        tfTim.setPrefWidth(220);

        // Combo trạng thái
        ObservableList<TrangThaiHD> items = FXCollections.observableArrayList(TrangThaiHD.values());
        items.remove(TrangThaiHD.TAT_CA);
        items.add(0, TrangThaiHD.TAT_CA);
        cbLocTrangThai.setItems(items);
        cbLocTrangThai.getSelectionModel().select(TrangThaiHD.TAT_CA);
        cbLocTrangThai.setPromptText("Tất cả trạng thái");
        cbLocTrangThai.setConverter(TrangThaiHD.converter());
        cbLocTrangThai.setPrefWidth(160);

        dinhDangNgayPill(dpTuNgay, "Từ ngày");
        dinhDangNgayPill(dpDenNgay, "Đến ngày");

        HBox nhomLoc = new HBox(
                pill(bocIcon("🔍", tfTim)),
                pill(cbLocTrangThai),
                pill(bocIcon("📅", dpTuNgay)),
                pill(bocIcon("📅", dpDenNgay)),
                btnTaiLai
        );
        nhomLoc.setSpacing(12);
        nhomLoc.setAlignment(Pos.CENTER_LEFT);
        nhomLoc.setPadding(new Insets(14));
        nhomLoc.setStyle("-fx-background-color:#f7fafc; -fx-background-radius:10; -fx-border-color:#e8edf3; -fx-border-radius:10;");

        VBox center = new VBox(nhomLoc, bang);
        center.setSpacing(10);
        VBox.setVgrow(bang, Priority.ALWAYS);
        return center;
    }

    private void khoiTaoBang() {
        bang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bang.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<HoaDon, String> cMa = new TableColumn<>("Mã Hóa Đơn");
        cMa.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));

        TableColumn<HoaDon, String> cMaKhach = new TableColumn<>("Mã Khách Hàng");
        cMaKhach.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HoaDon, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HoaDon, String> cell) {
                String maKH = "";
                if (cell.getValue().getKhachHang() != null)
                    maKH = Optional.ofNullable(cell.getValue().getKhachHang().getMaKhachHang()).orElse("");
                return new ReadOnlyStringWrapper(maKH);
            }
        });

        TableColumn<HoaDon, String> cTong = new TableColumn<>("Tổng tiền");
        cTong.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HoaDon, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HoaDon, String> cell) {
                String text = dinhDangTien.format(cell.getValue().getTongTien()) + " ₫";
                return new ReadOnlyStringWrapper(text);
            }
        });
        cTong.setStyle("-fx-alignment:CENTER-RIGHT; -fx-font-weight:bold;");

        TableColumn<HoaDon, String> cNgay = new TableColumn<>("Ngày");
        cNgay.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HoaDon, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HoaDon, String> cell) {
                LocalDateTime ldt = cell.getValue().getNgayDat();
                String text = (ldt == null) ? "-" : ldt.toLocalDate().format(dinhDangDMY);
                return new ReadOnlyStringWrapper(text);
            }
        });
        cNgay.setStyle("-fx-alignment:CENTER;");

        TableColumn<HoaDon, String> cTrangThai = new TableColumn<>("Trạng thái");
        cTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        cTrangThai.setCellFactory(new Callback<TableColumn<HoaDon, String>, TableCell<HoaDon, String>>() {
            @Override
            public TableCell<HoaDon, String> call(TableColumn<HoaDon, String> param) {
                return new TableCell<HoaDon, String>() {
                    @Override
                    protected void updateItem(String raw, boolean empty) {
                        super.updateItem(raw, empty);
                        if (empty || raw == null) {
                            setGraphic(null);
                            return;
                        }
                        TrangThaiHD st = mapTrangThai(raw);
                        setGraphic(vienChip(st.nhan(), st.mau()));
                    }
                };
            }
        });

        bang.getColumns().setAll(cMa, cMaKhach, cTong, cNgay, cTrangThai);
        bang.setItems(duLieuSapXep);
        duLieuSapXep.comparatorProperty().bind(bang.comparatorProperty());
    }

    private void khoiTaoSuKien() {
        tfTim.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> o, String a, String b) {
                apDungBoLoc();
            }
        });
        cbLocTrangThai.valueProperty().addListener(new ChangeListener<TrangThaiHD>() {
            @Override
            public void changed(ObservableValue<? extends TrangThaiHD> o, TrangThaiHD a, TrangThaiHD b) {
                apDungBoLoc();
            }
        });
        dpTuNgay.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> o, LocalDate a, LocalDate b) {
                apDungBoLoc();
            }
        });
        dpDenNgay.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> o, LocalDate a, LocalDate b) {
                apDungBoLoc();
            }
        });
        btnTaiLai.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { taiDuLieu(); }
        });
    }

    private void taiDuLieu() {
        HoaDon_DAO dao = new HoaDon_DAO();
        ObservableList<HoaDon> ds = FXCollections.observableArrayList(dao.getAll());
        duLieuGoc.setAll(ds);
        apDungBoLoc();
    }

    private void apDungBoLoc() {
        String tuKhoa = Optional.ofNullable(tfTim.getText()).orElse("").trim().toLowerCase();
        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();
        TrangThaiHD stChon = cbLocTrangThai.getValue();

        duLieuLoc.setPredicate(hd -> {
            if (hd == null) return false;
            String maHD = Optional.ofNullable(hd.getMaHoaDon()).orElse("").toLowerCase();
            String maKH = (hd.getKhachHang() == null) ? "" :
                    Optional.ofNullable(hd.getKhachHang().getMaKhachHang()).orElse("").toLowerCase();

            boolean hopLeTuKhoa = tuKhoa.isEmpty() || maHD.contains(tuKhoa) || maKH.contains(tuKhoa);
            boolean hopLeTrangThai = (stChon == null || stChon == TrangThaiHD.TAT_CA)
                    || mapTrangThai(hd.getTrangThai()) == stChon;

            LocalDate ngay = (hd.getNgayDat() == null) ? null : hd.getNgayDat().toLocalDate();
            boolean hopLeNgay = true;
            if (tuNgay != null && ngay != null) hopLeNgay &= !ngay.isBefore(tuNgay);
            if (denNgay != null && ngay != null) hopLeNgay &= !ngay.isAfter(denNgay);

            return hopLeTuKhoa && hopLeTrangThai && hopLeNgay;
        });
    }

    private static void dinhDangNgayPill(DatePicker dp, String prompt) {
        dp.setPromptText(prompt);
        dp.setEditable(false);
        dp.setPrefWidth(140);
        dp.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");
    }

    private static Node pill(Node inner) {
        HBox box = new HBox(inner);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle("-fx-background-color:white; -fx-border-color:#e6e9ee; -fx-background-radius:20; -fx-border-radius:20;");
        return box;
    }

    private static HBox bocIcon(String icon, Node node) {
        Label lb = new Label(icon);
        lb.setStyle("-fx-opacity:0.8; -fx-font-size:13px;");
        HBox h = new HBox(lb, node);
        h.setSpacing(6);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private static StackPane vienChip(String text, Color color) {
        Label lb = new Label(text);
        lb.setPadding(new Insets(3, 8, 3, 8));
        lb.setStyle("-fx-font-size:12px; -fx-font-weight:700;");
        lb.setTextFill(color);
        StackPane pill = new StackPane(lb);
        pill.setBackground(new Background(new BackgroundFill(
                Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.12),
                new CornerRadii(999), Insets.EMPTY)));
        return pill;
    }

    private static TrangThaiHD mapTrangThai(String raw) {
        if (raw == null) return TrangThaiHD.DANG_CHO;
        String x = raw.trim().toLowerCase();
        if (x.contains("đã thanh toán") || x.contains("hoàn") || x.contains("done"))
            return TrangThaiHD.HOAN_THANH;
        if (x.contains("hủy") || x.contains("cancel"))
            return TrangThaiHD.DA_HUY;
        if (x.contains("chờ") || x.contains("pending"))
            return TrangThaiHD.DANG_CHO;
        return TrangThaiHD.DANG_CHO;
    }

    public enum TrangThaiHD {
        TAT_CA("Tất cả", Color.web("#6b7280")),
        HOAN_THANH("Hoàn thành", Color.web("#10b981")),
        DANG_CHO("Đang chờ", Color.web("#3b82f6")),
        DA_HUY("Đã hủy", Color.web("#ef4444"));

        private final String nhan;
        private final Color mau;

        TrangThaiHD(String n, Color m) { nhan = n; mau = m; }

        public String nhan() { return nhan; }
        public Color mau() { return mau; }

        public static StringConverter<TrangThaiHD> converter() {
            return new StringConverter<TrangThaiHD>() {
                public String toString(TrangThaiHD s) { return s == null ? "" : s.nhan; }
                public TrangThaiHD fromString(String s) {
                    for (TrangThaiHD v : values()) if (Objects.equals(v.nhan, s)) return v;
                    return null;
                }
            };
        }
    }
}
