package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dao.Phong_DAO;
import model.Phong;

public class Phong_Controller {
    private Phong_DAO phong_DAO = new Phong_DAO();

    public Phong_Controller() {

    }

    public boolean capNhatTrangThaiPhong(String maPhong, String trangThaiMoi) {
        return phong_DAO.capNhatTrangThaiPhong(maPhong, trangThaiMoi);
    }

    public List<Phong> getDsPhongTheoTrangThai(String trangThai) {
        List<Phong> dsPhong = phong_DAO.getPhongTheoTrangThai(trangThai);

        return dsPhong;
    }

    public Phong getPhongTheoSoPhong(String soPhong) {
        Phong p_ketQua = null;
        for (Phong p : phong_DAO.getTatCaPhong()) {
            if (p.getSoPhong().trim().equalsIgnoreCase(soPhong)) {
                p_ketQua = p;
            }
        }
        if (p_ketQua != null)

        {
            return p_ketQua;
        } else {
            return null;
        }
    }

    public List<Phong> getDsachPhong_TrangTimKiem() {
        List<Phong> dsachPhong = phong_DAO.getTatCaPhong();
        return dsachPhong;
    }

    /**
     * Lấy danh sách phòng TRỐNG theo thời gian check-in và check-out
     * 
     * @param tenLoaiPhong Tên loại phòng (VIP/Thường) hoặc null
     * @param thoiGianCheckIn Thời gian check-in (yyyy-MM-dd HH:mm:ss)
     * @param thoiGianCheckOut Thời gian check-out (yyyy-MM-dd HH:mm:ss)
     * @return Danh sách phòng trống trong khoảng thời gian
     */
    public List<Phong> getDsachPhongTrongTheoThoiGian(String tenLoaiPhong, String thoiGianCheckIn, String thoiGianCheckOut) {
        return phong_DAO.timKiemPhongTrongTheoThoiGian(tenLoaiPhong, thoiGianCheckIn, thoiGianCheckOut);
    }

    /**
     * @deprecated Sử dụng getDsachPhongTrongTheoThoiGian() thay thế
     */
    @Deprecated
    public List<Phong> getDsachPhongTheoThoiGian(String tenLoaiPhong, String thoiGianCheckIn, String thoiGianCheckOut) {
        return getDsachPhongTrongTheoThoiGian(tenLoaiPhong, thoiGianCheckIn, thoiGianCheckOut);
    }

    public List<Integer> getDsTang() {
        List<Integer> ketQua = new ArrayList<>();

        for (Phong p : phong_DAO.getTatCaPhong()) {
            int soTang = p.getTang();
            if (!ketQua.contains(soTang)) {
                ketQua.add(soTang);
            }
        }
        ketQua.add(0, 0);
        // Sắp xếp tăng
        ketQua.sort((a, b) -> a - b);

        return ketQua;
    }

