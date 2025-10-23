USE Victorya_Hotel_v4;
GO

-- ===========================
-- XÓA DỮ LIỆU CŨ (NẾU CÓ)
-- ===========================
DELETE FROM DanhGia;
DELETE FROM HuyPhong;
DELETE FROM ChiTietHoaDon_DichVu;
DELETE FROM ChiTietHoaDon;
DELETE FROM HoaDon;
DELETE FROM ChiTietPhieuDatPhong_DichVu;
DELETE FROM ChiTietPhieuDatPhong;
DELETE FROM PhieuDatPhong;
DELETE FROM KhachHang;
DELETE FROM DichVu_LoaiPhong;
DELETE FROM DichVu;
DELETE FROM CaLamViecNhanVien;
DELETE FROM Ca;
DELETE FROM Phong;
DELETE FROM LoaiPhong;
DELETE FROM LoaiDatPhong;
DELETE FROM NhanVien;
DELETE FROM TaiKhoan;
DELETE FROM KhuyenMai;
GO

-- ===========================
-- 1. TÀI KHOẢN (5 tài khoản)
-- ===========================
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro) VALUES
('admin', '$2a$10$9eypGo00I/fQB.j2X0ZyB.adLUV9/Kj/Mt8RUziE6UoFwTCYd.Ivi', N'admin'), --mk admin
('0365271958', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', N'employee'); --mk hoang@123H


-- ===========================
-- 2. NHÂN VIÊN (5 nhân viên)
-- ===========================
INSERT INTO NhanVien (maNhanVien, tenNhanVien, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau, tenDangNhap, trangThai) VALUES
('NV001', N'Nguyễn Huy Hoàng', 1, '2004-08-27', 'nguyenhuyhoang270804@gmail.com', '0901234567', '2020-01-10', 'admin', N'Đang làm'),
('NV002', N'Trần Thị Bình', 0, '1992-08-20', 'ttb@victorya.com', '0912345678', '2021-03-15', '0365271958', N'Đang làm');

-- ===========================
-- 3. CA LÀM VIỆC (định dạng: CA-YYYYMMDD-X)
-- ===========================
INSERT INTO Ca (maCa, ngayBatDau, ngayKetThuc) VALUES
('CA-20251020-1', '2025-10-20', '2025-10-20'), -- Ca sáng 20/10
('CA-20251020-2', '2025-10-20', '2025-10-20'), -- Ca chiều 20/10
('CA-20251020-3', '2025-10-20', '2025-10-20'), -- Ca tối 20/10
('CA-20251021-1', '2025-10-21', '2025-10-21'), -- Ca sáng 21/10
('CA-20251021-2', '2025-10-21', '2025-10-21'), -- Ca chiều 21/10
('CA-20251021-3', '2025-10-21', '2025-10-21'), -- Ca tối 21/10
('CA-20251022-1', '2025-10-22', '2025-10-22'), -- Ca sáng 22/10
('CA-20251022-2', '2025-10-22', '2025-10-22'), -- Ca chiều 22/10
('CA-20251022-3', '2025-10-22', '2025-10-22'), -- Ca tối 22/10
('CA-20251023-1', '2025-10-23', '2025-10-23'), -- Ca sáng 23/10 (HÔM NAY)
('CA-20251023-2', '2025-10-23', '2025-10-23'), -- Ca chiều 23/10
('CA-20251023-3', '2025-10-23', '2025-10-23'); -- Ca tối 23/10

-- ===========================
-- 4. CA LÀM VIỆC NHÂN VIÊN
-- ===========================
INSERT INTO CaLamViecNhanVien (maCaLamViec, maNhanVien, ngay, tienMoCa, tienKetCa, maCa, trangThai) VALUES
-- Ngày 20/10/2025
('CLV001', 'NV002', '2025-10-20', 500000, 2800000, 'CA-20251020-1', N'Đã hoàn thành'),

-- Ngày 21/10/2025
('CLV004', 'NV002', '2025-10-21', 500000, 3100000, 'CA-20251021-1', N'Đã hoàn thành'),
-- Ngày 22/10/2025
('CLV008', 'NV002', '2025-10-22', 2900000, 4700000, 'CA-20251022-2', N'Đã hoàn thành'),
-- Ngày 23/10/2025 (HÔM NAY)
('CLV010', 'NV002', '2025-10-23', 500000, NULL, 'CA-20251023-1', N'Đang làm');

-- ===========================
-- 5. LOẠI PHÒNG (2 loại: Thường và VIP)
-- ===========================
INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao) VALUES
('LP01', N'Phòng Thường', 100000, '2024-01-01'),
('LP02', N'Phòng VIP', 200000, '2024-01-01');

