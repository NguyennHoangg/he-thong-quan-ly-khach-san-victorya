package view;

import controller.HuyPhong_Controller;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.PhieuHuyPhong;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class QuanLiHuyPhong_GUI extends BorderPane {

    private final DatePicker dpTuNgay = new DatePicker();
    private final DatePicker dpDenNgay = new DatePicker();
    private final Button btnTaiLai = new Button("Tải lại");

    private final TableView<PhieuHuyPhong> bang = new TableView<>();
    private final ObservableList<PhieuHuyPhong> duLieuBang = FXCollections.observableArrayList();

    private final HuyPhong_Controller controller = new HuyPhong_Controller();
    private final DateTimeFormatter dinhDangDMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QuanLiHuyPhong_GUI() {
        setPadding(new Insets(16, 24, 24, 24));
        setTop(xayDungKhuVucTieuDe());
        setCenter(xayDungKhuVucNoiDung());
        khoiTaoBang();
        khoiTaoSuKien();

        bang.setItems(duLieuBang);
        bang.setPlaceholder(new Label("Nhấn Tải lại để xem danh sách phiếu hủy phòng"));
    }

    private Node xayDungKhuVucTieuDe() {
        Label tieuDe = new Label("Danh sách phiếu hủy phòng");
        tieuDe.setStyle("-fx-font-size:22px; -fx-font-weight:800; -fx-text-fill:#111827;");
        VBox box = new VBox(tieuDe);
        box.setPadding(new Insets(0, 0, 8, 0));
        return box;
    }

    private Node xayDungKhuVucNoiDung() {

        dinhDangNgayPill(dpTuNgay, "Từ ngày");
        dinhDangNgayPill(dpDenNgay, "Đến ngày");

        btnTaiLai.setStyle(
                "-fx-background-color:#e5e7eb; -fx-text-fill:#111827; "
                        + "-fx-background-radius:999; -fx-padding:6 12;");

        HBox nhomLoc = new HBox(
                pill(bocIcon("📅", dpTuNgay)),
                pill(bocIcon("📅", dpDenNgay)),
                btnTaiLai);
        nhomLoc.setSpacing(10);
        nhomLoc.setAlignment(Pos.CENTER_LEFT);
        nhomLoc.setPadding(new Insets(10));
        nhomLoc.setStyle(
                "-fx-background-color:#f8fafc; -fx-background-radius:10; "
                        + "-fx-border-color:#e8edf3; -fx-border-radius:10;");

        bang.setPrefHeight(600);

        VBox center = new VBox(nhomLoc, bang);
        center.setSpacing(10);
        VBox.setVgrow(bang, Priority.ALWAYS);
        return center;
    }

    private void khoiTaoBang() {
        bang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bang.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<PhieuHuyPhong, String> cMa = new TableColumn<>("Mã hủy");
        cMa.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getMaHuyPhong()));

        TableColumn<PhieuHuyPhong, String> cPhong = new TableColumn<>("Số phòng");
        cPhong.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue()
                        .getPdp()
                        .getDsachPhieuDatPhong()
                        .get(0)
                        .getPhong()
                        .getSoPhong()));

        TableColumn<PhieuHuyPhong, String> cKh = new TableColumn<>("Khách hàng");
        cKh.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue()
                        .getPdp()
                        .getKhachHang()
                        .getTenKhachHang()));

        TableColumn<PhieuHuyPhong, String> cLyDo = new TableColumn<>("Lý do");
        cLyDo.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getLyDo()));

        TableColumn<PhieuHuyPhong, String> cNgay = new TableColumn<>("Ngày hủy");

        cNgay.setCellValueFactory(cd -> {
            java.sql.Date d = cd.getValue().getNgayHuy();
            String text = (d == null) ? "-" : d.toLocalDate().format(dinhDangDMY);
            return new ReadOnlyStringWrapper(text);
        });

        cNgay.setStyle("-fx-alignment:CENTER;");

        bang.getColumns().setAll(cMa, cPhong, cKh, cLyDo, cNgay);
    }

    private void khoiTaoSuKien() {

        dpTuNgay.valueProperty().addListener((o, a, b) -> thucHienLoc());
        dpDenNgay.valueProperty().addListener((o, a, b) -> thucHienLoc());

        btnTaiLai.setOnAction(e -> {
            dpTuNgay.setValue(null);
            dpDenNgay.setValue(null);
            duLieuBang.setAll(controller.layDsHuyPhong());
        });
    }

    private void thucHienLoc() {
        LocalDate tu = dpTuNgay.getValue();
        LocalDate den = dpDenNgay.getValue();

        if (tu == null && den == null)
            return;

        if (tu == null)
            tu = den;
        if (den == null)
            den = tu;

        duLieuBang.setAll(controller.layDsHuyPhongTheoKhoangNgay(tu, den));
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
                "-fx-background-color:white; -fx-border-color:#e6e9ee; "
                        + "-fx-background-radius:20; -fx-border-radius:20;");
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
}
