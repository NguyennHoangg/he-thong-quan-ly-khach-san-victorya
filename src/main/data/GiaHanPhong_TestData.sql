USE Victorya_Hotel_v5;
GO

-- ===========================
-- DỮ LIỆU MẪU ĐỂ TEST CHỨC NĂNG GIA HẠN PHÒNG
-- ===========================
-- Script này tạo dữ liệu mẫu để test tính năng gia hạn phòng
-- Bao gồm: Khách hàng, Phiếu đặt phòng, Chi tiết phiếu đặt phòng với phòng đang ở
-- ===========================

-- Xóa dữ liệu cũ nếu có (để test lại)
-- Lưu ý: Phải xóa theo thứ tự do ràng buộc khóa ngoại
DELETE FROM ChiTietPhieuDatPhong_DichVu WHERE maPhieuDatPhong IN (SELECT maPhieuDatPhong FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong LIKE 'PDP-GH-%');
DELETE FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong LIKE 'PDP-GH-%';
DELETE FROM PhieuDatPhong WHERE maPhieuDatPhong LIKE 'PDP-GH-%';
DELETE FROM KhachHang WHERE maKhachHang LIKE 'KH-GH-%';

GO

-- ===========================
-- 1. TẠO KHÁCH HÀNG MẪU ĐỂ TEST GIA HẠN
-- ===========================
INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) VALUES
-- Khách hàng 1: Đang ở phòng, có thể gia hạn
('KH-GH-001', '999999999999', N'Nguyễn Văn Gia Hạn 1', '0999999999', 'giahan1@test.com', GETDATE()),
-- Khách hàng 2: Đang ở nhiều phòng, có thể gia hạn
('KH-GH-002', '888888888888', N'Trần Thị Gia Hạn 2', '0888888888', 'giahan2@test.com', GETDATE()),
-- Khách hàng 3: Đang ở phòng VIP, có thể gia hạn
('KH-GH-003', '777777777777', N'Lê Văn Gia Hạn 3', '0777777777', 'giahan3@test.com', GETDATE());

GO

-- ===========================
-- 2. TẠO PHIẾU ĐẶT PHÒNG MẪU
-- ===========================
INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang) VALUES
('PDP-GH-001', GETDATE(), 'KH-GH-001'),
('PDP-GH-002', GETDATE(), 'KH-GH-002'),
('PDP-GH-003', GETDATE(), 'KH-GH-002'),
('PDP-GH-004', GETDATE(), 'KH-GH-003');

GO

-- ===========================
-- 3. TẠO CHI TIẾT PHIẾU ĐẶT PHÒNG MẪU
-- Thời gian nhận phòng: Hôm qua (đã nhận)
-- Thời gian trả phòng: Hôm nay + 1 ngày (có thể gia hạn thêm)
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

-- Tự động tìm các phòng có sẵn trong database
SET @maPhongThuong1 = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP01' ORDER BY maPhong);
SET @maPhongThuong2 = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP01' AND maPhong <> ISNULL(@maPhongThuong1, '') ORDER BY maPhong);
SET @maPhongThuong3 = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP01' AND maPhong <> ISNULL(@maPhongThuong1, '') AND maPhong <> ISNULL(@maPhongThuong2, '') ORDER BY maPhong);
SET @maPhongVIP = (SELECT TOP 1 maPhong FROM Phong WHERE maLoaiPhong = 'LP02' ORDER BY maPhong);

-- Khách hàng 1: Đang ở phòng thường
-- Nhận phòng: 1 ngày trước (đảm bảo GETDATE() >= thoiGianNhanPhong), Trả phòng: 2 ngày sau (đảm bảo GETDATE() <= thoiGianTraPhong)
DECLARE @thoiGianHienTai DATETIME = GETDATE();
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi, trangThai) VALUES
('PDP-GH-001', @maPhongThuong1, 
 DATEADD(DAY, -1, @thoiGianHienTai),  -- 1 ngày trước (đảm bảo GETDATE() >= thoiGianNhanPhong)
 DATEADD(DAY, 2, @thoiGianHienTai),   -- 2 ngày sau (đảm bảo GETDATE() <= thoiGianTraPhong)
 @maLoaiDatPhong, 2, N'Đang ở');

-- Khách hàng 2: Đang ở 2 phòng
-- Phòng 1: Nhận 2 ngày trước, Trả 3 ngày sau
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi, trangThai) VALUES
('PDP-GH-002', @maPhongThuong2,
 DATEADD(DAY, -2, @thoiGianHienTai),  -- 2 ngày trước
 DATEADD(DAY, 3, @thoiGianHienTai),   -- 3 ngày sau
 @maLoaiDatPhong, 2, N'Đang ở');