-- ===========================
-- 6. PHÒNG (40 phòng, 8 tầng)
-- Tầng 1-6: Phòng Thường (30 phòng - 5 phòng/tầng)
-- Tầng 7-8: Phòng VIP (10 phòng - 5 phòng/tầng)
-- Định dạng: maPhong = P-XXXX (P-0001 đến P-0040)
-- Định dạng: tenPhong = YXX (Y: tầng, XX: số phòng)
-- ===========================
-- TẦNG 1 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang) VALUES
('P-0001', '101', N'Đang ở', 'LP01', 1),
('P-0002', '102', N'Trống', 'LP01', 1),
('P-0003', '103', N'Đang ở', 'LP01', 1),
('P-0004', '104', N'Trống', 'LP01', 1),
('P-0005', '105', N'Đã đặt', 'LP01', 1);

-- TẦNG 2 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang) VALUES
('P-0006', '201', N'Đang ở', 'LP01', 2),
('P-0007', '202', N'Trống', 'LP01', 2),
('P-0008', '203', N'Trống', 'LP01', 2),
('P-0009', '204', N'Đang ở', 'LP01', 2),
('P-0010', '205', N'Trống', 'LP01', 2);

-- TẦNG 3 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang) VALUES
('P-0011', '301', N'Trống', 'LP01', 3),
('P-0012', '302', N'Đã đặt', 'LP01', 3),
('P-0013', '303', N'Trống', 'LP01', 3),
('P-0014', '304', N'Bảo trì', 'LP01', 3),
('P-0015', '305', N'Trống', 'LP01', 3);

-- TẦNG 4 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang) VALUES
('P-0016', '401', N'Trống', 'LP01', 4),
('P-0017', '402', N'Đang ở', 'LP01', 4),
('P-0018', '403', N'Trống', 'LP01', 4),
('P-0019', '404', N'Trống', 'LP01', 4),
('P-0020', '405', N'Trống', 'LP01', 4);

-- TẦNG 5 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang) VALUES
('P-0021', '501', N'Trống', 'LP01', 5),
('P-0022', '502', N'Trống', 'LP01', 5),
('P-0023', '503', N'Đang ở', 'LP01', 5),
('P-0024', '504', N'Trống', 'LP01', 5),
('P-0025', '505', N'Trống', 'LP01', 5);

-- TẦNG 6 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang) VALUES
('P-0026', '601', N'Trống', 'LP01', 6),
('P-0027', '602', N'Trống', 'LP01', 6),
('P-0028', '603', N'Trống', 'LP01', 6),
('P-0029', '604', N'Trống', 'LP01', 6),
('P-0030', '605', N'Trống', 'LP01', 6);

-- TẦNG 7 - Phòng VIP
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang) VALUES
('P-0031', '701', N'Đang ở', 'LP02', 7),
('P-0032', '702', N'Trống', 'LP02', 7),
('P-0033', '703', N'Đang ở', 'LP02', 7),
('P-0034', '704', N'Trống', 'LP02', 7),
('P-0035', '705', N'Đã đặt', 'LP02', 7);

-- TẦNG 8 - Phòng VIP
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang) VALUES
('P-0036', '801', N'Đang ở', 'LP02', 8),
('P-0037', '802', N'Trống', 'LP02', 8),
('P-0038', '803', N'Đã đặt', 'LP02', 8),
('P-0039', '804', N'Trống', 'LP02', 8),
('P-0040', '805', N'Trống', 'LP02', 8);

