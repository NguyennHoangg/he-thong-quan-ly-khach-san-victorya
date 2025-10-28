-- ===========================
-- DỮ LIỆU TEST CHO TRANG NHẬN PHÒNG
-- ===========================
-- File này chứa dữ liệu test để kiểm thử chức năng nhận phòng
-- Bao gồm: Khách hàng, Phiếu đặt phòng, Phòng có trạng thái "Đã đặt"

USE Victorya_Hotel;
GO

-- ===========================
-- 1. KHÁCH HÀNG TEST
-- ===========================

-- Khách hàng 1: Có 1 phòng chờ nhận
IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE CCCD = '123456789012')
BEGIN
    INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao)
    VALUES ('KH_TEST001', '123456789012', N'Nguyễn Văn An', '0901234567', 'nguyenvanan@gmail.com', CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo khách hàng test 1: Nguyễn Văn An (CCCD: 123456789012)';
END
ELSE
BEGIN
    UPDATE KhachHang 
    SET hoTen = N'Nguyễn Văn An',
        soDienThoai = '0901234567',
        email = 'nguyenvanan@gmail.com'
    WHERE CCCD = '123456789012';
    PRINT N'⚠️ Khách hàng test 1 đã tồn tại - Đã cập nhật thông tin';
END

-- Khách hàng 2: Có nhiều phòng chờ nhận
IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE CCCD = '987654321098')
BEGIN
    INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao)
    VALUES ('KH_TEST002', '987654321098', N'Trần Thị Bình', '0912345678', 'tranthibinh@gmail.com', CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo khách hàng test 2: Trần Thị Bình (CCCD: 987654321098)';
END
ELSE
BEGIN
    UPDATE KhachHang 
    SET hoTen = N'Trần Thị Bình',
        soDienThoai = '0912345678',
        email = 'tranthibinh@gmail.com'
    WHERE CCCD = '987654321098';
    PRINT N'⚠️ Khách hàng test 2 đã tồn tại - Đã cập nhật thông tin';
END

-- Khách hàng 3: Có phòng chờ nhận và phòng đang ở (để test phân biệt)
IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE CCCD = '456789123456')
BEGIN
    INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao)
    VALUES ('KH_TEST003', '456789123456', N'Lê Hoàng Cường', '0923456789', 'lehoangcuong@gmail.com', CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo khách hàng test 3: Lê Hoàng Cường (CCCD: 456789123456)';
END
ELSE
BEGIN
    UPDATE KhachHang 
    SET hoTen = N'Lê Hoàng Cường',
        soDienThoai = '0923456789',
        email = 'lehoangcuong@gmail.com'
    WHERE CCCD = '456789123456';
    PRINT N'⚠️ Khách hàng test 3 đã tồn tại - Đã cập nhật thông tin';
END

-- ===========================
-- 2. TẠO LOẠI PHÒNG NẾU CHƯA CÓ
-- ===========================

-- Tạo loại phòng Standard nếu chưa có
IF NOT EXISTS (SELECT 1 FROM LoaiPhong WHERE maLoaiPhong = 'LP_STD')
BEGIN
    INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao)
    VALUES ('LP_STD', N'Standard', 500000.00, CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại phòng Standard';
END

-- Tạo loại phòng VIP nếu chưa có
IF NOT EXISTS (SELECT 1 FROM LoaiPhong WHERE maLoaiPhong = 'LP_VIP')
BEGIN
    INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao)
    VALUES ('LP_VIP', N'VIP', 1000000.00, CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại phòng VIP';
END

-- ===========================
-- 3. TẠO PHÒNG TEST NẾU CHƯA CÓ
-- ===========================

-- Tạo phòng P101 nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P101')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P101', N'101', N'Sẵn sàng', 'LP_STD', 1);
    PRINT N'✅ Đã tạo phòng P101 (Tầng 1 - Standard)';
END

-- Tạo phòng P102 nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P102')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P102', N'102', N'Sẵn sàng', 'LP_STD', 1);
    PRINT N'✅ Đã tạo phòng P102 (Tầng 1 - Standard)';
END

-- Tạo phòng P103 nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P103')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P103', N'103', N'Sẵn sàng', 'LP_STD', 1);
    PRINT N'✅ Đã tạo phòng P103 (Tầng 1 - Standard)';
END

-- Tạo phòng P201 nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P201')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P201', N'201', N'Sẵn sàng', 'LP_VIP', 2);
    PRINT N'✅ Đã tạo phòng P201 (Tầng 2 - VIP)';
