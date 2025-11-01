USE Victorya_Hotel_v5;
GO

-- ===========================
-- DỮ LIỆU MẪU ĐỂ TEST CHỨC NĂNG NHẬN PHÒNG (CHECK-IN)
-- ===========================
-- Script này tạo dữ liệu mẫu để test tính năng nhận phòng
-- Bao gồm: Khách hàng, Phiếu đặt phòng, Chi tiết phiếu đặt phòng với phòng chưa nhận
-- Điều kiện: thoiGianNhanPhong IS NULL, trangThai = N'Đã đặt'
-- ===========================

-- Xóa dữ liệu cũ nếu có (để test lại)
-- Lưu ý: Phải xóa theo thứ tự do ràng buộc khóa ngoại
DELETE FROM ChiTietPhieuDatPhong_DichVu WHERE maPhieuDatPhong IN (SELECT maPhieuDatPhong FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong LIKE 'PDP-NP-%');
DELETE FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong LIKE 'PDP-NP-%';
DELETE FROM PhieuDatPhong WHERE maPhieuDatPhong LIKE 'PDP-NP-%';
DELETE FROM KhachHang WHERE maKhachHang LIKE 'KH-NP-%';

GO

-- ===========================
-- 1. TẠO KHÁCH HÀNG MẪU ĐỂ TEST NHẬN PHÒNG
-- ===========================
INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) VALUES
-- Khách hàng 1: Đã đặt phòng thường, chưa nhận
('KH-NP-001', '111111111111', N'Nguyễn Văn Nhận Phòng 1', '0111111111', 'nhanphong1@test.com', GETDATE()),
-- Khách hàng 2: Đã đặt nhiều phòng, chưa nhận
('KH-NP-002', '222222222222', N'Trần Thị Nhận Phòng 2', '0222222222', 'nhanphong2@test.com', GETDATE()),
-- Khách hàng 3: Đã đặt phòng VIP, chưa nhận
('KH-NP-003', '333333333333', N'Lê Văn Nhận Phòng 3', '0333333333', 'nhanphong3@test.com', GETDATE());

GO

-- ===========================
-- 2. TẠO PHIẾU ĐẶT PHÒNG MẪU
-- ===========================
INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang) VALUES
('PDP-NP-001', GETDATE(), 'KH-NP-001'),
('PDP-NP-002', GETDATE(), 'KH-NP-002'),
('PDP-NP-003', GETDATE(), 'KH-NP-002'),
('PDP-NP-004', GETDATE(), 'KH-NP-003');

GO

-- ===========================
-- 3. TẠO CHI TIẾT PHIẾU ĐẶT PHÒNG MẪU
-- Thời gian nhận phòng: NULL (chưa nhận)
-- Thời gian trả phòng: Trong tương lai (đã đặt)
-- Trạng thái: "Đã đặt" (không phải "Đang ở")
-- ===========================

-- Lấy thông tin phòng và loại đặt phòng có sẵn
DECLARE @maLoaiDatPhong VARCHAR(20);
DECLARE @maPhongThuong1 VARCHAR(20);
DECLARE @maPhongThuong2 VARCHAR(20);
DECLARE @maPhongThuong3 VARCHAR(20);
DECLARE @maPhongVIP VARCHAR(20);

-- Lấy loại đặt phòng
SET @maLoaiDatPhong = (SELECT TOP 1 maLoaiDatPhong FROM LoaiDatPhong WHERE tenLoaiDatPhong = N'Offline');
IF @maLoaiDatPhong IS NULL
    SET @maLoaiDatPhong = (SELECT TOP 1 maLoaiDatPhong FROM LoaiDatPhong);

-- Tự động tìm các phòng trống hoặc có thể đặt trong database
-- Chọn các phòng có trạng thái "Trống" hoặc có thể đặt
SET @maPhongThuong1 = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP01' AND (trangThai = N'Trống' OR trangThai = N'Đã đặt') ORDER BY maPhong);
SET @maPhongThuong2 = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP01' AND maPhong <> ISNULL(@maPhongThuong1, '') AND (trangThai = N'Trống' OR trangThai = N'Đã đặt') ORDER BY maPhong);
SET @maPhongThuong3 = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP01' AND maPhong <> ISNULL(@maPhongThuong1, '') AND maPhong <> ISNULL(@maPhongThuong2, '') AND (trangThai = N'Trống' OR trangThai = N'Đã đặt') ORDER BY maPhong);
SET @maPhongVIP = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP02' AND (trangThai = N'Trống' OR trangThai = N'Đã đặt') ORDER BY maPhong);

-- Nếu không tìm thấy phòng, tạo mới hoặc dùng phòng đầu tiên
IF @maPhongThuong1 IS NULL
    SET @maPhongThuong1 = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP01' ORDER BY maPhong);
IF @maPhongThuong2 IS NULL
    SET @maPhongThuong2 = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP01' AND maPhong <> ISNULL(@maPhongThuong1, '') ORDER BY maPhong);
IF @maPhongThuong3 IS NULL
    SET @maPhongThuong3 = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP01' AND maPhong <> ISNULL(@maPhongThuong1, '') AND maPhong <> ISNULL(@maPhongThuong2, '') ORDER BY maPhong);
IF @maPhongVIP IS NULL
    SET @maPhongVIP = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP02' ORDER BY maPhong);