-- Phòng 2: Nhận 1 ngày trước, Trả 2 ngày sau
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi, trangThai) VALUES
('PDP-GH-003', @maPhongThuong3,
 DATEADD(DAY, -1, @thoiGianHienTai),  -- 1 ngày trước (đảm bảo GETDATE() >= thoiGianNhanPhong)
 DATEADD(DAY, 2, @thoiGianHienTai),   -- 2 ngày sau (đảm bảo GETDATE() <= thoiGianTraPhong)
 @maLoaiDatPhong, 3, N'Đang ở');

-- Khách hàng 3: Đang ở phòng VIP
-- Nhận 1 ngày trước, Trả 2 ngày sau
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi, trangThai) VALUES
('PDP-GH-004', @maPhongVIP,
 DATEADD(DAY, -1, @thoiGianHienTai),  -- 1 ngày trước (đảm bảo GETDATE() >= thoiGianNhanPhong)
 DATEADD(DAY, 2, @thoiGianHienTai),   -- 2 ngày sau (đảm bảo GETDATE() <= thoiGianTraPhong)
 @maLoaiDatPhong, 4, N'Đang ở');

-- ===========================
-- 4. CẬP NHẬT TRẠNG THÁI PHÒNG THÀNH "Đang ở"
-- ===========================
UPDATE Phong SET trangThai = N'Đang ở' WHERE maPhong IN (@maPhongThuong1, @maPhongThuong2, @maPhongThuong3, @maPhongVIP);

GO

-- ===========================
-- 5. KIỂM TRA DỮ LIỆU ĐÃ TẠO VÀ TRẠNG THÁI
-- ===========================
SELECT 
    kh.CCCD,
    kh.hoTen,
    pdp.maPhieuDatPhong,
    p.soPhong,
    lp.tenLoaiPhong,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    GETDATE() as ThoiGianHienTai,
    CASE 
        WHEN GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong THEN N'Đang hoạt động'
        ELSE N'Không hoạt động'
    END as TrangThaiHoatDong,
    DATEDIFF(HOUR, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong) as SoGioO,
    ctpdp.trangThai as TrangThaiPhieu,
    p.trangThai as TrangThaiPhong
FROM KhachHang kh
INNER JOIN PhieuDatPhong pdp ON kh.maKhachHang = pdp.maKhachHang
INNER JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong
INNER JOIN Phong p ON ctpdp.maPhong = p.maPhong
INNER JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE kh.maKhachHang LIKE 'KH-GH-%'
ORDER BY kh.maKhachHang, p.soPhong;

-- Test query giống như trong DAO
PRINT '========================================';
PRINT 'KIỂM TRA QUERY TÌM PHÒNG ĐANG HOẠT ĐỘNG (giống DAO)';
PRINT '========================================';

-- LƯU Ý QUAN TRỌNG: 
-- Query trong DAO có SELECT ctpdp.maDichVu nhưng bảng ChiTietPhieuDatPhong KHÔNG có cột này!
-- Điều này sẽ gây lỗi SQL. Cần sửa DAO để bỏ cột maDichVu hoặc thêm cột vào bảng.
-- 
-- Query test dưới đây KHÔNG có maDichVu để kiểm tra xem dữ liệu có đúng không:
SELECT 
    kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email, kh.ngayTao AS ngayTaoKH,
    pdp.maPhieuDatPhong, pdp.ngayTao AS ngayTaoPDP,
    ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong, ctpdp.maLoaiDatPhong, 
    ctpdp.maPhong, ctpdp.soNguoi,
    p.soPhong, p.trangThai, p.tang, lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia,
    GETDATE() as ThoiGianHienTai,
    CASE 
        WHEN GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong THEN N'CÓ - Tìm thấy'
        ELSE N'KHÔNG - Không tìm thấy'
    END as KetQuaTimKiem
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
JOIN Phong p ON p.maPhong = ctpdp.maPhong
JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong
WHERE kh.CCCD = '999999999999'
  AND GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong
ORDER BY p.tang, p.maPhong;

-- Query kiểm tra tất cả dữ liệu (không có điều kiện BETWEEN)
PRINT '';
PRINT '========================================';
PRINT 'KIỂM TRA TẤT CẢ DỮ LIỆU (không có điều kiện BETWEEN)';
PRINT '========================================';

