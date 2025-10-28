-- ===========================
-- KIỂM TRA DỮ LIỆU GIA HẠN PHÒNG
-- ===========================
USE Victorya_Hotel;
GO

PRINT N'========================================';
PRINT N'KIỂM TRA DỮ LIỆU GIA HẠN PHÒNG';
PRINT N'========================================';
PRINT '';

-- Kiểm tra Loại Phòng
PRINT N'--- LOẠI PHÒNG ---';
SELECT * FROM LoaiPhong WHERE maLoaiPhong IN ('LP_STD', 'LP_VIP');

PRINT '';
PRINT N'--- PHÒNG TEST (Tầng 4) ---';
SELECT 
    p.maPhong,
    p.soPhong,
    p.trangThai,
    p.tang,
    lp.tenLoaiPhong,
    lp.gia
FROM Phong p
LEFT JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE p.maPhong IN ('P401', 'P402', 'P403')
ORDER BY p.maPhong;

-- Đếm số phòng tồn tại
DECLARE @countPhong INT;
SELECT @countPhong = COUNT(*) FROM Phong WHERE maPhong IN ('P401', 'P402', 'P403');

PRINT '';
IF @countPhong = 0
    PRINT N'❌ KHÔNG CÓ PHÒNG NÀO TRONG SỐ P401-P403 TỒN TẠI!';
ELSE IF @countPhong < 3
    PRINT N'⚠️ CHỈ CÓ ' + CAST(@countPhong AS NVARCHAR(10)) + N'/3 PHÒNG TỒN TẠI';
ELSE
    PRINT N'✅ ĐỦ 3 PHÒNG ĐÃ TỒN TẠI';

PRINT '';
PRINT N'--- LOẠI ĐẶT PHÒNG ---';
SELECT * FROM LoaiDatPhong;

PRINT '';
PRINT N'--- KHÁCH HÀNG TEST ---';
SELECT 
    maKhachHang,
    CCCD,
    hoTen,
    soDienThoai,
    email
FROM KhachHang
WHERE maKhachHang IN ('KH_GIAHAN001', 'KH_GIAHAN002');

PRINT '';
PRINT N'--- PHÒNG ĐANG Ở CỦA KHÁCH TEST ---';
SELECT 
    kh.CCCD,
    kh.hoTen,
    p.soPhong,
    p.trangThai,
    lp.tenLoaiPhong,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    CASE 
        WHEN DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) < 24 
            THEN CAST(DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' giờ'
        ELSE CAST(DATEDIFF(DAY, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' ngày'
    END AS ConLai
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON kh.maKhachHang = pdp.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong
JOIN Phong p ON ctpdp.maPhong = p.maPhong
JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE kh.maKhachHang IN ('KH_GIAHAN001', 'KH_GIAHAN002')
    AND p.trangThai = N'Đang ở'
ORDER BY kh.CCCD, p.soPhong;

GO

