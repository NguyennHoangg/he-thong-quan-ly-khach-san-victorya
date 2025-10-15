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

}
