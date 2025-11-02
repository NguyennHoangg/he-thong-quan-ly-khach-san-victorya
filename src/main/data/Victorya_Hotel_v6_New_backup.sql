-- ===========================
-- VICTORYA HOTEL DATABASE v6
-- Cập nhật theo Model Classes
-- ===========================

-- Xóa database cũ nếu tồn tại
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'Victorya_Hotel_v6')
BEGIN
    ALTER DATABASE Victorya_Hotel_v6 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE Victorya_Hotel_v6;
END
GO

-- Tạo database mới
CREATE DATABASE Victorya_Hotel_v6;
GO

USE Victorya_Hotel_v6;
GO

-- ===========================
-- TẠO CÁC BẢNG
-- ===========================

-- 1. TaiKhoan
CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(100) NOT NULL,
    vaiTro VARCHAR(50) NOT NULL DEFAULT 'employee'
);

-- 2. NhanVien
CREATE TABLE NhanVien (
    maNhanVien VARCHAR(20) PRIMARY KEY,
    CCCD VARCHAR(12),
    tenNhanVien NVARCHAR(100) NOT NULL,
    gioiTinh BIT DEFAULT 1,
    ngaySinh DATE,
    email VARCHAR(100),
    soDienThoai VARCHAR(20),
    ngayBatDau DATE DEFAULT GETDATE(),
    tenDangNhap VARCHAR(50),
    trangThai NVARCHAR(50) DEFAULT N'Đang làm việc',
    diaChi NVARCHAR(255),
    FOREIGN KEY (tenDangNhap) REFERENCES TaiKhoan(tenDangNhap)
);

-- 3. Ca
CREATE TABLE Ca (
    maCa VARCHAR(20) PRIMARY KEY,
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL
);

-- 4. CaLamViecNhanVien
CREATE TABLE CaLamViecNhanVien (
    maCaLamViec VARCHAR(20) PRIMARY KEY,
    maNhanVien VARCHAR(20) NOT NULL,
    ngay DATE NOT NULL,
    tienMoCa FLOAT DEFAULT 0,
    tienKetCa FLOAT DEFAULT 0,
    maCa VARCHAR(20) NOT NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang mở',
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
    FOREIGN KEY (maCa) REFERENCES Ca(maCa)
);

-- 5. LoaiPhong
CREATE TABLE LoaiPhong (
    maLoaiPhong VARCHAR(20) PRIMARY KEY,
    tenLoaiPhong NVARCHAR(100) NOT NULL,
    gia DECIMAL(18, 2) NOT NULL,
    ngayTao DATE DEFAULT GETDATE()
);

-- 6. Phong
CREATE TABLE Phong (
    maPhong VARCHAR(20) PRIMARY KEY,
    soPhong NVARCHAR(100) NOT NULL,
    trangThai NVARCHAR(50) DEFAULT N'Trống',
    maLoaiPhong VARCHAR(20) NOT NULL,
    tang INT NOT NULL,
    tinhTrang NVARCHAR(55) DEFAULT N'Tốt',
    FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong(maLoaiPhong)
);

-- 7. LoaiDatPhong
CREATE TABLE LoaiDatPhong (
    maLoaiDatPhong VARCHAR(20) PRIMARY KEY,
    tenLoaiDatPhong NVARCHAR(100) NOT NULL,
    ngayTao DATE DEFAULT GETDATE()
);

-- 8. DichVu
CREATE TABLE DichVu (
    maDichVu VARCHAR(20) PRIMARY KEY,
    tenDichVu NVARCHAR(100) NOT NULL,
    gia DECIMAL(18, 2) NOT NULL,
    moTa NVARCHAR(255),
    donViTinh NVARCHAR(50)
);

-- 9. DichVu_LoaiPhong (Bảng liên kết)
CREATE TABLE DichVu_LoaiPhong (
    maDichVu VARCHAR(20),
    maLoaiPhong VARCHAR(20),
    PRIMARY KEY (maLoaiPhong, maDichVu),
    FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong(maLoaiPhong),
    FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);

-- 10. KhachHang
CREATE TABLE KhachHang (
    maKhachHang VARCHAR(20) PRIMARY KEY,
    CCCD VARCHAR(20) UNIQUE NOT NULL,
    hoTen NVARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    ngayTao DATE DEFAULT GETDATE()
);

