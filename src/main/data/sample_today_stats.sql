-- Mẫu dữ liệu “hôm nay” để test màn Thống kê
-- Chỉ chèn thêm dữ liệu, không sửa/xóa dữ liệu gốc
USE Victorya_Hotel_v6;
GO

DECLARE @today DATE = CAST(GETDATE() AS DATE);
DECLARE @dateCode CHAR(8) = CONVERT(CHAR(8), @today, 112); -- yyyymmdd

-- ID động bảo đảm không trùng lặp theo ngày
DECLARE @KH1 VARCHAR(20) = 'KH-TDY-01';
DECLARE @KH2 VARCHAR(20) = 'KH-TDY-02';
DECLARE @PDP1 VARCHAR(20) = 'PDP-' + @dateCode + '-T1';
DECLARE @PDP2 VARCHAR(20) = 'PDP-' + @dateCode + '-T2';
DECLARE @HD1  VARCHAR(20) = 'HD-'  + @dateCode + '-T01';
DECLARE @HD2  VARCHAR(20) = 'HD-'  + @dateCode + '-T02';

PRINT '=== Insert KhachHang (hôm nay) ===';
INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) VALUES
(@KH1, 'TDY001' + @dateCode, N'Test KH Today A', '0900000001', 'test-a@today.dev', @today),
(@KH2, 'TDY002' + @dateCode, N'Test KH Today B', '0900000002', 'test-b@today.dev', @today);

PRINT '=== Insert PhieuDatPhong (hôm nay) ===';
INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang, trangThai, tienDatCoc) VALUES
(@PDP1, @today, @KH1, N'Đã Thanh Toán', 300000),
(@PDP2, @today, @KH2, N'Đã Thanh Toán', 500000);

PRINT '=== Insert ChiTietPhieuDatPhong (hôm nay) ===';
-- Dùng phòng thường & VIP có sẵn trong seed data
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi, trangThai) VALUES
(@PDP1, 'P-0002', DATEADD(HOUR, 10, CAST(@today AS DATETIME)), DATEADD(HOUR, 18, CAST(@today AS DATETIME)), 'LDP01', 2, N'Đã Thanh Toán'),
(@PDP2, 'P-0032', DATEADD(HOUR, 12, CAST(@today AS DATETIME)), DATEADD(HOUR, 30, CAST(@today AS DATETIME)), 'LDP02', 3, N'Đã Thanh Toán');

PRINT '=== Insert ChiTietPhieuDatPhong_DichVu (hôm nay) ===';
-- Thêm dịch vụ cho phòng VIP để có doanh thu dịch vụ
INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu, soLuong) VALUES
(@PDP2, 'P-0032', 'DV-12345', 4), -- Nước suối
(@PDP2, 'P-0032', 'DV-89012', 3); -- Ăn sáng buffet

PRINT '=== Insert HoaDon (hôm nay) ===';
INSERT INTO HoaDon (maHoaDon, ngayDat, maKhachHang, maNhanVien, maKhuyenMai, ngayTao, trangThai, tongTien) VALUES
(@HD1, DATEADD(HOUR, 10, CAST(@today AS DATETIME)), @KH1, 'NV001', 'KM-0001', DATEADD(HOUR, 20, CAST(@today AS DATETIME)), N'Đã thanh toán', 1500000),
(@HD2, DATEADD(HOUR, 12, CAST(@today AS DATETIME)), @KH2, 'NV002', NULL,      DATEADD(HOUR, 22, CAST(@today AS DATETIME)), N'Đã thanh toán', 3200000);

PRINT '=== Insert ChiTietHoaDon (hôm nay) ===';
INSERT INTO ChiTietHoaDon (maHoaDon, maPhieuDatPhong, ngayTao, tongTien) VALUES
(@HD1, @PDP1, DATEADD(HOUR, 20, CAST(@today AS DATETIME)), 1500000),
(@HD2, @PDP2, DATEADD(HOUR, 22, CAST(@today AS DATETIME)), 3200000);

-- Trigger trg_ChiTietHoaDon_AutoInsertDichVu sẽ tự copy dịch vụ sang ChiTietHoaDon_DichVu

PRINT '=== Done: dữ liệu mẫu cho thống kê hôm nay đã được thêm ===';