    /**
     * Gợi ý phòng phù hợp dựa trên thời gian, số người và loại phòng
     * 
     * @param thoiGianCheckIn Thời gian check-in (yyyy-MM-dd HH:mm:ss)
     * @param thoiGianCheckOut Thời gian check-out (yyyy-MM-dd HH:mm:ss)
     * @param tenLoaiPhong Tên loại phòng (VIP/Thường) hoặc null
     * @param soNguoiLon Số người lớn
     * @param soTreEm Số trẻ em
     * @return Danh sách phòng được sắp xếp theo độ phù hợp
     */
    public List<Phong> goiYPhongPhuHop(String thoiGianCheckIn, String thoiGianCheckOut, String tenLoaiPhong, int soNguoiLon, int soTreEm) {
        
        
        // Lấy danh sách phòng trống theo thời gian
        List<Phong> dsPhongTrong = phong_DAO.timKiemPhongTrongTheoThoiGian(tenLoaiPhong, thoiGianCheckIn, thoiGianCheckOut);
        
      
        
        if (dsPhongTrong == null || dsPhongTrong.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Tính toán số phòng cần thiết
    
        
        // Lọc và đánh giá từng phòng
        List<PhongGoiY> dsPhongGoiY = new ArrayList<>();
        
        for (Phong phong : dsPhongTrong) {
            // Tính điểm phù hợp cho phòng này
            int diemPhuHop = tinhDiemPhuHop(phong, soNguoiLon, soTreEm, tenLoaiPhong);
            
            // THÊM TẤT CẢ phòng trống vào danh sách để có thể tổ hợp
            // Người dùng có thể chọn nhiều phòng để đủ chỗ
            dsPhongGoiY.add(new PhongGoiY(phong, diemPhuHop));
        }
        
        
        // Sắp xếp theo sức chứa (cao xuống thấp) - Dành cho nhân viên lễ tân
        dsPhongGoiY.sort((p1, p2) -> {
            int sucChua1 = p1.phong.getLoaiPhong().getSoNguoiLonToiDa() + p1.phong.getLoaiPhong().getSoTreEmToiDa();
            int sucChua2 = p2.phong.getLoaiPhong().getSoNguoiLonToiDa() + p2.phong.getLoaiPhong().getSoTreEmToiDa();
            
            if (sucChua1 != sucChua2) {
                return Integer.compare(sucChua2, sucChua1); // Sức chứa cao trước
            }
            
            // Nếu sức chứa bằng nhau, sắp xếp theo điểm phù hợp
            if (p1.diemPhuHop != p2.diemPhuHop) {
                return Integer.compare(p2.diemPhuHop, p1.diemPhuHop);
            }
            
            // Cuối cùng sắp xếp theo giá
            return Double.compare(p1.phong.getLoaiPhong().getGia(), p2.phong.getLoaiPhong().getGia());
        });
        
        // Trả về danh sách phòng đã sắp xếp
        return dsPhongGoiY.stream()
                .map(pg -> pg.phong)
                .collect(Collectors.toList());
    }
    
    /**
     * Tìm tổ hợp phòng tối ưu để chứa đủ số người
     * Ưu tiên: Ít phòng nhất, tỷ lệ sử dụng cao, tổng giá thấp
     * VD: 7 người lớn, 4 trẻ em → Gợi ý: 1 phòng gia đình (8 người) + 1 phòng đôi (4 người)
     */
    public List<Phong> timToHopPhongToiUu(List<Phong> dsPhongTrong, int soNguoiLon, int soTreEm) {
        if (dsPhongTrong == null || dsPhongTrong.isEmpty()) {
            return new ArrayList<>();
        }
        
        
        List<Phong> toHopToiUu = new ArrayList<>();
        int conLaiNguoiLon = soNguoiLon;
        int conLaiTreEm = soTreEm;
        
        // Tạo bản sao và SẮP XẾP theo ưu tiên: Gia đình → Đôi → Đơn, sau đó theo sức chứa cao → thấp
        List<Phong> dsPhongConLai = new ArrayList<>(dsPhongTrong);
        
        dsPhongConLai.sort((p1, p2) -> {
            String loai1 = p1.getLoaiPhong().getTenLoaiPhong().toLowerCase().trim();
            String loai2 = p2.getLoaiPhong().getTenLoaiPhong().toLowerCase().trim();
            
            // Ưu tiên 1: Loại phòng (Gia đình > Đôi > Đơn)
            int thuTu1 = (loai1.contains("gia") && loai1.contains("đình")) || loai1.contains("family") ? 1 
                       : loai1.contains("đôi") || loai1.contains("double") ? 2 
                       : 3;
            int thuTu2 = (loai2.contains("gia") && loai2.contains("đình")) || loai2.contains("family") ? 1 
                       : loai2.contains("đôi") || loai2.contains("double") ? 2 
                       : 3;
            
            if (thuTu1 != thuTu2) {
                return Integer.compare(thuTu1, thuTu2);
            }
            
            // Ưu tiên 2: Sức chứa cao nhất (trong cùng loại)
            int sucChua1 = p1.getLoaiPhong().getSoNguoiLonToiDa() + p1.getLoaiPhong().getSoTreEmToiDa();
            int sucChua2 = p2.getLoaiPhong().getSoNguoiLonToiDa() + p2.getLoaiPhong().getSoTreEmToiDa();
            return Integer.compare(sucChua2, sucChua1); // Cao xuống thấp
        });
      
        // Thuật toán tối ưu: Chọn phòng lớn nhất phù hợp với số người còn lại
        while ((conLaiNguoiLon > 0 || conLaiTreEm > 0) && !dsPhongConLai.isEmpty()) {
            Phong phongPhuHopNhat = null;
            int phanDuNhoNhat = Integer.MAX_VALUE;
            
            // Tìm phòng có phần dư nhỏ nhất (sử dụng tối ưu nhất)
            for (Phong phong : dsPhongConLai) {
                int sucChuaNL = phong.getLoaiPhong().getSoNguoiLonToiDa();
                int sucChuaTE = phong.getLoaiPhong().getSoTreEmToiDa();
                
                // Tính số người có thể chứa
                int soNLChua = Math.min(conLaiNguoiLon, sucChuaNL);
                int soTEChua = Math.min(conLaiTreEm, sucChuaTE);
                int tongNguoiChua = soNLChua + soTEChua;
                
                // Bỏ qua phòng không chứa được ai
                if (tongNguoiChua == 0) {
                    continue;
                }
                
                // Tính phần dư (số chỗ trống sau khi chứa)
                int phanDuNL = sucChuaNL - soNLChua;
                int phanDuTE = sucChuaTE - soTEChua;
                int tongPhanDu = phanDuNL + phanDuTE;
                
                // Ưu tiên: 
                // 1. Chứa được nhiều người nhất
                // 2. Phần dư nhỏ nhất (tránh lãng phí)
                // 3. Loại phòng (Gia đình > Đôi > Đơn)
                String tenLoai = phong.getLoaiPhong().getTenLoaiPhong().toLowerCase();
                int loaiPhongPriority = (tenLoai.contains("gia") && tenLoai.contains("đình")) ? 0 
                                      : tenLoai.contains("đôi") ? 1 
                                      : 2;
                
                // Điểm tổng hợp (càng nhỏ càng tốt)
                int diem = (100 - tongNguoiChua) * 1000 + tongPhanDu * 100 + loaiPhongPriority * 10;
             
                if (diem < phanDuNhoNhat) {
                    phanDuNhoNhat = diem;
                    phongPhuHopNhat = phong;
                }
            }
            
            // Thêm phòng tốt nhất vào tổ hợp
            if (phongPhuHopNhat != null) {
                toHopToiUu.add(phongPhuHopNhat);
                dsPhongConLai.remove(phongPhuHopNhat);
                
                int sucChuaNL = phongPhuHopNhat.getLoaiPhong().getSoNguoiLonToiDa();
                int sucChuaTE = phongPhuHopNhat.getLoaiPhong().getSoTreEmToiDa();
                
                conLaiNguoiLon = Math.max(0, conLaiNguoiLon - sucChuaNL);
                conLaiTreEm = Math.max(0, conLaiTreEm - sucChuaTE);
                
         
            } else {
                break; // Không tìm thấy phòng phù hợp
            }
        }
        return toHopToiUu;
    }
    
    /**
     * Tính điểm phù hợp của phòng dựa trên nhiều tiêu chí
     */
    private int tinhDiemPhuHop(Phong phong, int soNguoiLon, int soTreEm, String loaiPhongYeuCau) {
        int diem = 0;
        
        // 1. Kiểm tra loại phòng (50 điểm)
        if (loaiPhongYeuCau != null && !loaiPhongYeuCau.equals("Tất cả")) {
            if (phong.getLoaiPhong().getTenLoaiPhong().equalsIgnoreCase(loaiPhongYeuCau)) {
                diem += 50;
            }
        } else {
            diem += 25; // Thưởng điểm nếu không yêu cầu cụ thể
        }
        
        // 2. Đánh giá sức chứa người lớn (40 điểm)
        int soNguoiLonToiDa = phong.getLoaiPhong().getSoNguoiLonToiDa();
        if (soNguoiLonToiDa == soNguoiLon) {
            diem += 40; // Vừa khít - tốt nhất
        } else if (soNguoiLonToiDa > soNguoiLon) {
            // Thừa sức chứa - trừ điểm theo số người thừa
            int soNguoiThua = soNguoiLonToiDa - soNguoiLon;
            diem += Math.max(0, 40 - (soNguoiThua * 10)); // Mỗi người thừa trừ 10 điểm
        }
        
        // 3. Đánh giá sức chứa trẻ em (30 điểm)
        int soTreEmToiDa = phong.getLoaiPhong().getSoTreEmToiDa();
        if (soTreEmToiDa == soTreEm) {
            diem += 30; // Vừa khít
        } else if (soTreEmToiDa > soTreEm) {
            int soTreEmThua = soTreEmToiDa - soTreEm;
            diem += Math.max(0, 30 - (soTreEmThua * 8)); // Mỗi trẻ em thừa trừ 8 điểm
        }
        
        // 4. Ưu tiên phòng có giá thấp hơn (20 điểm)
        // Điểm này được xử lý trong sort() bên ngoài
        double giaPhong = phong.getLoaiPhong().getGia();
        if (giaPhong < 1000000) {
            diem += 20;
        } else if (giaPhong < 2000000) {
            diem += 15;
        } else if (giaPhong < 3000000) {
            diem += 10;
        } else {
            diem += 5;
        }
        
        // 5. Thưởng điểm nếu không lãng phí quá nhiều không gian (10 điểm)
        int tongNguoi = soNguoiLon + soTreEm;
        int tongSucChua = soNguoiLonToiDa + soTreEmToiDa;
        double tyLeLayDien = (double) tongNguoi / tongSucChua;
        
        if (tyLeLayDien >= 0.8) {
            diem += 10; // Sử dụng >= 80% sức chứa
        } else if (tyLeLayDien >= 0.6) {
            diem += 7;  // Sử dụng >= 60% sức chứa
        } else if (tyLeLayDien >= 0.4) {
            diem += 4;  // Sử dụng >= 40% sức chứa
        }
        
        return diem;
    }
    
    /**
     * Class nội bộ để lưu phòng và điểm phù hợp
     */
    private static class PhongGoiY {
        Phong phong;
        int diemPhuHop;
        
        PhongGoiY(Phong phong, int diemPhuHop) {
            this.phong = phong;
            this.diemPhuHop = diemPhuHop;
        }
    }
    
    public List<Phong> locPhong(String trangThai, String loai, int tang) {
        List<Phong> ds = phong_DAO.getPhongTheoTrangThai(trangThai);

        return ds.stream()
                .filter(p -> {
                    boolean hopLoai = loai == null || loai.equalsIgnoreCase("Tất cả")
                            || p.getLoaiPhong().getTenLoaiPhong().equalsIgnoreCase(loai);
                    boolean hopTang = tang == 0 || p.getTang() == tang;
                    return hopLoai && hopTang;
                })
                .collect(Collectors.toList());
    }

}