-- 11. PhieuDatPhong
CREATE TABLE PhieuDatPhong (
    maPhieuDatPhong VARCHAR(20) PRIMARY KEY,
    ngayTao DATE DEFAULT GETDATE(),
    maKhachHang VARCHAR(20) NOT NULL,
    trangThai NVARCHAR(20) DEFAULT N'Đã đặt',
    tienDatCoc DECIMAL(18, 2) DEFAULT 0,
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang)
);

-- 12. ChiTietPhieuDatPhong
CREATE TABLE ChiTietPhieuDatPhong (
    maPhieuDatPhong VARCHAR(20),
    maPhong VARCHAR(20),
    thoiGianNhanPhong DATETIME NOT NULL,
    thoiGianTraPhong DATETIME NOT NULL,
    maLoaiDatPhong VARCHAR(20) NOT NULL,
    soNguoi INT DEFAULT 1,
    trangThai NVARCHAR(50) DEFAULT N'Đã đặt',
    PRIMARY KEY (maPhieuDatPhong, maPhong),
    FOREIGN KEY (maPhieuDatPhong) REFERENCES PhieuDatPhong(maPhieuDatPhong),
    FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    FOREIGN KEY (maLoaiDatPhong) REFERENCES LoaiDatPhong(maLoaiDatPhong),
    CONSTRAINT CK_ChiTietPhieuDatPhong_Time CHECK (thoiGianTraPhong > thoiGianNhanPhong)
);

-- 13. ChiTietPhieuDatPhong_DichVu (Bảng liên kết)
CREATE TABLE ChiTietPhieuDatPhong_DichVu (
    maPhieuDatPhong VARCHAR(20),
    maPhong VARCHAR(20),
    maDichVu VARCHAR(20),
    soLuong INT DEFAULT 1,
    PRIMARY KEY (maPhieuDatPhong, maPhong, maDichVu),
    FOREIGN KEY (maPhieuDatPhong, maPhong) REFERENCES ChiTietPhieuDatPhong(maPhieuDatPhong, maPhong),
    FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);

-- 14. KhuyenMai
CREATE TABLE KhuyenMai (
    maKhuyenMai VARCHAR(20) PRIMARY KEY,
    tenKhuyenMai NVARCHAR(100) NOT NULL,
    ngayBatDau DATETIME NOT NULL,
    ngayKetThuc DATETIME NOT NULL,
    trangThai BIT DEFAULT 1,
    heSo FLOAT NOT NULL,
    tongTienToiThieu DECIMAL(18, 2) DEFAULT 0,
    tongKhuyenMaiToiDa DECIMAL(18, 2) DEFAULT 0,
    CONSTRAINT CK_KhuyenMai_NgayApDung CHECK (ngayKetThuc >= ngayBatDau),
    CONSTRAINT CK_KhuyenMai_HeSo CHECK (heSo >= 0 AND heSo <= 1)
);

-- 15. HoaDon
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(20) PRIMARY KEY,
    ngayDat DATETIME DEFAULT GETDATE(),
    maKhachHang VARCHAR(20) NOT NULL,
    maNhanVien VARCHAR(20) NOT NULL,
    maKhuyenMai VARCHAR(20),
    ngayTao DATETIME DEFAULT GETDATE(),
    trangThai NVARCHAR(50) DEFAULT N'Chưa thanh toán',
    tongTien DECIMAL(18, 2) DEFAULT 0,
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
    FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai)
);

-- 16. ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    maHoaDon VARCHAR(20),
    maPhieuDatPhong VARCHAR(20),
    ngayTao DATETIME DEFAULT GETDATE(),
    tongTien DECIMAL(18, 2) DEFAULT 0,
    PRIMARY KEY (maHoaDon, maPhieuDatPhong),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maPhieuDatPhong) REFERENCES PhieuDatPhong(maPhieuDatPhong)
);

-- 17. ChiTietHoaDon_DichVu (Bảng liên kết)
CREATE TABLE ChiTietHoaDon_DichVu (
    maHoaDon VARCHAR(20),
    maPhieuDatPhong VARCHAR(20),
    maDichVu VARCHAR(20),
    PRIMARY KEY (maHoaDon, maPhieuDatPhong, maDichVu),
    FOREIGN KEY (maHoaDon, maPhieuDatPhong) REFERENCES ChiTietHoaDon(maHoaDon, maPhieuDatPhong),
    FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);

