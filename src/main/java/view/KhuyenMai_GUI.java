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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.control.TableRow;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class KhuyenMai_GUI extends BorderPane {

    private final KhuyenMai_Controller kmController = new KhuyenMai_Controller();

    private final TextField tfTen = new TextField();
    private final TextField tfSoTien = new TextField();   // VND
    private final TextField tfHeSo  = new TextField();    // chỉ nhận 0 < x < 1
    private final TextField tfGiamToiDa = new TextField();// VND - Tiền KM tối đa
    private final ComboBox<TrangThai> cbTrangThai = new ComboBox<>();
    private final DatePicker dpNgayBatDau = new DatePicker();
    private final DatePicker dpNgayKetThuc = new DatePicker();

    private final Button btnLuu = new Button("Lưu");
    private final Button btnXoa = new Button("Xóa đã chọn");
    private final Button btnTaiLai = new Button("Tải lại");

    private final TextField tfTim = new TextField();
    private final ComboBox<TrangThai> cbLocTrangThai = new ComboBox<>();
    private final DatePicker dpLocNgayBD = new DatePicker();
    private final DatePicker dpLocNgayKT = new DatePicker();

    private final TableView<KhuyenMai> table = new TableView<>();
    private final ObservableList<KhuyenMai> duLieuGoc = FXCollections.observableArrayList();
    private final FilteredList<KhuyenMai> duLieuLoc = new FilteredList<>(duLieuGoc, p -> true);
    private final SortedList<KhuyenMai> duLieuSapXep = new SortedList<>(duLieuLoc);

    private final DateTimeFormatter fmtDMY = DateTimeFormatter.ofPattern("d/M/yy");
    private final NumberFormat nfVn  = NumberFormat.getInstance(new Locale("vi", "VN"));
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
            // Lấy tất cả từ CSDL
            List<KhuyenMai> all = kmController.getAll();
            // Chỉ giữ lại khuyến mãi đang hoạt động
            List<KhuyenMai> onlyActive = all.stream()
                    .filter(km -> tinhTrangThaiHienThi(km) == TrangThai.DANG_HOAT_DONG)
                    .collect(Collectors.toList());

            duLieuGoc.setAll(onlyActive);
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

        // Hàng 0: Tên | Trạng thái
        form.add(taoNhan("Tên khuyến mãi"), 0, r);
        dinhDangInput(tfTen, "Nhập tên khuyến mãi");
        form.add(tfTen, 0, r + 1);

        form.add(taoNhan("Trạng thái"), 1, r);
        dinhDangCombo(cbTrangThai, "Trạng thái");
        form.add(cbTrangThai, 1, r + 1);
        r += 2;

        // Hàng 2: Số tiền áp dụng | Tiền khuyến mãi tối đa
        form.add(taoNhan("Số tiền áp dụng "), 0, r);
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

        // Hàng 4: Thời gian áp dụng |Hệ số
        form.add(taoNhan("Thời gian áp dụng"), 0, r);
        form.add(taoNhan("Hệ số "), 1, r);
        r++;

        HBox hopNgay = new HBox(8);
        hopNgay.setAlignment(Pos.CENTER_LEFT);
        dinhDangDatePicker(dpNgayBatDau, "Ngày bắt đầu");
        dinhDangDatePicker(dpNgayKetThuc, "Ngày kết thúc");
        Label den = new Label("—");
        den.setStyle("-fx-text-fill:#6b7280; -fx-opacity:0.9;");
        hopNgay.getChildren().addAll(dpNgayBatDau, den, dpNgayKetThuc);
        form.add(hopNgay, 0, r);

        HBox hopHeSo = new HBox();
        hopHeSo.setAlignment(Pos.CENTER_LEFT);
        dinhDangInput(tfHeSo, "Hệ số");
        tfHeSo.setPrefWidth(120);
        hopHeSo.getChildren().add(tfHeSo);
        form.add(hopHeSo, 1, r);
        r += 2;

        // Hàng nút
        btnLuu.setStyle("-fx-background-color:#155EEB; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");
        btnXoa.setStyle("-fx-background-color:#f44336; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");
        btnTaiLai.setStyle("-fx-background-color:#9e9e9e; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");

        btnLuu.textProperty().bind(
                Bindings.when(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()))
                        .then("Thêm mới")
                        .otherwise("Cập nhật")
        );

        HBox actions = new HBox(10, btnLuu, btnXoa, btnTaiLai);
        actions.setAlignment(Pos.CENTER_LEFT);
        GridPane.setHalignment(actions, javafx.geometry.HPos.LEFT);
        GridPane.setHgrow(actions, Priority.NEVER);
        GridPane.setMargin(actions, new Insets(10, 0, 20, 0));
        form.add(actions, 0, r, 2, 1);

        VBox top = new VBox(form);
        top.setSpacing(16);
        return top;
    }

    private Node xayDungKhuVucGiua() {
        tfTim.setPromptText("Nhập tên khuyến mãi");
        tfTim.setPrefWidth(200);
        tfTim.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");

        dinhDangComboPill(cbLocTrangThai, "Trạng thái");
        dinhDangDatePill(dpLocNgayBD, "Ngày bắt đầu");
        dinhDangDatePill(dpLocNgayKT, "Ngày kết thúc");

        HBox boLoc = new HBox(
                taoPill(bocIcon("🔍", tfTim)),
                taoPill(cbLocTrangThai),
                taoPill(bocIcon("📅", dpLocNgayBD)),
                taoPill(bocIcon("📅", dpLocNgayKT))
        );
        boLoc.setSpacing(10);
        boLoc.setAlignment(Pos.CENTER_LEFT);
        boLoc.setPadding(new Insets(14));
        boLoc.setStyle("-fx-background-color:#f7fafc; -fx-background-radius:10; -fx-border-color:#e8edf3; -fx-border-radius:10;");

        VBox center = new VBox(boLoc, table);
        center.setSpacing(10);
        VBox.setVgrow(table, Priority.ALWAYS);
        return center;
    }

    private void khoiTaoCombobox() {
        // Form nhập chỉ chọn 2 trạng thái
        cbTrangThai.setItems(FXCollections.observableArrayList(
                TrangThai.DANG_HOAT_DONG,
                TrangThai.KET_THUC
        ));
        cbTrangThai.setConverter(TrangThai.converter());

        // Lọc: vẫn dùng đủ TAT_CA, DANG_HOAT_DONG, KET_THUC
        cbLocTrangThai.setItems(FXCollections.observableArrayList(TrangThai.values()));
        cbLocTrangThai.setConverter(TrangThai.converter());
    }

    private void khoiTaoBang() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<KhuyenMai, String> colTen = new TableColumn<>("Tên");
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenKhuyenMai"));

        TableColumn<KhuyenMai, Float> colSoTien = new TableColumn<>("Số tiền áp dụng");
        colSoTien.setCellValueFactory(new PropertyValueFactory<>("tongTienToiThieu"));
        colSoTien.setCellFactory(tc -> new TableCell<KhuyenMai, Float>() {
            @Override protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : nfVn.format(item));
            }
        });

        TableColumn<KhuyenMai, Float> colHeSo = new TableColumn<>("Hệ số");
        colHeSo.setCellValueFactory(new PropertyValueFactory<>("heSo"));
        colHeSo.setCellFactory(tc -> new TableCell<KhuyenMai, Float>() {
            @Override protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : pctVn.format(item));
            }
        });

        TableColumn<KhuyenMai, Float> colGiamToiDa = new TableColumn<>("KM tối đa");
        colGiamToiDa.setCellValueFactory(new PropertyValueFactory<>("tongKhuyenMaiToiDa"));
        colGiamToiDa.setCellFactory(tc -> new TableCell<KhuyenMai, Float>() {
            @Override protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : nfVn.format(item));
            }
        });

        TableColumn<KhuyenMai, String> colNgayBD = new TableColumn<>("Ngày bắt đầu");
        colNgayBD.setCellValueFactory((CellDataFeatures<KhuyenMai, String> cell) ->
                new ReadOnlyStringWrapper(
                        dinhDangNgay(cell.getValue().getNgayBatDau() == null
                                ? null
                                : cell.getValue().getNgayBatDau().toLocalDate())
                )
        );

        TableColumn<KhuyenMai, String> colNgayKT = new TableColumn<>("Ngày kết thúc");
        colNgayKT.setCellValueFactory((CellDataFeatures<KhuyenMai, String> cell) ->
                new ReadOnlyStringWrapper(
                        dinhDangNgay(cell.getValue().getNgayKetThuc() == null
                                ? null
                                : cell.getValue().getNgayKetThuc().toLocalDate())
                )
        );

        TableColumn<KhuyenMai, TrangThai> colTrangThai = new TableColumn<>("Trạng thái");
        colTrangThai.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(tinhTrangThaiHienThi(cell.getValue())));
        colTrangThai.setCellFactory(tc -> new TableCell<KhuyenMai, TrangThai>() {
            @Override
            protected void updateItem(TrangThai st, boolean empty) {
                super.updateItem(st, empty);
                if (empty || st == null) setGraphic(null);
                else setGraphic(vienChip(st.hienThi(), st.mau()));
            }
        });

        table.getColumns().addAll(colTen, colSoTien, colHeSo, colGiamToiDa, colNgayBD, colNgayKT, colTrangThai);
        table.setItems(duLieuSapXep);
        duLieuSapXep.comparatorProperty().bind(table.comparatorProperty());
    }

    private void khoiTaoSuKien() {
        UnaryOperator<TextFormatter.Change> onlyDigits = change -> {
            String n = change.getControlNewText();
            if (n == null || n.isEmpty()) return change;
            for (int i = 0; i < n.length(); i++) {
                if (!Character.isDigit(n.charAt(i))) return null;
            }
            return change;
        };
        tfSoTien.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), null, onlyDigits));
        tfGiamToiDa.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), null, onlyDigits));

        tfHeSo.setTextFormatter(new TextFormatter<String>((UnaryOperator<TextFormatter.Change>) change -> {
            String n = change.getControlNewText();
            if (n == null || n.isEmpty()) return change;
            if ("-".equals(n)) return change;
            if (".".equals(n) || "0.".equals(n)) return change;
            if (!n.matches("^-?\\d*(\\.\\d{0,4})?$")) return null;
            return change;
        }));
        tfHeSo.focusedProperty().addListener((obs, oldV, newV) -> {
            if (Boolean.FALSE.equals(newV)) {
                String raw = tfHeSo.getText();
                if (raw == null || raw.trim().isEmpty()) return;
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

        final var formKhongHopLe = tfTen.textProperty().isEmpty()
                .or(tfSoTien.textProperty().isEmpty())
                .or(tfHeSo.textProperty().isEmpty())
                .or(tfGiamToiDa.textProperty().isEmpty())
                .or(cbTrangThai.valueProperty().isNull())
                .or(dpNgayBatDau.valueProperty().isNull())
                .or(dpNgayKetThuc.valueProperty().isNull());

        btnXoa.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));

        EventHandler<ActionEvent> themMoiHandler = e -> {
            boolean success = false;
            try {
                int soTienToiThieu = Integer.parseInt(tfSoTien.getText().trim());
                if (soTienToiThieu < 0) {
                    hienThongBaoLoi("Giá trị không hợp lệ", "Số tiền áp dụng phải >= 0.");
                    return;
                }

                float heSo;
                try {
                    heSo = parseHeSoStrict(tfHeSo.getText());
                } catch (NumberFormatException ex) {
                    hienThongBaoLoi("Hệ số không hợp lệ",
                            "Hệ số phải là số thực > 0 và < 1 (ví dụ: 0.1, 0.25, 0.5).");
                    return;
                }

                if (dpNgayBatDau.getValue() != null && dpNgayKetThuc.getValue() != null
                        && dpNgayKetThuc.getValue().isBefore(dpNgayBatDau.getValue())) {
                    hienThongBaoLoi("Ngày không hợp lệ", "Ngày kết thúc phải >= ngày bắt đầu.");
                    return;
                }

                int giamToiDaInt = Integer.parseInt(tfGiamToiDa.getText().trim());
                if (giamToiDaInt < 0) {
                    hienThongBaoLoi("Giá trị không hợp lệ", "Tiền khuyến mãi tối đa phải >= 0.");
                    return;
                }

                boolean active = cbTrangThai.getValue() == TrangThai.DANG_HOAT_DONG;

                KhuyenMai entity = new KhuyenMai(
                        "",
                        tfTen.getText(),
                        dpNgayBatDau.getValue().atStartOfDay(),
                        dpNgayKetThuc.getValue().atStartOfDay(),
                        active,
                        heSo,
                        (float) soTienToiThieu,
                        (float) giamToiDaInt
                );

                // Thêm mới và lấy mã đã sinh
                String newId = kmController.addAndReturnId(entity);
                if (newId == null) {
                    hienThongBaoLoi("Không thể thêm", "Thêm mới thất bại.");
                    return;
                }
                entity.setMaKhuyenMai(newId); // gán mã cho entity để thao tác xóa/sửa sau này

                duLieuGoc.add(0, entity);
                apDungBoLoc();
                table.refresh();
                hienThongBao("Đã thêm", "Đã thêm khuyến mãi \"" + entity.getTenKhuyenMai() + "\"");
                success = true;
            } catch (NumberFormatException nfe) {
                hienThongBaoLoi("Giá trị không hợp lệ", "Hệ số / số tiền không đúng định dạng.");
            } catch (Exception ex) {
                hienThongBaoLoi("Lỗi", ex.getMessage());
            } finally {
                if (success) resetForm();
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

                int soTien = Integer.parseInt(tfSoTien.getText().trim());
                if (soTien < 0) {
                    hienThongBaoLoi("Giá trị không hợp lệ", "Số tiền áp dụng phải >= 0.");
                    return;
                }

                float heSo;
                try {
                    heSo = parseHeSoStrict(tfHeSo.getText());
                } catch (NumberFormatException ex) {
                    hienThongBaoLoi("Hệ số không hợp lệ",
                            "Hệ số phải là số thực > 0 và < 1 (ví dụ: 0.1, 0.25, 0.5).");
                    return;
                }

                if (dpNgayBatDau.getValue() != null && dpNgayKetThuc.getValue() != null
                        && dpNgayKetThuc.getValue().isBefore(dpNgayBatDau.getValue())) {
                    hienThongBaoLoi("Ngày không hợp lệ", "Ngày kết thúc phải >= ngày bắt đầu.");
                    return;
                }

                int giamToiDaInt = Integer.parseInt(tfGiamToiDa.getText().trim());
                if (giamToiDaInt < 0) {
                    hienThongBaoLoi("Giá trị không hợp lệ", "Tiền khuyến mãi tối đa phải >= 0.");
                    return;
                }

                km.setTenKhuyenMai(tfTen.getText());
                km.setNgayBatDau(dpNgayBatDau.getValue().atStartOfDay());
                km.setNgayKetThuc(dpNgayKetThuc.getValue().atStartOfDay());
                km.setTrangThai(cbTrangThai.getValue() == TrangThai.DANG_HOAT_DONG);
                km.setHeSo(heSo);
                km.settongTienToiThieu((float) soTien);
                km.settongKhuyenMaiToiDa((float) giamToiDaInt);

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
                if (success) resetForm();
            }
        };

        btnLuu.disableProperty().bind(formKhongHopLe);
        btnLuu.setOnAction(e -> {
            if (table.getSelectionModel().getSelectedItem() == null) {
                themMoiHandler.handle(e);
            } else {
                capNhatHandler.handle(e);
            }
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
                java.util.Optional<ButtonType> rs = xacNhan.showAndWait();
                if (!rs.isPresent() || rs.get() != ButtonType.OK) return;

                java.util.List<String> ids = new java.util.ArrayList<>();
                for (KhuyenMai p : chon) ids.add(p.getMaKhuyenMai());

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

        table.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends KhuyenMai> obs, KhuyenMai oldSel, KhuyenMai sel) -> {
                    if (sel == null) return;
                    tfTen.setText(sel.getTenKhuyenMai());
                    tfSoTien.setText(Integer.toString(Math.round(sel.getTongTienToiThieu())));
                    tfHeSo.setText(Float.toString(sel.getHeSo()));
                    tfGiamToiDa.setText(Integer.toString(Math.round(sel.getTongKhuyenMaiToiDa())));
                    dpNgayBatDau.setValue(sel.getNgayBatDau() == null ? null : sel.getNgayBatDau().toLocalDate());
                    dpNgayKetThuc.setValue(sel.getNgayKetThuc() == null ? null : sel.getNgayKetThuc().toLocalDate());
                    cbTrangThai.setValue(sel.isTrangThai() ? TrangThai.DANG_HOAT_DONG : TrangThai.KET_THUC);
                    tfTen.requestFocus();
                    tfTen.selectAll();
                });

        table.setRowFactory((TableView<KhuyenMai> tv) -> {
            final TableRow<KhuyenMai> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    KhuyenMai sel = row.getItem();
                    tfTen.setText(sel.getTenKhuyenMai());
                    tfSoTien.setText(Integer.toString(Math.round(sel.getTongTienToiThieu())));
                    tfHeSo.setText(Float.toString(sel.getHeSo()));
                    tfGiamToiDa.setText(Integer.toString(Math.round(sel.getTongKhuyenMaiToiDa())));
                    dpNgayBatDau.setValue(sel.getNgayBatDau() == null ? null : sel.getNgayBatDau().toLocalDate());
                    dpNgayKetThuc.setValue(sel.getNgayKetThuc() == null ? null : sel.getNgayKetThuc().toLocalDate());
                    cbTrangThai.setValue(sel.isTrangThai() ? TrangThai.DANG_HOAT_DONG : TrangThai.KET_THUC);
                    tfTen.requestFocus();
                    tfTen.selectAll();
                }
            });
            return row;
        });

        tfTim.textProperty().addListener((obs, o, n) -> apDungBoLoc());
        cbLocTrangThai.valueProperty().addListener((obs, o, n) -> apDungBoLoc());
        dpLocNgayBD.valueProperty().addListener((obs, o, n) -> apDungBoLoc());
        dpLocNgayKT.valueProperty().addListener((obs, o, n) -> apDungBoLoc());
    }

    private void resetBoLoc() {
        tfTim.clear();
        cbLocTrangThai.getSelectionModel().clearSelection();
        dpLocNgayBD.setValue(null);
        dpLocNgayKT.setValue(null);
    }

    private void apDungBoLoc() {
        final TrangThai st = cbLocTrangThai.getValue();
        final LocalDate from = dpLocNgayBD.getValue();
        final LocalDate to = dpLocNgayKT.getValue();
        final String tuKhoa = tfTim.getText() == null ? "" : tfTim.getText().trim().toLowerCase();

        duLieuLoc.setPredicate(new Predicate<KhuyenMai>() {
            @Override
            public boolean test(KhuyenMai p) {
                if (p == null) return false;

                boolean hopLeTrangThai = (st == null) || st == TrangThai.TAT_CA || tinhTrangThaiHienThi(p) == st;

                boolean hopLeNgay = true;
                if (from != null || to != null) {
                    LocalDate bd = p.getNgayBatDau() == null ? null : p.getNgayBatDau().toLocalDate();
                    LocalDate kt = p.getNgayKetThuc() == null ? null : p.getNgayKetThuc().toLocalDate();
                    LocalDate realBd = (bd == null) ? LocalDate.MIN : bd;
                    LocalDate realKt = (kt == null) ? LocalDate.MAX : kt;
                    LocalDate realFrom = (from == null) ? LocalDate.MIN : from;
                    LocalDate realTo   = (to == null)   ? LocalDate.MAX : to;
                    hopLeNgay = !realBd.isAfter(realTo) && !realKt.isBefore(realFrom);
                }

                boolean hopLeTuKhoa = true;
                if (!tuKhoa.isEmpty()) {
                    String ten = p.getTenKhuyenMai() == null ? "" : p.getTenKhuyenMai().toLowerCase();
                    hopLeTuKhoa = ten.contains(tuKhoa);
                }

                return hopLeTrangThai && hopLeNgay && hopLeTuKhoa;
            }
        });
    }

    private TrangThai tinhTrangThaiHienThi(KhuyenMai k) {
        LocalDate today = LocalDate.now();
        LocalDate kt = k.getNgayKetThuc() == null ? null : k.getNgayKetThuc().toLocalDate();


        if (kt != null && kt.isBefore(today)) {
            return TrangThai.KET_THUC;
        }
        // Còn lại coi là đang hoạt động
        return TrangThai.DANG_HOAT_DONG;
    }


    private float parseHeSoStrict(String raw) throws NumberFormatException {
        if (raw == null || raw.trim().isEmpty())
            throw new NumberFormatException("empty");
        float v = Float.parseFloat(raw.trim());
        if (v <= 0f || v >= 1f)
            throw new NumberFormatException("out_of_range");
        return v;
    }

    private static void dinhDangInput(TextField tf, String prompt) {
        tf.setPromptText(prompt);
        tf.setPrefWidth(340);
        tf.setStyle("-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:8 12;");
    }

    private static <T> void dinhDangCombo(ComboBox<T> cb, String prompt) {
        cb.setPromptText(prompt);
        cb.setPrefWidth(340);
        cb.setStyle("-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:4 10;");
    }

    private static void dinhDangDatePicker(DatePicker dp, String prompt) {
        dp.setPromptText(prompt);
        dp.setEditable(false);
        dp.setPrefWidth(165);
        dp.setStyle("-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:6 10;");
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
        box.setStyle("-fx-background-color:white; -fx-border-color:#e6e9ee; -fx-background-radius:20; -fx-border-radius:20;");
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
        cbTrangThai.getSelectionModel().clearSelection();   // fix đúng
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

    public enum TrangThai {
        TAT_CA("Tất cả", Color.web("#3b82f6")),
        KET_THUC("Kết thúc", Color.web("#ef4444")),
        DANG_HOAT_DONG("Đang hoạt động", Color.web("#10b981"));


        private final String hienThi;
        private final Color mau;

        TrangThai(String d, Color c) {
            hienThi = d;
            mau = c;
        }

        public String hienThi() { return hienThi; }
        public Color mau() { return mau; }

        public static StringConverter<TrangThai> converter() {
            return new StringConverter<TrangThai>() {
                @Override
                public String toString(TrangThai s) {
                    return s == null ? "" : s.hienThi();
                }

                @Override
                public TrangThai fromString(String s) {
                    for (TrangThai st : values())
                        if (Objects.equals(st.hienThi(), s))
                            return st;
                    return null;
                }
            };
        }
    }
}
