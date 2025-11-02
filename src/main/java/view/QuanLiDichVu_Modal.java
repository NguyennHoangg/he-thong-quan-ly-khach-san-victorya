package view;

import controller.DichVu_Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.DichVu;

public class QuanLiDichVu_Modal {

    private final Stage cuaSo = new Stage();
    private final DichVu_Controller dVu_Controller = new DichVu_Controller();

    private TextField tfMaDichVu = new TextField();
    private TextField tfTenDichVu = new TextField();
    private TextField tfGia = new TextField();
    private ComboBox<String> cmbDonViTinh = new ComboBox<>();
    private TextArea taMoTa = new TextArea();

    private Button btnLuu = new Button("Lưu");
    private Button btnXoa = new Button("Xóa");
    private Button btnMoi = new Button("Làm mới");
    private Button btnCapNhat = new Button("Cập nhật");

    public QuanLiDichVu_Modal(DichVu dvu) {
        cuaSo.initModality(Modality.APPLICATION_MODAL);
        cuaSo.setTitle("Thông tin dịch vụ");

        VBox khungChinh = new VBox(25);
        khungChinh.setPadding(new Insets(20));
        khungChinh.setAlignment(Pos.TOP_CENTER);

        khungChinh.getStylesheets().addAll(
                getClass().getResource("/css/Label.css").toExternalForm(),
                getClass().getResource("/css/Button.css").toExternalForm(),
                getClass().getResource("/css/Control.css").toExternalForm());

        Label tieuDe = new Label("Thông tin dịch vụ");
        tieuDe.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
        VBox.setMargin(tieuDe, new Insets(10, 0, 0, 0));

        Label lblMaDichVu = new Label("Mã dịch vụ:");
        Label lblTenDichVu = new Label("Tên dịch vụ:");
        Label lblGia = new Label("Giá:");
        Label lblMoTa = new Label("Mô tả:");
        Label lblDonViTinh = new Label("Đơn vị tính:");

        ObservableList<String> dsDonViTinh = FXCollections.observableArrayList(dVu_Controller.getDsDonViTinh());
        cmbDonViTinh.setPromptText("Đơn vị tính");
        cmbDonViTinh.setItems(dsDonViTinh);
        cmbDonViTinh.getStyleClass().add("cmb");

        GridPane luoiNhapLieu = new GridPane();
        luoiNhapLieu.setHgap(20); // Khoảng cách giữa các ô
        luoiNhapLieu.setVgap(18);
        luoiNhapLieu.setAlignment(Pos.CENTER);
        tfMaDichVu.setStyle("-fx-opacity: 0.85; -fx-background-color: #f5f5f5;");

        ColumnConstraints cotLabel = new ColumnConstraints(100); // chiều rộng cột
        cotLabel.setHalignment(HPos.RIGHT); // thẳng hàng bên phải giống excel

        ColumnConstraints cotNhap = new ColumnConstraints(400);
        luoiNhapLieu.getColumnConstraints().addAll(cotLabel, cotNhap);

        double chieuRong = 400;
        double chieuCao = 40;

        TextField[] cacTF = { tfMaDichVu, tfTenDichVu, tfGia };
        for (TextField tf : cacTF) {
            tf.setPrefWidth(chieuRong);
            tf.setPrefHeight(chieuCao);
        }
        tfMaDichVu.setEditable(false);

        taMoTa.setPrefWidth(chieuRong);
        taMoTa.setPrefHeight(80);

        cmbDonViTinh.setPrefSize(chieuRong, chieuCao);

        HBox hangNut = new HBox(25);
        hangNut.setAlignment(Pos.BASELINE_LEFT);
        btnLuu.getStyleClass().add("btn");
        btnLuu.setPrefWidth(80);
        btnXoa.getStyleClass().add("btn-huy");
        btnXoa.setPrefWidth(64);
        btnMoi.getStyleClass().add("btn-lam-moi");
        btnCapNhat.getStyleClass().add("btn-luu");

        btnLuu.setOnAction(e -> xuLyThem());
        btnCapNhat.setOnAction(e -> xuLyCapNhat());
        btnXoa.setOnAction(e -> xuLyXoa());
        btnMoi.setOnAction(e -> lamMoi());

        hangNut.getChildren().addAll(btnLuu, btnCapNhat, btnXoa, btnMoi);
        lblMoTa.setAlignment(Pos.TOP_RIGHT);
        luoiNhapLieu.add(lblMaDichVu, 0, 0);
        luoiNhapLieu.add(tfMaDichVu, 1, 0);
        luoiNhapLieu.add(lblTenDichVu, 0, 1);
        luoiNhapLieu.add(tfTenDichVu, 1, 1);
        luoiNhapLieu.add(lblDonViTinh, 0, 2);
        luoiNhapLieu.add(cmbDonViTinh, 1, 2);
        luoiNhapLieu.add(lblGia, 0, 3);
        luoiNhapLieu.add(tfGia, 1, 3);
        luoiNhapLieu.add(lblMoTa, 0, 4);
        luoiNhapLieu.add(taMoTa, 1, 4);
        luoiNhapLieu.add(hangNut, 1, 5);

        duLieu(dvu);

        khungChinh.getChildren().addAll(tieuDe, luoiNhapLieu);

        double chieuRongManHinh = Screen.getPrimary().getVisualBounds().getWidth() * 0.4;
        double chieuCaoManHinh = Screen.getPrimary().getVisualBounds().getHeight() * 0.55;

        Scene canh = new Scene(khungChinh, chieuRongManHinh, chieuCaoManHinh);
        cuaSo.setScene(canh);
    }