-- 18. HuyPhong
CREATE TABLE HuyPhong (
    maHuyPhong INT IDENTITY PRIMARY KEY,
    maPhieuDatPhong VARCHAR(20) NOT NULL,
    lyDo NVARCHAR(255),
    ngayHuy DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (maPhieuDatPhong) REFERENCES PhieuDatPhong(maPhieuDatPhong)
);

-- 19. DanhGia
CREATE TABLE DanhGia (
    maDanhGia INT IDENTITY PRIMARY KEY,
    maKhachHang VARCHAR(20) NOT NULL,
    maPhong VARCHAR(20),
    noiDung NVARCHAR(1000),
    ngayTao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maPhong) REFERENCES Phong(maPhong)
);

-- ===========================
-- TẠO INDEX
-- ===========================

CREATE INDEX IX_NhanVien_TenDangNhap ON NhanVien(tenDangNhap);
CREATE INDEX IX_NhanVien_TrangThai ON NhanVien(trangThai);
CREATE INDEX IX_CaLamViecNhanVien_MaNhanVien ON CaLamViecNhanVien(maNhanVien);
CREATE INDEX IX_CaLamViecNhanVien_MaCa ON CaLamViecNhanVien(maCa);
CREATE INDEX IX_CaLamViecNhanVien_Ngay ON CaLamViecNhanVien(ngay);
CREATE INDEX IX_Phong_MaLoaiPhong ON Phong(maLoaiPhong);
CREATE INDEX IX_Phong_TrangThai ON Phong(trangThai);
CREATE INDEX IX_PhieuDatPhong_MaKhachHang ON PhieuDatPhong(maKhachHang);
CREATE INDEX IX_PhieuDatPhong_NgayTao ON PhieuDatPhong(ngayTao);
CREATE INDEX IX_PhieuDatPhong_TrangThai ON PhieuDatPhong(trangThai);
CREATE INDEX IX_KhachHang_CCCD ON KhachHang(CCCD);
CREATE INDEX IX_KhachHang_SoDienThoai ON KhachHang(soDienThoai);
CREATE INDEX IX_ChiTietPhieuDatPhong_MaPhieuDatPhong ON ChiTietPhieuDatPhong(maPhieuDatPhong);
CREATE INDEX IX_ChiTietPhieuDatPhong_MaPhong ON ChiTietPhieuDatPhong(maPhong);
CREATE INDEX IX_ChiTietPhieuDatPhong_ThoiGianNhanPhong ON ChiTietPhieuDatPhong(thoiGianNhanPhong);
CREATE INDEX IX_ChiTietPhieuDatPhong_ThoiGianTraPhong ON ChiTietPhieuDatPhong(thoiGianTraPhong);
CREATE INDEX IX_KhuyenMai_NgayBatDau ON KhuyenMai(ngayBatDau);
CREATE INDEX IX_KhuyenMai_NgayKetThuc ON KhuyenMai(ngayKetThuc);
CREATE INDEX IX_KhuyenMai_TrangThai ON KhuyenMai(trangThai);
CREATE INDEX IX_HoaDon_MaKhachHang ON HoaDon(maKhachHang);
CREATE INDEX IX_HoaDon_MaNhanVien ON HoaDon(maNhanVien);
CREATE INDEX IX_HoaDon_NgayDat ON HoaDon(ngayDat);
CREATE INDEX IX_HoaDon_NgayTao ON HoaDon(ngayTao);
CREATE INDEX IX_HoaDon_TrangThai ON HoaDon(trangThai);

-- ===========================
-- THÊM DỮ LIỆU MẪU
-- ===========================

