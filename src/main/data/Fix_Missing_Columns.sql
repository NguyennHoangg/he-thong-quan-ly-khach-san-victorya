-- ===========================
-- FIX SCHEMA: Thêm cột soNguoiLonToiDa và soTreEmToiDa
-- ===========================

USE Victorya_Hotel_v6;
GO

-- Kiểm tra cột đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'LoaiPhong' AND COLUMN_NAME = 'soNguoiLonToiDa')
BEGIN
    PRINT 'Adding column soNguoiLonToiDa to LoaiPhong...';
    ALTER TABLE LoaiPhong
    ADD soNguoiLonToiDa INT DEFAULT 2;
    PRINT '✅ Added soNguoiLonToiDa';
END
ELSE
BEGIN
    PRINT 'Column soNguoiLonToiDa already exists.';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'LoaiPhong' AND COLUMN_NAME = 'soTreEmToiDa')
BEGIN
    PRINT 'Adding column soTreEmToiDa to LoaiPhong...';
    ALTER TABLE LoaiPhong
    ADD soTreEmToiDa INT DEFAULT 1;
    PRINT '✅ Added soTreEmToiDa';
END
ELSE
BEGIN
    PRINT 'Column soTreEmToiDa already exists.';
END
GO

-- Cập nhật giá trị mặc định cho các loại phòng
UPDATE LoaiPhong SET soNguoiLonToiDa = 2, soTreEmToiDa = 1 WHERE maLoaiPhong = 'LP01'; -- Thường
UPDATE LoaiPhong SET soNguoiLonToiDa = 2, soTreEmToiDa = 1 WHERE maLoaiPhong = 'LP02'; -- VIP
UPDATE LoaiPhong SET soNguoiLonToiDa = 3, soTreEmToiDa = 1 WHERE maLoaiPhong = 'LP03'; -- Superior
UPDATE LoaiPhong SET soNguoiLonToiDa = 4, soTreEmToiDa = 2 WHERE maLoaiPhong = 'LP04'; -- Deluxe
UPDATE LoaiPhong SET soNguoiLonToiDa = 6, soTreEmToiDa = 2 WHERE maLoaiPhong = 'LP05'; -- Suite
GO

-- Kiểm tra
SELECT * FROM LoaiPhong;
GO

PRINT '';
PRINT '===== HOÀN THÀNH FIX SCHEMA =====';
PRINT 'Đã thêm 2 cột:';
PRINT '  - soNguoiLonToiDa (INT)';
PRINT '  - soTreEmToiDa (INT)';
PRINT '';
PRINT 'Giờ chạy lại app và test!';
GO

