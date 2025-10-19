USE Victorya_Hotel;
GO

-- 1. TaiKhoan (3 tài khoản)
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro) VALUES
(N'admin', N'$2a$10$8BqAEPpsBMYFPk5n0BvIxOOiMz64RYP3rEEENOG.QeCIQ31MOxjLu', N'admin'),
(N'hoang', N'$2a$10$Geltzmv7X/.biGGgKZdJuObHlfF9PxUQdS3ZEnfngKtBDjTt.UjG2', N'Nhân viên'),
(N'quanly', N'$2a$10$abcdef1234567890abcdef', N'Quản lý');
GO

-- 2. NhanVien (5 nhân viên)
INSERT INTO NhanVien (maNhanVien, tenNhanVien, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau, tenDangNhap) VALUES
('NV001', N'Nguyen Van A', 1, '1995-01-01', 'a@example.com', '0912345678', '2020-01-01', 'admin'),
('NV002', N'Tran Thi B', 0, '1996-02-02', 'b@example.com', '0912345679', '2021-02-01', 'hoang'),
('NV003', N'Le Van C', 1, '1994-03-03', 'c@example.com', '0912345680', '2022-03-01', 'hoang'),
('NV004', N'Pham Thi D', 0, '1993-04-04', 'd@example.com', '0912345681', '2023-04-01', 'hoang'),
('NV005', N'Hoang Van E', 1, '1992-05-05', 'e@example.com', '0912345682', '2024-05-01', 'quanly');
GO


-- 3. Ca (5 ca)
INSERT INTO Ca (maCa, ngayBatDau, ngayKetThuc) VALUES
('CA001','2025-10-01','2025-10-01'),
('CA002','2025-10-02','2025-10-02'),
('CA003','2025-10-03','2025-10-03'),
('CA004','2025-10-04','2025-10-04'),
('CA005','2025-10-05','2025-10-05');
GO

-- 4. CaLamViecNhanVien (3 ca làm việc nhân viên)
INSERT INTO CaLamViecNhanVien (maCaLamViec, maNhanVien, ngay, tenCaLamViec, heSoLuong, tienCa, maCa, trangThai) VALUES
('CLV001','NV001','2025-10-01',N'Ca Sáng',1.0,500000,'CA001',N'Hoạt động'),
('CLV002','NV002','2025-10-02',N'Ca Chiều',1.2,600000,'CA002',N'Hoạt động'),
('CLV003','NV003','2025-10-03',N'Ca Tối',1.5,700000,'CA003',N'Hoạt động');
GO

-- 5. LoaiPhong (2 loại)
INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao) VALUES
('LP01', N'VIP', 2000000, GETDATE()),
('LP02', N'Thường', 1000000, GETDATE());
GO

-- 6. Phong (20 phòng)
INSERT INTO Phong (maPhong, tenPhong, trangThai, maLoaiPhong, tang) VALUES
('P001', N'101', N'Đã đặt', 'LP01', 1),
('P002', N'102', N'Đã đặt', 'LP01', 1),
('P003', N'103', N'Đã đặt', 'LP01', 1),
('P004', N'104', N'Đã đặt', 'LP02', 1),
('P005', N'105', N'Đã đặt', 'LP02', 1),
('P006', N'201', N'Đã đặt', 'LP01', 2),
('P007', N'202', N'Đã đặt', 'LP01', 2),
('P008', N'203', N'Đã đặt', 'LP02', 2),
('P009', N'204', N'Đã đặt', 'LP02', 2),
('P010', N'205', N'Đã đặt', 'LP02', 2),
('P011', N'301', N'Trống', 'LP01', 3),
('P012', N'302', N'Trống', 'LP01', 3),
('P013', N'303', N'Trống', 'LP02', 3),
('P014', N'304', N'Trống', 'LP02', 3),
('P015', N'305', N'Trống', 'LP02', 3),
('P016', N'401', N'Trống', 'LP01', 4),
('P017', N'402', N'Trống', 'LP01', 4),
('P018', N'403', N'Trống', 'LP02', 4),
('P019', N'404', N'Trống', 'LP02', 4),
('P020', N'405', N'Trống', 'LP02', 4);
GO