-- ===========================
-- 7. LOẠI ĐẶT PHÒNG (Online / Offline)
-- ===========================
INSERT INTO LoaiDatPhong (maLoaiDatPhong, tenLoaiDatPhong, ngayTao) VALUES
('LDP01', N'Online', '2024-01-01'),
('LDP02', N'Offline', '2024-01-01');

-- ===========================
-- 8. DỊCH VỤ (Định dạng: DV-XXXXX - 5 số ngẫu nhiên)
-- ===========================
INSERT INTO DichVu (maDichVu, tenDichVu, gia, moTa, donViTinh) VALUES
('DV-12345', N'Nước suối', 10000, N'Nước suối Lavie 500ml', N'Chai'),
('DV-23456', N'Coca Cola', 15000, N'Nước ngọt Coca Cola 330ml', N'Lon'),
('DV-34567', N'Bia Heineken', 25000, N'Bia Heineken 330ml', N'Lon'),
('DV-45678', N'Snack', 20000, N'Snack khoai tây Poca', N'Gói'),
('DV-56789', N'Trái cây', 50000, N'Đĩa trái cây tươi', N'Đĩa'),
('DV-67890', N'Giặt ủi', 30000, N'Dịch vụ giặt ủi quần áo', N'Kg'),
('DV-78901', N'Đưa đón sân bay', 300000, N'Dịch vụ đưa đón sân bay', N'Lượt'),
('DV-89012', N'Ăn sáng buffet', 100000, N'Buffet sáng đa dạng món', N'Suất'),
('DV-90123', N'Massage', 200000, N'Dịch vụ massage 60 phút', N'Giờ'),
('DV-01234', N'Karaoke', 150000, N'Phòng karaoke theo giờ', N'Giờ');

-- ===========================
-- 9. DỊCH VỤ - LOẠI PHÒNG (CHỈ PHÒNG VIP CÓ DỊCH VỤ)
-- ===========================
INSERT INTO DichVu_LoaiPhong (maDichVu, maLoaiPhong) VALUES
-- Phòng VIP có TẤT CẢ dịch vụ
('DV-12345', 'LP02'),
('DV-23456', 'LP02'),
('DV-34567', 'LP02'),
('DV-45678', 'LP02'),
('DV-56789', 'LP02'),
('DV-67890', 'LP02'),
('DV-78901', 'LP02'),
('DV-89012', 'LP02'),
('DV-90123', 'LP02'),
('DV-01234', 'LP02');

