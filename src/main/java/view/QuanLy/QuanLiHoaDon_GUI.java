package view.QuanLy;

import controller.ChiTietHoaDon_Controller;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.*;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private final NumberFormat dinhDangTien = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));

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
        tfTim.setPromptText("Mã HĐ / Mã KH / Tên KH / Mã NV");
        tfTim.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:4 6;");
        tfTim.setPrefWidth(260);

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

        TableColumn<HoaDon, String> cMa = new TableColumn<>("Mã HĐ");
        cMa.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));

        TableColumn<HoaDon, String> cKh = new TableColumn<>("Khách hàng");
        cKh.setCellValueFactory(cd -> {
            KhachHang kh = cd.getValue().getKhachHang();
            String text = "-";
            if (kh != null) {
                String ma = Optional.ofNullable(kh.getMaKhachHang()).orElse("-");
                String ten = Optional.ofNullable(kh.getTenKhachHang()).orElse("");
                text = ten.isEmpty() ? ma : (ma + " - " + ten);
            }
            return new ReadOnlyStringWrapper(text);
        });

        TableColumn<HoaDon, String> cNv = new TableColumn<>("Nhân viên");
        cNv.setCellValueFactory(cd -> {
            NhanVien nv = cd.getValue().getNhanVien();
            String text = (nv == null) ? "-" :
                    (Optional.ofNullable(nv.getMaNhanVien()).orElse("-")
                            + " - "
                            + Optional.ofNullable(nv.getTenNhanVien()).orElse(""));
            return new ReadOnlyStringWrapper(text);
        });

        TableColumn<HoaDon, String> cTong = new TableColumn<>("Tổng tiền");
        cTong.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(dinhDangTien.format(cd.getValue().getTongTien()) + " ₫"));
        cTong.setStyle("-fx-alignment:CENTER-RIGHT; -fx-font-weight:bold;");

        TableColumn<HoaDon, String> cNgay = new TableColumn<>("Ngày");
        cNgay.setCellValueFactory(cd -> {
            LocalDateTime ldt = cd.getValue().getNgayDat();
            String text = (ldt == null) ? "-" : ldt.toLocalDate().format(dinhDangDMY);
            return new ReadOnlyStringWrapper(text);
        });
        cNgay.setStyle("-fx-alignment:CENTER;");

        TableColumn<HoaDon, String> cTrangThai = new TableColumn<>("Trạng thái");
        cTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        cTrangThai.setCellFactory(param -> new TableCell<>() {
            protected void updateItem(String raw, boolean empty) {
                super.updateItem(raw, empty);
                if (empty || raw == null) { setGraphic(null); return; }
                TrangThaiHD st = mapTrangThai(raw);
                setGraphic(vienChip(st.nhan(), st.mau()));
            }
        });

        bang.getColumns().setAll(cMa, cKh, cNv, cTong, cNgay, cTrangThai);

        // Double-click mở chi tiết
        bang.setRowFactory(tv -> {
            final TableRow<HoaDon> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    hienChiTietHoaDon(row.getItem());
                }
            });
            MenuItem xem = new MenuItem("Xem chi tiết");
            xem.setOnAction(e -> { if (!row.isEmpty()) hienChiTietHoaDon(row.getItem()); });
            row.setContextMenu(new ContextMenu(xem));
            return row;
        });

        bang.setItems(duLieuSapXep);
        duLieuSapXep.comparatorProperty().bind(bang.comparatorProperty());
    }

    // Dialog chi tiết: Thông tin hóa đơn + Bảng Chi tiết + Bảng Dịch vụ
    private void hienChiTietHoaDon(HoaDon hd) {
        if (hd == null || hd.getMaHoaDon() == null) {
            new Alert(Alert.AlertType.INFORMATION, "Không xác định được hóa đơn.").showAndWait();
            return;
        }

        // Nếu muốn refetch “tươi” từ DB trước khi hiện:
        // HoaDon fresh = new HoaDon_DAO().findById(hd.getMaHoaDon());
        // if (fresh != null) hd = fresh;

        ChiTietHoaDon_Controller ctl = new ChiTietHoaDon_Controller();

        // ===== Bảng Chi tiết hóa đơn
        TableView<ChiTietHoaDon> tblCT = new TableView<>();
        tblCT.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ChiTietHoaDon, String> cMaPDP = new TableColumn<>("Mã PDP");
        cMaPDP.setCellValueFactory(cd -> {
            PhieuDatPhong p = cd.getValue().getPhieuDatPhong();
            return new ReadOnlyStringWrapper(p == null ? "" : String.valueOf(p.getMaPhieuDatPhong()));
        });

        TableColumn<ChiTietHoaDon, String> cNgayTao = new TableColumn<>("Ngày tạo");
        cNgayTao.setCellValueFactory(cd -> {
            LocalDateTime ldt = cd.getValue().getNgayTao();
            String text = (ldt == null) ? "-" : ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new ReadOnlyStringWrapper(text);
        });
        cNgayTao.setStyle("-fx-alignment:CENTER;");

        TableColumn<ChiTietHoaDon, String> cTongTienCT = new TableColumn<>("Tổng tiền");
        cTongTienCT.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(dinhDangTien.format(cd.getValue().getTongTien()) + " ₫"));
        cTongTienCT.setStyle("-fx-alignment:CENTER-RIGHT; -fx-font-weight:bold;");

        tblCT.getColumns().setAll(cMaPDP, cNgayTao, cTongTienCT);

        // ===== Bảng Dịch vụ theo dòng chi tiết
        TableView<ChiTietHoaDonDichVu> tblDV = new TableView<>();
        tblDV.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ChiTietHoaDonDichVu, String> cMaDV = new TableColumn<>("Mã DV");
        cMaDV.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().getDichVu() == null ? "" : cd.getValue().getDichVu().getMaDichVu()));

        TableColumn<ChiTietHoaDonDichVu, String> cTenDV = new TableColumn<>("Tên dịch vụ");
        cTenDV.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().getDichVu() == null ? "" : cd.getValue().getDichVu().getTenDichVu()));

        TableColumn<ChiTietHoaDonDichVu, String> cDVT = new TableColumn<>("ĐVT");
        cDVT.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().getDichVu() == null ? "" :
                        Optional.ofNullable(cd.getValue().getDichVu().getDonViTinh()).orElse("")));
        cDVT.setStyle("-fx-alignment:CENTER;");

        TableColumn<ChiTietHoaDonDichVu, String> cGia = new TableColumn<>("Giá");
        cGia.setCellValueFactory(cd -> {
            double gia = (cd.getValue().getDichVu() == null) ? 0d : cd.getValue().getDichVu().getGia();
            return new ReadOnlyStringWrapper(dinhDangTien.format(gia) + " ₫");
        });
        cGia.setStyle("-fx-alignment:CENTER-RIGHT;");

        tblDV.getColumns().setAll(cMaDV, cTenDV, cDVT, cGia);

        // Nạp dữ liệu CT & DV
        ObservableList<ChiTietHoaDon> dsCT = FXCollections.observableArrayList(
                ctl.getByMaHoaDon(hd.getMaHoaDon())
        );
        tblCT.setItems(dsCT);

        tblCT.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel == null || sel.getPhieuDatPhong() == null) {
                tblDV.getItems().clear();
                return;
            }
            String maPDP = String.valueOf(sel.getPhieuDatPhong().getMaPhieuDatPhong());
            List<ChiTietHoaDonDichVu> dsDV = ctl.getDichVu(hd.getMaHoaDon(), maPDP);
            tblDV.setItems(FXCollections.observableArrayList(dsDV));
        });

        if (!dsCT.isEmpty()) {
            tblCT.getSelectionModel().selectFirst();
        }

        // ===== Header + Thông tin hóa đơn đầy đủ
        Label title = new Label("Chi tiết hóa đơn: " + hd.getMaHoaDon());
        title.setStyle("-fx-font-size:20px; -fx-font-weight:800; -fx-text-fill:#111827;");

        Node infoCard = taoPaneThongTinHoaDon(hd);

        TitledPane paneCT = new TitledPane("Các dòng chi tiết (mỗi dòng ứng với 1 Phiếu đặt phòng)", tblCT);
        TitledPane paneDV = new TitledPane("Dịch vụ của dòng chi tiết đang chọn", tblDV);
        paneCT.setExpanded(true);
        paneDV.setExpanded(true);

        VBox content = new VBox(title, infoCard, paneCT, paneDV);
        content.setSpacing(10);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color:white;");

        Stage dialog = new Stage();
        dialog.setTitle("Chi tiết hóa đơn");
        if (getScene() != null && getScene().getWindow() != null) {
            dialog.initOwner(getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
        }
        dialog.setScene(new Scene(content, 940, 620));
        dialog.show();
    }

    /** Thẻ “Thông tin hóa đơn” – đầy đủ thông tin từ JOIN */
    private Node taoPaneThongTinHoaDon(HoaDon hd) {
        // Lấy thông tin
        KhachHang kh = hd.getKhachHang();
        NhanVien nv = hd.getNhanVien();
        KhuyenMai km = hd.getKhuyenMai();

        String maHD = Optional.ofNullable(hd.getMaHoaDon()).orElse("-");
        String trangThaiText = Optional.ofNullable(hd.getTrangThai()).orElse("-");
        TrangThaiHD st = mapTrangThai(trangThaiText);

        String ngayDat = (hd.getNgayDat() == null) ? "-" : hd.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String ngayTao = (hd.getNgayTao() == null) ? "-" : hd.getNgayTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String tong = dinhDangTien.format(hd.getTongTien()) + " ₫";

        String maKH = kh == null ? "-" : Optional.ofNullable(kh.getMaKhachHang()).orElse("-");
        String tenKH = kh == null ? "-" : Optional.ofNullable(kh.getTenKhachHang()).orElse("-");
        String sdtKH = kh == null ? "-" : Optional.ofNullable(kh.getSoDienThoai()).orElse("-");
        String emailKH = kh == null ? "-" : Optional.ofNullable(kh.getEmail()).orElse("-");

        String maNV = nv == null ? "-" : Optional.ofNullable(nv.getMaNhanVien()).orElse("-");
        String tenNV = nv == null ? "-" : Optional.ofNullable(nv.getTenNhanVien()).orElse("-");

        String maKM = km == null ? "-" : Optional.ofNullable(km.getMaKhuyenMai()).orElse("-");
        String tenKM = km == null ? "-" : Optional.ofNullable(km.getTenKhuyenMai()).orElse("-");
        String heSoKM = km == null ? "-" : String.valueOf(Optional.ofNullable(km.getHeSo()).orElse(0f));

        // Layout
        GridPane left = new GridPane();
        left.setHgap(12); left.setVgap(8);

        int r = 0;
        left.add(labelValue("Mã hóa đơn", maHD), 0, r++);
        HBox stBox = new HBox(new Label("Trạng thái: "), vienChip(st.nhan(), st.mau()));
        stBox.setSpacing(8);
        left.add(stBox, 0, r++);
        left.add(labelValue("Ngày đặt", ngayDat), 0, r++);
        left.add(labelValue("Ngày tạo", ngayTao), 0, r++);
        left.add(labelValue("Tổng tiền", tong), 0, r++);

        GridPane right = new GridPane();
        right.setHgap(12); right.setVgap(8);
        int r2 = 0;
        right.add(labelValue("Mã KH", maKH), 0, r2++);
        right.add(labelValue("Tên KH", tenKH), 0, r2++);
        right.add(labelValue("SĐT KH", sdtKH), 0, r2++);
        right.add(labelValue("Email KH", emailKH), 0, r2++);
        right.add(new Separator(), 0, r2++);

        right.add(labelValue("Mã NV", maNV), 0, r2++);
        right.add(labelValue("Tên NV", tenNV), 0, r2++);
        right.add(new Separator(), 0, r2++);

        right.add(labelValue("Mã KM", maKM), 0, r2++);
        right.add(labelValue("Tên KM", tenKM), 0, r2++);
        right.add(labelValue("Hệ số KM", heSoKM), 0, r2++);

        HBox rows = new HBox(left, right);
        rows.setSpacing(24);
        rows.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(rows);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color:#f8fafc; -fx-border-color:#e5e7eb; -fx-background-radius:12; -fx-border-radius:12;");
        return card;
    }

    private Node labelValue(String label, String value) {
        Label l = new Label(label + ": ");
        l.setStyle("-fx-text-fill:#374151;");
        Label v = new Label(value == null ? "-" : value);
        v.setStyle("-fx-font-weight:700; -fx-text-fill:#111827;");
        HBox box = new HBox(l, v);
        box.setSpacing(6);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void khoiTaoSuKien() {
        tfTim.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> o, String a, String b) { apDungBoLoc(); }
        });
        cbLocTrangThai.valueProperty().addListener(new ChangeListener<TrangThaiHD>() {
            public void changed(ObservableValue<? extends TrangThaiHD> o, TrangThaiHD a, TrangThaiHD b) { apDungBoLoc(); }
        });
        dpTuNgay.valueProperty().addListener(new ChangeListener<LocalDate>() {
            public void changed(ObservableValue<? extends LocalDate> o, LocalDate a, LocalDate b) { apDungBoLoc(); }
        });
        dpDenNgay.valueProperty().addListener(new ChangeListener<LocalDate>() {
            public void changed(ObservableValue<? extends LocalDate> o, LocalDate a, LocalDate b) { apDungBoLoc(); }
        });
        btnTaiLai.setOnAction(new EventHandler<ActionEvent>() {
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
        final String tuKhoa = Optional.ofNullable(tfTim.getText()).orElse("").trim().toLowerCase();
        final LocalDate tuNgay = dpTuNgay.getValue();
        final LocalDate denNgay = dpDenNgay.getValue();
        final TrangThaiHD trangThai = cbLocTrangThai.getValue();

        duLieuLoc.setPredicate(hd -> {
            if (hd == null) return false;

            String ma = Optional.ofNullable(hd.getMaHoaDon()).orElse("").toLowerCase();

            KhachHang kh = hd.getKhachHang();
            String maKH = kh == null ? "" : Optional.ofNullable(kh.getMaKhachHang()).orElse("").toLowerCase();
            String tenKH = kh == null ? "" : Optional.ofNullable(kh.getTenKhachHang()).orElse("").toLowerCase();

            NhanVien nv = hd.getNhanVien();
            String maNV = nv == null ? "" : Optional.ofNullable(nv.getMaNhanVien()).orElse("").toLowerCase();

            boolean hopLeTrangThai = (trangThai == null || trangThai == TrangThaiHD.TAT_CA)
                    || mapTrangThai(hd.getTrangThai()) == trangThai;

            boolean hopLeTuKhoa = tuKhoa.isEmpty()
                    || ma.contains(tuKhoa)
                    || maKH.contains(tuKhoa)
                    || tenKH.contains(tuKhoa)
                    || maNV.contains(tuKhoa);

            LocalDate ngay = (hd.getNgayDat() == null) ? null : hd.getNgayDat().toLocalDate();
            boolean hopLeNgay = true;
            if (tuNgay != null && ngay != null) hopLeNgay &= !ngay.isBefore(tuNgay);
            if (denNgay != null && ngay != null) hopLeNgay &= !ngay.isAfter(denNgay);

            return hopLeTrangThai && hopLeTuKhoa && hopLeNgay;
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
        if (x.contains("đã") || x.contains("hoàn")) return TrangThaiHD.HOAN_THANH;
        if (x.contains("hủy")) return TrangThaiHD.DA_HUY;
        if (x.contains("chờ")) return TrangThaiHD.DANG_CHO;
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
