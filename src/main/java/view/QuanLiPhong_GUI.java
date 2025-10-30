package view;

import controller.QuanLiPhong_Controller;
import model.LoaiPhong;
import model.Phong;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.scene.paint.Color;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Predicate;
import java.time.LocalDate;

public class QuanLiPhong_GUI extends BorderPane {


    private static final String ID_TAT_CA = "__ALL__";
    private static final String TXT_TAT_CA = "Tất cả";


    private final QuanLiPhong_Controller controller = new QuanLiPhong_Controller();
    private String maPhongDangChon = null;


    private final TextField tfSoPhong = new TextField();
    private final TextField tfTang = new TextField();
    private final ComboBox<LoaiPhong> cbLoaiPhong = new ComboBox<>();
    private final ComboBox<String> cbTrangThai = new ComboBox<>();
    private final TextField tfGia = new TextField();

    private final Button btnLuu = new Button("Lưu");
    private final Button btnXoa = new Button("Xóa đã chọn");
    private final Button btnMoi = new Button("Tải lại");


    private final TextField tfTimKiem = new TextField();
    private final ComboBox<LoaiPhong> cbLocLoaiPhong = new ComboBox<>();
    private final ComboBox<String> cbLocTrangThai = new ComboBox<>();
    private final ComboBox<String> cbLocTang = new ComboBox<>();


    private final TableView<Phong> bang = new TableView<>();
    private final ObservableList<Phong> duLieuGoc = FXCollections.observableArrayList();


    public QuanLiPhong_GUI() {
        setPadding(new Insets(16));
        setCenter(taoNoiDungChinh());   // tạo UI
        ganSuKien();                    // gắn event KHÔNG dùng lambda
        taiDanhSachLoaiPhong();         // nạp loại phòng (kéo từ DB)
        taiDanhSachPhong();             // nạp danh sách phòng
    }
    private Node taoNoiDungChinh() {
        VBox goc = new VBox(12);
        goc.getChildren().addAll(taoFormNhap(), taoThanhLoc(), taoBang());
        return goc;
    }

    private Node taoFormNhap() {
        // Placeholder & readonly
        tfSoPhong.setPromptText("Nhập tên/số phòng");
        tfTang.setPromptText("Nhập tầng (vd: 1)");
        tfGia.setPromptText("Giá loại phòng (VND)");
        tfGia.setEditable(false); // Giá lấy theo Loại phòng -> không cho sửa tay

        cbLoaiPhong.setConverter(new StringConverter<LoaiPhong>() {
            @Override public String toString(LoaiPhong lp) {
                return lp == null ? "Loại phòng" : lp.getTenLoaiPhong();
            }
            @Override public LoaiPhong fromString(String s) { return null; }
        });

        cbTrangThai.setItems(FXCollections.observableArrayList("Trống", "Đã đặt"));
        cbTrangThai.setConverter(new StringConverter<String>() {
            @Override public String toString(String s) { return s == null ? "Trạng thái" : s; }
            @Override public String fromString(String s) { return s; }
        });

        // Kích thước
        cbLoaiPhong.setPrefWidth(400);
        cbTrangThai.setPrefWidth(400);
        tfSoPhong.setPrefWidth(300);
        tfTang.setPrefWidth(120);
        tfGia.setPrefWidth(220);

        // Style nút
        btnLuu.setStyle("-fx-background-color:#155EEB; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");
        btnXoa.setStyle("-fx-background-color:#f44336; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");
        btnMoi.setStyle("-fx-background-color:#9e9e9e; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 12;");

        // Binding text nút lưu
        btnLuu.textProperty().bind(
                Bindings.when(bang.getSelectionModel().selectedItemProperty().isNull())
                        .then("Thêm mới")
                        .otherwise("Cập nhật")
        );

        // Vô hiệu hóa "Xóa đã chọn" khi không có lựa chọn
        btnXoa.disableProperty().bind(Bindings.isEmpty(bang.getSelectionModel().getSelectedItems()));

        GridPane g = new GridPane();
        g.setHgap(16);
        g.setVgap(10);
        g.add(cot("Số phòng", tfSoPhong), 0, 0);
        g.add(cot("Tầng", tfTang), 1, 0);
        g.add(cot("Loại phòng", cbLoaiPhong), 0, 1);
        g.add(cot("Trạng thái", cbTrangThai), 1, 1);
        g.add(cot("Giá loại phòng", tfGia), 0, 2);

        HBox nutHanhDong = new HBox(10, btnLuu, btnXoa, btnMoi);
        g.add(nutHanhDong, 0, 3);
        GridPane.setMargin(nutHanhDong, new Insets(4, 0, 0, 0));

        return g;
    }

