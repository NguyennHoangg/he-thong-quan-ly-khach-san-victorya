-- ===========================
-- SETUP HOÀN CHỈNH DỮ LIỆU GIA HẠN PHÒNG
-- ===========================
-- File này kết hợp tất cả bước để tạo dữ liệu test một lần
-- Chạy file này nếu gặp vấn đề với các file riêng lẻ

USE Victorya_Hotel;
GO

PRINT N'========================================';
PRINT N'BẮT ĐẦU SETUP DỮ LIỆU GIA HẠN PHÒNG';
PRINT N'========================================';
PRINT '';

-- ===========================
-- BƯỚC 1: XÓA DỮ LIỆU CŨ (NẾU CÓ)
-- ===========================

PRINT N'BƯỚC 1: Xóa dữ liệu cũ...';

-- Xóa chi tiết phiếu đặt phòng
DELETE FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong IN ('PDP_GIAHAN001', 'PDP_GIAHAN002');
-- Xóa phiếu đặt phòng
DELETE FROM PhieuDatPhong WHERE maPhieuDatPhong IN ('PDP_GIAHAN001', 'PDP_GIAHAN002');
-- Xóa khách hàng
DELETE FROM KhachHang WHERE maKhachHang IN ('KH_GIAHAN001', 'KH_GIAHAN002');

PRINT N'✅ Đã xóa dữ liệu cũ (nếu có)';
PRINT '';

-- ===========================
-- BƯỚC 2: TẠO LOẠI PHÒNG
-- ===========================

PRINT N'BƯỚC 2: Tạo loại phòng...';

IF NOT EXISTS (SELECT 1 FROM LoaiPhong WHERE maLoaiPhong = 'LP_STD')
BEGIN
    INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao)
    VALUES ('LP_STD', N'Standard', 500000.00, CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại phòng Standard';
END
ELSE
    PRINT N'⚠️ Loại phòng Standard đã tồn tại';

IF NOT EXISTS (SELECT 1 FROM LoaiPhong WHERE maLoaiPhong = 'LP_VIP')
BEGIN
    INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao)
    VALUES ('LP_VIP', N'VIP', 1000000.00, CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại phòng VIP';
END
ELSE
    PRINT N'⚠️ Loại phòng VIP đã tồn tại';

PRINT '';

-- ===========================
-- BƯỚC 3: TẠO PHÒNG
-- ===========================

PRINT N'BƯỚC 3: Tạo phòng test...';

-- Xóa phòng cũ nếu có
DELETE FROM ChiTietPhieuDatPhong WHERE maPhong IN ('P401', 'P402', 'P403');
DELETE FROM Phong WHERE maPhong IN ('P401', 'P402', 'P403');

-- Tạo phòng P401
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
VALUES ('P401', N'401', N'Sẵn sàng', 'LP_STD', 4);
PRINT N'✅ Đã tạo phòng P401';

-- Tạo phòng P402
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
VALUES ('P402', N'402', N'Sẵn sàng', 'LP_VIP', 4);
PRINT N'✅ Đã tạo phòng P402';

-- Tạo phòng P403
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
VALUES ('P403', N'403', N'Sẵn sàng', 'LP_VIP', 4);
PRINT N'✅ Đã tạo phòng P403';

PRINT '';

-- ===========================
-- BƯỚC 4: TẠO LOẠI ĐẶT PHÒNG
-- ===========================

PRINT N'BƯỚC 4: Tạo loại đặt phòng...';

IF NOT EXISTS (SELECT 1 FROM LoaiDatPhong WHERE maLoaiDatPhong = 'LDP_NGAY')
BEGIN
    INSERT INTO LoaiDatPhong (maLoaiDatPhong, tenLoaiDatPhong, ngayTao)
    VALUES ('LDP_NGAY', N'Theo ngày', CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại đặt phòng "Theo ngày"';
END
ELSE
    PRINT N'⚠️ Loại đặt phòng đã tồn tại';

PRINT '';

-- ===========================
-- BƯỚC 5: TẠO KHÁCH HÀNG
-- ===========================

PRINT N'BƯỚC 5: Tạo khách hàng test...';

INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao)
VALUES ('KH_GIAHAN001', '999999999999', N'Phạm Văn Đức', '0909876543', 'phamvanduc@gmail.com', CAST(GETDATE() AS DATE));
PRINT N'✅ Đã tạo khách hàng: Phạm Văn Đức (CCCD: 999999999999)';

INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao)
VALUES ('KH_GIAHAN002', '888888888888', N'Võ Thị Em', '0918765432', 'vothiem@gmail.com', CAST(GETDATE() AS DATE));
PRINT N'✅ Đã tạo khách hàng: Võ Thị Em (CCCD: 888888888888)';