-- 1. TaiKhoan
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro) VALUES
('admin', '$2a$10$9eypGo00I/fQB.j2X0ZyB.adLUV9/Kj/Mt8RUziE6UoFwTCYd.Ivi', 'admin'), -- mk: admin
('0365271958', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'); -- mk: hoang@123H

-- 2. NhanVien
INSERT INTO NhanVien (maNhanVien, CCCD, tenNhanVien, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau, tenDangNhap, trangThai, diaChi) VALUES
('NV001', '042204003399', N'Nguyễn Huy Hoàng', 1, '2004-08-27', 'nguyenhuyhoang270804@gmail.com', '0901234567', '2020-01-10', 'admin', N'Đang làm việc', N'477/42 Nguyễn Văn Công, Gò Vấp'),
('NV002', '012345678910', N'Trần Thị Bình', 0, '1992-08-20', 'ttb@victorya.com', '0912345678', '2021-03-15', '0365271958', N'Đang làm việc', N'477/42 Nguyễn Văn Công, Gò Vấp');

-- 3. Ca làm việc
INSERT INTO Ca (maCa, ngayBatDau, ngayKetThuc) VALUES
('CA-20251020-1', '2025-10-20', '2025-10-20'),
('CA-20251020-2', '2025-10-20', '2025-10-20'),
('CA-20251020-3', '2025-10-20', '2025-10-20'),
('CA-20251021-1', '2025-10-21', '2025-10-21'),
('CA-20251021-2', '2025-10-21', '2025-10-21'),
('CA-20251021-3', '2025-10-21', '2025-10-21'),
('CA-20251022-1', '2025-10-22', '2025-10-22'),
('CA-20251022-2', '2025-10-22', '2025-10-22'),
('CA-20251022-3', '2025-10-22', '2025-10-22'),
('CA-20251023-1', '2025-10-23', '2025-10-23'),
('CA-20251023-2', '2025-10-23', '2025-10-23'),
('CA-20251023-3', '2025-10-23', '2025-10-23');

-- 3.1. Ca làm việc nhân viên
INSERT INTO CaLamViecNhanVien (maCaLamViec, maNhanVien, ngay, tienMoCa, tienKetCa, maCa, trangThai) VALUES
('CLV001', 'NV002', '2025-10-20', 500000, 2800000, 'CA-20251020-1', N'Đã hoàn thành'),
('CLV004', 'NV002', '2025-10-21', 500000, 3100000, 'CA-20251021-1', N'Đã hoàn thành'),
('CLV008', 'NV002', '2025-10-22', 2900000, 4700000, 'CA-20251022-2', N'Đã hoàn thành'),
('CLV010', 'NV002', '2025-10-23', 500000, NULL, 'CA-20251023-1', N'Đang mở');

-- 4. LoaiPhong
INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, ngayTao) VALUES
('LP01', N'Phòng Thường', 100000, '2024-01-01'),
('LP02', N'Phòng VIP', 200000, '2024-01-01');

-- 5. DichVu
INSERT INTO DichVu (maDichVu, tenDichVu, gia, moTa, donViTinh) VALUES
('DV-12345', N'Nước suối', 10000, N'Nước suối Lavie 500ml', N'Chai'),
('DV-23456', N'Coca Cola', 15000, N'Nước ngọt Coca Cola 330ml', N'Lon'),
('DV-34567', N'Bia Heineken', 25000, N'Bia Heineken 330ml', N'Lon'),
('DV-45678', N'Snack', 20000, N'Snack khoai tây Poca', N'Gói'),
('DV-56789', N'Trái cây', 50000, N'Đĩa trái cây tươi', N'Đĩa'),
('DV-67890', N'Giặt ủi', 30000, N'Dịch vụ giặt ủi quần áo', N'Kg'),
('DV-78901', N'Đưa đón sân bay', 300000, N'Dịch vụ đưa đón sân bay', N'Lượt'),
('DV-89012', N'Ăn sáng buffet', 100000, N'Buffet sáng đa dạng món', N'Suất'),
('DV-01234', N'Karaoke', 150000, N'Phòng karaoke theo giờ', N'Giờ'),
('DV-90123', N'Dịch vụ đặc biệt', 500000, N'Dịch vụ đặc biệt cho phòng VIP', N'Lượt');

-- 6. DichVu_LoaiPhong (CHỈ PHÒNG VIP CÓ DỊCH VỤ)
INSERT INTO DichVu_LoaiPhong (maDichVu, maLoaiPhong) VALUES
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

-- 7. Phong (40 phòng, 8 tầng - Tầng 1-6: Thường, Tầng 7-8: VIP)
-- TẦNG 1 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0001', '101', N'Đang ở', 'LP01', 1, N'Tốt'),
('P-0002', '102', N'Trống', 'LP01', 1, N'Tốt'),
('P-0003', '103', N'Đang ở', 'LP01', 1, N'Tốt'),
('P-0004', '104', N'Trống', 'LP01', 1, N'Tốt'),
('P-0005', '105', N'Đã đặt', 'LP01', 1, N'Tốt');

