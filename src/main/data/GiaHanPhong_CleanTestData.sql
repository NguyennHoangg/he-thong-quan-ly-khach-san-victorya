-- ===========================
-- XÓA DỮ LIỆU TEST CHO GIA HẠN PHÒNG
-- ===========================
-- File này xóa tất cả dữ liệu test đã tạo cho chức năng gia hạn phòng
-- Chạy file này khi muốn làm sạch database sau khi test

USE Victorya_Hotel;
GO

PRINT N'========================================';
PRINT N'BẮT ĐẦU XÓA DỮ LIỆU TEST GIA HẠN PHÒNG';
PRINT N'========================================';
PRINT '';

-- ===========================
-- 1. XÓA CHI TIẾT PHIẾU ĐẶT PHÒNG
-- ===========================

DELETE FROM ChiTietPhieuDatPhong 
WHERE maPhieuDatPhong IN ('PDP_GIAHAN001', 'PDP_GIAHAN002');

IF @@ROWCOUNT > 0
    PRINT N'✅ Đã xóa ' + CAST(@@ROWCOUNT AS NVARCHAR(10)) + N' chi tiết phiếu đặt phòng test';
ELSE
    PRINT N'⚠️ Không tìm thấy chi tiết phiếu đặt phòng test để xóa';

-- ===========================
-- 2. XÓA PHIẾU ĐẶT PHÒNG
-- ===========================

DELETE FROM PhieuDatPhong 
WHERE maPhieuDatPhong IN ('PDP_GIAHAN001', 'PDP_GIAHAN002');

IF @@ROWCOUNT > 0
    PRINT N'✅ Đã xóa ' + CAST(@@ROWCOUNT AS NVARCHAR(10)) + N' phiếu đặt phòng test';
ELSE
    PRINT N'⚠️ Không tìm thấy phiếu đặt phòng test để xóa';

-- ===========================
-- 3. XÓA KHÁCH HÀNG TEST
-- ===========================

DELETE FROM KhachHang 
WHERE maKhachHang IN ('KH_GIAHAN001', 'KH_GIAHAN002');

IF @@ROWCOUNT > 0
    PRINT N'✅ Đã xóa ' + CAST(@@ROWCOUNT AS NVARCHAR(10)) + N' khách hàng test';
ELSE
    PRINT N'⚠️ Không tìm thấy khách hàng test để xóa';

-- ===========================
-- 4. CẬP NHẬT LẠI TRẠNG THÁI PHÒNG
-- ===========================

UPDATE Phong 
SET trangThai = N'Sẵn sàng' 
WHERE maPhong IN ('P401', 'P402', 'P403');

IF @@ROWCOUNT > 0
    PRINT N'✅ Đã cập nhật ' + CAST(@@ROWCOUNT AS NVARCHAR(10)) + N' phòng về trạng thái "Sẵn sàng"';
ELSE
    PRINT N'⚠️ Không tìm thấy phòng để cập nhật';

-- ===========================
-- 5. KIỂM TRA KẾT QUẢ
-- ===========================

PRINT '';
PRINT N'========================================';
PRINT N'KIỂM TRA KẾT QUẢ SAU KHI XÓA';
PRINT N'========================================';

-- Kiểm tra còn khách hàng test không
DECLARE @countKH INT;
SELECT @countKH = COUNT(*) 
FROM KhachHang 
WHERE maKhachHang IN ('KH_GIAHAN001', 'KH_GIAHAN002');

IF @countKH = 0
    PRINT N'✅ Đã xóa hết khách hàng test';
ELSE
    PRINT N'⚠️ Còn ' + CAST(@countKH AS NVARCHAR(10)) + N' khách hàng test chưa xóa';

-- Kiểm tra còn phiếu đặt phòng test không
DECLARE @countPDP INT;
SELECT @countPDP = COUNT(*) 
FROM PhieuDatPhong 
WHERE maPhieuDatPhong IN ('PDP_GIAHAN001', 'PDP_GIAHAN002');

IF @countPDP = 0
    PRINT N'✅ Đã xóa hết phiếu đặt phòng test';
ELSE
    PRINT N'⚠️ Còn ' + CAST(@countPDP AS NVARCHAR(10)) + N' phiếu đặt phòng test chưa xóa';

-- Kiểm tra trạng thái các phòng
SELECT 
    p.maPhong,
    p.soPhong,
    p.trangThai,
    p.tang,
    lp.tenLoaiPhong
FROM Phong p
JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE p.maPhong IN ('P401', 'P402', 'P403')
ORDER BY p.maPhong;

PRINT '';
PRINT N'========================================';
PRINT N'ĐÃ XÓA XONG DỮ LIỆU TEST!';
PRINT N'========================================';

GO