SELECT 
    kh.CCCD,
    kh.hoTen,
    pdp.maPhieuDatPhong,
    p.soPhong,
    lp.tenLoaiPhong,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    GETDATE() as ThoiGianHienTai,
    DATEDIFF(HOUR, ctpdp.thoiGianNhanPhong, GETDATE()) as SoGioTuKhiNhan,
    DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) as SoGioConLai,
    CASE 
        WHEN GETDATE() < ctpdp.thoiGianNhanPhong THEN N'Chưa nhận phòng'
        WHEN GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong THEN N'Đang hoạt động ✓'
        WHEN GETDATE() > ctpdp.thoiGianTraPhong THEN N'Đã quá hạn'
        ELSE N'Không xác định'
    END as TrangThaiHoatDong
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
JOIN Phong p ON ctpdp.maPhong = p.maPhong
JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE kh.CCCD IN ('999999999999', '888888888888', '777777777777')
ORDER BY kh.CCCD, p.soPhong;

GO

-- ===========================
-- HƯỚNG DẪN TEST CHỨC NĂNG GIA HẠN PHÒNG
-- ===========================
-- 
-- ⚠️ QUAN TRỌNG: VẤN ĐỀ VỚI DAO
-- Query trong ChiTietPhieuDatPhong_DAO.java (dòng 70) có:
--   SELECT ... ctpdp.maDichVu ...
-- Nhưng bảng ChiTietPhieuDatPhong KHÔNG có cột maDichVu!
-- 
-- ĐIỀU NÀY SẼ GÂY LỖI SQL VÀ KHÔNG TÌM THẤY PHÒNG!
-- 
-- GIẢI PHÁP:
-- 1. Sửa file ChiTietPhieuDatPhong_DAO.java dòng 70:
--    Xóa "ctpdp.maDichVu," khỏi SELECT statement
--    Hoặc thay bằng: "NULL as maDichVu,"
-- 
-- 2. Sau đó mới chạy script test này và test ứng dụng
-- 
-- ===========================
-- 
-- BƯỚC 1: Chạy script này trong SQL Server Management Studio
--         để tạo dữ liệu mẫu
-- 
-- BƯỚC 2: Kiểm tra query test phía trên để xác nhận dữ liệu đúng
--         (Query test không có maDichVu nên sẽ chạy được)
-- 
-- BƯỚC 3: Sửa DAO nếu cần (xem cảnh báo ở trên) - ĐÃ TỰ ĐỘNG SỬA TRONG CODE
-- 
-- BƯỚC 4: Mở ứng dụng và vào chức năng "Gia hạn phòng"
-- 
-- BƯỚC 5: Test với các CCCD sau:
--    ✅ 999999999999 
--       → Khách hàng: Nguyễn Văn Gia Hạn 1
--       → Đang ở: 1 phòng thường (P-0002)
--       → Thời gian: Nhận hôm qua 14:00, Trả ngày mai 12:00
--       
--    ✅ 888888888888
--       → Khách hàng: Trần Thị Gia Hạn 2
--       → Đang ở: 2 phòng thường (P-0003, P-0004)
--       → Phòng 1: Nhận hôm qua 10:00, Trả ngày kia 4:00
--       → Phòng 2: Nhận hôm nay 8:00, Trả ngày mai 18:00
--       
--    ✅ 777777777777
--       → Khách hàng: Lê Văn Gia Hạn 3
--       → Đang ở: 1 phòng VIP
--       → Thời gian: Nhận hôm qua 16:00, Trả ngày mai 12:00
-- 
-- BƯỚC 4: Test các tính năng:
--    ✅ Tìm kiếm phòng theo CCCD
--    ✅ Chọn nhiều phòng để gia hạn cùng lúc
--    ✅ Chọn thời gian gia hạn (ngày + giờ)
--    ✅ Kiểm tra tính toán:
--       - Giá tiền ở thêm (theo giờ)
--       - Phí cọc = 30% giá tiền ở thêm
--       - Tổng tiền = Giá tiền ở thêm + Phí cọc
--    ✅ Gia hạn thành công và cập nhật database
--    ✅ Refresh lại dữ liệu sau khi gia hạn
-- 
-- BƯỚC 5: Kiểm tra tính toán giá tiền:
--    - Phòng Thường: 100,000 VND/ngày = ~4,167 VND/giờ = ~69 VND/phút
--    - Phòng VIP: 200,000 VND/ngày = ~8,333 VND/giờ = ~139 VND/phút
--    - Phí cọc = Giá tiền ở thêm × 30%
--    - Tổng tiền = Giá tiền ở thêm + Phí cọc
-- 
-- BƯỚC 6: Xóa dữ liệu test (nếu cần):
--    DELETE FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong LIKE 'PDP-GH-%';
--    DELETE FROM PhieuDatPhong WHERE maPhieuDatPhong LIKE 'PDP-GH-%';
--    DELETE FROM KhachHang WHERE maKhachHang LIKE 'KH-GH-%';
--

GO

