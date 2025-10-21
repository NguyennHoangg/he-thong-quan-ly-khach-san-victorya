-- Script tạo dữ liệu test mới cho GiaHanPhong_GUI
-- Tạo khách hàng KH021, phòng P021/P022 và phiếu đặt phòng

USE Victorya_Hotel;
GO

PRINT '========== BẮT ĐẦU TẠO DỮ LIỆU TEST ==========';
PRINT '';

-- 1. Lấy dữ liệu tham chiếu có sẵn
DECLARE @maLoaiPhong VARCHAR(20) = (SELECT TOP 1 maLoaiPhong FROM LoaiPhong ORDER BY maLoaiPhong);
DECLARE @maLoaiDatPhong VARCHAR(20) = (SELECT TOP 1 maLoaiDatPhong FROM LoaiDatPhong ORDER BY maLoaiDatPhong);
DECLARE @maDichVu VARCHAR(20) = (SELECT TOP 1 maDichVu FROM DichVu ORDER BY maDichVu);

IF @maLoaiPhong IS NULL OR @maLoaiDatPhong IS NULL OR @maDichVu IS NULL
BEGIN
    PRINT 'LỖI: Thiếu dữ liệu cơ bản (LoaiPhong, LoaiDatPhong, DichVu)';
    PRINT 'Vui lòng chạy script Victorya_Hotel_v3.sql và DataSample.sql trước!';
    RETURN;
END

PRINT '1. Dữ liệu tham chiếu:';
PRINT '   - Loại phòng: ' + @maLoaiPhong;
PRINT '   - Loại đặt phòng: ' + @maLoaiDatPhong;
PRINT '   - Dịch vụ: ' + @maDichVu;
PRINT '';

-- 2. Tạo Khách hàng KH021
PRINT '2. Tạo Khách hàng KH021...';

IF EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang = 'KH021')
BEGIN
    DELETE FROM ChiTietPhieuDatPhong 
    WHERE maPhieuDatPhong IN (SELECT maPhieuDatPhong FROM PhieuDatPhong WHERE maKhachHang = 'KH021');
    
    DELETE FROM PhieuDatPhong WHERE maKhachHang = 'KH021';
    
    DELETE FROM KhachHang WHERE maKhachHang = 'KH021';
    
    PRINT '   - Đã xóa dữ liệu cũ của KH021';
END

INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao)
VALUES ('KH021', '123456789021', N'Nguyễn Văn Test 021', '0987654021', 'test021@email.com', GETDATE());

PRINT '   - Đã tạo khách hàng: KH021';
PRINT '   - CCCD: 123456789021';
PRINT '   - Họ tên: Nguyễn Văn Test 021';
PRINT '';

-- 3. Tạo Phòng P021 và P022
PRINT '3. Tạo Phòng P021 và P022...';

-- Xóa phòng cũ nếu tồn tại
IF EXISTS (SELECT 1 FROM Phong WHERE maPhong IN ('P021', 'P022'))
BEGIN
    DELETE FROM ChiTietPhieuDatPhong WHERE maPhong IN ('P021', 'P022');
    DELETE FROM Phong WHERE maPhong IN ('P021', 'P022');
    PRINT '   - Đã xóa phòng cũ P021, P022';
END

-- Tạo phòng P021
INSERT INTO Phong (maPhong, tenPhong, trangThai, maLoaiPhong, tang)
VALUES ('P021', N'Phòng 021', N'Trống', @maLoaiPhong, 2);

PRINT '   - Đã tạo phòng: P021 (Tầng 2)';

-- Tạo phòng P022
INSERT INTO Phong (maPhong, tenPhong, trangThai, maLoaiPhong, tang)
VALUES ('P022', N'Phòng 022', N'Trống', @maLoaiPhong, 2);

PRINT '   - Đã tạo phòng: P022 (Tầng 2)';
PRINT '';

-- 4. Tạo Phiếu đặt phòng
PRINT '4. Tạo Phiếu đặt phòng...';

DECLARE @maPhieuDatPhong VARCHAR(20) = 'PDP021';

IF EXISTS (SELECT 1 FROM PhieuDatPhong WHERE maPhieuDatPhong = @maPhieuDatPhong)
BEGIN
    DELETE FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = @maPhieuDatPhong;
    DELETE FROM PhieuDatPhong WHERE maPhieuDatPhong = @maPhieuDatPhong;
    PRINT '   - Đã xóa phiếu đặt phòng cũ';
END

