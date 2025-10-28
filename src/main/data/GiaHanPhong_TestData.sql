-- ===========================
-- DỮ LIỆU TEST CHO TRANG GIA HẠN PHÒNG
-- ===========================
-- File này chứa dữ liệu test để kiểm thử chức năng gia hạn phòng
-- Bao gồm: Khách hàng, Phiếu đặt phòng, Phòng có trạng thái "Đang ở"

USE Victorya_Hotel;
GO

-- ===========================
-- 1. KHÁCH HÀNG TEST
-- ===========================

-- Khách hàng 1: Có 1 phòng đang ở
IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE CCCD = '999999999999')
BEGIN
    INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao)
    VALUES ('KH_GIAHAN001', '999999999999', N'Phạm Văn Đức', '0909876543', 'phamvanduc@gmail.com', CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo khách hàng test 1: Phạm Văn Đức (CCCD: 999999999999)';
END
ELSE
BEGIN
    UPDATE KhachHang 
    SET hoTen = N'Phạm Văn Đức',
        soDienThoai = '0909876543',
        email = 'phamvanduc@gmail.com'
    WHERE CCCD = '999999999999';
    PRINT N'⚠️ Khách hàng test 1 đã tồn tại - Đã cập nhật thông tin';
END

-- Khách hàng 2: Có nhiều phòng đang ở
IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE CCCD = '888888888888')
BEGIN
    INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao)
    VALUES ('KH_GIAHAN002', '888888888888', N'Võ Thị Em', '0918765432', 'vothiem@gmail.com', CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo khách hàng test 2: Võ Thị Em (CCCD: 888888888888)';
END
ELSE
BEGIN
    UPDATE KhachHang 
    SET hoTen = N'Võ Thị Em',
        soDienThoai = '0918765432',
        email = 'vothiem@gmail.com'
    WHERE CCCD = '888888888888';
    PRINT N'⚠️ Khách hàng test 2 đã tồn tại - Đã cập nhật thông tin';
END

-- ===========================
-- 2. TẠO LOẠI PHÒNG NẾU CHƯA CÓ
-- ===========================

-- Loại phòng Standard
IF NOT EXISTS (SELECT 1 FROM LoaiPhong WHERE maLoaiPhong = 'LP_STD')
BEGIN
    INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao)
    VALUES ('LP_STD', N'Standard', 500000.00, CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại phòng Standard';
END

-- Loại phòng VIP
IF NOT EXISTS (SELECT 1 FROM LoaiPhong WHERE maLoaiPhong = 'LP_VIP')
BEGIN
    INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao)
    VALUES ('LP_VIP', N'VIP', 1000000.00, CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại phòng VIP';
END

-- ===========================
-- 3. TẠO PHÒNG TEST NẾU CHƯA CÓ
-- ===========================

-- Phòng P401
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P401')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P401', N'401', N'Sẵn sàng', 'LP_STD', 4);
    PRINT N'✅ Đã tạo phòng P401 (Tầng 4 - Standard)';
END

-- Phòng P402
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P402')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P402', N'402', N'Sẵn sàng', 'LP_VIP', 4);
    PRINT N'✅ Đã tạo phòng P402 (Tầng 4 - VIP)';
END

-- Phòng P403
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P403')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P403', N'403', N'Sẵn sàng', 'LP_VIP', 4);
    PRINT N'✅ Đã tạo phòng P403 (Tầng 4 - VIP)';
END

-- ===========================
-- 4. CẬP NHẬT TRẠNG THÁI PHÒNG VỀ "ĐANG Ở"
-- ===========================

UPDATE Phong SET trangThai = N'Đang ở' WHERE maPhong IN ('P401', 'P402', 'P403');
IF @@ROWCOUNT > 0
    PRINT N'✅ Đã cập nhật ' + CAST(@@ROWCOUNT AS NVARCHAR(10)) + N' phòng về trạng thái "Đang ở"';

