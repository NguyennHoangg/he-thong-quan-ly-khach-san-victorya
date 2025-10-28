-- ===========================
-- QUICK TEST - GIA HẠN PHÒNG
-- ===========================
-- Chạy script này để test nhanh chức năng gia hạn phòng

USE Victorya_Hotel;
GO

PRINT N'========================================';
PRINT N'QUICK TEST - GIA HẠN PHÒNG';
PRINT N'========================================';
PRINT N'';

-- Test với CCCD: 999999999999
PRINT N'TEST 1: CCCD = 999999999999';
PRINT N'---';

SELECT 
    kh.CCCD AS 'CCCD',
    kh.hoTen AS 'Tên Khách',
    kh.soDienThoai AS 'SĐT',
    kh.email AS 'Email',
    p.soPhong AS 'Phòng',
    lp.tenLoaiPhong AS 'Loại',
    p.trangThai AS 'Trạng thái',
    CONVERT(VARCHAR, ctpdp.thoiGianTraPhong, 120) AS 'Trả Phòng',
    CASE 
        WHEN DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) < 24 
            THEN CAST(DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' giờ'
        ELSE CAST(DATEDIFF(DAY, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' ngày'
    END AS 'Còn Lại'
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
JOIN Phong p ON p.maPhong = ctpdp.maPhong
JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong
WHERE kh.CCCD = '999999999999'
  AND GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong;

IF @@ROWCOUNT = 0
    PRINT N'❌ KHÔNG TÌM THẤY DỮ LIỆU!';
ELSE
    PRINT N'✅ OK - Dữ liệu đã sẵn sàng';

PRINT N'';
PRINT N'========================================';
PRINT N'';

-- Test với CCCD: 888888888888
PRINT N'TEST 2: CCCD = 888888888888';
PRINT N'---';

SELECT 
    kh.CCCD AS 'CCCD',
    kh.hoTen AS 'Tên Khách',
    p.soPhong AS 'Phòng',
    lp.tenLoaiPhong AS 'Loại',
    p.trangThai AS 'Trạng thái',
    CONVERT(VARCHAR, ctpdp.thoiGianTraPhong, 120) AS 'Trả Phòng',
    CASE 
        WHEN DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) < 24 
            THEN CAST(DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' giờ'
        ELSE CAST(DATEDIFF(DAY, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' ngày'
    END AS 'Còn Lại'
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
JOIN Phong p ON p.maPhong = ctpdp.maPhong
JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong
WHERE kh.CCCD = '888888888888'
  AND GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong;

IF @@ROWCOUNT = 0
    PRINT N'❌ KHÔNG TÌM THẤY DỮ LIỆU!';
ELSE
    PRINT N'✅ OK - Dữ liệu đã sẵn sàng';

PRINT N'';
PRINT N'========================================';
PRINT N'KẾT LUẬN';
PRINT N'========================================';
PRINT N'';
PRINT N'Nếu cả 2 test đều OK:';
PRINT N'→ Dữ liệu trong database HOÀN TOÀN ĐÚNG';
PRINT N'→ Vấn đề có thể ở:';
PRINT N'  • Code Java (DAO/Controller)';
PRINT N'  • Connection database';
PRINT N'  • Build/Compile project';
PRINT N'';
PRINT N'Nếu 1 trong 2 test FAIL:';
PRINT N'→ Chạy: GiaHanPhong_FullSetup.sql';
PRINT N'→ Sau đó chạy lại script này';
PRINT N'';

GO

