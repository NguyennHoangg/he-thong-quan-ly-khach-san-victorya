package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoaiPhong {
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private double gia;
    private LocalDate ngayTao;
    private List<DichVu> dsachDichVu;

    // Sức chứa
    private int soNguoiLonToiDa;
    private int soTreEmToiDa;



    /**
     * Full constructor: dùng khi bạn muốn set luôn sức chứa
     */
    public LoaiPhong(String maLoaiPhong,
                     String tenLoaiPhong,
                     double gia,
                     LocalDate ngayTao,
                     List<DichVu> dsachDichVu,
                     int soNguoiLonToiDa,
                     int soTreEmToiDa) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.gia = gia;
        this.ngayTao = ngayTao;
        this.dsachDichVu = (dsachDichVu != null) ? dsachDichVu : new ArrayList<>();
        this.soNguoiLonToiDa = soNguoiLonToiDa;
        this.soTreEmToiDa = soTreEmToiDa;
    }

    /**
     * Đầy đủ (5 tham số) – GIỮ TƯƠNG THÍCH CŨ
     * => mặc định sức chứa 2 người lớn, 1 trẻ em (có thể sửa bằng setter)
     */
    public LoaiPhong(String maLoaiPhong,
                     String tenLoaiPhong,
                     double gia,
                     LocalDate ngayTao,
                     List<DichVu> dsachDichVu) {
        this(maLoaiPhong, tenLoaiPhong, gia, ngayTao, dsachDichVu, 2, 1);
    }

    /**
     * 4 tham số: ngày tạo (không danh sách dịch vụ)
     */
    public LoaiPhong(String maLoaiPhong,
                     String tenLoaiPhong,
                     double gia,
                     LocalDate ngayTao) {
        this(maLoaiPhong, tenLoaiPhong, gia, ngayTao, new ArrayList<>(), 2, 1);
    }

    /**
     * 4 tham số: danh sách dịch vụ (tương thích Phong_DAO)
     */
    public LoaiPhong(String maLoaiPhong,
                     String tenLoaiPhong,
                     double gia,
                     List<DichVu> dsachDichVu) {
        this(maLoaiPhong, tenLoaiPhong, gia, null, dsachDichVu, 2, 1);
    }

    /**
     * 3 tham số: tối giản
     */
    public LoaiPhong(String maLoaiPhong,
                     String tenLoaiPhong,
                     double gia) {
        this(maLoaiPhong, tenLoaiPhong, gia, null, new ArrayList<>(), 2, 1);
    }

    /**
     * 1 tham số: chỉ mã (tham chiếu FK)
     */
    public LoaiPhong(String maLoaiPhong) {
        this(maLoaiPhong, null, 0.0, null, new ArrayList<>(), 2, 1);
    }

    /* ================== GETTER / SETTER ================== */

    public String getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public void setMaLoaiPhong(String maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

    public List<DichVu> getDsachDichVu() {
        return dsachDichVu;
    }

    public void setDsachDichVu(List<DichVu> dsachDichVu) {
        this.dsachDichVu = (dsachDichVu != null) ? dsachDichVu : new ArrayList<>();
    }

    public int getSoNguoiLonToiDa() {
        return soNguoiLonToiDa;
    }

    public void setSoNguoiLonToiDa(int soNguoiLonToiDa) {
        this.soNguoiLonToiDa = soNguoiLonToiDa;
    }

    public int getSoTreEmToiDa() {
        return soTreEmToiDa;
    }

    public void setSoTreEmToiDa(int soTreEmToiDa) {
        this.soTreEmToiDa = soTreEmToiDa;
    }

    @Override
    public String toString() {
        return (tenLoaiPhong != null && !tenLoaiPhong.isBlank()) ? tenLoaiPhong : maLoaiPhong;
    }
}
