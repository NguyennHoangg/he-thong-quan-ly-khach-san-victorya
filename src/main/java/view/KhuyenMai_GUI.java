package view;

import controller.KhuyenMai_Controller;
import model.KhuyenMai;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class KhuyenMai_GUI extends BorderPane {

    private final KhuyenMai_Controller kmController = new KhuyenMai_Controller();


    private final TextField tfMa = new TextField();
    private final TextField tfTen = new TextField();
    private final TextField tfSoTien = new TextField();  // VND
    private final TextField tfHeSo  = new TextField();   // 0.1 (10%) hoặc 10%
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
            duLieuGoc.setAll(kmController.getAll());
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

        // Hàng 0: Mã | Trạng thái
        form.add(taoNhan("Mã khuyến mãi"), 0, r);
        dinhDangInput(tfMa, "Sẽ tự sinh khi thêm mới");
        tfMa.setEditable(false);
        form.add(tfMa, 0, r + 1);

        form.add(taoNhan("Trạng thái"), 1, r);
        dinhDangCombo(cbTrangThai, "Trạng thái");
        form.add(cbTrangThai, 1, r + 1);
        r += 2;

        // Hàng 2: Tên | Số tiền
        form.add(taoNhan("Tên khuyến mãi"), 0, r);
        dinhDangInput(tfTen, "Nhập tên khuyến mãi");
        form.add(tfTen, 0, r + 1);

        form.add(taoNhan("Số tiền áp dụng (VND)"), 1, r);
        dinhDangInput(tfSoTien, "Nhập số tiền áp dụng (nguyên)");
        form.add(tfSoTien, 1, r + 1);
        r += 2;

        // hàng 3
        form.add(taoNhan("Thời gian áp dụng"), 0, r);
        form.add(taoNhan("Hệ số (0.1 = 10%, cho phép '10%')"), 1, r);
        r++;

        // Trái: 2 DatePicker nằm trên 1 hàng
        HBox hopNgay = new HBox(8);
        hopNgay.setAlignment(Pos.CENTER_LEFT);
        dinhDangDatePicker(dpNgayBatDau, "Ngày bắt đầu");
        dinhDangDatePicker(dpNgayKetThuc, "Ngày kết thúc");
        Label den = new Label("—");
        den.setStyle("-fx-text-fill:#6b7280; -fx-opacity:0.9;");
        hopNgay.getChildren().addAll(dpNgayBatDau, den, dpNgayKetThuc);
        form.add(hopNgay, 0, r);

        // Phải: ô Hệ số gọn 150px
        HBox hopHeSo = new HBox();
        hopHeSo.setAlignment(Pos.CENTER_LEFT);
        dinhDangInput(tfHeSo, "Ví dụ: 0.1 hoặc 10%");
        tfHeSo.setPrefWidth(150);
        hopHeSo.getChildren().add(tfHeSo);
        form.add(hopHeSo, 1, r);
        r += 2;


        // Hàng nút
        btnLuu.setStyle("-fx-background-color:#155EEB; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");
        btnXoa.setStyle("-fx-background-color:#f44336; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");
        btnTaiLai.setStyle("-fx-background-color:#9e9e9e; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");

        btnLuu.textProperty().bind(
                Bindings.when(tfMa.textProperty().isEmpty())
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
        tfTim.setPromptText("Tên / Mã");
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
        cbTrangThai.setItems(FXCollections.observableArrayList(
                TrangThai.DANG_HOAT_DONG, TrangThai.KET_THUC, TrangThai.CHUA_BAT_DAU
        ));
        cbTrangThai.setConverter(TrangThai.converter());

        cbLocTrangThai.setItems(FXCollections.observableArrayList(TrangThai.values()));
        cbLocTrangThai.setConverter(TrangThai.converter());
    }

    private void khoiTaoBang() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<KhuyenMai, String> colMa = new TableColumn<>("Mã khuyến mãi");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maKhuyenMai"));

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
                setText(empty || item == null ? "" : pctVn.format(item)); // 0.1 -> 10%
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
        colTrangThai.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(tinhTrangThaiHienThi(cell.getValue()))
        );
        colTrangThai.setCellFactory(new Callback<TableColumn<KhuyenMai, TrangThai>, TableCell<KhuyenMai, TrangThai>>() {
            @Override
            public TableCell<KhuyenMai, TrangThai> call(TableColumn<KhuyenMai, TrangThai> tc) {
                return new TableCell<KhuyenMai, TrangThai>() {
                    @Override
                    protected void updateItem(TrangThai st, boolean empty) {
                        super.updateItem(st, empty);
                        if (empty || st == null) {
                            setGraphic(null);
                        } else {
                            setGraphic(vienChip(st.hienThi(), st.mau()));
                        }
                    }
                };
            }
        });

        // Đặt Hệ số ở bên phải cột Số tiền
        table.getColumns().addAll(colMa, colTen, colSoTien, colHeSo, colNgayBD, colNgayKT, colTrangThai);
        table.setItems(duLieuSapXep);
        duLieuSapXep.comparatorProperty().bind(table.comparatorProperty());
    }

    private void khoiTaoSuKien() {
        // Chỉ số nguyên cho Số tiền
        tfSoTien.setTextFormatter(new TextFormatter<Integer>(
                new IntegerStringConverter(),
                null,
                new UnaryOperator<TextFormatter.Change>() {
                    @Override
                    public TextFormatter.Change apply(TextFormatter.Change change) {
                        String n = change.getControlNewText();
                        if (n == null || n.isEmpty()) return change;
                        for (int i = 0; i < n.length(); i++) {
                            if (!Character.isDigit(n.charAt(i))) return null;
                        }
                        return change;
                    }
                }));

        // Số thập phân cho Hệ số (cho phép có dấu % ở cuối)
        tfHeSo.setTextFormatter(new TextFormatter<String>(
                change -> {
                    String n = change.getControlNewText();
                    if (n == null || n.isEmpty()) return change;
                    if (!n.matches("\\d*(\\.?\\d{0,4})?%?")) return null;
                    return change;
                }));

        // RÀNG BUỘC form hợp lệ
        final var formKhongHopLe = tfTen.textProperty().isEmpty()
                .or(tfSoTien.textProperty().isEmpty())
                .or(tfHeSo.textProperty().isEmpty())
                .or(cbTrangThai.valueProperty().isNull())
                .or(dpNgayBatDau.valueProperty().isNull())
                .or(dpNgayKetThuc.valueProperty().isNull());

        // Xóa chỉ bật khi có chọn
        btnXoa.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));

        EventHandler<ActionEvent> themMoiHandler = e -> {
            try {
                // 1) Đọc & kiểm tra input
                int soTienToiThieu = Integer.parseInt(tfSoTien.getText().trim()); // tiền tối thiểu
                if (soTienToiThieu < 0) {
                    hienThongBao("Giá trị không hợp lệ", "Số tiền tối thiểu phải >= 0.");
                    return;
                }

                float heSo = parseHeSo(tfHeSo.getText());           // cho phép "0.1", "10", "10%"
                if (heSo < 0f || heSo > 1f) {
                    hienThongBao("Giá trị không hợp lệ", "Hệ số phải nằm trong [0, 1].");
                    return;
                }

                if (dpNgayBatDau.getValue() != null && dpNgayKetThuc.getValue() != null
                        && dpNgayKetThuc.getValue().isBefore(dpNgayBatDau.getValue())) {
                    hienThongBao("Ngày không hợp lệ", "Ngày kết thúc phải >= ngày bắt đầu.");
                    return;
                }

                boolean active = cbTrangThai.getValue() == TrangThai.DANG_HOAT_DONG;

                // 2) Tính tổng KM tối đa ngay trên client (DAO cũng sẽ tính lại để đảm bảo nhất quán)
                float giamToiDa = soTienToiThieu * heSo;

                // 3) Tạo entity: (ma, ten, ngayBD, ngayKT, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa)
                KhuyenMai entity = new KhuyenMai(
                        "",
                        tfTen.getText(),
                        dpNgayBatDau.getValue().atStartOfDay(),
                        dpNgayKetThuc.getValue().atStartOfDay(),
                        active,
                        heSo,
                        (float) soTienToiThieu,   // dơn tối thiểu
                        giamToiDa                 // tổng KM tối đa = tối thiểu × hệ số
                );

                // 4) Gọi controller (DAO sẽ tự sinh mã KM-xxx và tính lại giamToiDa bằng BigDecimal)
                boolean ok = kmController.add(entity);
                if (!ok) {
                    hienThongBao("Không thể thêm", "Thêm mới thất bại.");
                    return;
                }


                duLieuGoc.add(0, entity);   // entity đã có mã mới do DAO set
                apDungBoLoc();
                table.refresh();
                hienThongBao("Đã thêm", "Đã thêm khuyến mãi mã " + entity.getMaKhuyenMai());

            } catch (NumberFormatException nfe) {
                hienThongBao("Giá trị không hợp lệ", "Hệ số hoặc số tiền không đúng định dạng.");
            } catch (Exception ex) {
                hienThongBao("Lỗi", ex.getMessage());
            } finally {
                resetForm();
            }
        };


        EventHandler<ActionEvent> capNhatHandler = e -> {
            try {
                KhuyenMai sel = table.getSelectionModel().getSelectedItem();
                if (sel == null) {
                    String ma = tfMa.getText();
                    if (ma == null || ma.isBlank()) {
                        hienThongBao("Thiếu mã", "Vui lòng chọn một khuyến mãi cần cập nhật từ bảng.");
                        return;
                    }
                    for (KhuyenMai k : duLieuGoc) {
                        if (ma.equals(k.getMaKhuyenMai())) { sel = k; break; }
                    }
                    if (sel == null) {
                        hienThongBao("Không tìm thấy", "Bản ghi với mã " + ma + " không tồn tại trong danh sách.");
                        return;
                    }
                }

                int soTien = Integer.parseInt(tfSoTien.getText().trim());
                float heSo = parseHeSo(tfHeSo.getText());
                if (dpNgayBatDau.getValue() != null && dpNgayKetThuc.getValue() != null
                        && dpNgayKetThuc.getValue().isBefore(dpNgayBatDau.getValue())) {
                    hienThongBao("Ngày không hợp lệ", "Ngày kết thúc phải >= ngày bắt đầu.");
                    return;
                }

                sel.setTenKhuyenMai(tfTen.getText());
                sel.setNgayBatDau(dpNgayBatDau.getValue().atStartOfDay());
                sel.setNgayKetThuc(dpNgayKetThuc.getValue().atStartOfDay());
                sel.setTrangThai(cbTrangThai.getValue() == TrangThai.DANG_HOAT_DONG);
                sel.setHeSo(heSo);
                sel.settongTienToiThieu((float) soTien);
                sel.settongKhuyenMaiToiDa(soTien * heSo);


                boolean updated = kmController.update(sel);
                if (!updated) {
                    hienThongBao("Không thể cập nhật", "Không có bản ghi nào được cập nhật.");
                    return;
                }

                table.refresh();
                apDungBoLoc();
                hienThongBao("Đã cập nhật", "Cập nhật khuyến mãi mã " + sel.getMaKhuyenMai() + " thành công.");
            } catch (NumberFormatException nfe) {
                hienThongBao("Giá trị không hợp lệ", "Hệ số hoặc số tiền không đúng định dạng.");
            } catch (Exception ex) {
                hienThongBao("Lỗi", ex.getMessage());
            } finally {
                resetForm();
            }
        };

        btnLuu.disableProperty().bind(formKhongHopLe);
        btnLuu.setOnAction(e -> {
            String ma = tfMa.getText();
            if (ma == null || ma.isBlank()) {
                themMoiHandler.handle(e);
            } else {
                capNhatHandler.handle(e);
            }
        });

        btnTaiLai.setOnAction(e -> {
            try {
                taiDuLieu();
                apDungBoLoc();
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
                    hienThongBao("Không thể xóa", "Không xóa được bản ghi nào.");
                    return;
                }
                duLieuGoc.removeAll(new java.util.ArrayList<>(chon));
                hienThongBao("Đã xóa", "Đã xóa " + deleted + " bản ghi.");
            } finally {
                resetForm();
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<KhuyenMai>() {
            @Override
            public void changed(ObservableValue<? extends KhuyenMai> obs, KhuyenMai oldSel, KhuyenMai sel) {
                if (sel == null) return;
                tfMa.setText(sel.getMaKhuyenMai());
                tfTen.setText(sel.getTenKhuyenMai());
                tfSoTien.setText(Integer.toString(Math.round(sel.getTongTienToiThieu())));
                tfHeSo.setText(Float.toString(sel.getHeSo()));
                dpNgayBatDau.setValue(sel.getNgayBatDau() == null ? null : sel.getNgayBatDau().toLocalDate());
                dpNgayKetThuc.setValue(sel.getNgayKetThuc() == null ? null : sel.getNgayKetThuc().toLocalDate());
                cbTrangThai.setValue(sel.isTrangThai() ? TrangThai.DANG_HOAT_DONG : TrangThai.KET_THUC);
                tfTen.requestFocus();
                tfTen.selectAll();
            }
        });

        table.setRowFactory(new Callback<TableView<KhuyenMai>, TableRow<KhuyenMai>>() {
            @Override
            public TableRow<KhuyenMai> call(TableView<KhuyenMai> tv) {
                final TableRow<KhuyenMai> row = new TableRow<>();
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2 && !row.isEmpty()) {
                            KhuyenMai sel = row.getItem();
                            tfMa.setText(sel.getMaKhuyenMai());
                            tfTen.setText(sel.getTenKhuyenMai());
                            tfSoTien.setText(Integer.toString(Math.round(sel.getTongTienToiThieu())));
                            tfHeSo.setText(Float.toString(sel.getHeSo()));
                            dpNgayBatDau.setValue(sel.getNgayBatDau() == null ? null : sel.getNgayBatDau().toLocalDate());
                            dpNgayKetThuc.setValue(sel.getNgayKetThuc() == null ? null : sel.getNgayKetThuc().toLocalDate());
                            cbTrangThai.setValue(sel.isTrangThai() ? TrangThai.DANG_HOAT_DONG : TrangThai.KET_THUC);
                            tfTen.requestFocus();
                            tfTen.selectAll();
                        }
                    }
                });
                return row;
            }
        });

        tfTim.textProperty().addListener((obs, o, n) -> apDungBoLoc());
        cbLocTrangThai.valueProperty().addListener((obs, o, n) -> apDungBoLoc());
        dpLocNgayBD.valueProperty().addListener((obs, o, n) -> apDungBoLoc());
        dpLocNgayKT.valueProperty().addListener((obs, o, n) -> apDungBoLoc());
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

                // Lọc theo khoảng có giao nhau
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
                    String ma = p.getMaKhuyenMai() == null ? "" : p.getMaKhuyenMai().toLowerCase();
                    String ten = p.getTenKhuyenMai() == null ? "" : p.getTenKhuyenMai().toLowerCase();
                    hopLeTuKhoa = ma.contains(tuKhoa) || ten.contains(tuKhoa);
                }

                return hopLeTrangThai && hopLeNgay && hopLeTuKhoa;
            }
        });
    }

    // Tính trạng thái hiển thị dựa trên ngày & cờ boolean
    private TrangThai tinhTrangThaiHienThi(KhuyenMai k) {
        LocalDate today = LocalDate.now();
        LocalDate bd = k.getNgayBatDau() == null ? null : k.getNgayBatDau().toLocalDate();
        LocalDate kt = k.getNgayKetThuc() == null ? null : k.getNgayKetThuc().toLocalDate();
        if (bd != null && bd.isAfter(today)) return TrangThai.CHUA_BAT_DAU;
        if (kt != null && kt.isBefore(today)) return TrangThai.KET_THUC;
        return k.isTrangThai() ? TrangThai.DANG_HOAT_DONG : TrangThai.KET_THUC;
    }

    // Parse hệ số: cho phép "0.1" hoặc "10%" hoặc "10"
    private float parseHeSo(String raw) throws NumberFormatException {
        String s = raw == null ? "" : raw.trim().replace("%", "");
        if (s.isEmpty()) throw new NumberFormatException("empty");
        float v = Float.parseFloat(s);
        if (v > 1f) v = v / 100f;  // 10 -> 0.10
        if (v < 0f) v = 0f;
        return v;
    }

    // ====== style helpers ======
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
        dp.setPrefWidth(165); // nhỏ gọn hơn để nằm cùng hàng
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

    // Xoá sạch form + bỏ chọn bảng
    private void resetForm() {
        tfMa.clear();
        tfTen.clear();
        tfSoTien.clear();
        tfHeSo.clear();
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

    private String dinhDangNgay(LocalDate d) {
        return d == null ? "-" : d.format(fmtDMY);
    }

    public enum TrangThai {
        TAT_CA("Tất cả", Color.web("#3b82f6")),
        CHUA_BAT_DAU("Chưa bắt đầu", Color.web("#3b82f6")),
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