-- ===========================
-- 10. KHÁCH HÀNG (15 khách)
-- ===========================
INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) VALUES
-- Khách đang ở (7 người)
('KH001', '001234567890', N'Nguyễn Văn Hùng', '0987654321', 'nvhung@gmail.com', '2025-10-20'),
('KH002', '002345678901', N'Trần Thị Lan', '0976543210', 'ttlan@gmail.com', '2025-10-21'),
('KH003', '003456789012', N'Lê Minh Tuấn', '0965432109', 'lmtuan@gmail.com', '2025-10-21'),
('KH004', '004567890123', N'Phạm Thu Hà', '0954321098', 'ptha@gmail.com', '2025-10-22'),
('KH005', '005678901234', N'Hoàng Đức Anh', '0943210987', 'hdanh@gmail.com', '2025-10-22'),
('KH006', '006789012345', N'Vũ Thị Mai', '0932109876', 'vtmai@gmail.com', '2025-10-20'),
('KH007', '007890123456', N'Đỗ Văn Nam', '0921098765', 'dvnam@gmail.com', '2025-10-21'),
-- Khách đã đặt (3 người)
('KH008', '008901234567', N'Bùi Thị Hoa', '0910987654', 'bthoa@gmail.com', '2025-10-23'),
('KH009', '009012345678', N'Trương Văn Phong', '0909876543', 'tvphong@gmail.com', '2025-10-23'),
('KH010', '010123456789', N'Ngô Thị Linh', '0898765432', 'ntlinh@gmail.com', '2025-10-23'),
-- Khách đã trả phòng (5 người)
('KH011', '011234567890', N'Phan Văn Đức', '0887654321', 'pvduc@gmail.com', '2025-10-18'),
('KH012', '012345678901', N'Đinh Thị Ngọc', '0876543210', 'dtngoc@gmail.com', '2025-10-19'),
('KH013', '013456789012', N'Mai Văn Tâm', '0865432109', 'mvtam@gmail.com', '2025-10-19'),
('KH014', '014567890123', N'Lý Thị Thu', '0854321098', 'ltthu@gmail.com', '2025-10-20'),
('KH015', '015678901234', N'Tô Văn Long', '0843210987', 'tvlong@gmail.com', '2025-10-20'),
-- Khách mới (thêm 15 khách hàng)
('KH016', '016789012345', N'Nguyễn Thị Lan', '0832109876', 'ntlan@gmail.com', '2025-10-15'),
('KH017', '017890123456', N'Hoàng Văn Minh', '0821098765', 'hvminh@gmail.com', '2025-10-16'),
('KH018', '018901234567', N'Trịnh Thị Hương', '0810987654', 'tthuong@gmail.com', '2025-10-17'),
('KH019', '019012345678', N'Lê Văn Cường', '0909876543', 'lvcuong@gmail.com', '2025-10-18'),
('KH020', '020123456789', N'Phạm Thị Nhung', '0898765432', 'ptnhung@gmail.com', '2025-10-19'),
('KH021', '021234567890', N'Đặng Văn Tuấn', '0887654321', 'dvtuan@gmail.com', '2025-10-20'),
('KH022', '022345678901', N'Võ Thị Loan', '0876543210', 'vtloan@gmail.com', '2025-10-21'),
('KH023', '023456789012', N'Bùi Văn Hải', '0865432109', 'bvhai@gmail.com', '2025-10-22'),
('KH024', '024567890123', N'Dương Thị Kim', '0854321098', 'dtkim@gmail.com', '2025-10-15'),
('KH025', '025678901234', N'Cao Văn Sơn', '0843210987', 'cvson@gmail.com', '2025-10-16'),
('KH026', '026789012345', N'Lưu Thị Trang', '0832109876', 'lttrang@gmail.com', '2025-10-17'),
('KH027', '027890123456', N'Hồ Văn Thắng', '0821098765', 'hvthang@gmail.com', '2025-10-18'),
('KH028', '028901234567', N'Phan Thị Oanh', '0810987654', 'ptoanh@gmail.com', '2025-10-19'),
('KH029', '029012345678', N'Trần Văn Tùng', '0909876543', 'tvtung@gmail.com', '2025-10-20'),
('KH030', '030123456789', N'Ngô Thị Yến', '0898765432', 'ntyen@gmail.com', '2025-10-21');

-- ===========================
-- 11. KHUYẾN MÃI (Định dạng: KM-XXXX)
-- ===========================
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa) VALUES
('KM-0001', N'Giảm 10% cho hóa đơn trên 1 triệu', '2025-10-01', '2025-10-31', N'Đang áp dụng', 0.10, 1000000, 200000),
('KM-0002', N'Giảm 15% cho hóa đơn trên 2 triệu', '2025-10-01', '2025-10-31', N'Đang áp dụng', 0.15, 2000000, 400000),
('KM-0003', N'Giảm 20% cho hóa đơn trên 5 triệu', '2025-10-01', '2025-10-31', N'Đang áp dụng', 0.20, 5000000, 1000000),
('KM-0004', N'Khách hàng VIP - Giảm 25%', '2025-10-01', '2025-12-31', N'Đang áp dụng', 0.25, 3000000, 1500000),
('KM-0005', N'Khuyến mãi hè - Giảm 5%', '2025-09-01', '2025-09-30', N'Hết hạn', 0.05, 500000, 100000);