-- TẦNG 2 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0006', '201', N'Đang ở', 'LP01', 2, N'Tốt'),
('P-0007', '202', N'Trống', 'LP01', 2, N'Tốt'),
('P-0008', '203', N'Trống', 'LP01', 2, N'Tốt'),
('P-0009', '204', N'Đang ở', 'LP01', 2, N'Tốt'),
('P-0010', '205', N'Trống', 'LP01', 2, N'Tốt');

-- TẦNG 3 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0011', '301', N'Trống', 'LP01', 3, N'Tốt'),
('P-0012', '302', N'Đã đặt', 'LP01', 3, N'Tốt'),
('P-0013', '303', N'Trống', 'LP01', 3, N'Tốt'),
('P-0014', '304', N'Bảo trì', 'LP01', 3, N'Cần sửa chữa'),
('P-0015', '305', N'Trống', 'LP01', 3, N'Tốt');

-- TẦNG 4 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0016', '401', N'Trống', 'LP01', 4, N'Tốt'),
('P-0017', '402', N'Đang ở', 'LP01', 4, N'Tốt'),
('P-0018', '403', N'Trống', 'LP01', 4, N'Tốt'),
('P-0019', '404', N'Trống', 'LP01', 4, N'Tốt'),
('P-0020', '405', N'Trống', 'LP01', 4, N'Tốt');

-- TẦNG 5 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0021', '501', N'Trống', 'LP01', 5, N'Tốt'),
('P-0022', '502', N'Trống', 'LP01', 5, N'Tốt'),
('P-0023', '503', N'Đang ở', 'LP01', 5, N'Tốt'),
('P-0024', '504', N'Trống', 'LP01', 5, N'Tốt'),
('P-0025', '505', N'Trống', 'LP01', 5, N'Tốt');

-- TẦNG 6 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0026', '601', N'Trống', 'LP01', 6, N'Tốt'),
('P-0027', '602', N'Trống', 'LP01', 6, N'Tốt'),
('P-0028', '603', N'Trống', 'LP01', 6, N'Tốt'),
('P-0029', '604', N'Trống', 'LP01', 6, N'Tốt'),
('P-0030', '605', N'Trống', 'LP01', 6, N'Tốt');

-- TẦNG 7 - Phòng VIP
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0031', '701', N'Đang ở', 'LP02', 7, N'Tốt'),
('P-0032', '702', N'Trống', 'LP02', 7, N'Tốt'),
('P-0033', '703', N'Đang ở', 'LP02', 7, N'Tốt'),
('P-0034', '704', N'Trống', 'LP02', 7, N'Tốt'),
('P-0035', '705', N'Đã đặt', 'LP02', 7, N'Tốt');

-- TẦNG 8 - Phòng VIP
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0036', '801', N'Đang ở', 'LP02', 8, N'Tốt'),
('P-0037', '802', N'Trống', 'LP02', 8, N'Tốt'),
('P-0038', '803', N'Đã đặt', 'LP02', 8, N'Tốt'),
('P-0039', '804', N'Trống', 'LP02', 8, N'Tốt'),
('P-0040', '805', N'Trống', 'LP02', 8, N'Tốt');

-- 8. LoaiDatPhong
INSERT INTO LoaiDatPhong (maLoaiDatPhong, tenLoaiDatPhong, ngayTao) VALUES
('LDP001', N'Theo giờ', '2024-01-01'),
('LDP002', N'Qua đêm', '2024-01-01'),
('LDP003', N'Theo ngày', '2024-01-01');

-- 9. KhachHang
INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) VALUES
('KH0001', '079123456789', N'Nguyễn Văn A', '0911111111', 'vana@gmail.com', '2024-01-15'),
('KH0002', '079234567890', N'Trần Thị B', '0922222222', 'thib@gmail.com', '2024-02-20'),
('KH0003', '079345678901', N'Lê Văn C', '0933333333', 'vanc@gmail.com', '2024-03-10'),
('KH0004', '079456789012', N'Phạm Thị D', '0944444444', 'thid@gmail.com', '2024-04-05');

