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

    /* ===== Constructors ===== */

    // Đầy đủ (5 tham số)
    public LoaiPhong(String maLoaiPhong, String tenLoaiPhong, double gia,
                     LocalDate ngayTao, List<DichVu> dsachDichVu) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.gia = gia;
        this.ngayTao = ngayTao;
        this.dsachDichVu = (dsachDichVu != null) ? dsachDichVu : new ArrayList<>();
    }

    // 4 tham số: ngày tạo (không danh sách dịch vụ)
    public LoaiPhong(String maLoaiPhong, String tenLoaiPhong, double gia, LocalDate ngayTao) {
        this(maLoaiPhong, tenLoaiPhong, gia, ngayTao, new ArrayList<>());
    }

    // **4 tham số: danh sách dịch vụ (để tương thích Phong_DAO của bạn)**
    public LoaiPhong(String maLoaiPhong, String tenLoaiPhong, double gia, List<DichVu> dsachDichVu) {
        this(maLoaiPhong, tenLoaiPhong, gia, null, dsachDichVu);
    }

    // 3 tham số: tối giản
    public LoaiPhong(String maLoaiPhong, String tenLoaiPhong, double gia) {
        this(maLoaiPhong, tenLoaiPhong, gia, null, new ArrayList<>());
    }

    // 1 tham số: chỉ mã (tham chiếu FK)
    public LoaiPhong(String maLoaiPhong) {
        this(maLoaiPhong, null, 0.0, null, new ArrayList<>());
    }

    /* ===== Getters/Setters ===== */

    public String getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(String maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }

    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public void setTenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; }

    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }

    public LocalDate getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDate ngayTao) { this.ngayTao = ngayTao; }

    public List<DichVu> getDsachDichVu() { return dsachDichVu; }
    public void setDsachDichVu(List<DichVu> dsachDichVu) {
        this.dsachDichVu = (dsachDichVu != null) ? dsachDichVu : new ArrayList<>();
    }

    @Override
    public String toString() {
        return (tenLoaiPhong != null && !tenLoaiPhong.isBlank()) ? tenLoaiPhong : maLoaiPhong;
    }
}