-- Thêm 10 phòng mới
INSERT INTO Phong (maPhong, tenPhong, trangThai, maLoaiPhong, tang) VALUES
('P021', N'501', N'Trống', 'LP01', 5),
('P022', N'502', N'Trống', 'LP01', 5),
('P023', N'503', N'Trống', 'LP02', 5),
('P024', N'504', N'Trống', 'LP02', 5),
('P025', N'505', N'Trống', 'LP02', 5),
('P026', N'601', N'Trống', 'LP01', 6),
('P027', N'602', N'Trống', 'LP01', 6),
('P028', N'603', N'Trống', 'LP02', 6),
('P029', N'604', N'Trống', 'LP02', 6),
('P030', N'605', N'Trống', 'LP02', 6);
GO

-- Thêm 10 phòng trạng thái Đang ở
INSERT INTO Phong (maPhong, tenPhong, trangThai, maLoaiPhong, tang) VALUES
('P031', N'701', N'Đang ở', 'LP01', 7),
('P032', N'702', N'Đang ở', 'LP01', 7),
('P033', N'703', N'Đang ở', 'LP02', 7),
('P034', N'704', N'Đang ở', 'LP02', 7),
('P035', N'705', N'Đang ở', 'LP02', 7),
('P036', N'801', N'Đang ở', 'LP01', 8),
('P037', N'802', N'Đang ở', 'LP01', 8),
('P038', N'803', N'Đang ở', 'LP02', 8),
('P039', N'804', N'Đang ở', 'LP02', 8),
('P040', N'805', N'Đang ở', 'LP02', 8);
GO

-- 7. LoaiDatPhong (2 loại)
INSERT INTO LoaiDatPhong (maLoaiDatPhong, tenLoaiDatPhong, ngayTao) VALUES
('LDP01', N'Online', GETDATE()),
('LDP02', N'Offline', GETDATE());
GO

-- 8. DichVu (3 dịch vụ)
INSERT INTO DichVu (maDichVu, tenDichVu, gia, moTa, donViTinh) VALUES
('DV01', N'Dọn phòng', 50000, N'Dọn phòng hàng ngày', N'Lần'),
('DV02', N'Giặt ủi', 30000, N'Giặt ủi quần áo', N'Kg'),
('DV03', N'Ăn sáng', 100000, N'Buffet sáng', N'Người');
GO

-- Thêm 10 dịch vụ mới
INSERT INTO DichVu (maDichVu, tenDichVu, gia, moTa, donViTinh) VALUES
('DV04', N'Nước suối', 15000, N'Nước suối đóng chai', N'Chai'),
('DV05', N'Đưa đón sân bay', 200000, N'Dịch vụ đưa đón sân bay', N'Lượt'),
('DV06', N'Spa', 300000, N'Dịch vụ spa thư giãn', N'Lần'),
('DV07', N'Massage', 250000, N'Massage toàn thân', N'Lần'),
('DV08', N'Bể bơi', 50000, N'Sử dụng bể bơi', N'Lượt'),
('DV09', N'Gym', 40000, N'Phòng tập gym', N'Lượt'),
('DV10', N'Karaoke', 120000, N'Phòng karaoke', N'Giờ'),
('DV11', N'Đặt tiệc', 2000000, N'Đặt tiệc theo yêu cầu', N'Lần'),
('DV12', N'Cafe sáng', 40000, N'Cafe sáng tại sảnh', N'Cốc'),
('DV13', N'Bữa tối', 250000, N'Set menu bữa tối', N'Người');
GO