-- 10. KhuyenMai
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa) VALUES
('KM001', N'Giảm giá 10% cho khách hàng mới', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, 0.1, 500000, 200000),
('KM002', N'Giảm giá 15% cho hóa đơn trên 2 triệu', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, 0.15, 2000000, 500000),
('KM003', N'Giảm giá 20% cho khách VIP', '2024-06-01 00:00:00', '2024-12-31 23:59:59', 1, 0.2, 3000000, 1000000);

-- 11. PhieuDatPhong mẫu
INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang, trangThai, tienDatCoc) VALUES
('PDP0211202400001', '2024-11-02', 'KH0001', N'Đã đặt', 180000);

-- 12. ChiTietPhieuDatPhong mẫu
INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi, trangThai) VALUES
('PDP0211202400001', 'P101', '2024-11-02 14:00:00', '2024-11-02 17:00:00', 'LDP001', 2, N'Đã đặt');

-- 13. ChiTietPhieuDatPhong_DichVu mẫu
INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu, soLuong) VALUES
('PDP0211202400001', 'P101', 'DV001', 2),
('PDP0211202400001', 'P101', 'DV002', 1);

-- Cập nhật trạng thái phòng
UPDATE Phong SET trangThai = N'Đã đặt' WHERE maPhong = 'P101';

GO

-- ===========================
-- TẠO TRIGGERS
-- ===========================

-- 1. Trigger tự động xóa dịch vụ khi xóa ChiTietPhieuDatPhong
CREATE TRIGGER trg_ChiTietPhieuDatPhong_AutoDeleteDichVu
ON ChiTietPhieuDatPhong
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Xóa các dịch vụ liên quan khi xóa chi tiết phiếu đặt phòng
    DELETE ctpdp_dv
    FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv
    INNER JOIN deleted d ON ctpdp_dv.maPhieuDatPhong = d.maPhieuDatPhong
                        AND ctpdp_dv.maPhong = d.maPhong;
END;
GO

-- 2. Trigger tự động insert dịch vụ vào ChiTietHoaDon_DichVu khi tạo ChiTietHoaDon
CREATE TRIGGER trg_ChiTietHoaDon_AutoInsertDichVu
ON ChiTietHoaDon
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Insert các dịch vụ từ PhieuDatPhong vào ChiTietHoaDon_DichVu
    INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maDichVu)
    SELECT DISTINCT
        i.maHoaDon,
        i.maPhieuDatPhong,
        ctpdp_dv.maDichVu
    FROM inserted i
    INNER JOIN ChiTietPhieuDatPhong_DichVu ctpdp_dv 
        ON ctpdp_dv.maPhieuDatPhong = i.maPhieuDatPhong
    WHERE NOT EXISTS (
        SELECT 1 
        FROM ChiTietHoaDon_DichVu cthd_dv
        WHERE cthd_dv.maHoaDon = i.maHoaDon
        AND cthd_dv.maPhieuDatPhong = i.maPhieuDatPhong
        AND cthd_dv.maDichVu = ctpdp_dv.maDichVu
    );
END;
GO

-- 3. Trigger tự động xóa dịch vụ khi xóa ChiTietHoaDon
CREATE TRIGGER trg_ChiTietHoaDon_AutoDeleteDichVu
ON ChiTietHoaDon
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Xóa các dịch vụ liên quan khi xóa chi tiết hóa đơn
    DELETE cthd_dv
    FROM ChiTietHoaDon_DichVu cthd_dv
    INNER JOIN deleted d ON cthd_dv.maHoaDon = d.maHoaDon
                        AND cthd_dv.maPhieuDatPhong = d.maPhieuDatPhong;
END;
GO

-- 4. Trigger cập nhật trạng thái phòng khi thêm PhieuDatPhong
CREATE TRIGGER trg_PhieuDatPhong_UpdateRoomStatus
ON ChiTietPhieuDatPhong
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Cập nhật trạng thái phòng thành "Đã đặt"
    UPDATE p
    SET p.trangThai = N'Đã đặt'
    FROM Phong p
    INNER JOIN inserted i ON p.maPhong = i.maPhong;
END;
GO

