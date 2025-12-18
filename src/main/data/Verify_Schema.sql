-- ===========================
-- VERIFY SCHEMA VS CODE
-- Kiểm tra cấu trúc bảng và data types
-- ===========================

USE Victorya_Hotel_v6;
GO

PRINT '===== VERIFY SCHEMA =====';
PRINT '';

-- 1. Check Phong table structure
PRINT '1. BẢNG PHONG:';
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Phong'
ORDER BY ORDINAL_POSITION;

SELECT 'Sample Data:' as Info;
SELECT TOP 3 * FROM Phong;
PRINT '';

-- 2. Check KhachHang table
PRINT '2. BẢNG KHACHHANG:';
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'KhachHang'
ORDER BY ORDINAL_POSITION;

SELECT 'Sample Data:' as Info;
SELECT TOP 3 maKhachHang, hoTen, soDienThoai FROM KhachHang;
PRINT '';

-- 3. Check HoaDon table
PRINT '3. BẢNG HOADON:';
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'HoaDon'
ORDER BY ORDINAL_POSITION;

SELECT 'Sample Data:' as Info;
SELECT TOP 3 maHoaDon, ngayDat, ngayTao, tongTien FROM HoaDon;
PRINT '';

-- 4. Check PhieuDatPhong table
PRINT '4. BẢNG PHIEUDATPHONG:';
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'PhieuDatPhong'
ORDER BY ORDINAL_POSITION;

SELECT 'Sample Data:' as Info;
SELECT TOP 3 maPhieuDatPhong, ngayTao, maKhachHang, trangThai FROM PhieuDatPhong;
PRINT '';

-- 5. Test exact query từ ThongKe_DAO
PRINT '5. TEST QUERY THỐNG KÊ PHÒNG (từ ThongKe_DAO):';
SELECT 
    COUNT(*) as tongSoPhong,
    SUM(CASE WHEN trangThai = N'Đang sử dụng' THEN 1 ELSE 0 END) as phongDangSuDung,
    SUM(CASE WHEN trangThai = N'Trống' THEN 1 ELSE 0 END) as phongTrong,
    SUM(CASE WHEN trangThai = N'Đã đặt' THEN 1 ELSE 0 END) as phongDaDat
FROM Phong;

-- Check các trạng thái thực tế
SELECT 'Trạng thái phòng thực tế:' as Info;
SELECT trangThai, COUNT(*) as soLuong
FROM Phong
GROUP BY trangThai;
PRINT '';

-- 6. Test query Top Khách Hàng
PRINT '6. TEST QUERY TOP KHÁCH HÀNG (từ ThongKe_DAO):';
SELECT TOP 5
    kh.maKhachHang,
    kh.hoTen as tenKhachHang,
    kh.soDienThoai,
    COUNT(hd.maHoaDon) as soLanDat,
    COALESCE(SUM(hd.tongTien), 0) as tongChiTieu
FROM KhachHang kh
LEFT JOIN HoaDon hd ON kh.maKhachHang = hd.maKhachHang
GROUP BY kh.maKhachHang, kh.hoTen, kh.soDienThoai
ORDER BY tongChiTieu DESC;
PRINT '';

-- 7. Check data types của tongTien
PRINT '7. DATA TYPE của tongTien:';
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    NUMERIC_PRECISION,
    NUMERIC_SCALE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME = 'tongTien';

SELECT 'Sample tongTien values:' as Info;
SELECT TOP 5 maHoaDon, tongTien, SQL_VARIANT_PROPERTY(tongTien, 'BaseType') as ActualType
FROM HoaDon;
PRINT '';

-- 8. Test query Doanh thu theo loại phòng
PRINT '8. TEST QUERY DOANH THU THEO LOẠI PHÒNG:';
SELECT 
    lp.tenLoaiPhong,
    COUNT(DISTINCT hd.maHoaDon) as soHoaDon,
    COALESCE(SUM(hd.tongTien), 0) as doanhThu
FROM LoaiPhong lp
JOIN Phong p ON lp.maLoaiPhong = p.maLoaiPhong
JOIN ChiTietPhieuDatPhong ctpdp ON p.maPhong = ctpdp.maPhong
JOIN PhieuDatPhong pdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
JOIN ChiTietHoaDon cthd ON pdp.maPhieuDatPhong = cthd.maPhieuDatPhong
JOIN HoaDon hd ON cthd.maHoaDon = hd.maHoaDon
WHERE hd.ngayTao BETWEEN '2025-10-01' AND '2025-10-31'
GROUP BY lp.tenLoaiPhong
ORDER BY doanhThu DESC;
PRINT '';

-- 9. Check NULL values trong các cột quan trọng
PRINT '9. CHECK NULL VALUES:';
SELECT 
    'Phong - trangThai' as TableColumn,
    COUNT(*) as TotalRows,
    SUM(CASE WHEN trangThai IS NULL THEN 1 ELSE 0 END) as NullCount
FROM Phong
UNION ALL
SELECT 
    'HoaDon - tongTien',
    COUNT(*),
    SUM(CASE WHEN tongTien IS NULL THEN 1 ELSE 0 END)
FROM HoaDon
UNION ALL
SELECT 
    'HoaDon - ngayTao',
    COUNT(*),
    SUM(CASE WHEN ngayTao IS NULL THEN 1 ELSE 0 END)
FROM HoaDon;
PRINT '';

-- 10. Test query Khuyến mãi với data types
PRINT '10. TEST QUERY KHUYẾN MÃI:';
SELECT 
    km.maKhuyenMai,
    km.tenKhuyenMai,
    km.heSo,
    SQL_VARIANT_PROPERTY(km.heSo, 'BaseType') as HeSoType,
    COUNT(hd.maHoaDon) as soLanSuDung,
    COALESCE(SUM(hd.tongTien * km.heSo), 0) as tongGiamGia
FROM KhuyenMai km
LEFT JOIN HoaDon hd ON km.maKhuyenMai = hd.maKhuyenMai
GROUP BY km.maKhuyenMai, km.tenKhuyenMai, km.heSo
ORDER BY soLanSuDung DESC;

PRINT '';
PRINT '===== END VERIFICATION =====';
GO