-- ===========================
-- 12. PHIẾU ĐẶT PHÒNG 
-- Định dạng: PDP-DDMMYYYY-XXX (XXX là số thứ tự trong ngày)
-- ===========================
INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang) VALUES
-- Phiếu đã hoàn thành (5 phiếu - đã thanh toán)
('PDP-18102025-001', '2025-10-18', 'KH011'),
('PDP-19102025-001', '2025-10-19', 'KH012'),
('PDP-19102025-002', '2025-10-19', 'KH013'),
('PDP-20102025-001', '2025-10-20', 'KH014'),
('PDP-20102025-002', '2025-10-20', 'KH015'),
-- Phiếu đang hoạt động (7 phiếu - Đang ở phòng)
('PDP-20102025-003', '2025-10-20', 'KH001'),
('PDP-21102025-001', '2025-10-21', 'KH002'),
('PDP-21102025-002', '2025-10-21', 'KH003'),
('PDP-22102025-001', '2025-10-22', 'KH004'),
('PDP-22102025-002', '2025-10-22', 'KH005'),
('PDP-20102025-004', '2025-10-20', 'KH006'),
('PDP-21102025-003', '2025-10-21', 'KH007'),
-- Phiếu đã đặt (3 phiếu - chưa nhận phòng)
('PDP-23102025-001', '2025-10-23', 'KH008'),
('PDP-23102025-002', '2025-10-23', 'KH009'),
('PDP-23102025-003', '2025-10-23', 'KH010'),
-- Phiếu mới thêm (20 phiếu)
('PDP-15102025-001', '2025-10-15', 'KH016'),
('PDP-15102025-002', '2025-10-15', 'KH024'),
('PDP-16102025-001', '2025-10-16', 'KH017'),
('PDP-16102025-002', '2025-10-16', 'KH025'),
('PDP-17102025-001', '2025-10-17', 'KH018'),
('PDP-17102025-002', '2025-10-17', 'KH026'),
('PDP-18102025-002', '2025-10-18', 'KH019'),
('PDP-18102025-003', '2025-10-18', 'KH027'),
('PDP-19102025-003', '2025-10-19', 'KH020'),
('PDP-19102025-004', '2025-10-19', 'KH028'),
('PDP-20102025-005', '2025-10-20', 'KH021'),
('PDP-20102025-006', '2025-10-20', 'KH029'),
('PDP-21102025-004', '2025-10-21', 'KH022'),
('PDP-21102025-005', '2025-10-21', 'KH030'),
('PDP-22102025-003', '2025-10-22', 'KH023'),
('PDP-22102025-004', '2025-10-22', 'KH016'),
('PDP-23102025-004', '2025-10-23', 'KH017'),
('PDP-23102025-005', '2025-10-23', 'KH018'),
('PDP-23102025-006', '2025-10-23', 'KH019'),
('PDP-23102025-007', '2025-10-23', 'KH020');

-- ===========================
-- 13. CHI TIẾT PHIẾU ĐẶT PHÒNG
-- ===========================
-- Phiếu đã hoàn thành (đã trả phòng)
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi) VALUES
('PDP-18102025-001', 'P-0006', '2025-10-18 14:15:00', '2025-10-19 11:45:00', 'LDP02', 2),
('PDP-19102025-001', '2025-10-19 08:10:00', '2025-10-19 19:50:00', 'LDP01', 1),
('PDP-19102025-002', 'P-0031', '2025-10-19 15:20:00', '2025-10-20 11:55:00', 'LDP02', 2),
('PDP-20102025-001', 'P-0017', '2025-10-20 10:05:00', '2025-10-20 21:50:00', 'LDP01', 2),
('PDP-20102025-002', 'P-0009', '2025-10-20 13:10:00', '2025-10-21 11:45:00', 'LDP02', 1),
-- Phiếu đã hoàn thành mới (15 phiếu)
('PDP-15102025-001', 'P-0002', '2025-10-15 10:00:00', '2025-10-16 10:00:00', 'LDP02', 2),
('PDP-15102025-002', 'P-0004', '2025-10-15 14:00:00', '2025-10-16 12:00:00', 'LDP01', 1),
('PDP-16102025-001', 'P-0007', '2025-10-16 09:00:00', '2025-10-17 09:00:00', 'LDP02', 2),
('PDP-16102025-002', 'P-0010', '2025-10-16 15:00:00', '2025-10-17 11:00:00', 'LDP01', 2),
('PDP-17102025-001', 'P-0013', '2025-10-17 11:00:00', '2025-10-18 11:00:00', 'LDP02', 1),
('PDP-17102025-002', 'P-0015', '2025-10-17 16:00:00', '2025-10-18 12:00:00', 'LDP01', 2),
('PDP-18102025-002', 'P-0019', '2025-10-18 08:00:00', '2025-10-19 10:00:00', 'LDP01', 1),
('PDP-18102025-003', 'P-0021', '2025-10-18 13:00:00', '2025-10-19 13:00:00', 'LDP02', 2),
('PDP-19102025-003', 'P-0025', '2025-10-19 10:00:00', '2025-10-20 10:00:00', 'LDP01', 2),
('PDP-19102025-004', 'P-0027', '2025-10-19 14:00:00', '2025-10-20 14:00:00', 'LDP02', 1),
('PDP-20102025-005', 'P-0029', '2025-10-20 09:00:00', '2025-10-21 09:00:00', 'LDP01', 2),
('PDP-20102025-006', 'P-0032', '2025-10-20 15:00:00', '2025-10-21 15:00:00', 'LDP02', 3),
('PDP-21102025-004', 'P-0034', '2025-10-21 10:00:00', '2025-10-22 10:00:00', 'LDP02', 2),
('PDP-21102025-005', 'P-0036', '2025-10-21 14:00:00', '2025-10-22 14:00:00', 'LDP01', 1),
('PDP-22102025-003', 'P-0038', '2025-10-22 11:00:00', '2025-10-23 11:00:00', 'LDP02', 2);