-- 5. Trigger cập nhật trạng thái phòng khi hủy/xóa đặt phòng
CREATE TRIGGER trg_ChiTietPhieuDatPhong_RestoreRoomStatus
ON ChiTietPhieuDatPhong
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Khôi phục trạng thái phòng thành "Trống" khi xóa đặt phòng
    UPDATE p
    SET p.trangThai = N'Trống'
    FROM Phong p
    INNER JOIN deleted d ON p.maPhong = d.maPhong;
END;
GO

-- ===========================
-- STORED PROCEDURES
-- ===========================

-- Procedure để lấy danh sách phòng trống theo loại và thời gian
CREATE PROCEDURE sp_GetAvailableRooms
    @maLoaiPhong VARCHAR(20) = NULL,
    @thoiGianNhanPhong DATETIME,
    @thoiGianTraPhong DATETIME
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT p.*
    FROM Phong p
    WHERE (@maLoaiPhong IS NULL OR p.maLoaiPhong = @maLoaiPhong)
    AND p.trangThai = N'Trống'
    AND NOT EXISTS (
        SELECT 1
        FROM ChiTietPhieuDatPhong ct
        WHERE ct.maPhong = p.maPhong
        AND ct.trangThai NOT IN (N'Đã hủy', N'Đã thanh toán')
        AND (
            (@thoiGianNhanPhong BETWEEN ct.thoiGianNhanPhong AND ct.thoiGianTraPhong)
            OR (@thoiGianTraPhong BETWEEN ct.thoiGianNhanPhong AND ct.thoiGianTraPhong)
            OR (ct.thoiGianNhanPhong BETWEEN @thoiGianNhanPhong AND @thoiGianTraPhong)
        )
    );
END;
GO

-- Procedure để tính tổng doanh thu theo khoảng thời gian
CREATE PROCEDURE sp_GetRevenueByDateRange
    @tuNgay DATE,
    @denNgay DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        CAST(hd.ngayTao AS DATE) AS Ngay,
        COUNT(DISTINCT hd.maHoaDon) AS SoHoaDon,
        SUM(hd.tongTien) AS TongDoanhThu,
        SUM(CASE WHEN hd.maKhuyenMai IS NOT NULL THEN hd.tongTien * km.heSo ELSE 0 END) AS TongGiamGia
    FROM HoaDon hd
    LEFT JOIN KhuyenMai km ON hd.maKhuyenMai = km.maKhuyenMai
    WHERE CAST(hd.ngayTao AS DATE) BETWEEN @tuNgay AND @denNgay
    AND hd.trangThai = N'Đã thanh toán'
    GROUP BY CAST(hd.ngayTao AS DATE)
    ORDER BY CAST(hd.ngayTao AS DATE);
END;
GO

-- Procedure để lấy thông tin khách hàng thường xuyên
CREATE PROCEDURE sp_GetFrequentCustomers
    @topN INT = 10
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP (@topN)
        kh.maKhachHang,
        kh.hoTen,
        kh.soDienThoai,
        kh.email,
        COUNT(DISTINCT pdp.maPhieuDatPhong) AS SoLanDat,
        SUM(pdp.tienDatCoc) AS TongTienCoc
    FROM KhachHang kh
    INNER JOIN PhieuDatPhong pdp ON kh.maKhachHang = pdp.maKhachHang
    GROUP BY kh.maKhachHang, kh.hoTen, kh.soDienThoai, kh.email
    ORDER BY COUNT(DISTINCT pdp.maPhieuDatPhong) DESC;
END;
GO

PRINT N'✓ Database Victorya_Hotel_v6 đã được tạo thành công!';
PRINT N'✓ Đã thêm dữ liệu mẫu:';
PRINT N'  - 4 tài khoản (1 admin, 3 nhân viên)';
PRINT N'  - 4 nhân viên';
PRINT N'  - 4 loại phòng (Standard, Superior, Deluxe, VIP)';
PRINT N'  - 40 phòng (4 tầng x 10 phòng/tầng)';
PRINT N'  - 9 dịch vụ';
PRINT N'  - 3 loại đặt phòng';
PRINT N'  - 4 khách hàng';
PRINT N'  - 3 khuyến mãi';
PRINT N'  - 1 phiếu đặt phòng mẫu';
PRINT N'✓ Đã tạo 5 triggers tự động';
PRINT N'✓ Đã tạo 3 stored procedures';
