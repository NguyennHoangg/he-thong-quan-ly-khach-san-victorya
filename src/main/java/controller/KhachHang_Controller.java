package controller;

import java.util.ArrayList;
import java.util.List;

import dao.KhachHang_DAO;
import model.KhachHang;

public class KhachHang_Controller {
    private KhachHang_DAO kh_dao = new KhachHang_DAO();

    public KhachHang_Controller() {
    }

    public List<KhachHang> getDsKhachHang() {
        return kh_dao.getDsKhachHang();
    }
    
    // Phương thức cũ để tương thích với code hiện tại
    public static List<KhachHang> getDsachKH(String CCCD){
        KhachHang_DAO khachHang_DAO = new KhachHang_DAO();
        KhachHang kh = khachHang_DAO.timKhachHangTheoCCCD(CCCD);
        List<KhachHang> dsachKH = new ArrayList<>();
        if (kh != null) {
            dsachKH.add(kh);
        }
        return dsachKH;
    }
    
    // Phương thức tìm khách hàng theo CCCD (tương thích)
    public static KhachHang timKhachHangTheoCCCD(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return null;
        }
        KhachHang_DAO khachHang_DAO = new KhachHang_DAO();
        return khachHang_DAO.timKhachHangTheoCCCD(cccd);
    }

    public boolean themKhachHang(KhachHang kh, StringBuilder loiNhan) {
        // Kiểm tra trùng CCCD
        KhachHang khTrungCCCD = kh_dao.timKhachHangTheoCCCD(kh.getCCCD());
        if (khTrungCCCD != null) {
            loiNhan.append("CCCD đã tồn tại trong hệ thống!\n");
            return false;
        }
        
        // Kiểm tra trùng số điện thoại
        KhachHang khTrungSDT = kh_dao.timKhachHangTheoSoDienThoai(kh.getSoDienThoai());
        if (khTrungSDT != null) {
            loiNhan.append("Số điện thoại đã tồn tại trong hệ thống!\n");
            return false;
        }
        
        // Kiểm tra trùng email (nếu có)
        if (kh.getEmail() != null && !kh.getEmail().trim().isEmpty()) {
            KhachHang khTrungEmail = kh_dao.timKhachHangTheoEmail(kh.getEmail());
            if (khTrungEmail != null) {
                loiNhan.append("Email đã tồn tại trong hệ thống!\n");
                return false;
            }
        }
        
        // Thêm mới
        if (kh_dao.themKhachHang(kh)) {
            loiNhan.append("Thêm khách hàng thành công");
            return true;
        } else {
            loiNhan.append("Thêm khách hàng thất bại!");
            return false;
        }
    }

    public boolean suaKhachHang(KhachHang kh, StringBuilder loiNhan) {
        // Kiểm tra khách hàng có tồn tại không
        KhachHang khCu = kh_dao.timKhachHangTheoMa(kh.getMaKhachHang());
        if (khCu == null) {
            loiNhan.append("Không tìm thấy khách hàng cần sửa!\n");
            return false;
        }
        
        // Kiểm tra trùng CCCD (ngoại trừ chính khách hàng đang sửa)
        KhachHang khTrungCCCD = kh_dao.timKhachHangTheoCCCD(kh.getCCCD());
        if (khTrungCCCD != null && !khTrungCCCD.getMaKhachHang().equals(kh.getMaKhachHang())) {
            loiNhan.append("CCCD đã tồn tại trong hệ thống!\n");
            return false;
        }
        
        // Kiểm tra trùng số điện thoại (ngoại trừ chính khách hàng đang sửa)
        KhachHang khTrungSDT = kh_dao.timKhachHangTheoSoDienThoai(kh.getSoDienThoai());
        if (khTrungSDT != null && !khTrungSDT.getMaKhachHang().equals(kh.getMaKhachHang())) {
            loiNhan.append("Số điện thoại đã tồn tại trong hệ thống!\n");
            return false;
        }
        
        // Kiểm tra trùng email (nếu có, ngoại trừ chính khách hàng đang sửa)
        if (kh.getEmail() != null && !kh.getEmail().trim().isEmpty()) {
            KhachHang khTrungEmail = kh_dao.timKhachHangTheoEmail(kh.getEmail());
            if (khTrungEmail != null && !khTrungEmail.getMaKhachHang().equals(kh.getMaKhachHang())) {
                loiNhan.append("Email đã tồn tại trong hệ thống!\n");
                return false;
            }
        }
        
        // Giữ nguyên ngày tạo
        kh.setNgayTao(khCu.getNgayTao());
        
        // Cập nhật
        if (kh_dao.capNhatKhachHang(kh)) {
            loiNhan.append("Cập nhật khách hàng thành công");
            return true;
        } else {
            loiNhan.append("Cập nhật khách hàng thất bại!");
            return false;
        }
    }

    public boolean xoaKhachHang(KhachHang kh, StringBuilder loiNhan) {
        if (kh == null || kh.getMaKhachHang() == null) {
            loiNhan.append("Không tìm thấy thông tin khách hàng cần xóa!");
            return false;
        }
        
        boolean ketQua = kh_dao.xoaKhachHang(kh.getMaKhachHang());
        if (ketQua) {
            loiNhan.append("Xóa khách hàng thành công!");
            return true;
        } else {
            loiNhan.append("Không thể xóa khách hàng!\n");
            loiNhan.append("Có thể khách hàng này đang có phiếu đặt phòng hoặc hóa đơn liên quan.");
            return false;
        }
    }

    public List<KhachHang> timKhachHang(String tuKhoa) {
        List<KhachHang> dsTimDuoc = new ArrayList<>();
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return getDsKhachHang();
        }
        tuKhoa = tuKhoa.trim().toLowerCase();
        
        // Nếu là CCCD (12 số)
        if (tuKhoa.matches("^[0-9]{12}$")) {
            KhachHang kh = kh_dao.timKhachHangTheoCCCD(tuKhoa);
            if (kh != null) {
                dsTimDuoc.add(kh);
            }
            return dsTimDuoc;
        }
        
        // Tìm theo tên hoặc email
        for (KhachHang kh : kh_dao.getDsKhachHang()) {
            String ten = kh.getTenKhachHang() != null ? kh.getTenKhachHang().toLowerCase() : "";
            String email = kh.getEmail() != null ? kh.getEmail().toLowerCase() : "";
            String sdt = kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "";
            
            if (ten.contains(tuKhoa) || email.contains(tuKhoa) || sdt.contains(tuKhoa)) {
                dsTimDuoc.add(kh);
            }
        }
        return dsTimDuoc;
    }

    public List<KhachHang> timKhachHangKetHop(String ten, String cccd, String email, String soDienThoai) {
        List<KhachHang> dsKetQua = new ArrayList<>();
        List<KhachHang> dsAll = kh_dao.getDsKhachHang();
        
        for (KhachHang kh : dsAll) {
            boolean hopTen = (ten == null || ten.trim().isEmpty()) || 
                            (kh.getTenKhachHang() != null && kh.getTenKhachHang().toLowerCase().contains(ten.trim().toLowerCase()));
            boolean hopCCCD = (cccd == null || cccd.trim().isEmpty()) || 
                            (kh.getCCCD() != null && kh.getCCCD().contains(cccd.trim()));
            boolean hopEmail = (email == null || email.trim().isEmpty()) || 
                            (kh.getEmail() != null && kh.getEmail().toLowerCase().contains(email.trim().toLowerCase()));
            boolean hopSDT = (soDienThoai == null || soDienThoai.trim().isEmpty()) || 
                            (kh.getSoDienThoai() != null && kh.getSoDienThoai().contains(soDienThoai.trim()));
            
            if (hopTen && hopCCCD && hopEmail && hopSDT) {
                dsKetQua.add(kh);
            }
        }
        
        return dsKetQua;
    }

    public boolean kiemTra(KhachHang kh, StringBuilder loi) {
        if (kh.getTenKhachHang() == null || kh.getTenKhachHang().trim().isEmpty()) {
            loi.append("Tên khách hàng không được rỗng!\n");
            return false;
        }
        if (!kh.getTenKhachHang().matches("^[\\p{L}\\s]+$")) {
            loi.append("Tên khách hàng chỉ được chứa chữ cái và khoảng trắng!\n");
            return false;
        }
        if (kh.getCCCD() == null || kh.getCCCD().trim().isEmpty()) {
            loi.append("CCCD không được rỗng!\n");
            return false;
        }
        if (!kh.getCCCD().matches("^[0-9]{12}$")) {
            loi.append("CCCD phải gồm đúng 12 chữ số!\n");
            return false;
        }
        if (kh.getSoDienThoai() == null || kh.getSoDienThoai().trim().isEmpty()) {
            loi.append("Số điện thoại không được rỗng!\n");
            return false;
        }
        if (!kh.getSoDienThoai().matches("^[0-9]{10,11}$")) {
            loi.append("Số điện thoại phải gồm 10-11 chữ số!\n");
            return false;
        }
        if (kh.getEmail() != null && !kh.getEmail().trim().isEmpty()) {
            if (!kh.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
                loi.append("Email không hợp lệ!\n");
                return false;
            }
        }
        return true;
    }
}