-- 9. KhachHang (20 khách)
INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) VALUES
('KH001','123456789001','Nguyen Van A','0912345001','a1@example.com',GETDATE()),
('KH002','123456789002','Tran Thi B','0912345002','b2@example.com',GETDATE()),
('KH003','123456789003','Le Van C','0912345003','c3@example.com',GETDATE()),
('KH004','123456789004','Pham Thi D','0912345004','d4@example.com',GETDATE()),
('KH005','123456789005','Hoang Van E','0912345005','e5@example.com',GETDATE()),
('KH006','123456789006','Nguyen Van F','0912345006','f6@example.com',GETDATE()),
('KH007','123456789007','Tran Thi G','0912345007','g7@example.com',GETDATE()),
('KH008','123456789008','Le Van H','0912345008','h8@example.com',GETDATE()),
('KH009','123456789009','Pham Thi I','0912345009','i9@example.com',GETDATE()),
('KH010','123456789010','Hoang Van J','0912345010','j10@example.com',GETDATE()),
('KH011','123456789011','Nguyen Van K','0912345011','k11@example.com',GETDATE()),
('KH012','123456789012','Tran Thi L','0912345012','l12@example.com',GETDATE()),
('KH013','123456789013','Le Van M','0912345013','m13@example.com',GETDATE()),
('KH014','123456789014','Pham Thi N','0912345014','n14@example.com',GETDATE()),
('KH015','123456789015','Hoang Van O','0912345015','o15@example.com',GETDATE()),
('KH016','123456789016','Nguyen Van P','0912345016','p16@example.com',GETDATE()),
('KH017','123456789017','Tran Thi Q','0912345017','q17@example.com',GETDATE()),
('KH018','123456789018','Le Van R','0912345018','r18@example.com',GETDATE()),
('KH019','123456789019','Pham Thi S','0912345019','s19@example.com',GETDATE()),
('KH020','123456789020','Hoang Van T','0912345020','t20@example.com',GETDATE());
GO

-- 10. PhieuDatPhong (2 phiếu đặt phòng)
INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang) VALUES
('PD001', GETDATE(), 'KH001'),
('PD002', GETDATE(), 'KH002');
GO

-- 11. ChiTietPhieuDatPhong (20 chi tiết)
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, gioBatDau, gioKetThuc, maDichVu, maLoaiDatPhong, soNguoi) VALUES
('PD001','P001','2025-10-13 14:00:00','2025-10-14 12:00:00','DV01','LDP01',2),
('PD001','P002','2025-10-13 14:00:00','2025-10-14 12:00:00','DV02','LDP02',3),
('PD001','P003','2025-10-13 14:00:00','2025-10-14 12:00:00','DV03','LDP01',1),
('PD001','P004','2025-10-13 14:00:00','2025-10-14 12:00:00','DV01','LDP02',2),
('PD001','P005','2025-10-13 14:00:00','2025-10-14 12:00:00','DV02','LDP01',2),
('PD002','P006','2025-10-13 14:00:00','2025-10-14 12:00:00','DV03','LDP02',3),
('PD002','P007','2025-10-13 14:00:00','2025-10-14 12:00:00','DV01','LDP01',1),
('PD002','P008','2025-10-13 14:00:00','2025-10-14 12:00:00','DV02','LDP02',2),
('PD002','P009','2025-10-13 14:00:00','2025-10-14 12:00:00','DV03','LDP01',2),
('PD002','P010','2025-10-13 14:00:00','2025-10-14 12:00:00','DV01','LDP02',1),
('PD001','P011','2025-10-13 14:00:00','2025-10-14 12:00:00','DV02','LDP01',2),
('PD001','P012','2025-10-13 14:00:00','2025-10-14 12:00:00','DV03','LDP02',2),
('PD001','P013','2025-10-13 14:00:00','2025-10-14 12:00:00','DV01','LDP01',1),
('PD001','P014','2025-10-13 14:00:00','2025-10-14 12:00:00','DV02','LDP02',2),
('PD001','P015','2025-10-13 14:00:00','2025-10-14 12:00:00','DV03','LDP01',2),
('PD002','P016','2025-10-13 14:00:00','2025-10-14 12:00:00','DV01','LDP02',1),
('PD002','P017','2025-10-13 14:00:00','2025-10-14 12:00:00','DV02','LDP01',2),
('PD002','P018','2025-10-13 14:00:00','2025-10-14 12:00:00','DV03','LDP02',3),
('PD002','P019','2025-10-13 14:00:00','2025-10-14 12:00:00','DV01','LDP01',1),
('PD002','P020','2025-10-13 14:00:00','2025-10-14 12:00:00','DV02','LDP02',2);
GO