PRINT '';

-- ===========================
-- BƯỚC 6: TẠO PHIẾU ĐẶT PHÒNG
-- ===========================

PRINT N'BƯỚC 6: Tạo phiếu đặt phòng...';

INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang)
VALUES ('PDP_GIAHAN001', CAST(GETDATE() AS DATE), 'KH_GIAHAN001');
PRINT N'✅ Đã tạo phiếu: PDP_GIAHAN001';

INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang)
VALUES ('PDP_GIAHAN002', CAST(GETDATE() AS DATE), 'KH_GIAHAN002');
PRINT N'✅ Đã tạo phiếu: PDP_GIAHAN002';

PRINT '';

-- ===========================
-- BƯỚC 7: TẠO CHI TIẾT VÀ CẬP NHẬT TRẠNG THÁI
-- ===========================

PRINT N'BƯỚC 7: Tạo chi tiết phiếu đặt và cập nhật trạng thái phòng...';

-- P401: Khách 1
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
VALUES (
    'PDP_GIAHAN001',
    'P401',
    DATEADD(DAY, -2, GETDATE()),
    DATEADD(HOUR, 3, GETDATE()),
    'LDP_NGAY',
    2
);
UPDATE Phong SET trangThai = N'Đang ở' WHERE maPhong = 'P401';
PRINT N'✅ P401 - Khách 1 - Còn 3 giờ';

-- P402: Khách 2
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
VALUES (
    'PDP_GIAHAN002',
    'P402',
    DATEADD(DAY, -1, GETDATE()),
    DATEADD(DAY, 1, GETDATE()),
    'LDP_NGAY',
    2
);
UPDATE Phong SET trangThai = N'Đang ở' WHERE maPhong = 'P402';
PRINT N'✅ P402 - Khách 2 - Còn 1 ngày';

-- P403: Khách 2
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
VALUES (
    'PDP_GIAHAN002',
    'P403',
    DATEADD(DAY, -1, GETDATE()),
    DATEADD(DAY, 2, GETDATE()),
    'LDP_NGAY',
    3
);
UPDATE Phong SET trangThai = N'Đang ở' WHERE maPhong = 'P403';
PRINT N'✅ P403 - Khách 2 - Còn 2 ngày';

PRINT '';

-- ===========================
-- BƯỚC 8: KIỂM TRA KẾT QUẢ CUỐI CÙNG
-- ===========================

PRINT N'========================================';
PRINT N'KIỂM TRA KẾT QUẢ CUỐI CÙNG';
PRINT N'========================================';
PRINT '';

-- Query chính xác như trong DAO
SELECT 
    kh.CCCD,
    kh.hoTen,
    p.soPhong,
    p.trangThai,
    lp.tenLoaiPhong,
    lp.gia,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    ctpdp.soNguoi,
    CASE 
        WHEN DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) < 24 
            THEN CAST(DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' giờ'
        ELSE CAST(DATEDIFF(DAY, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' ngày'
    END AS ThoiGianConLai
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
JOIN Phong p ON p.maPhong = ctpdp.maPhong
JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong
WHERE kh.CCCD IN ('999999999999', '888888888888')
  AND GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong
ORDER BY kh.CCCD, p.soPhong;

PRINT '';
PRINT N'========================================';
PRINT N'SETUP HOÀN TẤT!';
PRINT N'========================================';
PRINT N'';
PRINT N'📌 Bây giờ bạn có thể test với CCCD:';
PRINT N'   • 999999999999 (Phạm Văn Đức) - 1 phòng';
PRINT N'   • 888888888888 (Võ Thị Em) - 2 phòng';
PRINT N'';

GO

