package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuDatPhong {
    private PhieuDatPhong phieuDatPhong;
    private LoaiDatPhong loaiDatPhong;
    private List<DichVu> dsachDichVu = new ArrayList<>();
    private int soGioLuuTru;
    private LocalDateTime thoiGianNhanPhong;
    private LocalDateTime thoiGianTraPhong;
    private Phong phong;
    private int soNguoi;

    private String ngayDem;

    public ChiTietPhieuDatPhong(PhieuDatPhong phieuDatPhong, LoaiDatPhong loaiDatPhong, List<DichVu> dsachDichVu,
            int soGioLuuTru, LocalDateTime thoiGianNhanPhong, LocalDateTime thoiGianTraPhong, Phong phong,
            int soNguoi) {
        this.phieuDatPhong = phieuDatPhong;
        this.loaiDatPhong = loaiDatPhong;
        this.dsachDichVu = dsachDichVu;
        this.soGioLuuTru = soGioLuuTru;
        this.thoiGianNhanPhong = thoiGianNhanPhong;
        this.thoiGianTraPhong = thoiGianTraPhong;
        this.phong = phong;
        this.soNguoi = soNguoi;
    }

    public ChiTietPhieuDatPhong(PhieuDatPhong phieuDatPhong, LoaiDatPhong loaiDatPhong, List<DichVu> dsachDichVu,
            int soGioLuuTru, LocalDateTime thoiGianNhanPhong, LocalDateTime thoiGianTraPhong, Phong phong,
            int soNguoi, String ngayDem, double tinhThanhTien) {
        this.phieuDatPhong = phieuDatPhong;
        this.loaiDatPhong = loaiDatPhong;
        this.dsachDichVu = dsachDichVu;
        this.soGioLuuTru = soGioLuuTru;
        this.thoiGianNhanPhong = thoiGianNhanPhong;
        this.thoiGianTraPhong = thoiGianTraPhong;
        this.phong = phong;
        this.soNguoi = soNguoi;
        this.ngayDem = ngayDem;
        tinhThanhTien = tinhThanhTien();

    }

    public ChiTietPhieuDatPhong(PhieuDatPhong phieuDatPhong2, LoaiDatPhong loaiDatPhong2, List<DichVu> dsachDichVu2,
            int soGioLuuTru2, LocalDateTime thoiGianNhanPhong2, LocalDateTime thoiGianTraPhong2, ChiTietPhieuDatPhong p,
            int soNguoi2) {
    }



    public ChiTietPhieuDatPhong(PhieuDatPhong phieuDatPhong2, LoaiDatPhong loaiDatPhong2, List<DichVu> dsachDichVu2,
            int soGioLuuTru2, LocalDateTime thoiGianNhanPhong2, LocalDateTime thoiGianTraPhong2, Phong phong2,
            int soNguoi2, double tinhThanhTien) {
        //TODO Auto-generated constructor stub
    }

    public String getNgayDem() {
        return ngayDem;
    }

    public void setNgayDem(String ngayDem) {
        this.ngayDem = ngayDem;
    }

    public PhieuDatPhong getPhieuDatPhong() {
        return phieuDatPhong;
    }

    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) {
        this.phieuDatPhong = phieuDatPhong;
    }

    public LoaiDatPhong getLoaiDatPhong() {
        return loaiDatPhong;
    }

    public void setLoaiDatPhong(LoaiDatPhong loaiDatPhong) {
        this.loaiDatPhong = loaiDatPhong;
    }

    public List<DichVu> getDsachDichVu() {
        return dsachDichVu;
    }

    public void setDsachDichVu(List<DichVu> dsachDichVu) {
        this.dsachDichVu = dsachDichVu;
    }

    public int getSoGioLuuTru() {
        return soGioLuuTru;
    }

    public void setSoGioLuuTru(int soGioLuuTru) {
        this.soGioLuuTru = soGioLuuTru;
    }

    public LocalDateTime getThoiGianNhanPhong() {
        return thoiGianNhanPhong;
    }

    public void setThoiGianNhanPhong(LocalDateTime thoiGianNhanPhong) {
        this.thoiGianNhanPhong = thoiGianNhanPhong;
    }

    public LocalDateTime getThoiGianTraPhong() {
        return thoiGianTraPhong;
    }

    public void setThoiGianTraPhong(LocalDateTime thoiGianTraPhong) {
        this.thoiGianTraPhong = thoiGianTraPhong;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }

    public int getSoNguoi() {
        return soNguoi;
    }

    public void setSoNguoi(int soNguoi) {
        this.soNguoi = soNguoi;
    }

    public double tinhThanhTien() {
        // Tính tiền phòng
        double thanhTien = this.soGioLuuTru * this.phong.getLoaiPhong().getGia();

        // Cộng thêm tiền dịch vụ
        if (dsachDichVu != null && !dsachDichVu.isEmpty()) {
            for (DichVu dv : dsachDichVu) {
                thanhTien += dv.getGia();
            }
        }

        return thanhTien;
    }

}
