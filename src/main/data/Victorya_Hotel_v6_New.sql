
-- ===========================
-- VICTORYA HOTEL DATABASE v6
-- Cập nhật theo Model Classes
-- ===========================


-- Tạo database mới
CREATE DATABASE Victorya_Hotel_v7;
GO

USE Victorya_Hotel_v7;
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
    tienMoCa DECIMAL(18,2)DEFAULT 0,
    tienKetCa DECIMAL(18,2) DEFAULT 0,
    tongChi DECIMAL(10, 2) DEFAULT 0 NOT NULL,
    tongThu DECIMAL(10, 2) DEFAULT 0 NOT NULL,
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
    soNguoiLonToiDa INT NOT NULL,
    soTreEmToiDa INT NOT NULL,
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
    [moTa] [nvarchar](500) NULL,
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
    trangThai VARCHAR(50),
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
    maPhong VARCHAR(20),
    ngayTao DATETIME DEFAULT GETDATE(),
    tongTien DECIMAL(18, 2) DEFAULT 0,
    PRIMARY KEY (maHoaDon, maPhieuDatPhong, maPhong),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maPhieuDatPhong, maPhong) REFERENCES ChiTietPhieuDatPhong(maPhieuDatPhong, maPhong)
);

-- 17. ChiTietHoaDon_DichVu (Bảng liên kết)
CREATE TABLE ChiTietHoaDon_DichVu (
    maHoaDon VARCHAR(20),
    maPhieuDatPhong VARCHAR(20),
    maPhong VARCHAR(20),
    maDichVu VARCHAR(20),
    PRIMARY KEY (maHoaDon, maPhieuDatPhong, maPhong, maDichVu),
    FOREIGN KEY (maHoaDon, maPhieuDatPhong, maPhong) REFERENCES ChiTietHoaDon(maHoaDon, maPhieuDatPhong, maPhong),
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
('0365271959', '$2a$10$9eypGo00I/fQB.j2X0ZyB.adLUV9/Kj/Mt8RUziE6UoFwTCYd.Ivi', 'admin'), -- mk: admin
('0365271958', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'); -- mk: hoang@123H

-- 2. NhanVien
INSERT INTO NhanVien (maNhanVien, CCCD, tenNhanVien, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau, tenDangNhap, trangThai, diaChi) VALUES
('NV001', '042204003399', N'Nguyễn Huy Hoàng', 1, '2004-08-27', 'nguyenhuyhoang270804@gmail.com', '0901234567', '2020-01-10', '0365271959', N'Đang làm việc', N'477/42 Nguyễn Văn Công, Gò Vấp'),
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
INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, soNguoiLonToiDa, soTreEmToiDa, ngayTao) VALUES
('LP01', N'Phòng đơn', 60000, 2, 1, '2024-01-01'),
('LP02', N'Phòng đôi', 80000, 4, 2, '2024-01-01'),
('LP03', N'Phòng gia đình', 100000, 6, 2, '2024-01-01');

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
('P-0011', '301', N'Trống', 'LP03', 3, N'Tốt'),
('P-0012', '302', N'Đã đặt', 'LP03', 3, N'Tốt'),
('P-0013', '303', N'Trống', 'LP03', 3, N'Tốt'),
('P-0014', '304', N'Bảo trì', 'LP03', 3, N'Cần sửa chữa'),
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
('LDP01', N'Online', '2024-01-01'),
('LDP02', N'Offline', '2024-01-01');

-- 9. KhachHang (30 khách hàng)
INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) VALUES
('KH001', '001234567890', N'Nguyễn Văn Hùng', '0987654321', 'nvhung@gmail.com', '2025-10-20'),
('KH002', '002345678901', N'Trần Thị Lan', '0976543210', 'ttlan@gmail.com', '2025-10-21'),
('KH003', '003456789012', N'Lê Minh Tuấn', '0965432109', 'lmtuan@gmail.com', '2025-10-21'),
('KH004', '004567890123', N'Phạm Thu Hà', '0954321098', 'ptha@gmail.com', '2025-10-22'),
('KH005', '005678901234', N'Hoàng Đức Anh', '0943210987', 'hdanh@gmail.com', '2025-10-22'),
('KH006', '006789012345', N'Vũ Thị Mai', '0932109876', 'vtmai@gmail.com', '2025-10-20'),
('KH007', '007890123456', N'Đỗ Văn Nam', '0921098765', 'dvnam@gmail.com', '2025-10-21'),
('KH008', '008901234567', N'Bùi Thị Hoa', '0910987654', 'bthoa@gmail.com', '2025-10-23'),
('KH009', '009012345678', N'Trương Văn Phong', '0909876543', 'tvphong@gmail.com', '2025-10-23'),
('KH010', '010123456789', N'Ngô Thị Linh', '0898765432', 'ntlinh@gmail.com', '2025-10-23'),
('KH011', '011234567890', N'Phan Văn Đức', '0887654321', 'pvduc@gmail.com', '2025-10-18'),
('KH012', '012345678901', N'Đinh Thị Ngọc', '0876543210', 'dtngoc@gmail.com', '2025-10-19'),
('KH013', '013456789012', N'Mai Văn Tâm', '0865432109', 'mvtam@gmail.com', '2025-10-19'),
('KH014', '014567890123', N'Lý Thị Thu', '0854321098', 'ltthu@gmail.com', '2025-10-20'),
('KH015', '015678901234', N'Tô Văn Long', '0843210987', 'tvlong@gmail.com', '2025-10-20'),
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

