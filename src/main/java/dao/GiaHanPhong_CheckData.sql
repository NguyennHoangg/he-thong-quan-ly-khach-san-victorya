-- Script kiểm tra dữ liệu hiện có trong database
USE Victorya_Hotel;
GO

PRINT '========== KIỂM TRA DỮ LIỆU ==========';

-- 1. Kiểm tra Khách hàng
PRINT '';
PRINT '1. Danh sách Khách hàng:';
SELECT TOP 5 maKhachHang, CCCD, hoTen, soDienThoai FROM KhachHang;

-- 2. Kiểm tra Phòng
PRINT '';
PRINT '2. Danh sách Phòng:';
SELECT TOP 5 maPhong, tenPhong, trangThai, maLoaiPhong FROM Phong;

-- 3. Kiểm tra Loại phòng
PRINT '';
PRINT '3. Danh sách Loại phòng:';
SELECT TOP 5 maLoaiPhong, tenLoaiPhong, gia FROM LoaiPhong;

-- 4. Kiểm tra Loại đặt phòng
PRINT '';
PRINT '4. Danh sách Loại đặt phòng:';
SELECT TOP 5 maLoaiDatPhong, tenLoaiDatPhong FROM LoaiDatPhong;

-- 5. Kiểm tra Dịch vụ
PRINT '';
PRINT '5. Danh sách Dịch vụ:';
SELECT TOP 5 maDichVu, tenDichVu, gia FROM DichVu;

-- 6. Kiểm tra Phiếu đặt phòng
PRINT '';
PRINT '6. Danh sách Phiếu đặt phòng:';
SELECT TOP 5 maPhieuDatPhong, maKhachHang, ngayTao FROM PhieuDatPhong;

-- 7. Kiểm tra Chi tiết phiếu đặt phòng đang hoạt động
PRINT '';
PRINT '7. Chi tiết phiếu đặt phòng đang hoạt động:';
SELECT ctpdp.maPhieuDatPhong, ctpdp.maPhong, 
       ctpdp.gioBatDau, ctpdp.gioKetThuc,
       kh.CCCD, kh.hoTen
FROM ChiTietPhieuDatPhong ctpdp
JOIN PhieuDatPhong pdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong
JOIN KhachHang kh ON kh.maKhachHang = pdp.maKhachHang
WHERE GETDATE() BETWEEN ctpdp.gioBatDau AND ctpdp.gioKetThuc;

PRINT '';
PRINT '========== KẾT THÚC KIỂM TRA ==========';