END

-- Tạo phòng P202 nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P202')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P202', N'202', N'Sẵn sàng', 'LP_VIP', 2);
    PRINT N'✅ Đã tạo phòng P202 (Tầng 2 - VIP)';
END

-- Tạo phòng P301 nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = 'P301')
BEGIN
    INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang)
    VALUES ('P301', N'301', N'Sẵn sàng', 'LP_VIP', 3);
    PRINT N'✅ Đã tạo phòng P301 (Tầng 3 - VIP)';
END

-- ===========================
-- 4. CẬP NHẬT TRẠNG THÁI PHÒNG
-- ===========================

-- Cập nhật các phòng về trạng thái "Đã đặt"
UPDATE Phong SET trangThai = N'Đã đặt' WHERE maPhong IN ('P101', 'P102', 'P103', 'P201', 'P202', 'P301');
IF @@ROWCOUNT > 0
    PRINT N'✅ Đã cập nhật ' + CAST(@@ROWCOUNT AS NVARCHAR(10)) + N' phòng về trạng thái "Đã đặt"';
ELSE
    PRINT N'⚠️ Không có phòng nào được cập nhật trạng thái';

-- ===========================
-- 5. TẠO LOẠI ĐẶT PHÒNG NẾU CHƯA CÓ
-- ===========================

-- Tạo loại đặt phòng "Theo ngày" nếu chưa có
IF NOT EXISTS (SELECT 1 FROM LoaiDatPhong WHERE maLoaiDatPhong = 'LDP_NGAY')
BEGIN
    INSERT INTO LoaiDatPhong (maLoaiDatPhong, tenLoaiDatPhong, ngayTao)
    VALUES ('LDP_NGAY', N'Theo ngày', CAST(GETDATE() AS DATE));
    PRINT N'✅ Đã tạo loại đặt phòng "Theo ngày"';
END

-- ===========================
-- 6. PHIẾU ĐẶT PHÒNG TEST
-- ===========================

-- Phiếu đặt phòng 1: Cho khách hàng 1 (1 phòng)
IF NOT EXISTS (SELECT 1 FROM PhieuDatPhong WHERE maPhieuDatPhong = 'PDP_TEST001')
BEGIN
    INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang)
    VALUES ('PDP_TEST001', CAST(GETDATE() AS DATE), 'KH_TEST001');
    PRINT N'✅ Đã tạo phiếu đặt phòng test 1: PDP_TEST001';
END
ELSE
BEGIN
    PRINT N'⚠️ Phiếu đặt phòng test 1 đã tồn tại';
END

-- Phiếu đặt phòng 2: Cho khách hàng 2 (nhiều phòng)
IF NOT EXISTS (SELECT 1 FROM PhieuDatPhong WHERE maPhieuDatPhong = 'PDP_TEST002')
BEGIN
    INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang)
    VALUES ('PDP_TEST002', CAST(GETDATE() AS DATE), 'KH_TEST002');
    PRINT N'✅ Đã tạo phiếu đặt phòng test 2: PDP_TEST002';
END
ELSE
BEGIN
    PRINT N'⚠️ Phiếu đặt phòng test 2 đã tồn tại';
END

-- Phiếu đặt phòng 3: Cho khách hàng 3 (kết hợp đã đặt và đang ở)
IF NOT EXISTS (SELECT 1 FROM PhieuDatPhong WHERE maPhieuDatPhong = 'PDP_TEST003')
BEGIN
    INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang)
    VALUES ('PDP_TEST003', CAST(GETDATE() AS DATE), 'KH_TEST003');
    PRINT N'✅ Đã tạo phiếu đặt phòng test 3: PDP_TEST003';
END
ELSE
BEGIN
    PRINT N'⚠️ Phiếu đặt phòng test 3 đã tồn tại';
END

-- ===========================
-- 7. CHI TIẾT PHIẾU ĐẶT PHÒNG - PHÒNG CHỜ NHẬN
-- ===========================

-- Chi tiết 1: Khách hàng 1 - Phòng 101 (Đã đặt, đến giờ nhận)
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = 'PDP_TEST001' AND maPhong = 'P101')
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
    VALUES (
        'PDP_TEST001', 
        'P101',
        DATEADD(HOUR, -2, GETDATE()), -- Đã đến giờ nhận (2 giờ trước)
        DATEADD(DAY, 2, GETDATE()),   -- Trả phòng sau 2 ngày
        'LDP_NGAY',
        2
    );
    PRINT N'✅ Đã tạo chi tiết phòng P101 cho khách hàng 1 (CHỜ NHẬN)';