-- ===========================
-- 5. TẠO LOẠI ĐẶT PHÒNG NẾU CHƯA CÓ
-- ===========================

IF NOT EXISTS (SELECT 1 FROM LoaiDatPhong WHERE maLoaiDatPhong = 'LDP_NGAY')
BEGIN
    INSERT INTO LoaiDatPhong (maLoaiDatPhong, tenLoaiDatPhong, ngayTao)
    VALUES ('LDP_NGAY', N'Theo ngày', CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại đặt phòng "Theo ngày"';
END

-- ===========================
-- 6. PHIẾU ĐẶT PHÒNG TEST
-- ===========================

-- Phiếu 1: Cho khách hàng 1
IF NOT EXISTS (SELECT 1 FROM PhieuDatPhong WHERE maPhieuDatPhong = 'PDP_GIAHAN001')
BEGIN
    INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang)
    VALUES ('PDP_GIAHAN001', CAST(GETDATE() AS DATE), 'KH_GIAHAN001');
    PRINT N'✅ Đã tạo phiếu đặt phòng: PDP_GIAHAN001';
END
ELSE
BEGIN
    PRINT N'⚠️ Phiếu PDP_GIAHAN001 đã tồn tại';
END

-- Phiếu 2: Cho khách hàng 2
IF NOT EXISTS (SELECT 1 FROM PhieuDatPhong WHERE maPhieuDatPhong = 'PDP_GIAHAN002')
BEGIN
    INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang)
    VALUES ('PDP_GIAHAN002', CAST(GETDATE() AS DATE), 'KH_GIAHAN002');
    PRINT N'✅ Đã tạo phiếu đặt phòng: PDP_GIAHAN002';
END
ELSE
BEGIN
    PRINT N'⚠️ Phiếu PDP_GIAHAN002 đã tồn tại';
END

-- ===========================
-- 7. CHI TIẾT PHIẾU ĐẶT PHÒNG - PHÒNG ĐANG Ở
-- ===========================

-- Chi tiết 1: Khách 1 - Phòng 401 (Đang ở, sắp hết hạn - còn 3 giờ)
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = 'PDP_GIAHAN001' AND maPhong = 'P401')
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
    VALUES (
        'PDP_GIAHAN001',
        'P401',
        DATEADD(DAY, -2, GETDATE()),   -- Đã nhận từ 2 ngày trước
        DATEADD(HOUR, 3, GETDATE()),   -- Sẽ trả sau 3 giờ (CẦN GIA HẠN GẤP!)
        'LDP_NGAY',
        2
    );
    PRINT N'✅ Đã tạo chi tiết P401 - Còn 3 giờ (CẦN GIA HẠN GẤP)';
END
ELSE
BEGIN
    UPDATE ChiTietPhieuDatPhong 
    SET thoiGianNhanPhong = DATEADD(DAY, -2, GETDATE()),
        thoiGianTraPhong = DATEADD(HOUR, 3, GETDATE())
    WHERE maPhieuDatPhong = 'PDP_GIAHAN001' AND maPhong = 'P401';
    PRINT N'⚠️ Đã cập nhật thời gian cho P401';
END

-- Chi tiết 2: Khách 2 - Phòng 402 (Đang ở, còn 1 ngày)
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = 'PDP_GIAHAN002' AND maPhong = 'P402')
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
    VALUES (
        'PDP_GIAHAN002',
        'P402',
        DATEADD(DAY, -1, GETDATE()),   -- Đã nhận từ 1 ngày trước
        DATEADD(DAY, 1, GETDATE()),    -- Trả sau 1 ngày
        'LDP_NGAY',
        2
    );
    PRINT N'✅ Đã tạo chi tiết P402 - Còn 1 ngày';