    private VBox cot(String nhan, Node dieuKhien) {
        Label lb = new Label(nhan);
        VBox hop = new VBox(6, lb, dieuKhien);
        hop.setPrefWidth(360);
        return hop;
    }

    private Node taoThanhLoc() {
        // Ô tìm kiếm
        tfTimKiem.setPromptText("Tìm theo tên phòng");

        // Loại phòng
        cbLocLoaiPhong.setPromptText("Loại phòng");
        cbLocLoaiPhong.setConverter(new StringConverter<LoaiPhong>() {
            @Override
            public String toString(LoaiPhong t) {
                return (t == null || t.getTenLoaiPhong() == null) ? "Loại phòng" : t.getTenLoaiPhong();
            }
            @Override
            public LoaiPhong fromString(String s) { return null; }
        });
        cbLocLoaiPhong.setButtonCell(new ListCell<LoaiPhong>() {
            @Override
            protected void updateItem(LoaiPhong item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getTenLoaiPhong() == null || TXT_TAT_CA.equals(item.getTenLoaiPhong())) {
                    setText("Loại phòng");
                } else {
                    setText(item.getTenLoaiPhong());
                }
            }
        });

        // Trạng thái
        cbLocTrangThai.setPromptText("Trạng thái");
        cbLocTrangThai.setItems(FXCollections.observableArrayList(TXT_TAT_CA, "Trống", "Đã đặt"));
        cbLocTrangThai.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || TXT_TAT_CA.equals(item)) {
                    setText("Trạng thái");
                } else {
                    setText(item);
                }
            }
        });
        cbLocTrangThai.getSelectionModel().selectFirst(); // chọn "Tất cả"

        // Tầng
        cbLocTang.setPromptText("Tầng");
        cbLocTang.getItems().setAll("Tất cả", "Tầng 0", "Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4", "Tầng 5");
        cbLocTang.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || TXT_TAT_CA.equals(item)) {
                    setText("Tầng");
                } else {
                    setText(item);
                }
            }
        });
        cbLocTang.getSelectionModel().selectFirst(); // chọn "Tất cả"

        HBox thanh = new HBox(10, tfTimKiem, cbLocLoaiPhong, cbLocTrangThai, cbLocTang);
        thanh.setAlignment(Pos.CENTER_LEFT);
        thanh.setPadding(new Insets(8, 0, 8, 0));
        return thanh;
    }

    private Node taoBang() {
        // Cho phép chọn nhiều dòng để xoá nhiều bản ghi
        bang.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Cột Số phòng
        TableColumn<Phong, String> cotSoPhong = new TableColumn<>("Phòng");
        cotSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        cotSoPhong.setPrefWidth(120);

        // Cột Loại phòng
        TableColumn<Phong, String> cotLoaiPhong = new TableColumn<>("Loại phòng");
        cotLoaiPhong.setCellValueFactory(new Callback<CellDataFeatures<Phong, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Phong, String> c) {
                LoaiPhong lp = c.getValue().getLoaiPhong();
                String ten = (lp != null) ? lp.getTenLoaiPhong() : "";
                return new ReadOnlyStringWrapper(ten);
            }
        });
        cotLoaiPhong.setPrefWidth(180);

        // Cột Tầng
        TableColumn<Phong, String> cotTang = new TableColumn<>("Tầng");
        cotTang.setCellValueFactory(new Callback<CellDataFeatures<Phong, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Phong, String> c) {
                return new ReadOnlyStringWrapper("Tầng " + c.getValue().getTang());
            }
        });
        cotTang.setPrefWidth(100);

        // Cột Trạng thái
        TableColumn<Phong, String> cotTrangThai = new TableColumn<>("Trạng thái");
        cotTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        cotTrangThai.setPrefWidth(140);
        cotTrangThai.setCellFactory(new Callback<TableColumn<Phong, String>, TableCell<Phong, String>>() {
            @Override
            public TableCell<Phong, String> call(TableColumn<Phong, String> tc) {
                return new TableCell<Phong, String>() {
                    @Override
                    protected void updateItem(String st, boolean empty) {
                        super.updateItem(st, empty);
                        if (empty || st == null) {
                            setGraphic(null);
                        } else {
                            Color mau;
                            String hienThi;
                            if (st.equalsIgnoreCase("Trống")) {
                                mau = Color.web("#10b981");   // xanh lá
                                hienThi = "Trống";
                            } else if (st.equalsIgnoreCase("Đã đặt")) {
                                mau = Color.web("#ef4444");   // đỏ
                                hienThi = "Đã đặt";
                            } else {
                                mau = Color.web("#6b7280");   // xám
                                hienThi = st;
                            }
                            setGraphic(vienChip(hienThi, mau));
                        }
                    }
                };
            }
        });

        // Cột Giá
        TableColumn<Phong, Number> cotGia = new TableColumn<>("Giá (VND)");
        cotGia.setCellValueFactory(new Callback<CellDataFeatures<Phong, Number>, ObservableValue<Number>>() {
            @Override
            public ObservableValue<Number> call(CellDataFeatures<Phong, Number> c) {
                LoaiPhong lp = c.getValue().getLoaiPhong();
                double gia = (lp == null) ? 0d : lp.getGia(); // getGia() là double -> không null
                return new ReadOnlyDoubleWrapper(gia);
            }
        });
        cotGia.setPrefWidth(140);
        cotGia.setCellFactory(new Callback<TableColumn<Phong, Number>, TableCell<Phong, Number>>() {
            @Override
            public TableCell<Phong, Number> call(TableColumn<Phong, Number> col) {
                return new TableCell<Phong, Number>() {
                    @Override
                    protected void updateItem(Number v, boolean empty) {
                        super.updateItem(v, empty);
                        if (empty || v == null) setText(null);
                        else setText(dinhDangVND(v));
                        setStyle("-fx-alignment: CENTER-RIGHT;");
                    }
                };
            }
        });

        bang.getColumns().setAll(cotSoPhong, cotLoaiPhong, cotTang, cotTrangThai, cotGia);
        bang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bang.setPrefHeight(440);

        // Lọc & sắp xếp
        final FilteredList<Phong> loc = new FilteredList<>(duLieuGoc);

        tfTimKiem.textProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> o, String oldVal, String newVal) {
                loc.setPredicate(taoDieuKienLoc());
            }
        });
        cbLocLoaiPhong.valueProperty().addListener(new ChangeListener<LoaiPhong>() {
            @Override public void changed(ObservableValue<? extends LoaiPhong> o, LoaiPhong oldVal, LoaiPhong newVal) {
                loc.setPredicate(taoDieuKienLoc());
            }
        });
        cbLocTrangThai.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> o, String oldVal, String newVal) {
                loc.setPredicate(taoDieuKienLoc());
            }
        });
        cbLocTang.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> o, String oldVal, String newVal) {
                loc.setPredicate(taoDieuKienLoc());
            }
        });

        SortedList<Phong> sapXep = new SortedList<>(loc);
        sapXep.comparatorProperty().bind(bang.comparatorProperty());
        bang.setItems(sapXep);

        // Chọn dòng -> đổ form
        bang.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Phong>() {
            @Override
            public void changed(ObservableValue<? extends Phong> o, Phong cu, Phong moi) {
                if (moi != null) {
                    maPhongDangChon = moi.getMaPhong();
                    tfSoPhong.setText(moi.getSoPhong());
                    tfTang.setText(String.valueOf(moi.getTang()));
                    cbLoaiPhong.getSelectionModel().select(moi.getLoaiPhong());
                    cbTrangThai.getSelectionModel().select(moi.getTrangThai());
                    LoaiPhong lp = moi.getLoaiPhong();
                    tfGia.setText(dinhDangVND(lp == null ? 0 : lp.getGia()));
                }
            }
        });

        return new VBox(new Separator(), bang);
    }

    /* ==================== Lọc dữ liệu ==================== */
    private Predicate<Phong> taoDieuKienLoc() {
        final String tuKhoa = layChuoi(tfTimKiem.getText());
        final LoaiPhong loai = cbLocLoaiPhong.getValue();
        final String tt = cbLocTrangThai.getValue();
        final String tang = cbLocTang.getValue();

        return new Predicate<Phong>() {
            @Override
            public boolean test(Phong r) {
                boolean hopTuKhoa = tuKhoa.isBlank()
                        || (r.getSoPhong() != null && r.getSoPhong().toLowerCase().contains(tuKhoa));
                boolean hopLoai = (loai == null)
                        || ID_TAT_CA.equals(loai.getMaLoaiPhong())
                        || (r.getLoaiPhong() != null
                        && loai.getMaLoaiPhong().equals(r.getLoaiPhong().getMaLoaiPhong()));
                boolean hopTT = (tt == null) || TXT_TAT_CA.equals(tt)
                        || tt.equalsIgnoreCase(r.getTrangThai());
                String tangHienThi = "Tầng " + r.getTang();
                boolean hopTang = (tang == null) || TXT_TAT_CA.equals(tang)
                        || tangHienThi.equalsIgnoreCase(tang);

                return hopTuKhoa && hopLoai && hopTT && hopTang;
            }
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

    private void hienThongBao(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
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


    private void ganSuKien() {
        tfTimKiem.setOnKeyPressed(new javafx.event.EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent e) {
                if (e.getCode() == KeyCode.ENTER) bang.requestFocus();
            }
        });

        cbLoaiPhong.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LoaiPhong>() {
            @Override public void changed(ObservableValue<? extends LoaiPhong> o, LoaiPhong cu, LoaiPhong moi) {
                if (moi != null) tfGia.setText(dinhDangVND(moi.getGia()));
                else tfGia.clear();
            }
        });

        btnLuu.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override public void handle(javafx.event.ActionEvent e) { luuPhong(); }
        });

        // Xoá nhiều
        btnXoa.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override public void handle(javafx.event.ActionEvent e) { xoaNhieuPhong(); }
        });

        // Tải lại
        btnMoi.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override public void handle(javafx.event.ActionEvent e) {
                try {
                    taiDanhSachPhong();
                    bang.getSelectionModel().clearSelection();
                    hienThongBao("Đã tải lại", "Danh sách phòng đã được tải lại từ CSDL.");
                } finally {
                    lamMoiForm();
                }
            }
        });
    }


    private void luuPhong() {
        try {
            String soPhong   = tfSoPhong.getText().trim();
            String tangStr = tfTang.getText().trim();
            LoaiPhong loai = cbLoaiPhong.getValue();
            String tt      = cbTrangThai.getValue();

            if (soPhong.isEmpty() || tangStr.isEmpty() || loai == null || tt == null) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin.").showAndWait();
                return;
            }

            // Lấy số tầng (chỉ lấy ký tự số)
            String tangDigits = tangStr.replaceAll("[^0-9]", "");
            if (tangDigits.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Giá trị tầng không hợp lệ.").showAndWait();
                return;
            }
            int tang = Integer.parseInt(tangDigits);

            // Gọi hàm kiểm tra số phòng
            String soPhongHopLe = kiemTraSoPhongHopLe(soPhong, tang);
            if (soPhongHopLe == null) return; // Nếu sai, dừng luôn

            // Tạo đối tượng Phong
            Phong p = new Phong(
                    maPhongDangChon, // null => thêm; khác null => sửa
                    soPhongHopLe,
                    new LoaiPhong(loai.getMaLoaiPhong(), loai.getTenLoaiPhong(), loai.getGia(), loai.getNgayTao()),
                    tt,
                    tang
            );

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

        // Lọc bỏ mọi ký tự không phải số
        String soDigits = soPhong.replaceAll("\\D+", ""); // Ví dụ "P301" -> "301"
        if (soDigits.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Số phòng không hợp lệ (phải là số).").showAndWait();
            return null;
        }

        // Kiểm tra bắt đầu bằng số tầng
        String prefixTang = String.valueOf(tang);
        if (!soDigits.startsWith(prefixTang)) {
            new Alert(Alert.AlertType.WARNING,
                    "Số phòng phải bắt đầu bằng số tầng.\n" +
                            "Ví dụ: Tầng " + tang + " thì số phòng nên dạng \"" + prefixTang + "xx\".\n" +
                            "Bạn nhập: \"" + soPhong + "\"").showAndWait();
            return null;
        }

        return soDigits; // Trả về phần số hợp lệ
    }

    private void lamMoiForm() {
        maPhongDangChon = null;
        bang.getSelectionModel().clearSelection();
        tfSoPhong.clear();
        tfTang.clear();
        tfGia.clear();
        cbLoaiPhong.getSelectionModel().clearSelection();
        cbTrangThai.getSelectionModel().clearSelection();
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
        if (maPhong == null) return;
        for (Phong p : bang.getItems()) {
            if (maPhong.equals(p.getMaPhong())) {
                bang.getSelectionModel().select(p);
                bang.scrollTo(p);
                break;
            }
        }
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
        if (conf.getResult() != ButtonType.OK) return;

        int daXoa = 0;
        java.util.List<Phong> banSao = new java.util.ArrayList<>(chon);
        for (Phong p : banSao) {
            try {
                controller.deleteRoom(p.getMaPhong());
                daXoa++;
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Không thể xoá phòng " + p.getSoPhong() + ": " + ex.getMessage()).showAndWait();
            }
        }
        taiDanhSachPhong();
        lamMoiForm();
        new Alert(Alert.AlertType.INFORMATION, "Đã xoá " + daXoa + " phòng.").showAndWait();
    }
}
