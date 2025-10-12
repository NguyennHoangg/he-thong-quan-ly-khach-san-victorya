use Victorya_Hotel
insert into TaiKhoan(tenDangNhap, matKhau,vaiTro) values (N'admin', N'$2a$10$8BqAEPpsBMYFPk5n0BvIxOOiMz64RYP3rEEENOG.QeCIQ31MOxjLu', N'admin');
insert into TaiKhoan(tenDangNhap, matKhau,vaiTro) values (N'hoang', N'$2a$10$Geltzmv7X/.biGGgKZdJuObHlfF9PxUQdS3ZEnfngKtBDjTt.UjG2', N'employee');


-- ===========================
-- SAMPLE DATA (except TaiKhoan)
-- ===========================

-- NhanVien (3 rows)
INSERT INTO NhanVien (maNhanVien, tenNhanVien, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau, tenDangNhap)
VALUES
('NV001', N'Nguyễn Văn A', 1, '1990-05-10', 'nva@example.com', '0912345678', '2020-01-01', NULL),
('NV002', N'Trần Thị B', 0, '1992-08-20', 'ttb@example.com', '0987654321', '2021-06-15', NULL),
('NV003', N'Lê Công C', 1, '1988-12-05', 'lcc@example.com', '0901122334', '2019-11-01', NULL);

-- Ca (3 rows)
INSERT INTO Ca (maCa, ngayBatDau, ngayKetThuc)
VALUES
('C1', '2025-01-01', '2025-01-01'),
('C2', '2025-01-02', '2025-01-02'),
('C3', '2025-01-03', '2025-01-03');

-- CaLamViecNhanVien (3 rows)
INSERT INTO CaLamViecNhanVien (maCaLamViec, maNhanVien, ngay, tenCaLamViec, heSoLuong, tienCa, maCa, trangThai)
VALUES
('CLV001', 'NV001', '2025-01-10', N'Ca sáng', 1.0, 200000, 'C1', N'active'),
('CLV002', 'NV002', '2025-01-10', N'Ca chiều', 1.2, 240000, 'C2', N'active'),
('CLV003', 'NV003', '2025-01-11', N'Ca đêm', 1.5, 300000, 'C3', N'active');

-- LoaiPhong (3 rows)
INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao)
VALUES
('LP01', N'Phòng Standard', 500000.00, '2024-01-01'),
('LP02', N'Phòng Deluxe', 800000.00, '2024-01-01'),
('LP03', N'Phòng Suite', 1500000.00, '2024-01-01');

-- Phong (3 rows)
INSERT INTO Phong (maPhong, tenPhong, trangThai, maLoaiPhong)
VALUES
('P101', N'P101', N'Available', 'LP01'),
('P102', N'P102', N'Occupied', 'LP02'),
('P201', N'P201', N'Maintenance', 'LP03');

-- LoaiDatPhong (3 rows)
INSERT INTO LoaiDatPhong (maLoaiDatPhong, tenLoaiDatPhong, ngayTao)
VALUES
('LD01', N'Theo giờ', '2024-01-01'),
('LD02', N'Qua đêm', '2024-01-01'),
('LD03', N'Theo tuần', '2024-01-01');

-- DichVu (3 rows)
INSERT INTO DichVu (maDichVu, tenDichVu, gia, moTa, donViTinh)
VALUES
('DV01', N'Dịch vụ Ăn uống', 150000.00, N'Khăn lạnh, nước uống', N'phần'),
('DV02', N'Dịch vụ Giặt ủi', 50000.00, N'Giặt quần áo nhanh', N'kg'),
('DV03', N'Dịch vụ Spa', 300000.00, N'Gói massage 60 phút', N'lượt');

-- KhachHang (3 rows)
INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao)
VALUES
('KH001', '012345678901', N'Phạm Văn X', '0911222333', 'pvx@example.com', '2024-05-01'),
('KH002', '098765432109', N'Ngô Thị Y', '0911333444', 'nty@example.com', '2024-06-10'),
('KH003', '023456789012', N'Hồ Văn Z', '0911444555', 'hvz@example.com', '2024-07-20');

-- PhieuDatPhong (3 rows)
INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang)
VALUES
('PD001', '2025-01-01', 'KH001'),
('PD002', '2025-01-02', 'KH002'),
('PD003', '2025-01-03', 'KH003');

