package utils;

import javafx.scene.layout.BorderPane;
import view.*;
import view.Phong.DatPhong;
import view.Phong.DoiPhong_GUI;
import view.Phong.GiaHanPhong_GUI;
import view.Phong.HuyPhong_GUI;
import view.Phong.NhanPhong_GUI;
import view.QuanLy.QuanLiDichVu_GUI;
import view.QuanLy.QuanLiNhanVien_GUI;
import view.QuanLy.QuanLiPhong_GUI;

/**
 * Utility class để quản lý lazy loading và preloading các panels
 * Tối ưu hiệu suất khởi động ứng dụng bằng cách load song song các màn hình
 * quan trọng
 */
public class PanelLoader {
    // Cache các panels đã load
    private BorderPane panelTrangChu;
    private BorderPane panelTimKiem;
    private BorderPane panelDatPhong;
    private BorderPane panelDoiPhong;
    private BorderPane panelHuyPhong;
    private BorderPane panelGiaHanPhong;
    private BorderPane panelNhanPhong;
    private BorderPane panelKhuyenMai;
    private BorderPane panelTaiKhoan;
    private BorderPane panelQuanLiPhong;
    private BorderPane panelQuanLiNhanVien;
    private BorderPane panelQuanLiDichVu;
    private BorderPane panelQuanLiKhachHang;
    private BorderPane panelThanhToan;
    private BorderPane pannelQuanLiHoaDon;
    private BorderPane panelThongKe;
    private BorderPane panelCaLamViec;
    private BorderPane panelTraPhong;

    private static PanelLoader instance;

    // Progress callback interface
    public interface ProgressCallback {
        void onProgress(double progress, String message);
    }

    private PanelLoader() {
        // Private constructor for singleton
    }

    /**
     * Singleton instance
     */
    public static PanelLoader getInstance() {
        if (instance == null) {
            instance = new PanelLoader();
        }
        return instance;
    }

    /**
     * Pre-load 5 màn hình phòng song song trong background với progress callback
     */
    public void preloadRoomPanels(ProgressCallback callback) {
        Thread preloadThread = new Thread(() -> {
            try {

                final int totalPanels = 5;
                final double progressPerPanel = 0.3 / totalPanels; // 30% total progress cho 5 panels
                double currentProgress = 0.6; // Bắt đầu từ 60% (sau SVG icons)

                // Load song song 5 panels phòng
                Thread t1 = new Thread(() -> {
                    panelTimKiem = new DashBoard_GUI();
                }, "Preload-TimKiem");

                Thread t2 = new Thread(() -> {
                    panelDatPhong = new DatPhong();
                }, "Preload-DatPhong");

                Thread t3 = new Thread(() -> {
                    panelDoiPhong = new DoiPhong_GUI();
                }, "Preload-DoiPhong");

                Thread t4 = new Thread(() -> {
                    panelHuyPhong = new HuyPhong_GUI();
                }, "Preload-HuyPhong");

                Thread t5 = new Thread(() -> {
                    panelGiaHanPhong = new GiaHanPhong_GUI();
                }, "Preload-GiaHan");

                // Start tất cả threads
                t1.start();
                if (callback != null)
                    callback.onProgress(currentProgress += progressPerPanel, "Đang tải Tìm kiếm phòng...");

                t3.start();
                if (callback != null)
                    callback.onProgress(currentProgress += progressPerPanel, "Đang tải Đổi phòng...");

                t2.start();
                if (callback != null)
                    callback.onProgress(currentProgress += progressPerPanel, "Đang tải Đặt phòng...");

                t4.start();
                if (callback != null)
                    callback.onProgress(currentProgress += progressPerPanel, "Đang tải Hủy phòng...");

                t5.start();
                if (callback != null)
                    callback.onProgress(currentProgress += progressPerPanel, "Đang tải Gia hạn phòng...");

                // Đợi tất cả load xong
                t3.join();
                t1.join();
                t2.join();
                t4.join();
                t5.join();

                if (callback != null)
                    callback.onProgress(0.9, "Hoàn tất tải màn hình...");

            } catch (Exception e) {
                System.err.println("❌ Lỗi khi pre-load panels: " + e.getMessage());
                e.printStackTrace();
            }
        }, "PanelPreloader");

        preloadThread.setDaemon(true);
        preloadThread.start();
    }

    // === GETTERS với lazy loading ===

    public BorderPane getPaneTranggChu(){
        if(panelTrangChu == null){
            panelTrangChu = new DashBoard_GUI();
        }
        return panelTrangChu;
    }

    

  
    
    public BorderPane getPanelTimKiem() {
        if (panelTimKiem == null) {
            panelTimKiem = new DatPhong();
        }
        return panelTimKiem;
    }

    public BorderPane getPaneCaLamViec(){
        if(panelCaLamViec == null){
            panelCaLamViec = new CaLamViec_GUI();
        }
        return panelCaLamViec;
    }

    public BorderPane getPanelDatPhong() {
        if (panelDatPhong == null) {
            panelDatPhong = new DatPhong();
        }
        return panelDatPhong;
    }
    
    public BorderPane getPanelDatPhong(CaLamViec_GUI caLamViecGUI) {
        // Tạo mới với ca làm việc
        panelDatPhong = new DatPhong(caLamViecGUI);
        return panelDatPhong;
    }

    public BorderPane getPanelDoiPhong() {
        if (panelDoiPhong == null) {
            panelDoiPhong = new DoiPhong_GUI();
        }
        return panelDoiPhong;
    }

