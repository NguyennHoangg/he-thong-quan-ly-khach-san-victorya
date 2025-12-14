package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class KhuyenMai {
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;

    // 3 trạng thái
    private TrangThai trangThai;

    private float heSo;
    private float tongTienToiThieu;
    private float tongKhuyenMaiToiDa;

    public KhuyenMai() {}

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai,
                     LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc,
                     TrangThai trangThai, float heSo, float tongTienToiThieu, float tongKhuyenMaiToiDa) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
        this.heSo = heSo;
        this.tongTienToiThieu = tongTienToiThieu;
        this.tongKhuyenMaiToiDa = tongKhuyenMaiToiDa;
    }

    public enum TrangThai {
        SAP_DIEN_RA("Sắp diễn ra"),
        DANG_HOAT_DONG("Đang hoạt động"),
        KET_THUC("Kết thúc");

        private final String label;
        TrangThai(String label) { this.label = label; }
        public String label() { return label; }

        public static TrangThai fromDb(String s) {
            if (s == null) return null;
            String x = s.trim().toLowerCase();
            if (x.contains("sắp") || x.contains("sap")) return SAP_DIEN_RA;
            if (x.contains("kết") || x.contains("ket")) return KET_THUC;
            return DANG_HOAT_DONG;
        }

        public static String toDb(TrangThai st) {
            return st == null ? null : st.label();
        }

        // Tính theo ngày (source of truth)
        public static TrangThai computeByDates(LocalDate bd, LocalDate kt) {
            LocalDate today = LocalDate.now();
            if (bd != null && bd.isAfter(today)) return SAP_DIEN_RA;
            if (kt != null && kt.isBefore(today)) return KET_THUC;
            return DANG_HOAT_DONG;
        }
    }
    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public String getTenKhuyenMai() { return tenKhuyenMai; }
    public void setTenKhuyenMai(String tenKhuyenMai) { this.tenKhuyenMai = tenKhuyenMai; }

    public LocalDateTime getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDateTime ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public LocalDateTime getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDateTime ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public TrangThai getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThai trangThai) { this.trangThai = trangThai; }

    public float getHeSo() { return heSo; }
    public void setHeSo(float heSo) { this.heSo = heSo; }

    public float getTongTienToiThieu() { return tongTienToiThieu; }
    public void setTongTienToiThieu(float tongTienToiThieu) { this.tongTienToiThieu = tongTienToiThieu; }

    public float getTongKhuyenMaiToiDa() { return tongKhuyenMaiToiDa; }
    public void setTongKhuyenMaiToiDa(float tongKhuyenMaiToiDa) { this.tongKhuyenMaiToiDa = tongKhuyenMaiToiDa; }
}
