package view;

import controller.ChiTietHoaDon_Controller;
import dao.HoaDon_DAO;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

    // Bộ lọc
    private final TextField tfTim = new TextField();
    private final ComboBox<TrangThaiHD> cbLocTrangThai = new ComboBox<>();
    private final DatePicker dpTuNgay = new DatePicker();
    private final DatePicker dpDenNgay = new DatePicker();
    private final Button btnTaiLai = new Button("Tải lại");

    // Dữ liệu bảng (chỉ 1 list đơn giản)
    private final TableView<HoaDon> bang = new TableView<>();
    private final ObservableList<HoaDon> duLieuBang = FXCollections.observableArrayList();

    private final DateTimeFormatter dinhDangDMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat dinhDangTien = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));

    public QuanLiHoaDon_GUI() {
        setPadding(new Insets(16, 24, 24, 24));
        setTop(xayDungKhuVucTieuDe());
        setCenter(xayDungKhuVucNoiDung());
        khoiTaoBang();
        khoiTaoSuKien();
        LocalDate homNay = LocalDate.now();
        dpTuNgay.setValue(homNay);
        dpDenNgay.setValue(homNay);
        thucHienTraCuu();
    }

    private Node xayDungKhuVucTieuDe() {
        Label tieuDe = new Label("Danh sách hóa đơn");
        tieuDe.setStyle("-fx-font-size:22px; -fx-font-weight:800; -fx-text-fill:#111827;");
        VBox box = new VBox(tieuDe);
        box.setPadding(new Insets(0, 0, 8, 0));
        return box;
    }

    private Node xayDungKhuVucNoiDung() {
        // ô tìm kiếm
        tfTim.setPromptText("Mã HĐ");
        tfTim.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");
        tfTim.setPrefWidth(240);

        // combobox trạng thái
        ObservableList<TrangThaiHD> items = FXCollections.observableArrayList(TrangThaiHD.values());

        items.remove(TrangThaiHD.TAT_CA);
        items.add(0, TrangThaiHD.TAT_CA);
        cbLocTrangThai.setItems(items);
        cbLocTrangThai.getSelectionModel().select(TrangThaiHD.TAT_CA);
        cbLocTrangThai.setConverter(TrangThaiHD.converter());
        cbLocTrangThai.setPrefWidth(150);
        cbLocTrangThai.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");

        // date pickers
        dinhDangNgayPill(dpTuNgay, "Từ ngày");
        dinhDangNgayPill(dpDenNgay, "Đến ngày");

        // nút tải lại
        btnTaiLai.setStyle(
                "-fx-background-color:#e5e7eb; -fx-text-fill:#111827; -fx-background-radius:999; -fx-padding:6 12;");

        // thanh bộ lọc
        HBox nhomLoc = new HBox(
                pill(bocIcon("🔍", tfTim)),
                pill(cbLocTrangThai),
                pill(bocIcon("📅", dpTuNgay)),
                pill(bocIcon("📅", dpDenNgay)),
                btnTaiLai);
        nhomLoc.setSpacing(10);
        nhomLoc.setAlignment(Pos.CENTER_LEFT);
        nhomLoc.setPadding(new Insets(10));
        nhomLoc.setStyle(
                "-fx-background-color:#f8fafc; -fx-background-radius:10; -fx-border-color:#e8edf3; -fx-border-radius:10;");

        // Bảng
        bang.setPrefHeight(600);

        VBox center = new VBox(nhomLoc, bang);
        center.setSpacing(10);
        VBox.setVgrow(bang, Priority.ALWAYS);
        return center;
    }

    private void khoiTaoBang() {
        bang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bang.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);// cho phép chọn một dòng tại 1 thời điểm

        TableColumn<HoaDon, String> cMa = new TableColumn<>("Mã HĐ");
        cMa.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));// lấy gia trị từ thuộc tính trong model

        TableColumn<HoaDon, String> cKh = new TableColumn<>("Khách hàng");
        cKh.setCellValueFactory(cd -> { //      khách hàng
            KhachHang kh = cd.getValue().getKhachHang();
            String text = "-";
            if (kh != null) {
                String ma = Optional.ofNullable(kh.getMaKhachHang()).orElse("-");
                String ten = Optional.ofNullable(kh.getTenKhachHang()).orElse("");
                text = ten.isEmpty() ? ma : (ma + " - " + ten);
            }
            return new ReadOnlyStringWrapper(text);/** Bọc (wrap) một chuỗi String thành ObservableValue<String>
            để TableColumn có thể hiển thị được dữ liệu.**/
        });

        TableColumn<HoaDon, String> cNv = new TableColumn<>("Nhân viên");
        cNv.setCellValueFactory(cd -> {
            NhanVien nv = cd.getValue().getNhanVien();
            String text = (nv == null) ? "-"
                    : (Optional.ofNullable(nv.getMaNhanVien()).orElse("-")
                            + " - "
                            + Optional.ofNullable(nv.getTenNhanVien()).orElse(""));
            return new ReadOnlyStringWrapper(text);
        });

        TableColumn<HoaDon, String> cTong = new TableColumn<>("Tổng tiền");
        cTong.setCellValueFactory(
                cd -> new ReadOnlyStringWrapper(dinhDangTien.format(cd.getValue().getTongTien()) + " ₫"));
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
            xem.setOnAction(e -> {
                if (!row.isEmpty())
                    hienChiTietHoaDon(row.getItem());
            });
            row.setContextMenu(new ContextMenu(xem));
            return row;
        });

        bang.setItems(duLieuBang);
    }

    private void hienChiTietHoaDon(HoaDon hd) {
        if (hd == null || hd.getMaHoaDon() == null) {
            new Alert(Alert.AlertType.INFORMATION, "Không xác định được hóa đơn.").showAndWait();
            return;
        }

        ChiTietHoaDon_Controller ctl = new ChiTietHoaDon_Controller();

        // Bảng Chi tiết hóa đơn
        TableView<ChiTietHoaDon> tblCT = new TableView<>();
        tblCT.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ChiTietHoaDon, String> cMaPDP = new TableColumn<>("Mã PDP");

        cMaPDP.setCellValueFactory(cd -> { // quy định giá trị gì
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
        cTongTienCT.setCellValueFactory(
                cd -> new ReadOnlyStringWrapper(dinhDangTien.format(cd.getValue().getTongTien()) + " ₫"));
        cTongTienCT.setStyle("-fx-alignment:CENTER-RIGHT; -fx-font-weight:bold;");

        tblCT.getColumns().setAll(cMaPDP, cNgayTao, cTongTienCT);

        // Bảng Dịch vụ
        TableView<ChiTietHoaDonDichVu> tblDV = new TableView<>();
        tblDV.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ChiTietHoaDonDichVu, String> cMaDV = new TableColumn<>("Mã DV");
        cMaDV.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getDichVu() == null ? "" : cd.getValue().getDichVu().getMaDichVu()));

        TableColumn<ChiTietHoaDonDichVu, String> cTenDV = new TableColumn<>("Tên dịch vụ");
        cTenDV.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getDichVu() == null ? "" : cd.getValue().getDichVu().getTenDichVu()));

        TableColumn<ChiTietHoaDonDichVu, String> cDVT = new TableColumn<>("ĐVT");
        cDVT.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getDichVu() == null ? ""
                : Optional.ofNullable(cd.getValue().getDichVu().getDonViTinh()).orElse("")));
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
                ctl.getByMaHoaDon(hd.getMaHoaDon()));
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

        // Thông tin hóa đơn JOIN đầy đủ
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

    // Pane chứa thông tin hóa đơn (JOIN)
    private Node taoPaneThongTinHoaDon(HoaDon hd) {
        KhachHang kh = hd.getKhachHang();
        NhanVien nv = hd.getNhanVien();
        KhuyenMai km = hd.getKhuyenMai();

        String maHD = Optional.ofNullable(hd.getMaHoaDon()).orElse("-");
        String trangThaiText = Optional.ofNullable(hd.getTrangThai()).orElse("-");
        TrangThaiHD st = mapTrangThai(trangThaiText);

        String ngayDat = (hd.getNgayDat() == null) ? "-"
                : hd.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String ngayTao = (hd.getNgayTao() == null) ? "-"
                : hd.getNgayTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
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

        GridPane left = new GridPane();
        left.setHgap(12);
        left.setVgap(8);
        int r = 0;
        left.add(labelValue("Mã hóa đơn", maHD), 0, r++);
        HBox stBox = new HBox(new Label("Trạng thái: "), vienChip(st.nhan(), st.mau()));
        stBox.setSpacing(8);
        left.add(stBox, 0, r++);
        left.add(labelValue("Ngày đặt", ngayDat), 0, r++);
        left.add(labelValue("Ngày tạo", ngayTao), 0, r++);
        left.add(labelValue("Tổng tiền", tong), 0, r++);

        GridPane right = new GridPane();
        right.setHgap(12);
        right.setVgap(8);
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
        card.setStyle(
                "-fx-background-color:#f8fafc; -fx-border-color:#e5e7eb; -fx-background-radius:12; -fx-border-radius:12;");
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
        // Gõ Enter trong ô tìm -> tra cứu
        tfTim.setOnAction(e -> thucHienTraCuu());

        // Khi xoá hết text thì dọn bảng nếu không còn filter nào khác
        tfTim.textProperty().addListener((o, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                if (khongCoBoLocKhac()) {
                    duLieuBang.clear();
                    bang.setPlaceholder(new Label("Nhập điều kiện tìm kiếm / lọc để hiển thị hóa đơn"));
                } else {
                    thucHienTraCuu();
                }
            }
        });

        // Thay đổi combobox trạng thái -> tra cứu
        cbLocTrangThai.valueProperty().addListener((o, a, b) -> thucHienTraCuu());

        // Thay đổi ngày -> tra cứu
        dpTuNgay.valueProperty().addListener((o, a, b) -> thucHienTraCuu());
        dpDenNgay.valueProperty().addListener((o, a, b) -> thucHienTraCuu());

        // Nút tải lại -> reset bộ lọc + clear bảng
        btnTaiLai.setOnAction(this::handleTaiLai);
    }

    private void handleTaiLai(ActionEvent e) {
        resetBoLoc();
        duLieuBang.clear();
        bang.setPlaceholder(new Label("Nhập điều kiện tìm kiếm / lọc để hiển thị hóa đơn"));
    }

    /** Clear controls về trạng thái ban đầu (không tìm, không lọc) */
    private void resetBoLoc() {
        tfTim.clear();
        cbLocTrangThai.getSelectionModel().select(TrangThaiHD.TAT_CA);
        dpTuNgay.setValue(null);
        dpDenNgay.setValue(null);
        bang.getSortOrder().clear();
    }

    /** Kiểm tra xem ngoài ô text ra còn filter nào đang bật không */
    private boolean khongCoBoLocKhac() {
        TrangThaiHD trangThai = cbLocTrangThai.getValue();
        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();
        return (trangThai == null || trangThai == TrangThaiHD.TAT_CA)
                && tuNgay == null
                && denNgay == null;
    }

    private void thucHienTraCuu() {
        final String tuKhoaRaw = Optional.ofNullable(tfTim.getText()).orElse("").trim();
        final String tuKhoa = tuKhoaRaw.toLowerCase();
        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();
        final TrangThaiHD trangThai = cbLocTrangThai.getValue();
        if (tuNgay != null && denNgay == null) {
            denNgay = tuNgay;
        } else if (tuNgay == null && denNgay != null) {
            tuNgay = denNgay;
        }
        boolean khongCoBoLoc = tuKhoa.isEmpty()
                && tuNgay == null
                && denNgay == null
                && (trangThai == null || trangThai == TrangThaiHD.TAT_CA);

        if (khongCoBoLoc) {
            duLieuBang.clear();
            bang.setPlaceholder(new Label("Nhập điều kiện tìm kiếm / lọc để hiển thị hóa đơn"));
            return;
        }

        try {
            HoaDon_DAO dao = new HoaDon_DAO();

            String trangThaiRaw = mapTrangThaiToDbValue(trangThai);

            List<HoaDon> ketQua = dao.timKiem(
                    tuKhoa.isEmpty() ? null : tuKhoa,
                    trangThaiRaw,
                    tuNgay,
                    denNgay);

            duLieuBang.setAll(ketQua);

            if (ketQua.isEmpty()) {
                bang.setPlaceholder(new Label("Không tìm thấy hóa đơn phù hợp điều kiện."));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Lỗi khi tải dữ liệu hóa đơn:\n" + ex.getMessage())
                    .showAndWait();
        }
    }

    private static void dinhDangNgayPill(DatePicker dp, String prompt) {
        dp.setPromptText(prompt);
        dp.setEditable(false);
        dp.setPrefWidth(132);
        dp.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");
    }

    private static Node pill(Node inner) {
        HBox box = new HBox(inner);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5, 10, 5, 10));
        box.setStyle(
                "-fx-background-color:white; -fx-border-color:#e6e9ee; -fx-background-radius:20; -fx-border-radius:20;");
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

    /** Map chuỗi trạng thái DB -> enum UI để hiển thị màu/nhãn */
    private static TrangThaiHD mapTrangThai(String raw) {
        if (raw == null)
            return TrangThaiHD.DANG_CHO;
        String x = raw.trim().toLowerCase();

        // Đã thanh toán -> Hoàn thành
        if (x.contains("đã thanh toán") || x.contains("da thanh toan"))
            return TrangThaiHD.HOAN_THANH;

        // Chưa thanh toán -> Đang chờ
        if (x.contains("chưa thanh toán") || x.contains("chua thanh toan"))
            return TrangThaiHD.DANG_CHO;

        // Đã hủy -> Đã hủy
        if (x.contains("đã hủy") || x.contains("da huy") || x.contains("hủy") || x.contains("huy"))
            return TrangThaiHD.DA_HUY;

        return TrangThaiHD.DANG_CHO;
    }

    /** Map enum trạng thái trên UI -> chuỗi lưu trong DB */
    private static String mapTrangThaiToDbValue(TrangThaiHD st) {
        if (st == null || st == TrangThaiHD.TAT_CA)
            return null;

        return switch (st) {
            case HOAN_THANH -> "Đã thanh toán";
            case DANG_CHO -> "Chưa thanh toán";
            case DA_HUY -> "Đã hủy";
            default -> null;
        };
    }

    public enum TrangThaiHD {
        TAT_CA("Tất cả", Color.web("#6b7280")),
        HOAN_THANH("Hoàn thành", Color.web("#10b981")),
        DANG_CHO("Đang chờ", Color.web("#3b82f6")),
        DA_HUY("Đã hủy", Color.web("#ef4444"));

        private final String nhan;
        private final Color mau;

        TrangThaiHD(String n, Color m) {
            nhan = n;
            mau = m;
        }

        public String nhan() {
            return nhan;
        }

        public Color mau() {
            return mau;
        }

        public static StringConverter<TrangThaiHD> converter() {
            return new StringConverter<TrangThaiHD>() {
                @Override
                public String toString(TrangThaiHD s) {
                    return s == null ? "" : s.nhan;
                }

                @Override
                public TrangThaiHD fromString(String s) {
                    for (TrangThaiHD v : values())
                        if (Objects.equals(v.nhan, s))
                            return v;
                    return null;
                }
            };
        }
    }
}