    public BorderPane getPanelHuyPhong() {
        if (panelHuyPhong == null) {
            panelHuyPhong = new HuyPhong_GUI();
        }
        return panelHuyPhong;
    }
    
    public BorderPane getPanelHuyPhong(CaLamViec_GUI caLamViecGUI) {
        // Tạo mới với ca làm việc
        panelHuyPhong = new HuyPhong_GUI(caLamViecGUI);
        return panelHuyPhong;
    }

    public BorderPane getPanelGiaHanPhong() {
        if (panelGiaHanPhong == null) {
            panelGiaHanPhong = new GiaHanPhong_GUI();
        }
        return panelGiaHanPhong;
    }

    public BorderPane getPanelNhanPhong() {
        if (panelNhanPhong == null) {
            panelNhanPhong = new NhanPhong_GUI();
        }
        return panelNhanPhong;
    }

    public BorderPane getPanelKhuyenMai() {
        if (panelKhuyenMai == null) {
            panelKhuyenMai = new KhuyenMai_GUI();
        }
        return panelKhuyenMai;
    }

    public BorderPane getPanelTaiKhoan() {
        if (panelTaiKhoan == null) {
            panelTaiKhoan = new TaiKhoan_GUI();
        }
        return panelTaiKhoan;
    }

    // Overloaded method để truyền thông tin người dùng
    public BorderPane getPanelTaiKhoan(String tenDangNhap) {
        // Tạo mới mỗi lần để load thông tin user mới nhất
        panelTaiKhoan = new TaiKhoan_GUI(tenDangNhap);
        return panelTaiKhoan;
    }

    // Overloaded method để truyền thông tin người dùng từ NhanVien
    public BorderPane getPanelTaiKhoan(model.TaiKhoan taiKhoan, model.NhanVien nhanVien) {
        String tenDangNhap = taiKhoan != null ? taiKhoan.getTenDangNhap()
                : (nhanVien != null && nhanVien.getTaiKhoan() != null ? nhanVien.getTaiKhoan().getTenDangNhap() : null);
        panelTaiKhoan = new TaiKhoan_GUI(tenDangNhap);
        return panelTaiKhoan;
    }

    public BorderPane getPanelQuanLiPhong() {
        if (panelQuanLiPhong == null) {
            panelQuanLiPhong = new QuanLiPhong_GUI();
        }
        return panelQuanLiPhong;
    }

    public BorderPane getPanelQuanLiNhanVien() {
        if (panelQuanLiNhanVien == null) {
            panelQuanLiNhanVien = new QuanLiNhanVien_GUI();
        }
        return panelQuanLiNhanVien;
    }

    public BorderPane getPanelQuanLiDichVu() {
        if (panelQuanLiDichVu == null) {
            panelQuanLiDichVu = new QuanLiDichVu_GUI();
        }
        return panelQuanLiDichVu;
    }

    public BorderPane getPanelQuanLiKhachHang() {
        if (panelQuanLiKhachHang == null) {
            panelQuanLiKhachHang = new QuanLiKhachHang_GUI();
        }
        return panelQuanLiKhachHang;
    }

    public BorderPane getPanelThanhToan() {
        if (panelThanhToan == null) {
            panelThanhToan = new ThanhToan_GUI();
        }
        return panelThanhToan;
    }
    
    public BorderPane getPanelThanhToan(CaLamViec_GUI caLamViecGUI) {
        // Tạo mới với ca làm việc
        panelThanhToan = new ThanhToan_GUI(caLamViecGUI);
        return panelThanhToan;
        }
    

    /**
     * Clear cache của trang thanh toán để tạo instance mới
     */
    public void clearThanhToanCache() {
        if (panelThanhToan instanceof ThanhToan_GUI) {
            ((ThanhToan_GUI) panelThanhToan).cleanup();
        }
        panelThanhToan = null;
    }
    public BorderPane getPanelQuanLyHoaDon(){
        if(pannelQuanLiHoaDon == null){
            pannelQuanLiHoaDon = new QuanLiHoaDon_GUI();
        }
        return pannelQuanLiHoaDon;
    }

    public BorderPane getPanelThongKe() {
        if (panelThongKe == null) {
            panelThongKe = new ThongKe_GUI();
        }
        return panelThongKe;
    }
    
    public BorderPane getPanelTrangChu() {
        if (panelTrangChu == null) {
            panelTrangChu = new CaLamViec_GUI();
        }
        return panelTrangChu;
    }

    /**
     * Clear cache để giải phóng bộ nhớ khi cần
     */
    public void clearCache() {
        // Cleanup thanh toán trước khi clear
        if (panelThanhToan instanceof ThanhToan_GUI) {
            ((ThanhToan_GUI) panelThanhToan).cleanup();
        }
        

        panelTrangChu = null;
        panelTimKiem = null;
        panelDatPhong = null;
        panelDoiPhong = null;
        panelHuyPhong = null;
        panelGiaHanPhong = null;
        panelNhanPhong = null;
        panelKhuyenMai = null;
        panelTaiKhoan = null;
        panelQuanLiPhong = null;
        panelQuanLiNhanVien = null;
        panelQuanLiDichVu = null;
        panelQuanLiKhachHang = null;
        panelThanhToan = null;
        pannelQuanLiHoaDon = null;
        panelCaLamViec = null;
        panelTraPhong = null;
    }
}