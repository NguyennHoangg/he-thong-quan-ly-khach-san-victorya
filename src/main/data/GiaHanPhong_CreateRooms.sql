-- ===========================
-- TẠO PHÒNG TEST CHO GIA HẠN PHÒNG
-- ===========================
-- Script này CHỈ tạo loại phòng và phòng test
-- Chạy script này TRƯỚC khi chạy GiaHanPhong_TestData.sql

USE Victorya_Hotel;
GO

PRINT N'========================================';
PRINT N'BẮT ĐẦU TẠO PHÒNG TEST GIA HẠN';
PRINT N'========================================';
PRINT '';

-- ===========================
-- 1. TẠO LOẠI PHÒNG
-- ===========================

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
-- 2. TẠO PHÒNG TẦNG 4
-- ===========================

-- Phòng P401 (Standard)
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P401')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P401', N'401', N'Sẵn sàng', 'LP_STD', 4);
    PRINT N'✅ Đã tạo phòng P401 (Tầng 4 - Standard)';
END
ELSE
BEGIN
    PRINT N'⚠️ Phòng P401 đã tồn tại';
END

-- Phòng P402 (VIP)
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P402')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P402', N'402', N'Sẵn sàng', 'LP_VIP', 4);
    PRINT N'✅ Đã tạo phòng P402 (Tầng 4 - VIP)';
END
ELSE
BEGIN
    PRINT N'⚠️ Phòng P402 đã tồn tại';
END

-- Phòng P403 (VIP)
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P403')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P403', N'403', N'Sẵn sàng', 'LP_VIP', 4);
    PRINT N'✅ Đã tạo phòng P403 (Tầng 4 - VIP)';
END
ELSE
BEGIN
    PRINT N'⚠️ Phòng P403 đã tồn tại';
END

PRINT '';

-- ===========================
-- 3. TẠO LOẠI ĐẶT PHÒNG
-- ===========================

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

PRINT N'--- LOẠI PHÒNG ---';
SELECT maLoaiPhong, tenLoaiPhong, gia FROM LoaiPhong WHERE maLoaiPhong IN ('LP_STD', 'LP_VIP');

PRINT '';
PRINT N'--- PHÒNG TẦNG 4 ---';
SELECT 
    p.maPhong,
    p.soPhong,
    p.trangThai,
    p.tang,
    lp.tenLoaiPhong,
    lp.gia
FROM Phong p
JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE p.maPhong IN ('P401', 'P402', 'P403')
ORDER BY p.maPhong;

PRINT '';
PRINT N'--- LOẠI ĐẶT PHÒNG ---';
SELECT maLoaiDatPhong, tenLoaiDatPhong FROM LoaiDatPhong WHERE maLoaiDatPhong = 'LDP_NGAY';

PRINT '';
PRINT N'========================================';
PRINT N'HOÀN TẤT TẠO PHÒNG TEST!';
PRINT N'========================================';
PRINT N'';
PRINT N'✅ Bây giờ bạn có thể chạy GiaHanPhong_TestData.sql';
PRINT N'   để tạo khách hàng và phiếu đặt phòng test';

GO

