-- ===========================
-- DEBUG - KIỂM TRA DỮ LIỆU GIA HẠN PHÒNG
-- ===========================
USE Victorya_Hotel;
GO

PRINT N'========================================';
PRINT N'DEBUG - KIỂM TRA DỮ LIỆU';
PRINT N'========================================';
PRINT '';

-- 1. Kiểm tra khách hàng có tồn tại không
PRINT N'1. KHÁCH HÀNG TEST:';
SELECT 
    maKhachHang,
    CCCD,
    hoTen,
    soDienThoai,
    email
FROM KhachHang
WHERE CCCD IN ('999999999999', '888888888888');

DECLARE @countKH INT = @@ROWCOUNT;
IF @countKH = 0
    PRINT N'   ❌ KHÔNG TÌM THẤY KHÁCH HÀNG TEST!';
ELSE
    PRINT N'   ✅ Tìm thấy ' + CAST(@countKH AS NVARCHAR(10)) + N' khách hàng test';

PRINT '';

-- 2. Kiểm tra phòng có tồn tại không
PRINT N'2. PHÒNG TEST:';
SELECT 
    maPhong,
    soPhong,
    trangThai,
    maLoaiPhong,
    tang
FROM Phong
WHERE maPhong IN ('P401', 'P402', 'P403');

DECLARE @countPhong INT = @@ROWCOUNT;
IF @countPhong = 0
    PRINT N'   ❌ KHÔNG TÌM THẤY PHÒNG TEST!';
ELSE
    PRINT N'   ✅ Tìm thấy ' + CAST(@countPhong AS NVARCHAR(10)) + N' phòng test';

PRINT '';

-- 3. Kiểm tra phiếu đặt phòng
PRINT N'3. PHIẾU ĐẶT PHÒNG:';
SELECT 
    pdp.maPhieuDatPhong,
    pdp.maKhachHang,
    kh.CCCD,
    kh.hoTen,
    pdp.ngayTao
FROM PhieuDatPhong pdp
JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang
WHERE kh.CCCD IN ('999999999999', '888888888888');

DECLARE @countPDP INT = @@ROWCOUNT;
IF @countPDP = 0
    PRINT N'   ❌ KHÔNG TÌM THẤY PHIẾU ĐẶT PHÒNG!';
ELSE
    PRINT N'   ✅ Tìm thấy ' + CAST(@countPDP AS NVARCHAR(10)) + N' phiếu đặt phòng';

PRINT '';

-- 4. Kiểm tra chi tiết phiếu đặt phòng
PRINT N'4. CHI TIẾT PHIẾU ĐẶT PHÒNG (TẤT CẢ):';
SELECT 
    ctpdp.maPhieuDatPhong,
    ctpdp.maPhong,
    p.soPhong,
    p.trangThai,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    ctpdp.maLoaiDatPhong,
    ctpdp.soNguoi
FROM ChiTietPhieuDatPhong ctpdp
JOIN Phong p ON ctpdp.maPhong = p.maPhong
WHERE ctpdp.maPhieuDatPhong IN (
    SELECT maPhieuDatPhong 
    FROM PhieuDatPhong 
    WHERE maKhachHang IN ('KH_GIAHAN001', 'KH_GIAHAN002')
);

DECLARE @countCTPDP INT = @@ROWCOUNT;
IF @countCTPDP = 0
    PRINT N'   ❌ KHÔNG TÌM THẤY CHI TIẾT PHIẾU ĐẶT PHÒNG!';
ELSE
    PRINT N'   ✅ Tìm thấy ' + CAST(@countCTPDP AS NVARCHAR(10)) + N' chi tiết';

PRINT '';

-- 5. Kiểm tra điều kiện của DAO (Query chính xác như trong code)
PRINT N'5. TEST QUERY DAO (getDatPhongHienTaiTheoCCCD):';
PRINT N'   Điều kiện: p.trangThai = "Đang ở" AND GETDATE() BETWEEN thoiGianNhanPhong AND thoiGianTraPhong';
PRINT '';

SELECT 
    kh.CCCD,
    kh.hoTen,
    p.soPhong,
    p.trangThai,
    lp.tenLoaiPhong,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    GETDATE() AS [Thoi_Gian_Hien_Tai],
    CASE 
        WHEN p.trangThai = N'Đang ở' THEN N'✓ Đúng trạng thái'
        ELSE N'✗ SAI trạng thái: ' + p.trangThai
    END AS KiemTra_TrangThai,
    CASE 
        WHEN GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong 
            THEN N'✓ Trong thời gian'
        ELSE N'✗ Ngoài thời gian'
    END AS KiemTra_ThoiGian
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
JOIN Phong p ON p.maPhong = ctpdp.maPhong
JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong
WHERE kh.CCCD IN ('999999999999', '888888888888')
ORDER BY kh.CCCD, p.soPhong;

DECLARE @countResult INT = @@ROWCOUNT;
PRINT '';
IF @countResult = 0
BEGIN
    PRINT N'   ❌ QUERY KHÔNG TRẢ VỀ KẾT QUẢ!';
    PRINT N'';
    PRINT N'   Kiểm tra:';
    PRINT N'   1. Trạng thái phòng phải là "Đang ở"';
    PRINT N'   2. GETDATE() phải BETWEEN thoiGianNhanPhong AND thoiGianTraPhong';
END
ELSE
BEGIN
    PRINT N'   ✅ Query trả về ' + CAST(@countResult AS NVARCHAR(10)) + N' kết quả';
END

PRINT '';

-- 6. Kiểm tra query với điều kiện chính xác
PRINT N'6. QUERY VỚI ĐIỀU KIỆN ĐẦY ĐỦ (Giống DAO):';

SELECT 
    kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email,
    pdp.maPhieuDatPhong,
    ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong, ctpdp.maLoaiDatPhong, ctpdp.maPhong, ctpdp.soNguoi,
    p.soPhong, p.trangThai, p.tang, 
    lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
JOIN Phong p ON p.maPhong = ctpdp.maPhong
JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong
WHERE kh.CCCD = '999999999999'
  AND GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong
ORDER BY p.tang, p.maPhong;

PRINT '';
PRINT N'========================================';
PRINT N'HƯỚNG DẪN KHẮC PHỤC';
PRINT N'========================================';
PRINT N'';
PRINT N'Nếu không thấy dữ liệu:';
PRINT N'1. Chạy GiaHanPhong_CreateRooms.sql';
PRINT N'2. Chạy GiaHanPhong_TestData.sql';
PRINT N'3. Kiểm tra lại bằng script này';
PRINT N'';
PRINT N'Nếu vẫn không thấy:';
PRINT N'4. Kiểm tra trạng thái phòng = "Đang ở"';
PRINT N'5. Kiểm tra thời gian: GETDATE() phải nằm giữa thoiGianNhanPhong và thoiGianTraPhong';

GO

