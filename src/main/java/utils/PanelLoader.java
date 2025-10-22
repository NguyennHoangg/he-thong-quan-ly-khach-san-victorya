package utils;

import javafx.scene.layout.BorderPane;
import view.*;

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
    private BorderPane panelKhuyenMai;
    private BorderPane panelTaiKhoan;
    private BorderPane panelCauHinh;
    private BorderPane panelQuanLiPhong;
    private BorderPane panelQuanLiNhanVien;
    private BorderPane panelQuanLiDichVu;
    private BorderPane panelThanhToan;

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
                    panelTimKiem = new TimKiemPhong();
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

    public BorderPane getPanelTrangChu() {
        if (panelTrangChu == null) {
            panelTrangChu = new BorderPane();
        }
        return panelTrangChu;
    }

    public BorderPane getPanelTimKiem() {
        if (panelTimKiem == null) {
            panelTimKiem = new TimKiemPhong();
        }
        return panelTimKiem;
    }

    public BorderPane getPanelDatPhong() {
        if (panelDatPhong == null) {
            panelDatPhong = new DatPhong();
        }
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

    public BorderPane getPanelGiaHanPhong() {
        if (panelGiaHanPhong == null) {
            panelGiaHanPhong = new GiaHanPhong_GUI();
        }
        return panelGiaHanPhong;
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

    public BorderPane getPanelCauHinh() {
        if (panelCauHinh == null) {
            panelCauHinh = new CaiDatHeThong_GUI();
        }
        return panelCauHinh;
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

    public BorderPane getPanelThanhToan(){
        if(panelThanhToan == null){
            panelThanhToan = new ThanhToan_GUI();
        }
        return panelThanhToan;
    }

    /**
     * Clear cache để giải phóng bộ nhớ khi cần
     */
    public void clearCache() {
        panelTrangChu = null;
        panelTimKiem = null;
        panelDatPhong = null;
        panelDoiPhong = null;
        panelHuyPhong = null;
        panelGiaHanPhong = null;
        panelKhuyenMai = null;
        panelTaiKhoan = null;
        panelCauHinh = null;
        panelQuanLiPhong = null;
        panelQuanLiNhanVien = null;
        panelQuanLiDichVu = null;
    }
}
