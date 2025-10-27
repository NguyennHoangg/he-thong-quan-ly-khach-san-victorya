-- Script tạo dữ liệu demo cho GiaHanPhong_GUI
-- Chạy script này để có dữ liệu test cho trang gia hạn phòng
-- QUAN TRỌNG: Script này sử dụng dữ liệu có sẵn trong database

USE Victorya_Hotel;
GO

-- Lấy thông tin có sẵn từ database
DECLARE @maKhachHang VARCHAR(20) = (SELECT TOP 1 maKhachHang FROM KhachHang ORDER BY maKhachHang);
DECLARE @maLoaiDatPhong VARCHAR(20) = (SELECT TOP 1 maLoaiDatPhong FROM LoaiDatPhong ORDER BY maLoaiDatPhong);
DECLARE @maDichVu VARCHAR(20) = (SELECT TOP 1 maDichVu FROM DichVu ORDER BY maDichVu);
DECLARE @maPhong1 VARCHAR(20) = (SELECT TOP 1 maPhong FROM Phong WHERE trangThai = N'Trống' ORDER BY maPhong);
DECLARE @maPhong2 VARCHAR(20) = (SELECT maPhong FROM (SELECT maPhong, ROW_NUMBER() OVER (ORDER BY maPhong) as rn FROM Phong WHERE trangThai = N'Trống') t WHERE rn = 2);

-- Kiểm tra dữ liệu có sẵn
IF @maKhachHang IS NULL
BEGIN
    PRINT 'LỖI: Không có khách hàng nào trong database. Vui lòng chạy script DataSample.sql trước!';
    RETURN;
END

IF @maLoaiDatPhong IS NULL
BEGIN
    PRINT 'LỖI: Không có loại đặt phòng nào trong database. Vui lòng chạy script Victorya_Hotel_v3.sql trước!';
    RETURN;
END

IF @maDichVu IS NULL
BEGIN
    PRINT 'LỖI: Không có dịch vụ nào trong database. Vui lòng chạy script Victorya_Hotel_v3.sql trước!';
    RETURN;
END

IF @maPhong1 IS NULL
BEGIN
    PRINT 'LỖI: Không có phòng trống nào trong database. Vui lòng thêm phòng trước!';
    RETURN;
END

PRINT 'Sử dụng dữ liệu có sẵn:';
PRINT '  - Khách hàng: ' + @maKhachHang;
PRINT '  - Loại đặt phòng: ' + @maLoaiDatPhong;
PRINT '  - Dịch vụ: ' + @maDichVu;
PRINT '  - Phòng 1: ' + @maPhong1;
PRINT '  - Phòng 2: ' + ISNULL(@maPhong2, 'NULL');
PRINT '';

-- 1. Cập nhật CCCD cho khách hàng để dễ test
DECLARE @cccdTest VARCHAR(20) = '123456789012';
UPDATE KhachHang 
SET CCCD = @cccdTest
WHERE maKhachHang = @maKhachHang;

PRINT 'Đã cập nhật CCCD test: ' + @cccdTest + ' cho khách hàng: ' + @maKhachHang;

-- 2. Tạo phiếu đặt phòng demo
DECLARE @maPhieuDatPhong VARCHAR(20) = 'PDP_TEST_001';

IF NOT EXISTS (SELECT 1 FROM PhieuDatPhong WHERE maPhieuDatPhong = @maPhieuDatPhong)
BEGIN
    INSERT INTO PhieuDatPhong (maPhieuDatPhong, maKhachHang, ngayTao)
    VALUES (@maPhieuDatPhong, @maKhachHang, GETDATE());
    PRINT 'Đã tạo phiếu đặt phòng: ' + @maPhieuDatPhong;
END
ELSE
BEGIN
    PRINT 'Phiếu đặt phòng đã tồn tại: ' + @maPhieuDatPhong;
END

-- 3. Xóa chi tiết phiếu đặt phòng cũ (nếu có)
DELETE FROM ChiTietPhieuDatPhong 
WHERE maPhieuDatPhong = @maPhieuDatPhong;

-- 4. Tạo chi tiết phiếu đặt phòng mới (phòng đang hoạt động)
-- Phòng 1 - bắt đầu 1 giờ trước, kết thúc 2 giờ sau
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maLoaiDatPhong, maDichVu, gioBatDau, gioKetThuc, maPhong, soNguoi)
VALUES (@maPhieuDatPhong, @maLoaiDatPhong, @maDichVu, 
        DATEADD(HOUR, -1, GETDATE()), 
        DATEADD(HOUR, 2, GETDATE()), 
        @maPhong1, 2);

PRINT 'Đã tạo chi tiết phòng: ' + @maPhong1;

-- Phòng 2 (nếu có)
IF @maPhong2 IS NOT NULL
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maLoaiDatPhong, maDichVu, gioBatDau, gioKetThuc, maPhong, soNguoi)
    VALUES (@maPhieuDatPhong, @maLoaiDatPhong, @maDichVu, 
            DATEADD(MINUTE, -30, GETDATE()), 
            DATEADD(HOUR, 1.5, GETDATE()), 
            @maPhong2, 1);
    
    PRINT 'Đã tạo chi tiết phòng: ' + @maPhong2;
END

-- 5. Đảm bảo phòng có trạng thái "Đang sử dụng"
UPDATE Phong SET trangThai = N'Đang sử dụng' WHERE maPhong IN (@maPhong1, @maPhong2);
PRINT 'Đã cập nhật trạng thái phòng thành "Đang sử dụng"';

-- 5. Kiểm tra dữ liệu đã tạo
SELECT 
    'KhachHang' as TableName, 
    maKhachHang, 
    CCCD, 
    hoTen, 
    soDienThoai 
FROM KhachHang 
WHERE CCCD = '123456789012'

UNION ALL

SELECT 
    'PhieuDatPhong' as TableName, 
    maPhieuDatPhong, 
    maKhachHang, 
    CONVERT(VARCHAR, ngayTao, 120), 
    '' 
FROM PhieuDatPhong 
WHERE maPhieuDatPhong = @maPhieuDatPhong

UNION ALL

SELECT 
    'ChiTietPhieuDatPhong' as TableName, 
    maPhieuDatPhong, 
    maPhong, 
    CONVERT(VARCHAR, gioBatDau, 120), 
    CONVERT(VARCHAR, gioKetThuc, 120) 
FROM ChiTietPhieuDatPhong 
WHERE maPhieuDatPhong = @maPhieuDatPhong;

PRINT 'Đã tạo dữ liệu demo cho GiaHanPhong_GUI';
PRINT 'CCCD test: 123456789012';
PRINT 'Phòng test: P101, P102';