-- Phiếu Đang ở (7 phòng)
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi) VALUES
('PDP-20102025-003', 'P-0001', '2025-10-20 14:10:00', '2025-10-29 14:10:00', 'LDP01', 2),
('PDP-21102025-001', 'P-0003', '2025-10-21 13:05:00', '2025-10-29 14:10:00', 'LDP01', 2),
('PDP-21102025-002', 'P-0006', '2025-10-21 15:10:00', '2025-10-29 14:10:00', 'LDP02', 1),
('PDP-22102025-001', 'P-0009', '2025-10-22 10:15:00', '2025-10-29 14:10:00', 'LDP02', 2),
('PDP-22102025-002', 'P-0023', '2025-10-22 14:10:00', '2025-10-29 14:10:00', 'LDP01', 1),
('PDP-20102025-004', 'P-0031', '2025-10-20 16:10:00', '2025-10-29 14:10:00', 'LDP02', 3),
('PDP-21102025-003', 'P-0033', '2025-10-21 17:10:00', '2025-10-29 14:10:00', 'LDP02', 2);

-- Phiếu đặt trước (8 phòng)
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi) VALUES
('PDP-23102025-001', 'P-0005', '2025-11-01 14:10:00', '2025-11-20 14:10:00', 'LDP01', 2),
('PDP-23102025-002', 'P-0012', '2025-11-02 14:10:00', '2025-11-20 14:10:00', 'LDP02', 2),
('PDP-23102025-003', 'P-0035', '2025-11-03 14:10:00', '2025-11-20 14:10:00', 'LDP01', 3),
('PDP-22102025-004', 'P-0016', '2025-10-25 10:00:00', '2025-10-27 10:00:00', 'LDP01', 2),
('PDP-23102025-004', 'P-0018', '2025-10-26 14:00:00', '2025-10-28 14:00:00', 'LDP02', 1),
('PDP-23102025-005', 'P-0020', '2025-10-27 09:00:00', '2025-10-29 09:00:00', 'LDP01', 2),
('PDP-23102025-006', 'P-0024', '2025-10-28 15:00:00', '2025-10-30 15:00:00', 'LDP02', 2),
('PDP-23102025-007', 'P-0026', '2025-10-29 11:00:00', '2025-10-31 11:00:00', 'LDP01', 1);

-- ===========================
-- 14. CHI TIẾT PHIẾU ĐẶT PHÒNG - DỊCH VỤ (CHỈ PHÒNG VIP)
-- ===========================
-- Phiếu đã hoàn thành - Phòng VIP
INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu, soLuong) VALUES
('PDP-19102025-002', 'P-0031', 'DV-12345', 3),
('PDP-19102025-002', 'P-0031', 'DV-89012', 2),
('PDP-19102025-002', 'P-0031', 'DV-90123', 1);