-- 10. KhuyenMai
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa) VALUES
('KM-0001', N'Giảm 10% cho hóa đơn trên 1 triệu', '2025-10-01 00:00:00', '2025-10-31 23:59:59', 'Đang áp dụng', 0.10, 1000000, 200000),
('KM-0002', N'Giảm 15% cho hóa đơn trên 2 triệu', '2025-10-01 00:00:00', '2025-10-31 23:59:59', 'Đang áp dụng', 0.15, 2000000, 400000),
('KM-0003', N'Giảm 20% cho hóa đơn trên 5 triệu', '2025-10-01 00:00:00', '2025-10-31 23:59:59', 'Đang áp dụng', 0.20, 5000000, 1000000),
('KM-0004', N'Khách hàng VIP - Giảm 25%', '2025-10-01 00:00:00', '2025-12-31 23:59:59', 'Đang áp dụng', 0.25, 3000000, 1500000),
('KM-0005', N'Khuyến mãi hè - Giảm 5%', '2025-09-01 00:00:00', '2025-09-30 23:59:59', 'Đang áp dụng', 0.05, 500000, 100000);


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
    INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maPhong, maDichVu)
    SELECT DISTINCT
        i.maHoaDon,
        i.maPhieuDatPhong,
        i.maPhong,
        ctpdp_dv.maDichVu
    FROM inserted i
    INNER JOIN ChiTietPhieuDatPhong_DichVu ctpdp_dv 
        ON ctpdp_dv.maPhieuDatPhong = i.maPhieuDatPhong
        AND ctpdp_dv.maPhong = i.maPhong
    WHERE NOT EXISTS (
        SELECT 1 
        FROM ChiTietHoaDon_DichVu cthd_dv
        WHERE cthd_dv.maHoaDon = i.maHoaDon
        AND cthd_dv.maPhieuDatPhong = i.maPhieuDatPhong
        AND cthd_dv.maPhong = i.maPhong
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
                        AND cthd_dv.maPhieuDatPhong = d.maPhieuDatPhong
                        AND cthd_dv.maPhong = d.maPhong;
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

-- 6. Trigger tự động cập nhật trạng thái phòng khi hủy phòng
CREATE TRIGGER trg_HuyPhong_UpdateRoomStatus
ON HuyPhong
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Cập nhật trạng thái phòng về "Trống" và trạng thái PhieuDatPhong về "Đã hủy"
    UPDATE p
    SET p.trangThai = N'Trống'
    FROM Phong p
    INNER JOIN ChiTietPhieuDatPhong ctpdp ON p.maPhong = ctpdp.maPhong
    INNER JOIN inserted i ON ctpdp.maPhieuDatPhong = i.maPhieuDatPhong;
    
    -- Cập nhật trạng thái PhieuDatPhong
    UPDATE pdp
    SET pdp.trangThai = N'Đã hủy'
    FROM PhieuDatPhong pdp
    INNER JOIN inserted i ON pdp.maPhieuDatPhong = i.maPhieuDatPhong;
    
    -- Cập nhật trạng thái ChiTietPhieuDatPhong
    UPDATE ctpdp
    SET ctpdp.trangThai = N'Đã hủy'
    FROM ChiTietPhieuDatPhong ctpdp
    INNER JOIN inserted i ON ctpdp.maPhieuDatPhong = i.maPhieuDatPhong;
END;
GO
