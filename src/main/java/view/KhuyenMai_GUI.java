package view;

import controller.KhuyenMai_Controller;
import model.KhuyenMai;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableRow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class KhuyenMai_GUI extends BorderPane {

    // ===== Controller =====
    private final KhuyenMai_Controller kmController = new KhuyenMai_Controller();

    // ===== Controls (Header) =====
    private final TextField tfSearchHeader = new TextField();

    // ===== Form =====
    private final TextField tfMa = new TextField();               // Mã (read-only, DB tự sinh)
    private final TextField tfTen = new TextField();
    private final TextField tfSoTien = new TextField(); // map từ soTienDuocGiamToiDa
    private final ComboBox<Status> cbTrangThai = new ComboBox<>();
    private final DatePicker dpNgayBatDau = new DatePicker();
    private final DatePicker dpNgayKetThuc = new DatePicker();

    // Nhóm nút hành động
    private final Button btnThemMoi = new Button("Thêm mới");     // clear form
    private final Button btnCapNhat = new Button("Cập nhật");     // upsert
    private final Button btnXoa = new Button("Xóa đã chọn");      // xóa nhiều
    private final Button btnTaiLai = new Button("Tải lại");       // reload
    private final Button btnLuu = new Button("Lưu");              // alias cập nhật

    // ===== Filter bar =====
    private final TextField tfSearchFilter = new TextField();
    private final ComboBox<Status> cbFilterTrangThai = new ComboBox<>();
    private final DatePicker dpFilterNgayBD = new DatePicker();
    private final DatePicker dpFilterNgayKT = new DatePicker();

    // ===== Table & Data =====
    private final TableView<Promotion> table = new TableView<>();
    private final ObservableList<Promotion> masterData = FXCollections.observableArrayList();
    private final FilteredList<Promotion> filtered = new FilteredList<>(
            masterData,
            new Predicate<Promotion>() {
                @Override
                public boolean test(Promotion promotion) {
                    return true;
                }
            }
    );
    private final SortedList<Promotion> sorted = new SortedList<>(filtered);

    private final DateTimeFormatter dmy = DateTimeFormatter.ofPattern("d/M/yy");

    public KhuyenMai_GUI() {
        setPadding(new Insets(16, 24, 24, 24));
        setTop(buildTop());
        setCenter(buildCenter());
        initCombos();
        initTable();
        initActions();
        loadData();
    }

    // ===================== LIFECYCLE =====================
    private void loadData() {
        try {
            java.util.List<KhuyenMai> list = kmController.getAll();
            java.util.List<Promotion> ui = new java.util.ArrayList<>();
            for (KhuyenMai km : list) {
                ui.add(mapToPromotion(km)); // DB -> UI
            }
            masterData.setAll(ui);
            applyFilters();
        } catch (Exception ex) {
            masterData.clear();
            seedSampleData(); // fallback để UI vẫn chạy
            applyFilters();
            alert("Không thể tải dữ liệu từ SQL Server",
                    "Lỗi: " + ex.getMessage() + "\nĐang hiển thị dữ liệu mẫu.");
        }
    }

    private Node buildTop() {
        tfSearchHeader.setPromptText("Search for rooms and offers");
        tfSearchHeader.setPrefWidth(420);
        tfSearchHeader.setStyle(
                "-fx-background-color:#f7fafc; -fx-background-radius:8; -fx-border-radius:8; -fx-padding:8 12;");

        HBox header = new HBox(wrapWithIcon("\uD83D\uDD0D", tfSearchHeader));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 12, 0));

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

        // Row 0: Mã (trái) | Trạng thái (phải)
        form.add(label("Mã khuyến mãi"), 0, r);
        styleInput(tfMa, "Sẽ tự sinh khi lưu mới");
        tfMa.setEditable(false);
        form.add(tfMa, 0, r + 1);

        form.add(label("Trạng thái"), 1, r);
        styleCombo(cbTrangThai, "Trạng thái");
        form.add(cbTrangThai, 1, r + 1);

        r += 2;

        // Row 2: Tên (trái) | Số tiền (phải)
        form.add(label("Tên khuyến mãi"), 0, r);
        styleInput(tfTen, "Nhập tên khuyến mãi");
        form.add(tfTen, 0, r + 1);

        form.add(label("Số tiền áp dụng"), 1, r);
        styleInput(tfSoTien, "Nhập số tiền áp dụng");
        form.add(tfSoTien, 1, r + 1);

        r += 2;

        // Row 4: Thời gian (gộp 2 cột)
        form.add(label("Thời gian áp dụng"), 0, r, 2, 1);

        HBox dateBox = new HBox(12);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        styleDate(dpNgayBatDau, "Ngày bắt đầu");
        styleDate(dpNgayKetThuc, "Ngày kết thúc");

        Label den = new Label("—");
        den.setStyle("-fx-text-fill:#6b7280; -fx-opacity:0.9;");

        dateBox.getChildren().addAll(dpNgayBatDau, den, dpNgayKetThuc);
        form.add(dateBox, 0, r + 1, 2, 1);

        r += 2;

        // Row 6: Nhóm nút (gộp 2 cột, canh phải)
        btnThemMoi.setStyle("-fx-background-color:#64748b; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 12;");
        btnCapNhat.setStyle("-fx-background-color:#155EEB; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 16;");
        btnXoa.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 12;");
        btnTaiLai.setStyle("-fx-background-color:#0ea5e9; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 12;");
        btnLuu.setStyle("-fx-background-color:#155EEB; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 16;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10, spacer, btnThemMoi, btnCapNhat, btnXoa, btnTaiLai);
        actions.setAlignment(Pos.CENTER_RIGHT);

        GridPane.setMargin(actions, new Insets(10, 0, 20, 0));
        form.add(actions, 0, r, 2, 1);

        VBox top = new VBox(header, form);
        top.setSpacing(16);
        return top;
    }

    private Node buildCenter() {
        tfSearchFilter.setPromptText("Tên");
        tfSearchFilter.setPrefWidth(230);

        styleComboPill(cbFilterTrangThai, "Trạng thái");
        styleDatePill(dpFilterNgayBD, "Ngày bắt đầu");
        styleDatePill(dpFilterNgayKT, "Ngày kết thúc");

        HBox filters = new HBox(
                pill(wrapWithIcon("\uD83D\uDD0D", tfSearchFilter)),
                pill(cbFilterTrangThai),
                pill(wrapWithIcon("📅", dpFilterNgayBD)),
                pill(wrapWithIcon("📅", dpFilterNgayKT)));
        filters.setSpacing(10);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(14));
        filters.setStyle(
                "-fx-background-color:#f7fafc; -fx-background-radius:10; -fx-border-color:#e8edf3; -fx-border-radius:10;");

        VBox center = new VBox(filters, table);
        center.setSpacing(10);
        VBox.setVgrow(table, Priority.ALWAYS);
        return center;
    }

    // ===================== INIT =====================
    private void initCombos() {
        cbTrangThai.setItems(FXCollections.observableArrayList(Status.values()));
        cbFilterTrangThai.setItems(FXCollections.observableArrayList(Status.values()));
        cbTrangThai.setConverter(Status.converter());
        cbFilterTrangThai.setConverter(Status.converter());
    }

    private void initTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<Promotion, String> colMa = new TableColumn<>("Mã khuyến mãi");
        colMa.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Promotion, String> colTen = new TableColumn<>("Tên");
        colTen.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Promotion, Integer> colSoTien = new TableColumn<>("Số tiền áp dụng");
        colSoTien.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Promotion, String> colNgayBD = new TableColumn<>("Ngày bắt đầu");
        colNgayBD.setCellValueFactory(new Callback<CellDataFeatures<Promotion, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Promotion, String> cell) {
                return new ReadOnlyStringWrapper(formatDate(cell.getValue().getStartDate()));
            }
        });

        TableColumn<Promotion, String> colNgayKT = new TableColumn<>("Ngày kết thúc");
        colNgayKT.setCellValueFactory(new Callback<CellDataFeatures<Promotion, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Promotion, String> cell) {
                return new ReadOnlyStringWrapper(formatDate(cell.getValue().getEndDate()));
            }
        });

        TableColumn<Promotion, Status> colTrangThai = new TableColumn<>("Trạng thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTrangThai.setCellFactory(new Callback<TableColumn<Promotion, Status>, TableCell<Promotion, Status>>() {
            @Override
            public TableCell<Promotion, Status> call(TableColumn<Promotion, Status> tc) {
                return new TableCell<Promotion, Status>() {
                    @Override
                    protected void updateItem(Status st, boolean empty) {
                        super.updateItem(st, empty);
                        if (empty || st == null) {
                            setGraphic(null);
                        } else {
                            setGraphic(chip(st.display(), st.color()));
                        }
                    }
                };
            }
        });

        table.getColumns().add(colMa);
        table.getColumns().add(colTen);
        table.getColumns().add(colSoTien);
        table.getColumns().add(colNgayBD);
        table.getColumns().add(colNgayKT);
        table.getColumns().add(colTrangThai);

        table.setItems(sorted);
        sorted.comparatorProperty().bind(table.comparatorProperty());
    }

    private void initActions() {
        // Chỉ cho nhập số nguyên vào tfSoTien
        tfSoTien.setTextFormatter(new TextFormatter<Integer>(
                new IntegerStringConverter(),
                null,
                new UnaryOperator<TextFormatter.Change>() {
                    @Override
                    public TextFormatter.Change apply(TextFormatter.Change change) {
                        String n = change.getControlNewText();
                        if (n == null || n.isEmpty()) return change;
                        int len = n.length();
                        for (int i = 0; i < len; i++) {
                            if (!Character.isDigit(n.charAt(i))) return null;
                        }
                        return change;
                    }
                }
        ));

        // Disable nút Cập nhật khi thiếu dữ liệu bắt buộc
        btnCapNhat.disableProperty().bind(
                tfTen.textProperty().isEmpty()
                        .or(tfSoTien.textProperty().isEmpty())
                        .or(cbTrangThai.valueProperty().isNull())
                        .or(dpNgayBatDau.valueProperty().isNull())
                        .or(dpNgayKetThuc.valueProperty().isNull())
        );

        // Disable nút Xóa khi không chọn gì
        btnXoa.disableProperty().bind(
                Bindings.isEmpty(table.getSelectionModel().getSelectedItems())
        );

        // Nút Lưu cũ → gọi chung hành vi với Cập nhật
        btnLuu.disableProperty().bind(btnCapNhat.disableProperty());

        // ======= Handlers KHÔNG dùng lambda =======

        // Thêm mới (clear form)
        btnThemMoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearForm();
                tfMa.clear();
                tfTen.requestFocus();
            }
        });

        // Tải lại
        btnTaiLai.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadData();
                alert("Đã tải lại", "Danh sách khuyến mãi đã được tải lại từ CSDL.");
            }
        });

        // Upsert
        EventHandler<ActionEvent> upsertHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    int amount = Integer.parseInt(tfSoTien.getText().trim());

                    if (dpNgayBatDau.getValue() != null && dpNgayKetThuc.getValue() != null
                            && dpNgayKetThuc.getValue().isBefore(dpNgayBatDau.getValue())) {
                        alert("Ngày không hợp lệ", "Ngày kết thúc phải >= ngày bắt đầu.");
                        return;
                    }

                    String code = tfMa.getText();
                    Promotion pTemp = new Promotion(
                            code == null ? "" : code.trim(),
                            tfTen.getText(),
                            amount,
                            dpNgayBatDau.getValue(),
                            dpNgayKetThuc.getValue(),
                            cbTrangThai.getValue()
                    );
                    KhuyenMai entity = mapToEntity(pTemp);

                    if (code == null || code.trim().isEmpty()) {
                        // THÊM MỚI: DB tự sinh mã
                        String newId = kmController.addReturningId(entity);
                        if (newId == null || newId.trim().isEmpty()) {
                            alert("Không thể thêm", "Thêm mới thất bại (không nhận được mã).");
                            return;
                        }
                        tfMa.setText(newId);

                        Promotion p = new Promotion(
                                newId,
                                pTemp.getName(),
                                pTemp.getAmount(),
                                pTemp.getStartDate(),
                                pTemp.getEndDate(),
                                pTemp.getStatus()
                        );
                        masterData.add(0, p);
                        alert("Đã thêm mới", "Thêm khuyến mãi mã " + newId + " thành công.");
                    } else {
                        // CẬP NHẬT
                        boolean ok = kmController.update(entity);
                        if (!ok) {
                            alert("Không thể cập nhật", "Cập nhật thất bại (0 dòng bị ảnh hưởng).");
                            return;
                        }
                        Promotion toReplace = findByCodeInUI(code.trim());
                        Promotion p = new Promotion(
                                code.trim(),
                                pTemp.getName(),
                                pTemp.getAmount(),
                                pTemp.getStartDate(),
                                pTemp.getEndDate(),
                                pTemp.getStatus()
                        );
                        if (toReplace != null) {
                            masterData.set(masterData.indexOf(toReplace), p);
                        } else {
                            masterData.add(0, p);
                        }
                        alert("Đã cập nhật", "Cập nhật khuyến mãi mã " + code + " thành công.");
                    }

                    clearForm();
                    table.getSelectionModel().clearSelection();

                } catch (NumberFormatException nfe) {
                    alert("Số tiền không hợp lệ", "Vui lòng nhập số nguyên.");
                } catch (Exception ex) {
                    alert("Lỗi", ex.getMessage());
                }
            }
        };
        btnCapNhat.setOnAction(upsertHandler);
        btnLuu.setOnAction(upsertHandler);

        // Xóa nhiều
        btnXoa.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                ObservableList<Promotion> sel = table.getSelectionModel().getSelectedItems();
                if (sel == null || sel.isEmpty()) {
                    alert("Chưa chọn", "Hãy chọn ít nhất 1 dòng để xóa.");
                    return;
                }

                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Xác nhận xóa");
                confirm.setHeaderText("Xóa " + sel.size() + " khuyến mãi?");
                confirm.setContentText("Hành động này không thể hoàn tác.");
                java.util.Optional<ButtonType> rs = confirm.showAndWait();
                if (!rs.isPresent() || rs.get() != ButtonType.OK) return;

                java.util.List<String> ids = new java.util.ArrayList<String>();
                for (Promotion p : sel) ids.add(p.getCode());

                int deleted = kmController.deleteMany(ids);
                if (deleted <= 0) {
                    alert("Không thể xóa", "Không xóa được bản ghi nào.");
                    return;
                }
                masterData.removeAll(new java.util.ArrayList<Promotion>(sel));
                alert("Đã xóa", "Đã xóa " + deleted + " bản ghi.");
            }
        });

        // ======= Listeners KHÔNG dùng lambda =======

        // Search header
        tfSearchHeader.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String o, String n) {
                applyFilters();
            }
        });

        // Filter bar
        tfSearchFilter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String o, String n) {
                applyFilters();
            }
        });

        cbFilterTrangThai.valueProperty().addListener(new ChangeListener<Status>() {
            @Override
            public void changed(ObservableValue<? extends Status> obs, Status o, Status n) {
                applyFilters();
            }
        });

        dpFilterNgayBD.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> obs, LocalDate o, LocalDate n) {
                applyFilters();
            }
        });

        dpFilterNgayKT.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> obs, LocalDate o, LocalDate n) {
                applyFilters();
            }
        });

        // Chọn dòng -> đổ form
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Promotion>() {
            @Override
            public void changed(ObservableValue<? extends Promotion> obs, Promotion oldSel, Promotion sel) {
                if (sel == null) return;
                tfMa.setText(sel.getCode());
                tfTen.setText(sel.getName());
                tfSoTien.setText(Integer.toString(sel.getAmount()));
                dpNgayBatDau.setValue(sel.getStartDate());
                dpNgayKetThuc.setValue(sel.getEndDate());
                cbTrangThai.setValue(sel.getStatus());
            }
        });

        // Double-click hàng để đẩy lên form
        table.setRowFactory(new Callback<TableView<Promotion>, TableRow<Promotion>>() {
            @Override
            public TableRow<Promotion> call(TableView<Promotion> tv) {
                final TableRow<Promotion> row = new TableRow<>();
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2 && !row.isEmpty()) {
                            Promotion sel = row.getItem();
                            tfMa.setText(sel.getCode());
                            tfTen.setText(sel.getName());
                            tfSoTien.setText(Integer.toString(sel.getAmount()));
                            dpNgayBatDau.setValue(sel.getStartDate());
                            dpNgayKetThuc.setValue(sel.getEndDate());
                            cbTrangThai.setValue(sel.getStatus());
                        }
                    }
                });
                return row;
            }
        });
    }

    // ===================== FILTERING =====================
    private void applyFilters() {
        String kwForm = tfSearchFilter.getText() == null ? "" : tfSearchFilter.getText().trim().toLowerCase();
        String kwHeader = tfSearchHeader.getText() == null ? "" : tfSearchHeader.getText().trim().toLowerCase();
        String kw = kwForm;
        if (!kwHeader.isEmpty()) {
            kw = kw.isEmpty() ? kwHeader : (kw + " " + kwHeader);
        }

        Status st = cbFilterTrangThai.getValue();
        LocalDate from = dpFilterNgayBD.getValue();
        LocalDate to = dpFilterNgayKT.getValue();

        final String fKw = kw;
        final Status fSt = st;
        final LocalDate fFrom = from;
        final LocalDate fTo = to;

        filtered.setPredicate(new Predicate<Promotion>() {
            @Override
            public boolean test(Promotion p) {
                if (p == null) return false;

                boolean okKw = true;
                if (fKw != null && !fKw.isEmpty()) {
                    String lowerCode = p.getCode() == null ? "" : p.getCode().toLowerCase();
                    String lowerName = p.getName() == null ? "" : p.getName().toLowerCase();
                    okKw = lowerCode.contains(fKw) || lowerName.contains(fKw);
                }

                boolean okSt = fSt == null || p.getStatus() == fSt;

                boolean okDate = true;
                if (fFrom != null && p.getStartDate() != null) {
                    okDate = okDate && !p.getStartDate().isBefore(fFrom);
                }
                if (fTo != null && p.getEndDate() != null) {
                    okDate = okDate && !p.getEndDate().isAfter(fTo);
                }

                return okKw && okSt && okDate;
            }
        });
    }

    // ===================== MAP DB <-> UI =====================
    private Promotion mapToPromotion(KhuyenMai km) {
        String code = km.getMaKhuyenMai();
        String name = km.getTenKhuyenMai();
        int amount = Math.round(km.gettongKhuyenMaiToiDa());
        LocalDate start = km.getNgayBatDau() == null ? null : km.getNgayBatDau().toLocalDate();
        LocalDate end   = km.getNgayKetThuc() == null ? null : km.getNgayKetThuc().toLocalDate();
        Status st = km.isTrangThai() ? Status.ACTIVE : Status.ENDED;
        return new Promotion(code, name, amount, start, end, st);
    }

    private KhuyenMai mapToEntity(Promotion p) {
        LocalDateTime s = p.getStartDate() == null ? null : p.getStartDate().atStartOfDay();
        LocalDateTime e = p.getEndDate()   == null ? null : p.getEndDate().atStartOfDay();
        boolean trangThai = p.getStatus() == Status.ACTIVE;
        float heSo = 0f;
        float soTienToiThieu = 0f;
        float soTienGiamToiDa = (float) p.getAmount();
        return new KhuyenMai(
                p.getCode(),
                p.getName(),
                s,
                e,
                trangThai,
                heSo,
                soTienToiThieu,
                soTienGiamToiDa
        );
    }

    // ===================== DỮ LIỆU MẪU =====================
    private void seedSampleData() {
        masterData.addAll(
                new Promotion("#5644", "Family deal", 10,
                        LocalDate.of(2023, 3, 2), LocalDate.of(2023, 3, 21),
                        Status.NOT_STARTED),
                new Promotion("#6112", "Christmas deal", 12,
                        LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 25),
                        Status.ENDED),
                new Promotion("#6141", "Launch promo", 15,
                        LocalDate.of(2023, 3, 5), null,
                        Status.NOT_STARTED),
                new Promotion("#6535", "Black Friday", 10,
                        LocalDate.of(2023, 4, 15), LocalDate.of(2023, 5, 1),
                        Status.ACTIVE)
        );
    }

    // ===================== UI HELPERS =====================
    private static void styleInput(TextField tf, String prompt) {
        tf.setPromptText(prompt);
        tf.setPrefWidth(340);
        tf.setStyle(
                "-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:8 12;");
    }

    private static <T> void styleCombo(ComboBox<T> cb, String prompt) {
        cb.setPromptText(prompt);
        cb.setPrefWidth(340);
        cb.setStyle(
                "-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:4 10;");
    }

    private static void styleDate(DatePicker dp, String prompt) {
        dp.setPromptText(prompt);
        dp.setEditable(false);
        dp.setPrefWidth(340);
        dp.setStyle(
                "-fx-background-color:white; -fx-background-radius:8; -fx-border-color:#e6e9ee; -fx-border-radius:8; -fx-padding:6 10;");
    }

    private static <T> void styleComboPill(ComboBox<T> cb, String prompt) {
        cb.setPromptText(prompt);
        cb.setPrefWidth(140);
        cb.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");
    }

    private static void styleDatePill(DatePicker dp, String prompt) {
        dp.setPromptText(prompt);
        dp.setEditable(false);
        dp.setPrefWidth(140);
        dp.setStyle("-fx-background-color:transparent; -fx-border-color:transparent; -fx-padding:2 6;");
    }

    private static Node pill(Node inner) {
        HBox box = new HBox(inner);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle(
                "-fx-background-color:white; -fx-border-color:#e6e9ee; -fx-background-radius:20; -fx-border-radius:20;");
        return box;
    }

    private static Label label(String text) {
        Label lb = new Label(text);
        lb.setStyle("-fx-font-size:12px; -fx-text-fill:#5b667a;");
        return lb;
    }

    private static HBox wrapWithIcon(String icon, Node node) {
        Label lb = new Label(icon);
        lb.setStyle("-fx-opacity:0.8;");
        HBox h = new HBox(lb, node);
        h.setSpacing(6);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private static StackPane chip(String text, Color color) {
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

    private void clearForm() {
        tfTen.clear();
        tfSoTien.clear();
        cbTrangThai.setValue(null);
        dpNgayBatDau.setValue(null);
        dpNgayKetThuc.setValue(null);
        // Không xóa tfMa ở đây; btnThemMoi sẽ clear để phân biệt thêm mới/cập nhật
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String formatDate(LocalDate d) {
        return d == null ? "-" : d.format(dmy);
    }

    private Promotion findByCodeInUI(String code) {
        if (code == null) return null;
        for (Promotion p : masterData) {
            if (code.equals(p.getCode())) return p;
        }
        return null;
    }

    // ===================== MODELS (UI) =====================
    public enum Status {
        NOT_STARTED("Chưa bắt đầu", Color.web("#3b82f6")),
        ENDED("Kết thúc", Color.web("#ef4444")),
        ACTIVE("Đang hoạt động", Color.web("#10b981"));

        private final String display;
        private final Color color;

        Status(String d, Color c) {
            display = d;
            color = c;
        }

        public String display() {
            return display;
        }

        public Color color() {
            return color;
        }

        public static StringConverter<Status> converter() {
            return new StringConverter<Status>() {
                @Override
                public String toString(Status s) {
                    return s == null ? "" : s.display();
                }

                @Override
                public Status fromString(String s) {
                    for (Status st : values())
                        if (Objects.equals(st.display(), s))
                            return st;
                    return null;
                }
            };
        }
    }

    public static class Promotion {
        private final StringProperty code = new SimpleStringProperty();
        private final StringProperty name = new SimpleStringProperty();
        private final IntegerProperty amount = new SimpleIntegerProperty();
        private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
        private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
        private final ObjectProperty<Status> status = new SimpleObjectProperty<>();

        public Promotion(String c, String n, int a, LocalDate s, LocalDate e, Status st) {
            code.set(c);
            name.set(n);
            amount.set(a);
            startDate.set(s);
            endDate.set(e);
            status.set(st);
        }

        public String getCode() { return code.get(); }
        public String getName() { return name.get(); }
        public int getAmount() { return amount.get(); }
        public LocalDate getStartDate() { return startDate.get(); }
        public LocalDate getEndDate() { return endDate.get(); }
        public Status getStatus() { return status.get(); }
    }
}