-- Phiếu Đang ở - Phòng VIP
INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu, soLuong) VALUES
-- Phòng 701 (P-0031)
('PDP-20102025-004', 'P-0031', 'DV-12345', 5),
('PDP-20102025-004', 'P-0031', 'DV-23456', 4),
('PDP-20102025-004', 'P-0031', 'DV-89012', 3),
('PDP-20102025-004', 'P-0031', 'DV-90123', 2),
('PDP-20102025-004', 'P-0031', 'DV-78901', 1),
-- Phòng 703 (P-0033)
('PDP-21102025-003', 'P-0033', 'DV-12345', 6),
('PDP-21102025-003', 'P-0033', 'DV-45678', 3),
('PDP-21102025-003', 'P-0033', 'DV-89012', 2),
('PDP-21102025-003', 'P-0033', 'DV-01234', 1);

-- ===========================
-- 15. HÓA ĐƠN (Định dạng: HD-YYYYMMDD-XXXXX)
-- ===========================
INSERT INTO HoaDon (maHoaDon, ngayDat, maKhachHang, maNhanVien, maKhuyenMai, ngayTao, trangThai, tongTien) VALUES
-- Hóa đơn đã thanh toán (20 hóa đơn)
('HD-20251019-00001', '2025-10-18 14:00:00', 'KH011', 'NV002', NULL, '2025-10-19', N'Đã thanh toán', 2420000),
('HD-20251019-00002', '2025-10-19 08:00:00', 'KH012', 'NV002', 'KM-0001', '2025-10-19', N'Đã thanh toán', 1188000),
('HD-20251020-00001', '2025-10-19 15:00:00', 'KH013', 'NV002', NULL, '2025-10-20', N'Đã thanh toán', 2650000),
('HD-20251020-00002', '2025-10-20 10:00:00', 'KH014', 'NV002', NULL, '2025-10-20', N'Đã thanh toán', 1320000),
('HD-20251021-00001', '2025-10-20 13:00:00', 'KH015', 'NV002', 'KM-0001', '2025-10-21', N'Đã thanh toán', 2530000),
('HD-20251016-00001', '2025-10-15 10:00:00', 'KH016', 'NV001', NULL, '2025-10-16', N'Đã thanh toán', 1100000),
('HD-20251016-00002', '2025-10-15 14:00:00', 'KH024', 'NV001', 'KM-0001', '2025-10-16', N'Đã thanh toán', 990000),
('HD-20251017-00001', '2025-10-16 09:00:00', 'KH017', 'NV002', NULL, '2025-10-17', N'Đã thanh toán', 1100000),
('HD-20251017-00002', '2025-10-16 15:00:00', 'KH025', 'NV002', NULL, '2025-10-17', N'Đã thanh toán', 880000),
('HD-20251018-00001', '2025-10-17 11:00:00', 'KH018', 'NV001', 'KM-0002', '2025-10-18', N'Đã thanh toán', 2040000),
('HD-20251018-00002', '2025-10-17 16:00:00', 'KH026', 'NV001', NULL, '2025-10-18', N'Đã thanh toán', 880000),
('HD-20251019-00003', '2025-10-18 08:00:00', 'KH019', 'NV002', NULL, '2025-10-19', N'Đã thanh toán', 1210000),
('HD-20251019-00004', '2025-10-18 13:00:00', 'KH027', 'NV002', 'KM-0001', '2025-10-19', N'Đã thanh toán', 1980000),
('HD-20251020-00003', '2025-10-19 10:00:00', 'KH020', 'NV001', NULL, '2025-10-20', N'Đã thanh toán', 1100000),
('HD-20251020-00004', '2025-10-19 14:00:00', 'KH028', 'NV001', NULL, '2025-10-20', N'Đã thanh toán', 1100000),
('HD-20251021-00002', '2025-10-20 09:00:00', 'KH021', 'NV002', 'KM-0001', '2025-10-21', N'Đã thanh toán', 990000),
('HD-20251021-00003', '2025-10-20 15:00:00', 'KH029', 'NV002', NULL, '2025-10-21', N'Đã thanh toán', 2200000),
('HD-20251022-00001', '2025-10-21 10:00:00', 'KH022', 'NV001', 'KM-0002', '2025-10-22', N'Đã thanh toán', 2040000),
('HD-20251022-00002', '2025-10-21 14:00:00', 'KH030', 'NV001', NULL, '2025-10-22', N'Đã thanh toán', 1100000),
('HD-20251023-00001', '2025-10-22 11:00:00', 'KH023', 'NV002', NULL, '2025-10-23', N'Đã thanh toán', 1100000);