-- Thêm 10 chi tiết đặt phòng cho 10 phòng mới và 10 dịch vụ mới
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, gioBatDau, gioKetThuc, maDichVu, maLoaiDatPhong, soNguoi) VALUES
('PD001','P021','2025-10-15 14:00:00','2025-10-16 12:00:00','DV04','LDP01',2),
('PD001','P022','2025-10-15 14:00:00','2025-10-16 12:00:00','DV05','LDP02',3),
('PD001','P023','2025-10-15 14:00:00','2025-10-16 12:00:00','DV06','LDP01',1),
('PD001','P024','2025-10-15 14:00:00','2025-10-16 12:00:00','DV07','LDP02',2),
('PD001','P025','2025-10-15 14:00:00','2025-10-16 12:00:00','DV08','LDP01',2),
('PD002','P026','2025-10-15 14:00:00','2025-10-16 12:00:00','DV09','LDP02',3),
('PD002','P027','2025-10-15 14:00:00','2025-10-16 12:00:00','DV10','LDP01',1),
('PD002','P028','2025-10-15 14:00:00','2025-10-16 12:00:00','DV11','LDP02',2),
('PD002','P029','2025-10-15 14:00:00','2025-10-16 12:00:00','DV12','LDP01',2),
('PD002','P030','2025-10-15 14:00:00','2025-10-16 12:00:00','DV13','LDP02',1);
GO

-- 12. ChiTietPhieuDatPhong_DichVu (10 bản, lấy 10 dịch vụ từ chi tiết trên)
INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu) VALUES
('PD001','P001','DV01'),
('PD001','P002','DV02'),
('PD001','P003','DV03'),
('PD001','P004','DV01'),
('PD001','P005','DV02'),
('PD002','P006','DV03'),
('PD002','P007','DV01'),
('PD002','P008','DV02'),
('PD002','P009','DV03'),
('PD002','P010','DV01');
GO

-- Thêm 10 bản ghi dịch vụ cho 10 phòng mới
INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu) VALUES
('PD001','P021','DV04'),
('PD001','P022','DV05'),
('PD001','P023','DV06'),
('PD001','P024','DV07'),
('PD001','P025','DV08'),
('PD002','P026','DV09'),
('PD002','P027','DV10'),
('PD002','P028','DV11'),
('PD002','P029','DV12'),
('PD002','P030','DV13');
GO

-- 13. HoaDon (20 hóa đơn)
INSERT INTO HoaDon (maHoaDon, ngayDat, maKhachHang, maNhanVien, maKhuyenMai, ngayTao, trangThai, tongTien) VALUES
('HD001',GETDATE(),'KH001','NV001',NULL,GETDATE(),N'Đã thanh toán',1000000),
('HD002',GETDATE(),'KH002','NV002',NULL,GETDATE(),N'Đã thanh toán',2000000),
('HD003',GETDATE(),'KH003','NV003',NULL,GETDATE(),N'Đã thanh toán',1500000),
('HD004',GETDATE(),'KH004','NV004',NULL,GETDATE(),N'Đã thanh toán',1200000),
('HD005',GETDATE(),'KH005','NV005',NULL,GETDATE(),N'Đã thanh toán',1800000),
('HD006',GETDATE(),'KH006','NV001',NULL,GETDATE(),N'Đã thanh toán',1100000),
('HD007',GETDATE(),'KH007','NV002',NULL,GETDATE(),N'Đã thanh toán',1300000),
('HD008',GETDATE(),'KH008','NV003',NULL,GETDATE(),N'Đã thanh toán',1400000),
('HD009',GETDATE(),'KH009','NV004',NULL,GETDATE(),N'Đã thanh toán',1250000),
('HD010',GETDATE(),'KH010','NV005',NULL,GETDATE(),N'Đã thanh toán',1350000),
('HD011',GETDATE(),'KH011','NV001',NULL,GETDATE(),N'Đã thanh toán',1150000),
('HD012',GETDATE(),'KH012','NV002',NULL,GETDATE(),N'Đã thanh toán',1450000),
('HD013',GETDATE(),'KH013','NV003',NULL,GETDATE(),N'Đã thanh toán',1550000),
('HD014',GETDATE(),'KH014','NV004',NULL,GETDATE(),N'Đã thanh toán',1650000),
('HD015',GETDATE(),'KH015','NV005',NULL,GETDATE(),N'Đã thanh toán',1750000),
('HD016',GETDATE(),'KH016','NV001',NULL,GETDATE(),N'Đã thanh toán',1850000),
('HD017',GETDATE(),'KH017','NV002',NULL,GETDATE(),N'Đã thanh toán',1950000),
('HD018',GETDATE(),'KH018','NV003',NULL,GETDATE(),N'Đã thanh toán',2050000),
('HD019',GETDATE(),'KH019','NV004',NULL,GETDATE(),N'Đã thanh toán',2150000),
('HD020',GETDATE(),'KH020','NV005',NULL,GETDATE(),N'Đã thanh toán',2250000);
GO

