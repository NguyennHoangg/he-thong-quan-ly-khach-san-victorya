-- ===========================
-- KIỂM TRA DỮ LIỆU PHÒNG
-- ===========================
USE Victorya_Hotel;
GO

PRINT N'========================================';
PRINT N'KIỂM TRA DỮ LIỆU HIỆN TẠI';
PRINT N'========================================';
PRINT '';

-- Kiểm tra Loại Phòng
PRINT N'--- LOẠI PHÒNG ---';
SELECT * FROM LoaiPhong;

PRINT '';
PRINT N'--- PHÒNG TEST ---';
-- Kiểm tra các phòng P101-P301
SELECT 
    p.maPhong,
    p.soPhong,
    p.trangThai,
    p.tang,
    lp.tenLoaiPhong,
    lp.gia
FROM Phong p
LEFT JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE p.maPhong IN ('P101', 'P102', 'P103', 'P201', 'P202', 'P301')
ORDER BY p.tang, p.maPhong;

-- Đếm số phòng tồn tại
DECLARE @countPhong INT;
SELECT @countPhong = COUNT(*) FROM Phong WHERE maPhong IN ('P101', 'P102', 'P103', 'P201', 'P202', 'P301');

PRINT '';
IF @countPhong = 0
    PRINT N'❌ KHÔNG CÓ PHÒNG NÀO TRONG SỐ P101-P301 TỒN TẠI!';
ELSE IF @countPhong < 6
    PRINT N'⚠️ CHỈ CÓ ' + CAST(@countPhong AS NVARCHAR(10)) + N'/6 PHÒNG TỒN TẠI';
ELSE
    PRINT N'✅ ĐỦ 6 PHÒNG ĐÃ TỒN TẠI';

PRINT '';
PRINT N'--- LOẠI ĐẶT PHÒNG ---';
SELECT * FROM LoaiDatPhong;

GO