    public void hienThi() {
        cuaSo.showAndWait();
    }

    public void lamMoi() {
        tfTenDichVu.clear();
        tfGia.clear();
        taMoTa.clear();
        cmbDonViTinh.getSelectionModel().clearSelection();
    }

    private void duLieu(DichVu dv) {

        if (dv == null) {
            return;
        }
        tfMaDichVu.setText(dv.getMaDichVu());
        tfTenDichVu.setText(dv.getTenDichVu());
        tfGia.setText(String.valueOf(dv.getGia()));
        taMoTa.setText(dv.getMoTa());
        cmbDonViTinh.getSelectionModel().select(dv.getDonViTinh());
    }

    private DichVu layDuLieu() {
        DichVu dvuMoi = new DichVu(null);
        String maDV = tfMaDichVu.getText();
        if (maDV.isEmpty())
            maDV = null;
        String tenDV = tfTenDichVu.getText();
        double gia = Double.parseDouble(tfGia.getText());
        String moTa = taMoTa.getText();
        String donViTinh = cmbDonViTinh.getSelectionModel().getSelectedItem();

        dvuMoi = new DichVu(maDV, tenDV, gia, moTa, donViTinh);

        StringBuilder loiNhan = new StringBuilder();
        boolean hopLe = dVu_Controller.kiemTraDauVao(tenDV, donViTinh, gia, loiNhan);
        if (!hopLe) {
            hienThiThongBao("Cảnh báo", loiNhan.toString(), AlertType.ERROR);
            return null;
        }
        return dvuMoi;
    }

    public void xuLyThem() {
        DichVu dvMoi = layDuLieu();
        StringBuilder loiNhan = new StringBuilder();
        if (dVu_Controller.themDichVu(dvMoi, loiNhan)) {
            hienThiThongBao("Thông báo", loiNhan.toString(), AlertType.INFORMATION);
            cuaSo.close();
        } else {
            hienThiThongBao("Cảnh báo", loiNhan.toString(), AlertType.ERROR);
        }
    }

    public void xuLyCapNhat() {
        DichVu dvMoi = layDuLieu();
        StringBuilder loiNhan = new StringBuilder();
        if (dVu_Controller.capNhatDichVuTheoMa(dvMoi, loiNhan)) {
            hienThiThongBao("Thông báo", loiNhan.toString(), AlertType.INFORMATION);
            cuaSo.close();
        } else {
            hienThiThongBao("Cảnh báo", loiNhan.toString(), AlertType.ERROR);
        }
    }

    public void xuLyXoa() {
        DichVu dvMoi = layDuLieu();
        StringBuilder loiNhan = new StringBuilder();
        if (dVu_Controller.xoaDichVu(dvMoi, loiNhan)) {
            hienThiThongBao("Thông báo", loiNhan.toString(), AlertType.INFORMATION);
            cuaSo.close();
        } else {
            hienThiThongBao("Cảnh báo", loiNhan.toString(), AlertType.ERROR);
        }
    }

    public void hienThiThongBao(String tieuDe, String noiDung, AlertType loaiThongBao) {
        Alert thongBao = new Alert(loaiThongBao);
        thongBao.setContentText(noiDung);
        thongBao.setTitle(tieuDe);
        thongBao.setHeaderText(null);
        thongBao.showAndWait();
    }
}