INSERT INTO PhieuDatPhong (maPhieuDatPhong, maKhachHang, ngayTao)
VALUES (@maPhieuDatPhong, 'KH021', GETDATE());

PRINT '   - Đã tạo phiếu đặt phòng: ' + @maPhieuDatPhong;
PRINT '';

-- 5. Tạo Chi tiết phiếu đặt phòng (đang hoạt động)
PRINT '5. Tạo Chi tiết phiếu đặt phòng...';

-- P021: Bắt đầu 1 giờ trước, kết thúc 3 giờ sau
DECLARE @gioBatDau1 DATETIME = DATEADD(HOUR, -1, GETDATE());
DECLARE @gioKetThuc1 DATETIME = DATEADD(HOUR, 3, GETDATE());

INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maLoaiDatPhong, maDichVu, gioBatDau, gioKetThuc, maPhong, soNguoi)
VALUES (@maPhieuDatPhong, @maLoaiDatPhong, @maDichVu, @gioBatDau1, @gioKetThuc1, 'P021', 2);

PRINT '   - Phòng P021:';
PRINT '     + Bắt đầu: ' + CONVERT(VARCHAR, @gioBatDau1, 120);
PRINT '     + Kết thúc: ' + CONVERT(VARCHAR, @gioKetThuc1, 120);
PRINT '     + Số người: 2';

-- P022: Bắt đầu 30 phút trước, kết thúc 2 giờ sau
DECLARE @gioBatDau2 DATETIME = DATEADD(MINUTE, -30, GETDATE());
DECLARE @gioKetThuc2 DATETIME = DATEADD(HOUR, 2, GETDATE());

INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maLoaiDatPhong, maDichVu, gioBatDau, gioKetThuc, maPhong, soNguoi)
VALUES (@maPhieuDatPhong, @maLoaiDatPhong, @maDichVu, @gioBatDau2, @gioKetThuc2, 'P022', 1);

PRINT '   - Phòng P022:';
PRINT '     + Bắt đầu: ' + CONVERT(VARCHAR, @gioBatDau2, 120);
PRINT '     + Kết thúc: ' + CONVERT(VARCHAR, @gioKetThuc2, 120);
PRINT '     + Số người: 1';
PRINT '';

-- 6. Cập nhật trạng thái phòng
PRINT '6. Cập nhật trạng thái phòng...';

UPDATE Phong 
SET trangThai = N'Đang sử dụng' 
WHERE maPhong IN ('P021', 'P022');

PRINT '   - Đã cập nhật trạng thái: Đang sử dụng';
PRINT '';

-- 7. Kiểm tra kết quả
PRINT '7. Kiểm tra kết quả:';
PRINT '';

SELECT 
    kh.maKhachHang,
    kh.CCCD,
    kh.hoTen,
    kh.soDienThoai,
    pdp.maPhieuDatPhong,
    ctpdp.maPhong,
    p.tenPhong,
    p.tang,
    p.trangThai,
    CONVERT(VARCHAR, ctpdp.gioBatDau, 120) as 'Giờ bắt đầu',
    CONVERT(VARCHAR, ctpdp.gioKetThuc, 120) as 'Giờ kết thúc',
    CASE 
        WHEN GETDATE() BETWEEN ctpdp.gioBatDau AND ctpdp.gioKetThuc THEN N'✓ Đang hoạt động'
        ELSE N'✗ Không hoạt động'
    END as 'Trạng thái'
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
JOIN Phong p ON p.maPhong = ctpdp.maPhong
WHERE kh.maKhachHang = 'KH021'
ORDER BY ctpdp.maPhong;

PRINT '';
PRINT '========== HOÀN THÀNH ==========';
PRINT '';
PRINT '✓ Dữ liệu test đã sẵn sàng!';
PRINT '';
PRINT '📌 HƯỚNG DẪN TEST:';
PRINT '   1. Mở GiaHanPhong_GUI';
PRINT '   2. Nhập CCCD: 123456789021';
PRINT '   3. Bấm "Tìm kiếm"';
PRINT '   4. Sẽ hiển thị 2 phòng: P021, P022';
PRINT '';
PRINT '⏰ LƯU Ý: Dữ liệu test có hiệu lực:';
PRINT '   - P021: trong vòng 3 giờ kể từ bây giờ';
PRINT '   - P022: trong vòng 2 giờ kể từ bây giờ';
PRINT '   Sau thời gian này, vui lòng chạy lại script!';
PRINT '';