-- ChiTietPhieuDatPhong (3 rows)
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, gioBatDau, gioKetThuc, maDichVu, maLoaiDatPhong)
VALUES
('PD001', 'P101', '2025-01-01 14:00:00', '2025-01-02 12:00:00', 'DV01', 'LD02'),
('PD002', 'P102', '2025-01-02 15:00:00', '2025-01-03 11:00:00', 'DV02', 'LD02'),
('PD003', 'P201', '2025-01-03 16:00:00', '2025-01-04 10:00:00', NULL, 'LD01');

-- KhuyenMai (3 rows)
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa)
VALUES
('KM01', N'Giảm 10%', '2024-12-01', '2025-12-31', N'active', 0.10, 0.00, 1000000.00),
('KM02', N'Giảm cuối tuần 20%', '2025-01-01', '2025-06-30', N'active', 0.20, 500000.00, 2000000.00),
('KM03', N'Black Friday 50%', '2025-11-25', '2025-11-30', N'planned', 0.50, 1000000.00, 5000000.00);

-- HoaDon (3 rows)
INSERT INTO HoaDon (maHoaDon, ngayDat, maKhachHang, maNhanVien, maKhuyenMai, ngayTao, trangThai)
VALUES
('HD001', '2025-01-02 12:30:00', 'KH001', 'NV001', 'KM01', '2025-01-02', N'Paid'),
('HD002', '2025-01-03 13:00:00', 'KH002', 'NV002', NULL, '2025-01-03', N'Pending'),
('HD003', '2025-01-04 14:15:00', 'KH003', 'NV003', 'KM02', '2025-01-04', N'Paid');

-- ChiTietHoaDon (3 rows)
INSERT INTO ChiTietHoaDon (maHoaDon, maPhieuDatPhong, ngayTao)
VALUES
('HD001', 'PD001', '2025-01-02'),
('HD002', 'PD002', '2025-01-03'),
('HD003', 'PD003', '2025-01-04');

-- ChiTietPhieuDatPhong_DichVu (3 rows)
-- Use conditional inserts to avoid duplicates if triggers already populated these rows
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong_DichVu WHERE maPhieuDatPhong='PD001' AND maPhong='P101' AND maDichVu='DV01')
    INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu) VALUES ('PD001', 'P101', 'DV01');
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong_DichVu WHERE maPhieuDatPhong='PD002' AND maPhong='P102' AND maDichVu='DV02')
    INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu) VALUES ('PD002', 'P102', 'DV02');
IF NOT EXISTS (SELECT 1 FROM ChiTietPhieuDatPhong_DichVu WHERE maPhieuDatPhong='PD001' AND maPhong='P101' AND maDichVu='DV03')
    INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu) VALUES ('PD001', 'P101', 'DV03'); -- PD001 also used for another dịch vụ

-- ChiTietHoaDon_DichVu (3 rows)
-- Use conditional inserts to avoid duplicates if triggers already populated these rows
IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon_DichVu WHERE maHoaDon='HD001' AND maPhieuDatPhong='PD001' AND maDichVu='DV01')
    INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maDichVu) VALUES ('HD001', 'PD001', 'DV01');
IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon_DichVu WHERE maHoaDon='HD002' AND maPhieuDatPhong='PD002' AND maDichVu='DV02')
    INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maDichVu) VALUES ('HD002', 'PD002', 'DV02');
IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon_DichVu WHERE maHoaDon='HD003' AND maPhieuDatPhong='PD003' AND maDichVu='DV03')
    INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maDichVu) VALUES ('HD003', 'PD003', 'DV03');

-- HuyPhong (3 rows)
INSERT INTO HuyPhong (maPhieuDatPhong, lyDo, ngayHuy)
VALUES
('PD003', N'Khách huỷ vào phút chót', '2025-01-03 09:00:00'),
('PD002', N'Phòng không đủ điều kiện', '2025-01-02 08:30:00'),
('PD001', N'Khách đổi lịch', '2025-01-01 10:00:00');

-- DanhGia (3 rows)
INSERT INTO DanhGia (maKhachHang, maPhong, noiDung, ngayTao)
VALUES
('KH001', 'P101', N'Phòng sạch, nhân viên phục vụ tốt', '2025-01-05 10:00:00'),
('KH002', 'P102', N'Giá hợp lý nhưng hơi ồn', '2025-01-06 11:00:00'),
('KH003', 'P201', N'Rất hài lòng với dịch vụ spa', '2025-01-07 12:00:00');



select * FROM TaiKhoan

select * from PhieuDatPhong

select * from DanhGia