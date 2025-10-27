package view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class QuanLiHoaDon_GUI extends BorderPane {

    private final TextField tfTim = new TextField();
    private final ComboBox<TrangThaiHD> cbLocTrangThai = new ComboBox<>();
    private final DatePicker dpTuNgay = new DatePicker();
    private final DatePicker dpDenNgay = new DatePicker();

    private final Button btnTaiLai = new Button("Tải lại");
    private final Button btnXoa = new Button("Xóa đã chọn");

    private final TableView<DongHoaDon> bang = new TableView<>();
    private final ObservableList<DongHoaDon> duLieuGoc = FXCollections.observableArrayList();
    private final FilteredList<DongHoaDon> duLieuLoc = new FilteredList<>(duLieuGoc, p -> true);
    private final SortedList<DongHoaDon> duLieuSapXep = new SortedList<>(duLieuLoc);

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
        tfTim.setPromptText("Tên/Mã hóa đơn/Khách hàng");
        tfTim.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:4 6;");
        tfTim.setPrefWidth(220);

        // Sắp xếp items để 'Tất cả' đứng đầu và KHÔNG bị trùng
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
                new HBox(8, btnXoa, btnTaiLai)
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
        bang.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<DongHoaDon, String> cMa = new TableColumn<>("Mã Hóa Đơn");
        cMa.setCellValueFactory(new PropertyValueFactory<>("ma"));

        TableColumn<DongHoaDon, String> cKhach = new TableColumn<>("Khách hàng");
        cKhach.setCellValueFactory(new PropertyValueFactory<>("khachHang"));

        TableColumn<DongHoaDon, String> cTong = new TableColumn<>("Tổng tiền");
        cTong.setCellValueFactory((CellDataFeatures<DongHoaDon, String> cell) ->
                new ReadOnlyStringWrapper(dinhDangVND(cell.getValue().getTongTien())));
        cTong.setStyle("-fx-alignment:CENTER-RIGHT;");

        TableColumn<DongHoaDon, String> cNgay = new TableColumn<>("Ngày");
        cNgay.setCellValueFactory((CellDataFeatures<DongHoaDon, String> cell) -> {
            LocalDate d = cell.getValue().getNgay();
            return new ReadOnlyStringWrapper(d == null ? "-" : d.format(dinhDangDMY));
        });

        TableColumn<DongHoaDon, TrangThaiHD> cTrangThai = new TableColumn<>("Trạng thái");
        cTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        cTrangThai.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(TrangThaiHD st, boolean empty) {
                super.updateItem(st, empty);
                if (empty || st == null) setGraphic(null);
                else setGraphic(vienChip(st.nhan(), st.mau()));
            }
        });

        bang.getColumns().addAll(cMa, cKhach, cTong, cNgay, cTrangThai);
        bang.setItems(duLieuSapXep);
        duLieuSapXep.comparatorProperty().bind(bang.comparatorProperty());
    }

    private void khoiTaoSuKien() {
        tfTim.textProperty().addListener((o, a, b) -> apDungBoLoc());
        cbLocTrangThai.valueProperty().addListener((o, a, b) -> apDungBoLoc());
        dpTuNgay.valueProperty().addListener((o, a, b) -> apDungBoLoc());
        dpDenNgay.valueProperty().addListener((o, a, b) -> apDungBoLoc());

        btnTaiLai.setOnAction(this::suKienTaiLai);
        btnXoa.disableProperty().bind(Bindings.isEmpty(bang.getSelectionModel().getSelectedItems()));
        btnXoa.setOnAction(this::suKienXoa);
    }

    private void taiDuLieu() {
        duLieuGoc.setAll(
                new DongHoaDon("101", "Mark", 2_500_000, LocalDate.of(2025,1,1), TrangThaiHD.DANG_CHO),
                new DongHoaDon("201", "Bruno", 1_000_000, LocalDate.of(2025,1,2), TrangThaiHD.DA_HUY),
                new DongHoaDon("301", "Sekso", 32_420_000, LocalDate.of(2025,1,30), TrangThaiHD.DA_HUY),
                new DongHoaDon("401", "Cunha", 500_000, LocalDate.of(2025,12,21), TrangThaiHD.HOAN_THANH),
                new DongHoaDon("501", "Harry Maguire", 300_000, LocalDate.of(2025,10,30), TrangThaiHD.HOAN_THANH)
        );
        apDungBoLoc();
    }

    private void suKienTaiLai(ActionEvent e) { taiDuLieu(); }

    private void suKienXoa(ActionEvent e) {
        List<DongHoaDon> chon = new ArrayList<>(bang.getSelectionModel().getSelectedItems());
        if (chon.isEmpty()) { thongBao("Chưa chọn", "Hãy chọn ít nhất 1 dòng để xóa."); return; }
        duLieuGoc.removeAll(chon);
        thongBao("Đã xóa", "Đã xóa " + chon.size() + " bản ghi.");
    }

    private void apDungBoLoc() {
        final String tuKhoa = Optional.ofNullable(tfTim.getText()).orElse("").trim().toLowerCase();
        final LocalDate tuNgay = dpTuNgay.getValue();
        final LocalDate denNgay = dpDenNgay.getValue();
        final TrangThaiHD trangThai = cbLocTrangThai.getValue();

        duLieuLoc.setPredicate(d -> {
            if (d == null) return false;

            String ma = Optional.ofNullable(d.getMa()).orElse("").toLowerCase();
            String kh = Optional.ofNullable(d.getKhachHang()).orElse("").toLowerCase();

            boolean hopLeTrangThai = (trangThai == null || trangThai == TrangThaiHD.TAT_CA) || d.getTrangThai() == trangThai;
            boolean hopLeTuKhoa = tuKhoa.isEmpty() || ma.contains(tuKhoa) || kh.contains(tuKhoa);

            boolean hopLeNgay = true;
            if (tuNgay != null && d.getNgay() != null) hopLeNgay &= !d.getNgay().isBefore(tuNgay);
            if (denNgay != null && d.getNgay() != null) hopLeNgay &= !d.getNgay().isAfter(denNgay);

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

    private void thongBao(String tieuDe, String noiDung) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(tieuDe);
        a.setHeaderText(tieuDe);
        a.setContentText(noiDung);
        a.showAndWait();
    }

    private String dinhDangVND(Number n) { return n == null ? "-" : dinhDangTien.format(n); }

    public static class DongHoaDon {
        private String ma, khachHang; private double tongTien; private LocalDate ngay; private TrangThaiHD trangThai;
        public DongHoaDon(String ma, String khachHang, double tongTien, LocalDate ngay, TrangThaiHD trangThai){this.ma=ma;this.khachHang=khachHang;this.tongTien=tongTien;this.ngay=ngay;this.trangThai=trangThai;}
        public String getMa(){return ma;} public String getKhachHang(){return khachHang;} public double getTongTien(){return tongTien;} public LocalDate getNgay(){return ngay;} public TrangThaiHD getTrangThai(){return trangThai;}
    }

    public enum TrangThaiHD {
        TAT_CA("Tất cả", Color.web("#6b7280")),
        HOAN_THANH("Hoàn thành", Color.web("#10b981")),
        DANG_CHO("Đang chờ", Color.web("#3b82f6")),
        DA_HUY("Đã hủy", Color.web("#ef4444"));
        private final String nhan; private final Color mau; TrangThaiHD(String n, Color m){nhan=n;mau=m;} public String nhan(){return nhan;} public Color mau(){return mau;}
        public static StringConverter<TrangThaiHD> converter(){return new StringConverter<>(){public String toString(TrangThaiHD s){return s==null?"":s.nhan;}public TrangThaiHD fromString(String s){for(TrangThaiHD v:values())if(Objects.equals(v.nhan, s))return v;return null;}};}
    }
}