-- 14. ChiTietHoaDon (10 chi tiết)
INSERT INTO ChiTietHoaDon (maHoaDon, maPhieuDatPhong, ngayTao, tongTien) VALUES
('HD001','PD001',GETDATE(),500000),
('HD002','PD002',GETDATE(),600000),
('HD003','PD001',GETDATE(),550000),
('HD004','PD002',GETDATE(),650000),
('HD005','PD001',GETDATE(),700000),
('HD006','PD002',GETDATE(),720000),
('HD007','PD001',GETDATE(),500000),
('HD008','PD002',GETDATE(),600000),
('HD009','PD001',GETDATE(),550000),
('HD010','PD002',GETDATE(),650000);
GO

-- 15. ChiTietHoaDon_DichVu (10 bản)
INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maDichVu) VALUES
('HD001','PD001','DV01'),
('HD002','PD002','DV02'),
('HD003','PD001','DV03'),
('HD004','PD002','DV01'),
('HD005','PD001','DV02'),
('HD006','PD002','DV03'),
('HD007','PD001','DV01'),
('HD008','PD002','DV02'),
('HD009','PD001','DV03'),
('HD010','PD002','DV01');
GO

-- 16. KhuyenMai (10 khuyến mãi)
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa) VALUES
('KM001',N'KM Tet',GETDATE(),DATEADD(DAY,10,GETDATE()),N'Hoạt động',0.1,1000000,500000),
('KM002',N'KM Mua he',GETDATE(),DATEADD(DAY,15,GETDATE()),N'Hoạt động',0.2,2000000,800000),
('KM003',N'KM Cuoi nam',GETDATE(),DATEADD(DAY,20,GETDATE()),N'Hoạt động',0.15,1500000,600000),
('KM004',N'KM Khach hang than thiet',GETDATE(),DATEADD(DAY,30,GETDATE()),N'Hoạt động',0.1,1200000,500000),
('KM005',N'KM Online',GETDATE(),DATEADD(DAY,10,GETDATE()),N'Hoạt động',0.05,500000,250000),
('KM006',N'KM Offline',GETDATE(),DATEADD(DAY,15,GETDATE()),N'Hoạt động',0.1,1000000,500000),
('KM007',N'KM Dich vu',GETDATE(),DATEADD(DAY,20,GETDATE()),N'Hoạt động',0.2,2000000,1000000),
('KM008',N'KM Ngay Le',GETDATE(),DATEADD(DAY,30,GETDATE()),N'Hoạt động',0.25,3000000,1500000),
('KM009',N'KM Sinh nhat',GETDATE(),DATEADD(DAY,15,GETDATE()),N'Hoạt động',0.15,1500000,700000),
('KM010',N'KM Cuoi thang',GETDATE(),DATEADD(DAY,10,GETDATE()),N'Hoạt động',0.1,1000000,500000);
GO