-- ===========================
-- 16. CHI TIẾT HÓA ĐƠN
-- ===========================
INSERT INTO ChiTietHoaDon (maHoaDon, maPhieuDatPhong, ngayTao, tongTien) VALUES
('HD-20251019-00001', 'PDP-18102025-001', '2025-10-19', 2420000),
('HD-20251019-00002', 'PDP-19102025-001', '2025-10-19', 1188000),
('HD-20251020-00001', 'PDP-19102025-002', '2025-10-20', 2650000),
('HD-20251020-00002', 'PDP-20102025-001', '2025-10-20', 1320000),
('HD-20251021-00001', 'PDP-20102025-002', '2025-10-21', 2530000),
('HD-20251016-00001', 'PDP-15102025-001', '2025-10-16', 1100000),
('HD-20251016-00002', 'PDP-15102025-002', '2025-10-16', 990000),
('HD-20251017-00001', 'PDP-16102025-001', '2025-10-17', 1100000),
('HD-20251017-00002', 'PDP-16102025-002', '2025-10-17', 880000),
('HD-20251018-00001', 'PDP-17102025-001', '2025-10-18', 2040000),
('HD-20251018-00002', 'PDP-17102025-002', '2025-10-18', 880000),
('HD-20251019-00003', 'PDP-18102025-002', '2025-10-19', 1210000),
('HD-20251019-00004', 'PDP-18102025-003', '2025-10-19', 1980000),
('HD-20251020-00003', 'PDP-19102025-003', '2025-10-20', 1100000),
('HD-20251020-00004', 'PDP-19102025-004', '2025-10-20', 1100000),
('HD-20251021-00002', 'PDP-20102025-005', '2025-10-21', 990000),
('HD-20251021-00003', 'PDP-20102025-006', '2025-10-21', 2200000),
('HD-20251022-00001', 'PDP-21102025-004', '2025-10-22', 2040000),
('HD-20251022-00002', 'PDP-21102025-005', '2025-10-22', 1100000),
('HD-20251023-00001', 'PDP-22102025-003', '2025-10-23', 1100000);

-- ===========================
-- 17. CHI TIẾT HÓA ĐƠN - DỊCH VỤ (Tự động từ trigger)
-- ===========================
-- Trigger sẽ tự động thêm dữ liệu từ ChiTietPhieuDatPhong_DichVu

-- ===========================
-- 18. HỦY PHÒNG (Không có trong data mẫu này)
-- ===========================

-- ===========================
-- 19. ĐÁNH GIÁ (5 đánh giá từ khách đã trả phòng)
-- ===========================
INSERT INTO DanhGia (maKhachHang, maPhong, noiDung, ngayTao) VALUES
('KH011', 'P-0006', N'Phòng sạch sẽ, thoáng mát. Nhân viên nhiệt tình. Sẽ quay lại!', '2025-10-19 13:00:00'),
('KH012', 'P-0001', N'Giá cả hợp lý, dịch vụ tốt. Hài lòng với trải nghiệm.', '2025-10-19 21:00:00'),
('KH013', 'P-0031', N'Phòng VIP rất đẹp, dịch vụ 5 sao. Đáng đồng tiền!', '2025-10-20 13:00:00'),
('KH014', 'P-0017', N'Phòng hơi nhỏ nhưng tiện nghi đầy đủ, wifi nhanh.', '2025-10-20 23:00:00'),
('KH015', 'P-0009', N'Nhân viên nhiệt tình, phòng đẹp. Sẽ giới thiệu bạn bè.', '2025-10-21 13:00:00');


-- Xem tất cả phiếu đặt phòng và chi tiết của khách hàng theo CCCD
DECLARE @CCCD VARCHAR(20) = '001234567890';

