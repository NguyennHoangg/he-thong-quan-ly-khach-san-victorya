-- ===========================
-- TẠO PHÒNG TEST CHO NHẬN PHÒNG
-- ===========================
-- Script này CHỈ tạo loại phòng và phòng test
-- Chạy script này TRƯỚC khi chạy NhanPhong_TestData.sql

USE Victorya_Hotel;
GO

PRINT N'========================================';
PRINT N'BẮT ĐẦU TẠO PHÒNG TEST';
PRINT N'========================================';
PRINT '';

-- ===========================
-- 1. TẠO LOẠI PHÒNG
-- ===========================

-- Tạo loại phòng Standard nếu chưa có
IF NOT EXISTS (SELECT 1 FROM LoaiPhong WHERE maLoaiPhong = 'LP_STD')
BEGIN
    INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao)
    VALUES ('LP_STD', N'Standard', 500000.00, CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại phòng Standard (500,000 VND)';
END
ELSE
BEGIN
    PRINT N'⚠️ Loại phòng Standard đã tồn tại';
END

-- Tạo loại phòng VIP nếu chưa có
IF NOT EXISTS (SELECT 1 FROM LoaiPhong WHERE maLoaiPhong = 'LP_VIP')
BEGIN
    INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao)
    VALUES ('LP_VIP', N'VIP', 1000000.00, CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại phòng VIP (1,000,000 VND)';
END
ELSE
BEGIN
    PRINT N'⚠️ Loại phòng VIP đã tồn tại';
END

PRINT '';

-- ===========================
-- 2. TẠO CÁC PHÒNG TEST
-- ===========================

-- Tạo phòng P101 (Tầng 1 - Standard)
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P101')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P101', N'101', N'Sẵn sàng', 'LP_STD', 1);
    PRINT N'✅ Đã tạo phòng P101 (Tầng 1 - Standard)';
END
ELSE
BEGIN
    PRINT N'⚠️ Phòng P101 đã tồn tại';
END

-- Tạo phòng P102 (Tầng 1 - Standard)
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P102')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P102', N'102', N'Sẵn sàng', 'LP_STD', 1);
    PRINT N'✅ Đã tạo phòng P102 (Tầng 1 - Standard)';
END
ELSE
BEGIN
    PRINT N'⚠️ Phòng P102 đã tồn tại';
END

-- Tạo phòng P103 (Tầng 1 - Standard)
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P103')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P103', N'103', N'Sẵn sàng', 'LP_STD', 1);
    PRINT N'✅ Đã tạo phòng P103 (Tầng 1 - Standard)';
END
ELSE
BEGIN
    PRINT N'⚠️ Phòng P103 đã tồn tại';
END

-- Tạo phòng P201 (Tầng 2 - VIP)
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P201')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P201', N'201', N'Sẵn sàng', 'LP_VIP', 2);
    PRINT N'✅ Đã tạo phòng P201 (Tầng 2 - VIP)';
END
ELSE
BEGIN
    PRINT N'⚠️ Phòng P201 đã tồn tại';
END

-- Tạo phòng P202 (Tầng 2 - VIP)
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P202')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P202', N'202', N'Sẵn sàng', 'LP_VIP', 2);
    PRINT N'✅ Đã tạo phòng P202 (Tầng 2 - VIP)';
END
ELSE
BEGIN
    PRINT N'⚠️ Phòng P202 đã tồn tại';
END

-- Tạo phòng P301 (Tầng 3 - VIP)
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P301')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P301', N'301', N'Sẵn sàng', 'LP_VIP', 3);
    PRINT N'✅ Đã tạo phòng P301 (Tầng 3 - VIP)';
END
ELSE
BEGIN
    PRINT N'⚠️ Phòng P301 đã tồn tại';
END

PRINT '';

-- ===========================
-- 3. TẠO LOẠI ĐẶT PHÒNG
-- ===========================

-- Tạo loại đặt phòng "Theo ngày"
IF NOT EXISTS (SELECT 1 FROM LoaiDatPhong WHERE maLoaiDatPhong = 'LDP_NGAY')
BEGIN
    INSERT INTO LoaiDatPhong (maLoaiDatPhong, tenLoaiDatPhong, ngayTao)
    VALUES ('LDP_NGAY', N'Theo ngày', CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại đặt phòng "Theo ngày"';
END
ELSE
BEGIN
    PRINT N'⚠️ Loại đặt phòng "Theo ngày" đã tồn tại';
END

PRINT '';

-- ===========================
-- 4. KIỂM TRA KẾT QUẢ
-- ===========================

PRINT N'========================================';
PRINT N'KIỂM TRA DỮ LIỆU VỪA TẠO';
PRINT N'========================================';
PRINT '';

-- Kiểm tra loại phòng
PRINT N'--- LOẠI PHÒNG ---';
SELECT maLoaiPhong, tenLoaiPhong, gia FROM LoaiPhong WHERE maLoaiPhong IN ('LP_STD', 'LP_VIP');

PRINT '';
PRINT N'--- PHÒNG TEST ---';
-- Kiểm tra phòng
SELECT 
    p.maPhong,
    p.soPhong,
    p.trangThai,
    p.tang,
    lp.tenLoaiPhong,
    lp.gia
FROM Phong p
JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE p.maPhong IN ('P101', 'P102', 'P103', 'P201', 'P202', 'P301')
ORDER BY p.tang, p.maPhong;

PRINT '';
PRINT N'--- LOẠI ĐẶT PHÒNG ---';
SELECT maLoaiDatPhong, tenLoaiDatPhong FROM LoaiDatPhong WHERE maLoaiDatPhong = 'LDP_NGAY';

PRINT '';
PRINT N'========================================';
PRINT N'HOÀN TẤT TẠO PHÒNG TEST!';
PRINT N'========================================';
PRINT N'';
PRINT N'✅ Bây giờ bạn có thể chạy NhanPhong_TestData.sql';
PRINT N'   để tạo khách hàng và phiếu đặt phòng test';

GO