END
ELSE
BEGIN
    -- Cập nhật thời gian nếu đã tồn tại
    UPDATE ChiTietPhieuDatPhong 
    SET thoiGianNhanPhong = DATEADD(HOUR, -2, GETDATE()),
        thoiGianTraPhong = DATEADD(DAY, 2, GETDATE())
    WHERE maPhieuDatPhong = 'PDP_TEST001' AND maPhong = 'P101';
    PRINT N'⚠️ Đã cập nhật thời gian cho phòng P101';
END

-- Chi tiết 2: Khách hàng 2 - Phòng 102 (Đã đặt, đến giờ nhận)
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = 'PDP_TEST002' AND maPhong = 'P102')
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
    VALUES (
        'PDP_TEST002', 
        'P102',
        DATEADD(HOUR, -1, GETDATE()), -- Đã đến giờ nhận (1 giờ trước)
        DATEADD(DAY, 1, GETDATE()),   -- Trả phòng sau 1 ngày
        'LDP_NGAY',
        1
    );
    PRINT N'✅ Đã tạo chi tiết phòng P102 cho khách hàng 2 (CHỜ NHẬN)';
END
ELSE
BEGIN
    UPDATE ChiTietPhieuDatPhong 
    SET thoiGianNhanPhong = DATEADD(HOUR, -1, GETDATE()),
        thoiGianTraPhong = DATEADD(DAY, 1, GETDATE())
    WHERE maPhieuDatPhong = 'PDP_TEST002' AND maPhong = 'P102';
    PRINT N'⚠️ Đã cập nhật thời gian cho phòng P102';
END

-- Chi tiết 3: Khách hàng 2 - Phòng 201 (Đã đặt, đến giờ nhận)
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = 'PDP_TEST002' AND maPhong = 'P201')
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
    VALUES (
        'PDP_TEST002', 
        'P201',
        DATEADD(MINUTE, -30, GETDATE()), -- Đã đến giờ nhận (30 phút trước)
        DATEADD(DAY, 3, GETDATE()),      -- Trả phòng sau 3 ngày
        'LDP_NGAY',
        2
    );
    PRINT N'✅ Đã tạo chi tiết phòng P201 cho khách hàng 2 (CHỜ NHẬN)';
END
ELSE
BEGIN
    UPDATE ChiTietPhieuDatPhong 
    SET thoiGianNhanPhong = DATEADD(MINUTE, -30, GETDATE()),
        thoiGianTraPhong = DATEADD(DAY, 3, GETDATE())
    WHERE maPhieuDatPhong = 'PDP_TEST002' AND maPhong = 'P201';
    PRINT N'⚠️ Đã cập nhật thời gian cho phòng P201';
END

-- Chi tiết 4: Khách hàng 2 - Phòng 202 (Đã đặt, đúng giờ nhận)
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = 'PDP_TEST002' AND maPhong = 'P202')
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
    VALUES (
        'PDP_TEST002', 
        'P202',
        GETDATE(),                    -- Đúng giờ nhận
        DATEADD(DAY, 2, GETDATE()),   -- Trả phòng sau 2 ngày
        'LDP_NGAY',
        3
    );
    PRINT N'✅ Đã tạo chi tiết phòng P202 cho khách hàng 2 (CHỜ NHẬN)';
END
ELSE
BEGIN
    UPDATE ChiTietPhieuDatPhong 
    SET thoiGianNhanPhong = GETDATE(),
        thoiGianTraPhong = DATEADD(DAY, 2, GETDATE())
    WHERE maPhieuDatPhong = 'PDP_TEST002' AND maPhong = 'P202';
    PRINT N'⚠️ Đã cập nhật thời gian cho phòng P202';
END

-- Chi tiết 5: Khách hàng 3 - Phòng 301 (Đã đặt, chờ nhận)
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = 'PDP_TEST003' AND maPhong = 'P301')
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
    VALUES (
        'PDP_TEST003', 
        'P301',
        DATEADD(HOUR, -3, GETDATE()), -- Đã đến giờ nhận (3 giờ trước)
        DATEADD(DAY, 5, GETDATE()),   -- Trả phòng sau 5 ngày
        'LDP_NGAY',
        2
    );
    PRINT N'✅ Đã tạo chi tiết phòng P301 cho khách hàng 3 (CHỜ NHẬN)';
