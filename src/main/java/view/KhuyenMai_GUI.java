package view;

import controller.KhuyenMai_Controller;
import model.KhuyenMai;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class KhuyenMai_GUI extends BorderPane {

    private final KhuyenMai_Controller kmController = new KhuyenMai_Controller();

    private final TextField tfTen = new TextField();
    private final TextField tfSoTien = new TextField();
    private final TextField tfHeSo = new TextField();
    private final TextField tfGiamToiDa = new TextField();

    private final ComboBox<KhuyenMai.TrangThai> cbTrangThai = new ComboBox<>();

    private final DatePicker dpNgayBatDau = new DatePicker();
    private final DatePicker dpNgayKetThuc = new DatePicker();

    private final Button btnLuu = new Button("Lưu");
    private final Button btnXoa = new Button("Xóa đã chọn");
    private final Button btnTaiLai = new Button("Tải lại");

    private final TextField tfTim = new TextField();
    private final ComboBox<KhuyenMai.TrangThai> cbLocTrangThai = new ComboBox<>();
    private final DatePicker dpLocNgayBD = new DatePicker();
    private final DatePicker dpLocNgayKT = new DatePicker();

    private final TableView<KhuyenMai> table = new TableView<>();

    private final ObservableList<KhuyenMai> duLieuGoc = FXCollections.observableArrayList();
    private final FilteredList<KhuyenMai> duLieuLoc = new FilteredList<>(duLieuGoc, p -> true);
    private final SortedList<KhuyenMai> duLieuSapXep = new SortedList<>(duLieuLoc);

    private final DateTimeFormatter fmtDMY = DateTimeFormatter.ofPattern("d/M/yy");
    private final NumberFormat nfVn = NumberFormat.getInstance(new Locale("vi", "VN"));
    private final NumberFormat pctVn = NumberFormat.getPercentInstance(new Locale("vi", "VN"));

    public KhuyenMai_GUI() {
        pctVn.setMaximumFractionDigits(2);

        setPadding(new Insets(16, 24, 24, 24));
        setTop(xayDungKhuVucTren());
        setCenter(xayDungKhuVucGiua());

        khoiTaoCombobox();
        khoiTaoBang();
        khoiTaoSuKien();
        taiDuLieu();
    }

    private void taiDuLieu() {
        try {
            List<KhuyenMai> all = kmController.getAll();
            duLieuGoc.setAll(all);
            apDungBoLoc();
        } catch (Exception ex) {
            duLieuGoc.clear();
            apDungBoLoc();
            hienThongBao("Không thể tải dữ liệu từ SQL Server",
                    "Lỗi: " + ex.getMessage() + "\nĐang hiển thị dữ liệu trống.");
        }
    }

    private Node xayDungKhuVucTren() {
        GridPane form = new GridPane();
        form.setHgap(24);
        form.setVgap(12);
        form.setPadding(new Insets(12, 0, 0, 0));

        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(50);
        left.setMaxWidth(380);

        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(50);
        right.setMaxWidth(380);

        form.getColumnConstraints().addAll(left, right);

        int r = 0;

        form.add(taoNhan("Tên khuyến mãi"), 0, r);
        dinhDangInput(tfTen, "Nhập tên khuyến mãi");
        form.add(tfTen, 0, r + 1);

        form.add(taoNhan("Trạng thái"), 1, r);
        dinhDangCombo(cbTrangThai, "Trạng thái");
        cbTrangThai.setDisable(true);
        form.add(cbTrangThai, 1, r + 1);
        r += 2;

        form.add(taoNhan("Số tiền áp dụng"), 0, r);
        dinhDangInput(tfSoTien, "Nhập số tiền áp dụng");
        form.add(tfSoTien, 0, r + 1);

        form.add(taoNhan("Tiền khuyến mãi tối đa (VND)"), 1, r);
        HBox hopGiamTD = new HBox();
        hopGiamTD.setAlignment(Pos.CENTER_LEFT);
        dinhDangInput(tfGiamToiDa, "Nhập số tiền giảm tối đa");
        tfGiamToiDa.setPrefWidth(360);
        hopGiamTD.getChildren().add(tfGiamToiDa);
        form.add(hopGiamTD, 1, r + 1);
        r += 2;

        form.add(taoNhan("Thời gian áp dụng"), 0, r);
        form.add(taoNhan("Hệ số"), 1, r);
        r++;

        HBox hopNgay = new HBox(8);
        hopNgay.setAlignment(Pos.CENTER_LEFT);
        dinhDangDatePicker(dpNgayBatDau, "Ngày bắt đầu");
        dinhDangDatePicker(dpNgayKetThuc, "Ngày kết thúc");
        formatDatePicker(dpNgayBatDau);
        formatDatePicker(dpNgayKetThuc);
        formatDatePicker(dpLocNgayBD);
        formatDatePicker(dpLocNgayKT);
        Label den = new Label("—");
        den.setStyle("-fx-text-fill:#6b7280; -fx-opacity:0.9;");
        hopNgay.getChildren().addAll(dpNgayBatDau, den, dpNgayKetThuc);
        form.add(hopNgay, 0, r);

        HBox hopHeSo = new HBox();
        hopHeSo.setAlignment(Pos.CENTER_LEFT);
        dinhDangInput(tfHeSo, "Hệ số (0.1 = 10%)");
        tfHeSo.setPrefWidth(120);
        hopHeSo.getChildren().add(tfHeSo);
        form.add(hopHeSo, 1, r);
        r += 2;

        btnLuu.setStyle(
                "-fx-background-color:#155EEB; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");
        btnXoa.setStyle(
                "-fx-background-color:#f44336; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");
        btnTaiLai.setStyle(
                "-fx-background-color:#9e9e9e; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");

        btnLuu.textProperty().bind(
                Bindings.when(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()))
                        .then("Thêm mới")
                        .otherwise("Cập nhật"));

        HBox actions = new HBox(10, btnLuu, btnXoa, btnTaiLai);
        actions.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(actions, new Insets(10, 0, 20, 0));
        form.add(actions, 0, r, 2, 1);

        VBox top = new VBox(form);
        top.setSpacing(16);
        return top;
    }

    private Node xayDungKhuVucGiua() {
        tfTim.setPromptText("Nhập mã hoặc tên khuyến mãi");
        tfTim.setPrefWidth(200);
        tfTim.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");

        dinhDangComboPill(cbLocTrangThai, "Trạng thái");
        dinhDangDatePill(dpLocNgayBD, "Ngày bắt đầu");
        dinhDangDatePill(dpLocNgayKT, "Ngày kết thúc");

        HBox boLoc = new HBox(
                taoPill(bocIcon("🔍", tfTim)),
                taoPill(cbLocTrangThai),
                taoPill(bocIcon("📅", dpLocNgayBD)),
                taoPill(bocIcon("📅", dpLocNgayKT)));
        boLoc.setSpacing(10);
        boLoc.setAlignment(Pos.CENTER_LEFT);
        boLoc.setPadding(new Insets(14));
        boLoc.setStyle(
                "-fx-background-color:#f7fafc; -fx-background-radius:10; -fx-border-color:#e8edf3; -fx-border-radius:10;");

        VBox center = new VBox(boLoc, table);
        center.setSpacing(10);
        VBox.setVgrow(table, Priority.ALWAYS);
        return center;
    }

    private void khoiTaoCombobox() {
        cbTrangThai.setItems(FXCollections.observableArrayList(
                KhuyenMai.TrangThai.DANG_HOAT_DONG,
                KhuyenMai.TrangThai.SAP_DIEN_RA,
                KhuyenMai.TrangThai.KET_THUC));
        cbTrangThai.setConverter(trangThaiConverter());

        cbLocTrangThai.setItems(FXCollections.observableArrayList(
                null,
                KhuyenMai.TrangThai.DANG_HOAT_DONG,
                KhuyenMai.TrangThai.SAP_DIEN_RA,
                KhuyenMai.TrangThai.KET_THUC));
        cbLocTrangThai.setConverter(new StringConverter<>() {
            @Override
            public String toString(KhuyenMai.TrangThai st) {
                return st == null ? "Tất cả" : st.label();
            }

            @Override
            public KhuyenMai.TrangThai fromString(String s) {
                return null;
            }
        });

        cbLocTrangThai.setValue(KhuyenMai.TrangThai.DANG_HOAT_DONG);
    }

    private void khoiTaoBang() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<KhuyenMai, String> colTen = new TableColumn<>("Tên");
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenKhuyenMai"));

        TableColumn<KhuyenMai, Float> colSoTien = new TableColumn<>("Số tiền áp dụng");
        colSoTien.setCellValueFactory(new PropertyValueFactory<>("tongTienToiThieu"));
        colSoTien.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : nfVn.format(Math.round(item)));
            }
        });

        TableColumn<KhuyenMai, Float> colHeSo = new TableColumn<>("Hệ số");
        colHeSo.setCellValueFactory(new PropertyValueFactory<>("heSo"));
        colHeSo.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : pctVn.format(item));
            }
        });

        TableColumn<KhuyenMai, Float> colGiamToiDa = new TableColumn<>("KM tối đa");
        colGiamToiDa.setCellValueFactory(new PropertyValueFactory<>("tongKhuyenMaiToiDa"));
        colGiamToiDa.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : nfVn.format(Math.round(item)));
            }
        });

        TableColumn<KhuyenMai, String> colNgayBD = new TableColumn<>("Ngày bắt đầu");
        colNgayBD.setCellValueFactory((CellDataFeatures<KhuyenMai, String> cell) -> {
            LocalDateTime ldt = cell.getValue().getNgayBatDau();
            return new ReadOnlyStringWrapper(dinhDangNgay(ldt == null ? null : ldt.toLocalDate()));
        });

        TableColumn<KhuyenMai, String> colNgayKT = new TableColumn<>("Ngày kết thúc");
        colNgayKT.setCellValueFactory((CellDataFeatures<KhuyenMai, String> cell) -> {
            LocalDateTime ldt = cell.getValue().getNgayKetThuc();
            return new ReadOnlyStringWrapper(dinhDangNgay(ldt == null ? null : ldt.toLocalDate()));
        });

        TableColumn<KhuyenMai, KhuyenMai.TrangThai> colTrangThai = new TableColumn<>("Trạng thái");
        colTrangThai.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getTrangThai()));
        colTrangThai.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(KhuyenMai.TrangThai st, boolean empty) {
                super.updateItem(st, empty);
                if (empty || st == null)
                    setGraphic(null);
                else
                    setGraphic(vienChip(st.label(), mauTrangThai(st)));
            }
        });

        table.getColumns().addAll(colTen, colSoTien, colHeSo, colGiamToiDa, colNgayBD, colNgayKT, colTrangThai);

        table.setItems(duLieuSapXep);
        duLieuSapXep.comparatorProperty().bind(table.comparatorProperty());
        Label lbKhongTimThay = new Label("🔍 Không tìm thấy khuyến mãi phù hợp");
        lbKhongTimThay.setStyle("-fx-text-fill:#6b7280; -fx-font-size:14px;");
        table.setPlaceholder(lbKhongTimThay);
    }

    private void khoiTaoSuKien() {
        // TIỀN: cho phép số + dấu chấm, và tự format khi rời ô
        UnaryOperator<TextFormatter.Change> moneyFilter = change -> {
            String n = change.getControlNewText();
            if (n == null || n.isEmpty())
                return change;

            // chỉ cho phép số và dấu chấm
            if (!n.matches("[0-9.]*"))
                return null;

            // không cho toàn dấu chấm
            if (n.replace(".", "").isEmpty())
                return null;

            return change;
        };
        tfSoTien.setTextFormatter(new TextFormatter<>(moneyFilter));
        tfGiamToiDa.setTextFormatter(new TextFormatter<>(moneyFilter));

        tfSoTien.focusedProperty().addListener((obs, ov, nv) -> {
            if (Boolean.FALSE.equals(nv))
                tfSoTien.setText(formatVnd(tfSoTien.getText()));
        });
        tfGiamToiDa.focusedProperty().addListener((obs, ov, nv) -> {
            if (Boolean.FALSE.equals(nv))
                tfGiamToiDa.setText(formatVnd(tfGiamToiDa.getText()));
        });

        tfHeSo.setTextFormatter(new TextFormatter<String>((UnaryOperator<TextFormatter.Change>) change -> {
            String n = change.getControlNewText();
            if (n == null || n.isEmpty())
                return change;
            if ("-".equals(n))
                return change;
            if (".".equals(n) || "0.".equals(n))
                return change;
            if (!n.matches("^-?\\d*(\\.\\d{0,4})?$"))
                return null;
            return change;
        }));

        tfHeSo.focusedProperty().addListener((obs, oldV, newV) -> {
            if (Boolean.FALSE.equals(newV)) {
                String raw = tfHeSo.getText();
                if (raw == null || raw.trim().isEmpty())
                    return;
                try {
                    parseHeSoStrict(raw);
                } catch (NumberFormatException ex) {
                    hienThongBaoLoi("Hệ số không hợp lệ",
                            "Hệ số phải là số thực > 0 và < 1 (ví dụ: 0.1, 0.25, 0.5).");
                    tfHeSo.requestFocus();
                    tfHeSo.selectAll();
                }
            }
        });

        // trạng thái form tự cập nhật theo ngày
        Runnable updateFormStatus = () -> {
            LocalDate bd = dpNgayBatDau.getValue();
            LocalDate kt = dpNgayKetThuc.getValue();
            if (bd == null || kt == null) {
                cbTrangThai.setValue(null);
                return;
            }
            cbTrangThai.setValue(KhuyenMai.TrangThai.computeByDates(bd, kt));
        };
        dpNgayBatDau.valueProperty().addListener((o, ov, nv) -> updateFormStatus.run());
        dpNgayKetThuc.valueProperty().addListener((o, ov, nv) -> updateFormStatus.run());

        final var formKhongHopLe = tfTen.textProperty().isEmpty()
                .or(tfSoTien.textProperty().isEmpty())
                .or(tfHeSo.textProperty().isEmpty())
                .or(tfGiamToiDa.textProperty().isEmpty())
                .or(dpNgayBatDau.valueProperty().isNull())
                .or(dpNgayKetThuc.valueProperty().isNull());

        btnLuu.disableProperty().bind(formKhongHopLe);
        btnXoa.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));

        EventHandler<ActionEvent> themMoiHandler = e -> {
            boolean success = false;
            try {
                int soTienToiThieu = parseVndToInt(tfSoTien.getText());
                if (soTienToiThieu < 0) {
                    hienThongBaoLoi("Giá trị không hợp lệ", "Số tiền áp dụng phải >= 0.");
                    return;
                }

                float heSo = parseHeSoStrict(tfHeSo.getText());

                if (dpNgayKetThuc.getValue().isBefore(dpNgayBatDau.getValue())) {
                    hienThongBaoLoi("Ngày không hợp lệ", "Ngày kết thúc phải >= ngày bắt đầu.");
                    return;
                }

                int giamToiDaInt = parseVndToInt(tfGiamToiDa.getText());
                if (giamToiDaInt < 0) {
                    hienThongBaoLoi("Giá trị không hợp lệ", "Tiền khuyến mãi tối đa phải >= 0.");
                    return;
                }
                if(soTienToiThieu<giamToiDaInt){
                    hienThongBaoLoi("Tiền khuyến mãi được giảm không được vượt tiền tối thiểu","Vui lòng nhập lại");
                    return;
                }
                KhuyenMai.TrangThai trangThai = KhuyenMai.TrangThai.computeByDates(
                        dpNgayBatDau.getValue(), dpNgayKetThuc.getValue());
                if (trangThai == KhuyenMai.TrangThai.KET_THUC) {
                    hienThongBaoLoi("Không thể thêm khuyến mãi",
                            "Khuyến mãi đã kết thúc (ngày kết thúc đã qua). Vui lòng chọn lại thời gian.");
                    return;}
                KhuyenMai entity = new KhuyenMai(
                        "",
                        tfTen.getText(),
                        dpNgayBatDau.getValue().atStartOfDay(),
                        dpNgayKetThuc.getValue().atStartOfDay(),
                        trangThai,
                        heSo,
                        (float) soTienToiThieu,
                        (float) giamToiDaInt);

                String newId = kmController.addAndReturnId(entity);
                if (newId == null) {
                    hienThongBaoLoi("Không thể thêm", "Thêm mới thất bại.");
                    return;
                }

                KhuyenMai entity1 = new KhuyenMai(
                        newId,
                        tfTen.getText(),
                        dpNgayBatDau.getValue().atStartOfDay(),
                        dpNgayKetThuc.getValue().atStartOfDay(),
                        trangThai,
                        heSo,
                        (float) soTienToiThieu,
                        (float) giamToiDaInt);

                duLieuGoc.add(0, entity1);
                apDungBoLoc();
                table.refresh();

                hienThongBao("Đã thêm", "Đã thêm khuyến mãi \"" + entity1.getTenKhuyenMai() + "\"");
                success = true;

            } catch (NumberFormatException nfe) {
                hienThongBaoLoi("Giá trị không hợp lệ", "Hệ số / số tiền không đúng định dạng.");
            } catch (Exception ex) {
                hienThongBaoLoi("Lỗi", ex.getMessage());
            } finally {
                if (success)
                    resetForm();
            }
        };

        EventHandler<ActionEvent> capNhatHandler = e -> {
            boolean success = false;
            try {
                KhuyenMai km = table.getSelectionModel().getSelectedItem();
                if (km == null) {
                    hienThongBao("Thiếu lựa chọn", "Vui lòng chọn một khuyến mãi cần cập nhật từ bảng.");
                    return;
                }

                int soTien = parseVndToInt(tfSoTien.getText());
                if (soTien < 0) {
                    hienThongBaoLoi("Giá trị không hợp lệ", "Số tiền áp dụng phải >= 0.");
                    return;
                }

                float heSo = parseHeSoStrict(tfHeSo.getText());

                if (dpNgayKetThuc.getValue().isBefore(dpNgayBatDau.getValue())) {
                    hienThongBaoLoi("Ngày không hợp lệ", "Ngày kết thúc phải >= ngày bắt đầu.");
                    return;
                }

                int giamToiDaInt = parseVndToInt(tfGiamToiDa.getText());
                if (giamToiDaInt < 0) {
                    hienThongBaoLoi("Giá trị không hợp lệ", "Tiền khuyến mãi tối đa phải >= 0.");
                    return;
                }
                if(soTien<giamToiDaInt){
                    hienThongBaoLoi("Tiền khuyến mãi được giảm không được vượt tiền tối thiểu","Vui lòng nhập lại");
               return;
                }
                km.setTenKhuyenMai(tfTen.getText());
                km.setNgayBatDau(dpNgayBatDau.getValue().atStartOfDay());
                km.setNgayKetThuc(dpNgayKetThuc.getValue().atStartOfDay());
                km.setHeSo(heSo);
                km.setTongTienToiThieu((float) soTien);
                km.setTongKhuyenMaiToiDa((float) giamToiDaInt);

                km.setTrangThai(KhuyenMai.TrangThai.computeByDates(dpNgayBatDau.getValue(), dpNgayKetThuc.getValue()));

                boolean updated = kmController.update(km);
                if (!updated) {
                    hienThongBaoLoi("Không thể cập nhật", "Không có bản ghi nào được cập nhật.");
                    return;
                }

                table.refresh();
                apDungBoLoc();
                hienThongBao("Đã cập nhật", "Cập nhật khuyến mãi \"" + km.getTenKhuyenMai() + "\" thành công.");
                success = true;

            } catch (NumberFormatException nfe) {
                hienThongBaoLoi("Giá trị không hợp lệ", "Hệ số / số tiền không đúng định dạng.");
            } catch (Exception ex) {
                hienThongBaoLoi("Lỗi", ex.getMessage());
            } finally {
                if (success)
                    resetForm();
            }
        };

        btnLuu.setOnAction(e -> {
            if (table.getSelectionModel().getSelectedItem() == null)
                themMoiHandler.handle(e);
            else
                capNhatHandler.handle(e);
        });

        btnTaiLai.setOnAction(e -> {
            try {
                resetBoLoc();
                taiDuLieu();
                hienThongBao("Đã tải lại", "Danh sách khuyến mãi đã được tải lại từ CSDL.");
            } finally {
                resetForm();
            }
        });

        btnXoa.setOnAction(e -> {
            try {
                ObservableList<KhuyenMai> chon = table.getSelectionModel().getSelectedItems();
                if (chon == null || chon.isEmpty()) {
                    hienThongBao("Chưa chọn", "Hãy chọn ít nhất 1 dòng để xóa.");
                    return;
                }

                Alert xacNhan = new Alert(Alert.AlertType.CONFIRMATION);
                xacNhan.setTitle("Xác nhận xóa");
                xacNhan.setHeaderText("Xóa " + chon.size() + " khuyến mãi?");
                xacNhan.setContentText("Hành động này không thể hoàn tác.");
                var rs = xacNhan.showAndWait();
                if (rs.isEmpty() || rs.get() != ButtonType.OK)
                    return;

                java.util.List<String> ids = new java.util.ArrayList<>();
                for (KhuyenMai p : chon)
                    ids.add(p.getMaKhuyenMai());

                int deleted = kmController.deleteMany(ids);
                if (deleted <= 0) {
                    hienThongBaoLoi("Không thể xóa", "Không xóa được bản ghi nào.");
                    return;
                }

                duLieuGoc.removeAll(new java.util.ArrayList<>(chon));
                hienThongBao("Đã xóa", "Đã xóa " + deleted + " bản ghi.");
            } finally {
                resetForm();
            }
        });

        // đổ form theo dòng chọn
        table.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends KhuyenMai> obs, KhuyenMai oldSel, KhuyenMai sel) -> {
                    if (sel == null)
                        return;

                    tfTen.setText(sel.getTenKhuyenMai());
                    tfSoTien.setText(nfVn.format(Math.round(sel.getTongTienToiThieu())));
                    tfHeSo.setText(Float.toString(sel.getHeSo()));
                    tfGiamToiDa.setText(nfVn.format(Math.round(sel.getTongKhuyenMaiToiDa())));
                    dpNgayBatDau.setValue(sel.getNgayBatDau() == null ? null : sel.getNgayBatDau().toLocalDate());
                    dpNgayKetThuc.setValue(sel.getNgayKetThuc() == null ? null : sel.getNgayKetThuc().toLocalDate());
                    cbTrangThai.setValue(sel.getTrangThai());

                    tfTen.requestFocus();
                    tfTen.selectAll();
                });

        tfTim.textProperty().addListener((obs, o, n) -> apDungBoLoc());
        cbLocTrangThai.valueProperty().addListener((obs, o, n) -> apDungBoLoc());
        dpLocNgayBD.valueProperty().addListener((obs, o, n) -> apDungBoLoc());
        dpLocNgayKT.valueProperty().addListener((obs, o, n) -> apDungBoLoc());
    }

    private void resetBoLoc() {
        tfTim.clear();
        dpLocNgayBD.setValue(null);
        dpLocNgayKT.setValue(null);
        cbLocTrangThai.setValue(KhuyenMai.TrangThai.DANG_HOAT_DONG);
    }

    private void apDungBoLoc() {
        final KhuyenMai.TrangThai stLoc = cbLocTrangThai.getValue();
        final LocalDate from = dpLocNgayBD.getValue();
        final LocalDate to = dpLocNgayKT.getValue();
        final String tuKhoa = tfTim.getText() == null ? "" : tfTim.getText().trim().toLowerCase();

        duLieuLoc.setPredicate((KhuyenMai p) -> {
            if (p == null)
                return false;

            if (stLoc != null && p.getTrangThai() != stLoc)
                return false;

            if (!tuKhoa.isEmpty()) {
                String ten = p.getTenKhuyenMai() == null ? "" : p.getTenKhuyenMai().toLowerCase();
                String ma = p.getMaKhuyenMai() == null ? "" : p.getMaKhuyenMai().toLowerCase();
                if (!ten.contains(tuKhoa) && !ma.contains(tuKhoa))
                    return false;
            }

            LocalDate bd = p.getNgayBatDau() == null ? null : p.getNgayBatDau().toLocalDate();
            LocalDate kt = p.getNgayKetThuc() == null ? null : p.getNgayKetThuc().toLocalDate();

            if (from != null) {
                if (bd == null || bd.isBefore(from))
                    return false;
            }
            if (to != null) {
                if (kt == null || kt.isAfter(to))
                    return false;
            }
            return true;
        });
    }

    // TIỆN ÍCH TIỀN
    private int parseVndToInt(String raw) throws NumberFormatException {
        if (raw == null)
            throw new NumberFormatException("null");
        String x = raw.trim().replace(".", "");
        if (x.isEmpty())
            throw new NumberFormatException("empty");
        return Integer.parseInt(x);
    }

    private String formatVnd(String raw) {
        if (raw == null)
            return "";
        String x = raw.trim().replace(".", "");
        if (x.isEmpty())
            return "";
        long v = Long.parseLong(x);
        return nfVn.format(v);
    }

    private float parseHeSoStrict(String raw) throws NumberFormatException {
        if (raw == null || raw.trim().isEmpty())
            throw new NumberFormatException("empty");
        float v = Float.parseFloat(raw.trim());
        if (v <= 0f || v >= 1f)
            throw new NumberFormatException("hệ số không hợp lệ");
        return v;
    }

    private static StringConverter<KhuyenMai.TrangThai> trangThaiConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(KhuyenMai.TrangThai s) {
                return s == null ? "" : s.label();
            }

            @Override
            public KhuyenMai.TrangThai fromString(String s) {
                return KhuyenMai.TrangThai.fromDb(s);
            }
        };
    }

    private static Color mauTrangThai(KhuyenMai.TrangThai st) {
        if (st == null)
            return Color.GRAY;
        return switch (st) {
            case DANG_HOAT_DONG -> Color.web("#10b981");
            case SAP_DIEN_RA -> Color.web("#f59e0b");
            case KET_THUC -> Color.web("#ef4444");
        };
    }

    private static void dinhDangInput(TextField tf, String prompt) {
        tf.setPromptText(prompt);
        tf.setPrefWidth(340);
        tf.setStyle(
                "-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:8 12;");
    }

    private static <T> void dinhDangCombo(ComboBox<T> cb, String prompt) {
        cb.setPromptText(prompt);
        cb.setPrefWidth(340);
        cb.setStyle(
                "-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:4 10;");
    }

    private static void dinhDangDatePicker(DatePicker dp, String prompt) {
        dp.setPromptText(prompt);
        dp.setEditable(false);
        dp.setPrefWidth(165);
        dp.setStyle(
                "-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:6 10;");
    }

    private static <T> void dinhDangComboPill(ComboBox<T> cb, String prompt) {
        cb.setPromptText(prompt);
        cb.setPrefWidth(140);
        cb.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");
    }

    private static void dinhDangDatePill(DatePicker dp, String prompt) {
        dp.setPromptText(prompt);
        dp.setEditable(false);
        dp.setPrefWidth(140);
        dp.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");
    }

    private static Node taoPill(Node inner) {
        HBox box = new HBox(inner);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle(
                "-fx-background-color:white; -fx-border-color:#e6e9ee; -fx-background-radius:20; -fx-border-radius:20;");
        return box;
    }

    private static Label taoNhan(String text) {
        Label lb = new Label(text);
        lb.setStyle("-fx-font-size:12px; -fx-text-fill:#5b667a;");
        return lb;
    }

    private static HBox bocIcon(String icon, Node node) {
        Label lb = new Label(icon);
        lb.setStyle("-fx-opacity:0.8;");
        HBox h = new HBox(lb, node);
        h.setSpacing(6);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private static StackPane vienChip(String text, Color color) {
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

    private void resetForm() {
        tfTen.clear();
        tfSoTien.clear();
        tfHeSo.clear();
        tfGiamToiDa.clear();
        cbTrangThai.getSelectionModel().clearSelection();
        dpNgayBatDau.setValue(null);
        dpNgayKetThuc.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    private void hienThongBao(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void hienThongBaoLoi(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String dinhDangNgay(LocalDate d) {
        return d == null ? "-" : d.format(fmtDMY);
    }

    private void formatDatePicker(DatePicker dp) {
        dp.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate d) {
                return d == null ? "" : d.format(fmtDMY);
            }

            @Override
            public LocalDate fromString(String s) {
                return (s == null || s.isBlank()) ? null : LocalDate.parse(s, fmtDMY);
            }
        });
    }

}
