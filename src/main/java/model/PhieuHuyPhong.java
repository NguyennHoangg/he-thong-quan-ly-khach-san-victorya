package model;

import java.sql.Date;

public class PhieuHuyPhong {
    private String maHuyPhong;
    private PhieuDatPhong pdp;
    private String lyDo;
    private Date ngayHuy;

    public PhieuHuyPhong(PhieuDatPhong pdp, String lyDo, Date ngayHuy) {
        this.pdp = pdp;
        this.lyDo = lyDo;
        this.ngayHuy = ngayHuy;
    }

    public String getMaHuyPhong() {
        return maHuyPhong;
    }

    public void setMaHuyPhong(String maHuyPhong) {
        this.maHuyPhong = maHuyPhong;
    }

    public PhieuDatPhong getPdp() {
        return pdp;
    }

    public void setPdp(PhieuDatPhong pdp) {
        this.pdp = pdp;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public Date getNgayHuy() {
        return ngayHuy;
    }

    public void setNgayHuy(Date ngayHuy) {
        this.ngayHuy = ngayHuy;
    }

}