-- Khách hàng 1: Đã đặt phòng thường, chưa nhận
-- thoiGianNhanPhong = NULL (chưa nhận), thoiGianTraPhong = 2 ngày sau
DECLARE @thoiGianHienTai DATETIME = GETDATE();
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi) VALUES
('PDP-NP-001', @maPhongThuong1, 
 NULL,  -- Chưa nhận phòng
 DATEADD(DAY, 2, @thoiGianHienTai),   -- Trả phòng 2 ngày sau
 @maLoaiDatPhong, 2);

-- Khách hàng 2: Đã đặt 2 phòng, chưa nhận
-- Phòng 1: Trả phòng 3 ngày sau
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi) VALUES
('PDP-NP-002', @maPhongThuong2,
 NULL,  -- Chưa nhận phòng
 DATEADD(DAY, 3, @thoiGianHienTai),   -- Trả phòng 3 ngày sau
 @maLoaiDatPhong, 2);

-- Phòng 2: Trả phòng 2 ngày sau
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi) VALUES
('PDP-NP-003', @maPhongThuong3,
 NULL,  -- Chưa nhận phòng
 DATEADD(DAY, 2, @thoiGianHienTai),   -- Trả phòng 2 ngày sau
 @maLoaiDatPhong, 3);

-- Khách hàng 3: Đã đặt phòng VIP, chưa nhận
-- Trả phòng 2 ngày sau
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi) VALUES
('PDP-NP-004', @maPhongVIP,
 NULL,  -- Chưa nhận phòng
 DATEADD(DAY, 2, @thoiGianHienTai),   -- Trả phòng 2 ngày sau
 @maLoaiDatPhong, 4);

GO

-- ===========================
-- 4. CẬP NHẬT TRẠNG THÁI PHÒNG THÀNH "Đã đặt"
-- ===========================
-- Cập nhật trạng thái phòng thành "Đã đặt" cho các phòng trong chi tiết phiếu đặt phòng
UPDATE Phong 
SET trangThai = N'Đã đặt' 
WHERE maPhong IN (
    SELECT DISTINCT maPhong 
    FROM ChiTietPhieuDatPhong 
    WHERE maPhieuDatPhong LIKE 'PDP-NP-%'
);

GO

-- ===========================
-- 5. KIỂM TRA DỮ LIỆU ĐÃ TẠO
-- ===========================
PRINT '========================================';
PRINT 'KIỂM TRA DỮ LIỆU NHẬN PHÒNG ĐÃ TẠO';
PRINT '========================================';

SELECT 
    kh.CCCD,
    kh.hoTen,
    kh.soDienThoai,
    kh.email,
    pdp.maPhieuDatPhong,
    pdp.ngayTao as NgayTaoPhieu,
    p.soPhong,
    lp.tenLoaiPhong,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    ctpdp.soNguoi,
    CASE 
        WHEN ctpdp.thoiGianNhanPhong IS NULL THEN N'Chưa nhận'
        ELSE N'Đã nhận'
    END as TrangThaiNhan,
    p.trangThai as TrangThaiPhong,
    DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) as SoGioConLai
FROM KhachHang kh
INNER JOIN PhieuDatPhong pdp ON kh.maKhachHang = pdp.maKhachHang
INNER JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong
INNER JOIN Phong p ON ctpdp.maPhong = p.maPhong
INNER JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE kh.maKhachHang LIKE 'KH-NP-%'
ORDER BY kh.maKhachHang, p.soPhong;

PRINT '';
PRINT '========================================';
PRINT 'KIỂM TRA QUERY TÌM PHÒNG CHƯA NHẬN (giống DAO)';
PRINT '========================================';

-- Query test giống như trong PhieuDatPhong_DAO.getPhieuDatPhongChuaNhanTheoCCCD
SELECT 
    kh.CCCD,
    kh.hoTen,
    pdp.maPhieuDatPhong,
    p.soPhong,
    lp.tenLoaiPhong,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    p.trangThai as TrangThaiPhong
FROM PhieuDatPhong pdp
JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong
JOIN Phong p ON p.maPhong = ctpdp.maPhong
JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong
WHERE kh.CCCD = '111111111111' 
  AND p.trangThai = N'Đã đặt' 
  AND ctpdp.thoiGianNhanPhong IS NULL
ORDER BY pdp.ngayTao DESC;

PRINT '';
PRINT '========================================';
PRINT 'HƯỚNG DẪN TEST';
PRINT '========================================';
PRINT '1. Mở trang Nhận phòng';
PRINT '2. Nhập CCCD: 111111111111, 222222222222, hoặc 333333333333';
PRINT '3. Click "Tìm kiếm"';
PRINT '4. Kiểm tra danh sách phòng chưa nhận';
PRINT '5. Click "XÁC NHẬN NHẬN PHÒNG" để nhận phòng';
PRINT '';
PRINT 'Sau khi nhận phòng:';
PRINT '- thoiGianNhanPhong sẽ được cập nhật = GETDATE()';
PRINT '- Trạng thái phòng sẽ thay đổi từ "Đã đặt" thành "Đang ở"';
PRINT '';
PRINT '========================================';
PRINT 'HOÀN THÀNH TẠO DỮ LIỆU MẪU';
PRINT '========================================';