-- 17. HuyPhong (5 bản)
INSERT INTO HuyPhong (maPhieuDatPhong, lyDo) VALUES
('PD001',N'Khách hủy trước'),
('PD002',N'Khách hủy sau'),
('PD001',N'Khách hủy đột xuất'),
('PD002',N'Khách không đến'),
('PD001',N'Lỗi hệ thống');
GO

-- 18. DanhGia (5 bản)
INSERT INTO DanhGia (maKhachHang, maPhong, noiDung) VALUES
('KH001','P001',N'Dịch vụ tốt'),
('KH002','P002',N'Phòng sạch sẽ'),
('KH003','P003',N'Nhân viên thân thiện'),
('KH004','P004',N'Ăn sáng ngon'),
('KH005','P005',N'Rất hài lòng');
GO

INSERT INTO DichVu_LoaiPhong(maDichVu, maLoaiPhong) VALUES 
('DV01', 'LP01'),
('DV02', 'LP01'),
('DV03', 'LP01'),
('DV03', 'LP02');

-- Gán 10 dịch vụ mới cho 2 loại phòng
INSERT INTO DichVu_LoaiPhong(maDichVu, maLoaiPhong) VALUES
('DV04', 'LP01'),
('DV05', 'LP01'),
('DV06', 'LP01'),
('DV07', 'LP01'),
('DV08', 'LP02'),
('DV09', 'LP02'),
('DV10', 'LP02'),
('DV11', 'LP02'),
('DV12', 'LP01'),
('DV13', 'LP02');


select COUNT(*) from Phong
select * from LoaiPhong
select * from LoaiDatPhong
select * from ChiTietPhieuDatPhong_DichVu
select * from ChiTietHoaDon_DichVu
select * from ChiTietHoaDon
select * from ChiTietPhieuDatPhong
select * from DichVu_LoaiPhong

SELECT DISTINCT
    p.*,
    lp.*,
    dv.*,
    CASE
        WHEN EXISTS (
            SELECT 1
            FROM ChiTietPhieuDatPhong ct
            WHERE ct.maPhong = p.maPhong
              AND ct.gioBatDau < '2025-10-13 14:00:00.000' -- thời gian kết thúc tìm
              AND ct.gioKetThuc > '2025-10-14 12:00:00.000' -- thời gian bắt đầu tìm
        )
        THEN N'Đang bận'
        ELSE N'Trống'
    END AS tinhTrangThoiGian
FROM Phong p
JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
LEFT JOIN DichVu_LoaiPhong dvp ON lp.maLoaiPhong = dvp.maLoaiPhong
LEFT JOIN DichVu dv ON dvp.maDichVu = dv.maDichVu
WHERE lp.tenLoaiPhong = null
ORDER BY p.tang, p.maPhong;

SELECT DISTINCT 
    p.*, 
    lp.*, 
    dv.*
FROM Phong p
JOIN LoaiPhong lp 
    ON p.maLoaiPhong = lp.maLoaiPhong
LEFT JOIN DichVu_LoaiPhong dvp 
    ON lp.maLoaiPhong = dvp.maLoaiPhong
LEFT JOIN DichVu dv 
    ON dvp.maDichVu = dv.maDichVu
JOIN ChiTietPhieuDatPhong ct 
    ON ct.maPhong = p.maPhong
    AND (
        (ct.gioBatDau BETWEEN '2025-10-13 12:00:00' AND '2025-14-10 12:00:00')
        OR (ct.gioKetThuc BETWEEN '2025-10-30 12:00:00' AND '2025-11-02 12:00:00')
        OR ('2025-10-30 12:00:00' BETWEEN ct.gioBatDau AND ct.gioKetThuc)
        OR ('2025-11-02 12:00:00' BETWEEN ct.gioBatDau AND ct.gioKetThuc)
    )
WHERE 
    (N'VIP' IS NULL OR lp.tenLoaiPhong = N'VIP')
ORDER BY p.tang, p.maPhong;