END
ELSE
BEGIN
    UPDATE ChiTietPhieuDatPhong 
    SET thoiGianNhanPhong = DATEADD(DAY, -1, GETDATE()),
        thoiGianTraPhong = DATEADD(DAY, 1, GETDATE())
    WHERE maPhieuDatPhong = 'PDP_GIAHAN002' AND maPhong = 'P402';
    PRINT N'⚠️ Đã cập nhật thời gian cho P402';
END

-- Chi tiết 3: Khách 2 - Phòng 403 (Đang ở, còn 2 ngày)
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = 'PDP_GIAHAN002' AND maPhong = 'P403')
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
    VALUES (
        'PDP_GIAHAN002',
        'P403',
        DATEADD(DAY, -1, GETDATE()),   -- Đã nhận từ 1 ngày trước  
        DATEADD(DAY, 2, GETDATE()),    -- Trả sau 2 ngày
        'LDP_NGAY',
        3
    );
    PRINT N'✅ Đã tạo chi tiết P403 - Còn 2 ngày';
END
ELSE
BEGIN
    UPDATE ChiTietPhieuDatPhong 
    SET thoiGianNhanPhong = DATEADD(DAY, -1, GETDATE()),
        thoiGianTraPhong = DATEADD(DAY, 2, GETDATE())
    WHERE maPhieuDatPhong = 'PDP_GIAHAN002' AND maPhong = 'P403';
    PRINT N'⚠️ Đã cập nhật thời gian cho P403';
END

-- ===========================
-- 8. KIỂM TRA DỮ LIỆU ĐÃ TẠO
-- ===========================

PRINT '';
PRINT N'========================================';
PRINT N'TỔNG KẾT DỮ LIỆU TEST CHO GIA HẠN PHÒNG';
PRINT N'========================================';

-- Kiểm tra khách hàng
SELECT 
    CCCD,
    hoTen,
    soDienThoai,
    email
FROM KhachHang
WHERE maKhachHang IN ('KH_GIAHAN001', 'KH_GIAHAN002');

PRINT '';
PRINT N'--- KHÁCH HÀNG VÀ PHÒNG ĐANG Ở ---';

-- Kiểm tra phòng đang ở cho từng khách hàng
SELECT 
    kh.CCCD,
    kh.hoTen,
    p.soPhong,
    lp.tenLoaiPhong,
    p.tang,
    p.trangThai,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    ctpdp.soNguoi,
    CASE 
        WHEN DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) < 24 
            THEN CAST(DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' giờ'
        ELSE CAST(DATEDIFF(DAY, GETDATE(), ctpdp.thoiGianTraPhong) AS NVARCHAR) + N' ngày'
    END AS ThoiGianConLai
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON kh.maKhachHang = pdp.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong
JOIN Phong p ON ctpdp.maPhong = p.maPhong
JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE kh.maKhachHang IN ('KH_GIAHAN001', 'KH_GIAHAN002')
    AND p.trangThai = N'Đang ở'
    AND GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong
ORDER BY kh.CCCD, p.tang, p.soPhong;

PRINT '';
PRINT N'========================================';
PRINT N'HƯỚNG DẪN TEST';
PRINT N'========================================';
PRINT N'';
PRINT N'📌 CCCD để test:';
PRINT N'   1. CCCD: 999999999999 (Phạm Văn Đức) - 1 phòng, còn 3 giờ ⚠️';
PRINT N'   2. CCCD: 888888888888 (Võ Thị Em) - 2 phòng, còn 1-2 ngày';
PRINT N'';
PRINT N'✅ Các phòng có trạng thái "Đang ở" và đang trong thời gian lưu trú';
PRINT N'✅ Sau khi gia hạn, thời gian trả phòng sẽ được cập nhật';
PRINT N'✅ Cột "Còn lại" hiển thị thời gian còn lại đến khi trả phòng';
PRINT N'';
PRINT N'========================================';
PRINT N'DỮ LIỆU TEST ĐÃ TẠO THÀNH CÔNG!';
PRINT N'========================================';

GO