END
ELSE
BEGIN
    UPDATE ChiTietPhieuDatPhong 
    SET thoiGianNhanPhong = DATEADD(HOUR, -3, GETDATE()),
        thoiGianTraPhong = DATEADD(DAY, 5, GETDATE())
    WHERE maPhieuDatPhong = 'PDP_TEST003' AND maPhong = 'P301';
    PRINT N'⚠️ Đã cập nhật thời gian cho phòng P301';
END

-- ===========================
-- 8. THÊM PHÒNG ĐANG Ở CHO KHÁCH HÀNG 3 (ĐỂ TEST PHÂN BIỆT)
-- ===========================

-- Cập nhật phòng P103 về trạng thái "Đang ở"
UPDATE Phong SET trangThai = N'Đang ở' WHERE maPhong = 'P103';

-- Thêm chi tiết phòng đang ở cho khách hàng 3
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = 'PDP_TEST003' AND maPhong = 'P103')
BEGIN
    INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi)
    VALUES (
        'PDP_TEST003', 
        'P103',
        DATEADD(DAY, -1, GETDATE()),  -- Đã nhận từ 1 ngày trước
        DATEADD(DAY, 2, GETDATE()),   -- Trả phòng sau 2 ngày
        'LDP_NGAY',
        2
    );
    PRINT N'✅ Đã tạo chi tiết phòng P103 cho khách hàng 3 (ĐANG Ở - không hiển thị trong nhận phòng)';
END
ELSE
BEGIN
    UPDATE ChiTietPhieuDatPhong 
    SET thoiGianNhanPhong = DATEADD(DAY, -1, GETDATE()),
        thoiGianTraPhong = DATEADD(DAY, 2, GETDATE())
    WHERE maPhieuDatPhong = 'PDP_TEST003' AND maPhong = 'P103';
    PRINT N'⚠️ Đã cập nhật thời gian cho phòng P103';
END

-- ===========================
-- 9. KIỂM TRA DỮ LIỆU ĐÃ TẠO
-- ===========================

PRINT '';
PRINT N'========================================';
PRINT N'TỔNG KẾT DỮ LIỆU TEST CHO TRANG NHẬN PHÒNG';
PRINT N'========================================';

-- Kiểm tra khách hàng
SELECT 
    CCCD,
    hoTen,
    soDienThoai,
    email
FROM KhachHang
WHERE maKhachHang IN ('KH_TEST001', 'KH_TEST002', 'KH_TEST003');

PRINT '';
PRINT N'--- KHÁCH HÀNG VÀ PHÒNG CHỜ NHẬN ---';

-- Kiểm tra phòng chờ nhận cho từng khách hàng
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
        WHEN ctpdp.thoiGianNhanPhong <= GETDATE() AND ctpdp.thoiGianTraPhong >= GETDATE() 
            THEN N'✅ SẴN SÀNG NHẬN'
        ELSE N'⏳ Chưa đến giờ'
    END AS TrangThaiNhan
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON kh.maKhachHang = pdp.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong
JOIN Phong p ON ctpdp.maPhong = p.maPhong
JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
WHERE kh.maKhachHang IN ('KH_TEST001', 'KH_TEST002', 'KH_TEST003')
    AND p.trangThai = N'Đã đặt'
    AND ctpdp.thoiGianNhanPhong <= GETDATE()
    AND ctpdp.thoiGianTraPhong >= GETDATE()
ORDER BY kh.CCCD, p.tang, p.soPhong;

PRINT '';
PRINT N'========================================';
PRINT N'HƯỚNG DẪN TEST';
PRINT N'========================================';
PRINT N'';
PRINT N'📌 CCCD để test:';
PRINT N'   1. CCCD: 123456789012 (Nguyễn Văn An) - 1 phòng chờ nhận';
PRINT N'   2. CCCD: 987654321098 (Trần Thị Bình) - 3 phòng chờ nhận';
PRINT N'   3. CCCD: 456789123456 (Lê Hoàng Cường) - 1 phòng chờ nhận + 1 phòng đang ở';
PRINT N'';
PRINT N'✅ Các phòng có trạng thái "Đã đặt" và đã đến giờ nhận';
PRINT N'✅ Sau khi nhận phòng, trạng thái sẽ chuyển thành "Đang ở"';
PRINT N'';
PRINT N'========================================';
PRINT N'DỮ LIỆU TEST ĐÃ TẠO THÀNH CÔNG!';
PRINT N'========================================';

GO

